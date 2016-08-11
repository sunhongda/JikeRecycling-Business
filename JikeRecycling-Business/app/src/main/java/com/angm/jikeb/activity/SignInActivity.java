package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;


//登录
public class SignInActivity extends BaseActivity {



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
    }

    @Override
    public void initData() {

    }
}




















