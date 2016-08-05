package com.buslink.busjie.driver.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
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

import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TripListFragment extends LevelTwoFragment {

    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.srl)
    SwipeRefreshLayout srl;
    @Bind(R.id.no_trip)
    LinearLayout noTrip;
    @Bind(R.id.progressbar)
    ProgressBar progressBar;


    private RecyclerView.Adapter adapter;
    private int page = 0, pageSize = 10;
    private List<JSONObject> list;

    @Override
    public String getTitle() {
        return "行程详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.f_recycler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Subscriber(tag = "tripdetail")
    void resh(MyEvent e) {
        page = 0;
//        new XDataUtils(activity).deleteData(Net.PMY_TRIPLST, 0);
//        EventBus.getDefault().post(new MyEvent(""), "red");
        getData();
    }



    @Override
    protected void initView() {
        srl.setLoadNoFull(true);
        srl.setColor(R.color.green, R.color.green_p, R.color.green, R.color.green_p);
        srl.setOnLoadListener(new SwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                getData();
            }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                getData();
            }
        });
        rv.setLayoutManager(new GridLayoutManager(mActivity, 1, GridLayoutManager.VERTICAL, false));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.getItemAnimator().setAddDuration(1000);
        rv.getItemAnimator().setRemoveDuration(1000);
        rv.getItemAnimator().setMoveDuration(1000);
        rv.getItemAnimator().setChangeDuration(1000);
        list = new ArrayList<>();
        rv.setAdapter(adapter = new RecyclerView.Adapter<SimpleHolder>() {

            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final SimpleHolder holder = new SimpleHolder(mActivity.getLayoutInflater().inflate(R.layout.i_trip_list, null));
                holder.setTag(R.id.tv, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_start_time, R.id.tv_start_address, R.id.tv_end_time, R.id.tv_end_address, R.id.ll_pass,
                        R.id.tv_payment_in_advance, R.id.tv_final_payment,
                        R.id.ll_pay,
                        R.id.v, R.id.tripelist_red);
                holder.setTag(R.id.tv_bianhao);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doClick(holder.getIndex());
                    }
                });
                return holder;
            }

            @Override
            public void onBindViewHolder(final SimpleHolder holder, final int position) {
                holder.setIndex(position);
                final JSONObject o = list.get(position);
                holder.<TextView>getTag(R.id.tv_bianhao).setText("订单编号：" + XString.getStr(o, "orderno"));
                holder.<TextView>getTag(R.id.tv).setText(String.format("时间：%s", DateUtils.getYYYYMMdd(XString.getLong(o, JsonName.ADD_TIME))));
                holder.<TextView>getTag(R.id.tv_1).setText(XString.getStr(o, JsonName.START_CITY) + "→");
                if (!TextUtils.isEmpty(XString.getStr(o, JsonName.CITY))) {
                    if (TextUtils.isEmpty(XString.getStr(o, JsonName.CITY_TWO))) {
                        holder.<TextView>getTag(R.id.tv_2).setText(XString.getStr(o, JsonName.CITY) + "→");
                    } else {
                        holder.<TextView>getTag(R.id.tv_2).setText(XString.getStr(o, JsonName.CITY) + "···");
                    }
                    holder.<TextView>getTag(R.id.tv_2).setVisibility(View.VISIBLE);
                } else {
                    holder.<TextView>getTag(R.id.tv_2).setVisibility(View.GONE);
                }

                if (XString.getInt(o, JsonName.IS_TRIP) == 0) {
                    // holder.<View>getTag(R.id.v).setBackgroundResource(R.drawable.shape_green_c);
                    holder.<ImageView>getTag(R.id.tripelist_red).setVisibility(View.INVISIBLE);
                } else {
                    // holder.<View>getTag(R.id.v).setBackgroundResource(R.drawable.shap_red_c);
                    holder.<ImageView>getTag(R.id.tripelist_red).setVisibility(View.VISIBLE);
                }

                holder.<TextView>getTag(R.id.tv_3).setText(XString.getStr(o, JsonName.END_CITY));
                holder.<TextView>getTag(R.id.tv_start_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(o, JsonName.START_DATE)));
                holder.<TextView>getTag(R.id.tv_end_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(o, JsonName.END_DATE)));
                holder.<TextView>getTag(R.id.tv_start_address).setText(AppUtils.getAddress(XString.getStr(o, JsonName.START_PROVINCE)
                        , XString.getStr(o, JsonName.START_CITY), ""));
                holder.<TextView>getTag(R.id.tv_end_address).setText(AppUtils.getAddress(XString.getStr(o, JsonName.END_PROVINCE)
                        , XString.getStr(o, JsonName.END_CITY), ""));

