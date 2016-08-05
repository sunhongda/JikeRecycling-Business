package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class PurseFragment extends LevelTwoFragment {

    @ViewInject(R.id.tv)
    private TextView tv;
    @ViewInject(R.id.tv_1)
    private TextView tv1;
    @ViewInject(R.id.tv_2)
    private TextView tv2;
    @ViewInject(R.id.tv_3)
    private TextView tv3;

    private String resultString;

    @Override
    public String getTitle() {
        return "我的钱包";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_purse;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
        resultString=mActivity.getIntent().getStringExtra("wallet");
        if(TextUtils.isEmpty(resultString)){
            getData();
        }else{
            setData();
        }
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if (EventName.AddBank.equals(e.getTag())) {
            getData();
        }
        if (EventName.DeleteBank.equals(e.getTag())) {
            getData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ll,
            R.id.ll_3})
    private void toChange(View v) { // 我的零钱
        Intent intent = new Intent(mActivity, BackActivity.class);
        switch (v.getId()) {
            case R.id.ll:
                intent.putExtra("wallet",resultString);
                intent.putExtra("fragmentName", ChangeFragment.class.getName());
                break;
            case R.id.ll_3:
                intent.putExtra("fragmentName", BankListFragment.class.getName());
                break;
        }
        startActivity(intent);
    }

    private void getData() {
        HttpUtils http = new HttpUtils();
        RequestParams params = mActivity.app.getPostParams();
        http.send(HttpRequest.HttpMethod.POST, Net.MY_WALLET, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                mActivity.dialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mActivity.dialog.dismiss();
                resultString = responseInfo.result;
                setData();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.dialog.dismiss();
                mActivity.app.toast("网络链接失败，请稍后再试");
            }
        });
    }

    private void setData() {
        if(TextUtils.isEmpty(resultString)){
            return;
        }
        try {
            JSONObject result = new JSONObject(resultString);
            JSONObject data = XString.getJSONObject(result, JsonName.DATA);
            if (XString.getBoolean(result, JsonName.STATUS)) {
                if(XString.getInt(data,JsonName.IS_SET_UPPWD)!=0){
                    mActivity.finish();
                }else{
                    tv.setText(XString.getStr(data,"totalrevenue")+"元");
                    tv3.setText(XString.getStr(data,JsonName.BANK_CARD_SUM)+"张");
                }
            } else {
                mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                mActivity.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
