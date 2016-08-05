package com.buslink.busjie.driver.fragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.viewholder.SimpleHolder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class BusFragment extends LevelTwoFragment {
    @ViewInject(R.id.rv)
    private RecyclerView rv;
    @ViewInject(R.id.tv_version)
    private TextView version;

    @Override
    public String getTitle() {
        return "巴士互联";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_bus;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    protected void initView() {
        version.setText("巴士互联 V" + MyApplication.getVersionName());
        final String[] array = new String[]{"功能介绍", "意见反馈", "版本更新", "司机协议", "联系我们"};
        rv.setLayoutManager(new LinearLayoutManager(mActivity));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(new RecyclerView.Adapter<SimpleHolder>() {

            @Override
            public SimpleHolder onCreateViewHolder(ViewGroup parent, int i) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, null);
                v.setTag(R.id.tv, v.findViewById(R.id.tv));
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch ((int) v.getTag()) {
                            case 0:
                                mActivity.startFragment(BackActivity.class, FunctionFragment.class);
                                break;
                            case 1:
                                mActivity.startFragment(BackActivity.class, FeedBackFragment.class);
                                break;
                            case 2:
                                checkUpdate();
                                break;
                            case 3:
                                Intent intent = new Intent();
                                intent.putExtra("url", Net.SERVICE_TERMS);
                                mActivity.startFragment(BackActivity.class, WebFragment.class, intent);
                                break;
                            case 4:
                                mActivity.startFragment(BackActivity.class, AboutFragment.class);
                                break;
                        }
                    }
                });
                return new SimpleHolder(v);
            }

            @Override
            public void onBindViewHolder(SimpleHolder viewHolder, int i) {
                ((TextView) viewHolder.itemView.getTag(R.id.tv)).setText(array[i]);
                viewHolder.itemView.setTag(i);
            }

            @Override
            public int getItemCount() {
                return array.length;
            }
        });
    }

    private void checkUpdate() {
        if(mActivity.app.handler!=null){
            mActivity.app.toast("正在下载");
            return;
        }
        HttpUtils http = new HttpUtils();
        RequestParams params = RequestManager.simpleGetParams();
        http.send(HttpRequest.HttpMethod.GET, Net.UPGRADE, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                mActivity.dialog.show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                mActivity.dialog.dismiss();
                try {
                    JSONObject result = new JSONObject(responseInfo.result);
                    JSONObject data = XString.getJSONObject(result, JsonName.DATA);
                    if (XString.getBoolean(result, JsonName.STATUS)) {
                        if (XString.getInt(data, JsonName.IS_UP_GRADE) == 1) {
                            upGrade(XString.getInt(data, JsonName.IS_QZ_UP_GRADE), XString.getStr(data, JsonName.URL));
                        } else {
                            mActivity.app.toast(TextUtils.isEmpty(XString.getStr(data, JsonName.MSG)) ? "当前是最新版本" : XString.getStr(data, JsonName.MSG));
                        }
                    } else {
                        mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.dialog.dismiss();
                mActivity.app.toast("网络链接失败，请稍后再试！");
            }
        });
    }

    private void upGrade(final int type, final String url) {
        String msg = "稍后再说";
        Boolean canceable = true;
        if (type == 1) {
            msg = "暂时退出";
            canceable = false;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                .setMessage("检测到新版本，是否升级？")
                .setTitle("提示")
                .setPositiveButton(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1) {
                        }
                    }
                })
                .setNegativeButton("立即升级", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        down(url);
                    }
                })
                .setCancelable(canceable)
                .create();
        alertDialog.show();
    }

    private void down(String url) {
        final NotificationManager notificationManager = (NotificationManager) mActivity.getSystemService(mActivity.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.mipmap.logo, "下载", System
                .currentTimeMillis());

        RemoteViews view = new RemoteViews(mActivity.getPackageName(), R.layout.notification_undata);
        notification.contentView = view;

        PendingIntent contentIntent = PendingIntent.getActivity(mActivity,
                R.string.app_name, new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        notification.contentIntent = contentIntent;
        final String sdPath = Environment.getExternalStorageDirectory() + "/passenger.apk";
        File f = new File(sdPath);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpUtils http = new HttpUtils();
        url = Net.HOST + "/buslk/apk/amap_branch_reconstruct_driver.apk";
        mActivity.app.handler = http.download(url, sdPath, true, false,
                new RequestCallBack<File>() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onStart() {
                        mActivity.app.toast("开始下载");
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int progress = (int) ((double) current / (double) total * 100);
                        // 更改文字
                        notification.contentView.setTextViewText(R.id.noti_tv, progress
                                + "%");
                        // 更改进度条
                        notification.contentView.setProgressBar(R.id.noti_pd, 100,
                                progress, false);
                        // 发送消息
                        notificationManager.notify(0, notification);

                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(sdPath)), "application/vnd.android.package-archive");
                        startActivity(intent);
                        mActivity.finish();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        mActivity.app.toast("网络链接失败，请稍后再试！");
                    }
                });
    }
}
