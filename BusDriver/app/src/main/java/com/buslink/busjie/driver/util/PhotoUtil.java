package com.buslink.busjie.driver.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.buslink.busjie.driver.R;

/**
 * Created by yanlong.luo on 2015/9/9.
 */
public class PhotoUtil {

    private AppCompatActivity activity;

    public void choosePicture(int requestCode, String pictureName) {

        Intent intent = new Intent();
        //Use the proper Intent action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);

    }

    public void takePhoto() {

    }

}
