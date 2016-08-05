package com.buslink.busjie.driver.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.buslink.busjie.driver.manager.MyApplication;

public class BaseActivity extends AppCompatActivity {
    public MyApplication app;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getApp();
        if(app==null){
            finish();
        }
        app.addActivity(this);
        dialog= new ProgressDialog(this);
        dialog.setMessage("正在努力加载中...");
        dialog.setCancelable(true);
    }
    public MyApplication application;

    @Override
    protected void onDestroy() {
        app.remove(this);
        application.remove(this);
        super.onDestroy();
    }

    public void startFragment(Class<?> activity,Class<?> fragment,Intent intent){
        if(intent==null){
            intent=new Intent();
        }
        intent.putExtra("fragmentName",fragment.getName());
        intent.setClass(this, activity);
        startActivity(intent);
    }
    public void startFragment(Class<?> activity,Class<?> fragment){
        startFragment(activity,fragment,null);
    }
}
