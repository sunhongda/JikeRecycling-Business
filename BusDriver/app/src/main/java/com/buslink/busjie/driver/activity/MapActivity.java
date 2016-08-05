package com.buslink.busjie.driver.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BaseActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.entity.MapSelectAdressInfo;
import com.buslink.busjie.driver.util.ResultType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2015/8/25.
 */
public class MapActivity extends BaseActivity implements OnGetSuggestionResultListener, BDLocationListener {

    @Bind(R.id.map_location_progressbar)
    ProgressBar mProgressBar;
    @Bind(R.id.et_map_address)
    EditText etMapAddress;
    @Bind(R.id.map_lacation_my_location)
    ImageView mLocationReset;
    @Bind(R.id.bmapView)
    MapView mBaiduMap;
    @Bind(R.id.map_lacation_zoom_out)
    TextView mZoomOut;// 缩小按钮
    @Bind(R.id.map_lacation_zoom_in)
    TextView mZoomIn;// 放大按钮
    @Bind(R.id.map_lacation_listview_search)
    ListView mMapLcationListview;
    @Bind(R.id.button_map)
    Button mButtonCommit;
    @Bind(R.id.map_location_title_layout)
    Toolbar mTitleLayout;
    @Bind(R.id.map_lacation_imageview_diwei)
    ImageView mImageView;

