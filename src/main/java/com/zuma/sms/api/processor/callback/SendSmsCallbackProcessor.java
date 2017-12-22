package com.zuma.sms.api.processor.callback;

import com.zuma.sms.api.SendTaskManager;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.PlatformService;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.CommonUtil;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/18 0018 15:09
 * 发送短信接口 回调 处理 抽象类
 * T 回调返回对象
 */
@Slf4j
@Component
public abstract class SendSmsCallbackProcessor<T> {


	/**
	 * 处理方法
	 */
	public boolean process(T response, Channel channel) {
		log.info("[发送短信回调处理]response:{},channelL{}",response,channel);
		//根据返回中携带的流水号,通过记录中的other_id,查询出数据库记录
		SmsSendRecord record = smsSendRecordService.findOneByOtherId(getOtherId(response));
		//根据数据拼接出 返回对象
		ResultDTO<ErrorData> resultDTO = getResultDTO(response, record);
		//更新数据库发送记录
		record = updateRecord(response, resultDTO, record);
		//调用通用处理,可重写该部分
		return commonProcess(record, resultDTO, channel);
	}

	protected abstract String getOtherId(T response);

	protected abstract ResultDTO<ErrorData> getResultDTO(T response, SmsSendRecord record);


	/**
	 * 通用处理
	 */
	protected boolean commonProcess(SmsSendRecord record, ResultDTO<ErrorData> resultDTO, Channel channel) {
		//判断是其他平台调用的发送 还是 定时发送任务发送的
		if (record.isTaskRecord()) {
			//如果是定时任务
			taskHandle(resultDTO, record, channel);
		} else {
			//如果是其他平台调用
			//发送返回对象给调用者
			sendCallback(resultDTO, record.getPlatformId());
		}
		return true;
	}


	/**
	 * 更新记录
	 *
	 * @param response
	 * @param resultDTO
	 * @param record
	 * @return
	 */
	protected SmsSendRecord updateRecord(T response, ResultDTO<ErrorData> resultDTO, SmsSendRecord record) {
		record.setAsyncTime(new Date())
				.setAsyncResultBody(CodeUtil.objectToJsonString(response))
				.setStatus(ResultDTO.isSuccess(resultDTO) ?
						SmsSendRecordStatusEnum.ASYNC_SUCCESS.getCode() :
						SmsSendRecordStatusEnum.ASYNC_FAILED.getCode());
		//如果失败,并且有异常信息,就设置上
		if (!ResultDTO.isSuccess(resultDTO) && StringUtils.isNotBlank(resultDTO.getMessage())) {
			record.setErrorInfo(CommonUtil.getString(record.getErrorInfo()) + "|" + resultDTO.getMessage());
		}
		return smsSendRecordService.save(record);
	}

	/**
	 * 处理定时任务结果
	 *
	 * @param resultDTO 返回对象
	 * @param record    记录
	 * @param channel   短信通道
	 */
	protected void taskHandle(ResultDTO<ErrorData> resultDTO, SmsSendRecord record, Channel channel) {
		sendTaskManager.asyncStatusIncrement(record.getSendTaskId(),ResultDTO.isSuccess(resultDTO));
	}


	/**
	 * 将ResultDTO发送给对应调用者
	 *
	 * @param resultDTO  返回对象
	 * @param platformId 返回给的平台id
	 */
	void sendCallback(ResultDTO<ErrorData> resultDTO, Long platformId) {
		//获取对应平台的回调url
		Platform platform = platformService.findOne(platformId);
		String callbackUrl = platform.getCallbackUrl();
		try {
			String result = httpClientUtil.doPostForString(callbackUrl, resultDTO);
			if (!callbackIsSuccess(result))
				throw new SmsSenderException("发送回调后,接收平台未成功");
		} catch (Exception e) {
			for (int i = 1; i <= 3; i++) {
				//重试
				log.error("[发送短信回调处理]给调用平台发送回调失败.当前重试次数={},调用者={},error={}", i, platform.getId(), e.getMessage(), e);
				try {
					String result = httpClientUtil.doPostForString(callbackUrl, resultDTO);
					if (callbackIsSuccess(result)) {

						log.error("[发送短信回调处理]给调用平台发送回调失败.重发{}次后,成功,调用者={}", i, platform.getId());
						return;
					}
				} catch (Exception e1) {

				}
			}
			//运行到此处,表示最终还是失败
			log.error("[发送短信回调处理]给调用平台发送回调最终失败");
			throw new SmsSenderException("给调用平台发送回调最终失败");
		}
	}


	/**
	 * 验证发送给对方平台的回调是否成功
	 */
	private boolean callbackIsSuccess(String result) {
		return result.equalsIgnoreCase("success");
	}

	/**
	 * 计算手机号个数
	 */
	protected int getPhoneLen(String phones) {
		return StringUtils.split(phones, ",").length;
	}


	//spring bean init............
	protected static PlatformService platformService;
	protected static SmsSendRecordService smsSendRecordService;
	protected static SendTaskRecordService sendTaskRecordService;
	protected static HttpClientUtil httpClientUtil;
	protected static SendTaskManager sendTaskManager;

	@Autowired
	public void init(PlatformService platformService,
					 SmsSendRecordService smsSendRecordService,
					 HttpClientUtil httpClientUtil,
					 SendTaskRecordService sendTaskRecordService,
					 SendTaskManager sendTaskManager) {
		SendSmsCallbackProcessor.platformService = platformService;
		SendSmsCallbackProcessor.smsSendRecordService = smsSendRecordService;
		SendSmsCallbackProcessor.httpClientUtil = httpClientUtil;
	}
}
