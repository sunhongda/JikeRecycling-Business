package com.buslink.busjie.driver.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/5.
 */
public class MyEvent {
    private String tag;
    private String data;
    private Map<String, Object> map;

    public MyEvent(String tag) {
        this.tag = tag;
        map = new HashMap<>();
    }
    public String getTag(){
        return tag;
    }
    public void setData(String data){
        this.data=data;
    }
    public String getData(){
        return data;
    }
    public void putExtra(String key, Object value) {
        map.put(key, value);
    }
    public Object getExtra(String key) {
        return map.get(key);
    }
}
