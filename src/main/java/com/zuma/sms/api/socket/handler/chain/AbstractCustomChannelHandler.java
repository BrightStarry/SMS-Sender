package com.zuma.sms.api.socket.handler.chain;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:24
 * 抽象自定义通道处理器
 */
public abstract class AbstractCustomChannelHandler implements CustomChannelHandler {

	//下标
	protected Integer index;
	//下一个处理器
	protected CustomChannelHandler nextHandler;

	/**
	 * 调用下一个处理器,如果为空,返回false
	 */
	protected boolean nextHandler(HandleObject handleObject)  throws Exception{
		//如果为空,会返回false;并不再执行;
		//如果不为空,才会执行后半句
		return nextHandler != null && nextHandler.handler(handleObject);
	}

	/**
	 * 设置下一个处理器
	 */
	@Override
	public void setSuccessor(CustomChannelHandler handler) {
		this.nextHandler = handler;
	}
}
