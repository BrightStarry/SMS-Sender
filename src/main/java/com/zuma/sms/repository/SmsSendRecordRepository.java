package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.SmsSendRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 短信发送记录
 */
public interface SmsSendRecordRepository extends JpaRepository<SmsSendRecord,Long> {

	/**
	 * 根据otherId查询单挑记录
	 */
	SmsSendRecord findByOtherId(String otherId);

	/**
	 * 根据任务id 分页查询 记录
	 */
	Page<SmsSendRecord> findBySendTaskId(long sendTaskId, Pageable pageable);

	/**
	 * 根据id 修改 状态
	 */
	@Modifying
	@Query("update SmsSendRecord set status = :status where id = :id")
	int updateStatusById(@Param("status") int status, @Param("id") long id);
}
