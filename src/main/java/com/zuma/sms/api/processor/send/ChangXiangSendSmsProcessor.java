package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.api.ChangXiangAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
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
 * 畅想 短信发送
 */
@Component
@Slf4j
public class ChangXiangSendSmsProcessor extends AbstractSendSmsProcessor<ChangXiangAPI.Request,ChangXiangAPI.Response,ChangXiangErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;



	@Override
	protected ChangXiangAPI.Request toRequestObject(Channel channel, String phones, String message) {
		//时间戳
		String dateStr = DateUtil.dateToString(new Date(),DateUtil.FORMAT_A);
		//签名
		String sign = CodeUtil.stringToMd5(CodeUtil.stringToMd5(channel.getBKey()) + dateStr);
		return ChangXiangAPI.Request.builder()
				.name(channel.getAKey())
				.seed(dateStr)
				.key(sign)
				.dest(phones)
				.content(message)
				.build();
	}


	//该平台因为返回数据不同,需要处理下
	@Override
	protected UpdateRecordInfo<ChangXiangErrorEnum> getUpdateRecordInfo(ChangXiangAPI.Response response) {
		UpdateRecordInfo<ChangXiangErrorEnum> info = new UpdateRecordInfo<ChangXiangErrorEnum>()
				.setEClass(ChangXiangErrorEnum.class)
				.setSuccessEnum(ChangXiangErrorEnum.SUCCESS)
				.setId(response.getIdOrCode());//该id对于失败的数据无意义

		//如果是成功
		if(EnumUtil.equals(response.getIsSuccessStr(),ChangXiangErrorEnum.SUCCESS)){
			info.setCode(ChangXiangErrorEnum.SUCCESS.getCode());
		}else{
			//如果失败
			info.setCode(response.getIdOrCode());
		}
		return info;
	}

	@Override
	protected String send(ChangXiangAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.changxiangSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	ChangXiangAPI.Response stringToResponseObject(String result) {
		try {
			String[] temp = StringUtils.split(result, ":");
			return ChangXiangAPI.Response.builder()
					.isSuccessStr(temp[0])
					.idOrCode(temp[1])
					.build();
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}
}
