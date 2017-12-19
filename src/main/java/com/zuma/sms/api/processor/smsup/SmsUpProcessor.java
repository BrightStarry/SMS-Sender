package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.service.SmsUpRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 09:54
 * 短信上行 处理器 抽象类
 */
@Component
@Slf4j
public abstract class SmsUpProcessor<T> {

	public boolean process(T response, Channel channel) {
		//将上行对象转为 上行实体记录
		SmsUpRecord smsUpRecord = responseToSmsUpRecord(response,channel);
		//保存数据
		smsUpRecord = smsUpRecordService.save(smsUpRecord);
		//TODO 暂留.其他操作
		return commonProcess(smsUpRecord,channel);
	}

	private boolean commonProcess(SmsUpRecord smsUpRecord,Channel channel) {
		return false;
	}

	protected abstract SmsUpRecord responseToSmsUpRecord(T response,Channel channel);


	//spring bean init ....
	private static SmsUpRecordService smsUpRecordService;

	public void init(SmsUpRecordService smsUpRecordService) {
		SmsUpProcessor.smsUpRecordService = smsUpRecordService;
	}

}
