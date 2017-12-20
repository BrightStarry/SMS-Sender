package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.dto.api.cmpp.CMPPTerminateAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 15:01
 * 中断连接响应 处理器
 */
@Component
@Slf4j
public class CMPPTerminateResponseHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPTerminateAPI.Response))
			return nextHandler(handleObject);

		CMPPDeliverAPI.Request request = (CMPPDeliverAPI.Request) handleObject.getMsg();
		log.info("[CMPP中断连接响应]通道:{},收到中断连接响应", handleObject.getChannel().getName());
		return true;
	}
}