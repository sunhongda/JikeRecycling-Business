package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/1/29.
 */
public class OrderListDetailFragmentCharacter extends LevelTwoFragment {
    private Intent intent;
    private String responseString;
    List<List<Map<String, Object>>> list;
    @ViewInject(R.id.tv_order_nb)
    TextView tvOrderNb;
    @ViewInject(R.id.tv_time)
    TextView tvTime;
    @ViewInject(R.id.tv_start_time)
    TextView tvStartTime;
    @ViewInject(R.id.tv_chartered_type)
    TextView tvCharteredType;
    @ViewInject(R.id.tv_bus_type)
    TextView tvBusType;
    @ViewInject(R.id.tv_price)
    TextView tvPrice;
    @ViewInject(R.id.tv_distance)
    TextView tvDistance;
    @ViewInject(R.id.tv_total)
    TextView tvTotal;
    @ViewInject(R.id.progressbar)
    ProgressBar progressBar;
    @ViewInject(R.id.cv)
    CardView cv;
    @ViewInject(R.id.days)
    LinearLayout ll;

    @ViewInject(R.id.bt)
    Button bt;
    @ViewInject(R.id.prompt)
     LinearLayout prompt;
    @ViewInject(R.id.tv_refund)
    TextView tvRefund;
    @ViewInject((R.id.ll_refund))
    LinearLayout llRefund;
    @ViewInject(R.id.tv_alreadypay)
    TextView tvAlreadypay;
    @ViewInject((R.id.ll_alreadypay))
    LinearLayout llAlreadypay;


    String orid;
    String carid;
    String pushid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.inject(this, view);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public String getTitle() {

        return "订单详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.f_orderlist_detail_chartered;
    }

    @Override
    protected void initView() {
        intent = getActivity().getIntent();
        orid=intent.getStringExtra("orid");
        pushid=intent.getStringExtra("pushid");
        getOrderData();
    }

