package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.adapter.SpaceItemDecoration;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.CircleTransform;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/6.
 */
public class AdListDetailFragment extends LevelTwoFragment {
    private Intent intent;
    private View v;
    private String pid;
    private RecyclerView.Adapter adapter;
    private List<JSONObject> list = new ArrayList<>();
    private JSONObject mheadjo=null;

    @Bind(R.id.srl)
    SwipeRefreshLayout srl;
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.no_carpooling_detail)
    TextView noCarpooling;
    @Bind(R.id.progressbar)
    ProgressBar progressBar;


    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    //private View mHeaderView=null;
    SimpleHolder headholder=null;


////    @Bind(R.id.addtime)
//    TextView addtime;
////    @Bind(R.id.order_state)
//    TextView order_state;
////    @Bind(R.id.start_time)
//    TextView start_time;
// //   @Bind(R.id.start_address)
//    TextView start_address;
////    @Bind(R.id.end_address)
//    TextView end_address;
// //   @Bind(R.id.estimated_time)
//    TextView estimated_time;
// //   @Bind(R.id.car_mileage)
//    TextView car_mileage;
// //   @Bind(R.id.empty_position)
//    TextView empty_position;
// //   @Bind(R.id.price)
//    TextView price;


    JSONObject data;
    private int page = 0, pageSize = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = LayoutInflater.from(container.getContext()).inflate(R.layout.ad_list_detail_fragment, container, false);
        ButterKnife.bind(this, v);
        intent = getActivity().getIntent();
        //  intent.putExtra(JsonName.PID, pid);
        pid = intent.getStringExtra(JsonName.PID);
        progressBar.setVisibility(View.VISIBLE);
        getOrderData(pid);
        getListData();
        return v;
    }

    @Override
    public String getTitle() {
        return "详情";
    }

    @Override
    protected int getResLayout() {
        return R.layout.ad_list_detail_fragment;
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


                final SimpleHolder holder = new SimpleHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_ad_detail, parent, false));
                    holder.setTag(R.id.tv_order_nb, R.id.iv_passenger, R.id.tv_name,
                            R.id.tv_phone_num, R.id.tv_booking,
                            R.id.tv_pay_state, R.id.tv_pay_price,
                            R.id.bt_is_in,R.id.tv_state);

                 headholder=new SimpleHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ad_header, parent, false));
                headholder.setTag(R.id.addtime,R.id.order_state,R.id.start_time,R.id.start_address,
                        R.id.end_address,R.id.estimated_time,R.id.car_mileage,R.id.empty_position,R.id.price);





