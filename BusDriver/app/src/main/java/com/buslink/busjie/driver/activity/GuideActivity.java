package com.buslink.busjie.driver.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.AppConstant;
import com.buslink.busjie.driver.db.User;
import com.buslink.busjie.driver.manager.DbManager;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.util.DensityUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GuideActivity extends AppCompatActivity {

    @Bind(R.id.pager)
    ViewPager pager;

    List<View> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
//        int i = pager.getChildCount();
//        View v = pager.getChildAt(0);
        initView();
    }

    private void initView() {
        ImageView imageView1 = new ImageView(this);
        LayoutInflater.from(this).inflate(R.layout.img1, null);
        imageView1.setImageResource(R.mipmap.guide1);
       imageView1.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.mipmap.guide2);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.mipmap.guide3);
    imageView3.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView imageView4 = new ImageView(this);
        imageView4.setImageResource(R.mipmap.guide4);
       imageView4.setScaleType(ImageView.ScaleType.FIT_XY);

//        View view1 = LayoutInflater.from(this).inflate(R.layout.img1, null);
//        View view2 = LayoutInflater.from(this).inflate(R.layout.img2, null);
//        View view3 = LayoutInflater.from(this).inflate(R.layout.img3, null);


        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(imageView4);
       list.add(imageView1);
       list.add(imageView2);
        list.add(imageView3);



        Button button = new Button(this);
        button.setText("立即开始");
       // button.setTextColor(getResources().getColor(R.color.green));
        button.setTextColor(Color.parseColor("#44db3a"));
        button.setBackgroundResource(android.R.color.white);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, Color.parseColor("#00ff00")); // 边框粗细及颜色 绿
        //drawable.setColor(0x22FFFF00); // 边框内部颜色
        button.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(0, 0, 0, DensityUtils.dip2px(this, 150));
        button.setWidth(DensityUtils.dip2px(this, 220));
        button.setHeight(DensityUtils.dip2px(this, 28));
        button.setLayoutParams(layoutParams);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(AppConstant.APP_SP, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(AppConstant.VERSION_CODE, MyApplication.getVersionCode());
                editor.commit();
                moveNext();
            }
        });
        layout.addView(button);
      list.add(layout);


        pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == (object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
               container.addView(list.get(position));
                return list.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(list.get(position));
            }
        });
    }

    private void moveNext() {
        try {
            DbUtils db = DbManager.getDb();
            List<User> userList = db.findAll(User.class);
            if (userList == null || userList.size() == 0) { // 登录界面
                startActivity(new Intent(this, SignInActivity.class));
            } else {
                if (TextUtils.isEmpty(userList.get(0).getName())) {
                    startActivity(new Intent(this, SignInActivity.class));
                } else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                }
            }
            finish();
        } catch (DbException e) {

        }
    }
}
