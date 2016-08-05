package com.buslink.busjie.driver.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.buslink.busjie.driver.activity.PickPhotoActivity;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.RequestName;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.manager.DeviceInfoManager;
import com.buslink.busjie.driver.manager.MyApplication;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/23.
 */
public class MyHelper {

    private static AsyncHttpClient client;
    public static String homeToDrawerToUserInfo = "homeToDrawerToUserInfo"; // 接单，评价，排名 在首页才有，而司机信息页需要

    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static void writeStringToFile(String fileName, String data) throws IOException {
        File file = new File(PickPhotoActivity.OUT_FILE_DIR, fileName);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        file.createNewFile();

        /*FileWriter fileWriter = new FileWriter(file.getName());
        BufferedWriter bufferWritter = new BufferedWriter(fileWriter);
        bufferWritter.write(data);
        bufferWritter.close();*/

        FileOutputStream fos = new FileOutputStream(file);
        byte[] contentInBytes = data.getBytes();
        fos.write(contentInBytes);
        fos.flush();
        fos.close();
    }

    public static void setRoundImage(Context context, ImageView image, String url, @DrawableRes final int defaultImg) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        BitmapUtils bitmapUtils = new BitmapUtils(context);
        bitmapUtils.display(image, Net.IMGURL + url, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                bitmap = MyBitmapUtils.createCirclImage(bitmap, bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
                imageView.setImageResource(defaultImg);
            }
        });
    }

    public static ArrayList<String> getProvinceData(InputStream inputStream) {
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            JSONArray jsonArray = new JSONArray(MyHelper.getStringFromInputStream(inputStream));
            int jsonArrayLength = jsonArray.length();
            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String province = jsonObject.getString("name");
                arrayList.add(province);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public static RequestParams getParams() {
        RequestParams params = new RequestParams();
        params.add(RequestName.UID, UserHelper.getInstance().getUid());
        params.add("did", UserHelper.getInstance().getUid());
        params.add(RequestName.VERSION, MyApplication.getVersionCode());
        params.add(RequestName.YZ_MID, DeviceInfoManager.getDeviceId());
        params.add(RequestName.MID, DeviceInfoManager.getDeviceId());
        params.add(RequestName.YZ_PHONE, UserHelper.getInstance().getPhone());
        params.add(RequestName.M_TYPE, "1");
        params.add(RequestName.TYPE, "2");
        return params;
    }

    public static AsyncHttpClient getClient() {
        if (client == null) {
            synchronized (MyHelper.class) {
                client = new AsyncHttpClient();
            }
        }
        return client;
    }

}
