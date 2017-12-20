package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPActiveTestAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:38
 * 链路检测响应 处理器
 */
@Slf4j
@Component
public class CMPPActiveTestResponseHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPActiveTestAPI.Response))
			return nextHandler(handleObject);

		CMPPActiveTestAPI.Response response = (CMPPActiveTestAPI.Response) handleObject.getMsg();
		log.info("[CMPP链路检测响应]通道:{},收到对方链路检测响应.",handleObject.getChannel().getName());
		return true;
	}
}