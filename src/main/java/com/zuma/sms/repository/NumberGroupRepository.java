package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.NumberGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 号码组
 */
public interface NumberGroupRepository extends JpaRepository<NumberGroup,Long> {

	/**
	 * 模糊查询
	 */
	Page<NumberGroup> findByNameContaining(String name, Pageable pageable);

	/**
	 * 批量删除
	 */
	void deleteAllByIdIn(Long[] ids);

	/**
	 * 查询指定号码源id的所有记录
	 */
	List<NumberGroup> findAllByNumberSourceIdEquals(Long numberSourceId);
}
