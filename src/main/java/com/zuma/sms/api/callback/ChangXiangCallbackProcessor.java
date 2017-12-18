package com.zuma.sms.api.callback;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.ChangXiangSendSmsAPI;
import com.zuma.sms.dto.api.ZhangYouAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.ChangXiangErrorEnum;
import com.zuma.sms.enums.error.KuanXinErrorEnum;
import com.zuma.sms.enums.error.ZhangYouErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 畅想 异步回调 处理器
 */
@Component
public class ChangXiangCallbackProcessor extends SendSmsCallbackProcessor<ChangXiangSendSmsAPI.AsyncResponseChild> {


	@Override
	protected String getOtherId(ChangXiangSendSmsAPI.AsyncResponseChild response) {
		return response.getId();
	}

	@Override
	protected ResultDTO<ErrorData> getResultDTO(ChangXiangSendSmsAPI.AsyncResponseChild response,SmsSendRecord record) {
		//如果成功 TODO 具体对象不明
		if(EnumUtil.equals(response.getCode(), ChangXiangErrorEnum.SUCCESS)){
			return ResultDTO.success(new ErrorData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}
		//失败
		//找到失败码对应枚举
		ChangXiangErrorEnum errorEnum = EnumUtil.getByCode(response.getCode(), ChangXiangErrorEnum.class);
		//返回失败信息
		return ResultDTO.error(errorEnum,new ErrorData(record.getPhones(),record.getMessage()));
	}


}
