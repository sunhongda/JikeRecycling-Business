package com.buslink.busjie.driver.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.AppConstant;
import com.buslink.busjie.driver.db.User;
import com.buslink.busjie.driver.manager.DbManager;
import com.buslink.busjie.driver.manager.MyApplication;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.version_name)
    TextView versoinName;

    DbUtils db;
    private static final String BaiduPushApiKey = "PG7g4UbnUoZ6UYCKTW3GA7z7";
    //private static final String BaiduPushApiKey = "n4W4Rstv0H6buambnYblxVig";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        versoinName.setText("V" + MyApplication.getVersionName());
        PushManager.startWork(this, PushConstants.LOGIN_TYPE_API_KEY, BaiduPushApiKey);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreferences sp = getSharedPreferences(AppConstant.APP_SP, MODE_PRIVATE);
                    String currentVersion = sp.getString(AppConstant.VERSION_CODE, "0");
                    if (!MyApplication.getVersionCode().equals(currentVersion)) {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));

                    } else {
                        db = DbManager.getDb();
                        List<User> userList = db.findAll(User.class);
                        if (userList == null || userList.size() == 0) { // 登录界面

                            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                        } else {
                            if (TextUtils.isEmpty(userList.get(0).getName())) {
                                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                            } else {
                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                    finish();
                } catch (DbException e) {

                }

            }
        }, 2000);
    }
}
