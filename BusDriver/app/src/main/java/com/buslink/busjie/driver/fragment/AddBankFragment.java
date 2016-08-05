package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.CitySelectDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class AddBankFragment extends LevelTwoFragment {

    @ViewInject(R.id.addbank_bank_tv)
    TextView mBank;
    @ViewInject(R.id.addbank_card_number)
    EditText mCardNumber;
    @ViewInject(R.id.addbank_city)
    TextView mCity;
    @ViewInject(R.id.addbank_branch)
    EditText mBranch;
    @ViewInject(R.id.addbank_name)
    EditText mName;
    @ViewInject(R.id.addbank_id_card)
    EditText mIdCard;
    @ViewInject(R.id.addbank_phone)
    EditText mPhone;
    private int btype;

    @Override
    public String getTitle() {
        return "添加银行卡";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_addbank;
    }

    @Override
    protected void initView() {
        mCity.setTag(new CitySelectDialog(mActivity).setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
            @Override
            public void onSelect(String province, String city) {
                mCity.setText(AppUtils.getAddress(province,city,""));
            }
        }));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @OnClick({
            R.id.addbank_bank_tv,
            R.id.addbank_city,
            R.id.addbank_submit})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.addbank_bank_tv:
                mActivity.startFragment(BackActivity.class, ChooseBankFragment.class);
                break;
            case R.id.addbank_city:
//                Intent intent2 = new Intent(mActivity, ChooseProvinceActivity.class);
//                startActivity(intent2);
                selectLocation(mCity);
                break;
            case R.id.addbank_submit:
                if (verify()) {
                    addBank();
                }
                break;
        }
    }

    @OnClick(R.id.agreement)
    private void agreement(View v) {
        Intent intent = new Intent();
        intent.putExtra("url", Net.E_COMMERCE);
        mActivity.startFragment(BackActivity.class, WebFragment.class, intent);

    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if (e.getTag().equals(EventName.ChooseBank)) {
            mBank.setText(e.getData());
            btype = (Integer) e.getExtra("btype");
        } else if (EventName.BankCity.equals(e.getTag())) {
            mCity.setText(e.getData());
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addBank() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("bank", mBank.getText().toString());
        params.addBodyParameter("account", mCardNumber.getText().toString());
        params.addBodyParameter("name", mName.getText().toString());
        params.addBodyParameter("card", mIdCard.getText().toString());
        params.addBodyParameter("phone", mPhone.getText().toString());
        params.addBodyParameter("btype", btype + "");
        params.addBodyParameter("openbank", mBranch.getText().toString());
        params.addBodyParameter("city", mCity.getText().toString());
        params.addBodyParameter("company", UserHelper.getInstance().getCompany());
        RequestManager.addBank(params, new AddBankRequestCallBack());
    }

    private boolean verify() {
        if (TextUtils.isEmpty(mBank.getText().toString())) {
            mActivity.app.toast("请选择银行");
            return false;
        }
        if (TextUtils.isEmpty(mCardNumber.getText().toString())) {
            mActivity.app.toast("请输入卡号");
            mCardNumber.requestFocus();
            return false;
        }
        if (mCardNumber.getText().toString().length() < 16) {
            mActivity.app.toast("卡号不能低于16位");
            mCardNumber.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mCity.getText().toString())) {
            mActivity.app.toast("请选择城市");
            return false;
        }
        if (TextUtils.isEmpty(mBranch.getText().toString())) {
            mActivity.app.toast("请输入银行卡支行");
            mBranch.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mName.getText().toString())) {
            mActivity.app.toast("请输入姓名");
            mName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(mIdCard.getText().toString())) {
            mActivity.app.toast("请输入身份证号");
            return false;
        }
        if (!isIdCard(mIdCard.getText().toString())) {
            mActivity.app.toast("身份证号错误，请重新填写");
            return false;
        }
        if (TextUtils.isEmpty(mPhone.getText().toString())) {
            mActivity.app.toast("请填写手机号");
            return false;
        }
        return true;
    }

    private boolean isIdCard(String idcard) {
        if (idcard.length() == 15) {
            String regular = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
            return idcard.matches(regular);
        } else if (idcard.length() == 18) {
            String regular = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
            return idcard.matches(regular);
        } else {
            return false;
        }
    }

    class AddBankRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            LogUtils.d(responseInfo.result);
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, "data");
                if (XString.getBoolean(jo, "status")) {
                    mActivity.app.toast("添加银行卡成功");
                    // TODO EVENTBUS
                    MyEvent e = new MyEvent(EventName.AddBank);
                    EventBus.getDefault().post(e);
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
            mActivity.app.toast("您的网络不给力");
        }
    }

    private void selectLocation(final TextView textView) {
        CitySelectDialog dialog = (CitySelectDialog) textView.getTag();
        dialog.show();
    }
}





















