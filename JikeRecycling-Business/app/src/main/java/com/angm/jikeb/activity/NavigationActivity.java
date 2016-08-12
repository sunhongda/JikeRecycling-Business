package com.angm.jikeb.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.angm.jikeb.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NavigationActivity extends AppCompatActivity {
    private static final int[] resource = new int[]{R.mipmap.welcome1, R.mipmap.welcome2,
            R.mipmap.welcome3, R.mipmap.welcome4};
    private ViewPager viewPager;
    private int currentItem;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        /*存储第一次进入显示*/
        preferences = getSharedPreferences("navigation", MODE_PRIVATE);
        boolean flag = preferences.getBoolean("flag", true);
        if (!flag) {
            startActivity(new Intent(this, WelComeActivity.class));
            finish();
        }
        MyFragmentStatePager adpter = new MyFragmentStatePager(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adpter);
        /*监听*/
        /*动画适配器*/
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float startY;
            float endX;
            float endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();
                        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                        //获取屏幕的宽度
                        Point size = new Point();
                        windowManager.getDefaultDisplay().getSize(size);
                        int width = size.x;
                        //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
                        if (currentItem == (resource.length - 1) && startX - endX > 0 && startX - endX >= (width / 4)) {
                            Log.i("zzz", "进入了触摸");
                            goToMainActivity();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                        }
                        break;
                }
                return false;
            }
        });
    }


    private void goToMainActivity() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();

        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean("flag", false);
        ed.commit();
    }

    public class MyFragmentStatePager
            extends FragmentStatePagerAdapter {

        public MyFragmentStatePager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new MyFragment(position);
        }

        @Override
        public int getCount() {
            return resource.length;
        }
    }

    @SuppressLint("ValidFragment")
    public class MyFragment extends Fragment {
        @Bind(R.id.navigation_image)
        ImageView navigationImage;
        private int position;

        public MyFragment(int position) {
            this.position = position;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View image = inflater.inflate(R.layout.fragment_navigation, null);
            ButterKnife.bind(this, image);
            navigationImage.setImageResource(resource[position]);
            return image;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
        }
    }
}
