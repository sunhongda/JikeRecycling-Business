package com.buslink.busjie.driver.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.IntroduceActivity;
import com.buslink.busjie.driver.activity.OrderCarpoolingActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.response.HomeResponse;
import com.buslink.busjie.driver.util.TypeHelp;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.HomeView;
import com.buslink.busjie.driver.view.ImageCycleView;
import com.buslink.busjie.driver.view.SlideShowView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class HomeFragment extends LevelOneFragment {
    private String imgResponseResult;
    private int isControl, orderState;
    private MediaPlayer mpStart;
    private View parent;

    ObjectAnimator ob;
    private final boolean start = true;
    private final boolean pause = false;

    private LocationClient mlocationClient;
    private MyLocationListener myListener = new MyLocationListener();
    HomeResponse response = new HomeResponse();

//    @Bind(R.id.bgab)
//    SlideShowView bgab;
    @Bind(R.id.icv)
    ImageCycleView icv;

    @Bind(R.id.iView)
    HomeView iv;
    @Bind(R.id.ordercontrol)
    CheckBox orderControl_btn;
    @Bind(R.id.order_red)
    TextView mOrderRed;
    @Bind(R.id.trip_red)
    TextView mTripRed;
    @Bind(R.id.carpooling_red)
    TextView mCarPoolingRed;

    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.tv_weather)
    TextView tvWeather;
    @Bind(R.id.iv_weather)
    ImageView ivWeather;
    @Bind(R.id.tv_temperature)
    TextView tvTemperature;

    Boolean isleft=true;
    int isorder =0;
    int istrip =0;
    int iscarpooling=0;


    //发拼车广告
