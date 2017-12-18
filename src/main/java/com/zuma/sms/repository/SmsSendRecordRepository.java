package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.SmsSendRecord;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
