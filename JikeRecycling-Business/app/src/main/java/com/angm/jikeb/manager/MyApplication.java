package com.angm.jikeb.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.angm.jikeb.constant.NetWorkConstant;
import com.angm.jikeb.util.CrashUtils;
import com.angm.jikeb.util.ToastHelper;
import com.yolanda.nohttp.NoHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shd on 16-8-1.
 */
public class MyApplication extends  Application {
    private static MyApplication app;
    private List<Activity> activities;
    private Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化异常处理
        app = this;
        activities = new ArrayList<Activity>();

        CrashUtils.getInstance().init(this, NetWorkConstant.FRONTEND_ERROR);
        //初始化Toast
        ToastHelper.init(this);

        NoHttp.init(this);
    }
    public static Application getApplication() {
        return app;
    }


    public static MyApplication getApp() {
        return app;
    }

    public static Context getContext() {
        return getApp().getApplicationContext();
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

    public void toast(String msg) {

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(app, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public MyApplication addActivity(Activity activity) {
        activities.add(activity);
        return app;
    }


    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    private void killActivity(Activity ac) {
        if (ac != null) {
            ac.finish();
        }
    }
    public void remove(Activity activity) {
        activities.remove(activity);
    }


    public void exitApp() {
        int size = activities.size();
        for (int i = 0; i < size; i++) {
            killActivity(activities.get(i));
        }
    }

}
