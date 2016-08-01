package com.angm.jike.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.angm.jike.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-1.
 * 引导界面  Activity
 *
 * @
 */
public class SplashActivity extends BaseActivity {


    @Bind(R.id.version_name)
    TextView versionName;

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
        ButterKnife.bind(this);
    }
}
