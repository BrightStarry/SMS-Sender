package com.zuma.sms.repository;

import com.zuma.sms.dto.IdFieldValuePair;
import com.zuma.sms.dto.IdStatusPair;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.util.DBBatchStringUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.SessionImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/26 0026 09:16
 * 批量操作相关
 */
@Repository
public class BatchRepository {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 批量修改 发送记录某个字段,根据id
	 * 支持 string 和 int 字段
	 */
	@SneakyThrows
	public void batchUpdateSmsSendRecordFieldById(List<IdFieldValuePair> list, String field, boolean isString) {
		Connection connection = entityManager.unwrap(SessionImpl.class).connection();
		String sql = "update sms_send_record set " + field + " = ? where id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);

		if (isString) {
			for (IdFieldValuePair item : list) {
				preparedStatement.setString(1, item.getValue());
				preparedStatement.setLong(2, item.getId());
				preparedStatement.addBatch();
			}
		} else {
			for (IdFieldValuePair item : list) {
				preparedStatement.setInt(1, Integer.parseInt(field));
				preparedStatement.setLong(2, item.getId());
				preparedStatement.addBatch();
			}
		}
		preparedStatement.executeBatch();
	}


	/**
	 *
	 * 批量新增 SmsSendRecord
	 */
	@SneakyThrows
	public void batchInsertSmsSendRecord(List<SmsSendRecord> list) {
		Connection connection = entityManager.unwrap(SessionImpl.class).connection();
		StringBuilder sql = new StringBuilder("INSERT INTO sms_send_record(send_task_id,platform_send_sms_record_id,channel_id,channel_name,phones,phone_count,message) VALUES ");
		for (SmsSendRecord record : list) {
			sql.append("(");
			if (record.getSendTaskId() != null)
				sql.append(DBBatchStringUtil.wrap(record.getSendTaskId(), false, true))
					.append("null,");
			else
				sql.append("null,")
						.append(DBBatchStringUtil.wrap(record.getPlatformSendSmsRecordId(), false, true));

			sql.append(DBBatchStringUtil.wrap(record.getChannelId(), false, true))
					.append(DBBatchStringUtil.wrap(record.getChannelName(), true, true))
					.append(DBBatchStringUtil.wrap(record.getPhones(), true, true))
					.append(DBBatchStringUtil.wrap(record.getPhoneCount(), false, true))
					.append(DBBatchStringUtil.wrap(record.getMessage(), true, false))
					.append("),");
		}
		sql.deleteCharAt(sql.length() - 1);
		System.out.println(sql.toString());
		PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());
		preparedStatement.execute();
	}


	/**
	 * 批量更新状态 CAS
	 */
	@SneakyThrows
	public void batchUpdateStatus(List<IdStatusPair> list) {
		Connection connection = entityManager.unwrap(SessionImpl.class).connection();
		String sql = "update sms_send_record set status = ? where id = ? and status = ? ";
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		for (IdStatusPair item : list) {
			preparedStatement.setInt(1,item.getNewStatus());
			preparedStatement.setLong(2, item.getId());
			preparedStatement.setInt(3,item.getOldStatus());
			preparedStatement.addBatch();
		}
		preparedStatement.executeBatch();
	}

	/**
	 * 通用 新增 保存
	 */
	public void batchSave(List list) {
		for (int i = 0; i < list.size(); i++) {
			entityManager.merge(list.get(i));
			if (i % 500 == 0) {
				entityManager.flush();
				entityManager.clear();
			}
		}
		entityManager.flush();
		entityManager.clear();
	}


}
