package com.angm.jikeb.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.widget.RelativeLayout;

import com.angm.jikeb.R;

import butterknife.Bind;


public class MainActivity extends BaseActivity {
    @Bind(R.id.main_rl)
    RelativeLayout mainRl;
    private boolean isWarnedToClose = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }


    /**
     * 再按一次退出
     */
    @Override
    public void onBackPressed() {
        if (isWarnedToClose) {
            finish();
        } else {
            isWarnedToClose = true;
            //app.toast("再按一次退出");
            Snackbar.make(mainRl, "再按一次退出", Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWarnedToClose = false;
                }
            }, 2000);
        }
    }
}
