package com.angm.jikeb.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.fragment.LevelTwoFragment;
import com.angm.jikeb.manager.MyApplication;

import butterknife.Bind;

/**
 * Created by shd on 16-8-3.
 * 用于带返回按钮的activity 用于启动带箭头的fragment
 */
public class BackActivity extends BaseActivity {


    public MyApplication app;
    public ProgressDialog dialog;
    @Bind(R.id.tv_toolbar_center)
    public TextView tvToolbarCenter;
    @Bind(R.id.toolbarTop)
    Toolbar mToolbar;
    @Bind(R.id.content_frame)
    FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_back;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        app = MyApplication.getApp();
        app.addActivity(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("努力加载中");
        dialog.setCancelable(true);
        String fragmentName = getIntent().getStringExtra("fragmentName");
        Bundle bundle = getIntent().getBundleExtra("fragmentBundle");
        initToolbar();


        try {
            if (!TextUtils.isEmpty(fragmentName) && !TextUtils.isEmpty(fragmentName)) {
                initFragment(fragmentName, bundle);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        mToolbar.setNavigationIcon(R.mipmap.back);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.mipmap.back);
    }

    private void initFragment(String name, Bundle bundle) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class c = Class.forName(name);
        LevelTwoFragment fragment = (LevelTwoFragment) c.newInstance();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
//      ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.commit();
    }

    public Toolbar getmToolbar() {
        return mToolbar;
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
