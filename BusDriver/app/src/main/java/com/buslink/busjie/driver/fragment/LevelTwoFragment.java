package com.buslink.busjie.driver.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buslink.busjie.driver.base.BackActivity;

/**
 * Created by Administrator on 2015/10/12.
 * 带返回箭头的fragment
 */
public abstract class LevelTwoFragment extends Fragment {

    BackActivity mActivity;
    protected Toolbar mToolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (BackActivity) activity;
        mToolbar = mActivity.getmToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getResLayout(), null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mToolbar.setTitle(getTitle());
    }

    abstract public String getTitle();
    abstract protected int getResLayout();
    abstract protected void initView();

}
