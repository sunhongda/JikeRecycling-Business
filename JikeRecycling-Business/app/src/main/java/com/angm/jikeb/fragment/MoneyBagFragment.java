package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BackActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/10.
 * 钱包界面（Fragment）
 */
public class MoneyBagFragment extends LevelTwoFragment {
    @Bind(R.id.fragment_money_right_go)
    ImageView fragmentMoneyRightGo;

    @Override
    public String getTitle() {
        return "即客支付";
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
    @Override
    protected int getResLayout() {
        return R.layout.fragment_money_bag;
    }

    @Override
    protected void initView() {
        fragmentMoneyRightGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.startFragment(BackActivity.class,ExternalFragment.class);
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