//                if(XString.getStr(o, JsonName.ONE_PAY_MONEY).equals("1")){
//                    //一次付款
//                    holder.<LinearLayout>getTag(R.id.ll_pay).setVisibility(View.VISIBLE);
//                    holder.<TextView>getTag(R.id.tv_payment_in_advance).setText(String.format("预付金额%s元", XString.getStr(o, JsonName.ONE_PAY_MONEY)));
//                    holder.<TextView>getTag(R.id.tv_final_payment).setText(String.format("去支付尾款%s元", XString.getStr(o, JsonName.TWO_PAY_MONEY)));
//                }else if(XString.getStr(o, JsonName.ONE_PAY_MONEY).equals("2")){
//                    // 二次付款
//                    holder.<LinearLayout>getTag(R.id.ll_pay).setVisibility(View.VISIBLE);
//                    holder.<TextView>getTag(R.id.tv_payment_in_advance).setText(String.format("预付款金额%s元", XString.getStr(o, JsonName.ONE_PAY_MONEY)));
//                    holder.<TextView>getTag(R.id.tv_final_payment).setText(String.format("已收到尾款%s元", XString.getStr(o, JsonName.TWO_PAY_MONEY)));
//
//                    holder.<TextView>getTag(R.id.tv_final_payment).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Toast.makeText(getActivity(),holder.getIndex()+"",Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }

