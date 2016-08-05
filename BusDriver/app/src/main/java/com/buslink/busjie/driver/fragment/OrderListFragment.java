package com.buslink.busjie.driver.fragment;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.SpaceItemDecoration;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.RequestName;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SwipeRefreshLayout;
import com.buslink.busjie.driver.viewholder.SimpleHolder;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import java.util.LinkedList;
import java.util.List;
import cz.msebera.android.httpclient.Header;
//订单列表
public class OrderListFragment extends OrderCarpoolingBaseFragment {
    private List<JSONObject> list;
    @ViewInject(R.id.srl)
    private SwipeRefreshLayout srl;
    @ViewInject(R.id.rv)
    private RecyclerView rv;
    @ViewInject(R.id.no_order)
    LinearLayout noOrder;
    @ViewInject(R.id.progressbar)
    ProgressBar progressBar;
    private boolean isload=true;

    private int page = 0, pageSize = 10;
    private RecyclerView.Adapter adapter;
    private final int ANIMATIONTIME = 500;
    String tv_order_state;
    private Intent intent;

    @Override
    public String getTitle() {
        return "订单列表";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_order_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Subscriber(tag="upOrderListDetailFragment")
    void onEventbus(String e){
        isload=false;
       page=0;
        getListData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
        getListData();
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void initView() {
//        mToolbar.inflateMenu(R.menu.menu_order);
//
//        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_order_soft:
//                        //mActivity.startActivity(new Intent(mActivity, AdListActivity.class));
////                       View v= (View) mToolbar.getMenu().findItem(R.id.action_order_soft).getActionView();
////                        MenuInflater inflater = getActivity().getMenuInflater();
////
////                        inflater.inflate(R.menu.menu_order,mToolbar.getMenu());
////                        mToolbar.getMenu().findItem(R.id.action_order_soft).setChecked(true);
//                        showPopupWindow(mToolbar);
//                }
//                return false;
//            }
//        });

        page = 0;
        srl.setLoadNoFull(true);
        srl.setColor(R.color.green, R.color.green_p, R.color.green, R.color.green_p);
        final int[] bgc = {R.color.pink, R.color.green, R.color.transparent,
                R.color.transparent, R.color.transparent, R.color.yellow,
                R.color.purple, R.color.transparent, R.color.transparent, R.color.blue};
        srl.setOnLoadListener(new SwipeRefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                isload=true;
                getListData();
            }
        });
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isload=false;
                page = 0;
                getListData();
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(mActivity));
        rv.setItemAnimator(new DefaultItemAnimator());
        list = new LinkedList<>();

        rv.addItemDecoration(new SpaceItemDecoration(16, 28));
        rv.setAdapter(adapter = new RecyclerView.Adapter<SimpleHolder>() {

            ValueAnimator va;

            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final SimpleHolder holder = new SimpleHolder(LayoutInflater.
                        from(parent.getContext()).inflate(R.layout.item_order_list_new, parent, false));
//                holder.setTag(R.id.cv, R.id.sv, R.id.ll, R.id.ll_1, R.id.iv);
//                holder.setTag(R.id.tv_1, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6,
//                        R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.ll_1, R.id.ll_2);
//                holder.setTag(R.id.rl, R.id.tv_1_0, R.id.tv_1_1, R.id.tv_1_2, R.id.tv_start_time, R.id.tv_start_address, R.id.ll_pass, R.id.tv_end_time, R.id.tv_end_address
//                        , R.id.tv_days, R.id.tv_cars_number, R.id.tv_car_type, R.id.tv_driver_welfare, R.id.bt);
//                holder.setTag(R.id.right, R.id.quote, R.id.btn, R.id.et, R.id.ll_4);
//                holder.setTag(R.id.tv_mileage, R.id.icon, R.id.ll_10, R.id.reddot);
                holder.setTag(R.id.icon, R.id.tv_order_state, R.id.tv_order_nb, R.id.tv_time_min, R.id.tv_start_address_time_month_day
                        , R.id.start_address, R.id.end_address, R.id.tv, R.id.order_time,
                        R.id.tv_use_car_time, R.id.tv_use_car_time, R.id.arrows,R.id.reddot);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳到订单详情页
                        String detail = XString.getStr(list.get(holder.getIndex()), "detail");
                        getOrderData(holder);

                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final JSONObject data = list.get(holder.getIndex());
                        String orderstate = XString.getStr(data, "orderstate");
                       // isdel	是否可删除 0不可删除  1可删除
                        if(XString.getStr(data,JsonName.ISDEL).equals("1")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setMessage("刪除该订单吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    AsyncHttpClient client = new AsyncHttpClient();
                                    RequestParams params = MyHelper.getParams();
                                    params.add("pushid", XString.getStr(data, "pushid"));

                                    client.post(mActivity, Net.DELETE_ORDER, params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            if (XString.getBoolean(response, JsonName.STATUS)) {
                                                mActivity.app.toast("删除成功");
                                                list.remove(holder.getIndex());
                                                adapter.notifyDataSetChanged();
                                                adapter.notifyItemRemoved(holder.getIndex());
                                            } else {
                                                JSONObject data = XString.getJSONObject(response, JsonName.DATA);
                                                mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                                            }
                                        }
                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                            mActivity.app.toast("您的网络不给力");
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("取消", null);
                            builder.create().show();
                        }
//                        if ("7".equals(orderstate) || "10".equals(orderstate)||"12".equals(orderstate)) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//                            builder.setMessage("刪除该订单吗？");
//                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    AsyncHttpClient client = new AsyncHttpClient();
//                                    RequestParams params = MyHelper.getParams();
//                                    params.add("pushid", XString.getStr(data, "pushid"));
//                                    client.post(mActivity, Net.DELETE_ORDER, params, new JsonHttpResponseHandler() {
//                                        @Override
//                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                            if (XString.getBoolean(response, JsonName.STATUS)) {
//                                                mActivity.app.toast("成功");
//                                                list.remove(holder.getIndex());
//                                                adapter.notifyDataSetChanged();
//                                                adapter.notifyItemRemoved(holder.getIndex());
//                                            } else {
//                                                JSONObject data = XString.getJSONObject(response, JsonName.DATA);
//                                                mActivity.app.toast(XString.getStr(data, JsonName.MSG));
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                                            mActivity.app.toast("您的网络不给力");
//                                        }
//                                    });
//                                }
//                            });
//                            builder.setNegativeButton("取消", null);
//                            builder.create().show();
//                        }
                        return true;
                    }
                });

