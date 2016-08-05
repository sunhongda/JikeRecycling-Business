package com.buslink.busjie.driver.activity;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BaseActivity;
import com.lidroid.xutils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/11/23.
 */
public class ScanPhotosActivity extends BaseActivity {

    public ImageView iv_bakc;
    private ViewPager viewPager;
    private ArrayList<String> list;
    private ImageView iv_photo_item;
    private String currentshowphoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_scanphonos_vp);
        iv_bakc=(ImageView)findViewById(R.id.iv_back);
        viewPager = (ViewPager) findViewById(R.id.vp);
        //将view装入数组
        list =getIntent().getStringArrayListExtra("plist");

        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                return list.size();
            }

            @Override
            //断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0==arg1;
            }
            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                 //arg0.removeView((View)arg2);
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int position){
               // ((ViewPager)arg0).addView(pageview.get(position));

               // View view = (View) View.inflate(getApplicationContext(),R.layout.a_scanphonos_vp_item,null);

               // ImageView ivshow= (ImageView) View.inflate(getApplicationContext(), R.layout.a_scanphonos_vp_item, null);
                LayoutInflater inflater =getLayoutInflater();
                View ivshow = inflater.inflate(R.layout.a_scanphonos_vp_item,null);

                iv_photo_item=(ImageView) ivshow.findViewById(R.id.iv_image_item);

                ((ViewPager) arg0).addView(ivshow);

                 currentshowphoto=list.get(position);
//                Picasso.with(getApplicationContext())
//                        .load(currentshowphoto)
////                        .placeholder(R.mipmap.icon_driver)
////                        .error(R.mipmap.icon_driver)
//                                // .transform(new CircleTransform())
//                        .into(iv_photo_item);

                BitmapUtils bUtils=new BitmapUtils(getApplicationContext());
                bUtils.display(iv_photo_item,currentshowphoto);

                return ivshow;
            }
        };

        viewPager.setAdapter(mPagerAdapter);
        viewPager.setCurrentItem(Integer.valueOf(getIntent().getStringExtra("currentposition")));

        iv_bakc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    }













