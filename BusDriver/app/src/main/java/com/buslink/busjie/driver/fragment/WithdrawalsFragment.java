package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class WithdrawalsFragment extends LevelTwoFragment {
    private String pid,chang,money,bid;

    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.tv_1)
    TextView tv1;
    @Bind(R.id.tv_2)
    TextView tv2;
    @Bind(R.id.tv_3)
    TextView tv3;
    @Bind(R.id.et)
    EditText et;
    @Bind(R.id.iv)
    ImageView iv;

    @Override
    public String getTitle() {
        return "零钱提现";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_withdrawals;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_withdrawals);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_tixian:
                        mActivity.startFragment(BackActivity.class, WithdrawListFragment.class);
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
        Intent intent=mActivity.getIntent();
        pid=intent.getStringExtra(JsonName.PID);
        chang=intent.getStringExtra("chang");
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
        displayData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if (EventName.ChooseMyBank.equals(e.getTag())) {
            JSONObject c = XString.getJSONObject(chang);
            JSONObject info = XString.getJSONObject(c,JsonName.DATA);
            JSONObject item = (JSONObject) e.getExtra("item");
            XString.put(info, "btype", XString.getStr(item, "btype"));
            XString.put(info,JsonName.BANK,XString.getStr(item,JsonName.BANK));
            XString.put(info,JsonName.BID,XString.getStr(item,JsonName.BID));
            XString.put(info,JsonName.ACCOUNT,XString.getStr(item,JsonName.ACCOUNT));
            XString.put(c,JsonName.DATA,info);
            chang=c.toString();
            displayData();
        }
    }

    @OnClick(R.id.ll) void chooseBank() {
        Intent intent = new Intent();
        intent.putExtra("clickable", true);
        mActivity.startFragment(BackActivity.class, BankListFragment.class, intent);
    }

    @OnClick(R.id.bt) void post() {
        if (verify()) {
            HttpUtils http = new HttpUtils();
            RequestParams params = mActivity.app.getPostParams();
            params.addBodyParameter(JsonName.MONEY, money);
            params.addBodyParameter(JsonName.BID, bid);
            params.addBodyParameter(JsonName.PID, pid);
            http.send(HttpRequest.HttpMethod.POST, Net.APPLY_FOR, params, new RequestCallBack<String>() {
                @Override
                public void onStart() {
                    super.onStart();
                    mActivity.dialog.show();
                }

                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    mActivity.dialog.dismiss();
                    LogUtils.d(responseInfo.result);
                    try {
                        JSONObject result=new JSONObject(responseInfo.result);
                        JSONObject data= XString.getJSONObject(result, JsonName.DATA);
                        if(XString.getBoolean(result,JsonName.STATUS)){
                            if(XString.getInt(data,JsonName.IS_CASH)!=1){
                                mActivity.app.toast(XString.getStr(data,JsonName.MSG));
                            }else{
                                mActivity.app.toast("成功提交提现申请");
                                EventBus.getDefault().post(new MyEvent("rechange"));
                            }
                            mActivity.finish();
                        }else{
                            mActivity.app.toast(XString.getStr(data,JsonName.MSG));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    LogUtils.d(s, e);
                    mActivity.dialog.dismiss();
                    mActivity.app.toast("网络链接失败，请稍后再试！");
                }
            });
        }
    }

    public void displayData(){
        JSONObject info=XString.getJSONObject(XString.getJSONObject(chang),JsonName.DATA);
        tv.setText(XString.getStr(info,JsonName.BANK));
        String account=XString.getStr(info,JsonName.ACCOUNT);
        if(!TextUtils.isEmpty(account)&&account.length()>4){
            tv1.setText("尾号"+account.substring(account.length()-4));
        }
        String btype = XString.getStr(info, "btype");
        if (!TextUtils.isEmpty(btype)) {
            Resources res = getResources();
            TypedArray bankIcons = res.obtainTypedArray(R.array.bank_icons);
            iv.setImageDrawable(bankIcons.getDrawable(Integer.valueOf(btype)));
        }
        tv3.setText("可转出余额"+XString.getDouble(info,JsonName.MONEY_SUM)+"元");
    }

    private boolean verify() {
        JSONObject info=XString.getJSONObject(XString.getJSONObject(chang),JsonName.DATA);
        money=et.getText().toString();
        bid=XString.getStr(info,JsonName.BID);
        if(TextUtils.isEmpty(bid) || "0".equals(bid)){
            mActivity.app.toast("请选择银行卡");
            return false;
        }
        if (TextUtils.isEmpty(money)) {
            et.setError("输入金额");
            return false;
        }
        if ("0".equals(money)) {
            et.setError("金额不能为0");
            return false;
        }
        if(Integer.parseInt(money)>200000){
            et.setError("单笔不能超过200000元");
            return false;
        }
        if (Integer.parseInt(money)>XString.getDouble(info, JsonName.MONEY_SUM)) {
            et.setError("最大提现金额为" + XString.getDouble(info, JsonName.MONEY_SUM));
            return false;
        }
        return true;
    }
}
