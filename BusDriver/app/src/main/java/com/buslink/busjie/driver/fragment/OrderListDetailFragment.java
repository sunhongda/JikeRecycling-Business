package com.buslink.busjie.driver.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.ScanPhotosActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SH;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2015/11/28
 * 订单详情页
 */
public class OrderListDetailFragment extends LevelTwoFragment {
    private Intent intent;
    @ViewInject(R.id.progressbar)
    ProgressBar progressBar;

    JSONObject data;
    LinearLayout ll_photos;


    //照片墙recycleview
    @Bind(R.id.rv)
    RecyclerView rv;
    RecyclerView.Adapter adapterImg;
    ImageView iv_photo_item;
    ArrayList<String> plist;

    @Bind(R.id.et_quote_2)
    EditText et_quote2;
    @Bind(R.id.bt_edit_quote)
    Button bt_edit_quote;
    @Bind(R.id.ll_edit_quote)
    LinearLayout ll_edit_quote;

    String quote;

    @Bind(R.id.tv_refund)
    TextView tvRefund;
    @Bind((R.id.ll_refund))
    LinearLayout llRefund;
    @Bind(R.id.tv_alreadypay)
    TextView tvAlreadypay;
    @Bind((R.id.ll_alreadypay))
    LinearLayout llAlreadypay;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public String getTitle() {
        return "订单信息";
    }

    @Override
    protected int getResLayout() {
        return R.layout.f_orderlist_detail;
    }

