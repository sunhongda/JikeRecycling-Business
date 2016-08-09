package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BackActivity;
import com.angm.jikeb.base.BaseFragment;
import com.angm.jikeb.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shd on 16-8-4.
 * 用于地图Fragment (首页ViewPager)
 */
public class FindFragment extends BaseFragment {

    @Bind(R.id.iv_1)
    ImageView iv1;
    @Bind(R.id.rl_find1)
    RelativeLayout rlFind1;
    @Bind(R.id.iv_2)
    ImageView iv2;
    @Bind(R.id.rl_find2)
    RelativeLayout rlFind2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_vp_find;
    }

    @Override
    protected void FragmentInitData() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.rl_find1, R.id.rl_find2})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.rl_find1:
                Log.i("   OnClick rl_find1");
                activity.startFragment(BackActivity.class,MapFragment.class);
                break;

            case R.id.rl_find2:
                Log.i("   OnClick rl_find2");

                break;

            default:
                break;
        }
    }
}
