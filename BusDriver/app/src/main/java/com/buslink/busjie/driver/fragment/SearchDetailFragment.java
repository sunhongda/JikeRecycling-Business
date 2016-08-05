package com.buslink.busjie.driver.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.ScanPhotosActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SH;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SearchDetailFragment extends LevelTwoFragment {

//    @Bind(R.id.tv_1_0)
//    TextView tv10;
//    @Bind(R.id.tv_1_1)
//    TextView tv11;
//    @Bind(R.id.tv_1_2)
//    TextView tv12;
//    @Bind(R.id.tv_start_time)
//    TextView tvStartTime;
//    @Bind(R.id.tv_start_address)
//    TextView tvStartAddress;
//    @Bind(R.id.ll_pass)
//    LinearLayout llPass;
//    @Bind(R.id.tv_end_time)
//    TextView tvEndTime;
//    @Bind(R.id.tv_end_address)
//    TextView tvEndAddress;
//    @Bind(R.id.tv_days)
//    TextView tvDays;
//    @Bind(R.id.tv_cars_number)
//    TextView tvCarsNumber;
//    @Bind(R.id.tv_car_type)
//    TextView tvCarType;
//    @Bind(R.id.tv_driver_welfare)
//    TextView tvDriverWelfare;
//    @Bind(R.id.tv_mileage)
//    TextView tvMileage;
//    @Bind(R.id.quote)
//    TextView quote;
//    @Bind(R.id.et)
//    EditText et;
//    @Bind(R.id.btn)
//    Button btn;
//    @Bind(R.id.ll_4)
//    LinearLayout ll4;

    String orid, carid, reqcarid;
    //新
    @Bind(R.id.rv)
    RecyclerView rv;
    RecyclerView.Adapter adapterImg;
    ArrayList<String> plist;
    JSONObject data;
    List<List<Map<String, Object>>> list;
    @Bind(R.id.days)
    LinearLayout ll;

    private View v;
    @Bind(R.id.et)
    EditText et;




    @Override
    public String getTitle() {
        return "订单详情";
    }

    @Override                                   //R.layout.fragment_search_detail
    protected int getResLayout() {



        return R.layout.f_orderlist_detail;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        v=view;
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        Intent intent = mActivity.getIntent();
        params.addBodyParameter("orid", intent.getStringExtra("orid"));
        params.addBodyParameter("pushid", intent.getStringExtra("pushid"));
        RequestManager.orderDetail(params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);

                displayData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.app.toast("您的网络不给力");
            }
        });
    }

    private void displayData(String jsonResult) {
        JSONObject r = XString.getJSONObject(jsonResult);
         data = XString.getJSONObject(r, JsonName.DATA);
             orid = XString.getStr(data, "orid");
            carid = XString.getStr(data, "carid");
            reqcarid = XString.getStr(data, "reqcarid");
//        if (XString.getBoolean(jo, JsonName.STATUS)) {
//            tv10.setText("订单号：" + XString.getStr(data, JsonName.ORDERNO));
//            tv11.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));
//            tvDays.setText("共" + XString.getStr(data, JsonName.DAYS) + "天");
//            tv12.setText(XString.getStr(data, JsonName.TOTAL) + "人（含儿童）");
//            tvCarsNumber.setText("共" + 1 + "辆");
//            tvCarType.setText(XString.getStr(data, JsonName.CAR_TYPE));
//            String like = "不包住，不包吃";
//            int isEat = XString.getInt(data, JsonName.IS_EAT);
//            int isLive = XString.getInt(data, JsonName.IS_LIVE);
//            if (isEat == 1 && isLive == 1) {
//                like = "包吃，包住";
//            }
//            if (isEat == 1 && isLive == 0) {
//                like = "包吃";
//            }
//
//            if (isEat == 0 && isLive == 1) {
//                like = "包住";
//            }
//
//            if (isEat == 0 && isLive == 0) {
//                like = "无";
//            }
//            tvDriverWelfare.setText(like);
//            tvMileage.setText(XString.getStr(data, "drivingrange"));
//            tvStartTime.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)));
//
//            tvStartAddress.setText(AppUtils.getAddress(
//                    XString.getStr(data, JsonName.START_PROVINCE),
//                    XString.getStr(data, JsonName.START_CITY),
//                    XString.getStr(data, JsonName.START_ADDRESS)
//            ));
//
//            tvEndTime.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.END_DATE)));
//
//            tvEndAddress.setText(AppUtils.getAddress(
//                    XString.getStr(data, JsonName.END_PROVINCE),
//                    XString.getStr(data, JsonName.END_CITY),
//                    XString.getStr(data, JsonName.END_ADDRESS)
//            ));
//            if (XString.getInt(data, JsonName.ORDERSTATE) == 1) {
//                quote.setVisibility(View.GONE);
//                ll4.setVisibility(View.VISIBLE);
//            } else {
//                quote.setVisibility(View.VISIBLE);
//                ll4.setVisibility(View.GONE);
//                String quote = XString.getStr(data, "quoted");
//                if (TextUtils.isEmpty(quote)) {
//                    this.quote.setText("此订单您没有报价");
//                } else {
//                    this.quote.setText("此订单您报价" + quote + "元");
//                }
//            }
//
//            orid = XString.getStr(data, "orid");
//            carid = XString.getStr(data, "carid");
//            reqcarid = XString.getStr(data, "reqcarid");
//
//            int len = 0;
//            JSONArray codes = XString.getJSONArray(data, JsonName.CODES);
//            if (codes != null)
//                len = codes.length();
//            llPass.removeAllViews();
//            for (int i = 0; i < len; i++) {
//                try {
//                    JSONObject pinfo = codes.getJSONObject(i);
//                    View view = mActivity.getLayoutInflater().inflate(
//                            R.layout.i_passenger_order, null);
//                    TextView time = (TextView) view.findViewById(R.id.order_detail_passingdate_textview);
//                    TextView address = (TextView) view.findViewById(R.id.order_detail_passingaddress_textview);
//                    time.setText(DateUtils.getSimpleFormatTime(XString.getLong(pinfo, JsonName.WAYDATE)));
//                    address.setText(AppUtils.getAddress(
//                            XString.getStr(pinfo, JsonName.PROVINCE),
//                            XString.getStr(pinfo, JsonName.CITY),
//                            XString.getStr(pinfo, JsonName.ADDRESS)));
//                    llPass.addView(view);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        if (XString.getBoolean(r, JsonName.STATUS)){
            list = new LinkedList<>();//每一天的集合
            List<Map<String, Object>> l = new LinkedList<>();//途径点的集合
            Map<String, Object> m = new HashMap<>();//放时间，地点   途径点
            l.add(m);
            list.add(l);
            m.put(JsonName.TIME, XString.getLong(data, JsonName.START_DATE));
            m.put(JsonName.CITY, XString.getStr(data, JsonName.START_CITY));
            m.put(JsonName.PROVINCE, XString.getStr(data, JsonName.START_PROVINCE));
            m.put(JsonName.ADDRESS, XString.getStr(data, JsonName.START_ADDRESS));

            JSONArray codes = XString.getJSONArray(data, JsonName.CODES);
            int size = codes.length();
            for (int i = 0; i < size; i++) {
                JSONObject jo = XString.getJSONObject(codes, i);
                int j = XString.getInt(jo, JsonName.DAY);
                if (list.size() < j) {
                    l = new LinkedList<>();
                    list.add(l);
                }
                l = list.get(j - 1);
                m = new HashMap<>();
                l.add(m);
                m.put(JsonName.TIME, XString.getLong(jo, JsonName.WAYDATE));
                m.put(JsonName.CITY, XString.getStr(jo, JsonName.CITY));
                m.put(JsonName.PROVINCE, XString.getStr(jo, JsonName.PROVINCE));
                m.put(JsonName.ADDRESS, XString.getStr(jo, JsonName.ADDRESS));
            }
            l = list.get(list.size() - 1);
            m = new HashMap<>();
            l.add(m);
            m.put(JsonName.TIME, XString.getLong(data, JsonName.END_DATE));
            m.put(JsonName.CITY, XString.getStr(data, JsonName.END_CITY));
            m.put(JsonName.PROVINCE, XString.getStr(data, JsonName.END_PROVINCE));
            m.put(JsonName.ADDRESS, XString.getStr(data, JsonName.END_ADDRESS));

            //  final SimpleHolder holdr = new SimpleHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.f_orderlist_detail,container, false));
            //订单页用到的数据tag
//        holdr.setTag(R.id.rl, R.id.tv_1_0, R.id.tv_1_1, R.id.tv_1_2, R.id.tv_start_time, R.id.tv_start_address, R.id.ll_pass,
//                //给第二天的布局设置tag
//                R.id.ll_pass_second_day,
//                R.id.tv_end_time,
//                R.id.tv_end_address, R.id.tv_days, R.id.tv_cars_number, R.id.tv_car_type, R.id.tv_driver_welfare, R.id.bt, R.id.tv_1_3);
//            RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl);
            TextView tv_1_0 = (TextView) v.findViewById(R.id.tv_1_0);//订单号
            tv_1_0.setText(String.format("订单号：%s", XString.getStr(data, JsonName.ORDERNO)));
            TextView tv_1_1 = (TextView) v.findViewById(R.id.tv_1_1);//订单号右边的时间
            tv_1_1.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));
            TextView tv_1_2 = (TextView) v.findViewById(R.id.tv_1_2);//总人数
            tv_1_2.setText(String.format("%s人（含儿童）", XString.getStr(data, JsonName.TOTAL)));
            TextView tv_cars_number = (TextView) v.findViewById(R.id.tv_cars_number);//用车数量
            tv_cars_number.setText(String.format("共%d辆", 1));
