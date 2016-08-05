package com.buslink.busjie.driver.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.buslink.busjie.driver.db.UserHelper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2015/8/22.
 */
public class CrashUtils implements Thread.UncaughtExceptionHandler {
    private final static String TAG = "CatshUtils";
    private final static boolean DEBUG = false;
    private static volatile boolean recordCrash = false;

    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashUtils instance;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    private String url;
    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".cr";

    private CrashUtils() {
    }

    public static CrashUtils getInstance() {
        if (instance == null) {
            instance = new CrashUtils();
        }
        return instance;
    }

    /**
     * 初始化,注册Context对象,
     * 获取系统默认的UncaughtException处理器,
     * 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx, String url) {
        mContext = ctx;
        this.url = url;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(ex != null && ex.getClass().equals(TimeoutException.class)) {
            Log.e("uncaught_ex", ex.getMessage(), ex);
            if(thread == Looper.getMainLooper().getThread()) {
                this.loop();
            }

        } else {
            if(!recordCrash) {
                recordCrash = true;
               handleException(ex);
                if(mDefaultHandler != null) {
                    mDefaultHandler.uncaughtException(thread, ex);
                }
            }

        }
    }
    private void loop() {
        try {
            Looper.loop();
        } catch (Throwable var2) {
            this.uncaughtException(Thread.currentThread(), var2);
        }

    }
    /**
     * 自定义错误处理,收集错误信息
     * 发送错误报告等操作均在此完成.
     * 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    public boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        String crashFileName = saveCrashInfoToFile(ex);
        //发送错误报告到服务器
        sendCrashReportsToServer(mContext);
        return true;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     *
     * @param ctx
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                File cr = new File(ctx.getFilesDir(), fileName);
                if (cr.length() == 0) {
                    cr.delete();
                } else {
                    postReport(cr);
//                    cr.delete();
                    Log.d(TAG, "上传");
                }
            }
        }
    }

    private void postReport(final File file) {
// 这里不再详述,开发者可以根据OPhoneSDN上的其他网络操作
// 教程来提交错误报告
        HttpUtils http = new HttpUtils();
        RequestParams params = new RequestParams();
        Log.d(TAG, file.length() + "****");
        params.addBodyParameter("error", file);
        params.addBodyParameter("type", "2");
        params.addBodyParameter("mtype", "1");
        params.addBodyParameter("uid", UserHelper.getInstance().getUid());
        params.addBodyParameter("version",getVersionCode()+"");
        http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(TAG + responseInfo.result);
                file.delete();
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public int getVersionCode() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * 获取错误报告文件名
     *
     * @param ctx
     * @return
     */
    private String[] getCrashReportFiles(Context ctx) {
        File filesDir = ctx.getFilesDir();
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        // mDeviceCrashInfo.put(STACK_TRACE, result);

        try {
            long timestamp = System.currentTimeMillis();
            String fileName = "crash-" + timestamp + CRASH_REPORTER_EXTENSION;
            FileOutputStream trace = mContext.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            // mDeviceCrashInfo.store(trace, "");
            trace.write(result.getBytes());
            trace.flush();
            trace.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file...", e);
        }
        return null;
    }


    /**
     * 收集程序崩溃的设备信息
     *
     * @param ctx
     */
    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
//使用反射来收集设备信息.在Build类中包含各种设备信息,
//例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
//具体信息请参考后面的截图
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(null));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }

        }

    }

    /**
     * 检测网络连接是否可用
     *
     * @param ctx
     * @return true 可用; false 不可用
     */
    private boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netinfo = cm.getAllNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        for (int i = 0; i < netinfo.length; i++) {
            if (netinfo[i].isConnected()) {
                return true;
            }
        }
        return false;
    }
}
