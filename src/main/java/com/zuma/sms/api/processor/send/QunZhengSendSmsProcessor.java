package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.api.QunZhengAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.QunZhengErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:18
 * 群正 短信发送
 */
@Component
@Slf4j
public class QunZhengSendSmsProcessor extends AbstractSendSmsProcessor<QunZhengAPI.Request,QunZhengAPI.Response,QunZhengErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;



	@Override
	protected QunZhengAPI.Request toRequestObject(Channel channel, String phones, String message) {
		return QunZhengAPI.Request.builder()
				.flag("sendsms")
				.loginName(channel.getAKey())
				.password(channel.getBKey())
				.p(phones)
				.c(message)
				.build();
	}



	@Override
	protected UpdateRecordInfo<QunZhengErrorEnum> getUpdateRecordInfo(QunZhengAPI.Response response) {
		return new UpdateRecordInfo<>(response.getId(),response.getCode(),
				QunZhengErrorEnum.class,QunZhengErrorEnum.SUCCESS,response.getId());
	}

	@Override
	protected String send(QunZhengAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.qunzhengSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	QunZhengAPI.Response stringToResponseObject(String result) {
		try {
			//分割，获取[0]code 和[1]流水号
			String[] temp = StringUtils.split(result, ",");
			return QunZhengAPI.Response.builder()
					.code(temp[0])
					.id(temp[1])
					.build();
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}
}
