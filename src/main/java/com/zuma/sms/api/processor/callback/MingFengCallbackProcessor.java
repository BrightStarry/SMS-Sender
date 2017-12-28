package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.SendData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.MingFengAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.MingFengErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 铭锋 异步回调 处理器
 */
@Component
public class MingFengCallbackProcessor extends SendSmsCallbackProcessor<MingFengAPI.AsyncResponseChild> {


	@Override
	protected String getOtherId(MingFengAPI.AsyncResponseChild response) {
		return response.getTaskid();
	}

	@Override
	protected ResultDTO<SendData> getResultDTO(MingFengAPI.AsyncResponseChild response, SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getStatus(), MingFengErrorEnum.SUCCESS3)
				|| EnumUtil.equals(response.getStatus(), MingFengErrorEnum.SUCCESS2)){
			return ResultDTO.success(new SendData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}
		//失败
		//找到失败码对应枚举
		MingFengErrorEnum errorEnum = EnumUtil.getByCode(response.getErrorcode(), MingFengErrorEnum.class);
		if(errorEnum == null)
			errorEnum = EnumUtil.getByCode(response.getStatus(), MingFengErrorEnum.class);
		//返回失败信息
		return ResultDTO.error(errorEnum,new SendData(record.getPhones(),record.getMessage()));
	}


}
