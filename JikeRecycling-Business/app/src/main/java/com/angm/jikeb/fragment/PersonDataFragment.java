package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.R;

/**
 * Created by Mr.Gao on 2016/8/10.
 * 个人资料界面
 */
public class PersonDataFragment extends LevelTwoFragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "个人资料";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_person_data;
    }

    @Override
    protected void initView() {

    }
}
