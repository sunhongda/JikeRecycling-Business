package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.angm.jikeb.R;
import com.angm.jikeb.adapter.MessageAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/10.
 * 消息界面（Fragment）
 */
public class MessageFragment extends LevelTwoFragment {
    @Bind(R.id.fragment_message_lv)
    ListView fragmentMessageLv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "消息";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initView() {
        MessageAdapter adapter = new MessageAdapter(getActivity());
        fragmentMessageLv.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
