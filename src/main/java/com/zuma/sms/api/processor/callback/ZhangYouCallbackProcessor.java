package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.SendData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.ZhangYouAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.KuanXinErrorEnum;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 掌游 异步回调 处理器
 */
@Component
@Slf4j
public class ZhangYouCallbackProcessor extends SendSmsCallbackProcessor<ZhangYouAPI.AsyncResponse> {


	@Override
	protected String getOtherId(ZhangYouAPI.AsyncResponse response) {
		if (response.getTaskId() == null) {
			log.error("[异步回调处理]返回对象id为空");
			throw new SmsSenderException("返回对象id为空");
		}
		return response.getTaskId();
	}

	@Override
	protected ResultDTO<SendData> getResultDTO(ZhangYouAPI.AsyncResponse response, SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getMsgCode(),KuanXinErrorEnum.CALLBACK_SUCCESS)){
			return ResultDTO.success(new SendData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}

		//失败
		//找到失败码对应枚举
		ZhangYouErrorEnum errorEnum = EnumUtil.getByCode(response.getMsgCode(), ZhangYouErrorEnum.class);

		//计算出手机号个数
		int length = StringUtils.split(record.getPhones(), ",").length;
		//返回失败信息
		return ResultDTO.error(errorEnum,new SendData(length,record.getPhones(),record.getMessage()));
	}


}
