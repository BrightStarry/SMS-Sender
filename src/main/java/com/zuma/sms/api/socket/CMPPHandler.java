package com.zuma.sms.api.socket;

import com.zuma.sms.api.socket.handler.chain.ChannelHandlerChainManager;
import com.zuma.sms.api.socket.handler.chain.HandleObject;
import com.zuma.sms.dto.api.cmpp.CMPPConnectAPI;
import com.zuma.sms.entity.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 13:49
 * netty处理器
 */
@Component
@Slf4j
@ChannelHandler.Sharable
@NoArgsConstructor
public class CMPPHandler extends ChannelHandlerAdapter {
	//spring bean init
	private static ChannelHandlerChainManager handlerManager;
	@Autowired
	public void init(ChannelHandlerChainManager handlerManager) {
		CMPPHandler.handlerManager = handlerManager;
	}


	//连接管理器
	private CMPPConnectionManager connectionManager;
	//该处理器对应的连接
	private CMPPConnection cmppConnection;
	//对应的通道
	private Channel channel;

	public CMPPHandler(CMPPConnectionManager connectionManager, CMPPConnection cmppConnection, Channel channel) {
		this.connectionManager = connectionManager;
		this.cmppConnection = cmppConnection;
		this.channel = channel;
	}

	/**
	 * 通道激活
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("[通道处理器]通道:{},通道激活.",channel.getName());

		//将通道上下文 保存到 连接中
		cmppConnection.setChannelHandlerContext(ctx);
		//发送连接请求
		channel.getCmppConnectionManager().sendConnectRequest(ctx.channel(),CMPPConnectAPI.Request.build(channel));
	}

	/**
	 * 读取到消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//调用处理链处理
		if (!handlerManager.handler(new HandleObject(msg, connectionManager, channel,cmppConnection)))
			log.error("[CMPP处理器]收到消息,未被任何处理器处理");
	}

	/**
	 * 用户自定义事件
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//如果不是 空闲状态事件,执行父类方法
		if (!(evt instanceof IdleStateEvent))
			super.userEventTriggered(ctx,evt);


		IdleStateEvent event = (IdleStateEvent) evt;
		//如果不是读取空闲超时事件
		if(event.state() != IdleState.READER_IDLE)
			return;

		//发送链路检测
		connectionManager.sendActiveTest(cmppConnection.getChannelHandlerContext().channel(),null);

		//TODO 开启线程等待链路检测结果
	}

	/**
	 * 异常处理
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("[CMPP处理器]通道:{}发生异常.error={}", channel.getName(),cause.getMessage());
	}
}
