package com.zuma.sms.repository;

import com.zuma.sms.entity.Channel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 通道
 */
public interface ChannelRepository extends JpaRepository<Channel,Long> {

	/**
	 * 查询非CMPP通道
	 */
	List<Channel> findAllByIsCmpp(int isCmpp,Sort sort);

}
