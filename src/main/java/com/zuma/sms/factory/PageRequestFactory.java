package com.zuma.sms.factory;

import com.zuma.sms.config.store.ConfigStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;


/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:05
 * {@link org.springframework.data.domain.PageRequest} 工厂
 * 主要为了复用通用构建方法
 */
@Component
public class PageRequestFactory {

	@Autowired
	private ConfigStore configStore;

	/**
	 * 构建通用的分页
	 */
	public Pageable buildForCommon(int pageNo, Integer pageSize) {
		return new PageRequest(--pageNo, pageSize == null ? configStore.getForCommonInt("PAGE_SIZE") : pageSize, buildSort());
	}

	/**
	 * 构建只查询一条记录的分页
	 */
	public Pageable buildForLimitOne() {
		return new PageRequest(0, 1);
	}

	/**
	 * 构建模糊查询用的固定数量的分页
	 * @return
	 */
	public Pageable buildForLikeSearch() {
		return new PageRequest(0, configStore.likeSearchMaxNum, buildSort());
	}

	/**
	 * 构建默认 Sort
	 * @return
	 */
	public Sort buildSort() {
		return new Sort(Sort.Direction.DESC, "id");
	}


}
