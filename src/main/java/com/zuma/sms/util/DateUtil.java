package com.zuma.sms.util;

import com.zuma.sms.entity.SendTaskRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * author:Administrator
 * datetime:2017/11/10 0010 13:33
 */
@Slf4j
public class DateUtil {
    public static final String FORMAT_A = "yyyyMMddHHmmss";
    public static final String FORMAT_B = "yyyy-MM-dd HH:mm:ss.S";
    public static final String FORMAT_C = "MMddHHmmss";
    public static final String FORMAT_D = "yyyy-MM-dd HH:mm:ss";


    /**
     * 按小时划分 开始日期 - 结束日期
     * See {@link SendTaskRecord.DateHourPair}
     */
    public static List<SendTaskRecord.DateHourPair> customParseDate(Date startTime, Date endTime) {
        List<SendTaskRecord.DateHourPair> result = new ArrayList<>();
        //第一次
        //结束时间为,开始时间加一小时然后去整
        Date tmpEndDate = getIntHour(incrementHour(startTime,1));
        result.add(new SendTaskRecord.DateHourPair(startTime,tmpEndDate));

        while (endTime.after(tmpEndDate)){
            startTime = tmpEndDate;
            tmpEndDate = incrementHour(tmpEndDate, 1);
            result.add(new SendTaskRecord.DateHourPair(startTime,tmpEndDate));
        }
        result.get(result.size() - 1).setEndTime(endTime);

        return result;
    }



    /**
     * 将date取整点 ,也就是 分钟和秒钟归0
     */
    public static Date getIntHour(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 将时间加上指定小时
     */
    public static Date incrementHour(Date time, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.HOUR,hour);
        return calendar.getTime();
    }





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
