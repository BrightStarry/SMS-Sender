package com.zuma.sms.api;

import com.zuma.sms.api.resolver.CommonMessageResolver;
import com.zuma.sms.api.resolver.MessageResolver;
import com.zuma.sms.batch.SmsSendRecordBatchManager;
import com.zuma.sms.enums.db.SmsSendRecordStatusEnum;
import com.zuma.sms.factory.MessageResolverFactory;
import com.zuma.sms.api.processor.send.SendSmsProcessor;
import com.zuma.sms.factory.ProcessorFactory;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.*;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.enums.SendTaskStatusEnum;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.service.BatchService;
import com.zuma.sms.service.ChannelService;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.service.SmsSendRecordService;
import com.zuma.sms.util.DateUtil;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.NumberFileUtil;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 15:01
 * 任务对象
 * 继承延时接口,放入延时队列
 */
@Accessors(chain = true)
@Getter
@Setter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Component
@ToString(exclude = {"channel", "mainQueue", "startTime", "endTime", "realStartTime", "realEndTime", "executor", "sendSmsProcessor", "messageResolver",
		"closeQueue", "lock", "isPause", "latch", "successNum", "failedNum", "usedNum", "shardThread", "dateRange", "pauseCondition", "resultHandleExecutor", "resultQueue", "messageResolver"})
public class SendTask implements Delayed {

	//任务id-发送记录的id
	private Long id;
	//数据库中的发送任务记录
	private volatile SendTaskRecord sendTaskRecord;
	//号码组并发队列 - 阻塞队列,最高存储1000个号码
	private BlockingQueue<SmsSendRecord> mainQueue;
	//线程池
	private ExecutorService executor;
	//任务开始时间
	private Long startTime;
	//任务结束时间
	private Long endTime;
	//实际开始时间
	private Long realStartTime;
	//实际结束时间
	private Long realEndTime;
	//通道实体类
	private Channel channel;
	//短信发送器
	private SendSmsProcessor sendSmsProcessor;
	//消息解析器
	private MessageResolver messageResolver;


	//结束任务队列-用来结束后将this放入
	private BlockingQueue<SendTask> closeQueue;
	//锁-结束处理/任务停止/任务状态/任务暂停
	private ReentrantLock lock;
	//闭锁-等待所有线程执行完毕
	private CountDownLatch latch;
	//是否中断 - 操作时不关心其当前值,无需加锁,直接用volatile
	private volatile boolean isInterrupt = false;
	//内部的是否中断判断.用来在任务完成后.自行结束
	private volatile boolean privateIsInterrupt = false;
	//任务状态, 等待 运行中 结束 处理完成
	private volatile Integer status = SendTaskStatusEnum.WAIT.getCode();

	//数量
	//同步-成功数
	private AtomicInteger successNum;
	//同步-失败数
	private AtomicInteger failedNum;
	//已操作数
	private AtomicInteger usedNum;


	//分段操作
	//每段数量 -- 分组操作的任务
	private int shardNum;
	//当前任务总段数-需要分成几组发送
	private int shardNoSum;
	//当前是第几段操作 默认为0
	private volatile int shardNo = 0;
	//分段监听线程
	private Thread shardThread;
	//获取当前分段的时间范围
	private DateHourPair dateRange;
	//总的时间范围
	private List<DateHourPair> dateHourPairs;

	//任务暂停
	//任务是否暂停
	private volatile boolean isPause = false;
	//任务暂停condition
	private Condition pauseCondition;
	//任务暂停时间
	private volatile long pauseTime;

	//解析发送结果线程池
	private ExecutorService resultHandleExecutor;
	//结果队列
	private BlockingQueue<ResultDTO<SendData>> resultQueue;
	//结果处理是否完成
	private volatile boolean isResultHandleDone = false;


	//任务准备-----------------------------------------

