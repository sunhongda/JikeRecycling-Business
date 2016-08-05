package com.buslink.busjie.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.fragment.HomeFragment;
import com.buslink.busjie.driver.fragment.WebFragment;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.ProgressDlg;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

//登录
public class SignInActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.signin_mobile_edittext)
    EditText mMobile;
    @Bind(R.id.signin_captcha_edittext)
    EditText mCaptcha;
    @Bind(R.id.signin_verify_button)
    Button mVerifyBtn;
    @Bind(R.id.signin_start_button)
    Button mStartBtn;

    private ProgressDlg mProgressDialog;

    private DelayTimer timer;

    private boolean isWarnedToClose = false;

    @OnClick(R.id.signin_verify_button)
    void requireYZM() { // 获取验证码短信
        RequestParams messageParams = new RequestParams();
        messageParams.addQueryStringParameter("phone", mMobile.getText().toString());
        RequestManager.getIdentifyMessage(messageParams, new MessageCallBack());
    }

    @OnClick(R.id.signin_start_button)
    void start() {
        hideInputMethod();
        showProgressDialog("加载中");
        RequestParams signInParams = new RequestParams();
        signInParams.addQueryStringParameter("phone",  mMobile.getText().toString());
        signInParams.addQueryStringParameter("code",   mCaptcha.getText().toString());
        //这里入口
        RequestManager.signIn(signInParams, new SignInCallBack());
        //登录的时候
       // sendLogin();
    }
                private void sendLogin() {
                    AsyncHttpClient client = new AsyncHttpClient();
                    com.loopj.android.http.RequestParams params = MyHelper.getParams();
                    params.add(JsonName.OPERATIONTYPE,"1");//1进入2退出
                    //退出登录
                    client.post(getApplicationContext(), Net.LONINSTATEHANDLE, params, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            JSONObject res = XString.getJSONObject(responseString);
                            JSONObject data = XString.getJSONObject(res, JsonName.DATA);
                            if (XString.getBoolean(res, JsonName.STATUS)) {

//                    app.exit();
//                    activity.startFragment(NoBackActivity.class, LoginFragment.class);
                            } else {
                                // app.toast(XString.getStr(data, JsonName.MSG));

                            }

                        }
                    });
                }

                @OnClick(R.id.tv)
    void showXieyi() {
        Intent intent = new Intent();
        intent.putExtra("url", Net.LOGIN_SERVICE);
        startFragment(BackActivity.class, WebFragment.class, intent);
    }

    public void startFragment(Class<?> activity, Class<?> fragment, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("fragmentName", fragment.getName());
        intent.setClass(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        ButterKnife.bind(this);
        initToolbar();
        mMobile.addTextChangedListener(mMobileWatcher);
        mCaptcha.addTextChangedListener(mCaptchaWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isWarnedToClose) {
            finish();
        } else {
            isWarnedToClose = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWarnedToClose = false;
                }
            }, 2000);
        }
    }

    TextWatcher mMobileWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (timer != null) {
                timer.cancel();
                timer = null;
                mVerifyBtn.setText("验证");
            }
            mCaptcha.setText("");
            mVerifyBtn.setText("验证");
            mStartBtn.setEnabled(false);

            if (!TextUtils.isEmpty(s.toString()) && isMobileNum(mMobile.getText().toString())) {
                mVerifyBtn.setEnabled(true);
            } else {
                mVerifyBtn.setEnabled(false);
            }
        }
    };

    TextWatcher mCaptchaWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s.toString())
                    && isMobileNum(mMobile.getText().toString())
                    && isVerfyNum(mCaptcha.getText().toString())) {
                mStartBtn.setEnabled(true);
            } else {
                mStartBtn.setEnabled(false);
            }
        }
    };

    private void initToolbar() {
        mToolbar.setTitle("验证手机");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean isMobileNum(String mobiles) {
        if (TextUtils.isEmpty(mobiles))
            return false;
        Pattern p = Pattern.compile("^(1)\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    private boolean isVerfyNum(String verify) {
        if (TextUtils.isEmpty(verify))
            return false;
        Pattern p = Pattern.compile("^\\d{4}$");
        Matcher m = p.matcher(verify);
        return m.matches();
    }

    private void hideInputMethod() {
        InputMethodManager mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mImm.hideSoftInputFromWindow(mCaptcha.getWindowToken(), 0);
    }


    private void showProgressDialog(String msg) {
        mProgressDialog = new ProgressDlg(this, msg, "");
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    class MessageCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            timer = new DelayTimer(60000, 1000);
            timer.start();
        }

        @Override
        public void onFailure(HttpException e, String s) {
            Toast.makeText(SignInActivity.this, "您的网络不给力", Toast.LENGTH_SHORT).show();
        }
    }

    class SignInCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            dismissProgressDialog();
            try {
                JSONObject jo1 = new JSONObject(responseInfo.result);
                boolean status = jo1.getBoolean("status");
                JSONObject data = jo1.getJSONObject("data");
                if (status) { // 验证码正确
                    String state = XString.getStr(data, "state");
                    String uid = XString.getStr(data, "uid");
                    String cid = XString.getStr(data, "cid");
                    UserHelper.getInstance().setUid(uid);
                    UserHelper.getInstance().setCid(cid);
                    UserHelper.getInstance().setPhone(mMobile.getText().toString());
                    if ("1".equals(state)) { // 首页
//                        Intent intent = new Intent(SignInActivity.this, DriverHomeActivity.class);
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.putExtra("fragmentName", HomeFragment.class.getName());
                        startActivity(intent);
//                        Intent intent = new Intent(SignInActivity.this, NoBackActivity.class);
//                        intent.putExtra("fragmentName", CarPhotoFragment.class.getName());
//                        startActivity(intent);
                        //startActivity(new Intent(SignInActivity.this, DriverHomeActivity.class));
                    } else if ("2".equals(state)) { // 注册界面
                        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                    }
                    finish();
                } else { // 验证码错误
                    String msg = data.getString("msg");
                    Toast.makeText(SignInActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(SignInActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            Toast.makeText(SignInActivity.this, "您的网络不给力", Toast.LENGTH_SHORT).show();
            dismissProgressDialog();
        }
    }

    public class DelayTimer extends CountDownTimer {
        public DelayTimer(long millisInFuture, long countDownInterval) {//倒计时间隔
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            resetVerifyButton();
        }
        @SuppressWarnings("deprecation")
        @Override
        public void onTick(long millisUntilFinished) {
            mVerifyBtn.setEnabled(false);
            mVerifyBtn.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    private void resetVerifyButton() {
        mVerifyBtn.setEnabled(true);
        mVerifyBtn.setText("验证");
    }
}




















