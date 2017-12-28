package com.zuma.sms.util;

import com.zuma.sms.dto.DateHourPair;
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
     * 某个时间范围 是否和 另一个时间范围 重合
     * @param sTime1 1 时间范围开始时间
     * @param eTime1 1 时间范围结束时间
     * @param sTime2 2 时间范围开始时间
     * @param eTime2 2 时间范围结束时间
     *
     * @return
     */
    public static boolean isCoincide(Date sTime1,Date eTime1,
                                     Date sTime2,Date eTime2) {
        return !(eTime1.before(sTime2) || sTime1.after(eTime2));
    }

    /**
     * 某个时间是否被一个时间段包含
     */
    public static boolean isContain(Date time, Date startTime, Date endTime) {
        return time.before(startTime) || time.after(endTime);
    }


    /**
     * 按小时划分 开始日期 - 结束日期
     * See {@link DateHourPair}
     */
    public static List<DateHourPair> customParseDate(Date startTime, Date endTime) {
        List<DateHourPair> result = new ArrayList<>();
        //第一次
        //结束时间为,开始时间加一小时然后去整
        Date tmpEndDate = getIntHour(incrementHour(startTime,1));
        result.add(new DateHourPair(startTime,tmpEndDate));

        //如果最后时间比前一段时间的最后时间 大于 超过 60s,才记入
        while ((endTime.getTime() - tmpEndDate.getTime()) > 60 * 1000){
            startTime = tmpEndDate;
            tmpEndDate = incrementHour(tmpEndDate, 1);
            result.add(new DateHourPair(startTime,tmpEndDate));
        }
        result.get(result.size() - 1).setEndTime(endTime);

        return result;
    }


    public static void main(String[] args) {
        Date a = stringToDate("2017-11-11 12:03:24", FORMAT_D);
        Date b = stringToDate("2017-11-11 13:00:00", FORMAT_D);
        List<DateHourPair> list = customParseDate(a, b);
        for (DateHourPair item : list) {
            System.out.println(item);
        }
    }



    /**
     * 将date取整点 ,也就是 分钟和秒钟归0
     */
    public static Date getIntHour(Date time) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(time);
//        calendar.set(Calendar.MINUTE,0);
//        calendar.set(Calendar.SECOND, 0);
//        return calendar.getTime();

        //测试用 分钟
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * 将时间加上指定小时
     */
    public static Date incrementHour(Date time, int hour) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(time);
//        calendar.add(Calendar.HOUR,hour);
//        return calendar.getTime();

        //测试用 分钟
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        calendar.add(Calendar.MINUTE,hour);
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
