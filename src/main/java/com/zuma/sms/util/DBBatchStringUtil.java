package com.zuma.sms.util;

import org.apache.commons.lang3.StringUtils;

/**
 * author:ZhengXing
 * datetime:2017/12/26 0026 16:54
 * 数据库批处理 字符串工具类
 */
public class DBBatchStringUtil {


	/**
	 * 字符串处理
	 * "1"  -->  ""1"," 或 "1," 等
	 * @param field
	 * @param isQuotes 是否加引号
	 * @param isComma 是否加逗号
	 * @return
	 */
	public static String wrap(String field, boolean isQuotes, boolean isComma) {
		if(isQuotes)
			field = StringUtils.wrap(field,"\"");
		if(isComma)
			field += ",";
		return field;
	}

	public static String wrap(Object field, boolean isQuotes, boolean isComma) {
		return wrap(String.valueOf(field), isQuotes, isComma);
	}
}