    private LatLng ll;
    private BaiduMap mMap;
    private MapStatus mapStatus;
    private GeoCoder mGeoCoder;
    private float MaxLevel = 17;
    private float MinLevel = 5;
    private LocationClient mlocationClient;
    private SuggestionSearch mSuggestionSearch;
    private MapAdapter myAdapter;
    private int select = 0;
    private ArrayList<MapSelectAdressInfo> mMapListInfo = new ArrayList<MapSelectAdressInfo>();
    private ReverseGeoCodeResult.AddressComponent addressComponent;
    private Projection mProjection;
    boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.a_map);
        ButterKnife.bind(this);
        mGeoCoder = GeoCoder.newInstance();
        mBaiduMap.showScaleControl(false);// 不显示比例尺
        mBaiduMap.showZoomControls(false);// 不显示缩放控件

        mMap = mBaiduMap.getMap();
        mMap.setMyLocationEnabled(true);// 设置开启定位图层
        mlocationClient = new LocationClient(this);
        mlocationClient.registerLocationListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mMapLcationListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        myAdapter = new MapAdapter(this, mMapListInfo);
        mMapLcationListview.setAdapter(myAdapter);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(0);
        mlocationClient.setLocOption(option);
        mlocationClient.start();
        initMGeoCoder();
        initMap();
    }

    @OnClick(R.id.btn_search)//搜索的结果
    void search() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMapAddress.getWindowToken(), 0);
        String textAddress = etMapAddress.getText().toString();
        if (TextUtils.isEmpty(textAddress)) {
            mLocationReset.performClick();
        } else {
            mGeoCoder.geocode(new GeoCodeOption().city(textAddress).address(textAddress));
        }
    }

    @OnClick(R.id.map_lacation_my_location)
    void locationReaset() {
        if (ll != null) {
            mMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll,14));
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
        }

    }

    @OnClick(R.id.map_back_btn)
    void back() {
        finish();
    }

    @OnClick(R.id.button_map)
    void commit() {
        Intent intent = getIntent();
        MapSelectAdressInfo info = mMapListInfo.get(select);
        intent.putExtra("city", info.mCity);
        intent.putExtra("adress", info.mAdress);
        intent.putExtra("province", info.mProvince);
        intent.putExtra("result", info);
        intent.putExtra(JsonName.LON, info.lon);
        intent.putExtra(JsonName.LAT, info.lat);
        setResult(ResultType.OK.ordinal(), intent);
        finish();
    }

    @OnClick({R.id.map_lacation_zoom_in, R.id.map_lacation_zoom_out})
    void zoom(View v) {
        switch (v.getId()) {
            case R.id.map_lacation_zoom_in:
                mMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                mapStatus = mMap.getMapStatus();
                break;
            case R.id.map_lacation_zoom_out:
                mMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                mapStatus = mMap.getMapStatus();
                break;
        }
        refreshZoomControlView();
    }

    @OnItemClick(R.id.map_lacation_listview_search)
    void choice(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        myAdapter.setSelectedPosition(arg2);
        select = arg2;
        myAdapter.notifyDataSetInvalidated();
        mButtonCommit.performClick();
    }

    private void refreshZoomControlView() {
        float zoom = mapStatus.zoom;

        if (zoom > MinLevel && zoom < MaxLevel) {

            if (!mZoomIn.isEnabled()) {
                mZoomIn.setEnabled(true); // 设置为可点击
            }

            if (!mZoomOut.isEnabled()) {
                mZoomOut.setEnabled(true);
            }

        } else if (zoom == MinLevel) {
            mZoomIn.setEnabled(false);
            mZoomOut.setEnabled(true);

        } else {

            mZoomOut.setEnabled(false);
            mZoomIn.setEnabled(true);
        }
    }

    private void initMGeoCoder() {
        mGeoCoder
                .setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

                    @Override
                    public void onGetReverseGeoCodeResult(
                            ReverseGeoCodeResult result) {
                        if (result == null
                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            app.toast("没有找到此位置信息");
                            return;
                        }
                        addressComponent = result.getAddressDetail();
                        List<PoiInfo> infos = result.getPoiList();
                        mMapListInfo.clear();
                        if (infos != null && !infos.isEmpty()) {
                            //默认列表的值
                            for (PoiInfo poiInfo : infos) {

                                MapSelectAdressInfo mapinfo = new MapSelectAdressInfo();
                                mapinfo.mName = addressComponent.city
                                        + addressComponent.district;
                                mapinfo.mAdress = addressComponent.district + addressComponent.street + poiInfo.name;
                                mapinfo.mCity = addressComponent.city;
                                mapinfo.mPostCode = poiInfo.postCode;
                                mapinfo.mProvince = addressComponent.province;
                                mapinfo.lon=poiInfo.location.longitude+"";
                                mapinfo.lat=poiInfo.location.latitude+"";
                                mMapListInfo.add(mapinfo);
                                myAdapter.notifyDataSetChanged();
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //myAdapter.setSelectedPosition(0);
                                mButtonCommit.setEnabled(true);
                            }
                        } else {
                            // hashMap = new HashMap<String, String>();
                            // hashMap.put("textname", "抱歉！未找到结果");
                            // strings.add(hashMap);
                            myAdapter.notifyDataSetChanged();
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mButtonCommit.setEnabled(false);
                            // hashMap = null;
                        }
                    }

                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult result) {
                        if (result == null
                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
                            app.toast("没有找到此位置信息");
                            return;
                        }
                        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(result.getLocation(),14));
                        //mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(result.getLocation()));
                    }
                });
    }

    private void initMap() {
        mMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                mProjection = mMap.getProjection();
            }
        });

        mMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                app.toast("开始定位了");
                DisplayMetrics metric = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metric);
                // int width = metric.widthPixels; // 屏幕宽度（像素）
                // int height = metric.heightPixels; // 屏幕高度（像素）
                int frameLayoutHeight = mTitleLayout.getHeight();
                int mMapViewHeight = mImageView.getHeight();
                int mImageViewHeight = mImageView.getHeight();
                Point point = new Point(metric.widthPixels / 2,
                        frameLayoutHeight + mMapViewHeight / 2
                                + mImageViewHeight / 2);
                LatLng latlng2 = mProjection.fromScreenLocation(point);
                mProgressBar.setVisibility(View.VISIBLE);
                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(latlng2));
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null || mMap == null || mGeoCoder == null)
            return;
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mMap.setMyLocationData(locData);

        if (isFirstLoc) {
            isFirstLoc = false;

            ll = new LatLng(location.getLatitude(), location.getLongitude());

            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ll));
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,14);
            mMap.animateMapStatus(u);
        }
    }

    @Override
    protected void onDestroy() {
        mBaiduMap.onDestroy();
        super.onDestroy();
    }

    public class MapAdapter extends BaseAdapter {

        private LayoutInflater inflater = null;
        private ArrayList<MapSelectAdressInfo> items = null;
        private int selectedPosition = -1;
        private Context context;

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public MapAdapter(Context context, ArrayList<MapSelectAdressInfo> list) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.items = list;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MapSelectAdressInfo item = items.get(position);
            if (item == null) {
                return null;
            }
            convertView = inflater.inflate(R.layout.i_map_list, null);

            TextView textView1 = (TextView) convertView
                    .findViewById(R.id.map_location_textview1);
            TextView textView2 = (TextView) convertView
                    .findViewById(R.id.map_location_textview2);
            LinearLayout linearLayout = (LinearLayout) convertView
                    .findViewById(R.id.map_location_relativelayout);

            if (!TextUtils.isEmpty(item.mName)) {
                textView1.setText(item.mName);
            } else {
                textView1.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(item.mAdress)) {
                textView2.setText(item.mAdress);
            } else {
                textView2.setVisibility(View.GONE);
            }

            // 设置选中效果
            if (selectedPosition == position) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.mipmap.ic_select);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                linearLayout.addView(imageView, layoutParams);
            }
            return convertView;
        }
    }

