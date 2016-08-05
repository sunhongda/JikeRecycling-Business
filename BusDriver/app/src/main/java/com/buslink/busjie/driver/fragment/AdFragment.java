package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.VerifyForm;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.CitySelectDialog;
import com.buslink.busjie.driver.view.TimeSelectDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdFragment extends LevelTwoFragment implements VerifyForm {

    int ispushad;

    @Bind(R.id.button)
    Button button;
    @Bind(R.id.start_time)
    TextView mStartTime;
    @Bind(R.id.end_time)
    TextView mEndTime;
    @Bind(R.id.start_address)
    TextView mStartAddress;
    @Bind(R.id.end_address)
    TextView mEndAddress;

    @OnClick(R.id.button) void ok() {
        if (ispushad == 2) {
            mActivity.app.toast("每天只能发布一条广告");
            return;
        }
        if (verify()) {
            postAd();
        }
    }

    @OnClick(R.id.start_time) void startTime() {
        selectTime(mStartTime);
    }

    @OnClick(R.id.end_time) void endTime() {
        selectTime(mEndTime);
    }

    @OnClick(R.id.start_address) void startAddress() {
        selectLocation(mStartAddress);
    }

    @OnClick(R.id.end_address) void endAddress() {
        selectLocation(mEndAddress);
    }

    @Override
    public String getTitle() {
        return "发布广告";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_ad;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_ad);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ad_history:
                        //mActivity.startActivity(new Intent(mActivity, AdListActivity.class));
                        mActivity.startFragment(BackActivity.class, AdListFragment2.class);
                }
                return false;
            }
        });
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
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        ispushad = bundle.getInt("ispushad");
    }

    private void postAd() {
        HttpUtils http = new HttpUtils();
        RequestParams params = RequestManager.simplePostParams();

        params.addBodyParameter("startdate", mStartTime.getText().toString());
        params.addBodyParameter("enddate", mEndTime.getText().toString());
        params.addBodyParameter("scity", mStartAddress.getText().toString());
        params.addBodyParameter("ecity", mEndAddress.getText().toString());
        http.send(HttpRequest.HttpMethod.POST, Net.AD, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    mActivity.app.toast("广告发布成功");
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.d(s, e);
                mActivity.app.toast("广告发布失败");
            }
        });
    }

    private void selectTime(final TextView textView) {
        TimeSelectDialog d = (TimeSelectDialog) textView.getTag();
        d.show();
    }

    private void selectLocation(final TextView textView) {
        CitySelectDialog dialog = (CitySelectDialog) textView.getTag();
        dialog.show();
    }

    @Override
    public boolean verify() {
        try {
            if (TextUtils.isEmpty(mStartTime.getText().toString())) {
                mActivity.app.toast("请选择出发时间");
                return false;
            }
            if (TextUtils.isEmpty(mEndTime.getText().toString())) {
                mActivity.app.toast("请选择结束时间");
                return false;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            long startTime, endTime;
            startTime = format.parse(mStartTime.getText().toString()).getTime();
            endTime = format.parse(mEndTime.getText().toString()).getTime();
            if (endTime <= startTime) {
                mActivity.app.toast("结束时间必须大于出发时间");
                return false;
            }
            if (TextUtils.isEmpty(mStartAddress.getText().toString())) {
                mActivity.app.toast("请选择出发城市");
                return false;
            }
            if (TextUtils.isEmpty(mEndAddress.getText().toString())) {
                mActivity.app.toast("请选择终点城市");
                return false;
            }
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}