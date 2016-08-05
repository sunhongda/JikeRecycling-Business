package com.buslink.busjie.driver.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/9/17.
 */
public class SimpleHolder extends RecyclerView.ViewHolder {
    private View v;
    private int index;

    public SimpleHolder(View itemView) {
        super(itemView);
        v = itemView;
    }

    public void setTag(int... ids) {
        if (v == null) {
            return;
        }
        for (int id : ids) {
            v.setTag(id, v.findViewById(id));
        }
    }

    public View getView(int id) {
        return (View) v.getTag(id);
    }

    public TextView getTextView(int id) {
        return (TextView) v.getTag(id);
    }
    public Button getButton(int id){
        return (Button) v.getTag(id);
    }

    public LinearLayout getLinerLayout(int id){
        return (LinearLayout)v.getTag(id);
    }

    public ImageView getImageView(int id) {
        return (ImageView) v.getTag(id);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public <T> T getTag(int id){
        return (T)v.getTag(id);
    }
}