    private int where;
    private boolean detaile, flag;
    //private LinkedList<JSONObject> list;//数据集合
    private String responseString;
    List<List<Map<String, Object>>> list;
    @Bind(R.id.days)
    LinearLayout ll;


    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(container.getContext()).inflate(R.layout.f_orderlist_detail, container, false);
        ButterKnife.bind(this, v);
        intent = getActivity().getIntent();
        intent.setClass(getActivity(), ScanPhotosActivity.class);
        getOrderData();
        return v;
    }

    private void setPhotos(final List<String> list) {
        //Net.IMGURL + XString.getStr(data, JsonName.CAR_IMG)

        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rv.setAdapter(adapterImg = new RecyclerView.Adapter<SH>() {


            @Override
            public SH onCreateViewHolder(ViewGroup parent, int viewType) {
                final SH sh = new SH(LayoutInflater.from(getActivity()).inflate(R.layout.i_order_photowall, parent, false));
                sh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.setClass(getActivity(), ScanPhotosActivity.class);
                        intent.putStringArrayListExtra("plist", plist);
                        intent.putExtra("currentposition", String.valueOf(sh.getIndex()));
                        startActivity(intent);
                    }
                });
                return sh;
            }

            @Override
            public void onBindViewHolder(SH holder, int position) {
                // holder.<ImageView>getView(R.id.iv_photowall).setImageBitmap(BitmapFactory.decodeFile(listImg.get(position).get("img")));
                holder.setIndex(position);
                BitmapUtils bUtils = new BitmapUtils(getActivity());
                bUtils.display(holder.<ImageView>getView(R.id.iv_photowall), list.get(position));
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // getData();
    }

    protected void initView() {


    }


    //显示
    public void setZero(TextView tv, Boolean type) {//true为新订单type为5或9的，false为老订单
        if (type) {
            tv.setText("00时00分");
        } else {
            tv.setText("0000-00-00 00:00");
        }
        tv.setTextColor(Color.parseColor("#f7f7f8"));
    }

    private void getOrderData() {
        AsyncHttpClient client = new AsyncHttpClient();

        String orid = intent.getStringExtra("orid");
        String pushid = intent.getStringExtra("pushid");
        RequestParams params = MyHelper.getParams();
        params.add(JsonName.ORID, orid);
        params.add(JsonName.PUSHID, pushid);
        client.post(Net.ORDERDETAIL, params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwabl) {
                throwabl.printStackTrace();
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                XString.remove(jo, "detail");
//                XString.put(jo, "detail", responseString);
//                XString.put(jo, "detailVisible", true);
//                XString.put(jo, "isorder", 0);
//                rv.smoothScrollToPosition(index);
//                LogUtils.d(responseString);
//                adapter.notifyItemChanged(index);


                //  responseString = intent.getStringExtra("responseString");


                Log.d("responseString1", "WEQ" + responseString);
                final int[] bgc = {R.color.pink, R.color.green, R.color.green,
                        R.color.green, R.color.green, R.color.yellow,
                        R.color.purple, R.color.green, R.color.green, R.color.blue};
                //获取到了数据
                //解析数据给订单设置
                list = new LinkedList<>();
                JSONObject r = XString.getJSONObject(responseString);
                data = XString.getJSONObject(r, JsonName.DATA);
                // String ordertype=XString.getStr(data, JsonName.ORDER_TYPE);


               // int bg = XString.getInt(data, JsonName.ORDERSTATE) - 1;
               // int bg=1+(int)(Math.random()*9);
                //  cv.setCardBackgroundColor(getResources().getColor(bgc[bg]));

//                CardView cv
//                   CardView cv= (CardView) v.findViewById(R.id.cv);
//                    cv.setCardBackgroundColor(getResources().getColor(R.color.green));
//                   cv.setPivotY(0);

//                if (bg > 0) {
//                    cv.setCardBackgroundColor(getResources().getColor(bgc[bg]));
//                }



                // 请求到详细数据后
                if (XString.getBoolean(r, JsonName.STATUS)) {
                    list = new LinkedList<>();//每一天的集合
                    List<Map<String, Object>> l = new LinkedList<>();//途径点的集合
                    Map<String, Object> m = new HashMap<>();//放时间，地点   途径点
                    l.add(m);
                    list.add(l);
                    m.put(JsonName.TIME, XString.getLong(data, JsonName.START_DATE));
                    m.put(JsonName.CITY, XString.getStr(data, JsonName.START_CITY));
                    m.put(JsonName.PROVINCE, XString.getStr(data, JsonName.START_PROVINCE));
                    m.put(JsonName.ADDRESS, XString.getStr(data, JsonName.START_ADDRESS));

                    JSONArray codes = XString.getJSONArray(data, JsonName.CODES);
                    int size = codes.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject jo = XString.getJSONObject(codes, i);
                        int j = XString.getInt(jo, JsonName.DAY);
                        if (list.size() < j) {
                            l = new LinkedList<>();
                            list.add(l);
                        }
                        l = list.get(j - 1);
                        m = new HashMap<>();
                        l.add(m);
                        m.put(JsonName.TIME, XString.getLong(jo, JsonName.WAYDATE));
                        m.put(JsonName.CITY, XString.getStr(jo, JsonName.CITY));
                        m.put(JsonName.PROVINCE, XString.getStr(jo, JsonName.PROVINCE));
                        m.put(JsonName.ADDRESS, XString.getStr(jo, JsonName.ADDRESS));
                    }
                    l = list.get(list.size() - 1);
                    m = new HashMap<>();
                    l.add(m);
                    m.put(JsonName.TIME, XString.getLong(data, JsonName.END_DATE));
                    m.put(JsonName.CITY, XString.getStr(data, JsonName.END_CITY));
                    m.put(JsonName.PROVINCE, XString.getStr(data, JsonName.END_PROVINCE));
                    m.put(JsonName.ADDRESS, XString.getStr(data, JsonName.END_ADDRESS));

                    //  final SimpleHolder holdr = new SimpleHolder(LayoutInflater.from(container.getContext()).inflate(R.layout.f_orderlist_detail,container, false));
                    //订单页用到的数据tag
//        holdr.setTag(R.id.rl, R.id.tv_1_0, R.id.tv_1_1, R.id.tv_1_2, R.id.tv_start_time, R.id.tv_start_address, R.id.ll_pass,
//                //给第二天的布局设置tag
//                R.id.ll_pass_second_day,
//                R.id.tv_end_time,
//                R.id.tv_end_address, R.id.tv_days, R.id.tv_cars_number, R.id.tv_car_type, R.id.tv_driver_welfare, R.id.bt, R.id.tv_1_3);
//            RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl);
                    TextView tv_1_0 = (TextView) v.findViewById(R.id.tv_1_0);//订单号
                    tv_1_0.setText(String.format("订单号：%s", XString.getStr(data, JsonName.ORDERNO)));
                    TextView tv_1_1 = (TextView) v.findViewById(R.id.tv_1_1);//订单号右边的时间
                    tv_1_1.setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));
                    TextView tv_1_2 = (TextView) v.findViewById(R.id.tv_1_2);//总人数
                    tv_1_2.setText(String.format("%s人（含儿童）", XString.getStr(data, JsonName.TOTAL)));
                    TextView tv_cars_number = (TextView) v.findViewById(R.id.tv_cars_number);//用车数量
                    tv_cars_number.setText(String.format("共%d辆", 1));
