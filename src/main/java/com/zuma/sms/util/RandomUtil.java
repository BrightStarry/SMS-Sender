package com.zuma.sms.util;

import org.apache.commons.text.RandomStringGenerator;

/**
 * author:ZhengXing
 * datetime:2017/12/14 0014 16:37
 * 随机数工具类
 */
public class RandomUtil {
	//可以产生0-9随机数的生成器
	private static final RandomStringGenerator generator = new RandomStringGenerator.Builder()
			.withinRange('0', '9').build();

	/**
	 * 产生指定位数的随机数
	 */
	public static String generateRandomNumber(int num) {
		return  generator.generate(num);
	}
}
