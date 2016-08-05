package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.CitySelectDialog;
import com.buslink.busjie.driver.view.TimeSelectDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Administrator on 2015/9/28.
 */
public class SearchFragment extends LevelTwoFragment {

    @ViewInject(R.id.start_time)
    TextView mStartTime;

    @ViewInject(R.id.end_time)
    TextView mEndTime;

    @ViewInject(R.id.start_address)
    TextView mStartAddress;

    @ViewInject(R.id.end_address)
    TextView mEndAddress;

    private TimeSelectDialog timeDialog1, timeDialog2;
    private CitySelectDialog cityDialog1, cityDialog2;
    private String carid;

    @OnClick({R.id.start_time,
            R.id.end_time,
            R.id.start_address,
            R.id.end_address})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_time:
                selectTime(mStartTime);
                break;
            case R.id.end_time:
                selectTime(mEndTime);
                break;
            case R.id.start_address:
                selectLocation(mStartAddress);
                break;
            case R.id.end_address:
                selectLocation(mEndAddress);
                break;
        }
    }

    @OnClick(R.id.search) void search(View v) {
        if (verify()) {
            submit();
        }
    }

    @Override
    public String getTitle() {
        return "搜索";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_search_main;
    }

    @Override
    protected void initView() {
        Calendar c = Calendar.getInstance();
        mStartTime.setTag(new TimeSelectDialog(mActivity, c).setOnSelectTimeListener(new TimeSelectDialog.SelectTimeListener() {
            @Override
            public void onSelect(String date) {
                mStartTime.setText(date);
            }
        }));
        mEndTime.setTag(new TimeSelectDialog(mActivity, c).setOnSelectTimeListener(new TimeSelectDialog.SelectTimeListener() {
            @Override
            public void onSelect(String date) {
                mEndTime.setText(date);
            }
        }));
        mStartAddress.setTag(new CitySelectDialog(mActivity).setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
            @Override
            public void onSelect(String province, String city) {
                mStartAddress.setText(city);
            }
        }));
        mEndAddress.setTag(new CitySelectDialog(mActivity, true).setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
            @Override
            public void onSelect(String province, String city) {
                mEndAddress.setText(city);
            }
        }));
        carid = mActivity.getIntent().getStringExtra("carid");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void selectTime(final TextView textView) {
        TimeSelectDialog d = (TimeSelectDialog) textView.getTag();
        d.show();
    }

    private void selectLocation(final TextView textView) {
        CitySelectDialog dialog = (CitySelectDialog) textView.getTag();
        dialog.show();
    }

    private boolean verify() {
        if (TextUtils.isEmpty(mStartTime.getText().toString())) {
            mActivity.app.toast("请选择出发时间");
            return false;
        }
        if (TextUtils.isEmpty(mEndTime.getText().toString())) {
            mActivity.app.toast("请选择结束时间");
            return false;
        }
        if (TextUtils.isEmpty(mStartAddress.getText().toString())) {
            mActivity.app.toast("请选择起点城市");
            return false;
        }
        if (TextUtils.isEmpty(mEndAddress.getText().toString())) {
            mActivity.app.toast("请选择结束城市");
            return false;
        }
        return true;
    }

    private void submit() {
        final RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("startdate", mStartTime.getText().toString());
        params.addBodyParameter("enddate", mEndTime.getText().toString());
        params.addBodyParameter("scity", mStartAddress.getText().toString());
        params.addBodyParameter("ecity", mEndAddress.getText().toString());
        params.addBodyParameter("carid", carid);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.SEARCH, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                super.onStart();
                mActivity.dialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mActivity.dialog.dismiss();
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONArray orderlist = XString.getJSONArray(data, "orderlist");
                    if (orderlist.length() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("result", responseInfo.result);
                        intent.putExtra("startdate", mStartTime.getText().toString());
                        intent.putExtra("enddate", mEndTime.getText().toString());
                        intent.putExtra("scity", mStartAddress.getText().toString());
                        intent.putExtra("ecity", mEndAddress.getText().toString());
                        intent.putExtra("carid", carid);
                        mActivity.startFragment(BackActivity.class, SearchListFragment.class, intent);
                    } else {
                        mActivity.app.toast("没有合适的订单");
                    }

                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.dialog.dismiss();
                mActivity.app.toast("您的网络不给力");
            }
        });
    }
}




