//            TextView tv_car_type = (TextView) v.findViewById(R.id.tv_car_type);//车型
//            tv_car_type.setText(XString.getStr(data, JsonName.CAR_TYPE));
                    TextView tv_car_type = (TextView) v.findViewById(R.id.tv_car_age);//车型
                    tv_car_type.setText(XString.getStr(data, JsonName.CAR_AGE));

                    //  订单类型 1接/送飞机 2广告单 3接/送火车 5旅游包车 6单位班车 7 会议用车 8预约巴士9 新版旅游包车
                    if (XString.getInt(data, JsonName.ORDER_TYPE) == 1 || XString.getInt(data, JsonName.ORDER_TYPE) == 3) {//接飞机
                        LinearLayout ll_plane = (LinearLayout) v.findViewById(R.id.ll_plane);
                        ll_plane.setVisibility(View.VISIBLE);
                        if (XString.getInt(data, JsonName.ORDER_TYPE) == 1) {
                            TextView tv_flight_train_number_pre = (TextView) v.findViewById(R.id.tv_flight_train_number_pre);
                            tv_flight_train_number_pre.setText("航班班次");
                        } else {
                            TextView tv_flight_train_number_pre = (TextView) v.findViewById(R.id.tv_flight_train_number_pre);
                            tv_flight_train_number_pre.setText("火车车次");
                        }


                        if (XString.getStr(data, JsonName.F_Number).equals(null) ||   //空则不显示
                                XString.getStr(data, JsonName.F_Number).trim().equals("")) {
                            ll_plane.setVisibility(View.GONE);
                        } else {
                            ll_plane.setVisibility(View.VISIBLE);
                            TextView tv_flight_number = (TextView) v.findViewById(R.id.tv_flight_number);
                            tv_flight_number.setText(XString.getStr(data, JsonName.F_Number));//航班号或火车车次
                        }

                    }


                    TextView tv_driver_welfare = (TextView) v.findViewById(R.id.tv_driver_welfare);//司机福利
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
                    tv_driver_welfare.setText(like);
                    TextView tv_days = (TextView) v.findViewById(R.id.tv_days);//总行程
                    tv_days.setText(String.format("共%s天", XString.getStr(data, JsonName.DAYS)));

                    TextView tv_1_3 = (TextView) v.findViewById(R.id.tv_1_3);
                    String s = XString.getStr(data, JsonName.DRIVING_RANGE);
                    tv_1_3.setText(TextUtils.isEmpty(s) ? "未统计" : s);//驾车里程
