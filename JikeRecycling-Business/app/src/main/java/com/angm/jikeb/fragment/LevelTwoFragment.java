package com.angm.jikeb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.base.BackActivity;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/10/12.
 * 带返回箭头的fragment
 */
public abstract class LevelTwoFragment extends Fragment {
    public LevelTwoFragment() {
    }

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

        View inflate = inflater.inflate(getResLayout(), null);
        ButterKnife.bind(this, inflate);

        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mActivity.tvToolbarCenter.setText(getTitle());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    abstract public String getTitle();

    abstract protected int getResLayout();

    abstract protected void initView();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
