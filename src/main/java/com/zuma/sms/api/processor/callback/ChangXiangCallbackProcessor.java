package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.SendData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.ChangXiangAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 畅想 异步回调 处理器
 */
@Component
public class ChangXiangCallbackProcessor extends SendSmsCallbackProcessor<ChangXiangAPI.AsyncResponseChild> {


	@Override
	protected String getOtherId(ChangXiangAPI.AsyncResponseChild response) {
		return response.getId();
	}

	@Override
	protected ResultDTO<SendData> getResultDTO(ChangXiangAPI.AsyncResponseChild response, SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getCode(), ChangXiangErrorEnum.SUCCESS)){
			return ResultDTO.success(new SendData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}
		//失败
		//找到失败码对应枚举
		ChangXiangErrorEnum errorEnum = EnumUtil.getByCode(response.getCode(), ChangXiangErrorEnum.class);
		//返回失败信息
		return ResultDTO.error(errorEnum,new SendData(record.getPhones(),record.getMessage()));
	}


}
