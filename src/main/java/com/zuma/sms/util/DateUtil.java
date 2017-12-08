package com.zuma.sms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author:Administrator
 * datetime:2017/11/10 0010 13:33
 */
@Slf4j
public class DateUtil {
    public static final String FORMAT_A = "yyyyMMddHHmmss";
    public static final String FORMAT_B = "yyyy-MM-dd HH:mm:ss.S";
    public static final String FORMAT_C = "MMddHHmmss";

    /**
     * string转日期，指定格式
     * @param dateString
     * @return
     */
    public static Date stringToDate(String dateString, String format){
        Date result = null;
        try {
            result = new SimpleDateFormat(format).parse(dateString);
        } catch (ParseException e) {
            log.error("【时间工具类】时间解析异常.error={}",e.getMessage(),e);
        }
        return  result;
    }

    /**
     * string 转日期，格式为
     */
    public static Date stringToDate(String dateString) {
        return stringToDate(dateString, FORMAT_A);
    }

    /**
     * date转string,指定格式
     */
    public static String dateToString(Date date,String format) {
        return DateFormatUtils.format(date, format);
    }
}