//    @OnClick(R.id.ad)
//    void ad() {//发广告
//        if (isControl == 0) {
//            activity.app.toast("请耐心等待审核");
//            return;
//        }
//        Intent intent = new Intent(activity, BackActivity.class);
//        intent.putExtra("fragmentName", AdFragment2.class.getName());
//        Bundle bundle = new Bundle();
//        bundle.putInt("ispushad", response.getIspushad());
//        intent.putExtra("fragmentBundle", bundle);
//        activity.startActivity(intent);
//    }


    //新版本
    @OnClick(R.id.home_orderl)
    void horderl() {
   // activity.startFragment(BackActivity.class, OrderListFragment.class);//订单


        //订单有包车单和拼车单
      //  activity.startFragment(OrderCarpoolingActivity.class,);
        Intent intent=new Intent();
        intent.putExtra("isleft",isleft);
        intent.putExtra("isorder",isorder);
        intent.putExtra("iscarpooling",iscarpooling);
        intent.setClass(getActivity(),OrderCarpoolingActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.home_trip)
    void htrip() {
        activity.startFragment(BackActivity.class, TripListFragment.class);//行程
    }
    @OnClick(R.id.home_carpooling)
    void hcarpooling() {
//        if (isControl == 0) {
//            activity.app.toast("请耐心等待审核");
//            return;
//        }
//        activity.startFragment(BackActivity.class, AdListFragment2.class);//
                if (isControl == 0) {
            activity.app.toast("请耐心等待审核");
            return;
        }
        Intent intent = new Intent(activity, BackActivity.class);
        intent.putExtra("fragmentName", AdFragment2.class.getName());
        Bundle bundle = new Bundle();
        bundle.putInt("ispushad", response.getIspushad());
        intent.putExtra("fragmentBundle", bundle);
        activity.startActivity(intent);

    }



//老版本的
//    @OnClick(R.id.order)
//    void order() {
//        activity.startFragment(BackActivity.class, OrderListFragment.class);//订单
//    }
//
//    @OnClick(R.id.trip)
//    void trip() {
//        activity.startFragment(BackActivity.class, TripListFragment.class);//行程
//    }
//
//    @OnClick(R.id.carpooling)
//    void search() {//搜索
//        if (isControl == 0) {
//            activity.app.toast("请耐心等待审核");
//            return;
//        }
////        Intent iSearch = new Intent();
////        iSearch.putExtra("carid", response.getCid());
////        activity.startFragment(BackActivity.class, SearchFragment.class, iSearch);
//
//        //搜索按钮改为直接跳到  拼车发布列表
//        activity.startFragment(BackActivity.class, AdListFragment2.class);
//    }

    @Override
    protected String getTitle() {
        return "巴士互联-司机";
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBdMap();
        EventBus.getDefault().register(this);
        activity.mContent = this;
        SharedPreferences sp = getActivity().getSharedPreferences("is_enter_introduce", Context.MODE_PRIVATE);
        if (sp.getBoolean("is_enter_introduce", true) != false) {
            Intent intent = new Intent(getActivity(), IntroduceActivity.class);
            startActivity(intent);
        }

//        //弹广告
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.home_fragment_ad, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(view);
//        builder.setTitle("A New Version is Available");
//        builder.create();


        // activity.startFragment(BackActivity.class, IntroduceFragment.class);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        initView();
        ob = ObjectAnimator.ofInt(iv, "round", 0, 270).setDuration(250);
        ob.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iv.invalidate();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        initLocation();
    }

    private void initLocation() {
        final LocationClient mLocationClient = new LocationClient(getActivity());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                switch (location.getLocType()) {
                    case BDLocation.TypeGpsLocation:
                    case BDLocation.TypeNetWorkLocation:
                    case BDLocation.TypeOffLineLocation:
                        tvCity.setText(location.getCity());
                        getWeather(location.getCity());
                        break;
                    default:
                        tvCity.setText("定位失败");
                        break;
                }
                mLocationClient.stop();
            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();
    }

    private void getWeather(String city) {
        HttpUtils http = new HttpUtils();
        //RequestParams params = activity.app.getPostParams();
        RequestParams params=new RequestParams();
        params.addBodyParameter(JsonName.CITY_NAME,city);
        params.addBodyParameter(JsonName.KEY,JsonName.WEATHER_KEY);
        http.send(HttpRequest.HttpMethod.POST, Net.WEATHER, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                JSONObject res = XString.getJSONObject(responseInfo.result);
                if (TextUtils.equals(XString.getStr(res, JsonName.REASON), "successed!")) {

                    JSONObject weather = XString.getJSONObject(
                            XString.getJSONObject(
                                    XString.getJSONObject(
                                            XString.getJSONObject(res, JsonName.RESULT), JsonName.DATA), JsonName.REAL_TIME), JsonName.WEATHER);
                    if(tvWeather!=null&&tvTemperature!=null){
                        tvWeather.setVisibility(View.VISIBLE);
                        tvTemperature.setVisibility(View.VISIBLE);
                        tvWeather.setText(XString.getStr(weather, JsonName.INFO));
                        ivWeather.setImageDrawable(TypeHelp.getIcWeather(XString.getStr(weather, JsonName.TIME), XString.getInt(weather, JsonName.IMG), getActivity()));
                        tvTemperature.setText(String.format("%s°", XString.getStr(weather, JsonName.TEMPERATURE)));
                    }
                } else {
                    tvWeather.setText("获取失败");
                    tvWeather.setVisibility(View.GONE);
                    tvTemperature.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.stop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MyEvent e) {
        if ("update_home".equals(e.getTag())) {
            getData();
        }
    }

    private void getData() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("lon", "");
        params.addQueryStringParameter("lat", "");
        params.addQueryStringParameter("city", "");
        RequestManager.home(params, new HomeRequestCallBack());
    }

    private void postLocation(String city, String lon, String lat) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("lon", lon);
        params.addQueryStringParameter("lat", lat);
        params.addQueryStringParameter("city", city);
        RequestManager.home(params, new HomeRequestCallBack());
    }

    class HomeRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) { // main thread
            response.parse(responseInfo.result);
            if (response.isStatus()) {
                updateUI();
            } else {
                activity.app.toast(response.getMsg());
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            LogUtils.e(s, e);
            activity.app.toast("您的网络不给力");
        }
    }

    private void acceptOrder() {
        if (orderState == 2) {
            orderControl_btn.setChecked(start);
            orderControl_btn.setEnabled(false);

            orderControl_btn.setTextColor(Color.parseColor("#FF2525"));
            orderControl_btn.setText("暂停接单");

            HttpUtils http = new HttpUtils();
            RequestParams params = activity.app.getGetParams();
            params.addQueryStringParameter("dstate", "1");
            http.send(HttpRequest.HttpMethod.GET, Net.ORDER_CONTROL, params, new orderControlRequestCallBack(1));

        }
    }

    private void refuseOrder() {
        if (isControl == 1 && orderState == 1) {
            orderControl_btn.setChecked(pause);

            orderControl_btn.setTextColor(Color.parseColor("#2ED964"));
            orderControl_btn.setText("开始接单");

            HttpUtils http = new HttpUtils();
            RequestParams params = activity.app.getGetParams();
            params.addQueryStringParameter("dstate", "2");

            http.send(HttpRequest.HttpMethod.GET, Net.ORDER_CONTROL, params, new orderControlRequestCallBack(2));

        }
    }

    class orderControlRequestCallBack extends RequestCallBack<String> {

        int dstate;

        public orderControlRequestCallBack(int dstate) {
            this.dstate = dstate;
        }

        @Override
        public void onStart() {
            super.onStart();
            activity.dialog.show();
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            activity.dialog.dismiss();
            LogUtils.d(responseInfo.result);
            orderControl_btn.setEnabled(true);
            if (dstate == 1) {
                try {
                    JSONObject jo1 = new JSONObject(responseInfo.result);
                    boolean status = XString.getBoolean(jo1, "status");
                    if (status) { // 尝试接单，成功
                        orderControl_btn.setChecked(pause);

                        playVoice(0); // 开始接单啦

                        ob.start();
                        orderState = dstate;

                    } else { // 尝试接单，失败
                        orderControl_btn.setChecked(start);
                        JSONObject jo2 = jo1.getJSONObject("data");
                        String msg = XString.getStr(jo2, "msg");
                        activity.app.toast(msg);
                        if ("审核中".equals(msg)) {
                            playVoice(2);
                        } else if ("审核未通过".equals(msg)) {
                            playVoice(3);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (dstate == 2) {
                try {
                    JSONObject jo1 = new JSONObject(responseInfo.result);
                    boolean status = XString.getBoolean(jo1, "status");
                    if (status) {
                        orderControl_btn.setChecked(start);
                        playVoice(1); // 停止接单啦
                        orderState = dstate;
                        ob.reverse();
                    } else {
                        orderControl_btn.setChecked(pause);
                        JSONObject jo2 = jo1.getJSONObject("data");
                        String msg = XString.getStr(jo2, "msg");
                        activity.app.toast(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    orderControl_btn.setEnabled(true);
                }
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            activity.dialog.dismiss();
            orderControl_btn.setEnabled(true);
            activity.app.toast("您的网络不给力");
        }
    }

    private void initBdMap() {
        mlocationClient = new LocationClient(getActivity());
        LocationClientOption option = new LocationClientOption();
        //option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        option.setLocationNotify(true);
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mlocationClient.setLocOption(option);
        mlocationClient.registerLocationListener(myListener);
        mlocationClient.start();
    }

    public void playVoice(int flag) {
        SharedPreferences sp = activity.getSharedPreferences(UserHelper.getInstance().getUid(), Context.MODE_PRIVATE);
        if (!sp.getBoolean("sound", true)) {
            return;
        }
        int id = 0;
        switch (flag) {
            case 0:
                id = R.raw.start;
                break;
            case 1:
                id = R.raw.pause;
                break;
            case 2:
                id = R.raw.shenhe_wait;
                break;
            case 3:
                id = R.raw.shenhe_fail;
                break;
        }

        if (mpStart != null) {
            mpStart.release();
        }
        mpStart = MediaPlayer.create(activity, id);
        mpStart.start();

    }


    private void getImgs() {
        HttpUtils http = new HttpUtils();
        RequestParams params = activity.app.getPostParams();
        http.send(HttpRequest.HttpMethod.POST, Net.ADIMAGESLST, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                imgResponseResult = responseInfo.result;
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    setData();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    private void setData() {
        if (imgResponseResult == null) {
            getImgs();
            return;
        }
        JSONObject result = XString.getJSONObject(imgResponseResult);
        JSONObject data = XString.getJSONObject(result, JsonName.DATA);
        int size = XString.getInt(data, JsonName.TOTAL);
        JSONArray list = XString.getJSONArray(data, JsonName.IMGLST);
       // String[] urls = new String[size];
        List <String> urls=new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
              //  urls[i] = Net.IMGURL + XString.getStr(list.getJSONObject(i), JsonName.URL);
                urls.add( Net.IMGURL + XString.getStr(list.getJSONObject(i), JsonName.URL));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       // bgab.setImageUrls(urls);
       icv.setImageResources(urls, new ImageCycleView.ImageCycleViewListener() {
           @Override
           public void displayImage(String imageURL, ImageView imageView) {
               Picasso.with(activity)
                     .load(imageURL)
                       .placeholder(R.mipmap.banner_default)
                       .error(R.mipmap.banner_default)
                               //  .transform(new CircleTransform())
                       .into(imageView);

           }
       });
        icv.startImageCycle();
    }

    private void updateUI() {
         isorder = response.getIsorder();
      istrip = response.getIstrip();
      iscarpooling = response.getIscarpooling();

        if (isorder+iscarpooling == 0) {
            mOrderRed.setVisibility(View.GONE);
        } else if (isorder+iscarpooling < 100) {
            if(isorder>=iscarpooling){//订单数小于拼车订单则先调到拼车订单页
                isleft=true;
            }else{
                isleft=false;
            }
            mOrderRed.setVisibility(View.VISIBLE);
            mOrderRed.setText((isorder+iscarpooling) + "");
        } else {
            mOrderRed.setVisibility(View.VISIBLE);
            mOrderRed.setText("99+");
        }
        if (istrip == 0) {
            mTripRed.setVisibility(View.GONE);
        } else if (isorder < 100) {
            mTripRed.setVisibility(View.VISIBLE);
            mTripRed.setText(istrip + "");
        } else {
            mTripRed.setVisibility(View.VISIBLE);
            mTripRed.setText("99+");
        }
//        if (iscarpooling == 0) {
//            mCarPoolingRed.setVisibility(View.GONE);
//        } else if (isorder < 100) {
//            mCarPoolingRed.setVisibility(View.VISIBLE);
//            mCarPoolingRed.setText(iscarpooling + "");
//        } else {
//            mCarPoolingRed.setVisibility(View.VISIBLE);
//            mCarPoolingRed.setText("99+");
//        }
        String name = response.getName();
        String company = response.getCompany();
        String dstate = response.getDstate();
        this.isControl = response.getIscontrol();
        LogUtils.d(dstate);
        if ("1".equals(dstate)) { // 接单
            orderState = 1;
            orderControl_btn.setChecked(false);
            iv.setRound(270);
            iv.invalidate();
        } else if ("2".equals(dstate)) { // 停止接单
            orderState = 2;
            orderControl_btn.setChecked(true);
        }
        String headUrl = response.getImg();

        UserHelper.getInstance().setName(name);
        UserHelper.getInstance().setCompany(company);
        UserHelper.getInstance().setHeadUrl(headUrl);
        MyEvent e = new MyEvent(EventName.updateMenuUI);
        e.putExtra("response", response);
        e.setData(response.getResultString());
        EventBus.getDefault().post(e);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            String city;
            double mLatitude, mLongitude;
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                city = location.getCity();
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                city = location.getCity();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                city = location.getCity();
            } else {
                return;
            }
            if (!TextUtils.isEmpty(city)) {
                postLocation(city, String.valueOf(mLongitude), String.valueOf(mLatitude));
            }

            LogUtils.d("city: " + city + "lat:" + mLatitude + ", lon:" + mLongitude);
        }
    }

    private void initView() {
        getImgs();

        orderControl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = orderControl_btn.isChecked();
                if (isChecked) {
                    refuseOrder();
                } else {
                    acceptOrder();
                }
            }
        });
    }


}