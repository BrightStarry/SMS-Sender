package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.dto.api.cmpp.CMPPSubmitAPI;
import com.zuma.sms.enums.error.CMPPSubmitErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:38
 * 短信上行或状态报告  处理器
 */
@Slf4j
@Component
public class CMPPDeliverHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPDeliverAPI.Request))
			return nextHandler(handleObject);

		CMPPDeliverAPI.Request request = (CMPPDeliverAPI.Request) handleObject.getMsg();
		log.info("[CMPP状态报告或短信上行]通道:{},消息:{}", request,handleObject.getChannel().getName());

		//响应对方服务器
		handleObject.getCmppConnectionManager().sendDeliverResponse(request, CMPPSubmitErrorEnum.SUCCESS);


		//TODO 根据类型不同处理
		//如果是状态推送,也就是发送短信的异步回调,
		if(request.getRegisteredDeliver() == 1){
		}else{
			//否则就是短信上行处理
		}
		return true;
	}
}