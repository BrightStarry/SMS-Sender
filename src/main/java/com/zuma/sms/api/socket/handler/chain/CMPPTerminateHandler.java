package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.dto.api.cmpp.CMPPHeader;
import com.zuma.sms.dto.api.cmpp.CMPPTerminateAPI;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 15:01
 * 中断连接请求或响应 处理器
 */
@Component
@Slf4j
public class CMPPTerminateHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPTerminateAPI.Request))
			return nextHandler(handleObject);

		CMPPDeliverAPI.Request request = (CMPPDeliverAPI.Request) handleObject.getMsg();
		log.info("[CMPP中断连接请求]通道:{},消息:{}", handleObject.getChannel().getName(),request);

		//响应对方服务器
		handleObject.getCmppConnectionManager().sendTerminate(request.getSequenceId());

		return true;
	}
}