//                paystate     是否支付 0未支付 1已支付
//                paytype	  支付次数 1一次性支付 2分为两次
                if (XString.getStr(o, JsonName.PAY_STATE).equals("0") && XString.getStr(o, JsonName.PAY_TYPE).equals("2")) {
                    //未支付尾款的订单
                    holder.<LinearLayout>getTag(R.id.ll_pay).setVisibility(View.VISIBLE);
                    holder.<TextView>getTag(R.id.tv_payment_in_advance).setText(String.format("预付款金额%s元", XString.getStr(o, JsonName.ONE_PAY_MONEY)));
                    if (XString.getInt(o, JsonName.ORDER_TYPE) == 10) {//包车单显示 未支付 并且不可点击
                        holder.<TextView>getTag(R.id.tv_final_payment).setText(String.format("乘客未支付%s元", XString.getStr(o, JsonName.TWO_PAY_MONEY)));
                    } else {
                        holder.<TextView>getTag(R.id.tv_final_payment).setText(String.format("我已收尾款%s元", XString.getStr(o, JsonName.TWO_PAY_MONEY)));

                        holder.<TextView>getTag(R.id.tv_final_payment).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                                        //                                setTitle("确定删除？").
                                                setMessage("您已收到乘客支付的尾款了吗？").
                                        //                                setIcon(R.drawable.icon_driverdcode).
                                                setPositiveButton("已收到", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO 点击确定完成订单
                                                //请求后paystate     是否支付 0未支付 1已支付  变为已支付
//                        http post请求
//                        地址：http://123.56.67.27:28081/buslk/confirmcompletion.htm   version  orid
                                                RequestParams params = new RequestParams();
                                                params.addBodyParameter("orid", XString.getStr(o, "orid"));

                                                params.addBodyParameter("version", MyApplication.getVersionCode());
                                                HttpUtils http = new HttpUtils();
                                                http.send(HttpRequest.HttpMethod.POST, Net.CONFIRM_COMPLETION, params, new RequestCallBack<String>() {
                                                    @Override
                                                    public void onSuccess(ResponseInfo<String> responseInfo) {
                                                        JSONObject result = XString.getJSONObject(responseInfo.result);
                                                        JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                                                        //JSONObject confirmStatus = XString.getJSONObject(result, JsonName.DATA);
                                                        if (XString.getBoolean(result, JsonName.STATUS)) {
                                                            mActivity.app.toast("确认成功");
                                                            //订单状态改为已完成
                                                            XString.put(o, JsonName.PAY_STATE, "1");
                                                            //刷新
                                                            adapter.notifyItemChanged(position);

                                                        } else {
                                                            mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(HttpException e, String s) {
                                                        mActivity.app.toast("您的网络不给力");
                                                    }
                                                });

//                        RequestManager.confirmCompletion(params, new RequestCallBack<String>() {
//                            @Override
//                            public void onSuccess(ResponseInfo<String> responseInfo) {
//                                JSONObject result = XString.getJSONObject(responseInfo.result);
//                                JSONObject data = XString.getJSONObject(result, JsonName.DATA);
//                                JSONObject confirmStatus = XString.getJSONObject(result, JsonName.DATA);
//                                if (XString.getBoolean(confirmStatus, JsonName.STATUS)) {
//                                    mActivity.app.toast("确认成功");
//                                    //订单状态改为已完成
//                                    //刷新
//                                    adapter.notifyItemChanged(position);
//                                }else {
//                                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
//                                }
//                            }
//                            @Override
//                            public void onFailure(HttpException e, String s) {
//                                mActivity.app.toast("您的网络不给力");
//                            }
//                        });

                                            }
                                        }).setNegativeButton("未收到", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).
                                        create();
                                alertDialog.show();
                            }
                        });
                    }



                } else {
                    //支付完成或者是已支付的订单
                    holder.<LinearLayout>getTag(R.id.ll_pay).setVisibility(View.GONE);
                }


            }

            @Override
            public int getItemCount() {
                return list.size();
            }

            private void doClick(int index) {
                JSONObject o = list.get(index);
                XString.put(o, JsonName.IS_TRIP, 0);


                adapter.notifyItemChanged(index);
                Intent intent = new Intent();
                intent.putExtra("orid", XString.getStr(o, "orid"));
                intent.putExtra("pushid", XString.getStr(o, "pushid"));
                //订单类型为包车
                if (XString.getInt(o, JsonName.ORDER_TYPE) == 10) {
                    mActivity.startFragment(BackActivity.class, TripDetailFragment2Character.class, intent);
                    LogUtils.d(XString.getStr(o, JsonName.ORDER_TYPE));

                } else {
                    mActivity.startFragment(BackActivity.class, TripDetailFragment2.class, intent);
                    LogUtils.d(XString.getStr(o, JsonName.ORDER_TYPE));
                }


            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        getData();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("page", page + "");
        RequestManager.tripList(params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                progressBar.setVisibility(View.GONE);
                srl.setRefreshing(false);
                srl.setLoading(false);

                JSONObject result = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                if (XString.getBoolean(result, JsonName.STATUS)) {
                    List<JSONObject> resList = XString.getList(data, JsonName.ORDER_LIST);
                    if (page == 0) {
                        list.clear();
                        adapter.notifyDataSetChanged();
                    }
                    list.addAll(resList);
                    adapter.notifyItemRangeInserted(page, resList.size() - 1);
                    page = list.size();
                    if (list.size() == 0) {
                        noTrip.setVisibility(View.VISIBLE);
                        srl.setVisibility(View.GONE);
                    } else {
                        noTrip.setVisibility(View.GONE);
                        srl.setVisibility(View.VISIBLE);
                    }
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (null != progressBar) {
                    progressBar.setVisibility(View.GONE);
                }

                mActivity.app.toast("您的网络不给力");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
    }
}
