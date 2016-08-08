package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by shd on 16-8-1.
 * 引导界面  Activity
 */
public class SplashActivity extends BaseActivity {
    @Bind(R.id.version_name)
    TextView versionName;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
     }
}
