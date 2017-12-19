package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.ChuangLanAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.ChuangLanCallbackErrorEnum;
import com.zuma.sms.enums.error.ChuangLanErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.springframework.stereotype.Component;


/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 创蓝 异步回调 处理器
 */
@Component
public class ChuangLanCallbackProcessor extends SendSmsCallbackProcessor<ChuangLanAPI.AsyncResponse> {


	@Override
	protected String getOtherId(ChuangLanAPI.AsyncResponse response) {
		return response.getMsgId();
	}

	@Override
	protected ResultDTO<ErrorData> getResultDTO(ChuangLanAPI.AsyncResponse response,SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getStatus(), ChuangLanCallbackErrorEnum.SUCCESS)){
			return ResultDTO.success(new ErrorData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}

		//失败
		//找到失败码对应枚举
		ChuangLanCallbackErrorEnum errorEnum = EnumUtil.getByCode(response.getStatus(), ChuangLanCallbackErrorEnum.class);

		//返回失败信息
		return ResultDTO.error(errorEnum,new ErrorData(getPhoneLen(record.getPhones()),record.getPhones(),record.getMessage()));
	}


}
