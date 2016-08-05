package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.manager.MyApplication;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by yanlong.luo on 2015/8/28.
 */
public class AboutFragment extends LevelTwoFragment {

//    @Bind(R.id.about_version)
//    TextView mVersion;

    @Override
    public String getTitle() {
        return "巴士互联";
    }

    @Override
    protected int getResLayout() {
        return R.layout.about_layout;
    }

    @Override
    protected void initView() {
      //  mVersion.setText("巴士互联V" + MyApplication.getVersionName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }
}
