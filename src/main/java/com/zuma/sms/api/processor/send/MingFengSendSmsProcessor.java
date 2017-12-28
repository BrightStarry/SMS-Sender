package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.api.MingFengAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.MingFengErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:18
 * 铭锋 短信发送
 */
@Component
@Slf4j
public class MingFengSendSmsProcessor extends AbstractSendSmsProcessor<MingFengAPI.Request,MingFengAPI.Response,MingFengErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;



	@Override
	protected MingFengAPI.Request toRequestObject(Channel channel, String phones, String message) {
		return new MingFengAPI.Request(channel.getAKey(), channel.getAKey(), channel.getBKey(), phones, message);
	}


	//该平台因为返回数据不同,需要处理下
	@Override
	protected UpdateRecordInfo<MingFengErrorEnum> getUpdateRecordInfo(MingFengAPI.Response response) {
		return new UpdateRecordInfo<>(response.getTaskID(),response.getReturnStatus(),
				MingFengErrorEnum.class,MingFengErrorEnum.SUCCESS2,response.getMessage());
	}

	@Override
	protected String send(MingFengAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.mingfengSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	MingFengAPI.Response stringToResponseObject(String result) {
		try {
			return CodeUtil.jsonStringToObject(result, MingFengAPI.Response.class);
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}
}
