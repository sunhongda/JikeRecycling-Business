package com.buslink.busjie.driver.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.util.MD5;
import com.buslink.busjie.driver.util.XString;
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

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class ChangeFragment extends LevelTwoFragment {
    private String resultString;
    private String pid;
    private String withdrawMoney;

    @ViewInject(R.id.tv)
    private TextView tv;
    @ViewInject(R.id.tv_1)
    private TextView tv1;

    @Override
    public String getTitle() {
        return "我的收入";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_change;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_change);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_change:
                        mActivity.startFragment(BackActivity.class, IncomeListFragment.class);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        resultString =activity.getIntent().getStringExtra("wallet");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
        setData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MyEvent e){
        if(TextUtils.equals(e.getTag(), "checkPass")){
            checkPass(e.getData());
        }
        if (TextUtils.equals(e.getTag(), "rechange")) {
            getData();
        }
    }

//    @OnClick({R.id.change_income_list})
//    private void onClick(View v) {
//        Intent intent = new Intent(mActivity, MyFragmentActivity.class);
//        switch (v.getId()) {
//            case R.id.change_income_list:
//                intent.putExtra("fragmentName", IncomeListFragment.class.getName());
//                break;
//        }
//        startActivity(intent);
//    }

    @OnClick(R.id.bt_1)
    private void toWithdrawals(View v){
        JSONObject jo = XString.getJSONObject(resultString);
        JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
        String account = XString.getStr(data, "account");
        if ("0".equals(withdrawMoney)) {
            mActivity.app.toast("您的可提现金额为0，不能提现");
            return;
        }
        if (TextUtils.isEmpty(account)) {
            mActivity.app.toast("您未绑定银行卡，请先添加银行卡");
            return;
        }
        mActivity.startFragment(BackActivity.class, CheckPassFragment.class);
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

    public void setData(){
        if(resultString==null){
            return;
        }
        try{
            JSONObject result=new JSONObject(resultString);
            JSONObject data = XString.getJSONObject(result, JsonName.DATA);
            if (XString.getBoolean(result, JsonName.STATUS)) {
                if(XString.getInt(data,JsonName.IS_SET_UPPWD)!=0){
                    mActivity.finish();
                }else{
                    tv.setText("¥"+XString.getStr(data,"totalrevenue"));
                    tv1.setText("¥"+XString.getStr(data,JsonName.MONEY_SUM));
                    withdrawMoney = XString.getStr(data,JsonName.MONEY_SUM);
                }
                pid = XString.getStr(data, JsonName.PID);
            } else {
                mActivity.app.toast(XString.getStr(data, JsonName.MSG));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void checkPass(String pass){
        HttpUtils http=new HttpUtils();
        RequestParams params=mActivity.app.getPostParams();
        params.addBodyParameter(JsonName.PWD, MD5.getMessageDigest(pass.getBytes()));
        http.send(HttpRequest.HttpMethod.POST, Net.VERIFICATION, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                mActivity.dialog.show();
                super.onStart();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mActivity.dialog.dismiss();
                LogUtils.d(responseInfo.result);
                JSONObject result=XString.getJSONObject(responseInfo.result);
                JSONObject data=XString.getJSONObject(result,JsonName.DATA);
                if(XString.getBoolean(result,JsonName.STATUS)){
                    Intent intent=new Intent();
                    intent.putExtra(JsonName.PID,pid);
                    intent.putExtra("chang",resultString);

                    mActivity.startFragment(BackActivity.class, WithdrawalsFragment.class, intent);
                }else {
                    mActivity.app.toast(XString.getStr(data,JsonName.MSG));
                }
            }
            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.dialog.dismiss();
                mActivity.app.toast("网络链接失败，请稍后再试！");
            }
        });
    }
}
