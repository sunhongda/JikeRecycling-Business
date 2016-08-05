package com.buslink.busjie.driver.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.HomeActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.fragment.OrderListDetailFragment;
import com.buslink.busjie.driver.fragment.TripDetailFragment2;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.event.EventBus;

public class DriverPushMessageReceiver extends PushMessageReceiver {

    private MediaPlayer mpStart;

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        LogUtils.d(responseString);
        if (errorCode == 0) {
            UserHelper.getInstance().setBdappid(appid);
            UserHelper.getInstance().setBduserid(userId);
            UserHelper.getInstance().setBdchannelid(channelId);
            LogUtils.d("百度云推送bind");
        }
    }

    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {

    }

    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {

    }

    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> sucessTags, List<String> failTags, String requestId) {

    }

    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {

    }

    @Override
    public void onMessage(Context context, String message,
                          String customContentString) {

    }

    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        // {"pushid":"DADB68505B5811E5B30798A0984FCE91","ptype":"1","orid":"DADAF3205B5811E5B307F633AA9B8DD0"}
        LogUtils.d(notifyString);
        try {
            JSONObject jo = new JSONObject(customContentString);
            String pushid = XString.getStr(jo, "pushid");
            String orid = XString.getStr(jo, "orid");
            int ptype = XString.getInt(jo, "ptype");
            Bundle bundle = new Bundle();
            bundle.putString("pushid", pushid);
            bundle.putString("orid", orid);
            Intent intent = new Intent(context, BackActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("bundle", bundle);
            intent.putExtra("orid", orid);
            intent.putExtra("pushid", pushid);

//            推送类型   1推订单    2发公告  3支付  4司机报价5确认接单（作废）
//            6线下支付（作废）7支付尾款  8提示收尾款9取消订单
//            10 订单退款  11拼车支付 12 拼车退款
            switch (ptype) {
                case 1:
                case 4:
//                    intent.putExtra("fragmentName", OrderListDetailFragment.class.getName());
//                    break;
                case 3:
                case 5:
                case 6:
//                    intent.putExtra("fragmentName", TripDetailFragment2.class.getName());
//                    break;
                case 7:
                case 8:
                case 9:
                case 10:

                case 11:
                case 12:



                default:
                    intent.setClass(context, HomeActivity.class);
                    context.startActivity(intent);
            }
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        LogUtils.d(notifyString);
        MyEvent e = new MyEvent("update_home");
        EventBus.getDefault().post(e);

        if (mpStart != null) {
            mpStart.release();
        }
        JSONObject jobj;
        jobj = XString.getJSONObject(customContentString);
        int id = XString.getInt(jobj, "ptype");
        PushManager.bind(context, id);
        MyApplication app = MyApplication.getApp();
        SharedPreferences s = app.getSharedPreferences(TextUtils.isEmpty(UserHelper.getInstance().getUid()) ? "default" : UserHelper.getInstance().getUid(), Context.MODE_PRIVATE);
        switch (id) {
            case 1: //单个订单的推送，跳转订单详情页
                if (s.getBoolean("sound", true)) {
                    mpStart = MediaPlayer.create(MyApplication.getContext(), R.raw.neworder);//您有一个新的订单
                    mpStart.start();
                }
                break;
            case 2:
                break;
            case 3:
                if (s.getBoolean("sound", true)) {
                    mpStart = MediaPlayer.create(MyApplication.getContext(), R.raw.newtype);//您的车辆已经预订成功
                    mpStart.start();
                }
                break;
            case 4:
                break;
            case 5:// 已支付
                break;
            default:
                break;
        }
    }
}
