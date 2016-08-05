package com.buslink.busjie.driver.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.SpaceItemDecoration;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SwipeRefreshLayout;
import com.buslink.busjie.driver.viewholder.SimpleHolder;
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

public class AdListActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;
    @Bind(R.id.rv)
    RecyclerView rv;

    private int page = 0, pageSize = 10;
    private List<JSONObject> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adlist);
        ButterKnife.bind(this);
        app = MyApplication.getApp();
        initToolbar();
        initView();
        getListData();
    }

    private void initView() {
        srl.setLoadNoFull(true);
        srl.setColor(R.color.green, R.color.green_p, R.color.green, R.color.green_p);
        srl.setOnLoadListener(new SwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getListData();
            }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getListData();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new SpaceItemDecoration(16, 0));
        rv.setAdapter(adapter = new RecyclerView.Adapter<SimpleHolder>() {
            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final SimpleHolder holder = new SimpleHolder(LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.item_adlist, parent, false));
                holder.setTag(R.id.time1, R.id.time2, R.id.time3);
                holder.setTag(R.id.place1, R.id.place2);
                holder.setTag(R.id.valid);
                return holder;
            }

            @Override
            public void onBindViewHolder(SimpleHolder holder, int position) {
                holder.setIndex(position);
                JSONObject jo = list.get(position);
                holder.getTextView(R.id.time1).setText(DateUtils.getSimpleFormatTime(XString.getLong(jo, "addtime")));
                holder.getTextView(R.id.time2).setText(DateUtils.getSimpleFormatTime(XString.getLong(jo, "startdate")));
                holder.getTextView(R.id.time3).setText(DateUtils.getSimpleFormatTime(XString.getLong(jo, "enddate")));
                holder.getTextView(R.id.place1).setText(XString.getStr(jo, "scity"));
                holder.getTextView(R.id.place2).setText(XString.getStr(jo, "ecity"));
                int valid = XString.getInt(jo, "effective");
                switch (valid) {
                    case 0:
                        holder.getTextView(R.id.valid).setText("无效");
                        break;
                    case 1:
                        holder.getTextView(R.id.valid).setText("有效");
                        break;
                    case 2:
                        holder.getTextView(R.id.valid).setText("过期");
                        break;
                    case 3:
                        holder.getTextView(R.id.valid).setText("已接单");
                        break;
                }
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    private void initToolbar() {
        mToolbar.setTitle("历史发布");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getListData() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pagesize", pageSize + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.ADLIST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                srl.setLoading(false);
                srl.setRefreshing(false);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONArray adlist = XString.getJSONArray(data, "adlist");
                    if (adlist.length() == 0) {
                        if (page == 0) {
                            app.toast("暂时没有数据");
                        } else {
                            app.toast("没有更多数据");
                        }
                        return;
                    }
                    List<JSONObject> resList = XString.getList(data, "adlist");
                    if (page == 0) {
                        list.clear();
                        list.addAll(resList);
                        adapter.notifyDataSetChanged();
                    } else {
                        list.addAll(resList);
                        adapter.notifyItemRangeInserted(page, resList.size() - 1);
                    }
                    page = list.size();
                } else {
                    app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                srl.setLoading(false);
                srl.setRefreshing(false);
                app.toast("网络异常");
            }
        });
    }
}
