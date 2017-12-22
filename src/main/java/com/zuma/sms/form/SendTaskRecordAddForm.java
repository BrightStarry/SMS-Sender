package com.zuma.sms.form;

import com.zuma.sms.enums.db.IntToBoolEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 09:57
 * 发送任务 新增 表单
 */
@Data
public class SendTaskRecordAddForm {


	@NotBlank(message = "任务名不能为空")
	@Length(min = 1, max = 32, message = "任务名长度不正确（1-32）")
	private String name;

	private String remark;

	private Long userId;

	@NotNull(message = "通道id为空")
	@Range(min = 1000,message = "通道id范围错误")
	private Long channelId;

	@NotNull(message = "号码组id为空")
	@Range(min = 1000,message = "号码组id范围错误")
	private Long numberGroupId;

	@NotNull(message = "话术id为空")
	@NotEmpty(message = "话术id为空")
	private Long[] smsContentId;

	@NotNull(message = "线程数为空")
	@Range(min = 1,max = 500,message = "线程数超出限制(1-500)")
	private Integer threadCount;


	private Integer isShard = IntToBoolEnum.FALSE.getCode();


	/**
	 * 开始时间.需要小于结束时间;
	 * 如果小于当前时间,则表示马上开始;
	 * 此处不做验证
	 */
	@NotNull(message = "开始时间不能为空")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectStartTime = new Date();

	@NotNull(message = "结束时间不能为空")
	@Future(message = "结束时间小于当前时间")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date expectEndTime;



}
