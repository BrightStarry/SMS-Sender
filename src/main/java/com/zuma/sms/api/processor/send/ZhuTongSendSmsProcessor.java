package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.api.ZhuTongAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.error.ZhuTongErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import com.zuma.sms.util.HttpClientUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:18
 * 助通 短信发送
 *
 * 如果需要使用变量接口. 直接修改 发送url即可..
 *
 */
@Component
@Slf4j
public class ZhuTongSendSmsProcessor extends AbstractSendSmsProcessor<ZhuTongAPI.Request,ZhuTongAPI.Response,ZhuTongErrorEnum>{

	@Autowired
	private HttpClientUtil httpClientUtil;

	@Autowired
	private ConfigStore configStore;


	@Override
	@SneakyThrows
	protected ZhuTongAPI.Request toRequestObject(Channel channel, String phones, String message) {
		String dateString = DateUtil.dateToString(new Date(), DateUtil.FORMAT_A);
		String tkey = CodeUtil.stringToMd5(CodeUtil.stringToMd5(channel.getBKey()) + dateString);
		return new ZhuTongAPI.Request(channel.getAKey(), dateString, tkey, phones, message);
	}

	@Override
	protected UpdateRecordInfo<ZhuTongErrorEnum> getUpdateRecordInfo(ZhuTongAPI.Response response) {
		return new UpdateRecordInfo<>(response.getMessageId(),response.getCode(),
				ZhuTongErrorEnum.class,ZhuTongErrorEnum.SUCCESS);
	}

	@Override
	protected String send(ZhuTongAPI.Request requestObject) {
		try {
			return httpClientUtil.doPostForString(configStore.zhutongSendSmsUrl, requestObject);
		} catch (Exception e) {
			log.error("[短信发送过程]短信发送http失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.HTTP_ERROR);
		}
	}

	@Override
	ZhuTongAPI.Response stringToResponseObject(String result) {
		try {
			/**
			 * 返回的格式为  <code>,<messageId|异常信息>
			 * 所以只后者为数字时,才为messageId
			 */
			String[] resultParams = StringUtils.split(result, ",");
			return new ZhuTongAPI.Response(resultParams[0],StringUtils.isNumeric(resultParams[1]) ? resultParams[1] : null);
		} catch (Exception e) {
			log.error("[短信发送过程]返回的string转为response对象失败.resultString={},error={}", result, e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.STRING_TO_RESPONSE_ERROR);
		}
	}


}
