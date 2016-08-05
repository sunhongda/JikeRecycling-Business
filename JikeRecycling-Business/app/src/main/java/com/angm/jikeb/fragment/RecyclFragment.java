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
 * (首页ViewPager)
 */
public class RecyclFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_vp_recycl;
    }

    @Override
    protected void FragmentInitData() {

    }


}
