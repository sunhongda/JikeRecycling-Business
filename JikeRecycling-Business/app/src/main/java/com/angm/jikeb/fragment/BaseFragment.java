package com.angm.jikeb.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shd on 16-8-3.
 */
public abstract class BaseFragment extends Fragment {

//    BackActivity mActivity;
    protected Toolbar mToolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        mActivity = (BackActivity) activity;
//        mToolbar = mActivity.getmToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayout(), null);
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
    abstract protected int getFragmentLayout();
    abstract protected void initView();

}
