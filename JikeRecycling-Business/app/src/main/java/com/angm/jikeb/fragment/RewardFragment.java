package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.angm.jikeb.R;
import com.angm.jikeb.adapter.RewardAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/18.
 * 奖励活动界面
 */
public class RewardFragment extends LevelTwoFragment {
    @Bind(R.id.fragment_message_lv)
    ListView fragmentMessageLv;

    @Override
    public String getTitle() {
        return "奖励活动";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView() {
        RewardAdapter adapter = new RewardAdapter(getActivity());
        fragmentMessageLv.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
