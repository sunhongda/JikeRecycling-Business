package com.buslink.busjie.driver.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2015/12/10.
 */
public class SH extends RecyclerView.ViewHolder{
    int index;
    public SH(View itemView) {
        super(itemView);
    }

    public <T> T getView(int id){
        return (T)itemView.findViewById(id);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
