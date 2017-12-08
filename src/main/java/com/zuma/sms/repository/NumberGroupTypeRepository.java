package com.zuma.sms.repository;

import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.NumberGroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 号码组类别
 */
public interface NumberGroupTypeRepository extends JpaRepository<NumberGroupType,Long> {

	/**
	 * 模糊查询
	 */
	Page<NumberGroupType> findByNameContaining(String name, Pageable pageable);

	/**
	 * 批量删除
	 */
	void deleteAllByIdIn(Long[] ids);
}
