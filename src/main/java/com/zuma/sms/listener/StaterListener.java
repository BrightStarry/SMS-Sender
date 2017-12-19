package com.zuma.sms.listener;

import com.zuma.sms.api.SendTaskManager;
import com.zuma.sms.api.socket.handler.chain.ChannelHandlerChainManager;
import com.zuma.sms.api.socket.handler.chain.CustomChannelHandler;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.repository.ChannelRepository;
import com.zuma.sms.repository.DictRepository;
import com.zuma.sms.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 14:33
 * 容器启动器
 */
@Component
@Slf4j
public class StaterListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ChannelStore channelStore;

	@Autowired
	private ConfigStore configStore;

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private DictRepository dictRepository;

	@Autowired
	private SendTaskManager sendTaskManager;

	@Autowired
	private Map<String,CustomChannelHandler> customChannelHandlerMap;

	@Autowired
	private ChannelHandlerChainManager channelHandlerChainManager;

	@Autowired
	private HttpClientUtil httpClientUtil;

	/**
	 * 容器加载完成操作
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		//加载所有通道
		channelStore.loadChannel(channelRepository);
		//加载所有配置属性
		configStore.loadConfig(dictRepository);
		//加载netty处理器
		channelHandlerChainManager.registerHandler(customChannelHandlerMap);
		//运行发送任务管理器
		sendTaskManager.run();


//		HttpGet get = new HttpGet("https://tm.ringbox.cn/");
//		try {
//			CloseableHttpResponse execute = httpClientUtil.getHttpClient().execute(get);
//			String s = EntityUtils.toString(execute.getEntity());
//			System.out.println(s);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}




}
