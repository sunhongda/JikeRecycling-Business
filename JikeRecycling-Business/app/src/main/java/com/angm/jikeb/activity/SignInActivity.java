package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;

import butterknife.Bind;


//登录
public class SignInActivity extends BaseActivity {


    @Bind(R.id.signin_mobile_edittext)
    EditText signinMobileEdittext;
    @Bind(R.id.signin_pwd_edittext)
    EditText signinPwdEdittext;
    @Bind(R.id.signin_chechkbox)
    CheckBox signinChechkbox;
    @Bind(R.id.signin_start_button)
    Button signinStartButton;
    @Bind(R.id.toolbar_signin_activity)
    Toolbar toolbarTop;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        initToolbar();
    }

    @Override
    public int getLayout() {
        return R.layout.sign_in;
    }

    @Override
    public void initView() {

    }
    private void initToolbar() {
        toolbarTop.setTitle("登录");
        toolbarTop.setNavigationIcon(null);
    }

    @Override
    public void initData() {

    }
}




