	/**
	 * 任务准备
	 */
	public void setup(BlockingQueue<SendTask> closeQueue) {
		//id
		this.id = sendTaskRecord.getId();
		//修改记录状态为运行中
		this.sendTaskRecord = sendTaskRecordService.updateStatus(id, SendTaskRecordStatusEnum.RUN);
		//开始时间
		this.realStartTime = System.currentTimeMillis();
		//修改当前任务状态为 运行中
		this.status = SendTaskStatusEnum.RUN.getCode();
		//获取到号码集合的并发队列
//		this.phoneQueue = fileAccessor.readBySendTask(sendTaskRecord.getNumberGroupId());
		this.mainQueue = new LinkedBlockingQueue<>(configStore.mainQueueLen);
		//构造线程池
		this.executor = Executors.newCachedThreadPool();
		this.resultHandleExecutor = Executors.newFixedThreadPool(sendTaskRecord.getThreadCount() / 2 + 1);
		//结果队列 - 无界
		this.resultQueue = new LinkedBlockingQueue<>();
		//构造锁
		this.lock = new ReentrantLock();
		this.latch = new CountDownLatch(sendTaskRecord.getThreadCount());
		//构造暂停条件
		this.pauseCondition = this.lock.newCondition();
		//其他
		this.successNum = new AtomicInteger();
		this.failedNum = new AtomicInteger();
		this.usedNum = new AtomicInteger();
		//查询通道
		this.channel = channelStore.get(sendTaskRecord.getChannelId());
		//匹配短信发送器
		this.sendSmsProcessor = processorFactory.buildSendSmsProcessor(channel);
		//消息解析器
		this.messageResolver = MessageResolverFactory.build(channel);
		//放入 结束任务队列,以便
		this.closeQueue = closeQueue;

		//预先插入所有发送记录
		preInsertSmsSendRecord();

		//分时任务准备
		setupShard();
	}

	/**
	 * 预先新建所有发送记录
	 */
	private void preInsertSmsSendRecord() {
		File file = NumberFileUtil.getFileByNumberGroupId(sendTaskRecord.getNumberGroupId());
		//当前偏移量
		long off = 0;
		//每次默认拉取字符数
		int len = configStore.phoneStrNum;
		String phoneStr;
		//每次最大发送手机数 * 12(11位+一个逗号,最后一个手机号后可能没有逗号)
		int maxGroupNumber12 = channel.getMaxGroupNumber() * 12;
		//每次解析后的手机号和短信
		PhoneMessagePair phoneMessagePair = null;
		//每次手机号字符
		String itemPhone = null;
		//短信内容
		String smsMessage = sendTaskRecord.getContent();
		//每次截取到的手机号字符串总长度
		int phoneStrLen;

		List<SmsSendRecord> records = new LinkedList<>();

		//第一层循环-分批读取文件中的前x个号码字符串
		while (StringUtils.isNotBlank((phoneStr = fileAccessor.readString(file, off, len)))) {
			//偏移量累加
			off += len;
			phoneStrLen = phoneStr.length();
			//第二层循环 - 遍历手机号, 步长为 该通道每次最大发送数*每个手机字符数
			for (int i = 0; i < phoneStrLen; i += maxGroupNumber12) {
				itemPhone = null;
				try {
					itemPhone = phoneStr.substring(i, i + maxGroupNumber12 < phoneStrLen ? i + maxGroupNumber12 : phoneStrLen);
					//如果以逗号结尾,截取逗号
					itemPhone = StringUtils.removeEnd(itemPhone, ",");
					//解析号码和短信内容
					phoneMessagePair = messageResolver.resolve(itemPhone, smsMessage);
					if (phoneMessagePair.getMessage() == null)
						//加入记录list
						records.add(new SmsSendRecord(id, channel.getId(), channel.getName(), phoneMessagePair.getPhones(),
								phoneMessagePair.getPhones().length() / 11, phoneMessagePair.getMessage()));
				} catch (Exception e) {
					if (!(e instanceof SmsSenderException)) {
						log.error("[任务]任务id:{},预先插入发送记录,截取单次发送手机号失败." +
								"phoneStrLen:{},itemPhone:{},phoneMessagePair:{},e:{}", phoneStrLen, itemPhone, phoneMessagePair, e.getMessage(), e);
					}
					//如果失败
					if (itemPhone != null) {
						//失败总数增加
						failedIncrement(StringUtils.split(itemPhone, ",").length);
						//写入失败日志
						fileAccessor.writeBySendTaskId(id, itemPhone, e.getMessage());
					}
				}
			}
			//调用批量保存方法
			batchService.batchInsertSmsSendRecord(records);
			//清空list -- 直接new了.防止引用传参引发的问题
			records = new LinkedList<>();
		}
	}


