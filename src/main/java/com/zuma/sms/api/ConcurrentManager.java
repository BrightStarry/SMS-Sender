package com.zuma.sms.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2017/12/7 0007 09:45
 * 并发管理器-限制每个连接的每秒并发数
 */
@Slf4j
public class ConcurrentManager {
	//唯一名字
	private String name;
	//该连接管理并发总量-和信号量设置的总量相同
	//volatile,便于并发修改
	//当通道为cmpp时,该数量需要乘以socket连接数
	private volatile Integer maxNum;
	//信号量,表示当前该socket的并发数
	private Semaphore semaphore;
	//清理器-自身维护的定时线程池,用来每若干秒清空信号量的值
	private ScheduledExecutorService cleaner;
	//是否已经预警
	private volatile Boolean isWarn = false;

	/**
	 * 构造
	 */
	public ConcurrentManager(String name, int maxNum) {
		this.name = name + "-并发管理器";
		this.maxNum = maxNum;
		this.semaphore = new Semaphore(maxNum,true);
		setup();
	}

	/**
	 * 结束定时线程,清空该对象
	 */
	public void clean() {
		cleaner.shutdown();
		cleaner = null;
	}

	/**
	 * setup方法,开启定时线程,限制并发
	 */
	public void setup() {
		this.cleaner = Executors.newScheduledThreadPool(1);
		//该定时器方法能保证固定频率的执行任务,如果任务延期则会发生任务并发
		//1秒后开始执行,每秒1秒执行一次,释放全部信号
		//在进行发送数据操作时,将只获取信号量,不再释放信号量
		cleaner.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					//当前并发数
					int i = getConcurrentNum();
					//释放当前并发数-此处无需同步,即使有误差,也只会让线程等待而已.
					//可以适当微调信号量即可
					semaphore.release(i);

					//如果当前并发超过最大并发的3/4,预警
					if(i > maxNum/4*3){
						//该判断必须写在上面这个if里面.具体逻辑自行思考...
						//反正就是一波预警发生后,只执行一次预警操作,之后如果并发小了
						//就将预警设置为false.以达到可以重复预警的目的
						if(isWarn = true)
							return;
						log.warn("[并发管理器]{},当前并发警告.当前并发:{},最大并发:{}",name,i,maxNum);
						//...TODO 预警操作

						//表示已经预警
						isWarn = true;
					}else
						//当并发量小了,设为false,表示该次预警结束
						isWarn = false;
				} catch (Exception e) {
					//....此处只是为了防止异常后,定时线程池中断
				}
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS);

	}

	/**
	 * 获取当前并发数
	 */
	public int getConcurrentNum() {
		//总并发数 - 当前可用并发数
		return maxNum - semaphore.availablePermits();
	}

	/**
	 * 并发数累加
	 */
	public void increment() {
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			log.error("[并发管理器]{},获取信号量失败.通道:{},当前并发数:{}", name,getConcurrentNum());
		}
	}



}
