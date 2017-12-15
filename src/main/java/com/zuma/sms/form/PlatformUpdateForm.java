package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * author:ZhengXing
 * datetime:2017/12/14 0014 15:51
 * 平台修改 表单
 */
@Data
public class PlatformUpdateForm {

	/**
	 * id
	 */
	@NotNull(message = "id为空")
	@Range(min = 1000,message = "id范围错误")
	private Long id;

	/**
	 * 平台名字
	 */
	@NotBlank(message = "名称为空")
	@Length(min = 1, max = 32, message = "名称长度不正确（1-32）")
	private String name;

	/**
	 * 回调url
	 */
	@NotBlank(message = "回调地址为空")
	@Length(min = 1, max = 32, message = "回调地址长度不正确（1-128）")
	private String callbackUrl;

	/**
	 * 状态
	 */
	@Range(max = 1, min = 1)
	private Integer status;
}
