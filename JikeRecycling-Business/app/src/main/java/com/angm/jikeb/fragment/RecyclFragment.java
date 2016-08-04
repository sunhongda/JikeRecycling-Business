package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.R;

/**
 * Created by shd on 16-8-4.
 */
public class RecyclFragment extends BaseFragment  {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "回收";
    }

    @Override
    protected int getFragmentLayout() {
        return  R.layout.fragment_vp_home;
    }

    @Override
    protected void initView() {

    }
}
