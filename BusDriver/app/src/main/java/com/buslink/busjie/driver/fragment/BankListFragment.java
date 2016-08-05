package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.BankListAdapter;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.MD5;
import com.buslink.busjie.driver.util.XString;
import com.daimajia.swipe.util.Attributes;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class BankListFragment extends LevelTwoFragment {

    private String resultString;
    private BankListAdapter mAdapter;
    private ArrayList<JSONObject> mDataSet;

    @ViewInject(R.id.recycler_view)
    private RecyclerView recyclerView;


    @Override
    public String getTitle() {
        return "银行卡";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_banklist;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_banklist);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_banklist:
                        mActivity.startFragment(BackActivity.class, CheckPassFragment.class);
                        break;
                }
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        boolean clickable = mActivity.getIntent().getBooleanExtra("clickable", false);
        mDataSet = new ArrayList<>();
        mAdapter = new BankListAdapter(mActivity, mDataSet);
        mAdapter.setMode(Attributes.Mode.Single);
        if (clickable) {
            mAdapter.setClickable();
        }
        recyclerView.setAdapter(mAdapter);
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
        getData();
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if (e.getTag().equals(EventName.AddBank)) {
            getData();
        } else if(e.getTag().equals("checkPass")){
            checkPass(e.getData());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getData() {
        RequestManager.bankList(new BankListRequestCallBack());
    }

    private void setData() {

    }

    class BankListRequestCallBack extends RequestCallBack<String> {

        @Override
        public void onStart() {
            mActivity.dialog.show();
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            mActivity.dialog.dismiss();
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, "data");
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONArray walletlst = XString.getJSONArray(data, "walletlst");
                    if (walletlst != null && walletlst.length() > 0) {
                        mAdapter.setData(walletlst);
                    }
                    resultString = responseInfo.result;
                    setData();
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.dialog.dismiss();
            mActivity.app.toast("您的网络不给力");
        }
    }

    public void checkPass(String pass){
        HttpUtils http=new HttpUtils();
//        JSONObject res=XString.getJSONObject(resultString);
//        JSONObject wllet=XString.getJSONObject(res,JsonName.DATA);
        RequestParams params=mActivity.app.getPostParams();
//        String modulus=XString.getStr(wllet, JsonName.MODULUS);
//        String public_exponent=XString.getStr(wllet, JsonName.PUBLIC_EXPONENT);
//        params.addBodyParameter(JsonName.MODULUS,modulus);
//        params.addBodyParameter(JsonName.PUBLIC_EXPONENT,public_exponent);
        params.addBodyParameter(JsonName.PWD, MD5.getMessageDigest(pass.getBytes()));
        http.send(HttpRequest.HttpMethod.POST, Net.VERIFICATION, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                mActivity.dialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mActivity.dialog.dismiss();
                JSONObject result = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                if (XString.getBoolean(result, JsonName.STATUS)) {
                    mActivity.startFragment(BackActivity.class,AddBankFragment.class);
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
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
