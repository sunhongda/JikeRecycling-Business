package com.buslink.busjie.driver.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.fragment.BaseFragment;
import com.buslink.busjie.driver.manager.MyApplication;

public class NoBackActivity extends BaseActivity {

    private Toolbar mToolbar;
    public MyApplication app;
    public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back);
        app = MyApplication.getApp();
        app.addActivity(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("努力加载中");
        dialog.setCancelable(true);
        String fragmentName = getIntent().getStringExtra("fragmentName");
        Bundle bundle = getIntent().getBundleExtra("fragmentBundle");
        initToolbar();
        try {
            initFragment(fragmentName, bundle);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
    }

    private void initFragment(String name, Bundle bundle) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class c = Class.forName(name);
        BaseFragment fragment = (BaseFragment) c.newInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.commit();
    }

    public Toolbar getmToolbar() {
        return mToolbar;
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
