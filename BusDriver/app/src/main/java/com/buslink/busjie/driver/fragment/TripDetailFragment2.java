package com.buslink.busjie.driver.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.ScanPhotosActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.AppUtils;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.ViewHolder;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.SH;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import cz.msebera.android.httpclient.Header;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TripDetailFragment2 extends LevelTwoFragment {

    ViewHolder holder;
    JSONObject data;
    String orid;
    //  int passengerStar;
    String img, name, uid, pstar, did;


    List<List<Map<String, Object>>> list;
    @Bind(R.id.days)
    LinearLayout ll;


    ArrayList<String> plist;
    private Intent intent;

    //照片墙recycleview
    @Bind(R.id.rv)
    RecyclerView rv;
    RecyclerView.Adapter adapterImg;
    ImageView iv_photo_item;

    //删除功能
    MenuItem edit;
    boolean editEnable;


    @Override
    public String getTitle() {
        return "行程详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.f_trip_detail;
    }

    @Override
    protected void initView() {


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        super.onViewCreated(view, savedInstanceState);
        holder = new ViewHolder(view);
        holder.setTag(R.id.tv, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_start_time,
                R.id.tv_start_address, R.id.tv_end_time, R.id.tv_end_address, R.id.ll_pass, R.id.bt);
        holder.setTag(R.id.tv_baojia, R.id.tv_num_of_people, R.id.tv_licheng, R.id.tv_schedule);

        getData();
    }

    @Subscriber(tag = "tripDetal")//
    void upData(String msg){
        switch (msg){
            case "upDateTripDetail":
                getData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void getData() {
        RequestParams params = new RequestParams();
        intent = mActivity.getIntent();
        params.addBodyParameter("orid", intent.getStringExtra("orid"));
        params.addBodyParameter("pushid", intent.getStringExtra("pushid"));
        RequestManager.tripDetail(params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject res = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(res, JsonName.DATA);
                if (XString.getBoolean(res, JsonName.STATUS)) {

                    isDeal(data);
                    holder.<TextView>getTag(R.id.tv).setText("订单编号：" + XString.getStr(data, "orderno"));
                    holder.<TextView>getTag(R.id.tv_1).setText(String.format("请您于%s前往%s乘车",
                            DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)),
                            AppUtils.getAddress(XString.getStr(data, JsonName.START_PROVINCE),
                                    XString.getStr(data, JsonName.START_CITY),
                                    XString.getStr(data, JsonName.START_ADDRESS))));
                    holder.<TextView>getTag(R.id.tv_baojia).setText("订单金额：" + XString.getStr(data, "quoted") + "元");
                    if (!TextUtils.isEmpty(XString.getStr(data, JsonName.NAME))) {
                        holder.<TextView>getTag(R.id.tv_2).setText(String.format("乘客：%s", XString.getStr(data, JsonName.NAME)));
                    }
                    if (!TextUtils.isEmpty(XString.getStr(data, "entname"))) {
                        holder.<TextView>getTag(R.id.tv_3).setText(String.format("公司名称：%s", XString.getStr(data, "entname")));
                    }
                    holder.<TextView>getTag(R.id.tv_4).setText(XString.getStr(data, JsonName.PHONE));

                    if (!TextUtils.isEmpty(XString.getStr(data, JsonName.TOTAL))) {
                        holder.<TextView>getTag(R.id.tv_num_of_people).setText(String.format("订单人数：%s人（含儿童）", XString.getStr(data, JsonName.TOTAL)));

                    }
                    if (!TextUtils.isEmpty(XString.getStr(data, JsonName.DAYS))) {
                        holder.<TextView>getTag(R.id.tv_schedule).setText(String.format("共%s天", XString.getStr(data, JsonName.DAYS)));

                    }

                    String a = XString.getStr(data, JsonName.DRIVING_RANGE).toString();
                    Log.d("licheng", XString.getStr(data, JsonName.DRIVING_RANGE).toString());
                    if (!TextUtils.isEmpty(XString.getStr(data, JsonName.DRIVING_RANGE))) {
                        if (XString.getStr(data, JsonName.DRIVING_RANGE).toString().equals("0")) {
                            holder.<TextView>getTag(R.id.tv_licheng).setText(String.format("里程：%s", "未统计"));


                        } else {
                            holder.<TextView>getTag(R.id.tv_licheng).setText(String.format("里程：%s", XString.getStr(data, JsonName.DRIVING_RANGE)));

                        }
                    }


//                    holder.<TextView>getTag(R.id.tv_start_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)));
//                    holder.<TextView>getTag(R.id.tv_end_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.END_DATE)));
//                    holder.<TextView>getTag(R.id.tv_start_address).setText(AppUtils.getAddress("", XString.getStr(data, JsonName.START_CITY),
//                            XString.getStr(data, JsonName.START_ADDRESS)));
//                    holder.<TextView>getTag(R.id.tv_end_address).setText(AppUtils.getAddress("", XString.getStr(data, JsonName.END_CITY),
//                            XString.getStr(data, JsonName.END_ADDRESS)));
//                    JSONArray pass = XString.getJSONArray(data, JsonName.CODES);
//                    int size = pass.length();
//                    holder.<LinearLayout>getTag(R.id.ll_pass).removeAllViews();
//                    for (int i = 0; i < size; i++) {
//                        JSONObject o = XString.getJSONObject(pass, i);
//                        View view = mActivity.getLayoutInflater().inflate(
//                                R.layout.i_passenger_order, null);
//                        TextView time = (TextView) view.findViewById(R.id.order_detail_passingdate_textview);
//                        TextView address = (TextView) view.findViewById(R.id.order_detail_passingaddress_textview);
//                        time.setText(DateUtils.getSimpleFormatTime(XString.getLong(o, JsonName.WAYDATE)));
//                        address.setText("途径：" + AppUtils.getAddress("",
//                                XString.getStr(o, JsonName.CITY),
//                                XString.getStr(o, JsonName.ADDRESS)));
//                        holder.<LinearLayout>getTag(R.id.ll_pass).addView(view);
//                    }
                    if (XString.getInt(data, JsonName.ORDERSTATE) == 4) {
                        holder.<Button>getTag(R.id.bt).setVisibility(View.VISIBLE);
                        orid = XString.getStr(data, JsonName.ORID);
                        pstar = XString.getStr(data, JsonName.PSTAR);
                        uid = XString.getStr(data, JsonName.UID);
                        name = XString.getStr(data, JsonName.NAME);
                        img = XString.getStr(data, JsonName.IMG);
                        did = XString.getStr(data, JsonName.DID);
                    } else {
                        holder.<Button>getTag(R.id.bt).setVisibility(View.GONE);
                    }
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
                //行程详细日程安排

                String ordertype = XString.getStr(data, JsonName.ORDER_TYPE);

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


                        //多日游
                        day.findViewById(R.id.ll).setVisibility(View.VISIBLE);
                        tvWhichDay.setText(String.format("第%d天", i + 1));

                        if (j == 0) {
                            tvStartOneDayTime.setText(DateUtils.getYYYYMMdd((long) m.get(JsonName.TIME)));
                            tvStartTime.setText(DateUtils.getHHmm((long) m.get(JsonName.TIME)));
                            tvStartAddress.setText(
                                    AppUtils.getAddress(
                                            (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
                        } else if (j == max - 1) {
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

                //结束


                //行程表图片
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

            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    private void isDeal(final JSONObject data) {

        // isdel	是否可删除 0不可删除  1可删除
        if(XString.getStr(data, JsonName.ISDEL).equals("1")){
            mToolbar.inflateMenu(R.menu.menu_user_info);
            Menu menu = mToolbar.getMenu();
            edit = menu.getItem(0);
            edit.setTitle("删除行程");
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_user_info_edit:
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setMessage("刪除该行程吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    AsyncHttpClient client = new AsyncHttpClient();
                                    com.loopj.android.http.RequestParams params = MyHelper.getParams();
                                    params.add("pushid", XString.getStr(data, "pushid"));
                                    client.post(mActivity, Net.DELETE_ORDER, params, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            if (XString.getBoolean(response, JsonName.STATUS)) {
                                                mActivity.app.toast("删除成功");

                                                EventBus.getDefault().post(new MyEvent(""), "tripdetail");
                                                getActivity().finish();
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
                            break;
                    }
                    return false;
                }
            });

        }


    }

    @OnClick(R.id.bt)
    void toProise() {
        Intent intent = new Intent();
        intent.putExtra(JsonName.ORID, orid);
        intent.putExtra(JsonName.PSTAR, pstar);
        intent.putExtra(JsonName.DID, did);
        intent.putExtra(JsonName.IMG, img);
        intent.putExtra(JsonName.NAME, name);
        intent.putExtra(JsonName.UID, uid);
        mActivity.startFragment(BackActivity.class, JudgePassengerFragment.class, intent);
        getData();
    }

    private void setPhotos(final List<String> list) {


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


    public void setZero(TextView tv, Boolean type) {//true为新订单type为5或9的，false为老订单
        if (type) {
            tv.setText("00时00分");
        } else {
            tv.setText("0000-00-00-00 00:00");
        }
        tv.setTextColor(Color.parseColor("#f7f7f8"));
    }


}


//if ("5".equals(ordertype) || "9".equals(ordertype)) { } else
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
//        if (m.get(JsonName.TIME).toString().equals("0")) {
//        setZero(tvEndTime, false);
//        } else {
//        tvEndTime.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//
//        }
//        tvEndAddress.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        } else {
//        View p = LayoutInflater.from(getActivity()).inflate(R.layout.i_passenger_order, pass, false);
//
//        TextView tv = (TextView) p.findViewById(R.id.order_detail_passingdate_textview);
//        if (m.get(JsonName.TIME).toString().equals("0")) {
//        setZero(tv, false);
//        } else {
//        tv.setText(DateUtils.getSimpleFormatTime((long) m.get(JsonName.TIME)));
//
//        }
//
//        TextView tv1 = (TextView) p.findViewById(R.id.order_detail_passingaddress_textview);
//        tv1.setText(
//        AppUtils.getAddress(
//        (String) m.get(JsonName.PROVINCE), (String) m.get(JsonName.CITY), (String) m.get(JsonName.ADDRESS)));
//        pass.addView(p);
//        }
//        }
