package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

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
	@NotNull(message = "类别id为空")
	private Long typeId;



	@Range(min = 1000,message = "id范围错误")
	@NotNull(message = "号码源id为空")
	private Long numberSourceId;



	@Range(min = 1,message = "号码总数不能少于1")
	private Integer numberCount;

	@Range(min = 0,max = 2,message = "分组模式错误")
	private Integer groupMode;

	//号码字符.当手动分组时需要传入
	private String phones;
}
