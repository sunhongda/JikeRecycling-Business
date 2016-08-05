package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.WithdrawListAdapter;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SwipeRefreshLayout;
import com.buslink.busjie.driver.view.SwipeRefreshLayout.OnLoadListener;
import com.buslink.busjie.driver.view.SwipeRefreshLayout.OnRefreshListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WithdrawListFragment extends LevelTwoFragment implements OnRefreshListener, OnLoadListener {

    @Bind(R.id.withdraw_swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @Bind(R.id.withdraw_list)
    ListView mListView;

    List<JSONObject> mList;
    WithdrawListAdapter mAdapter;

    private int page = 0;

    @Override
    public String getTitle() {
        return "提现记录";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_withdraw_list;
    }

    @Override
    protected void initView() {
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setOnLoadListener(this);
        mSwipeLayout.setColor(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setMode(SwipeRefreshLayout.Mode.BOTH);
        mSwipeLayout.setLoadNoFull(true);

        mList = new ArrayList<>();
        mAdapter = new WithdrawListAdapter(mActivity);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    @Override
    public void onLoad() {
        getData();
    }

    @Override
    public void onRefresh() {
        page = 0;
        getData();
    }

    private void getData() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pagesize", "10");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.WITHDRAWLIST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mSwipeLayout.setRefreshing(false);
                mSwipeLayout.setLoading(false);
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, "data");
                if (XString.getBoolean(jo, "status")) {
                    JSONArray array = XString.getJSONArray(data, "walletlst");
                    if (array != null && array.length() > 0) {
                        mAdapter.addList(array);
                        page = page + array.length();
                    } else {
                        if (page == 0) {
                            mActivity.app.toast("暂时没有数据");
                        } else {
                            mActivity.app.toast("没有更多数据");
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.app.toast("您的网络不给力");
                mSwipeLayout.setRefreshing(false);
                mSwipeLayout.setLoading(false);
            }
        });
    }
}
