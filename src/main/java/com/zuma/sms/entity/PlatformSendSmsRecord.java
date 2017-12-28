package com.zuma.sms.entity;

import com.zuma.sms.enums.db.PlatformSendSmsRecordStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:42
 * 平台调用发送短信记录
 */
@Entity
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Data
public class PlatformSendSmsRecord {

	/**
	 * id
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * 平台id
	 */
	private Long platformId;

	/**
	 * 发送手机号
	 */
	private String phone;

	/**
	 * 发送的消息
	 */
	private String smsMessage;

	/**
	 * 请求对象json串
	 */
	private String request;

	/**
	 * 返回结果json串
	 */
	private String result;

	/**
	 * 状态  -1:失败; 0:等待; 1:成功',
	 */
	private Integer status = PlatformSendSmsRecordStatusEnum.WAIT.getCode();

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 修改时间
	 */
	private Date updateTime;

	public PlatformSendSmsRecord(Long platformId, String phone, String smsMessage,String request) {
		this.platformId = platformId;
		this.phone = phone;
		this.smsMessage = smsMessage;
		this.request = request;
	}
}
