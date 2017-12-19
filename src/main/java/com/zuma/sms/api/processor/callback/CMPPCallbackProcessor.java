package com.zuma.sms.api.processor.callback;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.cmpp.CMPPSubmitAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 17:20
 * cmpp 同步回调(算是) 短信发送过程
 */
@Component
@Slf4j
public class CMPPCallbackProcessor extends SendSmsCallbackProcessor<CMPPSubmitAPI.Response> {


	/**
	 * 重写通用处理部分
	 */
	@Override
	protected boolean commonProcess(SmsSendRecord record, ResultDTO<ErrorData> resultDTO, Channel channel) {
		//如果成功
		if (ResultDTO.isSuccess(resultDTO))
			return true;
		//失败处理
		//如果是定时任务
		if(record.isTaskRecord())
			taskHandle(resultDTO,record,channel);
		else
			//如果是其他平台,直接通知其已经失败
			sendCallback(resultDTO,record.getPlatformId());
		return true;
	}

	@Override
	protected String getOtherId(CMPPSubmitAPI.Response response) {
		return String.valueOf(response.getSequenceId());
	}

	@Override
	protected ResultDTO<ErrorData> getResultDTO(CMPPSubmitAPI.Response response, SmsSendRecord record) {
		//如果成功
		if(CMPPSubmitErrorEnum.SUCCESS.getCode().equals((int)response.getResult())){
			return ResultDTO.success(new ErrorData()).setType(ResultDTOTypeEnum.SEND_SMS_CALLBACK_SYNC.getCode());
		}
		//失败
		//找到失败码对应枚举
		CMPPSubmitErrorEnum errorEnum = EnumUtil.getByCode((int) response.getResult(), CMPPSubmitErrorEnum.class);
		//返回失败信息
		return ResultDTO.errorOfInteger(errorEnum,new ErrorData(getPhoneLen(record.getPhones()),record.getPhones(),record.getMessage()));
	}
}
