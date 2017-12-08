package com.zuma.sms.listener;

import com.zuma.sms.api.ChannelManager;
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


	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		loadChannel();
		loadConfig();
	}

	/**
	 * 加载所有短信通道到ChannelStore,并加载对应的
	 * 通道管理器
	 * See{@link com.zuma.sms.api.ChannelManager}
	 */
	private void loadChannel() {
		Map<Long, Channel> map = new ConcurrentHashMap<>();
		//查询所有通道
		List<Channel> list = channelRepository.findAll();
		//存储已经加载的通道类型
		List<Integer> types = new ArrayList<>();
		//存储已经加载的通道管理器
		ChannelManager[] channelManagers = new ChannelManager[list.size()];
		//遍历
		for (Channel channel : list) {
			map.put(channel.getId(), channel);

			//如果未加载该类型
			if(channelManagers[channel.getType()] == null)
				channelManagers[channel.getType()] = new ChannelManager(channel.getType(), channel.getMaxConcurrent());
			channel.setChannelManager(channelManagers[channel.getType()]);
		}
		channelStore.setChannels(map);
	}

	/**
	 * 加载配置属性到Config
	 */
	private void loadConfig() {
		Map<String, Map<String, String>> map = new ConcurrentHashMap<>();
		for (ConfigModuleEnum item : ConfigModuleEnum.class.getEnumConstants()) {
			List<Dict> list = dictRepository.findByModuleEquals(item.getCode());
			Map<String, String> childMap = new ConcurrentHashMap<>();
			for (Dict dict : list) {
				childMap.put(dict.getName(), dict.getValue());
			}
			map.put(item.getCode(), childMap);
		}
		configStore.setConfig(map);
	}
}
