package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-1.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getLayout());
        initView();
        initData();
         //初始化 註解工具
        ButterKnife.bind(this);
    }

    public abstract int getLayout();

    public abstract void initView();

    public abstract void initData();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

    }
}