//                //head
//                mHeaderView=LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_header, rv, false);
//                //    @Bind(R.id.addtime)
//                 addtime=(TextView)mHeaderView.findViewById(R.id.addtime);
////    @Bind(R.id.order_state)
//                order_state=(TextView)mHeaderView.findViewById(R.id.order_state);
////    @Bind(R.id.start_time)
//                start_time=(TextView)mHeaderView.findViewById(R.id.start_time);
//                //   @Bind(R.id.start_address)
//                start_address=(TextView)mHeaderView.findViewById(R.id.start_address);
////    @Bind(R.id.end_address)
//                end_address=(TextView)mHeaderView.findViewById(R.id.end_address);
//                estimated_time=(TextView)mHeaderView.findViewById(R.id.estimated_time);
//                car_mileage=(TextView)mHeaderView.findViewById(R.id.car_mileage);
//                empty_position=(TextView)mHeaderView.findViewById(R.id.empty_position);
//                price=(TextView)mHeaderView.findViewById(R.id.price);



                return viewType == TYPE_HEADER ? headholder : holder;
            }
            @Override
            public void onBindViewHolder(final SimpleHolder holder, int position) {
                if(getItemViewType(position) == TYPE_HEADER) {
                    if(mheadjo!=null){
                        data = XString.getJSONObject(mheadjo, JsonName.DATA);

                        int state = XString.getInt(data, JsonName.ORDERREQUIRESTATE);//订单状态
                        switch (state) {
                            case 0:
                                headholder.getTextView(R.id.order_state).setText("正在售票");
                                headholder.getTextView(R.id.order_state).setTextColor(getResources().getColor(R.color.green));
                                break;
                            case 1:
                                headholder.getTextView(R.id.order_state).setText("票已售完");
                                headholder.getTextView(R.id.order_state).setTextColor(getResources().getColor(R.color.red));
                                break;
                            case 2:
                                headholder.getTextView(R.id.order_state).setText("停止售票");
                                headholder.getTextView(R.id.order_state).setTextColor(getResources().getColor(R.color.red));
                                break;
                            case 3:
                                headholder.getTextView(R.id.order_state).setText("票已过期");
                                headholder.getTextView(R.id.order_state).setTextColor(getResources().getColor(R.color.text_secead));
                                break;
                        }

                        headholder.getTextView(R.id.addtime) .setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.ADD_TIME)));//发布时间
                        headholder.getTextView(R.id.start_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.START_DATE)));//开始时间
                        headholder.getTextView(R.id.start_address).setText(XString.getStr(data, JsonName.START_ADDRESS));
                        headholder.getTextView(R.id.end_address).setText(XString.getStr(data, JsonName.END_ADDRESS));
                        if (XString.getLong(data, JsonName.DURATION) == 0) {
                            headholder.getTextView(R.id.estimated_time) .setText("未统计");
                        } else {
                            headholder.getTextView(R.id.estimated_time).setText(DateUtils.getSimpleFormatTime(XString.getLong(data, JsonName.DURATION)));
                        }
                        headholder.getTextView(R.id.car_mileage).setText(XString.getStr(data, JsonName.DRIVING_RANGE));
                        headholder.getTextView(R.id.empty_position).setText(XString.getStr(data, JsonName.SEATTOTAL) + "座");//余座
                        headholder.getTextView(R.id.price).setText(XString.getStr(data, JsonName.PRICE) + "元/位");
                    }

                }else if(getItemViewType(position) == TYPE_NORMAL){


                    //   position=position-1;
                    holder.setIndex(position-1);
                    final JSONObject jo = list.get(position-1);
                    holder.getTextView(R.id.tv_order_nb).setText("订单号：" + XString.getStr(jo, JsonName.ORDERNO));
                    Picasso.with(getActivity())
                            .load(Net.IMGURL + XString.getStr(jo, JsonName.IMG))
                            .placeholder(R.mipmap.home_wo_default)
                            .error(R.mipmap.home_wo_default)
                            .transform(new CircleTransform())
                            .into(holder.<ImageView>getTag(R.id.iv_passenger));
                    // holder.getImageView(R.id.iv_passenger).setText();
                    holder.getTextView(R.id.tv_name).setText(XString.getStr(jo, JsonName.NAME));
                    holder.getTextView(R.id.tv_phone_num).setText("电话：" + XString.getStr(jo, JsonName.PHONE));
                    holder.getTextView(R.id.tv_booking).setText("订座：" + XString.getStr(jo, JsonName.SEATTOTAL) + "个");
                    //订单状态1已支付;2. 订票失败（退款到零钱）3订单已退款（乘客申请退款）
                    String orderstate = null;
                    switch (XString.getInt(jo, JsonName.ORDERSTATE)) {
                        case 1:
                            orderstate = "已支付";
                            break;
                        case 2:
                            orderstate = "订票失败";
                            break;
                        case 3:
                            orderstate = "已退款";
                            break;
                    }
                    holder.getTextView(R.id.tv_pay_state).setText(orderstate);
                    holder.getTextView(R.id.tv_pay_price).setText(XString.getStr(jo, JsonName.QUOTED) + "元");//支付金额
                    //司机检票0 未检票（乘客未到）；1已检票（乘客已到）; 默认0

//                orderrequirestate  订单状态 1订单正常;2. 订票失败（退款到零钱）3订单已退款（乘客申请退款）  ;默认1
//                        orderrequirestate=1 正常订单才有检票环节;
                    if (XString.getInt(jo, JsonName.ORDERREQUIRESTATE) == 1) {
                        String isin ="未知错误";
                        String isinstate="未知错误";

                        switch (XString.getInt(jo, JsonName.ISARRIVE)) {
                            case 0:
                                isinstate = "未检票";
                                isin = "已检票";
                                holder.getTextView(R.id.tv_state).setTextColor(Color.parseColor("#c1c1bf"));//灰色

                                holder.getButton(R.id.bt_is_in).setTextColor(getResources().getColor(R.color.wbg));
                                break;
                            case 1:
                                isinstate = "已检票";
                                isin = "未检票";
                                holder.getTextView(R.id.tv_state).setTextColor(getResources().getColor(R.color.green));//绿色

                                holder.getButton(R.id.bt_is_in).setTextColor(Color.parseColor("#DB7093"));//紫红色
                                break;
                            default:
                                isinstate = "未检票";
                                isin = "已检票";
                                holder.getTextView(R.id.tv_state).setTextColor(Color.parseColor("#c1c1bf"));//灰色

                                holder.getButton(R.id.bt_is_in).setTextColor(getResources().getColor(R.color.wbg));
                                break;
                        }

                        holder.getTextView(R.id.tv_state).setText(isinstate);



//                    isexist	切换票状态功能是否存在  0不存在  1 存在
//                    备注：isexist=0  按钮为灰色点击不调接口
                        if (XString.getInt(jo, JsonName.ISEXIST) == 0) {
                            holder.getButton(R.id.bt_is_in).setVisibility(View.GONE);

//                            holder.getButton(R.id.bt_is_in).setText(isin);
//                            holder.getButton(R.id.bt_is_in).setBackgroundColor(Color.parseColor("#e6e6e6"));
//                            holder.getButton(R.id.bt_is_in).setEnabled(false);
                        } else if (XString.getInt(jo, JsonName.ISEXIST) == 1) {
                            holder.getButton(R.id.bt_is_in).setVisibility(View.VISIBLE);
                            holder.getButton(R.id.bt_is_in).setText(isin);
                            holder.getButton(R.id.bt_is_in).setEnabled(true);
                            holder.getButton(R.id.bt_is_in).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkIsArrive(jo, holder, XString.getInt(jo, JsonName.ISARRIVE));
                                }
                            });
                        }
                    }
                }

            }

            @Override
            public int getItemViewType(int position) {
                if(headholder == null) return TYPE_NORMAL;
                if(position == 0) return TYPE_HEADER;
                return TYPE_NORMAL;
            }

            @Override
            public int getItemCount() {
                return list.size()+1;
            }
        });
    }

    private void checkIsArrive(final JSONObject jo, final SimpleHolder holder, final int isin) {
        // 司机检票0 未检票（乘客未到）；1已检票（乘客已到）; 默认0
        final RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter(JsonName.PID, XString.getStr(jo, JsonName.PID));
        if (isin == 0) {
            params.addBodyParameter(JsonName.ISARRIVE, "1");
        } else if (isin == 1) {
            params.addBodyParameter(JsonName.ISARRIVE, "0");
        }
        params.addBodyParameter(JsonName.OID, XString.getStr(jo, JsonName.OID));
        HttpUtils http = new HttpUtils();
//        票状态： 0.正在售票；1.票已售完；2.停止售票；3. 票已过期; 默认0
//        票状态，前端只能进行0和2之间切换；
        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_ISARRIVE, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo2 = XString.getJSONObject(responseInfo.result);
                if (XString.getBoolean(jo2, JsonName.STATUS)) {
                    int index = holder.getIndex();
                    if (isin == 0) {
                        XString.put(jo, JsonName.ISARRIVE, 1);
                    } else if (isin == 1) {
                        XString.put(jo, JsonName.ISARRIVE, 0);
                    }
                    //adapter.notifyItemChanged(index);
                    adapter.notifyDataSetChanged();
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


    public void getOrderData(String pid) {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter(JsonName.PID, pid);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_CARSEATPUBLISHDETAIL, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);

                progressBar.setVisibility(View.GONE);
                srl.setRefreshing(false);
                srl.setLoading(false);


                JSONObject jo = XString.getJSONObject(responseInfo.result);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    mheadjo=jo;
                } else {
                    JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
//                if (null != progressBar) {
//                    progressBar.setVisibility(View.GONE);
//                }
                mActivity.app.toast("您的网络不给力");
            }
        });


    }

    public void getListData() {//获取乘客拼车列表
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("page", page + "");
        params.addBodyParameter("pagesize", pageSize + "");
        params.addBodyParameter(JsonName.PID, pid);
        HttpUtils http = new HttpUtils();
        //拼车列表
        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING_ORDERPAYLIST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                // {"data":{"carpoolinglst":[]},"status":true}
                srl.setLoading(false);
                srl.setRefreshing(false);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    //carpooling_orderpaylist
                    JSONArray adlist = XString.getJSONArray(data, "carpoolinglst");
                    if (adlist.length() == 0) {
                        if (page == 0) {
                            mActivity.app.toast("暂时没有乘客订车");
                        } else {
                            mActivity.app.toast("没有更多数据");
                        }
                        return;
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
                mActivity.app.toast("您的网络不给力");
            }
        });
    }
}