//            TextView tv_car_type = (TextView) v.findViewById(R.id.tv_car_type);//车型
//            tv_car_type.setText(XString.getStr(data, JsonName.CAR_TYPE));
            TextView tv_car_type = (TextView) v.findViewById(R.id.tv_car_age);//车型
            tv_car_type.setText(XString.getStr(data, JsonName.CAR_AGE));

            TextView tv_driver_welfare = (TextView) v.findViewById(R.id.tv_driver_welfare);//司机福利
            String like = "不包住，不包吃";
            int isEat = XString.getInt(data, JsonName.IS_EAT);
            int isLive = XString.getInt(data, JsonName.IS_LIVE);
            if (isEat == 1 && isLive == 1) {
                like = "包吃，包住";
            }
            if (isEat == 1 && isLive == 0) {
                like = "包吃";
            }
            if (isEat == 0 && isLive == 1) {
                like = "包住";
            }
            if (isEat == 0 && isLive == 0) {
                like = "无";
            }
            tv_driver_welfare.setText(like);
            TextView tv_days = (TextView) v.findViewById(R.id.tv_days);//总行程
            tv_days.setText(String.format("共%s天", XString.getStr(data, JsonName.DAYS)));

            TextView tv_1_3 = (TextView) v.findViewById(R.id.tv_1_3);
            String s = XString.getStr(data, JsonName.DRIVING_RANGE);
            tv_1_3.setText(TextUtils.isEmpty(s) ? "未统计" : s);//驾车里程
