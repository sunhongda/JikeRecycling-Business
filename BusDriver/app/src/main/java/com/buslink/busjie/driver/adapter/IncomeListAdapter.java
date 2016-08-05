package com.buslink.busjie.driver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.XString;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class IncomeListAdapter extends BaseAdapter{

    private Context mContext;
    List<JSONObject> mIncomeList;

    public IncomeListAdapter(Context context, List<JSONObject> incomeList) {
        mContext = context;
        mIncomeList = incomeList;
    }

    @Override
    public int getCount() {
        return mIncomeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mIncomeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_income_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.time = (TextView) convertView.findViewById(R.id.income_list_time);
            holder.money = (TextView) convertView.findViewById(R.id.income_list_money);

            holder.incomname= (TextView) convertView.findViewById(R.id.income_name);//添加收入的状态名
            holder.ordernum=(TextView)convertView.findViewById(R.id.tv_order_nb);//添加订单号
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject income = mIncomeList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat df = new DecimalFormat("#.00");
        holder.time.setText(format.format(XString.getLong(income, "enddate")));
        holder.money.setText("¥ " + df.format(XString.getDouble(income, "money")));
//        if("6".equals(XString.getStr(income, "resourcetype"))){
//            //6是退款
//            holder.incomname.setText("订单补偿金");
//            holder.image.setImageResource(R.mipmap.ic_chang_money);
//        }else{
//            holder.incomname.setText("已入账");
//            holder.image.setImageResource(R.mipmap.ic_chang_money);
//        }
        holder.image.setImageResource(AppUtils.getResourceTypeIocn(XString.getInt(income, JsonName.RESOURCE_TYPE)));
        holder.incomname.setText(AppUtils.getResourceTypeText(XString.getInt(income, JsonName.STATE)));
        holder.ordernum.setText("编号："+XString.getStr(income,JsonName.ORDERNO));

//        if ("1".equals(XString.getStr(income, "resourcetype"))) {
//            holder.image.setImageResource(R.mipmap.ic_chang_red);
//        } else if ("5".equals(XString.getStr(income, "resourcetype"))) {
//            holder.image.setImageResource(R.mipmap.ic_chang_money);
//        }
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView time;
        TextView money;
        TextView incomname;
        TextView ordernum;
    }

}
