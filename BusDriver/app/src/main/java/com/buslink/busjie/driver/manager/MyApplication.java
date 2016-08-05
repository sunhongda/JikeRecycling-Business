package com.buslink.busjie.driver.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.RequestName;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.util.CrashUtils;
import com.buslink.busjie.driver.util.ToastHelper;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application{

    private static MyApplication app;
    private List<Activity> activities;
    private Toast toast;
    public HttpHandler handler;
    private UserHelper mUserHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        activities = new ArrayList<Activity>();
        SDKInitializer.initialize(this);
        mUserHelper=UserHelper.getInstance();
        ToastHelper.init(this);

        CrashUtils.getInstance().init(this, Net.FRONTEND_ERROR);
    }

    public static Application getApplication() {
        return app;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    public static String getVersionCode() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return String.valueOf(info.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getVersionName() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static MyApplication getApp() {
        return app;
    }

    public void toast(String msg){
        if(toast!=null){
            toast.cancel();
        }
        toast= Toast.makeText(app, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public RequestParams getPostParams(){
        RequestParams params = new RequestParams();
        params.addBodyParameter(RequestName.UID, UserHelper.getInstance().getUid());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addBodyParameter(RequestName.VERSION, getVersionCode() + "");
        params.addBodyParameter(RequestName.YZ_MID, DeviceInfoManager.getDeviceId());
        params.addBodyParameter(RequestName.MID, DeviceInfoManager.getDeviceId());
        params.addBodyParameter(RequestName.YZ_PHONE, UserHelper.getInstance().getPhone());
        params.addBodyParameter(RequestName.M_TYPE, "1");
        params.addBodyParameter(RequestName.TYPE, "2");
        return params;
    }



    public RequestParams getGetParams(){
        RequestParams params = new RequestParams();
        params.addQueryStringParameter(RequestName.UID, UserHelper.getInstance().getUid());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addQueryStringParameter(RequestName.VERSION, getVersionCode() + "");
        params.addQueryStringParameter(RequestName.YZ_MID, DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter(RequestName.MID, DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter(RequestName.YZ_PHONE, UserHelper.getInstance().getPhone());
        params.addQueryStringParameter(RequestName.M_TYPE, "1");
        params.addQueryStringParameter(RequestName.TYPE, "2");
        return params;
    }
    public MyApplication addActivity(Activity activity) {
        activities.add(activity);
        return app;
    }

    public void remove(Activity activity) {
        activities.remove(activity);
    }

    public void finishAllWithoutRoot() {
        int size = activities.size();
        for (int i = 1; i < size; i++) {
            killActivity(activities.get(i));
        }
    }

    private void killActivity(Activity ac){
        if(ac!=null){
            ac.finish();
        }
    }

    public void exit() {
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            killActivity(activities.get(i));
        }
    }


}
