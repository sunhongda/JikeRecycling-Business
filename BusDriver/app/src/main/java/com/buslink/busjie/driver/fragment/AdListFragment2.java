package com.buslink.busjie.driver.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.SpaceItemDecoration;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
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

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

/**
 * Created by Administrator on 2016/01/06
 * 拼车广告发布列表页 LevelTwoFragment
 */
public class AdListFragment2 extends  OrderCarpoolingBaseFragment{

    private int page = 0, pageSize = 6;
    private List<JSONObject> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @ViewInject(R.id.srl)
    private SwipeRefreshLayout srl;
    @ViewInject(R.id.rv)
    private RecyclerView rv;
    Intent intent;
    @ViewInject(R.id.ll_no_order)
    LinearLayout noCarpooling;
    @ViewInject(R.id.progressbar)
    ProgressBar progressBar;
    private boolean isload=true;


    @Override
    public String getTitle() {
        return "发布列表";
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
                        from(parent.getContext()).inflate(R.layout.item_adlist2, parent, false));
//                holder.setTag(R.id.time1, R.id.time2, R.id.time3);
//                holder.setTag(R.id.place1, R.id.place2);
//                holder.setTag(R.id.valid);
                holder.setTag(R.id.tv_push_num);
                holder.setTag(R.id.tv_time_min);
                holder.setTag(R.id.start_address, R.id.end_address);
                holder.setTag(R.id.tv_sale_state,R.id.sale_tickets_state,//售票状态, 卖票按钮
                        R.id.tv_empty_position, R.id.tv_price);
                holder.setTag(R.id.iv_carpooling_red);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (intent == null) {
                            intent = new Intent();
                        } else {
                            intent = getActivity().getIntent();
                        }
                        final int index = holder.getIndex();
                        final JSONObject jo = list.get(index);
                        if(XString.getInt(jo,JsonName.ISCARPOOLING)==1){
                            EventBus.getDefault().post("","refcarpoolingred");
                        }


                        //消除列表红点
                       // 拼车单是否有新消息 0无1有
                         XString.put(jo, JsonName.ISCARPOOLING, 0);
                        //刷新首页数据
                        MyEvent e = new MyEvent("update_home");
                        EventBus.getDefault().post(e);

                        //  XString.put(jo, JsonName.IS_ORDER, 0);

