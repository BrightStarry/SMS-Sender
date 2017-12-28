package com.zuma.sms.batch;

import com.zuma.sms.api.processor.callback.SendSmsCallbackProcessor;
import com.zuma.sms.entity.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2017/12/27 0027 09:40
 * 发送短信回调处理 仓库
 */
@Component
@Slf4j
public class SendSmsCallbackProcessStorage {

	private DelayQueue<SendSmsCallbackProcessStorage.Temp> queue = new DelayQueue<>();


	//入队
	public void put(Temp temp) {
		queue.put(temp);
	}

	/**
	 * 开启线程
	 */
	public void startSendSmsCallbackProcessStorageThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				log.info("[发送短信回调处理-重试机制]启动.");
				while (true) {
					try {
						Temp temp = queue.take();
						log.info("[发送短信回调处理-重试机制]正在处理.tmep:{}", temp);
						temp.getSendSmsCallbackProcessor().process(temp);
					} catch (InterruptedException e) {
						//..不可能发生.
						log.error("[发送短信回调处理-重试机制]异常.e:{}",e.getMessage(),e);
					}
				}
			}
		}).start();
	}


	/**
	 * 临时存储回调信息,延后处理
	 * 入队5s后才能出队.5s也就是批处理每次保存的间隔
	 * @param <T>
	 */
	@Data
	public static class Temp<T> implements Delayed{
		private T response;//回调对象
		private Channel channel;//通道
		private SendSmsCallbackProcessor<T> sendSmsCallbackProcessor;//处理器
		private Long dequeueTime;//出队时间: 入队时间+x秒
		private Integer retryNum;//已重试次数

		public Temp(T response, Channel channel, SendSmsCallbackProcessor<T> sendSmsCallbackProcessor,Integer retryNum) {
			this.response = response;
			this.channel = channel;
			this.sendSmsCallbackProcessor = sendSmsCallbackProcessor;
			this.retryNum = retryNum;
			this.dequeueTime = System.currentTimeMillis() + 5000;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(dequeueTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		}

		@Override
		public int compareTo(Delayed o) {
			return Long.compare(getDelay(TimeUnit.NANOSECONDS), o.getDelay(TimeUnit.NANOSECONDS));
		}
	}

}
