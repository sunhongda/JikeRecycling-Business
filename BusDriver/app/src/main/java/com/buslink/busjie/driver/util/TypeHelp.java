package com.buslink.busjie.driver.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.buslink.busjie.driver.R;

/**
 * Created by Administrator on 2015/12/24.
 */
public class TypeHelp {
    public static TypedArray getWeather(String time,Context context){
        if("06:00:00".compareTo(time)<=0||"18:00:00".compareTo(time)>0){
            return context.getResources().obtainTypedArray(R.array.ic_weather);
        }else{
            return context.getResources().obtainTypedArray(R.array.ic_weather_night);
        }
    }
    public static Drawable getIcWeather(String time,int img,Context context){
        if(img==33){
            return getWeather(time,context).getDrawable(32);
        }else if(img<=31&&img>=0){
            return getWeather(time,context).getDrawable(img);
        }else {
            return getWeather(time,context).getDrawable(1);
        }
    }
}
