package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.util.MD5;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.InputListener;
import com.buslink.busjie.driver.view.PassInputView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/9/15.
 * 设置密码
 */
public class PassInputFragment extends LevelTwoFragment {
    @ViewInject(R.id.piv)
    private PassInputView piv;

    @Override
    public String getTitle() {
        return "设置密码";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_pass_input;
    }

    public void initView(){
        piv.setListener(new InputListener() {
            @Override
            public void onErr() {

            }

            @Override
            public void onCancle() {
                mActivity.finish();
            }

            @Override
            public void onTrue(String tv) {
                setPass(tv);
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setPass(String pass){
        HttpUtils http=new HttpUtils();
        RequestParams params=mActivity.app.getPostParams();
        params.addBodyParameter(JsonName.PWD, MD5.getMessageDigest(pass.getBytes()));
        http.send(HttpRequest.HttpMethod.POST, Net.SET_UP_PWD, params, new RequestCallBack<String>() {
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
                    JSONObject result = new JSONObject(responseInfo.result);
                    JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                    if (XString.getBoolean(result, JsonName.STATUS)) {
                        Intent intent = new Intent(mActivity, BackActivity.class);
                        intent.putExtra("wallet",responseInfo.result);
                        intent.putExtra("fragmentName", PurseFragment.class.getName());
                        startActivity(intent);
                        mActivity.finish();
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
            }
        });
    }
}
