package com.angm.jikeb.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BackActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-8.
 * 设置界面
 */
public class SettingFragment extends LevelTwoFragment {

    @Bind(R.id.fragment_setting_feedback)
    AutoRelativeLayout fragmentSettingFeedback;
    @Bind(R.id.fragment_setting_service)
    AutoRelativeLayout fragmentSettingService;
    @Bind(R.id.fragment_setting_back_login)
    Button fragmentSettingBackLogin;

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
                mActivity.startFragment(BackActivity.class, ServiceFragment.class);
            }
        });
        fragmentSettingBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                View back = View.inflate(getActivity(), R.layout.fragment_back_login, null);
                Button btn_yes = (Button) back.findViewById(R.id.fragment_back_login_yes);
                Button btn_no = (Button) back.findViewById(R.id.fragment_back_login_no);
                final AlertDialog ad = ab.create();
                ad.setView(back);
                ad.show();
                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       getActivity().finish();
                    }
                });
                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ad.dismiss();
                    }
                });

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
