package com.galaxy.ishare.utils;

import java.util.Date;

/**
 * Created by wchao911 on 14-7-9.
 */
public class TimeUtils {

    public static boolean isRepeatForDay(int repeatDays, int n){
        return ((repeatDays >> n) & 1) == 1;
    }
    
    public static long nextHourMinite(int hour, int minite) {
    	long tick = 0;
    	Date date = new Date();
    	if(date.getHours() >= hour && date.getMinutes() >= minite) {
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
    
}
