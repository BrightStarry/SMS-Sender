package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/26 0026 16:51
 * id 和 字段名 和 值
 * 批量修改时使用
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdFieldValuePair {

	private Long id;
	private String field;
	private String value;
	private boolean isString;//是否是string,也表示是否加引号
}
