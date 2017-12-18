package com.zuma.sms.api.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/14 0014 15:20
 * SOCKET连接的ip和端口对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IPPortPair {
	private String ip;
	private Integer port;
}