package com.angm.jikeb.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.angm.jikeb.R;
import com.angm.jikeb.adapter.MyRecycAdapter;
import com.angm.jikeb.base.BaseFragment;
import com.angm.jikeb.view.RecyclerViewHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shd on 16-8-4.
 * (首页ViewPager)
 */
public class RecyclFragment extends BaseFragment {

    @Bind(R.id.rv_main)
    RecyclerView rvMain;
    @Bind(R.id.sr_layout)
    SwipeRefreshLayout srLayout;
    @Bind(R.id.my_recycler_header)
    RecyclerViewHeader myRecyclerHeader;

    private List<String> data = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_vp_recycl;
    }

    @Override
    protected void FragmentInitData() {
        initSwipeLayout();
        initRecyclView();
    }

    /**
     * 下拉刷新布局
     */
    private void initSwipeLayout() {
        srLayout.setColorSchemeResources(R.color.green,
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        srLayout.setSize(SwipeRefreshLayout.LARGE);
        srLayout.setProgressViewEndTarget(true, 160);
        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        srLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        myRecyclerHeader.setupHeader(rvMain);
        myRecyclerHeader.setOnScrollTopListener(new RecyclerViewHeader.OnScrollTopListener() {
            @Override
            public void onTop(boolean isTop) {
                srLayout.setEnabled(isTop);
            }
        });
    }

    /**
     * 初始化RecycView 可用于（listview griadView等）
     */
    private void initRecyclView() {

        rvMain.setLayoutManager(new LinearLayoutManager(activity));
        rvMain.setAdapter(new MyRecycAdapter(activity, data));
        //设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());
         /*关联*/
        myRecyclerHeader.attachTo(rvMain);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
