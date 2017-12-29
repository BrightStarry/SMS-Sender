package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/29 0029 10:40
 * id 状态 对
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdStatusPair {
	private Long id;
	private Integer oldStatus;
	private Integer newStatus;
}
