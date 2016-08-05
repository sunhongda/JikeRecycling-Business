package com.buslink.busjie.driver.base;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.fragment.LevelOneFragment;
import com.buslink.busjie.driver.util.MyHelper;

public class DrawerActivity extends BaseActivity {

    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    protected FrameLayout mFrameLayout;

    protected String mTitle;

    private ActionBarDrawerToggle mDrawerToggle;
    public LevelOneFragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.home, R.string.home);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
       // mToolbar.setNavigationIcon(R.mipmap.home_wo_default);
       // mToolbar.setNavigationIcon(null);
       // MyHelper.setRoundImage(activity, mHeadImg, headUrl, R.mipmap.head_ring);
    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    public DrawerLayout getmDrawerLayout() {
        return mDrawerLayout;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit);
        ft.commit();
    }

    public void startFragment(Class<?> activity,Class<?> fragment,Intent intent){
        if(intent==null){
            intent=new Intent();
        }
        intent.putExtra("fragmentName", fragment.getName());
        intent.setClass(this, activity);
        startActivity(intent);
    }

    public void startFragment(Class<?> activity,Class<?> fragment){
        startFragment(activity, fragment, null);
    }

}
