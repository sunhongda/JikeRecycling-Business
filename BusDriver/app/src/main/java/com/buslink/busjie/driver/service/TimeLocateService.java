package com.buslink.busjie.driver.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.manager.DeviceInfoManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

/**
 * Created by Administrator on 2015/8/22.
 */
public class TimeLocateService extends Service {

    private final String TAG = getClass().getSimpleName();

    public LocationClient mLocationClient = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null){
            return super.onStartCommand(intent, flags, startId);
        }
        switch (intent.getAction()) {
            case "com.buslink.busjie.repeating":
                locationPlay(this.getApplication());
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void locationPlay(Context context) {
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new MyLocationListener());
        initLocation();
        mLocationClient.start();
    }

    public class MyLocationListener implements BDLocationListener {
        public String lon, lat, city;

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtils.d("位置上报");
            //Receive Location
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                city = location.getCity();
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                city = location.getCity();
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                city = location.getCity();
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
            } else {
                return;
            }
            LogUtils.d("lon:" + lon + ", lat:" + lat + ", city:" + city);
            if (!TextUtils.isEmpty(city)) {
                upLoadLocation(lon, lat, city);
            }
        }
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setScanSpan(0);
        mLocationClient.setLocOption(option);
    }

    private void upLoadLocation(String lon,String lat,String city){
        HttpUtils http=new HttpUtils();
       // Net.USER_GPS
        String url= Net.POSITIONREPORTING+"?type="+"2"+"&uid="+ UserHelper.getInstance().getUid();
        url=url+"&mid="+DeviceInfoManager.getDeviceId()+"&lon="+lon+"&lat="+lat
                +"&city="+city;


        http.send(HttpRequest.HttpMethod.POST, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                mLocationClient.stop();
                stopSelf();
            }

            @Override
            public void onFailure(HttpException e, String s) {
                
            }
        });
    }

}
