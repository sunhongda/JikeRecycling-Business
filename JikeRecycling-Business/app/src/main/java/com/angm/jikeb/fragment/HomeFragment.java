package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.angm.jikeb.R;
import com.angm.jikeb.adapter.HomeAdapter;
import com.angm.jikeb.base.BackActivity;
import com.angm.jikeb.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-4.
 * 用于我Fragment (首页ViewPager)
 */
public class HomeFragment extends BaseFragment {
    final String[] title = new String[]{"钱包", "消息中心", "设置"};
    final int[] image = new int[]{R.mipmap.money, R.mipmap.message, R.mipmap.setting};
    @Bind(R.id.fragment_vp_home_lv)
    ListView fragmentVpHomeLv;
    @Bind(R.id.fragment_vp_home_image_name)
    ImageView fragmentVpHomeImage_Name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_vp_home;
    }

    @Override
    protected void FragmentInitData() {
        HomeAdapter homeAdapter = new HomeAdapter(getActivity(), title, image);
        fragmentVpHomeLv.setAdapter(homeAdapter);
         /*item跳转*/
        fragmentVpHomeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    activity.startFragment(BackActivity.class, MoneyBagFragment.class);
                } else if (i == 1) {
                    activity.startFragment(BackActivity.class, MessageFragment.class);
                } else if (i == 2) {
                    activity.startFragment(BackActivity.class, SettingFragment.class);
                }
            }
        });

        /*跳转个人资料界面*/
        fragmentVpHomeImage_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startFragment(BackActivity.class, PersonDataFragment.class);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
