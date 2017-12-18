package com.zuma.sms.config.store;

import com.zuma.sms.api.ConcurrentManager;
import com.zuma.sms.api.socket.CMPPConnectionManager;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.repository.ChannelRepository;
import com.zuma.sms.util.EnumUtil;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 14:30
 * 通道存储
 */
@Component
@Setter
public class ChannelStore {
	//存储所有帐号,启动时加载
	private Map<Long, Channel> channels;

	/**
	 * 获取所有通道List
	 */
	public List<Channel> getAll() {
		return  new ArrayList<>(this.channels.values());
	}

	//根据id获取帐号
	public Channel get(Long id){
		return channels.get(id);
	}

	/**
	 * 加载所有短信通道到ChannelStore,并加载对应的
	 * 通道管理器
	 * See{@link ConcurrentManager}
	 */
	public  void loadChannel(ChannelRepository channelRepository) {
		Map<Long, Channel> map = new ConcurrentHashMap<>();
		//查询所有通道
		List<Channel> list = channelRepository.findAll();
		//存储已经加载的并发管理器
		ConcurrentManager[] concurrentManagers = new ConcurrentManager[list.size()];
		//遍历
		for (Channel channel : list) {
			map.put(channel.getId(), channel);

			//如果未加载该类型,则加载新的并发管理器
			if(concurrentManagers[channel.getType()] == null)
				concurrentManagers[channel.getType()] = new ConcurrentManager(channel.getName(), channel.getMaxConcurrent());
			channel.setConcurrentManager(concurrentManagers[channel.getType()]);

			//如果是cmpp类型,加载 CMPP连接管理器
			if (EnumUtil.equals(channel.getIsCMPP(), IntToBoolEnum.TRUE)) {
				CMPPConnectionManager cmppConnectionManager = new CMPPConnectionManager(channel);
				cmppConnectionManager.openConnection();
				channel.setCmppConnectionManager(cmppConnectionManager);
			}
		}
		setChannels(map);
	}

}
