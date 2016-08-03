package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angm.jikeb.R;

import butterknife.Bind;

/**
 * Created by shd on 16-8-3.
 * 用于 带返回按钮的activity
 */
public abstract class BackActivity extends BaseActivity {


    @Bind(R.id.tv_toolbar_center)
    TextView tvToolbarCenter;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private View toolbarView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        toolbarView = LayoutInflater.from(this).inflate(R.layout.activity_back, null);
        initToolBar();
    }

    // TODO: 2016-08-03    对 toolbar 进行重新赋值
    public void initToolBar() {
        tvToolbarCenter.setText(getTitileStr());
    }

     // TODO: 2016-08-03   重新进行赋值
     abstract String getTitileStr();
}
