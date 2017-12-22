package com.zuma.sms.schedule;

import com.zuma.sms.service.SendTaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * author:ZhengXing
 * datetime:2017/12/22 0022 12:58
 * 发送任务相关定时任务
 */
@Component
@Slf4j
public class SendTaskSchedule {

	@Autowired
	private SendTaskRecordService sendTaskRecordService;


	/**
	 * 清理失败任务
	 */
	@Scheduled(fixedRate = 60 * 1000)
	public void taskClean() {
		try {
			sendTaskRecordService.cleanFailedTask();
		} catch (Exception e) {
			log.error("[发送任务-定时任务-清理失败任务]失败.error:{}",e.getMessage(),e);
		}
	}

}
