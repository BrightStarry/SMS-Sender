package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.QunZhengAPI;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.QunZhengErrorEnum;
import com.zuma.sms.util.EnumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:56
 * 群正 异步回调 处理器
 * 该平台返回对象有些特殊,可能一次要处理多个数据,该类一次暂且只处理一条数据,
 * 多条处理就多次调用
 */
@Component
public class QunZhengCallbackProcessor extends SendSmsCallbackProcessor<QunZhengAPI.AsyncResponseChild> {


	@Override
	protected String getOtherId(QunZhengAPI.AsyncResponseChild response) {
		return response.getPno();
	}

	@Override
	protected ResultDTO<ErrorData> getResultDTO(QunZhengAPI.AsyncResponseChild response,SmsSendRecord record) {
		//如果成功
		if(EnumUtil.equals(response.getState(), QunZhengErrorEnum.CALLBACK_SUCCESS)){
			return ResultDTO.success(new ErrorData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_ASYNC.getCode());
		}

		//失败--宽信接口似乎只会将成功返回，此处应该不会执行到
		//找到失败码对应枚举
		QunZhengErrorEnum errorEnum = EnumUtil.getByCode(response.getState(), QunZhengErrorEnum.class);

		//计算出手机号个数
		int length = StringUtils.split(record.getPhones(), ",").length;
		//返回失败信息
		return ResultDTO.error(errorEnum,new ErrorData(length,record.getPhones(),record.getMessage()));
	}


}
