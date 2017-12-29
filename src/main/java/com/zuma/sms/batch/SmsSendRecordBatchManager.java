package com.zuma.sms.batch;

import com.zuma.sms.api.FileAccessor;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.IdStatusPair;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
	 * 要修改状态的id status 集合
	 */
	private List<IdStatusPair> idStatusPairs = new LinkedList<>();

	/**
	 * 锁
	 */
	private ReentrantLock lock1 = new ReentrantLock(true);
	/**
	 * 锁
	 */
	private ReentrantLock lock2 = new ReentrantLock(true);

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
			lock1.lock();
			updates.add(record);
			if (updates.size() > 500) {
				batchSave();
			}
		} finally {
			lock1.unlock();
		}
	}

	/**
	 * 状态更新记录 增加
	 */
	public void add(IdStatusPair idStatusPair) {
		try {
			lock2.lock();
			idStatusPairs.add(idStatusPair);
			if (idStatusPairs.size() > 500) {
				batchUpdateStatus();
			}
		} finally {
			lock2.unlock();
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
					if(updates.size() > 0)
						batchSave();
					if(idStatusPairs.size() > 0)
						batchUpdateStatus();
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
			lock1.lock();
			log.info("[短信发送记录 批处理管理器]批量保存开始.");
				batchService.batchSave(updates);
			log.info("[短信发送记录 批处理管理器]批量保存完成.");
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
			lock1.unlock();
		}
	}

	/**
	 * 状态保存
	 */
	public void batchUpdateStatus() {
		try {
			lock2.lock();
			log.info("[短信发送记录 批处理管理器]批量更新状态开始.");
			batchService.batchUpdateStatus(idStatusPairs);
			log.info("[短信发送记录 批处理管理器]批量更新状态完成.");
		} catch (Exception e) {
			log.error("[短信发送记录 批处理管理器]批量更新状态异常.e:{}", e.getMessage(), e);
			File file = new File(configStore.batchErrorFilePath);
			fileAccessor.writeStringToFile(file,new Date() + " 异常数据.e:" + e.getMessage(),true);
			//此处无法处理.导出所有数据
			for (IdStatusPair item : idStatusPairs) {
				fileAccessor.writeStringToFile(file,item.toString(),true);
			}
		} finally {
			idStatusPairs.clear();
			lock2.unlock();
		}
	}


}
