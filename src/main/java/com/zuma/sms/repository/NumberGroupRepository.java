package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.NumberGroup;
import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;

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

	/**
	 * 根据类别id查询所有记录
	 */
	List<NumberGroup> findAllByTypeIdEquals(Long typeId, Sort sort);

	/**
	 * 修改指定号码源id所有记录的名字
	 */
	@Modifying
	@Query("update NumberGroup set numberSourceName = :numberSourceName where numberSourceId= :numberSourceId")
	void updateNumberSourceNameByNumberSourceId(@Param("numberSourceName") String numberSourceName,
												@Param("numberSourceId") Long numberSourceId);

	/**
	 * 修改指定号码组类型id所有记录的名字
	 */
	@Modifying
	@Query("update NumberGroup set typeName = :numberGroupTypeName where typeId= :numberGroupTypeId")
	void updateTypeNameByTypeId(@Param("numberGroupTypeName") String numberGroupTypeName,
								@Param("numberGroupTypeId") Long numberGroupTypeId);


	/**
	 * 根据 号码组类型id IN 查询记录
	 */
	Page<NumberGroup> findByTypeIdIn(Long[] typeIds, Pageable pageable);


}
