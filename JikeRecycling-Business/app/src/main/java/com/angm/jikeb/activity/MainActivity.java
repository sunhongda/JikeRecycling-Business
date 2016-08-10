package com.angm.jikeb.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.angm.jikeb.R;
import com.angm.jikeb.base.BaseActivity;
import com.angm.jikeb.base.BaseFragment;
import com.angm.jikeb.fragment.FindFragment;
import com.angm.jikeb.fragment.HomeFragment;
import com.angm.jikeb.fragment.RecyclFragment;
import com.angm.jikeb.view.Indicator.SlidingTabLayout;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

import butterknife.Bind;


public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar_main_activity)
    Toolbar toolbarTop;
    @Bind(R.id.vp_main)
    ViewPager mPager;
    @Bind(R.id.sliding_tabs)
    SlidingTabLayout slidingTabs;
    @Bind(R.id.order_control)
    Button orderControl;
    @Bind(R.id.content_frame)
    AutoFrameLayout contentFrame;
    @Bind(R.id.main_rl)
    AutoRelativeLayout mainRl;
    private boolean isWarnedToClose = false;
    private AMap mMap;

    ///11
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fragmentName = getIntent().getStringExtra("fragmentName");
        Bundle bundle = getIntent().getBundleExtra("fragmentBundle");
        initToolbar();

    }


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        initToolbar();
        initViewPager();
        initTab();
    }

    @Override
    public void initData() {
        application.addActivity(this);
    }


    public Toolbar getmToolbar() {
        return toolbarTop;
    }


    private void initToolbar() {
        toolbarTop.setTitle("");
        toolbarTop.setNavigationIcon(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 初始化ViewPager
     */
    public void initViewPager() {
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        BaseFragment homeFragment = new HomeFragment();
        BaseFragment recyclFragment = new RecyclFragment();
        BaseFragment findFragment = new FindFragment();

        fragmentList.add(homeFragment);
        fragmentList.add(recyclFragment);
        fragmentList.add(findFragment);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, this));
        mPager.setCurrentItem(0);//设置当前显示标签页为第一页
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());//页面变化时的监听器
    }


    private void initTab() {
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setTabTitleTextSize(16);//标题字体大小
        slidingTabLayout.setTitleTextColor(Color.WHITE, Color.WHITE);//标题字体颜色
        slidingTabLayout.setTabStripWidth(110);//滑动条宽度
        slidingTabLayout.setSelectedIndicatorColors(Color.WHITE);//滑动条颜色
        slidingTabLayout.setDistributeEvenly(true); //均匀平铺选项卡
        slidingTabLayout.setViewPager(mPager);//最后调用此方法
    }


    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        private Context mContext;
        private String[] mTabTitle;


        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list, Context context) {
            super(fm);
            this.list = list;
            this.mContext = context;
            mTabTitle = mContext.getResources().getStringArray(R.array.tab_name);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitle[position];
        }
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
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
