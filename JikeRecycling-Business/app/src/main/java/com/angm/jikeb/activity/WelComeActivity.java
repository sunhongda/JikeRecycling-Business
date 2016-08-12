package com.angm.jikeb.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.angm.jikeb.R;

import java.util.Timer;
import java.util.TimerTask;
/*欢迎页*/
public class WelComeActivity extends AppCompatActivity {
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(WelComeActivity.this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }
}