//    @Override
//    public Toolbar getToobar() {
//        return mTitleLayout;
//    }
//
//    @Override
//    public void setTitle(String title) {
//
//    }
}
//package com.buslink.busjie.driver.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.baidu.location.BDLocation;
//import com.baidu.location.BDLocationListener;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapStatusUpdate;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.map.Projection;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.PoiInfo;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeOption;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
//import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
//import com.baidu.mapapi.search.sug.SuggestionResult;
//import com.baidu.mapapi.search.sug.SuggestionSearch;
//import com.buslink.busjie.driver.R;
//import com.buslink.busjie.driver.base.BaseActivity;
//import com.buslink.busjie.driver.constant.JsonName;
//import com.buslink.busjie.driver.entity.MapSelectAdressInfo;
//import com.buslink.busjie.driver.util.ResultType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.OnItemClick;
//
///**
// * Created by Administrator on 2015/8/25.
// */
//public class MapActivity extends BaseActivity implements OnGetSuggestionResultListener, BDLocationListener {
//
//    @Bind(R.id.map_location_progressbar)
//    ProgressBar mProgressBar;
//    @Bind(R.id.et_map_address)
//    EditText etMapAddress;
//    @Bind(R.id.map_lacation_my_location)
//    ImageView mLocationReset;
//    @Bind(R.id.bmapView)
//    MapView mBaiduMap;
//    @Bind(R.id.map_lacation_zoom_out)
//    TextView mZoomOut;// 缩小按钮
//    @Bind(R.id.map_lacation_zoom_in)
//    TextView mZoomIn;// 放大按钮
//    @Bind(R.id.map_lacation_listview_search)
//    ListView mMapLcationListview;
//    @Bind(R.id.button_map)
//    Button mButtonCommit;
//    @Bind(R.id.map_location_title_layout)
//    Toolbar mTitleLayout;
//    @Bind(R.id.map_lacation_imageview_diwei)
//    ImageView mImageView;
//
//    private LatLng ll;
//    private BaiduMap mMap;
//    private MapStatus mapStatus;
//    private GeoCoder mGeoCoder;
//    private float MaxLevel = 17;
//    private float MinLevel = 5;
//    private LocationClient mlocationClient;
//    private SuggestionSearch mSuggestionSearch;
//    private MapAdapter myAdapter;
//    private int select = 0;
//    private ArrayList<MapSelectAdressInfo> mMapListInfo = new ArrayList<MapSelectAdressInfo>();
//    private ReverseGeoCodeResult.AddressComponent addressComponent;
//    private Projection mProjection;
//    boolean isFirstLoc = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.a_map);
//        ButterKnife.bind(this);
//        mGeoCoder = GeoCoder.newInstance();
//        mBaiduMap.showScaleControl(false);// 不显示比例尺
//        mBaiduMap.showZoomControls(false);// 不显示缩放控件
//
//        mMap = mBaiduMap.getMap();
//        mMap.setMyLocationEnabled(true);// 设置开启定位图层
//        mlocationClient = new LocationClient(this);
//        mlocationClient.registerLocationListener(this);
//        mSuggestionSearch = SuggestionSearch.newInstance();
//        mSuggestionSearch.setOnGetSuggestionResultListener(this);
//        mMapLcationListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        myAdapter = new MapAdapter(this, mMapListInfo);
//        mMapLcationListview.setAdapter(myAdapter);
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);// 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(0);
//        mlocationClient.setLocOption(option);
//        mlocationClient.start();
//        initMGeoCoder();
//        initMap();
//    }
//
//    @OnClick(R.id.btn_search)
//    void search() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(etMapAddress.getWindowToken(), 0);
//        String textAddress = etMapAddress.getText().toString();
//        if (TextUtils.isEmpty(textAddress)) {
//            mLocationReset.performClick();
//        } else {
//            mGeoCoder.geocode(new GeoCodeOption().city(textAddress).address(textAddress));
//        }
//    }
//
//    @OnClick(R.id.map_lacation_my_location)
//    void locationReaset() {
//        if (ll != null) {
//            mMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(ll, 14));
//            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
//        }
//
//    }
//
//    @OnClick(R.id.map_back_btn)
//    void back() {
//        finish();
//    }
//
//    @OnClick(R.id.button_map)
//    void commit() {
//        Intent intent = getIntent();
//        MapSelectAdressInfo info = mMapListInfo.get(select);
//        intent.putExtra("city", info.mCity);
//        intent.putExtra("adress", info.mAdress);
//        intent.putExtra("province", info.mProvince);
//        intent.putExtra("result", info);
//        intent.putExtra(JsonName.LON, info.lon);
//        intent.putExtra(JsonName.LAT, info.lat);
//        setResult(ResultType.OK.ordinal(), intent);
//        finish();
//    }
//
//    @OnClick({R.id.map_lacation_zoom_in, R.id.map_lacation_zoom_out})
//     void zoom(View v) {
//        switch (v.getId()) {
//            case R.id.map_lacation_zoom_in:
//                mMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
//                mapStatus = mMap.getMapStatus();
//                break;
//            case R.id.map_lacation_zoom_out:
//                mMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
//                mapStatus = mMap.getMapStatus();
//                break;
//        }
//        refreshZoomControlView();
//    }
//
//    @OnItemClick(R.id.map_lacation_listview_search)
//    void choice(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//        myAdapter.setSelectedPosition(arg2);
//        select = arg2;
//        myAdapter.notifyDataSetInvalidated();
//        mButtonCommit.performClick();
//    }
//
//    private void refreshZoomControlView() {
//        float zoom = mapStatus.zoom;
//
//        if (zoom > MinLevel && zoom < MaxLevel) {
//
//            if (!mZoomIn.isEnabled()) {
//                mZoomIn.setEnabled(true); // 设置为可点击
//            }
//
//            if (!mZoomOut.isEnabled()) {
//                mZoomOut.setEnabled(true);
//            }
//
//        } else if (zoom == MinLevel) {
//            mZoomIn.setEnabled(false);
//            mZoomOut.setEnabled(true);
//
//        } else {
//
//            mZoomOut.setEnabled(false);
//            mZoomIn.setEnabled(true);
//        }
//    }
//
//    private void initMGeoCoder() {
//        mGeoCoder
//                .setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//
//                    @Override
//                    public void onGetReverseGeoCodeResult(
//                            ReverseGeoCodeResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            app.toast("没有找到此位置信息");
//                            return;
//                        }
//                        addressComponent = result.getAddressDetail();
//                        List<PoiInfo> infos = result.getPoiList();
//                        mMapListInfo.clear();
//                        if (infos != null && !infos.isEmpty()) {
//                            for (PoiInfo poiInfo : infos) {
//
//                                MapSelectAdressInfo mapinfo = new MapSelectAdressInfo();
//                                mapinfo.mName = addressComponent.city
//                                        + addressComponent.district;
//                                mapinfo.mAdress = addressComponent.district + addressComponent.street + poiInfo.name;
//                                mapinfo.mCity = addressComponent.city;
//                                mapinfo.mPostCode = poiInfo.postCode;
//                                mapinfo.mProvince = addressComponent.province;
//                                mapinfo.lon=poiInfo.location.longitude+"";
//                                mapinfo.lat=poiInfo.location.latitude+"";
//                                mMapListInfo.add(mapinfo);
//                                myAdapter.notifyDataSetChanged();
//                                mProgressBar.setVisibility(View.INVISIBLE);
//                              // myAdapter.setSelectedPosition(0);
//                                mButtonCommit.setEnabled(true);
//                            }
//                        } else {
//                            // hashMap = new HashMap<String, String>();
//                            // hashMap.put("textname", "抱歉！未找到结果");
//                            // strings.add(hashMap);
//                            myAdapter.notifyDataSetChanged();
//                            mProgressBar.setVisibility(View.INVISIBLE);
//                            mButtonCommit.setEnabled(false);
//                            // hashMap = null;
//                        }
//                    }
//
//                    @Override
//                    public void onGetGeoCodeResult(GeoCodeResult result) {
//                        if (result == null
//                                || result.error != SearchResult.ERRORNO.NO_ERROR) {
//                            app.toast("没有找到此位置信息");
//                            return;
//                        }
//                        mMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(result.getLocation(), 14));
//                        //mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(result.getLocation()));
//                    }
//                });
//    }
//
//    private void initMap() {
//        mMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
//
//            @Override
//            public void onMapLoaded() {
//                mProjection = mMap.getProjection();
//            }
//        });
//
//        mMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
//
//            @Override
//            public void onMapStatusChangeStart(MapStatus arg0) {
//            }
//
//            @Override
//            public void onMapStatusChangeFinish(MapStatus arg0) {
//                DisplayMetrics metric = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(metric);
//                // int width = metric.widthPixels; // 屏幕宽度（像素）
//                // int height = metric.heightPixels; // 屏幕高度（像素）
//                int frameLayoutHeight = mTitleLayout.getHeight();
//                int mMapViewHeight = mImageView.getHeight();
//                int mImageViewHeight = mImageView.getHeight();
//                Point point = new Point(metric.widthPixels / 2,
//                        frameLayoutHeight + mMapViewHeight / 2
//                                + mImageViewHeight / 2);
//                LatLng latlng2 = mProjection.fromScreenLocation(point);
//                mProgressBar.setVisibility(View.VISIBLE);
//                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
//                        .location(latlng2));
//            }
//
//            @Override
//            public void onMapStatusChange(MapStatus arg0) {
//            }
//        });
//    }
//
//    @Override
//    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
//
//    }
//
//    @Override
//    public void onReceiveLocation(BDLocation location) {
//        // map view 销毁后不在处理新接收的位置
//        if (location == null || mMap == null || mGeoCoder == null)
//            return;
//        MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(location.getRadius())
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(100).latitude(location.getLatitude())
//                .longitude(location.getLongitude()).build();
//        mMap.setMyLocationData(locData);
//
//        if (isFirstLoc) {
//            isFirstLoc = false;
//
//            ll = new LatLng(location.getLatitude(), location.getLongitude());
//
//            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
//                    .location(ll));
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14);
//            mMap.animateMapStatus(u);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        mBaiduMap.onDestroy();
//        super.onDestroy();
//    }
//
//    public class MapAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater = null;
//        private ArrayList<MapSelectAdressInfo> items = null;
//        private int selectedPosition = -1;
//        private Context context;
//
//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//        }
//
//        public MapAdapter(Context context, ArrayList<MapSelectAdressInfo> list) {
//            inflater = LayoutInflater.from(context);
//            this.context = context;
//            this.items = list;
//        }
//
//        @Override
//        public int getCount() {
//            return items.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return items.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            MapSelectAdressInfo item = items.get(position);
//            if (item == null) {
//                return null;
//            }
//            convertView = inflater.inflate(R.layout.i_map_list, null);
//
//            TextView textView1 = (TextView) convertView
//                    .findViewById(R.id.map_location_textview1);
//            TextView textView2 = (TextView) convertView
//                    .findViewById(R.id.map_location_textview2);
//            LinearLayout linearLayout = (LinearLayout) convertView
//                    .findViewById(R.id.map_location_relativelayout);
//
//            if (!TextUtils.isEmpty(item.mName)) {
//                textView1.setText(item.mName);
//            } else {
//                textView1.setVisibility(View.GONE);
//            }
//            if (!TextUtils.isEmpty(item.mAdress)) {
//                textView2.setText(item.mAdress);
//            } else {
//                textView2.setVisibility(View.GONE);
//            }
//
//            // 设置选中效果
//            if (selectedPosition == position) {
//                ImageView imageView = new ImageView(context);
//                imageView.setImageResource(R.mipmap.ic_select);
//                imageView.setScaleType(ImageView.ScaleType.CENTER);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
//                linearLayout.addView(imageView, layoutParams);
//            }
//            return convertView;
//        }
//    }
//
////    @Override
////    public Toolbar getToobar() {
////        return mTitleLayout;
////    }
////
////    @Override
////    public void setTitle(String title) {
////
////    }
//}