package com.zuma.sms.api.socket.handler.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:12
 * netty通道处理器链 管理器
 */
@Component
@Slf4j
public class ChannelHandlerChainManager {
	//第一个处理器
	private CustomChannelHandler first;
	//最后一个处理器
	private CustomChannelHandler last;

	/**
	 * 将处理器注册到管理器中
	 */
	public void registerHandler(CustomChannelHandler handler) {
		//如果为空,则它为第一个
		if(first == null)
			first = last = handler;
		else{
			//否则.将它加到最后一个的后面.并把它变成最后一个
			last.setSuccessor(handler);
			last = handler;
		}
	}

	/**
	 * 处理请求
	 */
	public boolean handler(HandleObject handleObject) throws Exception {
		return first.handler(handleObject);
	}

	/**
	 * 将处理器map注入
	 */
	public void registerHandler(Map<String, CustomChannelHandler> handlerMap) {
		for (Map.Entry<String, CustomChannelHandler> item : handlerMap.entrySet()) {
			registerHandler(item.getValue());
		}
		log.info("[ChannelHandlerChainManager]Netty通道处理器链注入完毕");
	}

}