//            public final static String TYPE_OF_CAR= "typeofcar";//用车类型 1单程2往返
//            public final static String INTENT_BID="intentbid";//意向出价
//            public final static String IMG_SUM="imgsum";//行程单照片总数
//            public final static String IMG_LST="imglst";//行程单照片集合
//            String typeofcar=XString.getStr(data,JsonName.TYPE_OF_CAR);////用车类型 1单程2往返

                    TextView tv_intent_bid = (TextView) v.findViewById(R.id.tv_intent_bid);

                    String intent_bid = XString.getStr(data, JsonName.INTENT_BID);

                    if (intent_bid == null || intent_bid.equals("0")) {
                        tv_intent_bid.setText("没有意向出价");
                    } else {
                        tv_intent_bid.setText(intent_bid+"元");
                    }

                    if(XString.getInt(data,JsonName.ORDERSTATE)==11&&XString.getInt(data,JsonName.DRIVERPROFIT)>0){
                        llRefund.setVisibility(View.VISIBLE);
                        tvRefund.setText(XString.getStr(data, JsonName.DRIVERPROFIT) + "元");//退单补偿金
                    }
                    if(XString.getInt(data,JsonName.ORDERSTATE)==11&&XString.getInt(data,JsonName.ONE_PAY_MONEY)>0){
                        llAlreadypay.setVisibility(View.VISIBLE);
                        //已付金额
                        tvAlreadypay.setText(XString.getStr(data,JsonName.ONE_PAY_MONEY)+"元");
                    }


                    plist = new ArrayList();
                    JSONArray parry = XString.getJSONArray(data, JsonName.IMG_LST);
                    int len = parry.length();
                    for (int i = 0; i < len; i++) {
                        try {
                            String path = XString.getStr(parry.getJSONObject(i), "path");
                            Log.d("path", path);
                            plist.add(Net.IMGURL + XString.getStr(parry.getJSONObject(i), "path"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (plist.size() > 0) {
                        rv.setVisibility(View.VISIBLE);
                        setPhotos(plist);
                    }


                    size = list.size();
                    for (int i = 0; i < size; i++) {
                        l = list.get(i);
                        int max = l.size();
                        View day = LayoutInflater.from(getActivity()).inflate(R.layout.i_orderlist, ll, false);
                        TextView tvWhichDay = (TextView) day.findViewById(R.id.tv_which_day);
                        TextView tvStartOneDayTime = (TextView) day.findViewById(R.id.tv_start_one_day_time);
                        TextView tvStartTime = (TextView) day.findViewById(R.id.tv_start_time);
                        TextView tvStartAddress = (TextView) day.findViewById(R.id.tv_start_address);
                        TextView tvEndTime = (TextView) day.findViewById(R.id.tv_end_time);
                        TextView tvEndAddress = (TextView) day.findViewById(R.id.tv_end_address);
                        LinearLayout pass = (LinearLayout) day.findViewById(R.id.ll_pass);
                        for (int j = 0; j < max; j++) {
                            m = l.get(j);
//                    if (size == 1) {
//                      //  day.findViewById(R.id.ll).setVisibility(View.GONE);//去掉第一天
//                        tvWhichDay.setText("行程安排");
//                    } else {
//                        day.findViewById(R.id.ll).setVisibility(View.VISIBLE);
//                    }

                            //多日游           //含照片的
                            day.findViewById(R.id.ll).setVisibility(View.VISIBLE);
                            tvWhichDay.setText(String.format("第%d天", i + 1));

                            if (j == 0) {
                                tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
                                tvStartTime.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                                tvStartAddress.setText(
                                        AppUtils.getAddress(
                                                (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
                            } else if (j == max - 1) {
                                //TODO
                                //||null.equals(m.get(JsonName.TIME).toString())
                                if (m.get(JsonName.TIME).toString().equals("0")) {
                                    setZero(tvEndTime, true);
                                } else {
                                    tvEndTime.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                                }

                                tvEndAddress.setText(
                                        AppUtils.getAddress(
                                                (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));

                            } else {
                                View p = LayoutInflater.from(getActivity()).inflate(R.layout.i_passenger_order, pass, false);
                                TextView tv = (TextView) p.findViewById(R.id.order_detail_passingdate_textview);
                                TextView tv1 = (TextView) p.findViewById(R.id.order_detail_passingaddress_textview);

                                if (m.get(JsonName.TIME).toString().equals("0")) {
                                    //<color name="bg">#f7f7f8</color>
                                    //  tvEndTime.setTextColor(Color.parseColor("#f7f7f8"));
                                    setZero(tv, true);
                                } else {
                                    tv.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                                }

                                tv1.setText(
                                        AppUtils.getAddress(
                                                (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
                                pass.addView(p);
                            }


                        }
                        ll.addView(day);
                    }
                }
                Button btn = (Button) v.findViewById(R.id.btn);


                TextView tv_quote = (TextView) v.findViewById(R.id.quote);

                LinearLayout ll_4 = (LinearLayout) v.findViewById(R.id.ll_4);


                //原来底部的发送报价
//                if (XString.getInt(data, JsonName.ORDERSTATE) == 1) {
//                    tv_quote.setVisibility(View.GONE);
//                    ll_4.setVisibility(View.VISIBLE);
//                    btn.setVisibility(View.VISIBLE);
//
//
//                } else {
//                    tv_quote.setVisibility(View.VISIBLE);
//                    ll_4.setVisibility(View.GONE);
//                    btn.setVisibility(View.GONE);
//                    quote = XString.getStr(data, "quoted");
//                    if (TextUtils.isEmpty(quote)) {
//                        tv_quote.setText("此订单您没有报价");
//                    } else {
//                        tv_quote.setText("此订单您报价" + quote + "元");
//                    }
//                }
                //TODO  修改报价
                //isupdatequoted	是否可以修改报价  0不可以  1可以	Int
                int a=XString.getInt(data, JsonName.ORDERSTATE);
                int b=XString.getInt(data,JsonName.IS_UPDATE_QUOTED);
                //未报价和已报价都显示  1.未报价  2.已报价
                quote = XString.getStr(data, "quoted");
                if( (XString.getInt(data, JsonName.ORDERSTATE) == 2|| XString.getInt(data, JsonName.ORDERSTATE) == 1)&&XString.getInt(data,JsonName.IS_UPDATE_QUOTED)==1){
                    //编辑修改
                    ll_edit_quote.setVisibility(View.VISIBLE);
                    et_quote2.setHint(quote);
                    et_quote2.setEnabled(false);
                    bt_edit_quote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (bt_edit_quote.getText().equals("编辑")) {
                                et_quote2.setEnabled(true);
                                et_quote2.setHint(null);
                                bt_edit_quote.setText("确认");

                            } else if (bt_edit_quote.getText().equals("确认")) {

                                    if (TextUtils.isEmpty(et_quote2.getText().toString()) || TextUtils.equals("0", et_quote2.getText().toString())) {
                                        mActivity.app.toast("请输入正确报价");
                                    } else if(et_quote2.getText().toString().equals(quote)){
                                        mActivity.app.toast("与当前报价相同");
                                    }else if(Integer.parseInt(et_quote2.getText().toString())<100&&Integer.parseInt(et_quote2.getText().toString())>0){
                                        mActivity.app.toast("报价需大于100元");
                                    }
                                    else {
                                        String reqcarid = XString.getStr(data, "reqcarid");
                                        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
                                        params.addQueryStringParameter("orid", XString.getStr(data, "orid"));
                                        params.addQueryStringParameter("carid", XString.getStr(data, "carid"));
                                        params.addQueryStringParameter("reqcarid", reqcarid);
                                        params.addQueryStringParameter("quoted", et_quote2.getText().toString());
                                        RequestManager.orderPrice(params, new RequestCallBack<String>() {
                                            @Override
                                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                                JSONObject jo = XString.getJSONObject(responseInfo.result);
                                                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                                                if (XString.getBoolean(jo, JsonName.STATUS)) {
                                                    mActivity.app.toast("报价成功");
                                                    //getOrderData(holder);
                                                    InputMethodManager mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    mImm.hideSoftInputFromWindow(et_quote2.getWindowToken(), 0);
                                                    EventBus.getDefault().post("","upOrderListDetailFragment");
                                                    getActivity().finish();
                                                } else {
                                                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                                                }
                                            }

                                            @Override
                                            public void onFailure(HttpException e, String s) {
                                                mActivity.app.toast("很抱歉，报价失败");
                                                et_quote2.setHint(quote);

                                            }
                                        });
                                    }
                            }
                        }
                    });
                }else if(XString.getInt(data, JsonName.ORDERSTATE) == 2&&XString.getInt(data,JsonName.IS_UPDATE_QUOTED)==0){
                //  已报价但不能修改的状态
                    ll_edit_quote.setVisibility(View.VISIBLE);
                    et_quote2.setHint(quote);
                    et_quote2.setEnabled(false);
                    bt_edit_quote.setEnabled(false);

                }else{
                    if(quote.equals("0")){
                        tv_quote.setVisibility(View.VISIBLE);
                        ll_4.setVisibility(View.GONE);
                        btn.setVisibility(View.GONE);
                        tv_quote.setText("此订单您没有报价");

                    }else{
                        ll_edit_quote.setVisibility(View.VISIBLE);
                        et_quote2.setHint(quote);
                        et_quote2.setEnabled(false);
                        bt_edit_quote.setEnabled(false);
                    }
                }

                final EditText et = (EditText) v.findViewById(R.id.et);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(et.getText().toString()) || TextUtils.equals("0", et.getText().toString())) {
                            mActivity.app.toast("请输入正确报价");
                        } else {
//                    String detail = XString.getStr(data, "detail");
//                    JSONObject jo = XString.getJSONObject(detail);
//                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                            String reqcarid = XString.getStr(data, "reqcarid");
                            com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
                            params.addQueryStringParameter("orid", XString.getStr(data, "orid"));
                            params.addQueryStringParameter("carid", XString.getStr(data, "carid"));
                            LogUtils.d("carid" + XString.getStr(data, "carid"));
                            LogUtils.d("reqcarid" + reqcarid);
                            params.addQueryStringParameter("reqcarid", reqcarid);
                            params.addQueryStringParameter("quoted", et.getText().toString());
                            RequestManager.orderPrice(params, new RequestCallBack<String>() {
                                @Override
                                public void onSuccess(ResponseInfo<String> responseInfo) {
                                    JSONObject jo = XString.getJSONObject(responseInfo.result);
                                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                                    if (XString.getBoolean(jo, JsonName.STATUS)) {
                                        mActivity.app.toast("报价成功");
                                        //getOrderData(holder);
                                        InputMethodManager mImm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        mImm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                                        EventBus.getDefault().post("","upOrderListDetailFragment");
                                        //通知订单页刷新并刷新当前页
                                        getActivity().finish();

//                                page = 0;
//                                getListData();
                                    } else {
                                        mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                                    }
                                }

                                @Override
                                public void onFailure(HttpException e, String s) {
                                    mActivity.app.toast("很抱歉，报价失败");
                                }
                            });
                        }
                    }
                });


            }
        });
    }




}


//if("5".equals(ordertype)||"9".equals(ordertype)){}
//{
//        //老订单
//        day.findViewById(R.id.ll).setVisibility(View.GONE);//去掉第一天
//        if (j == 0) {
//        tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
//        //DataUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)
//        // tvStartTime.setText(DataUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
//        tvStartTime.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//        tvStartAddress.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        } else if (j == max - 1) {
//        tvEndTime.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//        tvEndAddress.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        } else {
//        View p = LayoutInflater.from(getActivity()).inflate(R.layout.i_passenger_order, pass, false);
//        TextView tv = (TextView) p.findViewById(R.id.order_detail_passingdate_textview);
//        TextView tv1 = (TextView) p.findViewById(R.id.order_detail_passingaddress_textview);
//
//
//        if(m.get(JsonName.TIME).toString().equals("0")){
//        setZero(tv,false);
//        }else{
//        //tv.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
//        //兼容老版本订单途径点时间格式
//        tv.setText(DateUtils.getSimpleFormatTime((Long) m.get(JsonName.TIME)));
//        }
//        tv1.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        pass.addView(p);
//        }
//        }




