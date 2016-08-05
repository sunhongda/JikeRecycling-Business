package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.SpaceItemDecoration;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.DensityUtils;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.viewholder.SimpleHolder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by Administrator on 2015/10/20.
 */
public class SearchListFragment extends LevelTwoFragment {

    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;

    private List<JSONObject> list;
    private RecyclerView.Adapter adapter;
    String jsonResult;
    String startTime, endTime, startAddress, endAddress, carid;

    @Override
    public String getTitle() {
        return "搜索列表";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_search_list;
    }

    private void initData() {
        Intent intent = mActivity.getIntent();
        jsonResult = intent.getStringExtra("result");
        Log.d("jsonResult", jsonResult);


        startTime = intent.getStringExtra("startdate");
        endTime = intent.getStringExtra("enddate");
        startAddress = intent.getStringExtra("scity");
        endAddress = intent.getStringExtra("ecity");
        carid = intent.getStringExtra("carid");
    }

    @Override
    protected void initView() {
        initData();

        srl.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListData();
            }
        });

        final int[] bgc = {R.color.pink, R.color.green, R.color.transparent,
                R.color.transparent, R.color.transparent, R.color.yellow,
                R.color.purple, R.color.transparent, R.color.transparent, R.color.blue};
        list = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(mActivity));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new SpaceItemDecoration(DensityUtils.dip2px(mActivity, 12), DensityUtils.dip2px(mActivity, 16)));
        rv.setAdapter(adapter = new RecyclerView.Adapter<SimpleHolder>() {
            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final SimpleHolder holder = new SimpleHolder(LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.item_search_list, parent, false));
//                holder.setTag(R.id.cv, R.id.ll);
//                holder.setTag(R.id.tv_1, R.id.tv_3, R.id.tv_5, R.id.tv_9, R.id.tv_6, R.id.tv_8, R.id.tv_10);
//                holder.setTag(R.id.tv_11, R.id.tv_12);
//                holder.setTag(R.id.ll_1, R.id.ll_2);

                holder.setTag(R.id.icon, R.id.tv_order_state, R.id.tv_order_nb, R.id.tv_time_min, R.id.tv_start_address_time_month_day
                        , R.id.start_address, R.id.end_address, R.id.tv, R.id.order_time,
                        R.id.tv_use_car_time, R.id.tv_use_car_time, R.id.arrows);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject o = list.get(holder.getIndex());
                        Intent intent=new Intent();
                        intent.putExtra("orid", XString.getStr(o, "orid"));
                        intent.putExtra("pushid", XString.getStr(o, "pushid"));
                        mActivity.startFragment(BackActivity.class,SearchDetailFragment.class,intent);
                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(SimpleHolder holder, int position) {
                holder.setIndex(position);
                JSONObject info = list.get(position);
//                int orderstate = XString.getInt(info, JsonName.ORDERSTATE);
//                ((CardView) holder.getView(R.id.cv)).setCardBackgroundColor(getResources().getColor(bgc[orderstate - 1]));
//                holder.getTextView(R.id.tv_1).setText("订单号：" + XString.getStr(info, JsonName.ORDERNO));
//                holder.getTextView(R.id.tv_3).setText(DateUtils.getYYYYMMdd(XString.getLong(info, JsonName.ADD_TIME)));
//                holder.getTextView(R.id.tv_5).setText(DateUtils.getMonthDay(XString.getLong(info, JsonName.START_DATE)));
//                holder.getTextView(R.id.tv_6).setText(
//                        AppUtils.getAddress(
//                                XString.getStr(info, JsonName.START_PROVINCE), XString.getStr(info, JsonName.START_CITY), ""));
//                holder.getTextView(R.id.tv_9).setText(DateUtils.getMonthDay(XString.getLong(info, JsonName.END_DATE)));
//                holder.getTextView(R.id.tv_10).setText(
//                        AppUtils.getAddress(
//                                XString.getStr(info, JsonName.END_PROVINCE), XString.getStr(info, JsonName.END_CITY), ""
//                        ));

                //新
                holder.getTextView(R.id.tv_order_nb).setText("订单号：" + XString.getStr(info, JsonName.ORDERNO));//订单号
                holder.getTextView(R.id.order_time).setText(DateUtils.getYYYYMMdd(XString.getLong(info, JsonName.ADD_TIME)));//订单时间
                String tv_order_state = AppUtils.getOrderState(XString.getInt(info, JsonName.ORDERSTATE));
                holder.getTextView(R.id.tv_order_state).setText(tv_order_state);//未报价
                holder.getTextView(R.id.tv_time_min).setText(DateUtils.getHHmmm(XString.getLong(info, JsonName.START_DATE)));//开始时分
                holder.getTextView(R.id.tv_start_address_time_month_day).setText(DateUtils.getMonthDay(XString.getLong(info, JsonName.START_DATE)));//开始月日
                // holder.getTextView(R.id.tv).setText(AppUtils.getOrderState(XString.getInt(info, JsonName.ORDER_TYPE)));//接火车
                holder.getTextView(R.id.tv).setText(AppUtils.getOrderTypeString(XString.getInt(info, JsonName.ORDER_TYPE)));
                holder.getTextView(R.id.start_address).setText(
                        AppUtils.getAddress("", XString.getStr(info, JsonName.START_CITY), ""));//开始位置
                holder.getTextView(R.id.end_address).setText(
                        AppUtils.getAddress(
                                "", XString.getStr(info, JsonName.END_CITY), ""
                        ));//结束位置
                holder.getImageView(R.id.icon).setImageResource(AppUtils.getOrderTypeIcon(XString.getInt(info, "ordertype")));//图标

                //未读订单设置为绿色
                holder.getTextView(R.id.tv_order_state).setTextColor(Color.parseColor("#27cf00"));

            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        displayListData(jsonResult);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void getListData() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("startdate", startTime);
        params.addBodyParameter("enddate", endTime);
        params.addBodyParameter("scity", startAddress);
        params.addBodyParameter("ecity", endAddress);
        params.addBodyParameter("carid", carid);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.SEARCH, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                srl.setRefreshing(false);
                displayListData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                srl.setRefreshing(false);
                mActivity.app.toast("您的网络不给力");
            }
        });
    }

    private void displayListData(String jsonResult) {
        JSONObject result = XString.getJSONObject(jsonResult);
        JSONObject data = XString.getJSONObject(result, JsonName.DATA);
        if (XString.getBoolean(result, JsonName.STATUS)) {
            List<JSONObject> resList = XString.getList(data, JsonName.ORDER_LIST);
            list.clear();
            list.addAll(resList);
            adapter.notifyDataSetChanged();
            //adapter.notifyItemRangeInserted(0, resList.size() - 1);
        } else {
            mActivity.app.toast(XString.getStr(data, JsonName.MSG));
        }
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if ("finishSearchList".equals(e.getTag())) {
            mActivity.finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
