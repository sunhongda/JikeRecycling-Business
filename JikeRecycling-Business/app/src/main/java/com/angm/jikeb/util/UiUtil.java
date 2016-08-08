package com.angm.jikeb.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by shd on 16-8-8.
 */
public class UiUtil {


    public static void ShowMessageBox(Context c, String title, String content, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("确定", listener);

        builder.show();
    }

}
