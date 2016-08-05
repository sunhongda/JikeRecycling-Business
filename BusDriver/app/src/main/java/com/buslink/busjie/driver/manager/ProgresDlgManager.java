package com.buslink.busjie.driver.manager;

import android.content.DialogInterface;

import com.buslink.busjie.driver.view.ProgressDlg;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ProgresDlgManager {

    public static void show(ProgressDlg progressDlg) {
        try {progressDlg.setCancelable(true);
            progressDlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            progressDlg.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dismiss(ProgressDlg progressDlg) {
        if (progressDlg != null) {
            progressDlg.dismiss();
        }
    }

}
