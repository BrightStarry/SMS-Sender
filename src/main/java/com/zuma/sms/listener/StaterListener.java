package com.zuma.sms.listener;

import com.zuma.sms.api.ChannelManager;
import com.zuma.sms.api.SendTaskManager;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Dict;
import com.zuma.sms.enums.system.ConfigModuleEnum;
import com.zuma.sms.repository.ChannelRepository;
import com.zuma.sms.repository.DictRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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


	/**
	 * 容器加载完成操作
	 * @param contextRefreshedEvent
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		//加载所有通道
		channelStore.loadChannel(channelRepository);
		//加载所有配置属性
		configStore.loadConfig(dictRepository);
		//运行发送任务管理器
		sendTaskManager.run();
	}




}
