package com.zuma.sms.config.store;

import com.zuma.sms.api.ConcurrentManager;
import com.zuma.sms.api.socket.CMPPConnectionManager;
import com.zuma.sms.api.socket.IPPortPair;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.ChannelRepository;
import com.zuma.sms.util.EnumUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class ChannelStore {
	@Autowired
	private ConfigStore configStore;

	//存储所有帐号,启动时加载
	private Map<Long, Channel> channels;

	/**
	 * 获取所有通道List
	 */
	public List<Channel> getAll() {
		return  new ArrayList<>(this.channels.values());
	}

	/**
	 * 根据ChannelEnum获取通道
	 */
	public Channel get(ChannelEnum channelEnum) {
		for (Map.Entry<Long,Channel> item : channels.entrySet()) {
			if (EnumUtil.equals(item.getValue().getCacheName(),channelEnum))
				return item.getValue();
		}
		throw new SmsSenderException("系统bug.通道枚举和数据库通道不对应");
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
			//只有是否开启为true,才开启
			if (EnumUtil.equals(channel.getIsCmpp(), IntToBoolEnum.TRUE)) {

				//从配置中加载每个通道各自的CMPP ip和port
				channel.setIpPortPair(configStore.cmppIpPortMap.get(channel.getCacheName()));

				//创建cmpp连接管理器
				CMPPConnectionManager cmppConnectionManager = new CMPPConnectionManager(channel);

				//将连接管理器放入对应的CMPP短信通道
				channel.setCmppConnectionManager(cmppConnectionManager);

				if(configStore.isOpenCMPPConnection)
					//打开一个默认的CMPP连接
					openCMPPConnection(channel);
			}
		}
		setChannels(map);
		log.info("[channelStore]加载所有通道完成.");
	}

	//开启某个短信通道对应CMPP连接操作
	private void openCMPPConnection(Channel channel) {
		//TODO 宽信蹦了
		if(!channel.getCacheName().equalsIgnoreCase("kuanXinCMPP"))
			channel.getCmppConnectionManager().openConnection();
	}

}
