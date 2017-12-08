package com.zuma.sms.config.store;

import com.zuma.sms.entity.Channel;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

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

	//根据id获取帐号
	public Channel get(Long id){
		return channels.get(id);
	}

}
