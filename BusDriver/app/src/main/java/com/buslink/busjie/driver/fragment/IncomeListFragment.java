package com.buslink.busjie.driver.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.IncomeListAdapter;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SwipeRefreshLayout;
import com.buslink.busjie.driver.view.SwipeRefreshLayout.OnLoadListener;
import com.buslink.busjie.driver.view.SwipeRefreshLayout.OnRefreshListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IncomeListFragment extends LevelTwoFragment implements OnRefreshListener, OnLoadListener {

    @ViewInject(R.id.income_swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @ViewInject(R.id.income_list)
    ListView mListView;

    private List<JSONObject> mIncomeList;
    private IncomeListAdapter mAdapter;

    private int page = 0;

    private String resultString;

    @Override
    public String getTitle() {
        return "收入明细";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_income_list;
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

        mIncomeList = new ArrayList<>();
        mAdapter = new IncomeListAdapter(mActivity, mIncomeList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
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
        params.addBodyParameter(JsonName.MTYPE,"1");//设备类型1android 2ios
        RequestManager.incomeList(params, new IncomeListRequestCallBack());
    }

    class IncomeListRequestCallBack extends RequestCallBack<String> {

        @Override
        public void onStart() {
            mActivity.dialog.show();
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            mActivity.dialog.dismiss();
            LogUtils.d(responseInfo.result);
            mSwipeLayout.setLoading(false);
            mSwipeLayout.setRefreshing(false);
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, "data");
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONArray incomeArray = XString.getJSONArray(data, "walletlst");
                    if (incomeArray != null && incomeArray.length() > 0) {
                        if (page == 0) {
                            mIncomeList.clear();
                        }
                        for (int i = 0; i < incomeArray.length(); i++) {
                            JSONObject income = incomeArray.getJSONObject(i);
                            mIncomeList.add(income);
                        }
                        page = page + incomeArray.length();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        if (page == 0) {
                            mActivity.app.toast("暂时没有数据");
                        } else {
                            mActivity.app.toast("没有更多数据");
                        }
                    }
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mActivity.app.toast("数据错误");
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.dialog.dismiss();
            mSwipeLayout.setLoading(false);
            mSwipeLayout.setRefreshing(false);
            mActivity.app.toast("您的网络不给力");
        }
    }
}



















