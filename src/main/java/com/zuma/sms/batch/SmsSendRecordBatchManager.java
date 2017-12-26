package com.zuma.sms.batch;

import com.zuma.sms.api.FileAccessor;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/22 0022 14:58
 * 短信发送记录 批处理管理器
 */
@Component
@Slf4j
public class SmsSendRecordBatchManager {
	/**
	 * 要修改的短信发送记录集合
	 */
	private List<SmsSendRecord> updates = new LinkedList<>();
	/**
	 * 锁
	 */
	private ReentrantLock lock = new ReentrantLock(true);
	/**
	 * 定时线程池
	 */
	private ScheduledExecutorService scheduleSaveExecutor;

	@Autowired
	private BatchService batchService;

	@Autowired
	private FileAccessor fileAccessor;

	@Autowired
	private ConfigStore configStore;

	/**
	 * 要修改的记录 增加
	 */
	public void add(SmsSendRecord record) {
		try {
			lock.lock();
			updates.add(record);
			if (updates.size() > 500) {
				batchSave();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 定时线程保存
	 */
	public void scheduleSaveStart() {
		scheduleSaveExecutor = Executors.newScheduledThreadPool(1);
		scheduleSaveExecutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					batchSave();
				} catch (Exception e) {
					log.error("[短信发送记录 批处理管理器]定时批量保存异常.e:{}", e.getMessage(), e);
				}
			}
		},30,10, TimeUnit.SECONDS);
	}

	/**
	 * 保存
	 */
	public void batchSave() {
		try {
			lock.lock();
			batchService.batchSave(updates);
		} catch (Exception e) {
			log.error("[短信发送记录 批处理管理器]批量保存异常.e:{}", e.getMessage(), e);
			File file = new File(configStore.batchErrorFilePath);
			fileAccessor.writeStringToFile(file,new Date() + " 异常数据.e:" + e.getMessage(),true);
			//此处无法处理.导出所有数据
			for (SmsSendRecord item : updates) {
				fileAccessor.writeStringToFile(file,item.toString(),true);
			}
		} finally {
			updates.clear();
			lock.unlock();
		}
	}


}
