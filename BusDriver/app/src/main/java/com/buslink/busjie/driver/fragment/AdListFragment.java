package com.buslink.busjie.driver.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/14.
 * 广告列表
 */
public class AdListFragment extends LevelTwoFragment {

    private int page = 0, pageSize = 10;
    private List<JSONObject> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @ViewInject(R.id.srl)
    private SwipeRefreshLayout srl;
    @ViewInject(R.id.rv)
    private RecyclerView rv;

    @Override
    public String getTitle() {
        return "发布历史";
    }

    @Override
    protected void initView() {
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

        rv.setLayoutManager(new LinearLayoutManager(mActivity));
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
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setMessage("删除这条广告吗？");
                        builder.setNegativeButton("取消", null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject jo = list.get(holder.getIndex());
                                RequestParams params = new RequestParams();
                                params.addBodyParameter("version", MyApplication.getVersionCode());
                                params.addBodyParameter("cfid", XString.getStr(jo, "cfid"));
                                HttpUtils http = new HttpUtils();
                                http.send(HttpRequest.HttpMethod.POST, Net.DELETE_AD, params, new RequestCallBack<String>() {
                                    @Override
                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                        LogUtils.d(responseInfo.result);
                                        JSONObject jo = XString.getJSONObject(responseInfo.result);
                                        if (XString.getBoolean(jo, JsonName.STATUS)) {
                                            mActivity.app.toast("成功");
                                            list.remove(holder.getIndex());
                                            adapter.notifyDataSetChanged();
                                            adapter.notifyItemRemoved(holder.getIndex());
                                        } else {
                                            JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                                            mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                                        }
                                    }

                                    @Override
                                    public void onFailure(HttpException e, String s) {
                                        mActivity.app.toast("您的网络不给力");
                                    }
                                });
                            }
                        });
                        builder.create().show();
                        return true;
                    }
                });
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
                        holder.getTextView(R.id.valid).setTextColor(getResources().getColor(R.color.text_secead));
                        break;
                    case 1:
                        holder.getTextView(R.id.valid).setText("有效");
                        holder.getTextView(R.id.valid).setTextColor(getResources().getColor(R.color.green));
                        break;
                    case 2:
                        holder.getTextView(R.id.valid).setText("过期");
                        holder.getTextView(R.id.valid).setTextColor(getResources().getColor(R.color.text_secead));
                        break;
                    case 3:
                        holder.getTextView(R.id.valid).setText("已接单");
                        holder.getTextView(R.id.valid).setTextColor(getResources().getColor(R.color.text_secead));
                        break;
                }
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_adlist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
        getListData();
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
                            mActivity.app.toast("暂时没有数据");
                        } else {
                            mActivity.app.toast("没有更多数据");
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
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                srl.setLoading(false);
                srl.setRefreshing(false);
                mActivity.app.toast("您的网络不给力");
            }
        });
    }

}



















