package com.zuma.sms.api.send;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.QunZhengAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.enums.error.QunZhengErrorEnum;
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

import java.io.UnsupportedEncodingException;
import java.util.Date;

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
	protected ResultDTO<ErrorData> buildResult(QunZhengAPI.Response response, SmsSendRecord record) {
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
	protected UpdateRecordInfo<QunZhengErrorEnum> getUpdateRecordInfo(QunZhengAPI.Response response) {
		return new UpdateRecordInfo<>(response.getId(),response.getCode(),
				QunZhengErrorEnum.class,QunZhengErrorEnum.SUCCESS);
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
