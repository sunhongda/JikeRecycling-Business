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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseFragment;

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
/*    @Bind(R.id.tv_empty)
    TextView tvEmpty;
    @Bind(R.id.no_trip)
    LinearLayout noTrip;*/
    @Bind(R.id.progressbar)
    ProgressBar progressbar;

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
        srLayout.setProgressViewEndTarget(true, 100);
        srLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    /**
     * 初始化RecycView 可用于（listview griadView等）
     */
    private void initRecyclView() {
        rvMain.setLayoutManager(new LinearLayoutManager(activity));
        rvMain.setAdapter(new MyRecycAdapter());
        //设置Item增加、移除动画
        rvMain.setItemAnimator(new DefaultItemAnimator());
    }


    // RecycView 适配器
    public class MyRecycAdapter extends RecyclerView.Adapter<MyRecycAdapter.MyViewHolder> {

        public MyRecycAdapter() {
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(activity).inflate(R.layout.recycview_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tvName.setText("测试");
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_name)
            TextView tvName;

            public MyViewHolder(View view) {
                super(view);
                ButterKnife.bind(view);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
