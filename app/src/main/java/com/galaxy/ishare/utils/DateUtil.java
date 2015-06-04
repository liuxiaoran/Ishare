package com.galaxy.ishare.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Zhan on 2015/6/2.
 */
public class DateUtil {

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }
    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date_str).getTime() ; //1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 取得当前时间戳
     * @return
     */
    public static Long getTimeStamp(){
        return System.currentTimeMillis();
    }

    /**
     * 取得当前的日期
     * @param format
     * @return
     */
    public static String getCurtime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    public static String getDate(int index) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, index);
        date = calendar.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(date);
    }

//    //  输出结果：
//    //	timeStamp=1417792627
//    //	date=2014-12-05 23:17:07
//    //	1417792627
//    public static void main(String[] args) {
//        String timeStamp = timeStamp();
//        System.out.println("timeStamp="+timeStamp);
//
//        String date = timeStamp2Date(timeStamp, "yyyy-MM-dd HH:mm:ss");
//        System.out.println("date="+date);
//
//        Long timeStamp2 = date2TimeStamp(date, "yyyy-MM-dd HH:mm:ss");
//        System.out.println(timeStamp2);
//    }
}
