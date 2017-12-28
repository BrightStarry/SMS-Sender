package com.zuma.sms.service;

import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.repository.SmsSendRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 15:37
 * 短信发送记录
 */
@Component
public class SmsSendRecordService {

	@Autowired
	private SmsSendRecordRepository smsSendRecordRepository;
	@Autowired
	private PageRequestFactory pageRequestFactory;

	/**
	 * 根据任务id,分页查询记录
	 */
	public PageVO<SmsSendRecord> findByTaskIdPage(long sendTaskId, int pageNo, int pageSize) {
		Page<SmsSendRecord> page = smsSendRecordRepository
				.findBySendTaskId(sendTaskId, pageRequestFactory.buildForCommon(pageNo, pageSize));
		return JPAPage2PageVOConverter.convert(page);
	}





	/**
	 * 修改记录
	 */
	public SmsSendRecord save(SmsSendRecord smsSendRecord) {
		return smsSendRecordRepository.save(smsSendRecord);
	}

	/**
	 * 批量保存
	 */
	public List<SmsSendRecord> save(Iterable<SmsSendRecord> records) {
		return smsSendRecordRepository.save(records);
	}

	/**
	 * 根据otherId查询单挑记录
	 */
	public SmsSendRecord findOneByOtherId(String otherId) {
		return smsSendRecordRepository.findByOtherId(otherId);
	}
}
