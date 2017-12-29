package com.zuma.sms.api.processor.send;

import com.zuma.sms.batch.SmsSendRecordBatchManager;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.SendData;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.enums.system.CodeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 15:13
 * 抽象发送短信处理器
 */
@Slf4j
@Component
public abstract class AbstractSendSmsProcessor<R, P, E extends CodeEnum> implements SendSmsProcessor {

	protected static SmsSendRecordService smsSendRecordService;
	protected static SmsSendRecordBatchManager smsSendRecordBatchManager;

	@Autowired
	public void init(SmsSendRecordService smsSendRecordService,SmsSendRecordBatchManager smsSendRecordBatchManager) {
		AbstractSendSmsProcessor.smsSendRecordService = smsSendRecordService;
		AbstractSendSmsProcessor.smsSendRecordBatchManager = smsSendRecordBatchManager;
	}





	/**
	 * 处理方法,私有
	 *
	 * @param channel  通道
	 * @param record 短信发送记录
	 * @return 单次发送结果
	 */
	public ResultDTO<SendData> process(Channel channel, SmsSendRecord record) {
//		return ResultDTO.success(new SendData().setCount(record.getPhoneCount()));
		try {
			//将参数转为请求对象
			R requestObject;
			try {
				requestObject = toRequestObject(channel, record.getPhones(), record.getMessage());
			} catch (Exception e) {
				log.error("[短信发送过程]参数转请求对象异常.error:{}", e.getMessage(), e);
				throw new SmsSenderException("参数转请求对象异常");
			}
			//set 请求对象json字符
			record.setRequestBody(CodeUtil.objectToJsonString(requestObject));
			//获取信号量并累加
			channel.getConcurrentManager().increment();
			//发送并返回ResultDTO
			return getResult(requestObject, record, channel);

		} catch (SmsSenderException e) {
			record.setSyncTime(new Date()).setStatus(SmsSendRecordStatusEnum.ASYNC_FAILED.getCode())
					.setErrorInfo(e.getMessage());
			smsSendRecordBatchManager.add(record);
			return ResultDTO.error(e.getCode(), e.getMessage(), new SendData(record.getPhoneCount(),record.getPhones(), record.getMessage()));
		} catch (Exception e) {
			log.error("[短信发送过程]发生未知异常.e:{}", e.getMessage(), e);
			record.setSyncTime(new Date()).setStatus(SmsSendRecordStatusEnum.ASYNC_FAILED.getCode())
					.setErrorInfo(e.getMessage());
			smsSendRecordBatchManager.add(record);
			return ResultDTO.error(ErrorEnum.UNKNOWN_ERROR, new SendData(record.getPhoneCount(),record.getPhones(), record.getMessage()));
		}
	}


	/**
	 * 转为请求对象
	 *
	 * @param channel 通道
	 * @param phones  手机号s
	 * @param message 消息
	 */
	protected abstract R toRequestObject(Channel channel, String phones, String message)  throws Exception;

	/**
	 * 发送并返回结果
	 *
	 * @param requestObject
	 * @param record
	 * @return
	 */
	protected ResultDTO<SendData> getResult(R requestObject, SmsSendRecord record, Channel channel) {
		P response;
		//发送并获取结果
		String result = send(requestObject);
		//将string的结果转为响应对象
		response = stringToResponseObject(result);
		//将结果更新到本次发送记录
		record = updateRecord(response, record);
		//根据响应返回结果
		return buildResult(response, record);
	}


	/**
	 * 根据响应,返回结果
	 *
	 * @param response
	 * @return
	 */
	protected ResultDTO<SendData> buildResult(P response, SmsSendRecord record) {
		//成功
		if (EnumUtil.equals(record.getStatus(), SmsSendRecordStatusEnum.SYNC_SUCCESS))
			return ResultDTO.success();
		//失败
		return ResultDTO.error(
				ErrorEnum.OTHER_ERROR.getCode(),
				record.getErrorInfo(),
				new SendData(record.getPhoneCount(),record.getPhones(), record.getMessage()));
	}

	/**
	 * 将同步响应更新到发送记录
	 *
	 * @param response
	 * @param record
	 * @return
	 */
	protected SmsSendRecord updateRecord(P response, SmsSendRecord record) {
		try {
			//获取修改信息
			UpdateRecordInfo<E> updateRecordInfo = getUpdateRecordInfo(response);

			record.setSyncTime(new Date())
					.setSyncResultBody(CodeUtil.objectToJsonString(response))
					.setOtherId(updateRecordInfo.getId());
			//如果成功
			if (EnumUtil.equals(updateRecordInfo.getCode(), updateRecordInfo.getSuccessEnum())) {
				record.setStatus(SmsSendRecordStatusEnum.SYNC_SUCCESS.getCode());
			} else {
				//如果不成功
				//根据异常码获取异常枚举
				E errorEnum = (E) EnumUtil.getByCode(updateRecordInfo.getCode(), updateRecordInfo.getEClass());
				//如果异常枚举找不到,并且返回对象中自身携带了异常信息,就直接用该信息,否则就就是未知异常
				if (errorEnum == null) {
					if (updateRecordInfo.getMessage() != null)
						record.setErrorInfo(updateRecordInfo.getMessage());
					else
						record.setErrorInfo(ErrorEnum.UNKNOWN_ERROR.getMessage());
				} else {
					//如果异常枚举找到了.但是,只要有返回的异常消息,也使用返回的异常消息
					if (StringUtils.isNotBlank(updateRecordInfo.getMessage()))
						record.setErrorInfo(updateRecordInfo.getMessage());
					else
						record.setErrorInfo(errorEnum.getMessage());
				}
				record.setStatus(SmsSendRecordStatusEnum.SYNC_FAILED.getCode());
			}
			//保存并返回 批处理
			smsSendRecordBatchManager.add(record);
			return record;
		} catch (Exception e) {
			log.error("[短信发送过程]响应对象保存到记录失败");
			throw new SmsSenderException("响应对象保存到记录失败");
		}
	}

	protected abstract UpdateRecordInfo<E> getUpdateRecordInfo(P response);

	/**
	 * 发送,并获得响应
	 *
	 * @param requestObject
	 * @return
	 */
	protected abstract String send(R requestObject);

	/**
	 * 将返回的string转为对应response对象
	 *
	 * @param result
	 * @return
	 */
	abstract P stringToResponseObject(String result);
}
