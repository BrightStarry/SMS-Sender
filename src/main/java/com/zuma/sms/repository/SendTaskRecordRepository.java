package com.zuma.sms.repository;

import com.zuma.sms.entity.SendTaskRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 发送任务记录
 */
public interface SendTaskRecordRepository extends JpaRepository<SendTaskRecord,Long> {


	/**
	 * 查询 in 状态的所有记录
	 */
	List<SendTaskRecord> findAllByStatusIn(Integer[] statusIn);

	/**
	 * 分页查询
	 */
	Page<SendTaskRecord> findAllByIsDelete(Integer isDelete, Pageable pageable);

	/**
	 * 查询in
	 */
	List<SendTaskRecord> findAllByIdIn(Long[] ids);

	/**
	 * 模糊查询
	 */
	Page<SendTaskRecord> findByNameContainingAndIsDeleteEquals(String name, Integer isDelete, Pageable pageable);

	/**
	 * 查询 指定话术id  的记录
	 */
	Page<SendTaskRecord> findBySmsContentIdIn(Long[] SmsContentIds,Pageable pageable);

	/**
	 * 查询 指定号码组id 记录
	 */
	Page<SendTaskRecord> findByNumberGroupIdIn(Long[] NumberGroupIds,Pageable pageable);

	/**
	 * 修改 指定号码组id 的号码组名
	 */
	@Modifying
	@Query("update SendTaskRecord set numberGroupName= :numberGroupName where numberGroupId= :numberGroupId")
	void updateNumberGroupNameByNumberGroupId(@Param("numberGroupName") String numberGroupName,
											  @Param("numberGroupId") Long numberGroupId);

	/**
	 * 修改 累加 成功数 和 失败数
	 */
	@Modifying
	@Query("update SendTaskRecord " +
			" set successNum = successNum + :successNum , failedNum = failedNum + :failedNum " +
			"where id = :taskId ")
	SendTaskRecord updateSuccessAndFailedNum(Integer successNum, Integer failedNum, Long taskId);
}
