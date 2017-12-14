package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 09:57
 * 发送任务 修改 表单
 */
@Data
public class SendTaskRecordUpdateForm {
	@NotNull(message = "任务id为空")
	@Range(min = 1000,message = "任务id范围错误")
	private Long id;

	private String remark;


	@NotNull(message = "通道id为空")
	@Range(min = 1000,message = "通道id范围错误")
	private Long channelId;

	@NotNull(message = "号码组id为空")
	@Range(min = 1000,message = "号码组id范围错误")
	private Long numberGroupId;

	@NotNull(message = "话术id为空")
	@Range(min = 1000,message = "话术id范围错误")
	private Long smsContentId;

	@NotNull(message = "线程数为空")
	@Range(min = 1,max = 500,message = "线程数超出限制(1-500)")
	private Integer threadCount;


	/**
	 * 开始时间.需要小于结束时间;
	 * 如果小于当前时间,则表示马上开始;
	 * 此处不做验证
	 */
	@NotNull(message = "开始时间不能为空")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectStartTime;

	@NotNull(message = "结束时间不能为空")
	@Future(message = "结束时间小于当前时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectEndTime;



}
