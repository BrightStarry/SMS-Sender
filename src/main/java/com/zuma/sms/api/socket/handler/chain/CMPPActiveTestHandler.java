package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPActiveTestAPI;
import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.enums.CMPPCommandIdEnum;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:38
 * 链路检测请求  处理器
 */
@Slf4j
@Component
public class CMPPActiveTestHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPActiveTestAPI.Request))
			return nextHandler(handleObject);

		CMPPActiveTestAPI.Request request = (CMPPActiveTestAPI.Request) handleObject.getMsg();
		log.info("[CMPP链路检测请求]通道:{},消息:{}", request,handleObject.getChannel().getName());

		//发送响应
		handleObject.getCmppConnectionManager().sendActiveTest(request.getSequenceId());


		return true;
	}
}