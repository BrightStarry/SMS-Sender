package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.SendData;
import com.zuma.sms.dto.api.ZhuTongAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.ZhuTongErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.springframework.stereotype.Component;


/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 助通 异步回调 处理器
 */
@Component
public class ZhuTongCallbackProcessor extends SendSmsCallbackProcessor<ZhuTongAPI.AsyncResponse> {


	@Override
	protected String getOtherId(ZhuTongAPI.AsyncResponse response) {
		return response.getMessageId();
	}

	@Override
	protected ResultDTO<SendData> getResultDTO(ZhuTongAPI.AsyncResponse response, SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getCode(), ZhuTongErrorEnum.SUCCESS)){
			return ResultDTO.success(new SendData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}

		//失败
		//找到失败码对应枚举
		ZhuTongErrorEnum errorEnum = EnumUtil.getByCode(response.getCode(), ZhuTongErrorEnum.class);

		//返回失败信息
		return ResultDTO.error(errorEnum,new SendData(getPhoneLen(record.getPhones()),record.getPhones(),record.getMessage()));
	}


}
