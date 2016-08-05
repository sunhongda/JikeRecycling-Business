package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2015/11/20.
 */
public class WebFragment extends LevelTwoFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView wv=new WebView(inflater.getContext());
        wv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings setting = wv.getSettings();
        setting.setJavaScriptEnabled(true);//支持js
        wv.loadUrl(mActivity.getIntent().getStringExtra("url"));
        return wv;
    }

    @Override
    public String getTitle() {
        return "巴士互联";
    }

    @Override
    protected int getResLayout() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
