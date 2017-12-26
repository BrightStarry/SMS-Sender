package com.zuma.sms.repository;

import com.zuma.sms.entity.PlatformSendSmsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:50
 *  平台调用发送短信记录
 */
public interface PlatformSendSmsRecordRepository extends JpaRepository<PlatformSendSmsRecord,Long> {

}
