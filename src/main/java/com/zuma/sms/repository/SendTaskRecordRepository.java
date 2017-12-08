package com.zuma.sms.repository;

import com.zuma.sms.entity.SendTaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 发送任务记录
 */
public interface SendTaskRecordRepository extends JpaRepository<SendTaskRecord,Long> {


}
