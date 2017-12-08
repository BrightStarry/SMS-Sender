package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 11:28
 * 号码源表单检验类
 */
@Data
public class NumberSourceForm {
	@NotNull(message = "id为空")
	@Range(min = 1000,message = "id范围错误")
	private Long id;

	@NotBlank(message = "名称为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String name;

	@NotBlank(message = "备注为空")
	@Length(min = 1, max = 128, message = "备注长度不正确（1-128）")
	private String remark;

}
