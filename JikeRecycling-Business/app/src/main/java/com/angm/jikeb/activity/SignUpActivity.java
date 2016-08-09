package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;

import butterknife.Bind;


//注册页
public class SignUpActivity extends BaseActivity {


    @Bind(R.id.signin_mobile_edittext)
    EditText signinMobileEdittext;
    @Bind(R.id.signin_verify_button)
    Button signinVerifyButton;
    @Bind(R.id.signin_captcha_edittext)
    EditText signinCaptchaEdittext;
    @Bind(R.id.ed_setpwd)
    EditText edSetpwd;
    @Bind(R.id.ed_yespwd)
    EditText edYespwd;
    @Bind(R.id.signin_start_button)
    Button signinStartButton;
    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.aiplay_view)
    ImageView aiplayView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public int getLayout() {
        return R.layout.sign_up_bus;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }


}
