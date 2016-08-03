package com.angm.jikeb.util;
import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by shd
 * @TODO 万能适配器
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> schList;
//    protected LayoutInflater mInflater;

    public CommonAdapter(Context context, List<T> schList) {
//        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.schList = schList;
    }

    @Override
    public int getCount() {
        return schList.size();
    }
    @Override
    public Object getItem(int position) {
        return schList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}