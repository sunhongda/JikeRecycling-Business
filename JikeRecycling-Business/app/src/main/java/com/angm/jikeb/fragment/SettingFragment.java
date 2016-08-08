package com.angm.jikeb.fragment;

import android.os.Bundle;

import com.angm.jikeb.R;

/**
 * Created by shd on 16-8-8.
 */
public class SettingFragment extends LevelTwoFragment {

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public String getTitle() {
        return "设置";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {

    }
}
