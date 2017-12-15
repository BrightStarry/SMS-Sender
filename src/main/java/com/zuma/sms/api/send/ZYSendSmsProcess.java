package com.zuma.sms.api.send;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.ZhangYouAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:18
 * 掌游 短信发送
 */
@Component
@Slf4j
public class ZYSendSmsProcess extends AbstractSendSmsProcessor<ZhangYouAPI.Request,ZhangYouAPI.Response>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;

	@Autowired
	@Override
	protected void init(SmsSendRecordService smsSendRecordService) {
		super.init(smsSendRecordService);
	}

	@Override
	protected ZhangYouAPI.Request toRequestObject(Channel channel, String phones, String message) {
		//签名
		String sign = CodeUtil.stringToMd5(channel.getAKey() + channel.getCKey());
		//创建请求对象
		return new ZhangYouAPI.Request()
				.setSid(channel.getAKey())
				.setCpid(channel.getBKey())
				.setSign(sign)
				.setMobi(phones)
				.setMsg(CodeUtil.stringToUrlEncode(CodeUtil.stringToBase64(message)));
	}

	@Override
	protected ResultDTO<ErrorData> buildResult(ZhangYouAPI.Response response, SmsSendRecord record) {
		//成功
		if(EnumUtil.equals(record.getStatus(),SmsSendRecordStatusEnum.SYNC_SUCCESS))
			return ResultDTO.success();
		//失败
		return ResultDTO.error(
				ErrorEnum.OTHER_ERROR.getCode(),
				record.getErrorInfo(),
				new ErrorData(record.getPhones(),record.getMessage()));
	}

	@Override
	protected SmsSendRecord updateRecord(ZhangYouAPI.Response response, SmsSendRecord record) {
		try {
			record.setSyncTime(new Date())
					.setSyncResultBody(CodeUtil.objectToJsonString(response))
					.setOtherId(response.getId() != null ? response.getId() : "");
			//如果成功
			if(EnumUtil.equals(response.getCode(),ZhangYouErrorEnum.SUCCESS)){
				record.setStatus(SmsSendRecordStatusEnum.SYNC_SUCCESS.getCode());
			}else{
				//如果不成功
				//根据掌游异常码获取异常枚举
				ZhangYouErrorEnum errorEnum = EnumUtil.getByCode(response.getCode(), ZhangYouErrorEnum.class);
				record.setStatus(SmsSendRecordStatusEnum.SYNC_FAILED.getCode())
						.setErrorInfo(errorEnum == null ?  "未知异常" : errorEnum.getMessage());
			}
			//保存并返回
			return smsSendRecordService.save(record);
		} catch (Exception e) {
			log.error("[短信发送过程]响应对象保存到记录失败");
			record.setSyncTime(new Date())
					.setSyncResultBody(CodeUtil.objectToJsonString(response))
					.setErrorInfo("响应对象保存到记录失败");
			return smsSendRecordService.save(record);
		}
	}

	@Override
	protected String send(ZhangYouAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.zhangyouSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	ZhangYouAPI.Response stringToResponseObject(String result) {
		try {
			//根据| 分割，获取[0]code 和[1]流水号
			String[] temp = StringUtils.split(result, "|");
			return ZhangYouAPI.Response.builder()
					.code(temp[0])
					.id(temp[1])
					.build();
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}
}
