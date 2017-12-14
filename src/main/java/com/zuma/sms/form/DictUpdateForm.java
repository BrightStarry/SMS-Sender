package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 14:01
 * 字典表 修改 表单
 */
@Data
public class DictUpdateForm {
	@NotNull(message = "id为空")
	@Range(min = 1000,message = "id范围错误")
	private Long id;

	@NotBlank(message = "值为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String value;
}
