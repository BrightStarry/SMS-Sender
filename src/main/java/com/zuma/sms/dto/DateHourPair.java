package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 时间小时对
 * 将开始时间-结束时间转换
 * 例如 2017-11-11 12:32:21 - 2017-11-11 15:02:34
 * 转换为一个list:
 * 2017-11-11 12:32:21 - 2017-11-11 13:00:00
 * 2017-11-11 13:00:00 - 2017-11-11 14:00:00
 * 2017-11-11 14:00:00 - 2017-11-11 15:00:00
 * 2017-11-11 15:00:00 - 2017-11-11 15:02:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateHourPair {
	private Date startTime;
	private Date endTime;


	/**
	 * 计算指定时间是否在该区间内
	 * -1: 小于startTime
	 * 0: 在范围内
	 * 1: 大于endTime
	 */
	public int compareRange(Date time) {
		//如果早于开始时间
		if(time.before(startTime))
			return -1;
		//如果晚于结束时间
		if(time.after(endTime))
			return 1;
		//否则就是在范围内
		return 0;
	}

}