//            public final static String TYPE_OF_CAR= "typeofcar";//用车类型 1单程2往返
//            public final static String INTENT_BID="intentbid";//意向出价
//            public final static String IMG_SUM="imgsum";//行程单照片总数
//            public final static String IMG_LST="imglst";//行程单照片集合
//            String typeofcar=XString.getStr(data,JsonName.TYPE_OF_CAR);////用车类型 1单程2往返

            TextView tv_intent_bid= (TextView) v.findViewById(R.id.tv_intent_bid);

            String intent_bid=XString.getStr(data,JsonName.INTENT_BID);

            if(intent_bid==null||intent_bid.equals("0")){
                tv_intent_bid.setText("没有意向出价");
            }else {
                tv_intent_bid.setText(intent_bid);
            }


            plist=new ArrayList();
            JSONArray parry=  XString.getJSONArray(data, JsonName.IMG_LST);
            int len= parry.length();
            for(int i=0;i<len;i++){
                try {
                    String path=XString.getStr(parry.getJSONObject(i), "path");
                    Log.d("path", path);
                    plist.add(Net.IMGURL+ XString.getStr(parry.getJSONObject(i),"path"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(plist.size()>0){
                rv.setVisibility(View.VISIBLE);
                setPhotos(plist);
            }


            size = list.size();
            for (int i = 0; i < size; i++) {
                l = list.get(i);
                int max = l.size();
                View day = LayoutInflater.from(getActivity()).inflate(R.layout.i_orderlist, ll, false);
                TextView tvWhichDay = (TextView) day.findViewById(R.id.tv_which_day);
                TextView tvStartOneDayTime = (TextView) day.findViewById(R.id.tv_start_one_day_time);
                TextView tvStartTime = (TextView) day.findViewById(R.id.tv_start_time);
                TextView tvStartAddress = (TextView) day.findViewById(R.id.tv_start_address);
                TextView tvEndTime = (TextView) day.findViewById(R.id.tv_end_time);
                TextView tvEndAddress = (TextView) day.findViewById(R.id.tv_end_address);
                LinearLayout pass = (LinearLayout) day.findViewById(R.id.ll_pass);
                for (int j = 0; j < max; j++) {
                    m = l.get(j);
//                    if (size == 1) {
//                      //  day.findViewById(R.id.ll).setVisibility(View.GONE);//去掉第一天
//                        tvWhichDay.setText("行程安排");
//                    } else {
//                        day.findViewById(R.id.ll).setVisibility(View.VISIBLE);
//                    }
                    //TODO
                    String ordertype=XString.getStr(data, JsonName.ORDER_TYPE);

                    //if("5".equals(ordertype)||"9".equals(ordertype)){
                        //多日游           //含照片的
                        day.findViewById(R.id.ll).setVisibility(View.VISIBLE);
                        tvWhichDay.setText(String.format("第%d天", i + 1));

                        if (j == 0) {
                            tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
                            tvStartTime.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                            tvStartAddress.setText(
                                    AppUtils.getAddress(
                                            (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
                        } else if (j == max - 1) {
                            //TODO
                            //||null.equals(m.get(JsonName.TIME).toString())
                            if(m.get(JsonName.TIME).toString().equals("0")){
                                setZero(tvEndTime,true);
                            }else{
                                tvEndTime.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                            }

                            tvEndAddress.setText(
                                    AppUtils.getAddress(
                                            (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));

                        } else {
                            View p = LayoutInflater.from(getActivity()).inflate(R.layout.i_passenger_order, pass, false);
                            TextView tv = (TextView) p.findViewById(R.id.order_detail_passingdate_textview);
                            TextView tv1 = (TextView) p.findViewById(R.id.order_detail_passingaddress_textview);

                            if(m.get(JsonName.TIME).toString().equals("0")){
                                //<color name="bg">#f7f7f8</color>
                                //  tvEndTime.setTextColor(Color.parseColor("#f7f7f8"));
                                setZero(tv,true);
                            }else{
                                tv.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                            }

                            tv1.setText(
                                    AppUtils.getAddress(
                                            (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
                            pass.addView(p);
                        }



                }
                ll.addView(day);
            }
        }














    }

    @OnClick(R.id.btn) void submit() {
        if (verify()) {
            RequestParams params = RequestManager.simpleGetParams();
            params.addQueryStringParameter("orid", orid);
            params.addQueryStringParameter("carid", carid);
            params.addQueryStringParameter("reqcarid", reqcarid);
            params.addQueryStringParameter("quoted", et.getText().toString());
            RequestManager.orderPrice(params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    LogUtils.d(responseInfo.result);
                    JSONObject jo = XString.getJSONObject(responseInfo.result);
                    if (XString.getBoolean(jo, JsonName.STATUS)) {
                        hideKeyboard();
                        showDialog();
                    } else {
                        JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                        mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    mActivity.app.toast("您的网络不给力");
                }
            });
        }
    }

    private boolean verify() {
        if (TextUtils.isEmpty(et.getText().toString())) {
            mActivity.app.toast("报价不能为空");
            return false;
        }
        if ("0".equals(et.getText().toString())) {
            mActivity.app.toast("报价不能为0");
            return false;
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("报价成功")
                .setMessage("您可进入\"首页\" - \"订单\"中查看该订单")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyEvent e = new MyEvent("finishSearchList");
                        EventBus.getDefault().post(e);
                        mActivity.finish();
                    }
                });
        builder.create().show();
    }

    private void hideKeyboard() {
        InputMethodManager mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }



    public void setZero(TextView tv,Boolean type){//true为新订单type为5或9的，false为老订单
        if(type){
            tv.setText("00时00分");
        }else{
            tv.setText("0000-00-00 00:00");
        }
        tv.setTextColor(Color.parseColor("#f7f7f8"));
    }

    private void setPhotos(final List<String> list) {
        //Net.IMGURL + XString.getStr(data, JsonName.CAR_IMG)

        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rv.setAdapter(adapterImg = new RecyclerView.Adapter<SH>() {


            @Override
            public SH onCreateViewHolder(ViewGroup parent, int viewType) {
                final SH sh = new SH(LayoutInflater.from(getActivity()).inflate(R.layout.i_order_photowall, parent, false));
                sh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setClass(getActivity(), ScanPhotosActivity.class);
                        intent.putStringArrayListExtra("plist", plist);
                        intent.putExtra("currentposition", String.valueOf(sh.getIndex()));
                        startActivity(intent);
                    }
                });
                return sh;
            }

            @Override
            public void onBindViewHolder(SH holder, int position) {
                // holder.<ImageView>getView(R.id.iv_photowall).setImageBitmap(BitmapFactory.decodeFile(listImg.get(position).get("img")));
                holder.setIndex(position);
                BitmapUtils bUtils = new BitmapUtils(getActivity());
                bUtils.display(holder.<ImageView>getView(R.id.iv_photowall), list.get(position));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }



}














//
//
//}else{
//        //老订单
//        day.findViewById(R.id.ll).setVisibility(View.GONE);//去掉第一天
//        if (j == 0) {
//        tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
//        //DataUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)
//        // tvStartTime.setText(DataUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
//        tvStartTime.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//        tvStartAddress.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        } else if (j == max - 1) {
//        tvEndTime.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//        tvEndAddress.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        } else {
//        View p = LayoutInflater.from(getActivity()).inflate(R.layout.i_passenger_order, pass, false);
//        TextView tv = (TextView) p.findViewById(R.id.order_detail_passingdate_textview);
//        TextView tv1 = (TextView) p.findViewById(R.id.order_detail_passingaddress_textview);
//
//
//        if(m.get(JsonName.TIME).toString().equals("0")){
//        setZero(tv,false);
//        }else{
//        //tv.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
//        //兼容老版本订单途径点时间格式
//        tv.setText(DateUtils.getSimpleFormatTime((Long) m.get(JsonName.TIME)));
//        }
//        tv1.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        pass.addView(p);
//        }
//        }
