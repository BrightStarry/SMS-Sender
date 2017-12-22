package com.zuma.sms.api.processor.send;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.enums.system.CodeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

	@Autowired
	public void init(SmsSendRecordService smsSendRecordService) {
		AbstractSendSmsProcessor.smsSendRecordService = smsSendRecordService;
	}


	/**
	 * 处理方法,包装下
	 * 给短信平台的发送任务调用
	 */
	public ResultDTO<ErrorData> process(Channel channel, String phones, String message, Long taskId) {
		return process(channel, phones, message, taskId, null);

	}

	/**
	 * 处理方法,包装下
	 * 给其他平台接口调用
	 */
	public ResultDTO<ErrorData> process(Channel channel, String phones, String message, Platform platform) {
		return process(channel, phones, message, null, platform);
	}

	/**
	 * 处理方法,私有
	 *
	 * @param channel  通道
	 * @param phones   手机号s
	 * @param message  消息
	 * @param taskId   发送任务id, 二选一
	 * @param platform 平台   ,二选一
	 * @return 单次发送结果
	 */
	private ResultDTO<ErrorData> process(Channel channel, String phones, String message, Long taskId, Platform platform) {
		try {
			//将参数转为请求对象
			R requestObject = null;
			try {
				requestObject = toRequestObject(channel, phones, message);
			} catch (Exception e) {
				log.error("[短信发送过程]参数转请求对象异常.error:{}", e.getMessage(), e);
				throw new SmsSenderException("参数转请求对象异常");
			}
			//新建发送记录
			SmsSendRecord record = smsSendRecordService.newRecord(platform, taskId, channel, phones, message, CodeUtil.objectToJsonString(requestObject));
			//获取信号量并累加
			channel.getConcurrentManager().increment();
			//发送并返回ResultDTO
			return getResult(requestObject, record, channel);
		} catch (SmsSenderException e) {
			return ResultDTO.error(e.getCode(), e.getMessage(), new ErrorData(phones, message));
		} catch (Exception e) {
			log.error("[短信发送过程]发生未知异常.e:{}", e.getMessage(), e);
			return ResultDTO.error(ErrorEnum.UNKNOWN_ERROR, new ErrorData(phones, message));
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
	protected ResultDTO<ErrorData> getResult(R requestObject, SmsSendRecord record, Channel channel) {
		P response = null;
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
	protected ResultDTO<ErrorData> buildResult(P response, SmsSendRecord record) {
		//成功
		if (EnumUtil.equals(record.getStatus(), SmsSendRecordStatusEnum.SYNC_SUCCESS))
			return ResultDTO.success();
		//失败
		return ResultDTO.error(
				ErrorEnum.OTHER_ERROR.getCode(),
				record.getErrorInfo(),
				new ErrorData(record.getPhones(), record.getMessage()));
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
					if (updateRecordInfo.getMessage() != null)
						record.setErrorInfo(updateRecordInfo.getMessage());
					else
						record.setErrorInfo(errorEnum.getMessage());
				}
				record.setStatus(SmsSendRecordStatusEnum.SYNC_FAILED.getCode());
			}
			//保存并返回
			return smsSendRecordService.save(record);
		} catch (Exception e) {
			log.error("[短信发送过程]响应对象保存到记录失败");
			record.setSyncTime(new Date())
					.setSyncResultBody(CodeUtil.objectToJsonString(response))
					.setErrorInfo("响应对象保存到记录失败");
			return smsSendRecordService.save(record);
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
