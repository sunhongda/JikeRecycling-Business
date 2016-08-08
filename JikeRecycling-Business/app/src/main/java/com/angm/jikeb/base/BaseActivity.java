package com.angm.jikeb.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.angm.jikeb.manager.MyApplication;

import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-1.
 * activity 的最高抽象
 */
public abstract class BaseActivity extends AppCompatActivity {
    public MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        application = MyApplication.getApp();
        initData();
        initView();

    }


    public abstract int getLayout();


    public abstract void initView();


    public abstract void initData();

    @Override
    protected void onDestroy() {
        application.remove(this);
        ButterKnife.unbind(this);
        super.onDestroy();
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
