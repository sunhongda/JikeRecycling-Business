package com.buslink.busjie.driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/22.
 */
public class WithdrawListAdapter extends BaseAdapter {

    private Context mContext;
    private List<JSONObject> mList;

    public WithdrawListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_withdraw_list, null);
            holder = new ViewHolder();
            holder.state = (TextView) convertView.findViewById(R.id.state);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject item = mList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#.00");
        switch (XString.getInt(item, "state")) {
            case 1:
                holder.state.setText("成功");
                break;
            case 2:
                holder.state.setText("失败");
                break;
            case 0:
                holder.state.setText("处理中");
                break;
        }
        holder.time.setText(format.format(XString.getLong(item, "addtime")));
        holder.money.setText("¥ " + df.format(XString.getDouble(item, "money")));
        return convertView;
    }

    public void addList(JSONArray list) {
        try {
            for (int i = 0; i < list.length(); i++) {
                mList.add(list.getJSONObject(i));
            }
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        TextView state, time ,money;
    }
}
