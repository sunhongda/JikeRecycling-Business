package com.buslink.busjie.driver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.manager.ProgresDlgManager;
import com.buslink.busjie.driver.manager.RequestManager;
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
            //订单详情
public class OrderDetailFragment extends LevelTwoFragment {

    String orid, pushid, carid, reqcarid;
    private int orderState;
    private ProgressDlg progressDlg;

    @Bind(R.id.order_detail_orderid)
    TextView mOrderId; // 订单编号
    @Bind(R.id.order_detail_ordertime)
    TextView mOrderTime; // 下单时间
    @Bind(R.id.order_detail_ordertotal)
    TextView mNumberOfPeople; // 乘车人数
    @Bind(R.id.startdate)
    TextView mStartDate; // 出发日期
    @Bind(R.id.startaddress)
    TextView mStartAddress; // 出发地点
    @Bind(R.id.enddate)
    TextView mEndDate; // 结束日期
    @Bind(R.id.endaddress)
    TextView mEndAddress; // 结束地点
    @Bind(R.id.totaldate)
    TextView mNumberOfDate; // 行程周期
    @Bind(R.id.busnum)
    TextView mNumberOfBus; // 用车数量
    @Bind(R.id.cartype)
    TextView mCarType; // 车辆类型
    @Bind(R.id.driver_welfare)
    TextView mDriverBenefit; // 司机福利
    @Bind(R.id.ll_pass)
    LinearLayout mOrderAdressLayout;
    @Bind(R.id.description)
    TextView mDescription; // 此订单您报价x元
    @Bind(R.id.driver_quoted_layout)
    LinearLayout mOrderQuotedLayout;
    @Bind(R.id.ok)
    Button mSubmitBtn;
    @Bind(R.id.order_detail_paymethod_textview)
    TextView mPayMethodTextView;
    @Bind(R.id.scrollview)
    BouceScrollView mBounceScrollView;
    @Bind(R.id.edit_price)
    EditText mReportPrice;

    @OnClick(R.id.ok) void ok() {
        if (orderState == 1) {
            if (TextUtils.isEmpty(mReportPrice.getText().toString())) {
                mActivity.app.toast("请输入报价金额");
                return;
            } else if (Integer.valueOf(mReportPrice.getText().toString()) == 0) {
                mActivity.app.toast("报价必须大于0元");
                return;
            }
            hideInputMethod();
            RequestParams params = new RequestParams();
            params.addQueryStringParameter("orid", orid);
            params.addQueryStringParameter("carid", carid);
            params.addQueryStringParameter("reqcarid", reqcarid);
            params.addQueryStringParameter("quoted", mReportPrice.getText().toString());
            RequestManager.orderPrice(params, new OrderPriceRequestCallBack());
        } else {
            mActivity.finish();
        }
    }

    @Override
    public String getTitle() {
        return "订单详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_order_detail;
    }

