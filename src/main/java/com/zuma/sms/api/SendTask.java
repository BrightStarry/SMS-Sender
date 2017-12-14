package com.zuma.sms.api;

import com.zuma.sms.api.send.SendSmsProcessor;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.enums.SendTaskStatusEnum;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.service.ChannelService;
import com.zuma.sms.service.SendTaskRecordService;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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
public class SendTask implements Delayed {
	//数据库中的发送任务记录
	private SendTaskRecord sendTaskRecord;
	//号码组并发队列
	private ConcurrentLinkedQueue<String> phoneQueue;
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
	//任务状态, 等待 运行中 结束 处理完成
	private Integer status = SendTaskStatusEnum.WAIT.getCode();
	//任务结果-code存储 发送任务记录数据库表的status
	private ResultDTO result;


	//结束任务队列-用来结束后将this放入
	private BlockingQueue<SendTask> closeQueue;
	//锁-状态属性
	private ReentrantLock lock;
	//闭锁-等待所有线程执行完毕
	private CountDownLatch latch;
	//是否中断 - 操作时不关心其当前值,无需加锁,直接用volatile
	private volatile Boolean isInterrupt = false;

	//数量
	//成功数-异步响应成功
	private AtomicInteger successNum;
	//失败数-总失败数
	private AtomicInteger failedNum;

	//未响应数
	private AtomicInteger unResponse;
	//已操作数
	private AtomicInteger usedNum;

	//通道实体类
	private Channel channel;
	//短信发送器
	private SendSmsProcessor sendSmsProcessor;

	/**
	 * 重写toString()
	 * @return
	 */
	@Override
	public String toString() {
		return "SendTask:{sendTaskRecord:" + sendTaskRecord + ",startTime:" + startTime + ",endTime:" + endTime
				+ ",realStartTime:" + realStartTime + ",realEndTime:" + realEndTime
				+ ",status:" + EnumUtil.getByCode(status, SendTaskStatusEnum.class) + ",isInterrupt:" + isInterrupt
				+ ",successNum:" + successNum + ",failedNum:" + failedNum + ",unResponse:" + unResponse
				+ ",usedNum:" + usedNum + ",channel:" + (channel == null ? "" : channel.getName()) + "}";
	}

	/**
	 * 任务结束处理
	 * 再次判断是否处理过,
	 */
	public void endHandle() {
		try{
			log.info("[任务]endHandle()-开始处理任务.task:{}",this);
			lock.lock();
			//再次判断是否处理过
			if(isEnd()){
				log.info("[任务]endHandle()-该任务已经处理.task:{}",this);
				return;
			}
			//判断是否仍然在运行,直接关闭
			if (isRun()){
				//将状态改为中断
				this.isInterrupt = true;
				shutdown(false);
			}
			//此处不做查询,任务一旦开始后,不允许修改任务记录
			sendTaskRecord
					.setRealStartTime(new Date(this.realStartTime))
					.setRealEndTime(new Date(this.realEndTime))
					.setTotalTime((int)(this.realEndTime - this.realStartTime))
					.setSuccessNum(this.successNum.get())
					.setFailedNum(this.failedNum.get())
					.setUnResponse(this.unResponse.get())
					.setUsedNum(this.usedNum.get())
					.setErrorInfo(result == null ? "" : CodeUtil.objectToJsonString(result));
//					.setStatus(Integer.parseInt(result.getCode()));
			log.info("[任务]endHandle()-处理完成.task:{},sendTaskRecord:{}",this,sendTaskRecord);
			//保存
			this.sendTaskRecord = sendTaskRecordService.updateOne(sendTaskRecord);
		}finally {
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
			//判断是否还在运行中
			if(!isRun())
				//已经关闭,则取消
				return;
			//关闭线程池
			executor.shutdown();
			executor = null;
			//修改数据库任务状态为 中断 或 成功,如果失败,只会是启动失败
			this.sendTaskRecord = sendTaskRecordService.updateStatus(sendTaskRecord.getId(),
					this.isInterrupt ?
							SendTaskRecordStatusEnum.INTERRUPT : SendTaskRecordStatusEnum.SUCCESS);
			//为确保线程停止,手动将停止状态设为停止
			this.isInterrupt = true;
			//修改该对象任务状态
			setStatus(SendTaskStatusEnum.CLOSE.getCode());
			log.info("[任务]任务shutdown.task:{}",this);
			//结束时间
			this.realEndTime = System.currentTimeMillis();
			//放入结束任务队列
			if(isEnqueue)
				closeQueue.put(this);

		} catch (Exception e){
			//..不可能发生
			log.error("[sendTask]shutdown异常.e:{}",e.getMessage(),e);
		}finally {
			lock.unlock();
		}

	}


	/**
	 * 任务中断
	 */
	public void interrupt() {
		this.isInterrupt = true;
	}