    private void getOrderData() {
        AsyncHttpClient client = MyHelper.getClient();
        RequestParams params = MyHelper.getParams();
        params.add(JsonName.ORID, orid);
        params.add(JsonName.CARID, carid);
        params.add(JsonName.PUSHID,pushid);
        client.post(mActivity, Net.CHARTEREDBUS_DMYORDERDETAIL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mActivity.app.toast("您的网络不给力");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                LogUtils.d(responseString);
                cv.setCardBackgroundColor(getResources().getColor(R.color.green));
                progressBar.setVisibility(View.GONE);
                JSONObject jo = XString.getJSONObject(responseString);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                    carid=XString.getStr(data,JsonName.CARID);

                    tvOrderNb.setText(String.format("订单号：%s", XString.getStr(data, JsonName.ORDERNO)));
                    tvTime.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));
                    tvStartTime.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)));
                    tvCharteredType.setText(XString.getInt(data, JsonName.CDAY_TYPE) == 1 ? "半日租" : "全日租");//1半日租 2全日租
                    tvBusType.setText(XString.getStr(data, JsonName.CAR_NAME));
                    tvPrice.setText(XString.getStr(data, JsonName.PRICE_ONE) + "元");
                    tvDistance.setText((XString.getInt(data, JsonName.CDAY_TYPE) == 1 ? "4小时" : "8小时") + "/" + XString.getStr(data, JsonName.DISTANCE) + "公里");

                  //  tvTotal.setText(XString.getStr(data, JsonName.PRICE_SUM) + "元");
                    tvTotal.setText(XString.getStr(data, JsonName.PRICE_ONE) + "元");
                    if(XString.getInt(data,JsonName.ORDERSTATE)==11&&XString.getInt(data,JsonName.DRIVERPROFIT)>0){
                        llRefund.setVisibility(View.VISIBLE);
                        tvRefund.setText(XString.getStr(data, JsonName.DRIVERPROFIT) + "元");//退单补偿金
                    }
                    if(XString.getInt(data,JsonName.ORDERSTATE)==11&&XString.getInt(data,JsonName.ONE_PAY_MONEY)>0){
                        llAlreadypay.setVisibility(View.VISIBLE);
                        //已付金额
                        tvAlreadypay.setText(XString.getStr(data,JsonName.ONE_PAY_MONEY)+"元");
                    }


                    Map<String, Object> m1 = new HashMap<String, Object>();
                    m1.put(JsonName.TIME, XString.getLong(data, JsonName.START_DATE));
                    m1.put(JsonName.CITY, XString.getStr(data, JsonName.START_CITY));
                    m1.put(JsonName.PROVINCE, XString.getStr(data, JsonName.START_PROVINCE));
                    m1.put(JsonName.ADDRESS, XString.getStr(data, JsonName.START_ADDRESS));
                    Map<String, Object> m2 = new HashMap<String, Object>();
                    m2.put(JsonName.TIME, XString.getLong(data, JsonName.END_DATE));
                    m2.put(JsonName.CITY, XString.getStr(data, JsonName.END_CITY));
                    m2.put(JsonName.PROVINCE, XString.getStr(data, JsonName.END_PROVINCE));
                    m2.put(JsonName.ADDRESS, XString.getStr(data, JsonName.END_ADDRESS));


                    View day = LayoutInflater.from(getActivity()).inflate(R.layout.i_orderlist, ll, false);
                    TextView tvWhichDay = (TextView) day.findViewById(R.id.tv_which_day);
                    TextView tvStartOneDayTime = (TextView) day.findViewById(R.id.tv_start_one_day_time);
                    TextView tvStartTime = (TextView) day.findViewById(R.id.tv_start_time);
                    TextView tvStartAddress = (TextView) day.findViewById(R.id.tv_start_address);
                    TextView tvEndTime = (TextView) day.findViewById(R.id.tv_end_time);
                    tvEndTime.setVisibility(View.INVISIBLE);
                    TextView tvEndAddress = (TextView) day.findViewById(R.id.tv_end_address);
                    LinearLayout pass = (LinearLayout) day.findViewById(R.id.ll_pass);
                    tvWhichDay.setText(String.format("行程路线"));

                    tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m1.get(JsonName.TIME)));
                    tvStartTime.setText(DateUtils.getHHmm((long) m1.get(JsonName.TIME)));
                    tvStartAddress.setText(
                            AppUtils.getAddress(
                                    (String) m1.get(JsonName.PROVINCE), (String) m1.get(JsonName.CITY), (String) m1.get(JsonName.ADDRESS)));

                    tvEndTime.setText(DateUtils.getHHmm((long) m2.get(JsonName.TIME)));
                    tvEndAddress.setText(
                            AppUtils.getAddress(
                                    (String) m2.get(JsonName.PROVINCE), (String) m2.get(JsonName.CITY), (String) m2.get(JsonName.ADDRESS)));


                    ll.addView(day);


                    if(XString.getInt(data,JsonName.ORDERSTATE)==13)//未接单
                    {
                        prompt.setVisibility(View.VISIBLE);

                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                receiveOrder();

                            }
                        });
                    }else if(XString.getInt(data,JsonName.ORDERSTATE)==14){//未支付
                        prompt.setVisibility(View.VISIBLE);
                        bt.setText("确认");
                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().finish();
                            }
                        });
                    }else{
                        prompt.setVisibility(View.GONE);
                        bt.setText("确认");
                        bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getActivity().finish();
                            }
                        });
                    }
                } else {
                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }
        });


    }

    private void receiveOrder() {//接单
        AsyncHttpClient client=MyHelper.getClient();
        RequestParams params=MyHelper.getParams();
        params.add(JsonName.ORID, orid);
        params.add(JsonName.CARID, carid);
        params.add(JsonName.PUSHID,pushid);
        client.post(mActivity,Net.CHARTEREDBUS_CONFIRMATIONORDER,params,new TextHttpResponseHandler(){

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mActivity.app.toast("您的网络不给力");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        JSONObject jo=XString.getJSONObject(responseString);
                if(XString.getBoolean(jo,JsonName.STATUS)){
                    mActivity.app.toast("接单成功");
                    EventBus.getDefault().post("","upOrderListDetailFragment");
                    getActivity().finish();
                }else{
                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
