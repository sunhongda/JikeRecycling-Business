package com.buslink.busjie.driver.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.buslink.busjie.driver.R;

public class ProgressDlg extends Dialog {

    private TextView tvMsg;

    public ProgressDlg(Activity activity) {
        this(activity, null);
    }

    public ProgressDlg(Activity context, String msg) {
        super(context, R.style.custom_dlg);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.widget_progress_dlg);
        tvMsg = (TextView) findViewById(R.id.msg);
        if (msg != null && !msg.equals("")) {
            tvMsg.setText(msg);
        }
    }

    public ProgressDlg(Activity context, String msg, String additionalMsg) {
        super(context, R.style.custom_dlg);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.widget_progress_dlg);
        tvMsg = (TextView) findViewById(R.id.msg);
        TextView tvAdditionalMsg = (TextView) findViewById(R.id.additional_msg);

        if (msg != null && !msg.equals("")) {
            tvMsg.setText(msg);
        }

        if (additionalMsg != null && !additionalMsg.equals("")) {
            tvAdditionalMsg.setText(additionalMsg);
            tvAdditionalMsg.setVisibility(View.VISIBLE);
        } else {
            tvAdditionalMsg.setVisibility(View.GONE);
        }
    }

    public void setMessage(String msg) {
        if (msg != null && !msg.equals("")) {
            tvMsg.setText(msg);
        }
    }

}
