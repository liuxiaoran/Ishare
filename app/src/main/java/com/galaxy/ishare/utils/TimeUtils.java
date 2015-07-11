package com.galaxy.ishare.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 */
public class TimeUtils {

    public static int currentMonth;
    public static int currentHour;
    public static int currentMinute;
    public static int currentDay;
    public static boolean isRepeatForDay(int repeatDays, int n) {
        return ((repeatDays >> n) & 1) == 1;
    }

    public static long nextHourMinite(int hour, int minite) {
        long tick = 0;
        Date date = new Date();
        if (date.getHours() >= hour && date.getMinutes() >= minite) {
            date.setHours(hour);
            date.setMinutes(minite);
            date.setSeconds(0);
            tick = date.getTime() + 24 * 60 * 60 * 1000;
        } else {
            date.setHours(hour);
            date.setMinutes(minite);
            date.setSeconds(0);
            tick = date.getTime();
        }

        return tick;
    }

    public static void setCurrentTime() {

        Calendar nowCalendar = Calendar.getInstance();
        currentMonth = nowCalendar.get(Calendar.MONTH) + 1;
        currentDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        currentHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = nowCalendar.get(Calendar.MINUTE);


    }

    public static String getPresentPassTime(String publishTime) {

        String ret = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟

        setCurrentTime();
        Date publishDate = null;
        try {
            publishDate = sdf.parse(publishTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int publishMonth = publishDate.getMonth() + 1;
        int publishDay = publishDate.getDate();
        int publishHour = publishDate.getHours();
        int publishMinute = publishDate.getMinutes();
        if (publishMonth < currentMonth) {
            ret = (currentMonth - publishMonth) + "月前";
        } else if (publishMonth == currentMonth) {
            if (publishDay < currentDay) {
                ret = (currentDay - publishDay) + "天前";
            } else if (publishDay == currentDay) {
                if (publishHour < currentHour) {
                    ret = (currentHour - publishHour) + "小时前";
                } else if (publishHour == currentHour) {
                    if (publishMinute == currentMinute) {
                        ret = "刚刚";
                    } else {
                        ret = (currentMinute - publishMinute) + "分钟前";
                    }
                }
            }

        }

        return ret;
    }


}
