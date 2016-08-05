package com.buslink.busjie.driver.util;

import android.view.View;

/**
 * Created by Administrator on 2015/10/14.
 */
public class ViewHolder {
    View view;
    int index;

    public ViewHolder(View v){
        view=v;
    }

    public void setTag(int... ids){
        if (view == null) {
            return;
        }
        for (int id : ids) {
            view.setTag(id, view.findViewById(id));
        }
    }
    public <T> T getTag(int id){
        return (T)view.getTag(id);
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
