package com.zuma.sms.api.processor;

import com.zuma.sms.api.processor.send.SendSmsProcessor;
import com.zuma.sms.dto.*;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.PlatformSendSmsRecord;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.SmsAndPhoneRelationEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.factory.ProcessorFactory;
import com.zuma.sms.form.PlatformSendSmsForm;
import com.zuma.sms.service.BatchService;
import com.zuma.sms.service.SmsSendRecordService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import java.util.LinkedList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/27 0027 14:14
 * 平台发送短信处理器 主要处理:
 * 1.短信的一对一 : 一对多 等逻辑
 * 2.将每个要发送的数据预先保存成发送记录
 * 3.调用发送记录
 * 4.返回结果
 */
@Component
@Slf4j
public class PlatformSendSmsProcessor {
	@Autowired
	private SmsSendRecordService smsSendRecordService;
	@Autowired
	private ProcessorFactory processorFactory;

	/**
	 * 处理方法
	 *
	 * @param channel                 通道
	 * @param platform                平台
	 * @param phones       			手机号
	 * @param messages                 消息
	 * @param smsAndPhoneRelationEnum 消息和手机号对应关系枚举
	 * @param platformSendSmsRecord   平台调用发送记录
	 * @return
	 */
	public ResultDTO<ApiResult> process(Channel channel, Platform platform,
										String[] phones,
										String[] messages,
										SmsAndPhoneRelationEnum smsAndPhoneRelationEnum,
										PlatformSendSmsRecord platformSendSmsRecord) {

		//返回对象
		List<ResultDTO<SendData>> errorResultList = new LinkedList<>();
		ApiResult apiResult = new ApiResult().setErrorResults(errorResultList);

		//短信发送记录
		List<SmsSendRecord> smsSendRecordList = new LinkedList<>();
		//发送类
		SendSmsProcessor sendSmsProcessor = processorFactory.buildSendSmsProcessor(channel);
		try {
			switch (smsAndPhoneRelationEnum) {
				//一对一
				case ONE_ONE:
					//预先新增发送记录
					SmsSendRecord smsSendRecord = smsSendRecordService.save(new SmsSendRecord(platformSendSmsRecord.getId(), platform.getId(),
							channel.getId(), channel.getName(), phones[0], 1, messages[0]));
					//发送并加入返回list
					resultHandle(errorResultList,sendSmsProcessor.process(channel, smsSendRecord));
					break;
				//一对多
				case ONE_MULTI:
					//空间换时间-循环
					Long platformSendSmsRecordId = platformSendSmsRecord.getId();
					Long platFormId = platform.getId();
					Long channelId = channel.getId();
					String channelName = channel.getName();
					int length = phones.length;
					int maxGroupNumber = channel.getMaxGroupNumber();
					//如果该通道最大群发数是1
					if (maxGroupNumber == 1) {
						//循环创建发送记录
						for (int i = 0; i < length; i++) {
							smsSendRecordList.add(new SmsSendRecord(platformSendSmsRecordId, platFormId,
									channelId, channelName, phones[i], 1, messages[0]));
						}
					} else {
						//否则
						for (int i = 0; i < length; i += maxGroupNumber) {
							//拼接每条发送记录的手机号字符串
							StringBuilder temp = new StringBuilder();
							//遍历时保证最后一次循环时,不会数组越界
							int j = 0;
							for (int x = i + j; j < maxGroupNumber && x < length; j++, x++) {
								temp.append(",").append(phones[x]);
							}
							//截取逗号
							temp.deleteCharAt(0);
							smsSendRecordList.add(new SmsSendRecord(platformSendSmsRecordId, platFormId,
									channelId, channelName, temp.toString(), j - 1, messages[0]));
						}
					}
					//批量保存
					smsSendRecordList = smsSendRecordService.save(smsSendRecordList);

					//循环调用
					for (SmsSendRecord item : smsSendRecordList) {
						resultHandle(errorResultList,sendSmsProcessor.process(channel, item));
					}
					break;
				//多对多
				case MULTI_MULTI:
					//空间换时间-循环
					platformSendSmsRecordId = platformSendSmsRecord.getId();
					platFormId = platform.getId();
					channelId = channel.getId();
					channelName = channel.getName();
					length = phones.length;

					//因为短信消息不同,所以只能一条条发送
					//循环创建发送记录
					for (int i = 0; i < length; i++) {
						smsSendRecordList.add(new SmsSendRecord(platformSendSmsRecordId, platFormId,
								channelId, channelName, phones[i], 1, messages[i]));
					}
					//批量保存
					smsSendRecordList = smsSendRecordService.save(smsSendRecordList);

					//循环调用
					for (SmsSendRecord item : smsSendRecordList) {
						resultHandle(errorResultList,sendSmsProcessor.process(channel, item));
					}
					break;
			}

			//失败数
			apiResult.setErrorNum(errorResultList.size());

		} catch (Exception e) {
			//..基本没可能异常
			log.error("[平台接口发送处理器]异常.e:{}", e.getMessage(), e);
			return ResultDTO.error(ErrorEnum.PROCESS_ERROR.getCode(),
					ErrorEnum.PROCESS_ERROR.getMessage() + ":" + e.getMessage(), apiResult);
		}
		return ResultDTO.success(apiResult);
	}

	/**
	 * 处理返回数据
	 * @param errorResultList
	 * @param resultDTO
	 */
	private void resultHandle(List<ResultDTO<SendData>> errorResultList, ResultDTO<SendData> resultDTO) {
		if(!ResultDTO.isSuccess(resultDTO)){
			errorResultList.add(resultDTO);
		}
	}
}
