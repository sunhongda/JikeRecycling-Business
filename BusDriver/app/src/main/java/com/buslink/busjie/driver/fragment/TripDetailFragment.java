package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.ProgresDlgManager;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.OrderState;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.BouceScrollView;
import com.buslink.busjie.driver.view.ProgressDlg;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class TripDetailFragment extends LevelTwoFragment {

    String orid, pushid;
    private ProgressDlg progressDlg;
    private int orderstate, passengerStar;
    private String passengerName;

    @Bind(R.id.order_detail_orderid)
    TextView mOrderIdTextView;
    @Bind(R.id.order_detail_ordertime)
    TextView mOrderTimeTextView;
    @Bind(R.id.order_detail_ordertotal)
    TextView mNumberOfPeopleTextView;
    @Bind(R.id.order_detail_passengername_textview)
    TextView mPassengerName;
    @Bind(R.id.order_detail_passengerphone_textview)
    TextView mPassengerPhone;
    @Bind(R.id.order_detail_passengercompany_textview)
    TextView mPassengerCompany;
    @Bind(R.id.startdate)
    TextView mStartDateTextView;
    @Bind(R.id.startaddress)
    TextView mStartAddress;
    @Bind(R.id.enddate)
    TextView mEndDateTextView;
    @Bind(R.id.endaddress)
    TextView mEndAddress;
    @Bind(R.id.totaldate)
    TextView mNumberOfDateTextView;
    @Bind(R.id.busnum)
    TextView mNumberOfBusTextView;
    @Bind(R.id.cartype)
    TextView mCarTypeTextView;
    @Bind(R.id.driver_welfare)
    TextView mDriverBenefitTextView;
    @Bind(R.id.order_detail_orderstate_textview)
    TextView mOrderState;
    @Bind(R.id.order_address_info_detail_layout)
    LinearLayout mOrderAdressLayout;
    @Bind(R.id.order_detail_myoffer_textview)
    TextView mOrderOffer;
    @Bind(R.id.trip_detail_ok_btn)
    Button mSubmitBtn;
    @Bind(R.id.order_detail_paymethod_textview)
    TextView mPayMethodTextView;
    @Bind(R.id.order_detail_scrollview)
    BouceScrollView mBounceScrollView;

    @OnClick(R.id.trip_detail_ok_btn)
    public void onClick() {
        if (orderstate == 4) { // 已完成
            Intent intent = new Intent(mActivity, BackActivity.class);
            intent.putExtra("fragmentName", JudgePassengerFragment.class.getName());
            intent.putExtra("passengerName", passengerName);
            intent.putExtra("passengerStar", passengerStar);
            intent.putExtra("orid", orid);
            startActivity(intent);
        } else {
            mActivity.finish();
        }
    }

    @Override
    public String getTitle() {
        return "行程详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_trip_detail;
    }

    @Override
    protected void initView() {

    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orid = bundle.getString("orid");
            pushid = bundle.getString("pushid");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initData();
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if(EventName.JudgePassenger.equals(e.getTag())) {
            getData();
        }
    }

    private void getData() {
        mBounceScrollView.setVisibility(View.INVISIBLE);
        mSubmitBtn.setVisibility(View.INVISIBLE);
        progressDlg = new ProgressDlg(getActivity(), "加载中..");
        ProgresDlgManager.show(progressDlg);
        RequestParams params = new RequestParams();
        params.addBodyParameter("orid", orid);
        params.addBodyParameter("pushid", pushid);
        RequestManager.tripDetail(params, new TripDetailRequestCallBack());
    }

    class TripDetailRequestCallBack extends RequestCallBack<String>{
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                boolean status = XString.getBoolean(jo, "status");
                if (status) {
                    JSONObject data = jo.getJSONObject("data");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mOrderIdTextView.setText("订单编号：" + XString.getStr(data, "orderno"));
                    mOrderTimeTextView.setText("下单时间：" + format.format(XString.getLong(data, "addtime")));
                    String iseat = "";
                    String islive = "";
                    if (XString.getInt(data, "iseat") == 0) {
                        iseat = "不包吃";
                    } else {
                        iseat = "包吃";
                    }
                    if (XString.getInt(data, "islive") == 0) {
                        islive = "不包住";
                    } else {
                        islive = "包住";
                    }
                    mDriverBenefitTextView.setText("司机福利：" + iseat + "，" + islive);
                    orderstate = XString.getInt(data, "orderstate");
                    if (orderstate == 4) {
                        mSubmitBtn.setText("评价乘客");
                    } else {
                        mSubmitBtn.setText("确定");
                    }
                    mOrderState.setText("订单状态：" + OrderState.getState(XString.getInt(data, "orderstate")));

                    passengerName = XString.getStr(data, "name");
                    String company = XString.getStr(data, "entname");
                    if (TextUtils.isEmpty(passengerName)) {
                        mPassengerName.setVisibility(View.GONE);
                    } else {
                        mPassengerName.setText("姓名：" + passengerName);
                    }
                    mPassengerPhone.setText("电话：" + XString.getStr(data, "phone"));
                    if (TextUtils.isEmpty(company)) {
                        mPassengerCompany.setVisibility(View.GONE);
                    } else {
                        mPassengerCompany.setText("公司：" + company);
                    }
                    passengerStar = XString.getInt(data, "pstar");
                    mNumberOfDateTextView.setText("行程周期：共" + XString.getStr(data, "days") + "天");
                    mNumberOfBusTextView.setText("用车数量：共" + 1 + "辆");
                    mNumberOfPeopleTextView.setText("乘车人数：" + XString.getStr(data, "total") + "人（含儿童）");
                    mCarTypeTextView.setText("车辆类型：" + XString.getStr(data, "cartype"));
                    format.applyPattern("MM月dd日 HH:mm");
                    mStartDateTextView.setText(format.format(XString.getLong(data, "startdate")));
                    String startProvince = XString.getStr(data, "startprovince");
                    String startCity = XString.getStr(data, "startcity");
                    String startAddress = XString.getStr(data, "startaddress");
                    if (startProvince.equals(startCity)) {
                        if (startAddress.contains(startCity)) {
                            mStartAddress.setText("起点：" + startAddress);
                        } else {
                            mStartAddress.setText("起点：" + startCity + startAddress);
                        }
                    } else {
                        mStartAddress.setText("起点：" + startProvince + startCity + startAddress);
                    }
                    mEndDateTextView.setText(format.format(XString.getLong(data, "enddate")));
                    String endProvince = XString.getStr(data, "endprovince");
                    String endCity = XString.getStr(data, "endcity");
                    String endAddress = XString.getStr(data, "endaddress");
                    if (endProvince.equals(endCity)) {
                        if (endAddress.equals(endCity)) {
                            mEndAddress.setText("终点：" + endAddress);
                        } else {
                            mEndAddress.setText("终点：" + endCity + endAddress);
                        }
                    } else {
                        mEndAddress.setText("终点：" + endProvince + endCity + endAddress);
                    }
                    // 途经点
                    JSONArray codes = data.getJSONArray("codes");
                    if (codes != null && codes.length() > 0) {
                        for (int i = 0; i < codes.length(); i++) {
                            JSONObject pass = codes.getJSONObject(i);
                            View view = LayoutInflater.from(mActivity).inflate(R.layout.order_detail_address_layout, null);
                            TextView time = (TextView) view.findViewById(R.id.order_detail_passingdate_textview);
                            TextView address = (TextView) view.findViewById(R.id.order_detail_passingaddress_textview);
                            time.setText(format.format(XString.getLong(pass, "waydate")));
                            String passProvince = XString.getStr(pass, "province");
                            String passCity = XString.getStr(pass, "city");
                            String passAddress = XString.getStr(pass, "address");
                            if (passProvince.equals(passCity)) {
                                if (passAddress.contains(passCity)) {
                                    address.setText("途经：" + passAddress);
                                } else {
                                    address.setText("途经：" + passCity + passAddress);
                                }
                            } else {
                                address.setText("途经：" + passProvince + passCity + passAddress);
                            }
                            View separator = new View(getActivity());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                            params.topMargin = 5;
                            separator.setLayoutParams(params);
                            separator.setBackgroundColor(getResources().getColor(R.color.divider_white));
                            mOrderAdressLayout.addView(view);
                            mOrderAdressLayout.addView(separator);
                        }
                    }
                    mOrderOffer.setText("支付价格：" + XString.getStr(data, "quoted"));
                    switch (XString.getInt(data, "ptype")) {
                        case 1:
                            mPayMethodTextView.setText("支付方式：支付宝支付");
                            break;
                        case 2:
                            mPayMethodTextView.setText("支付方式：微信支付");
                            break;
                        case 3:
                            mPayMethodTextView.setText("支付方式：现金支付");
                            break;
                        case 4:
                            mPayMethodTextView.setText("支付方式：银联支付");
                            break;
                        case 5:
                            mPayMethodTextView.setText("零钱支付");
                            break;
                    }
                    mBounceScrollView.setVisibility(View.VISIBLE);
                    mSubmitBtn.setVisibility(View.VISIBLE);
                } else {
                    mActivity.app.toast("数据错误");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mActivity.app.toast("数据错误");
            }
            ProgresDlgManager.dismiss(progressDlg);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            LogUtils.e(s, e);
            mActivity.app.toast("数据错误");
            ProgresDlgManager.dismiss(progressDlg);
        }
    }

}