	/**
	 * 分时任务准备
	 */
	private void setupShard() {
		//如果是分时任务,
		if (EnumUtil.equals(sendTaskRecord.getIsShard(), IntToBoolEnum.TRUE)) {
			//计算出日期小时分段数据
			dateHourPairs = DateUtil.customParseDate(sendTaskRecord.getExpectStartTime(), sendTaskRecord.getExpectEndTime());
			//计算每小时的发送数目,至少1
			shardNum = sendTaskRecord.getNumberNum() / dateHourPairs.size() + 1;
			//计算当前分段数-分成几段发送
			shardNoSum = dateHourPairs.size();
			//获取当前第0段分段时间范围
			dateRange = dateHourPairs.get(shardNo);
		}
	}


	//任务开始-------------------------------------------------------------------------------------------

	/**
	 * 任务开始.异步
	 */
	public void start() {
		new Thread((new Runnable() {
			@Override
			public void run() {
				try {
					run1();
				} catch (Exception e) {
					log.error("[任务]任务id:{}.任务运行期间异常....几乎不会发生的.e:{}", id, e.getMessage(), e);
				}
			}
		})).start();
	}


	/**
	 * 任务开始
	 */
	private void run1() throws Exception {
		//启动记录拉取线程
		startRecordPullThread();

		//启动主线程
		startMainThread(sendTaskRecord.getThreadCount());

		//启动返回结果处理线程
		startResultHandleThread(sendTaskRecord.getThreadCount() / 2 + 1);

		//如果是分组任务,会开启分组线程
		startShardThread();

		//自身等待
		latch.await();

		log.info("[任务]任务id:{}.任务门闩被释放.准备结束.task:{}", id, this);
		//结束
		shutdown(true);
	}

	/**
	 * 开启结果处理线程
	 *
	 * @param threadNum
	 */
	private void startResultHandleThread(int threadNum) {
		//处理返回结果线程.不断从结果队列拉取结果,处理
		Runnable resultHandleThread = new Runnable() {
			@Override
			public void run() {
				ResultDTO<SendData> result = null;
				log.info("[任务]任务id:{},处理发送结果线程.启动.", id);
				while (true) {
					try {
						result = resultQueue.poll(3000, TimeUnit.MILLISECONDS);
						//如果获取结果为空了,并且被中断了才退出,否则继续运行
						if (result == null) {
							if (isInterrupt) {
								//结束前通知
								isResultHandleDone = true;
								break;
							} else
								continue;
						}
						log.info("[任务]任务id:{},处理发送结果线程.运行中.", id);
						int phoneNum = result.getData().getCount();
						if (ResultDTO.isSuccess(result)) {
							//成功总数+
							successIncrement(phoneNum);
						} else {
							//如果失败
							//失败总数增加
							failedIncrement(phoneNum);
							//写入失败日志
							fileAccessor.writeBySendTaskId(id, result.getData().getPhones(),
									result.getData().getMessages());
						}

					} catch (InterruptedException e) {
						log.error("[任务]任务id:{},处理发送结果线程.处理异常.result:{},e:{}", id, result, e.getMessage(), e);
					}
				}
				log.info("[任务]任务id:{},处理发送结果线程.结束.resultQueueSize:{}", id, resultQueue.size());
			}
		};
		for (int i = 0; i < threadNum; i++) {
			executor.execute(resultHandleThread);
		}
	}

