package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:16
 * 号码组修改 表单
 */
@Data
public class NumberGroupUpdateForm {

	private Long id;

	@NotBlank(message = "名称为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String name;

	private String remark;

	@Range(min = 1000,message = "id范围错误")
	@NotNull(message = "类别id为空")
	private Long typeId;
}
