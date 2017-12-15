package com.zuma.sms.api.send;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.CodeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 15:13
 * 抽象发送短信处理器
 */
@Slf4j
public abstract class AbstractSendSmsProcessor<R,P> implements SendSmsProcessor{

	protected SmsSendRecordService smsSendRecordService;

	/**
	 * spring容器注入方法,子类需要重写,执行父类方法,并增加Autowired注解
	 */
	protected void init(SmsSendRecordService smsSendRecordService){
		this.smsSendRecordService = smsSendRecordService;
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
	 *  给其他平台接口调用
	 */
	public ResultDTO<ErrorData> process(Channel channel, String phones, String message, Platform platform) {
		return process(channel, phones, message, null, platform);
	}

	/**
	 * 处理方法,私有
	 *
	 * @param channel 通道
	 * @param phones 手机号s
	 * @param message 消息
	 * @param taskId 发送任务id, 二选一
	 * @param platform 平台   ,二选一
	 * @return 单次发送结果
	 */
	private ResultDTO<ErrorData> process(Channel channel, String phones, String message, Long taskId, Platform platform) {
		try {
			//将参数转为请求对象
			R requestObject = toRequestObject(channel,phones,message);
			//新建发送记录
			SmsSendRecord record = smsSendRecordService.newRecord(platform,taskId, channel, phones,message, CodeUtil.objectToJsonString(requestObject));
			//获取信号量
			channel.getConcurrentManager().increment();
			//发送并返回ResultDTO
			return getResult(requestObject,record);
		}catch (SmsSenderException e){
			return ResultDTO.error(e.getCode(), e.getMessage(), new ErrorData(phones, message));
		} catch (Exception e) {
			log.error("[短信发送过程]发生未知异常.e:{}",e.getMessage(),e);
			return ResultDTO.error(ErrorEnum.UNKNOWN_ERROR, new ErrorData(phones, message));
		}
	}



	/**
	 * 转为请求对象
	 * @param channel 通道
	 * @param phones 手机号s
	 * @param message 消息
	 */
	protected abstract R toRequestObject(Channel channel, String phones, String message);

	/**
	 * 发送并返回结果
	 * @param requestObject
	 * @param record
	 * @return
	 */
	protected  ResultDTO<ErrorData> getResult(R requestObject,SmsSendRecord record){
		//发送并获取结果
		String result = send(requestObject);
		//将string的结果转为响应对象
		P response = stringToResponseObject(result);
		//将结果更新到本次发送记录
		record = updateRecord(response, record);
		//根据响应返回结果
		return buildResult(response,record);
	}

	/**
	 * 根据响应,返回结果
	 * @param response
	 * @return
	 */
	protected abstract ResultDTO<ErrorData> buildResult(P response,SmsSendRecord record);

	/**
	 * 将同步响应更新到发送记录
	 * @param response
	 * @param record
	 * @return
	 */
	protected abstract SmsSendRecord updateRecord(P response, SmsSendRecord record);

	/**
	 * 发送,并获得响应
	 * @param requestObject
	 * @return
	 */
	protected abstract String send(R requestObject);

	/**
	 * 将返回的string转为对应response对象
	 * @param result
	 * @return
	 */
	abstract P stringToResponseObject(String result);
}
