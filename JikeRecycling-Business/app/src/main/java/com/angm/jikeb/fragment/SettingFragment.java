package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BackActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-8.
 */
public class SettingFragment extends LevelTwoFragment {

    @Bind(R.id.fragment_setting_feedback)
    AutoRelativeLayout fragmentSettingFeedback;
    @Bind(R.id.fragment_setting_service)
    AutoRelativeLayout fragmentSettingService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
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
        fragmentSettingFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startFragment(BackActivity.class, FeedbackFragment.class);
            }
        });
        fragmentSettingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startFragment(BackActivity.class,ServiceFragment.class);
            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
