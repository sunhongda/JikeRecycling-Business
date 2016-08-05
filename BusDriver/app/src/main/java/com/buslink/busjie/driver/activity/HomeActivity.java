package com.buslink.busjie.driver.activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.DrawerActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.db.User;
import com.buslink.busjie.driver.fragment.DrawerFragment;
import com.buslink.busjie.driver.fragment.HomeFragment;
import com.buslink.busjie.driver.manager.DbManager;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.service.TimeLocateService;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import cz.msebera.android.httpclient.Header;
public class HomeActivity extends DrawerActivity {
    private boolean isWarnedToClose = false;
    boolean checkUpdate = false;
    HttpHandler handler;
    boolean isDeleteDownloadFile = true;
    Long fileTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAlarm();

    }

    private void initView() {
        HomeFragment homeFragment = new HomeFragment();
        DrawerFragment drawerFragment = new DrawerFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, homeFragment);
        ft.replace(R.id.menu_frame, drawerFragment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (isWarnedToClose) {
            finish();
        } else {
            isWarnedToClose = true;
            //app.toast("再按一次退出");
            Snackbar.make(mFrameLayout, "再按一次退出", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWarnedToClose = false;
                }
            }, 2000);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        sendLogin();
        if (!checkUpdate) {
            RequestManager.upgrade(new UpgradeRequestCallBack());
        }
    }

    private void sendLogin() {   //检测是否登录
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = MyHelper.getParams();
        params.add(JsonName.OPERATIONTYPE, "1");//1进入2退出
        //退出登录
        client.post(getApplicationContext(), Net.LONINSTATEHANDLE, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                JSONObject res = XString.getJSONObject(responseString);
                JSONObject data = XString.getJSONObject(res, JsonName.DATA);
                if (XString.getBoolean(res, JsonName.STATUS)) {
                    //aa
                    //如果有其他设备登录则再次登录登录
                    if (XString.getInt(data, JsonName.PAGESTAGE) == 3) {//1.直接进入软件界面 2退出软件  3进入登录界面
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("下线通知")
                                .setMessage("您的账号已在其他设备上登录，请重新登录！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(HomeActivity.this, SignInActivity.class));
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DbUtils db = DbManager.getDb();
                                        try {
                                            db.deleteAll(User.class);
                                            File file = new File(PickPhotoActivity.OUT_FILE_DIR);
                                            if (file.exists()) {
                                                deleteFile(file.getParentFile());
                                            }
                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                        app.exit();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }

//                    app.exit();
//                    activity.startFragment(NoBackActivity.class, LoginFragment.class);
                } else {
                    //  app.toast("mainActivity登录请求失败");
                }

            }
        });

    }

    private void deleteFile(File oldPath) {
        if (oldPath.isDirectory()) {
            File[] files = oldPath.listFiles();
            for (File file: files) {
                deleteFile(file);
            }
            oldPath.delete();
        } else {
            oldPath.delete();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) { // 启动过下载
            handler.cancel();
            if (isDeleteDownloadFile) {
                File downloadFile = new File(PickPhotoActivity.OUT_FILE_DIR + "driver" + fileTime + ".apk");
                if (downloadFile.exists()) {
                    downloadFile.delete();
                }
            }
        }
    }

    private void initAlarm() {
        Intent intent = new Intent(app, TimeLocateService.class);
        intent.setAction("com.buslink.busjie.repeating");
       // (Context context, int requestCode, Intent intent,int flags)
        PendingIntent mAlarmSender = PendingIntent.getService(this, 0, intent, 0);
        long firstTime = SystemClock.elapsedRealtime();

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 60 * 60 * 1000, mAlarmSender);
    }

    class UpgradeRequestCallBack extends RequestCallBack<String> {
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            try {
                JSONObject jo = new JSONObject(responseInfo.result);
                boolean status = XString.getBoolean(jo, "status");
                if (status) {
                    JSONObject data = XString.getJSONObject(jo, "data");
                    String isqzupgrade = XString.getStr(data, "isqzupgrade");
                    String isupgrade = XString.getStr(data, "isupgrade");
                    String url = XString.getStr(data, "url");
                    if ("1".equals(isupgrade)) { // 需要升级
                        upgradeDialog(isqzupgrade, url);
                    }
                } else {

                }
                checkUpdate = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(HttpException e, String s) {
            LogUtils.e(s, e);
        }
    }

    /**
     * 1.返回键：无论是否强制升级，返回键总可以点击
     * 2.空白区域：如果是强制升级，空白区域点击不能消失对话框
     * 3.否定键：如果是强制升级，点击后退出应用
     * 4.确定键：打开下载对话框
     * @param isqzupgrade 1.强制升级 0.非强制
     * @param url         下载地址
     */
    private void upgradeDialog(final String isqzupgrade, final String url) {
        String negativeButtonText = "取消";
        if ("1".equals(isqzupgrade)) {
            negativeButtonText = "暂时退出";
        } else if ("0".equals(isqzupgrade)) {
            negativeButtonText = "下次再说";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("检测到有新版本，是否升级？");
        // 4.确定键
        builder.setPositiveButton("下载升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadDialog(isqzupgrade, url);
//                dialog.dismiss();
            }
        });
        // 3.否定键
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("1".equals(isqzupgrade)) { // 3.如果是强制升级，用户点击“下次再说”时
                    finish(); // Activity销毁
                }
            }
        });
        // 1.返回键
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if ("1".equals(isqzupgrade)) { // 1.如果是强制升级，用户点击返回键时
                    finish(); // Activity销毁
                }
            }
        });
        AlertDialog dialog = builder.create();
        // 2.空白区域
        if ("1".equals(isqzupgrade)) { // 2.如果是强制升级，升级对话框无法通过点击空白处消失
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    /**
     * 1.空白区域 无论是否强制升级，点击空白区域无法消失对话框
     * 2.返回键 如果是强制升级，销毁Activity；如果非强制，关闭对话框，停止下载
     *
     * @param isqzupgrade
     * @param url
     */
    private void downloadDialog(final String isqzupgrade, final String url) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_download, null);
        final TextView tv = (TextView) view.findViewById(R.id.percent_textview);
        final ProgressBar pb = (ProgressBar) view.findViewById(R.id.percent_progressbar);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (handler != null) {
                    handler.cancel();
                }
                if ("1".equals(isqzupgrade)) {
                    finish();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        fileTime = System.currentTimeMillis();
        HttpUtils http = new HttpUtils();
        File file = new File(PickPhotoActivity.OUT_FILE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        handler = http.download(url,
                PickPhotoActivity.OUT_FILE_DIR + "driver" + fileTime + ".apk",
                true, true, new RequestCallBack<File>() {
                    @Override
                    public void onStart() {
                        super.onStart();

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int progress = (int) (current * 100 / total);
                        tv.setText(progress + "%");
                        pb.setProgress(progress);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        File file = new File(PickPhotoActivity.OUT_FILE_DIR + "driver" + fileTime + ".apk");
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(intent);
                        isDeleteDownloadFile = false;
                        finish();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        app.toast("下载失败，请重新下载");
                        dialog.dismiss();
                        finish(); // 下载失败，退出应用
                    }
                });
    }
}















