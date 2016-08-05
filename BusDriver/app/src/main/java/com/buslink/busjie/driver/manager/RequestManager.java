package com.buslink.busjie.driver.manager;

import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.util.CameraUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.IOException;

public class RequestManager {

    /**
     * 7个字段
     * yzphone, yzmid, uid, did, type, version, mtype
     * @return
     */
    public static RequestParams simplePostParams() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addBodyParameter("uid", UserHelper.getInstance().getUid());
        params.addBodyParameter("did", UserHelper.getInstance().getUid());
        params.addBodyParameter("cid", UserHelper.getInstance().getCid());
        params.addBodyParameter("type", "2");
        params.addBodyParameter("version", MyApplication.getVersionCode());
        params.addBodyParameter("mtype", "1");
        return params;
    }

    public static RequestParams simpleGetParams() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("uid", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("type", "2");
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("mtype", "1");
        return params;
    }

    // 接口文档5 获取验证码短信
    public static void getIdentifyMessage(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("type", "2");
        params.addQueryStringParameter("mid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("time", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.IDENTIFYMESSAGE, params, callBack);
    }

    // 接口文档6.2
    public static void signIn(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("mid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("tid", UserHelper.getInstance().getBdchannelid());
        params.addQueryStringParameter("mtype", "1");
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        String a =MyApplication.getVersionCode();
        LogUtils.d("versioncode:"+a);
        params.addQueryStringParameter("timestamp", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.DRIVERMANUALLOGIN, params, callBack);
        LogUtils.d("bdchannelid: " + UserHelper.getInstance().getBdchannelid());
    }

    // 接口文档2.2
    public static void imageUpload(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("version", MyApplication.getVersionCode());
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.IMAGEUPFILEAND, params, callBack);
    }

    // 接口文档9
    public static void signUp(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("phone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("mid", DeviceInfoManager.getDeviceId());
        params.addBodyParameter("did", UserHelper.getInstance().getUid());
        params.addBodyParameter("cid", UserHelper.getInstance().getCid());
        params.addBodyParameter("bdappid", UserHelper.getInstance().getBdappid());
        params.addBodyParameter("bduserid", UserHelper.getInstance().getBduserid());
        params.addBodyParameter("bdchannelid", UserHelper.getInstance().getBdchannelid());
        params.addBodyParameter("mtype", "1");
        params.addBodyParameter("version", MyApplication.getVersionCode());
        try {
            File file = new File(CameraUtil.PHOTO_DIR, "file");
            file.createNewFile();
            params.addBodyParameter("file", file);
            LogUtils.d("dummy file");
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.SIGNUP, params, callBack);
    }

    // 接口文档39
    public static void home(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("tid", UserHelper.getInstance().getBdchannelid());
        params.addQueryStringParameter("mtype", "1");
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("timestamp", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.HOME, params, callBack);
        LogUtils.d("bdchannelid: " + UserHelper.getInstance().getBdchannelid());
    }

    // 接口文档31
    public static void orderControl(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("timestamp", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.ORDERCONTROL, params, callBack);
    }

    // 订单列表
    public static void orderList(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addBodyParameter("uid", UserHelper.getInstance().getUid());
        params.addBodyParameter("version", MyApplication.getVersionCode());
        params.addBodyParameter("pagesize", "10");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.ORDERLIST, params, callBack);
    }

    // 订单详情
    public static void orderDetail(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("yzmid", DeviceInfoManager.getDeviceId());
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.ORDERDETAIL, params, callBack);
    }

    // 接口文档28 司机报价
    public static void orderPrice(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("time", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.ORDERPRICE, params, callBack);
    }

    // 行程列表
    public static void tripList(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addBodyParameter("uid", UserHelper.getInstance().getUid());
        params.addBodyParameter("version", MyApplication.getVersionCode());
        params.addBodyParameter("pagesize", "5");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.TRIPLIST, params, callBack);
    }

    // 行程详情
    public static void tripDetail(RequestParams params, RequestCallBack<String> callBack) {
        params.addBodyParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addBodyParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addBodyParameter("version", MyApplication.getVersionCode());
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.TRIPDETAIL, params, callBack);
    }
    //行程详情确认支付完成
    public static void confirmCompletion(RequestParams params, RequestCallBack<String> callBack){
        params.addBodyParameter("version", MyApplication.getVersionCode());
        String a=MyApplication.getVersionCode();
        HttpUtils http = new HttpUtils();
        String b=Net.CONFIRM_COMPLETION;
        http.send(HttpRequest.HttpMethod.POST, Net.CONFIRM_COMPLETION, params, callBack);
    }


    // 接口文档4 意见反馈
    public static void feedBack(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("uid", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("type", "2");
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("time", System.currentTimeMillis() + "");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.FEEDBACK, params, callBack);
    }

    // 接口文档11 查询司机注册信息
    public static void getDriverProfile(RequestParams params, RequestCallBack<String> callBack) {
        params.addQueryStringParameter("yzphone", UserHelper.getInstance().getPhone());
        params.addQueryStringParameter("yzmid", DeviceInfoManager.getDeviceId());
        params.addQueryStringParameter("did", UserHelper.getInstance().getUid());
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("time", System.currentTimeMillis()+"");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET, Net.DRIVERPROFILE, params, callBack);
    }

    // 接口文档40 软件升级
    public static void upgrade(RequestCallBack<String> callBack) {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("type", "2");
        params.addQueryStringParameter("version", MyApplication.getVersionCode());
        params.addQueryStringParameter("mtype", "1");

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.UPGRADE, params, callBack);
    }

    // 接口文档58 我的钱包
    public static void myWallet(RequestCallBack<String> callBack) {
        RequestParams params = simplePostParams();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.MY_WALLET, params, callBack);
    }

    // 接口文档60 添加银行卡
    public static void addBank(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.ADDBANK, params, callBack);
    }

    // 接口文档61 银行卡列表
    public static void bankList(RequestCallBack<String> callBack) {
        RequestParams params = simplePostParams();
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.BANKLIST, params, callBack);
    }

    // 接口文档68 申请提现
    public static void applyFor(RequestCallBack<String> callBack) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("uid", UserHelper.getInstance().getUid());
        params.addBodyParameter("type", "2");

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.APPLYFOR, params, callBack);
    }

    // 接口文档70 总收入明细
    public static void incomeList(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.INCOMELIST, params, callBack);
    }

    // 85.
    public static void updateDriver(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.UPDATE_DRIVER, params, callBack);
    }

    //86.
    public static void updateCar(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.UPDATE_CAR, params, callBack);
    }

    //87.
    public static void viewDriver(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.VIEW_DRIVER, params, callBack);
    }

    //88.
    public static void viewCar(RequestParams params, RequestCallBack<String> callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, Net.VIEW_CAR, params, callBack);
    }
}

















