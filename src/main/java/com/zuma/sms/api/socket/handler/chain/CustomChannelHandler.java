package com.zuma.sms.api.socket.handler.chain;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:13
 * 自定义通道处理器 接口
 */

public interface CustomChannelHandler {

	//处理请求
	boolean handler(HandleObject handleObject) throws Exception;

	//设置下一个处理器
	void setSuccessor(CustomChannelHandler handler);
}