	/**
	 * 任务开始.异步
	 */
	public void run(){
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					run1();
				} catch (Exception e) {
					log.error("[任务]任务运行期间异常....几乎不会发生的.e:{}",e.getMessage(),e);
					//...TODO 处理任务发生的异常.虽然几乎不可能发生
				}
			}
		});
	}

	/**
	 * 任务开始
	 */
	private void run1() throws Exception {
		//单个线程操作
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					//最大群发数
					int phoneNum = channel.getMaxGroupNumber();
					//号码
					StringBuilder phones;
					//消息
					String message = sendTaskRecord.getContent();
					//任务id
					Long taskId = sendTaskRecord.getId();

					//循环到队列为空
					threadEnd:
					while(true){
						phones = new StringBuilder();
						//该次方法的手机号数量
						int thisSendPhoneNum = 0;
						try {
							//检测到中断信号,停止
							if(isInterrupt)
								break;
							//循环,获取 和最大群发数相同的手机号
							for (int i = 0; i < phoneNum; i++) {
								//取出下一个手机号
								String item = phoneQueue.poll();
								//如果为空,表示队列中没有元素了
								if(item == null)
									break threadEnd;

								//该次发送数量
								thisSendPhoneNum++;
								log.info("[任务]任务进行中.");
								//此处逗号加前面,方便下面的截取方法,直接截取索引0
								phones.append(",").append(item);
							}
							TimeUnit.SECONDS.sleep(1);
							//截取逗号
							phones.deleteCharAt(0);
							//总操作数+
							usedNum.addAndGet(thisSendPhoneNum);
							//发送
							ErrorData errorData = new ErrorData();
							errorData.setCount(1);
							errorData.setMessages("xxx");
							errorData.setPhones(phones.toString());
							ResultDTO<ErrorData> result = ResultDTO.error(ErrorEnum.OTHER_ERROR,errorData);
							channel.getChannelManager().increment();
	//						ResultDTO<ErrorData> result = sendSmsProcessor.process(channel, phones.toString(), message, taskId);

							//处理返回对象
							//如果成功
							if(ResultDTO.isSuccess(result)){
								//成功总数+
								successIncrement(thisSendPhoneNum);
								continue;
							}
							//如果失败
							//取出失败手机号
							String errorPhone = result.getData().getPhones();
							//取出失败数
							Integer errorCount = result.getData().getCount();
							//失败总数增加
							failedIncrement(errorCount);
							//写入失败日志--TODO 可能需要同步
							fileAccessor.writeBySendTaskId(taskId,errorPhone);
						} catch (Exception e) {
							//失败,则发送不可能为成功,累加失败总数(如果进行本次发送数量不为0)
							if(thisSendPhoneNum != 0)
								failedIncrement(thisSendPhoneNum);
							log.error("[sendTask]sendTaskId:{},单次发送异常.e:{}",e.getMessage(),e);
						}
					}
				} finally {
					//通知主线程,该线程运行完毕
					latch.countDown();
				}
			}
		};

		//启动线程
		for (int i = 0; i < sendTaskRecord.getThreadCount(); i++) {
			this.executor.execute(runnable);
		}
		//自身等待
		latch.await();
		log.info("[任务]任务门闩被释放.准备结束.task:{}",this);
		//结束
		shutdown(true);
	}

	/**
	 * 任务准备
	 */
	public void setup(BlockingQueue<SendTask> closeQueue) {
		//修改记录状态为运行中
		this.sendTaskRecord = sendTaskRecordService.updateStatus(sendTaskRecord.getId(), SendTaskRecordStatusEnum.RUN);
		//开始时间
		this.realStartTime = System.currentTimeMillis();
		//修改当前任务状态为 运行中
		this.status = SendTaskStatusEnum.RUN.getCode();
		//获取到号码集合的并发队列
		this.phoneQueue = fileAccessor.readBySendTask(sendTaskRecord.getNumberGroupId());
		//构造线程池
		this.executor = Executors.newFixedThreadPool(sendTaskRecord.getThreadCount());
		//构造锁
		lock = new ReentrantLock();
		latch = new CountDownLatch(sendTaskRecord.getThreadCount());
		//其他
		this.successNum = new AtomicInteger();
		this.failedNum = new AtomicInteger();
		this.unResponse = new AtomicInteger();
		this.usedNum = new AtomicInteger();
		//查询通道
		this.channel = channelStore.get(sendTaskRecord.getChannelId());
		//匹配短信发送器 TODO 暂时注释
//		this.sendSmsProcessor = SendSmsProcessorFactory.build(this.channel);
		//放入 结束任务队列
		this.closeQueue = closeQueue;
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
		return Long.compare(o.getDelay(TimeUnit.NANOSECONDS), getDelay(TimeUnit.NANOSECONDS));
	}

	/**
	 * 判断任务是否还在运行
	 */
	public boolean isRun() {
		try {
			lock.lock();
			return this.status.equals(SendTaskStatusEnum.RUN.getCode());
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

	//计数器累加

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

	//一些静态spring bean
	private static SendTaskRecordService sendTaskRecordService;
	private static ChannelService channelService;
	private static FileAccessor fileAccessor;
	private static ChannelStore channelStore;

	@Autowired
	private void init(SendTaskRecordService sendTaskRecordService,ChannelService channelService,FileAccessor fileAccessor,ChannelStore channelStore) {
		SendTask.channelService = channelService;
		SendTask.sendTaskRecordService = sendTaskRecordService;
		SendTask.fileAccessor = fileAccessor;
		SendTask.channelStore = channelStore;
	}

	//若干构造方法

}
