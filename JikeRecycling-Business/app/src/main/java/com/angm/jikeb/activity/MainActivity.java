package com.angm.jikeb.activity;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.angm.jikeb.R;
import com.angm.jikeb.fragment.HomeFragment;
import com.angm.jikeb.fragment.MapFragment;
import com.angm.jikeb.fragment.RecyclFragment;
import com.angm.jikeb.util.Log;

import java.util.ArrayList;

import butterknife.Bind;


public class MainActivity extends BaseActivity {
    @Bind(R.id.main_rl)
    RelativeLayout mainRl;
    private boolean isWarnedToClose = false;
    private AMap mMap;
    private ViewPager mPager;
    private int anInt;
    private int currIndex;//当前页卡编号
    private int bmpW;//横线图片宽度
    private int offset;//图片移动的偏移量
    private ImageView imageLine;


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        Log.i(" initView ");

        InitImage();
        InitViewPager();
    }

    @Override
    public void initData() {

    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    public class ViewPageListener implements View.OnClickListener {
        private int index = 0;

        public ViewPageListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            mPager.setCurrentItem(index);
        }
    }

    /*
       * 初始化ViewPager
       */
    public void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vp_main);
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        HomeFragment homeFragment = new HomeFragment();
        RecyclFragment recyclFragment = new RecyclFragment();
        MapFragment mapFragment = new MapFragment();

        fragmentList.add(homeFragment);
        fragmentList.add(recyclFragment);
        fragmentList.add(mapFragment);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
    }

    /*
    * 初始化图片的位移像素
    */
    public void InitImage() {
        Log.i("  InitImage  ");
//        imageLine = (ImageView) findViewById(R.id.iv_title);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.fab_background).getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        anInt = (screenW / 4 - bmpW) / 2;

        //imgageview设置平移，使下划线平移到初始位置（平移一个offset）
        Matrix matrix = new Matrix();
        matrix.postTranslate(anInt, 0);
        imageLine.setImageMatrix(matrix);
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int one = anInt * 2 + bmpW;//两个相邻页面的偏移量

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            Animation animation = new TranslateAnimation(currIndex * one, arg0 * one, 0, 0);//平移动画
            currIndex = arg0;
            animation.setFillAfter(true);//动画终止时停留在最后一帧，不然会回到没有执行前的状态
            animation.setDuration(200);//动画持续时间0.2秒
            imageLine.startAnimation(animation);//是用ImageView来显示动画的
            int i = currIndex + 1;
            Toast.makeText(MainActivity.this, "您选择了第" + i + "个页卡", Toast.LENGTH_SHORT).show();
        }
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
//            Snackbar.make(mainRl, "再按一次退出", Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isWarnedToClose = false;
                }
            }, 2000);
        }
    }
}
