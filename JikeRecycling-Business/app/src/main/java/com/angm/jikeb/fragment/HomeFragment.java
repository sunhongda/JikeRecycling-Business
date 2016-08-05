package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseFragment;

/**
 * Created by shd on 16-8-4.
 * 用于地图Fragment (首页ViewPager)
 */
public class HomeFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {

        return R.layout.fragment_vp_home;
    }

    @Override
    protected int FragmentInitData() {
        return 0;
    }
}