    @Override
    protected void initView() {
        hide();
        progressDlg = new ProgressDlg(getActivity(), "加载中..");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        initData();
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orid = bundle.getString("orid");
            pushid = bundle.getString("pushid");
        }
    }

    private void getData() {
        ProgresDlgManager.show(progressDlg);
        RequestParams params = new RequestParams();
        params.addBodyParameter("orid", orid);
        params.addBodyParameter("pushid", pushid);
        RequestManager.orderDetail(params, new OrderDetailRequestCallBack());
    }

    class OrderDetailRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            try {
                JSONObject jo1 = new JSONObject(responseInfo.result);
                boolean status = XString.getBoolean(jo1, "status");
                if (status) {
                    JSONObject data = jo1.getJSONObject("data");
                    carid = XString.getStr(data, "carid");
                    reqcarid = XString.getStr(data, "reqcarid");
                    mOrderId.setText(XString.getStr(data, "orderno"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mOrderTime.setText(format.format(XString.getLong(data, "addtime")));
                    String eat = "";
                    String live = "";
                    if (XString.getInt(data, "iseat") == 0) {
                        eat = "不包吃";
                    } else {
                        eat = "包吃";
                    }
                    if (XString.getInt(data, "islive") == 0) {
                        live = "不包住";
                    } else {
                        live = "包住";
                    }
                    mDriverBenefit.setText(eat + "，" + live);
                    orderState = XString.getInt(data, "orderstate");
                    mNumberOfDate.setText("共" + XString.getStr(data, "days") + "天");
                    mNumberOfBus.setText("共" + 1 + "辆");
                    mNumberOfPeople.setText(XString.getStr(data, "total") + "人（含儿童）");
                    mCarType.setText(XString.getStr(data, "cartype"));
                    format.applyPattern("MM月dd日 HH:mm");
                    mStartDate.setText(format.format(XString.getLong(data, "startdate")));
                    String startProvince = XString.getStr(data, "startprovince");
                    String startCity = XString.getStr(data, "startcity");
                    String startAddress = XString.getStr(data, "startaddress");
                    if (startProvince.equals(startCity)) {
                        if (startAddress.contains(startCity)) {
                            mStartAddress.setText(startAddress);
                        } else {
                            mStartAddress.setText(startCity + startAddress);
                        }
                    } else {
                        mStartAddress.setText(startProvince + startCity + startProvince);
                    }
                    mEndDate.setText(format.format(XString.getLong(data, "enddate")));
                    String endProvince = XString.getStr(data, "endprovince");
                    String endCity = XString.getStr(data, "endcity");
                    String endAddress = XString.getStr(data, "endaddress");
                    if (endProvince.equals(endCity)) {
                        if (endAddress.contains(endCity)) {
                            mEndAddress.setText(endAddress);
                        } else {
                            mEndAddress.setText(endCity + endAddress);
                        }
                    } else {
                        mEndAddress.setText(endProvince + endCity + endAddress);
                    }

                    JSONArray codes = data.getJSONArray("codes");
                    if (codes != null && codes.length() > 0) {
                        for (int i = 0; i < codes.length(); i++) {
                            JSONObject pass = codes.getJSONObject(i);
                            View view = LayoutInflater.from(mActivity).inflate(R.layout.order_pass_layout, null);
                            TextView time = (TextView) view.findViewById(R.id.order_detail_passingdate_textview);
                            TextView address = (TextView) view.findViewById(R.id.order_detail_passingaddress_textview);
                            time.setText(format.format(XString.getLong(pass, "waydate")));
                            String passProvince = XString.getStr(pass, "province");
                            String passCity = XString.getStr(pass, "city");
                            String passAddress = XString.getStr(pass, "address");
                            if (passProvince.equals(passCity)) {
                                if (passAddress.contains(passCity)) {
                                    address.setText(passAddress);
                                } else {
                                    address.setText(passCity + passAddress);
                                }
                            } else {
                                address.setText(passProvince + passCity + passAddress);
                            }
                            View separator = new View(getActivity());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                            params.topMargin = 5;
                            separator.setLayoutParams(params);
                            separator.setBackgroundColor(getResources().getColor(R.color.divider_white));
                            mOrderAdressLayout.addView(view);
                            //mOrderAdressLayout.addView(separator);
                        }

                    }
                    if (orderState == 1) { // 未报价
                        mSubmitBtn.setText("发送报价");
                        mDescription.setVisibility(View.GONE); // 隐藏支付信息
                        mOrderQuotedLayout.setVisibility(View.VISIBLE); // 显示报价输入框
                    } else {
                        mDescription.setVisibility(View.VISIBLE);
                        mOrderQuotedLayout.setVisibility(View.GONE);
                        String quoted = XString.getStr(data, "quoted");
                        if (TextUtils.isEmpty(quoted)) {
                            mDescription.setText("此订单您没有报价");
                        } else {
                            mDescription.setText("订单报价：" + XString.getStr(data, "quoted") + "元");
                        }
                    }
                    show();
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

    class OrderPriceRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                boolean status = XString.getBoolean(jo, "status");
                if (status) {
                    mActivity.app.toast("报价成功");
                    getActivity().finish();
                } else {
                    JSONObject data = jo.getJSONObject("data");
                    String msg = data.getString("msg");
                    mActivity.app.toast(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mActivity.app.toast("很抱歉，报价失败");
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.app.toast("很抱歉，报价失败");
        }
    }

    private void hideInputMethod() {
        InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mReportPrice.getWindowToken(), 0);
    }

    private void hide() {
        mBounceScrollView.setVisibility(View.INVISIBLE);
        mOrderQuotedLayout.setVisibility(View.INVISIBLE);
        mDescription.setVisibility(View.INVISIBLE);
        mSubmitBtn.setVisibility(View.INVISIBLE);
    }

    private void show() {
        mBounceScrollView.setVisibility(View.VISIBLE);
        mSubmitBtn.setVisibility(View.VISIBLE);
    }
}
