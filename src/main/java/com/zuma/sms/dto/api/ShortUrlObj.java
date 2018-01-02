package com.zuma.sms.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2018/1/2 0002 09:35
 * 获取短链接口的参数
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlObj {
	private String types;
	private String urls;
	private String key;
	private String hash;
}
