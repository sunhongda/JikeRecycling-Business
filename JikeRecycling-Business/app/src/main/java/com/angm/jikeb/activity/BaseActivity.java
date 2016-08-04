package com.angm.jikeb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.angm.jikeb.manager.MyApplication;

import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-1.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public MyApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getLayout());
        app = MyApplication.getApp();
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
//        app.remove(this);
        ButterKnife.unbind(this);

    }

    public void startFragment(Class<?> activity, Class<?> fragment, Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("fragmentName", fragment.getName());
        intent.setClass(this, activity);
        startActivity(intent);
    }

    public void startFragment(Class<?> activity, Class<?> fragment) {
        startFragment(activity, fragment, null);
    }
}
