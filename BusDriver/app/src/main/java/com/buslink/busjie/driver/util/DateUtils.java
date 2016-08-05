package com.buslink.busjie.driver.util;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2015/8/29.
 */
public class DateUtils {
    public static String getSimpleFormatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(time);
    }
    public static String getTyyyyMMdd(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }
    public static String getYer(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(time);
    }
    public static String getMonthDay(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(time);
    }
    public static String getYYYYMMdd(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(time);
    }
    public static String getHh(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH时");
        return format.format(time);
    }
    public static String getMm(long time){
        SimpleDateFormat format = new SimpleDateFormat("mm分");
        return format.format(time);
    }

    public static String getHHmm(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH时mm分");
        return format.format(time);
    }
    public static String getHHmmm(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(time);
    }
}
