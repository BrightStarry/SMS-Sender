package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.dto.api.ZhangYouAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:18
 * 掌游 短信发送
 */
@Component
@Slf4j
public class ZhangYouSendSmsProcessor extends AbstractSendSmsProcessor<ZhangYouAPI.Request,ZhangYouAPI.Response,ZhangYouErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;


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
	protected UpdateRecordInfo<ZhangYouErrorEnum> getUpdateRecordInfo(ZhangYouAPI.Response response) {
		return new UpdateRecordInfo<>(response.getId(),response.getCode(),
				ZhangYouErrorEnum.class,ZhangYouErrorEnum.SUCCESS);
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
