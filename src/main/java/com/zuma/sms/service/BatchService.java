package com.zuma.sms.service;

import com.zuma.sms.dto.IdFieldValuePair;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/26 0026 09:15
 * 批量操作相关
 */
@Service
public class BatchService {

	@Autowired
	private BatchRepository batchRepository;

	/**
	 * 批量修改
	 */
	@Transactional
	public void batchUpdateSmsSendRecordFieldById(List<IdFieldValuePair> list, String field, boolean isString) {
		batchRepository.batchUpdateSmsSendRecordFieldById(list,field,isString);
	}

	/**
	 * 批量保存
	 * @param c
	 */
	@Transactional
	public void batchSave(List c) {
//		if(CollectionUtils.isEmpty(c))
//			return;
		batchRepository.batchSave(c);
	}

	/**
	 * 批量新增发送记录
	 */
	@Transactional
	public void batchInsertSmsSendRecord(List<SmsSendRecord> list) {
		if(CollectionUtils.isEmpty(list))
			return;
		batchRepository.batchInsertSmsSendRecord(list);
	}
}
