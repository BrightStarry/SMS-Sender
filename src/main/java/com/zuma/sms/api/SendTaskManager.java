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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
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
	//等待队列锁, 主要用于迭代器-此处必须使用公平锁,防止删除方法一直被循环任务阻塞
	private ReentrantLock waitLock = new ReentrantLock(true);


	//短信回调数量累加处理
	//异步回调后,保存到此处
	private Map<Long, SendTaskAsyncStatus> sendTaskAsyncStatusMap = new ConcurrentHashMap<>();
	//存储目前已有map中的key
	private BlockingQueue<Long> sendTaskAsyncStatusKeyQueue = new LinkedBlockingDeque<>();
	//回调锁
	private ReentrantLock sendTaskAsyncStatusLock = new ReentrantLock();
	//回调等待使用
	private Condition sendTaskAsyncStatusCondition = sendTaskAsyncStatusLock.newCondition();



	@Autowired
	private SendTaskRecordService sendTaskRecordService;





	/**
	 * 运行该任务管理器
	 */
	public void run() {
		//等待任务出队
		startDelayTakeThread();
		//运行任务出队
		startRunTakeThread();
		//结束任务出队
		startCloseTakeThread();
		//定时获取异步累加器,然后处理
		startAsyncStatusTakeThread();


		log.info("[SendTaskManager]发送任务管理器-各线程启动完成");
	}

	/**
	 * 将指定任务id的异步成功或失败状态增加
	 * 此处必须加锁,防止刚取A出来,循环线程就把A删除处理了.
	 * @param taskId 任务id
	 * @param isSuccessNum true:增加成功数; false:增加失败数
	 * @Param num 累计数
	 */
	public void asyncStatusIncrement(long taskId, boolean isSuccessNum,int num) {
		try {
			sendTaskAsyncStatusLock.lock();
			log.info("[任务管理器]异步状态累加.收到.taskId:{},isSuccessNum",taskId,isSuccessNum);
			//尝试从map中获取指定id的异步状态对象
			SendTaskAsyncStatus sendTaskAsyncStatus = sendTaskAsyncStatusMap.get(taskId);
			//如果没有则新建,
			if (sendTaskAsyncStatus == null) {
				sendTaskAsyncStatus = new SendTaskAsyncStatus();
				sendTaskAsyncStatusMap.put(taskId,sendTaskAsyncStatus);
				sendTaskAsyncStatusKeyQueue.offer(taskId);
			}
			//累加数量
			sendTaskAsyncStatus.increment(isSuccessNum,num);
		} finally {
			sendTaskAsyncStatusLock.unlock();
		}
	}


	//控制线程状态若干-----------------------------------------------

	/**
	 * 恢复启动任务
	 */
	public void rerunTask(long sendTaskRecordId) {
		for (SendTask sendTask : runQueue) {
			if (sendTask.getId().equals(sendTaskRecordId)) {
				sendTask.pauseToRun();
				return;
			}
		}
		throw new SmsSenderException("任务已经停止.");
	}


	/**
	 * 暂停任务 , 指定暂停毫秒数
	 * 不加锁
	 */
	public void pauseTask(long sendTaskRecordId,Long time) {
		for (SendTask sendTask : runQueue) {
			if(sendTask.getId().equals(sendTaskRecordId)){
				sendTask.pause(time);
				return;
			}
		}
		throw new SmsSenderException("任务在运行队列不存在,删除失败");
	}


	/**
	 * 中断任务
	 * 此处暂不加锁,任务即使在中断过程中结束,也没什么关系
	 */
	public void interruptTask(Long sendTaskRecordId) {
		//此处不增删元素,直接使用增强for
		for (SendTask sendTask : runQueue) {
			if (sendTask.getId().equals(sendTaskRecordId)) {
				log.info("[任务管理器]任务中断成功.sendTask:{}",sendTask);
				sendTask.interrupt();
				return;
			}
		}
		throw new SmsSenderException("任务在运行队列不存在,中断失败");
	}

	/**
	 * 删除任务,如果它还在等待队列
	 * 该迭代器为弱一致性,自行加锁,使其在元素的减少上变为强一致性,此处不处理等待任务的新增
	 */
	public void removeTask(Long sendTaskRecordId) {

		try {
			waitLock.lock();
			//遍历,如果id相同,删除该对象
			Iterator<SendTask> iterator = waitQueue.iterator();
			SendTask item;
			while (iterator.hasNext()) {
				item = iterator.next();
				if (item.getId().equals(sendTaskRecordId)) {
					iterator.remove();
					log.info("[任务管理器]任务删除成功.sendTaskId:{}",sendTaskRecordId);
					return;
				}
			}
			//未删除
			throw new SmsSenderException(ErrorEnum.TASK_EMPTY_IN_WAIT_QUEUE_ERROR);
		} finally {
			waitLock.unlock();
		}
	}

	/**
	 * 根据 发送任务记录 修改任务,如果它还在等待队列
	 */
	public void modifyTask(SendTaskRecord newSendTaskRecord) {
		//删除原对象的记录
		removeTask(newSendTaskRecord.getId());
		//构建新对象
		openTask(newSendTaskRecord);
	}


	/**
	 * 根据 数据库中的发送任务记录 新建任务
	 */
	public void openTask(SendTaskRecord sendTaskRecord) {
		//新建任务并入队
		put(SendTask.build(sendTaskRecord));
		log.info("[任务管理器]任务开启.sendRecord:{}",sendTaskRecord);
	}

	/**
	 * 任务入队,根据任务状态
	 */
	public void put(SendTask sendTask) {
		try {
			switch (EnumUtil.getByCode(sendTask.getStatus(), SendTaskStatusEnum.class,"[任务管理器]任务入队.状态异常.")) {
				case WAIT:
					log.info("[任务管理器]等待队列入队.sendTask:{}",sendTask);
					waitQueue.put(sendTask);
					break;
				case RUN:
					log.info("[任务管理器]运行队列入队.sendTask:{}",sendTask);
					runQueue.put(sendTask);
					break;
				case CLOSE:
					log.info("[任务管理器]结束队列入队.sendTask:{}",sendTask);
					closeQueue.put(sendTask);

			}
		} catch (InterruptedException e) {
			log.error("[发送任务管理器]任务入队失败.e:{}", e.getMessage(), e);
			throw new SmsSenderException("任务入队失败");
		}
	}


	//若干线程--------------------------------------------

	/**
	 * 定时从map中获取异步回调,累加到已经完成的任务上
	 */
	private void startAsyncStatusTakeThread() {
		//定时从map中获取异步回调,累加到已经完成的任务上
		new Thread((new Runnable() {
			@Override
			public void run() {
				while (true) {
					Long taskId;
					try {
						sendTaskAsyncStatusLock.lock();
						//返回特殊值的获取方式 返回null
						taskId = sendTaskAsyncStatusKeyQueue.poll();
						if(taskId != null){
							//删除并获取
							SendTaskAsyncStatus sendTaskAsyncStatus = sendTaskAsyncStatusMap.remove(taskId);
							log.info("[任务管理器]处理任务异步回调.获取到任务:{},status:{}",taskId,sendTaskAsyncStatus);
							//累加
							sendTaskRecordService.incrementSuccessAndFailedNumById(sendTaskAsyncStatus.getAsyncSuccessNum().get(),
									sendTaskAsyncStatus.getAsyncFailedNum().get(), taskId,true);
						}
						//每次等待 30s
						sendTaskAsyncStatusCondition.await(30, TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						log.error("[任务管理器]处理任务异步回调.失败.e:{}", e.getMessage(), e);
					}finally {
						sendTaskAsyncStatusLock.unlock();
					}
				}
			}
		})).start();
	}

	/**
	 * 从结束任务队列取出任务, 进行处理
	 */
	private void startCloseTakeThread() {
		//从结束任务队列取出任务, 进行处理
		new Thread((new Runnable() {
			@Override
			public void run() {
				while (true) {
					SendTask task;
					try {
						//取出任务
						task = closeQueue.take();
						log.info("[任务管理器]结束队列-任务出队.task:{}",task);
						//处理任务
						task.endHandle();
						task = null;
					} catch (Exception e) {
						//..此处失败不做处理
						log.error("[任务管理器]取出结束任务失败.e:{}", e.getMessage(), e);
					}
				}
			}
		})).start();
	}

	/**
	 * 从运行中的任务 延时 队列中获取到期任务
	 */
	private void startRunTakeThread() {
		//从运行中的任务 延时 队列中获取到期任务
		new Thread((new Runnable() {
			@Override
			public void run() {
				SendTask task;
				while (true) {
					try {
						//获取任务
						task = runQueue.take();
						log.info("[任务管理器]运行队列-任务出队.task:{}",task);
						//如果任务还在运行-中断任务
						if (task.isRun()){
							log.info("[任务管理器]运行队列-任务出队-任务还在运行-中断中");
							task.interrupt();//中断后任务会在结束后进入处理队列
						}else
							log.info("[任务管理器]运行队列-任务出队.任务已经处理.");
						//防止在下一个循环阻塞时,该对象不GC
						task = null;
					} catch (Exception e) {
						//...此处失败不做处理,
						log.error("[任务管理器]取出运行任务失败.e:{}", e.getMessage(), e);
					}
				}
			}
		})).start();
	}


	/**
	 * 等待任务获取线程
	 */
	private void startDelayTakeThread() {
		//从等待任务队列中获取可以开始的任务
		new Thread((new Runnable() {
			@Override
			public void run() {
				while (true) {
					//每次循环需要刷新一次,否则finally中处理的,可能会是上次的任务对象
					SendTask task = null;
					try {
						waitLock.lock();
						//获取可运行任务
						//此处超时退出机制,是为了能保证锁不会一直被它持有,让删除该队列中对象的代码可以运行
						task = waitQueue.poll(300, TimeUnit.MILLISECONDS);
						if(task == null)
							continue;

						//任务准备
						task.setup(closeQueue);
						log.info("[任务管理器]等待队列-任务出队.任务开始.task:{}",task);
						//任务入队
						put(task);

						//任务开始
						task.start();
					} catch (Exception e) {
						log.error("[任务管理器]开始任务失败.e:{}", e.getMessage(), e);
						//发生异常后,如果已经取出任务,将任务状态改为全部失败
						if (task != null) {
							task.interrupt();
							//构建异常对象
							ResultDTO<?> error = ResultDTO.error(String.valueOf(SendTaskRecordStatusEnum.FAILED.getCode()),
									ErrorEnum.TASK_START_ERROR.getMessage() + ":" + e.getMessage());
							//更新记录状态以及异常信息
							sendTaskRecordService.updateStatus(task.getId(),
									SendTaskRecordStatusEnum.FAILED, error.getMessage());
						}

					}finally {
						waitLock.unlock();
					}
				}
			}
		})).start();
	}

}