                        String pid = XString.getStr(jo, JsonName.PID);
                        intent.putExtra(JsonName.PID, pid);
                        mActivity.startFragment(BackActivity.class, AdListDetailFragment.class, intent);
                        adapter.notifyItemChanged(index);
                    }
                });
                   // 长按删除
                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            final JSONObject jo = list.get(holder.getIndex());
                            if (XString.getInt(jo, JsonName.ISDEL) == 1) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                                builder.setMessage("删除这条广告吗？");
                                builder.setNegativeButton("取消", null);
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //  isdel  是否可删除 0不可删除  1可删除
                                        // XString.getInt(jo, JsonName.ORDERREQUIRESTATE) == 3
                                        RequestParams params = new RequestParams();
                                        params.addBodyParameter("version", MyApplication.getVersionCode());
                                        params.addBodyParameter("cfid", XString.getStr(jo, "cfid"));
                                        params.addBodyParameter(JsonName.PID, XString.getStr(jo, JsonName.PID));
                                        HttpUtils http = new HttpUtils();
                                        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_CARSEATPUBLISHDEL, params, new RequestCallBack<String>() {
                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                LogUtils.d(responseInfo.result);
                                                JSONObject jo = XString.getJSONObject(responseInfo.result);
                                                if (XString.getBoolean(jo, JsonName.STATUS)) {
                                                    mActivity.app.toast("删除成功");
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
                            } else if(XString.getInt(jo, JsonName.ISDEL) == 0) {
                              //   mActivity.app.toast("此时不支持删除");
                            }

                            return true;
                        }
                    });



                return holder;
            }

            @Override
            public void onBindViewHolder(final SimpleHolder holder, int position) {
                holder.setIndex(position);
                final JSONObject jo = list.get(position);
                //拼车单是否有新消息 0无1有
                if(XString.getInt(jo,JsonName.ISCARPOOLING)==1){
                   holder.getImageView(R.id.iv_carpooling_red).setVisibility(View.VISIBLE);

                }else if(XString.getInt(jo,JsonName.ISCARPOOLING)==0){
                    holder.getImageView(R.id.iv_carpooling_red).setVisibility(View.INVISIBLE);
                }
//                holder.setTag(R.id.tv_time_min,R.id.tv_start_address_time_month_day,R.id.tv_order_nb);
//                holder.setTag(R.id.start_address, R.id.end_address);
//                holder.setTag(R.id.sale_tickets_state,//售票状态
//                        R.id.tv_empty_position, R.id.tv_price);
                holder.getTextView(R.id.tv_push_num).setText(XString.getStr(jo, JsonName.PUSHNUMBER));
                holder.getTextView(R.id.tv_time_min).setText(DateUtils.getSimpleFormatTime(XString.getLong(jo, JsonName.START_DATE)));
                holder.getTextView(R.id.start_address).setText(XString.getStr(jo, JsonName.START_ADDRESS));
                holder.getTextView(R.id.end_address).setText(XString.getStr(jo, JsonName.END_ADDRESS));
                holder.getTextView(R.id.tv_empty_position).setText("余" + XString.getStr(jo, JsonName.SEATTOTAL) + "座位|已卖" + (XString.getInt(jo, JsonName.TOTAL) - XString.getInt(jo, JsonName.SEATTOTAL)) + "座");
                holder.getTextView(R.id.tv_price).setText(XString.getStr(jo, JsonName.PRICE) + "元/位");
                // holder.getTextView(R.id.sale_tickets_state).setText(XString.getStr(jo, JsonName.ORDERREQUIRESTATE));//售票状态
                final int state = XString.getInt(jo, JsonName.ORDERREQUIRESTATE);
                //票状态： 0.正在售票；1.票已售完；2.停止售票；3. 票已过期; 默认0
                if (state == 0 || state == 2) {
//                    isexist	切换票状态功能是否存在  0不存在  1 存在
//                    备注：isexist=0  按钮为灰色点击不调接口
                    if (XString.getInt(jo, JsonName.ISEXIST) == 0) {
                      //  holder.getButton(R.id.sale_tickets_state).setBackgroundColor(Color.parseColor("#e6e6e6"));
                        holder.getButton(R.id.sale_tickets_state).setVisibility(View.INVISIBLE);
                    } else if (XString.getInt(jo, JsonName.ISEXIST) == 1) {
                        holder.getButton(R.id.sale_tickets_state).setVisibility(View.VISIBLE);
                        //holder.getButton(R.id.sale_tickets_state).setBackgroundColor(Color.WHITE);
                        holder.getButton(R.id.sale_tickets_state).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChangeStage(jo, holder, state);
                                //    AppUtils.getOrderState(XString.getInt(info, JsonName.ORDERSTATE));
                            }
                        });
                    }
                }else if (state==3){
                    holder.getButton(R.id.sale_tickets_state).setVisibility(View.VISIBLE);
                    holder.getButton(R.id.sale_tickets_state).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setMessage("删除这条广告吗？");
                            builder.setNegativeButton("取消", null);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    JSONObject jo = list.get(holder.getIndex());

                                    //  isdel  是否可删除 0不可删除  1可删除
                                    // XString.getInt(jo, JsonName.ORDERREQUIRESTATE) == 3

                                    if (XString.getInt(jo, JsonName.ISDEL) == 1) {
                                        RequestParams params = new RequestParams();
                                        params.addBodyParameter("version", MyApplication.getVersionCode());
                                        params.addBodyParameter("cfid", XString.getStr(jo, "cfid"));
                                        params.addBodyParameter(JsonName.PID, XString.getStr(jo, JsonName.PID));
                                        HttpUtils http = new HttpUtils();
                                        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_CARSEATPUBLISHDEL, params, new RequestCallBack<String>() {
                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                LogUtils.d(responseInfo.result);
                                                JSONObject jo = XString.getJSONObject(responseInfo.result);
                                                if (XString.getBoolean(jo, JsonName.STATUS)) {
                                                    mActivity.app.toast("删除成功");
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
                                    } else if (XString.getInt(jo, JsonName.ISDEL) == 0) {
                                        mActivity.app.toast("此时不支持删除");
                                    }
                                }
                            });
                            builder.create().show();
                        }
                    });
                }else{
                    holder.getButton(R.id.sale_tickets_state).setVisibility(View.INVISIBLE);
                }



                switch (state) {
                    case 0:
                        holder.getTextView(R.id.tv_sale_state).setText("正在售票");
                        holder.getTextView(R.id.tv_sale_state).setTextColor(getResources().getColor(R.color.green));

                        holder.getButton(R.id.sale_tickets_state).setText("停止售票");
                        holder.getButton(R.id.sale_tickets_state).setTextColor(Color.parseColor("#DB7093"));//紫红色
                        break;
                    case 1:
                        holder.getTextView(R.id.tv_sale_state).setText("票已售完");
                        holder.getTextView(R.id.tv_sale_state).setTextColor(getResources().getColor(R.color.red));

                        holder.getButton(R.id.sale_tickets_state).setText("票已售完");
                        holder.getButton(R.id.sale_tickets_state).setTextColor(getResources().getColor(R.color.red));
                        break;
                    case 2:
                        holder.getTextView(R.id.tv_sale_state).setText("停止售票");
                        holder.getTextView(R.id.tv_sale_state).setTextColor(getResources().getColor(R.color.red));

                        holder.getButton(R.id.sale_tickets_state).setText("开始售票");
                        holder.getButton(R.id.sale_tickets_state).setTextColor(getResources().getColor(R.color.wbg));
                        break;
                    case 3:
                        holder.getTextView(R.id.tv_sale_state).setText("票已过期");
                        holder.getTextView(R.id.tv_sale_state).setTextColor(Color.parseColor("#c1c1bf"));//灰色

                        holder.getButton(R.id.sale_tickets_state).setText("删除广告");
                        holder.getButton(R.id.sale_tickets_state).setTextColor(getResources().getColor(R.color.text_secead));
                        break;
                }
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    private void ChangeStage(final JSONObject jo, final SimpleHolder holder, final int state) {
        final RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter(JsonName.PID, XString.getStr(jo, JsonName.PID));

        if (state == 0) {
            params.addBodyParameter(JsonName.ORDERREQUIRESTATE, "2");
        } else if (state == 2) {
            params.addBodyParameter(JsonName.ORDERREQUIRESTATE, "0");

        }
        params.addBodyParameter(JsonName.ORDERREQUIRESTATE, XString.getStr(jo, JsonName.ORDERREQUIRESTATE));
        HttpUtils http = new HttpUtils();
//        票状态： 0.正在售票；1.票已售完；2.停止售票；3. 票已过期; 默认0
//        票状态，前端只能进行0和2之间切换；
        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_SELLTICKET, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo2 = XString.getJSONObject(responseInfo.result);
                if (XString.getBoolean(jo2, JsonName.STATUS)) {
                    int index = holder.getIndex();
                    if (state == 0) {
                        XString.put(jo, JsonName.ORDERREQUIRESTATE, 2);
                    } else if (state == 2) {
                        XString.put(jo, JsonName.ORDERREQUIRESTATE, 0);
                    }

                    adapter.notifyItemChanged(index);
                    //adapter.notifyItemRemoved(holder.getIndex());

                } else {
                    JSONObject data = XString.getJSONObject(jo2, JsonName.DATA);
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.app.toast("您的网络不给力");
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
       EventBus.getDefault().register(this);
        getListData();
        progressBar.setVisibility(View.VISIBLE);

    }

    private void getListData() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pagesize", pageSize + "");
        HttpUtils http = new HttpUtils();
        //拼车列表
        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_LIST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                progressBar.setVisibility(View.GONE);
                srl.setLoading(false);
                srl.setRefreshing(false);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    JSONArray adlist = XString.getJSONArray(data, "carpoolinglst");
                    if (adlist.length() == 0) {
                        if (page == 0) {
                            noCarpooling.setVisibility(View.VISIBLE);
                            srl.setVisibility(View.GONE);
                            mActivity.app.toast("暂时没有数据");
                        } else {
                            mActivity.app.toast("没有更多数据");
                        }
                        return;
                    } else {
                        noCarpooling.setVisibility(View.GONE);
                        srl.setVisibility(View.VISIBLE);
                    }
                    List<JSONObject> resList = XString.getList(data, "carpoolinglst");
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
                if (null != progressBar) {
                    progressBar.setVisibility(View.GONE);
                }
                mActivity.app.toast("您的网络不给力");
            }
        });
    }

    @Override
    public void onDestroy() {
        org.simple.eventbus.EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}



















