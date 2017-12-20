package com.zuma.sms.api.socket.handler.chain;

import com.zuma.sms.dto.api.cmpp.CMPPConnectAPI;
import com.zuma.sms.enums.error.CMPPConnectErrorEnum;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 14:31
 * 连接响应      处理器
 */
@Slf4j
@Component
public class CMPPConnectionHandler extends AbstractCustomChannelHandler{
	@Override
	public boolean handler(HandleObject handleObject)  throws Exception{
		if(!(handleObject.getMsg() instanceof CMPPConnectAPI.Response))
			return nextHandler(handleObject);

		CMPPConnectAPI.Response response = (CMPPConnectAPI.Response) handleObject.getMsg();
		log.info("[CMPP连接响应]通道:{},消息:{}", handleObject.getChannel().getName(),response);
		//如果成功
		if(CMPPConnectErrorEnum.SUCCESS.getCode().equals(response.getStatus())){
			handleObject.getCmppConnection().setRun();
		}

		return true;
	}
}
