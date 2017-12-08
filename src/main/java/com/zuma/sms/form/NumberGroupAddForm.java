package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:16
 * 号码组 表单
 */
@Data
public class NumberGroupAddForm {


	@Range(min = 1000,message = "id范围错误")
	private Long id;

	@NotBlank(message = "名称为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String name;

	private String remark;

	@Range(min = 1000,message = "id范围错误")
	@NotBlank(message = "号码组类别为空")
	private Long typeId;

	@NotBlank(message = "号码组类别名为空")
	@Length(min = 1, max = 32, message = "号码组类别名长度不正确（1-32）")
	private Long typeName;

	@Range(min = 1000,message = "id范围错误")
	@NotBlank(message = "号码源为空")
	private Long numberSourceId;

	@NotBlank(message = "号码源为空")
	@Length(min = 1, max = 32, message = "号码源名称长度不正确（1-32）")
	private String numberSourceName;


	private Short numberCount;

	@Range(min = 0,max = 2,message = "分组模式错误")
	private Integer groupMode;
}
