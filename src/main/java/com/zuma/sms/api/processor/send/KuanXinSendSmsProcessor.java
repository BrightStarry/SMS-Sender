package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.api.KuanXinAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.KuanXinErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.HttpClientUtil;
import com.zuma.sms.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		return new KuanXinAPI.Request()
				.setUserid(channel.getAKey())
				.setMobile(phones)
				.setMsgcontent(CodeUtil.stringToUrlEncode(message))
				.setSign(sign)
				.setTs(ts);
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
