package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.SmsContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 短信内容/话术
 */
public interface SmsContentRepository extends JpaRepository<SmsContent,Long> {
	/**
	 * 批量删除
	 */
	void deleteAllByIdIn(Long[] ids);

	/**
	 * 模糊查询
	 */
	Page<SmsContent> findByNameContaining(String name, Pageable pageable);
}
