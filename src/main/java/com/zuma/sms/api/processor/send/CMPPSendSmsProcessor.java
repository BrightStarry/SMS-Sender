package com.zuma.sms.api.processor.send;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.api.cmpp.CMPPSubmitAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 13:33
 * 筑望CMPP 短信发送过程
 */
@Component
@Slf4j
public class CMPPSendSmsProcessor extends AbstractSendSmsProcessor<CMPPSubmitAPI.Request,Integer,CMPPSubmitErrorEnum>{


	@Autowired
	private ConfigStore configStore;


	@Override
	protected CMPPSubmitAPI.Request toRequestObject(Channel channel, String phones, String message) {
		try {
			return CMPPSubmitAPI.Request.build(channel, phones, message);
		} catch (Exception e) {
			log.error("[短信发送过程]生成请求对象失败.error:{}",e.getMessage(),e);
			throw new SmsSenderException("生成请求对象失败");
		}
	}

	/**
	 * 重写
	 * @param requestObject
	 * @param record
	 * @param channel
	 * @return
	 */
	@Override
	protected ResultDTO<ErrorData> getResult(CMPPSubmitAPI.Request requestObject, SmsSendRecord record, Channel channel) {
		Integer sequenceId;
		try {
			//发送并返回序列号
			sequenceId = channel.getCmppConnectionManager().sendSms(requestObject);
		} catch (SmsSenderException e){
			log.error("[短信发送过程]发送socket请求失败-自定定义错误.error:{}", e.getMessage());
			throw new SmsSenderException(e.getMessage());
		}catch (Exception e) {
			log.error("[短信发送过程]发送socket请求失败.error:{}", e.getMessage(), e);
			throw new SmsSenderException(ErrorEnum.SOCKET_REQUEST_ERROR);
		}
		//将结果更新到本次发送记录
		record = updateRecord(sequenceId, record);
		//根据响应返回结果
		return buildResult(sequenceId,record);
	}


	@Override
	protected ResultDTO<ErrorData> buildResult(Integer response, SmsSendRecord record) {
		return ResultDTO.success();
	}

	/**
	 * 重写
	 * @param response
	 * @param record
	 * @return
	 */
	@Override
	protected SmsSendRecord updateRecord(Integer response, SmsSendRecord record) {
		//保存流水号并返回
		return smsSendRecordService.save(record.setOtherId(String.valueOf(response)));
	}

	//----后面的方法对CMPP无用


	@Override
	protected UpdateRecordInfo<CMPPSubmitErrorEnum> getUpdateRecordInfo(Integer response) {
		return null;
	}

	@Override
	protected String send(CMPPSubmitAPI.Request requestObject) {
		return null;
	}

	@Override
	Integer stringToResponseObject(String result) {
		return null;
	}


}
