package com.zuma.sms.service;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 14:37
 * 通道
 */
@Component
public class ChannelService {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private PageRequestFactory pageRequestFactory;


	public Channel findOne(Long id) {
		return channelRepository.findOne(id);
	}


	/**
	 * 查询非cmpp 并根据某字段排序
	 * @return
	 */
	public List<Channel> findAllNotCMPPSort(String fieldName) {
		return channelRepository.findAllByIsCmpp(pageRequestFactory.buildSortASC(fieldName));
	}




}
