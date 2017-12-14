package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.NumberSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 号码源
 */
public interface NumberSourceRepository extends JpaRepository<NumberSource,Long> {

	/**
	 * 根据名字模糊查询
	 */
	Page<NumberSource> findByNameContainingAndIsDeleteEquals(String name,Integer isDelete,Pageable pageable);

	/**
	 * 查询所有id in的记录
	 */
	List<NumberSource> findAllByIdIn(Long[] ids);

	/**
	 * 分页查询所有删除或未删除记录
	 */
	Page<NumberSource> findAllByIsDelete(Integer isDelete, Pageable pageable);

	/**
	 * 查询所有未删除
	 */
	List<NumberSource> findAllByIsDelete(Integer isDelete);
}