	/**
	 * 开启主线程
	 */
	private void startMainThread(int threadNum) {
		//主任务 - 从记录队列中拉取记录,发送,并将结果放入结果队列
		final Runnable main = new Runnable() {
			@Override
			public void run() {
				SmsSendRecord smsSendRecord;
				//循环到外部是否中断标识或内部标识有一个中断,即退出
				while (!isInterrupt && !privateIsInterrupt) {
					try {
						//检测到暂停信号,暂停
						if (isPause) {
							pauseThis(pauseTime);
						}
						//超时退出 阻塞的获取记录
						smsSendRecord = mainQueue.poll(3000, TimeUnit.MILLISECONDS);
						//如果为空,跳过该次循环
						if (smsSendRecord == null)
							continue;
						//发送-并将结果放入结果队列
						resultQueue.put(sendSmsProcessor.process(channel, smsSendRecord));
						//累加已操作数
						usedIncrement(smsSendRecord.getPhoneCount());
					} catch (Exception e) {
						//...此处也基本不可能失败了
						log.error("[任务]任务id:{}.sendTaskId:{},单次发送异常.e:{}", id, e.getMessage(), e);
					}
				}
				log.info("[任务]任务id:{}.主进程结束.", id);
				//通知主线程,该线程运行完毕
				latch.countDown();

			}
		};
		for (int i = 0; i < threadNum; i++) {
			this.executor.execute(main);
		}
	}

	/**
	 * 开启记录拉取线程
	 */
	private void startRecordPullThread() {
		//启动线程.不断从数据库记录中拉取对应的发送记录.
		Runnable phonePut = new Runnable() {
			@Override
			public void run() {

				//当前页数
				int pageNo = 1;
				//每次读取到的分页记录
				PageVO<SmsSendRecord> pageVO;
				log.info("[任务]任务id:{},手机号码拉取线程.启动.", id);
				do {
					try {
						//读取 1000条记录
						pageVO = smsSendRecordService.findByTaskIdPage(id, pageNo++, configStore.recordPullOnRunNum);
						if (pageVO.getCurrentSize() == 0)
							break;
						for (SmsSendRecord item : pageVO.getList()) {
							//不停插入直到成功或者中断- - 此处不做暂停处理,插满后,自动阻塞
							try {
								while (!(mainQueue.offer(item, 10000, TimeUnit.MILLISECONDS)) && !isInterrupt) ;
							} catch (Exception e) {
								log.error("[任务]任务id:{},任务记录拉取线程.插入记录到阻塞队列异常.e:{}", e.getMessage(), e);
								//...基本不会出现.不作处理
							}
						}
					} catch (Exception e) {
						log.error("[任务]任务id:{},任务记录拉取线程.异常.e:{}", id, e.getMessage(), e);
					}
				} while (!isInterrupt);
				log.info("[任务]任务id:{},手机号码拉取线程.结束.", id);

				try {
					//该线程结束后,准备结束该任务
					//号码总数
					int numberNum = sendTaskRecord.getNumberNum();
					Condition condition = lock.newCondition();
					//未发送完,一直循环,并且未中断
					while (usedNum.get() != numberNum && !isInterrupt) {
						try {
							lock.lock();
							condition.await(1000, TimeUnit.MILLISECONDS);
						} finally {
							lock.unlock();
						}
					}
					//跳出循环了.如果不是被外部中断的,将内部中断设置为true
					if (!isInterrupt)
						privateIsInterrupt = true;
				} catch (Exception e) {
					log.error("[任务]任务id:{},任务记录拉取线程.等待任务结束.异常.e:{}", e.getMessage(), e);
					//..没有失败的可能
				}
			}
		};
		executor.execute(phonePut);
	}


