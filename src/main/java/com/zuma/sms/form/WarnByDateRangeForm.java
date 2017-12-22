package com.zuma.sms.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/22 0022 12:49
 * 验证该时间范围内,任务是否过多需要预警
 */
@Data
public class WarnByDateRangeForm {


	@NotNull(message = "开始时间不能为空")
//	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;

	public void setStartTime(String startTime) {
		this.startTime = new Date(Long.parseLong(startTime));
	}

	public void setEndTime(String endTime) {
		this.endTime = new Date(Long.parseLong(endTime));
	}

	@NotNull(message = "结束时间不能为空")
	@Future(message = "结束时间小于当前时间")
//	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;

}
