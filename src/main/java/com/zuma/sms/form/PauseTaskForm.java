package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * author:ZhengXing
 * datetime:2017/12/21 0021 13:47
 * 暂停任务
 */

@Data
public class PauseTaskForm {

	@NotNull(message = "id为空")
	@Range(min = 1000,message = "id范围错误")
	private Long id;

	//暂停时间为秒
	@NotNull(message = "暂停时间为空")
	@Range(min = 1,message = "暂停时间超出限制")
	private Long time;

}