	/**
	 * 如果是分段操作的,启动监听线程
	 */
	public void startShardThread() {
		if (!EnumUtil.equals(sendTaskRecord.getIsShard(), IntToBoolEnum.TRUE))
			return;
		shardThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//未结束,一直运行
				while (!isInterrupt) {
					try {
						//如果当前是最后一组 (最后一组直接执行到结束),直接关闭该线程
						if (shardNo == shardNoSum)
							break;

						//如果超出当前分段的时间范围,并且不是最后一段分段
						//此处使用while是防止 开始时间过早.所以需要直接循环出当前时间所在的分段时间段
						while (dateRange.compareRange(new Date()) == 1) {
							//分段序号+1
							shardNo++;
							//获取新的分段时间范围
							dateRange = dateHourPairs.get(shardNo);
						}


						log.info("[任务]任务id:{}.分段线程运行中. isPause:{},isInterrupt:{},shardNo:{},usedNum:{}",
								id, isPause, isInterrupt, shardNo, usedNum);

						//判断当前已操作的号码数,是否大于等于当前分组需要操作的号码数
						if (usedNum.get() >= shardNum * shardNo) {
							try {
								lock.lock();
								//暂停任务-
								//如果此时任务已经被手动暂停,直接暂停自身
								if (isPause) {
									pauseThis(pauseTime);
								} else {
									//任务暂停时间  本次分段结束时间 - 当前时间 = 暂停的时间
									pauseTime = dateRange.getEndTime().getTime() - System.currentTimeMillis();
									//DB任务状态
									sendTaskRecord = sendTaskRecordService.updateStatus(id, SendTaskRecordStatusEnum.PAUSE);
									pauseThis(pauseTime);
								}
							} finally {
								lock.unlock();
							}
						}

						//每次执行完毕.等待3s.
						try {
							lock.lock();
							pauseCondition.await(3000, TimeUnit.MILLISECONDS);
						} finally {
							lock.unlock();
						}
					} catch (Exception e) {
						log.error("[任务]任务id:{}.分段线程异常.error:{}", id, e.getMessage(), e);
					}
				}
				log.info("[任务]任务id:{}.分段线程结束. isPause:{},isInterrupt:{},shardNo:{},usedNum:{}",
						id, isPause, isInterrupt, shardNo, usedNum);
			}
		});
		executor.execute(shardThread);

	}


	//任务结束处理----------------------------------------

	/**
	 * 任务结束处理
	 * 在{@link #shutdown(boolean)}方法后执行
	 */
	public void endHandle() {
		try {
			log.info("[任务]任务id:{}.结束处理-开始处理任务.task:{}", id, this);
			lock.lock();
			//再次判断是否处理过
			if (isEnd()) {
				log.info("[任务]任务id:{}.结束处理-该任务已经处理.task:{}", id, this);
				return;
			}
			//判断是否仍然在运行,直接关闭
			if (isRun()) {
				//将状态改为中断
				interrupt();
				shutdown(false);
			}


			//等待 结果处理线程完成
			Condition condition = lock.newCondition();
			while (!isResultHandleDone) {
				condition.await(1000, TimeUnit.MILLISECONDS);
			}
			//此处不做查询直接修改,任务一旦开始后,不允许修改任务记录
			sendTaskRecord
					.setRealStartTime(new Date(this.realStartTime))
					.setRealEndTime(new Date(this.realEndTime))
					.setTotalTime((int) (TimeUnit.SECONDS.convert((this.realEndTime - this.realStartTime), TimeUnit.MILLISECONDS)))
					.setUsedNum(this.usedNum.get())
					.setSuccessNum(successNum.get())
					.setFailedNum(failedNum.get());
			//保存
			sendTaskRecordService.save(sendTaskRecord);
			log.info("[任务]任务id:{}.结束处理-处理完成.task:{}", id, this);
		} catch (Exception e) {
			log.error("[任务]任务id:{}.结束处理-处理失败.task:{},e:{}", id, this, e.getMessage(), e);
			sendTaskRecordService.updateErrorInfo(id, "结束处理失败");
		} finally {
			//修改状态为处理完成
			setStatus(SendTaskStatusEnum.END.getCode());
			lock.unlock();
		}
	}

	/**
	 * 任务结束
	 * 此处加锁,是为了防止,从运行中队列取出,
	 * 进行中断操作,还未中断完成,对象就被
	 * 结束处理任务取出,进行处理;
	 * 加锁后,如果还未完成该方法,则处理方法等待.
	 * 如果还未执行到该方法,则在处理方法中,直接执行该方法.
	 * 这样.可能发生重复执行该方法的问题,所以需要在该方法中判断下是否已经关闭
	 *
	 * @param isEnqueue 是否在停止后,将其加入结束队列
	 */
	private void shutdown(boolean isEnqueue) {
		try {
			lock.lock();

			//为确保线程停止,手动将停止状态设为停止
			isInterrupt = true;

			//判断是否还在运行中
			if (!isRun())
				//已经关闭,则取消
				return;

			//如果还在运行,尝试唤醒暂停任务,并关闭
			pauseToInterrupt();

			//关闭线程池
			executor.shutdown();
			resultHandleExecutor.shutdown();
			//修改数据库任务状态为 中断 或 成功,如果失败,只会是启动失败
			this.sendTaskRecord = sendTaskRecordService.updateStatus(id,
					privateIsInterrupt ?
							SendTaskRecordStatusEnum.SUCCESS : SendTaskRecordStatusEnum.INTERRUPT);

			//修改该对象任务状态
			setStatus(SendTaskStatusEnum.CLOSE.getCode());
			log.info("[任务]任务id:{}.任务shutdown.task:{}", id, this);
			//结束时间
			this.realEndTime = System.currentTimeMillis();
			//放入结束任务队列
			if (isEnqueue)
				closeQueue.put(this);

		} catch (Exception e) {
			//..几乎不会发生
			log.error("[sendTask]任务id:{}.shutdown异常.e:{}", id, e.getMessage(), e);
		} finally {
			lock.unlock();
		}

	}


	//任务状态控制--------------------------------

	/**
	 * 外部调用-暂停任务
	 * 此处加锁,防止 多个线程暂停操作时,暂停时间不一致,也就是确保 pauseTime的线程安全
	 */
	public void pause(long pauseTime) {
		try {
			lock.lock();
			if (isInterrupt)
				throw new SmsSenderException("任务已经中断");
			if (isPause)
				throw new SmsSenderException("任务已经暂停");
			log.info("[任务]任务id:{}.准备暂停任务.", id);
			this.pauseTime = pauseTime;
			isPause = true;
			//DB任务状态
			sendTaskRecord = sendTaskRecordService.updateStatus(this.id, SendTaskRecordStatusEnum.PAUSE);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 私有- 暂停调用该方法的线程
	 */
	private void pauseThis(long pauseTime) {
		try {
			lock.lock();
			//如果任务已经被中断,直接退出,不允许再被暂停
			if (isInterrupt)
				return;
			//暂停任务线程
			isPause = true;
			//任务状态
			this.status = SendTaskStatusEnum.PAUSE.getCode();
			log.info("[任务]任务id:{}.任务暂停.暂停时间:{}", id, pauseTime);
			//暂停
			pauseCondition.await(pauseTime, TimeUnit.MILLISECONDS);
			log.info("[任务]任务id:{}.任务恢复.", id, pauseTime);
			//DB任务状态
			if (EnumUtil.equals(sendTaskRecord.getStatus(), SendTaskRecordStatusEnum.PAUSE))
				sendTaskRecord = sendTaskRecordService.updateStatusCAS(this.id, SendTaskRecordStatusEnum.PAUSE, SendTaskRecordStatusEnum.RUN);
			//取消暂停标志
			this.isPause = false;
			//任务状态
			this.status = SendTaskStatusEnum.RUN.getCode();
			//暂停时间为0
			this.pauseTime = 0L;
			//不管哪个被暂停的线程醒来后,唤醒所有线程
			pauseCondition.signalAll();
		} catch (InterruptedException e) {
			log.error("[任务]任务id:{}.暂停被中断.error:{}", id, e.getMessage());
			throw new SmsSenderException("任务暂停被中断");
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 中断任务
	 */
	public void interrupt() {
		this.isInterrupt = true;
		if (lock != null)
			pauseToInterrupt();
	}


	/**
	 * 将暂停任务中断
	 * 执行该方法前,isInterrupt应该为true
	 */
	public void pauseToInterrupt() {
		//目前,将是否中断设置为true,再将任务唤醒..也就相当于中断了
		pauseToRun();
	}

	/**
	 * 暂停任务恢复启动
	 */
	public void pauseToRun() {
		try {
			lock.lock();
			if (!isPause())
				return;
			//唤醒所有暂停任务
			pauseCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}


	/**
	 * 根据 发送任务记录,新建该对象
	 */
	public static SendTask build(SendTaskRecord sendTaskRecord) {
		return build(new SendTask(), sendTaskRecord);

	}


	/**
	 * 根据 发送任务记录,填充该对象
	 */
	private static SendTask build(SendTask sendTask, SendTaskRecord sendTaskRecord) {
		return sendTask.setSendTaskRecord(sendTaskRecord)
				.setStartTime(sendTaskRecord.getExpectStartTime().getTime())
				.setEndTime(sendTaskRecord.getExpectEndTime().getTime());
	}


	/**
	 * 判断任务是否还在运行
	 * 运行中或暂停任务都算运行中
	 */
	public boolean isRun() {
		try {
			lock.lock();
			return this.status.equals(SendTaskStatusEnum.RUN.getCode()) || isPause();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 判断任务是否处理完成
	 */
	public boolean isEnd() {
		try {
			lock.lock();
			return this.status.equals(SendTaskStatusEnum.END.getCode());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 判断任务是否暂停
	 */
	public boolean isPause() {
		try {
			lock.lock();
			return this.status.equals(SendTaskStatusEnum.PAUSE.getCode());
		} finally {
			lock.unlock();
		}
	}

	//计数器累加------------------

	/**
	 * 失败总数累加
	 */
	public int failedIncrement(int value) {
		return this.failedNum.addAndGet(value);
	}

	/**
	 * 成功数累加
	 */
	public int successIncrement(int value) {
		return this.successNum.addAndGet(value);
	}

	/**
	 * 已操作数累加
	 */
	public int usedIncrement(int value) {
		return this.usedNum.addAndGet(value);
	}


	/**
	 * 在延时队列中调用
	 * 返回 非正数时才可取出
	 *
	 * @param unit 时间单位,纳秒,延时队列调用时传入
	 * @return 还需延时时间, 纳秒
	 */
	@Override
	public long getDelay(TimeUnit unit) {
		//如果是等待中
		if (EnumUtil.equals(status, SendTaskStatusEnum.WAIT))
			//开始时间 - 当前时间  = 距离开始时间
			return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

		//如果是运行中
		if (EnumUtil.equals(status, SendTaskStatusEnum.RUN))
			//结束时间 - 当前时间 = 距离结束时间
			return unit.convert(endTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

		//如果是结束,也就是运行队列中的任务执行完毕,可直接取出...但是该延时队列无法提前取出...
		return -1000;
	}

	/**
	 * 比较方法,判断两个task,哪个优先级高.
	 * 则 还需延时时间越大 ,优先级越低
	 *
	 * @param o 比较的延时对象
	 * @return 负数 0 正数  x.compareTo(y) == -(y.compareTo(x))
	 */
	@Override
	public int compareTo(Delayed o) {
		//等待中和运行中都使用该方法
		//如果 该对象还需延时时间 > 比较对象还需延时时间, 则表示其优先级低
		return Long.compare(getDelay(TimeUnit.NANOSECONDS), o.getDelay(TimeUnit.NANOSECONDS));
	}

	//一些静态spring bean
	private static SendTaskRecordService sendTaskRecordService;
	private static ChannelService channelService;
	private static FileAccessor fileAccessor;
	private static ChannelStore channelStore;
	private static ConfigStore configStore;
	private static SmsSendRecordService smsSendRecordService;
	private static BatchService batchService;
	private static ProcessorFactory processorFactory;
	private static SmsSendRecordBatchManager smsSendRecordBatchManager;

	@Autowired
	private void init(SendTaskRecordService sendTaskRecordService, ChannelService channelService,
					  FileAccessor fileAccessor, ChannelStore channelStore, ConfigStore configStore,
					  SmsSendRecordService smsSendRecordService, BatchService batchService,
					  ProcessorFactory processorFactory,
					  SmsSendRecordBatchManager smsSendRecordBatchManager) {
		SendTask.smsSendRecordService = smsSendRecordService;
		SendTask.batchService = batchService;
		SendTask.channelService = channelService;
		SendTask.sendTaskRecordService = sendTaskRecordService;
		SendTask.fileAccessor = fileAccessor;
		SendTask.channelStore = channelStore;
		SendTask.configStore = configStore;
		SendTask.processorFactory = processorFactory;
		SendTask.smsSendRecordBatchManager = smsSendRecordBatchManager;
	}
}
