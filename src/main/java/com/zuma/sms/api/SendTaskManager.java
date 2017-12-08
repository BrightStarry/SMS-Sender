package com.zuma.sms.api;

import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.enums.SendTaskStatusEnum;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 17:49
 * 发送任务管理器
 * 存储 等待中/进行中/已结束 的任务
 */
@Component
@Slf4j
public class SendTaskManager {
	//等待任务 延时队列
	private BlockingQueue<SendTask> waitQueue = new DelayQueue<>();
	//运行中任务 延时队列-用来关闭超时任务
	private BlockingQueue<SendTask> runQueue = new DelayQueue<>();
	//结束任务 队列 非延时阻塞队列
	private BlockingQueue<SendTask> closeQueue = new LinkedBlockingQueue<>();

	@Autowired
	private SendTaskRecordService sendTaskRecordService;

	@Autowired
	private FileAccessor fileAccessor;


	/**
	 * 运行该任务管理器
	 */
	public void run() {
		//从等待任务队列中获取可以开始的任务
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					//每次循环需要刷新一次,否则finally中处理的,可能会是上次的任务对象
					SendTask task = null;
					try {
						//获取可运行任务
						task = waitQueue.take();
						//任务准备
						task.setup(closeQueue, fileAccessor);
						//任务开始
						task.run();
						//任务入队
						put(task);
					} catch (Exception e) {
						//发生异常后,如果已经取出任务,将任务状态改为全部失败
						if (task != null) {
							//构建异常对象
							ResultDTO<?> error = ResultDTO.error(String.valueOf(SendTaskRecordStatusEnum.FAILED.getCode()),
									ErrorEnum.TASK_START_ERROR.getMessage());
							//更新记录状态以及异常信息
							sendTaskRecordService.updateStatus(task.getSendTaskRecord().getId(),
									SendTaskRecordStatusEnum.FAILED, CodeUtil.objectToJsonString(error));
						}
						log.error("[任务管理器]开始任务失败.e:{}", e.getMessage(), e);
					}
				}
			}
		});

		//从运行中的任务 延时 队列中获取到期任务
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					SendTask task;
					try {
						//获取任务
						task = runQueue.take();
						//如果任务还在运行-中断任务
						if (task.isRun())
							task.interrupt();
						/**
						 * 如果任务未处理,此时任务状态为CLOSE,放入结束队列等待处理
						 * 此处可能存在同步问题,就是就是判断时,还未处理,入队时,
						 * 已经处理完成了.
						 * 所以.任务的处理方法需要同步
						 */
						if (!task.isEnd())
							put(task);
					} catch (Exception e) {
						//...此处失败不做处理,
						log.error("[任务管理器]取出运行任务失败.e:{}", e.getMessage(), e);
					}
				}
			}
		});

		//从结束任务队列取出任务, 进行处理
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					SendTask task;
					try {
						//取出任务
						task = closeQueue.take();
						//处理任务
						task.endHandle();
					} catch (Exception e) {
						//..此处失败不做处理
						log.error("[任务管理器]取出结束任务失败.e:{}", e.getMessage(), e);
					}
				}
			}
		});
	}


	/**
	 * 根据 发送任务记录 删除任务,如果它还在等待队列
	 */
	public void removeTask(SendTaskRecord sendTaskRecord) {
		//使用相同记录数据构建相同任务对象
		SendTask sendTask = SendTask.build(sendTaskRecord);
		//尝试删除该任务对象
		boolean flag = waitQueue.remove(sendTask);
		//删除失败,抛出异常
		if (!flag)
			throw new SmsSenderException(ErrorEnum.TASK_EMPTY_IN_WAIT_QUEUE_ERROR);
	}

	/**
	 * 根据 发送任务记录 修改任务,如果它还在等待队列
	 */
	public void modifyTask(SendTaskRecord oldSendTaskRecord,SendTaskRecord newSendTaskRecord) {
		//删除原对象的记录
		removeTask(oldSendTaskRecord);
		//构建新对象
		openTask(newSendTaskRecord);
	}


	/**
	 * 根据 数据库中的发送任务记录 新建任务
	 */
	public void openTask(SendTaskRecord sendTaskRecord) {
		//新建任务并入队
		put(SendTask.build(sendTaskRecord));
	}

	/**
	 * 任务入队,根据任务状态
	 */
	public void put(SendTask sendTask) {
		try {
			switch (EnumUtil.getByCode(sendTask.getStatus(), SendTaskStatusEnum.class)) {
				case WAIT:
					waitQueue.put(sendTask);
					break;
				case RUN:
					runQueue.put(sendTask);
					break;
				case CLOSE:
					closeQueue.put(sendTask);

			}
		} catch (InterruptedException e) {
			log.error("[发送任务管理器]任务入队失败.e:{}", e.getMessage(), e);
		}
	}

}
