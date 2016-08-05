package com.buslink.busjie.driver.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buslink.busjie.driver.base.DrawerActivity;

public abstract class LevelOneFragment extends Fragment {

    DrawerActivity activity;
    Toolbar mToolbar;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (DrawerActivity) activity;
        mToolbar = this.activity.getmToolbar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), null);
    }


    @Override
    public void onResume() {
        super.onResume();
        mToolbar.setTitle(getTitle());
        //activity.mContent = this;
    }

    protected abstract String getTitle();
    protected abstract int getLayout();
}
