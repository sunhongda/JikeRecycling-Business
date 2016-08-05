package com.buslink.busjie.driver.activity;

import android.support.v7.app.AlertDialog;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;

/**
 * Created by Administrator on 2015/11/25.
 */
public class IntroduceActivity extends Activity {
    LinearLayout ll_all;
    int i = 1;
    private MediaPlayer mpStart;
    View parent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_introduce_layout);

        playVoice(0);

        ll_all = (LinearLayout) findViewById(R.id.ll_all);
        ll_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (i) {
                    case 1:
                        ll_all.setBackgroundResource(R.mipmap.introduce_b);
                        i = 3;

                        //播放第一段录音
                        playVoice(1);//发布信息让乘客更容易找到您
                        //
                        break;
//                    case 2:
//                        ll_all.setBackgroundResource(R.mipmap.introduce_c);//拼车单
//                        i = 3;
//
//                        playVoice(2);//点击拼车单
//                        break;

                    case 3:
                        ll_all.setBackgroundResource(R.mipmap.introduce_c);
                        i = 4;

                        playVoice(3);//更多功能请点击个人中心
                        break;


                    case 4:
                        SharedPreferences sp = getSharedPreferences("is_enter_introduce", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        //false
                        editor.putBoolean("is_enter_introduce", false);

                        editor.commit();

                        playVoice(4);


//                        View contentView = getLayoutInflater()
//                                .inflate(R.layout.home_fragment_ad, null);
//
//                        /**初始化PopupWindow*/
//                        final PopupWindow popupWindow = new PopupWindow(contentView,
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT);
//                        popupWindow.setFocusable(true);// 取得焦点
//                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//                        /**设置PopupWindow弹出和退出时候的动画效果*/
//                        // popupWindow.setAnimationStyle(R.style.animation);
//
//                        parent =contentView;
//
//                        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//                   //     popupWindow.showAsDropDown(contentView);
//
//                        ImageView iv= (ImageView) contentView.findViewById(R.id.iv_home_fragment_ad);
//                        iv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(getApplicationContext(), "我是广告，我是广告！", Toast.LENGTH_LONG).show();
//                                popupWindow.dismiss();
//
//                                finish();
//                            }
//                        });


                        final View pv = getLayoutInflater().inflate(R.layout.p_ad, null);

                        final AlertDialog d = new AlertDialog.Builder(IntroduceActivity.this).setView(pv).create();
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        d.setCanceledOnTouchOutside(false);
                        TextView tv = (TextView) pv.findViewById(R.id.tv);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Animation a = new ScaleAnimation(1, 1, 1, 0);
                                a.setDuration(200);
                                pv.startAnimation(a);
                                a.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        d.dismiss();
                                        finish();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                            }
                        });
                        d.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Animation a = new ScaleAnimation(1, 1, 0, 1);
                                a.setDuration(200);
                                pv.startAnimation(a);
                            }
                        });
                        d.show();
                        break;


                }
            }
        });
    }

    public void playVoice(int flag) {

        int id = 0;
        switch (flag) {
            case 0:
                id = R.raw.introduce_start_jiedan;
                break;
            case 1:
                id = R.raw.clickcarpooling;
                break;
            case 2:
                id = R.raw.clickcarpoolinglist;
                break;
            case 3:
                id = R.raw.introduce_more_function;
                break;
            case 4:
                id = R.raw.introduce_welcome;
                break;
        }

        if (mpStart != null) {
            mpStart.release();
        }
        mpStart = MediaPlayer.create(this, id);
        mpStart.start();

    }
}
