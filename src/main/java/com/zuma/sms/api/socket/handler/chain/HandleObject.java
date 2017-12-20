package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.api.socket.CMPPConnection;
import com.zuma.sms.api.socket.CMPPConnectionManager;
import com.zuma.sms.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:22
 * 被处理的对象,交由处理链处理
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandleObject {
	//返回的消息
	private Object msg;

	//连接管理器
	private CMPPConnectionManager cmppConnectionManager;

	//通道
	private Channel channel;

	//当前连接
	private CMPPConnection cmppConnection;
}
