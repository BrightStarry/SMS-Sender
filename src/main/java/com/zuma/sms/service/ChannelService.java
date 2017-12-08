package com.zuma.sms.service;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 14:37
 * 通道
 */
@Component
public class ChannelService {
	@Autowired
	private ChannelRepository channelRepository;


	public Channel findOne(Long id) {
		return channelRepository.findOne(id);
	}
}
