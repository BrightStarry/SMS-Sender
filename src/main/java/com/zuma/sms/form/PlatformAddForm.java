package com.zuma.sms.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/14 0014 15:51
 * 平台新增 表单
 */
@Data
public class PlatformAddForm {

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
}
