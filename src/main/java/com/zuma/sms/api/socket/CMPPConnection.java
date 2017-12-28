package com.zuma.sms.api.socket;

import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.exception.SmsSenderException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * author:ZhengXing
 * datetime:2017/12/14 0014 14:38
 * socket连接器
 */
@Slf4j
@Component
@Data
@NoArgsConstructor
public class CMPPConnection {
	//spring bean init ...
	private static ConfigStore configStore;
	private static CMPPDecoder cmppDecoder;
	@Autowired
	public void init(ConfigStore configStore,CMPPDecoder cmppDecoder) {
		CMPPConnection.configStore = configStore;
		CMPPConnection.cmppDecoder = cmppDecoder;
	}

	//一些必要属性...
	//当前连接id
	private Integer id;
	//当前连接所属通道
	private Channel channel;
	//当前启动的线程池
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	//当前连接处理器
	private ChannelHandler channelHandler;
	//当前连接成功的通道持有上下文
	private ChannelHandlerContext channelHandlerContext;
	//锁
	private ReentrantLock lock = new ReentrantLock(true);
	//启动锁条件
	private Condition runCondition = lock.newCondition();
	//是否启动
	private volatile Boolean isRun = false;
	//是否中断
	private volatile Boolean isInterrupt = false;


	public CMPPConnection (Channel channel,Integer id,CMPPConnectionManager connectionManager) {
		this.channel = channel;
		this.id = id;
		this.channelHandler = new CMPPHandler(connectionManager, this, channel);
		log.info("[CMPP连接器]通道:{},id:{},连接器被创建,准备启动连接.",channel.getName(),id);
		start();
	}

	/**
	 * 获取 通道上下文 表示需要发送了
	 * @return
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		//如果停止
		if(isInterrupt)
			throw new SmsSenderException("CMPP连接停止.");
		//启动,直接返回
		if(isRun)
			return channelHandlerContext;
		//还未启动
		try {
			lock.lock();
			//等待3s,
			runCondition.await(5,TimeUnit.SECONDS);
			//如果还没启动
			if(isRun)
				throw new SmsSenderException("CMPP连接未启动成功.");
			return channelHandlerContext;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
		throw new SmsSenderException("CMPP连接未启动成功.");
	}

	/**
	 * 设为启动
	 * 修改标志,然后唤醒所有等待线程
	 */
	public void setRun(){
		try {
			lock.lock();
			this.isRun = true;
			runCondition.signalAll();
		} finally {

		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		//如果已经关闭.
		if(isInterrupt)
			return;

		log.info("[CMPP连接器]通道:{},id:{},中断方法被调用,开始中断连接.",channel.getName(),id);
		this.isInterrupt = true;
		executor.shutdown();
		if(channelHandlerContext != null){
			channelHandlerContext.executor().shutdownGracefully();
			channelHandlerContext.close();
		}

	}


	/**
	 * 异步启动
	 */
	private void start() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				while(!isInterrupt){
					start1();
				}
				log.info("[CMPP连接器]通道:{},id:{},连接完全关闭,线程中断.",channel.getName(),id);
			}
		});
	}

	/**
	 * 启动
	 */
	private void start1() {
		try {
			//创建线程组 - 手动设置线程数,默认为cpu核心数2倍
			EventLoopGroup eventLoopGroup =  new NioEventLoopGroup(4);
			//创建引导程序
			Bootstrap bootstrap = new Bootstrap();
			//保持长连接
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			//将线程加入bootstrap
			bootstrap.group(eventLoopGroup)
					//使用指定通道类
					.channel(NioSocketChannel.class)
					//设置日志
					.handler(new LoggingHandler(LogLevel.INFO))
					//重写通道初始化方法
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline()
									/**
									 * 心跳检测：超过xs未触发触发读取事件，则触发userEventTriggered()事件
									 */
									.addLast("idleState handler",new IdleStateHandler(configStore.cmppActiveTestSecond,0,0, TimeUnit.SECONDS))
									/**
									 * 长度解码器，防止粘包拆包
									 * @param maxFrameLength 解码时，处理每个帧数据的最大长度
									 * @param lengthFieldOffset 该帧数据中，存放该帧数据的长度的数据的起始位置
									 * @param lengthFieldLength 记录该帧数据长度的字段本身的长度
									 * @param lengthAdjustment 修改帧数据长度字段中定义的值，可以为负数
									 * @param initialBytesToStrip 解析的时候需要跳过的字节数
									 * @param failFast 为true，当frame长度超过maxFrameLength时立即报TooLongFrameException异常，为false，读取完整个帧再报异常
									 */
//                                    .addLast("length decoder",
//                                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
									/**
									 * 消息解码器，将收到的消息进行转换
									 */
									.addLast("logMessage decoder", cmppDecoder)
									/**
									 * 消息编码器，将发送的消息进行转换,使用编码器后会有问题..
									 */
//                                    .addLast("logMessage encoder",zhuWangEncoder )
									/**
									 * 长度编码器，防止粘包拆包
									 * 第一个参数为 长度字段长度
									 * 第二个为参数为 长度是否包含长度字段长度
									 */
									.addLast("length encoder",new LengthFieldPrepender(4,true))
									/**
									 * 超过s秒未触发读取事件，关闭
									 */
//                                    .addLast(new ReadTimeoutHandler(LogClientConfig.TIMEOUT_SECONDS))
									/**
									 * 自定义处理器
									 */
									.addLast(channelHandler);
						}
					});
			IPPortPair ipPortPair = channel.getIpPortPair();
			//链接到服务端
			ChannelFuture channelFuture = bootstrap.connect(ipPortPair.getIp(),ipPortPair.getPort()).sync();
			log.info("[CMPP连接器]通道:{},id:{},socket连接启动成功.",channel.getName(),id);
			//关闭前阻塞
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e){
			log.error("[CMPP连接器]通道:{},id:{},socket连接启动失败.error:{}",channel.getName(),id,e.getMessage(),e);
		}
		log.info("[CMPP连接器]通道:{},id:{},socket连接关闭.",channel.getName(),id);
	}
}
