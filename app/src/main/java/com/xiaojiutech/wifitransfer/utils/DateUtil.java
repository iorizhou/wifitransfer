package com.xiaojiutech.wifitransfer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String date2String(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static Date string2Date(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public static String long2DateString(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return date2String(new Date(time));
    }
}
