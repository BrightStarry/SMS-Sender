package com.zuma.sms.api.send;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.KuanXinAPI;
import com.zuma.sms.dto.api.KuanXinAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.enums.error.KuanXinErrorEnum;
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
 * 宽信 短信发送
 */
@Component
@Slf4j
public class KuanXinSendSmsProcessor extends AbstractSendSmsProcessor<KuanXinAPI.Request,KuanXinAPI.Response,KuanXinErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;


	@Override
	protected KuanXinAPI.Request toRequestObject(Channel channel, String phones, String message) {
		//时间戳
		long ts = System.currentTimeMillis();
		//签名
		String sign = CodeUtil.stringToMd5(channel.getAKey() + ts + channel.getBKey());
		return KuanXinAPI.Request.builder()
				.userId(channel.getAKey())
				.mobile(phones)
				.msgcontent(CodeUtil.stringToUrlEncode(message))
				.sign(sign)
				.ts(ts)
				.build();
	}

	@Override
	protected ResultDTO<ErrorData> buildResult(KuanXinAPI.Response response, SmsSendRecord record) {
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
	protected UpdateRecordInfo<KuanXinErrorEnum> getUpdateRecordInfo(KuanXinAPI.Response response) {
		return new UpdateRecordInfo<>(response.getData() == null ? "" : response.getData().getTaskId(),
				response.getCode(),response.getMsg(),
				KuanXinErrorEnum.class,KuanXinErrorEnum.SUCCESS);
	}

	@Override
	protected String send(KuanXinAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.kuanxinSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	KuanXinAPI.Response stringToResponseObject(String result) {
		try {
			return CodeUtil.jsonStringToObject(result, KuanXinAPI.Response.class);
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}
}
