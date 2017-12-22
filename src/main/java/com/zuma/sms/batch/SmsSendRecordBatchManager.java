package com.zuma.sms.batch;

import com.zuma.sms.entity.SendTaskRecord;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/22 0022 14:58
 * 短信发送记录 批处理管理器
 */
@Component
public class SmsSendRecordBatchManager {
	//锁
	private ReentrantLock lock;
	//存储任务的集合
	private List<SendTaskRecord> list = new LinkedList<>();

}