                return holder;
            }

            @Override
            public void onBindViewHolder(SimpleHolder holder, int position) { // controller
                holder.setIndex(position);
                JSONObject info = list.get(position);
                // int orderstate = XString.getInt(info, JsonName.ORDERSTATE);
                // ((CardView) holder.getView(R.id.cv)).setCardBackgroundColor(getResources().getColor(bgc[orderstate - 1]));

                //    holder.setTag(R.id.icon,R.id.reddot,R.id.tv_order_state,R.id.tv_order_nb,R.id.tv_time_min,R.id.tv_start_address_time_month_day
                //           ,R.id.start_address,R.id.end_address,R.id.tv,R.id.order_time);


                holder.getTextView(R.id.tv_order_nb).setText("订单号：" + XString.getStr(info, JsonName.ORDERNO));//订单号
                holder.getTextView(R.id.order_time).setText(DateUtils.getYYYYMMdd(XString.getLong(info, JsonName.ADD_TIME)));//订单时间



                //已读和未读
                if (XString.getInt(info, "isorder") == 0) {
                   tv_order_state = AppUtils.getOrderState(XString.getInt(info, JsonName.ORDERSTATE));
                    holder.getTextView(R.id.tv_order_state).setText(tv_order_state);//未报价

                    holder.getImageView(R.id.reddot).setVisibility(View.INVISIBLE);
                    //已读灰色
                    //tv_order_nb tv_use_car_time tv_time_min  tv_start_address_time_month_day
                    //start_address  end_address arrows   order_time  tv_order_state未报价
                    holder.getTextView(R.id.tv).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.tv_order_nb).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.tv_use_car_time).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.tv_time_min).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.tv_start_address_time_month_day).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.end_address).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.start_address).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.arrows).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.order_time).setTextColor(Color.parseColor("#9e9e9e"));
                    holder.getTextView(R.id.tv_order_state).setTextColor(Color.parseColor("#9e9e9e"));
                } else {
                    holder.getImageView(R.id.reddot).setVisibility(View.VISIBLE);
                    tv_order_state = AppUtils.getOrderState(XString.getInt(info, JsonName.ORDERSTATE));
                    holder.getTextView(R.id.tv_order_state).setText(tv_order_state);//未报价
                    //未读 黑色
                    holder.getTextView(R.id.tv).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.tv_order_nb).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.tv_use_car_time).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.tv_time_min).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.tv_start_address_time_month_day).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.end_address).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.start_address).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.arrows).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.order_time).setTextColor(Color.parseColor("#cf000000"));
                    holder.getTextView(R.id.tv_order_state).setTextColor(Color.parseColor("#cf000000"));
                }

                if (tv_order_state.equals("未抢单")) {
                    holder.getTextView(R.id.tv_order_state).setTextColor(Color.parseColor("#27cf00"));
                } else if (tv_order_state.equals("已报价")) {
                    holder.getTextView(R.id.tv_order_state).setTextColor(Color.parseColor("#6560b5"));

                } else {
                    //重用时要重新设置颜色为黑色
                    holder.getTextView(R.id.tv_order_state).setTextColor(Color.BLACK);
                }


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

                //setOrderData(holder);
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });

    }

    private void getOrderData(final SimpleHolder holder) {
        final int index = holder.getIndex();
        final JSONObject jo = list.get(index);
        //未读的话点击时通知改变ordercarpoolingactivity的红点
        if(XString.getInt(jo,JsonName.IS_ORDER)==1){
            EventBus.getDefault().post("","reforderred");
        }



        XString.put(jo, JsonName.IS_ORDER, 0);
        String orid = XString.getStr(jo, JsonName.ORID);
        String pushid = XString.getStr(jo, JsonName.PUSHID);
        if (intent == null) {
            intent = new Intent();
        } else {
            intent = getActivity().getIntent();
        }
        intent.putExtra("pushid", pushid);
        intent.putExtra("orid", orid);

        if(XString.getInt(jo,JsonName.ORDER_TYPE)==10){
            mActivity.startFragment(BackActivity.class, OrderListDetailFragmentCharacter.class, intent);
        }else{
            mActivity.startFragment(BackActivity.class, OrderListDetailFragment.class, intent);
        }

       // mActivity.startFragment(DetailActivity.class, OrderListDetailFragment.class, intent);

        adapter.notifyItemChanged(index);


//        AsyncHttpClient client = new AsyncHttpClient();
//        final int index = holder.getIndex();
//        final JSONObject jo = list.get(index);
//        String orid = XString.getStr(jo, JsonName.ORID);
//        String pushid = XString.getStr(jo, JsonName.PUSHID);
//        RequestParams params = MyHelper.getParams();
//        params.add(JsonName.ORID, orid);
//        params.add(JsonName.PUSHID, pushid);
//        client.post(Net.ORDERDETAIL, params, new TextHttpResponseHandler() {
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwabl) {
//                throwabl.printStackTrace();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
////                XString.remove(jo, "detail");
////                XString.put(jo, "detail", responseString);
////                XString.put(jo, "detailVisible", true);
////                XString.put(jo, "isorder", 0);
////                rv.smoothScrollToPosition(index);
////                LogUtils.d(responseString);
////                adapter.notifyItemChanged(index);
//                if (intent == null) {
//                    intent = new Intent();
//                } else {
//                    intent = getActivity().getIntent();
//                }
//
//
//                intent.putExtra("responseString", responseString);
//                //activity.dialog.dismiss();
//                mActivity.startFragment(BackActivity.class, OrderListDetailFragment.class, intent);
//
//            }
//        });
    }

    private void setOrderData(final SimpleHolder holder) {
        String detail = XString.getStr(list.get(holder.getIndex()), "detail");
        if (TextUtils.isEmpty(detail)) {
            return;
        }
        JSONObject jo = XString.getJSONObject(detail);
        JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
        if (XString.getBoolean(jo, JsonName.STATUS)) {
            holder.getTextView(R.id.tv_1_0).setText("订单号：" + XString.getStr(data, JsonName.ORDERNO));
            holder.getTextView(R.id.tv_1_1).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));
            holder.getTextView(R.id.tv_days).setText("共" + XString.getStr(data, JsonName.DAYS) + "天");
            holder.getTextView(R.id.tv_1_2).setText(XString.getStr(data, JsonName.TOTAL) + "人（含儿童）");
            holder.getTextView(R.id.tv_cars_number).setText("共" + 1 + "辆");
            holder.getTextView(R.id.tv_car_type).setText(XString.getStr(data, JsonName.CAR_TYPE));
            String like = "不包住，不包吃";
            int isEat = XString.getInt(data, JsonName.IS_EAT);
            int isLive = XString.getInt(data, JsonName.IS_LIVE);
            if (isEat == 1 && isLive == 1) {
                like = "包吃，包住";
            }
            if (isEat == 1 && isLive == 0) {
                like = "包吃";
            }

            if (isEat == 0 && isLive == 1) {
                like = "包住";
            }

            if (isEat == 0 && isLive == 0) {
                like = "无";
            }
            holder.getTextView(R.id.tv_driver_welfare).setText(like);
            holder.getTextView(R.id.tv_mileage).setText(XString.getStr(data, "drivingrange"));
            holder.getTextView(R.id.tv_start_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)));

            holder.getTextView(R.id.tv_start_address).setText(AppUtils.getAddress(
                    XString.getStr(data, JsonName.START_PROVINCE),
                    XString.getStr(data, JsonName.START_CITY),
                    XString.getStr(data, JsonName.START_ADDRESS)
            ));

            holder.getTextView(R.id.tv_end_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.END_DATE)));

            holder.getTextView(R.id.tv_end_address).setText(AppUtils.getAddress(
                    XString.getStr(data, JsonName.END_PROVINCE),
                    XString.getStr(data, JsonName.END_CITY),
                    XString.getStr(data, JsonName.END_ADDRESS)
            ));
            if (XString.getInt(data, JsonName.ORDERSTATE) == 1) {
                holder.getTextView(R.id.quote).setVisibility(View.GONE);
                holder.getView(R.id.ll_4).setVisibility(View.VISIBLE);
                holder.getView(R.id.btn).setVisibility(View.VISIBLE);
            } else {
                holder.getTextView(R.id.quote).setVisibility(View.VISIBLE);
                holder.getView(R.id.ll_4).setVisibility(View.GONE);
                holder.getView(R.id.btn).setVisibility(View.GONE);
                String quote = XString.getStr(data, "quoted");
                if (TextUtils.isEmpty(quote)) {
                    holder.getTextView(R.id.quote).setText("此订单您没有报价");
                } else {
                    holder.getTextView(R.id.quote).setText("此订单您报价" + quote + "元");
                }
            }

            int len = 0;
            JSONArray codes = XString.getJSONArray(data, JsonName.CODES);
            if (codes != null)
                len = codes.length();
            holder.getLinerLayout(R.id.ll_pass).removeAllViews();
            for (int i = 0; i < len; i++) {
                try {
                    JSONObject pinfo = codes.getJSONObject(i);
                    View view = mActivity.getLayoutInflater().inflate(
                            R.layout.i_passenger_order, null);
                    TextView time = (TextView) view.findViewById(R.id.order_detail_passingdate_textview);
                    TextView address = (TextView) view.findViewById(R.id.order_detail_passingaddress_textview);
                    time.setText(DateUtils.getSimpleFormatTime(XString.getLong(pinfo, JsonName.WAYDATE)));
                    address.setText(AppUtils.getAddress(
                            XString.getStr(pinfo, JsonName.PROVINCE),
                            XString.getStr(pinfo, JsonName.CITY),
                            XString.getStr(pinfo, JsonName.ADDRESS)));
                    holder.getLinerLayout(R.id.ll_pass).addView(view);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getListData() {
        AsyncHttpClient client = MyHelper.getClient();
        final RequestParams params = MyHelper.getParams();
        params.add(RequestName.PAGE, page + "");
        params.add(RequestName.PAGE_SIZE, pageSize + "");
        client.post(mActivity, Net.ORDERLIST, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progressBar.setVisibility(View.GONE);
                srl.setRefreshing(false);
                srl.setLoading(false);
                LogUtils.d(responseString);
                try {
                    JSONObject result = new JSONObject(responseString);
                    JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                    if (XString.getBoolean(result, JsonName.STATUS)) {
                        List<JSONObject> resList = XString.getList(data, JsonName.ORDER_LIST);
                        if (page == 0) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                        }
                        list.addAll(resList);
                        adapter.notifyItemRangeInserted(page, resList.size() - 1);
                        if(isload){
                            page = list.size();
                        }

                        if (list.size() == 0) {
                            noOrder.setVisibility(View.VISIBLE);
                            srl.setVisibility(View.GONE);
                        } else {
                            noOrder.setVisibility(View.GONE);
                            srl.setVisibility(View.VISIBLE);
                        }
                    } else {
                        mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                    }
                } catch (JSONException e) {
                    mActivity.app.toast("数据格式错误");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwabl) {
                progressBar.setVisibility(View.GONE);
                srl.setRefreshing(false);
                srl.setLoading(false);
                mActivity.app.toast("网络链接错误，请稍后再试！");
            }
        });
    }


    private void showPopupWindow(View v) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.order_soft_pop_window, null);
        // 设置按钮的点击事件
        Button button = (Button) contentView.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "button is pressed",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //  Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.mipmap.icon_order_bus));

        // 设置好参数之后再show
        // popupWindow.showAsDropDown(v);
        // popupWindow.isShowing(mToolbar, 800,0, Gravity.RIGHT);
        // popupWindow.showAtLocation(mToolbar,800,0, Gravity.RIGHT);
        // popupWindow.showAsDropDown(mToolbar, 1200,0, Gravity.RIGHT);
        // popupWindow.showAsDropDown(mToolbar);

    }

}
