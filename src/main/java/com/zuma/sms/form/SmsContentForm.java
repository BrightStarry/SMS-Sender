package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 14:10
 * 话术新增 表单
 */
@Data
public class SmsContentForm {
	private Long id;

	@NotBlank(message = "名称为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String name;
	@NotBlank(message = "内容为空")
	@Length(min = 1, max = 32, message = "内容长度不正确（1-256）")
	private String content;

	private String remark;
}
