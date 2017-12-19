package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.CMPPDeliverStatEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 09:18
 * cmpp 发送短信 异步回调 处理
 */
@Component
@Slf4j
public class CMPPAsyncCallbackProcessor extends SendSmsCallbackProcessor<CMPPDeliverAPI.Request> {
	@Override
	protected String getOtherId(CMPPDeliverAPI.Request response) {
		return String.valueOf(response.getMsgContent().getMsgId());
	}

	@Override
	protected ResultDTO<ErrorData> getResultDTO(CMPPDeliverAPI.Request response, SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getMsgContent().getStat(),CMPPDeliverStatEnum.DELIVERED)){
			return ResultDTO.success(new ErrorData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}
		//失败
		//找到失败码对应枚举
		CMPPDeliverStatEnum errorEnum = EnumUtil.getByCode(response.getMsgContent().getStat(), CMPPDeliverStatEnum.class);
		//返回失败信息
		return ResultDTO.error(errorEnum,new ErrorData(record.getPhones(),record.getMessage()));
	}
}
