package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 平台
 */
public interface PlatformRepository extends JpaRepository<Platform,Long> {

	/**
	 * 模糊查询
	 */
	Page<Platform> findByNameContaining(String name, Pageable pageable);

	/**
	 * 删除 in
	 */
	void deleteAllByIdIn(Long[] ids);

}
