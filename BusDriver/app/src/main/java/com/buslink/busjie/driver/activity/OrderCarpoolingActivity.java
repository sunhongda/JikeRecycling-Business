package com.buslink.busjie.driver.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.base.BaseActivity;
import com.buslink.busjie.driver.fragment.AdListFragment2;
import com.buslink.busjie.driver.fragment.OrderCarpoolingFragment;
import com.buslink.busjie.driver.fragment.OrderListFragment;
import com.buslink.busjie.driver.fragment.TripListFragment;


import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/2/20.
 */
public class OrderCarpoolingActivity extends BaseActivity {
    @Bind(R.id.bt_left)
    Button btLeft;
    @Bind(R.id.bt_right)
    Button btRight;
//    @Bind(R.id.rv)
//    RecyclerView rv;
    @Bind(R.id.ib_back)
    ImageButton ibBack;
    @Bind(R.id.left_red)
            TextView left_red;
    @Bind(R.id.right_red)
            TextView right_red;

    int index=R.id.bt_left;
    OrderListFragment orderlistfragment;
    AdListFragment2 ordercarpoolingfragment;
    int isorder=0;
    int iscarpooling=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.a_order_carpoolinglist);
        ButterKnife.bind(this);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isorder=getIntent().getIntExtra("isorder",0);
        iscarpooling=getIntent().getIntExtra("iscarpooling",0);



//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        if(orderlistfragment==null){
//            ft.replace(R.id.fl, new OrderListFragment());
//        }else{
//            ft.replace(R.id.fl,orderlistfragment);
//        }
//
//        ft.commit();

       if(getIntent().getBooleanExtra("isleft",true)) {
           left_red.setVisibility(View.GONE);
           right_red.setVisibility(View.VISIBLE);
           btLeft.performClick();

       }else{
           left_red.setVisibility(View.VISIBLE);
           right_red.setVisibility(View.GONE);
           btRight.performClick();

       }
    }



    @OnClick(value = {R.id.bt_left, R.id.bt_right})
     void onChageList(TextView v) {

//        if (index == v.getId()) {
//            return;
//        }

        switch (v.getText().toString()) {

            case "包车单":
                btLeft.setTextColor(getResources().getColor(R.color.tx_white));
                btLeft.setBackgroundResource(R.mipmap.icon_left_choose_jie);
                btRight.setTextColor(getResources().getColor(R.color.green));
                btRight.setBackgroundResource(R.mipmap.icon_right_song);
                index = v.getId();

                left_red.setVisibility(View.GONE);
                right_red.setVisibility(View.VISIBLE);

//                page = 0;
//                list.clear();
//                getData();
             //   activity.startFragment(BackActivity.class, OrderListFragment.class);//订单
              FragmentTransaction ft = getFragmentManager().beginTransaction();
                if(orderlistfragment==null){
                    ft.replace(R.id.fl, new OrderListFragment());
                }else{
                    ft.replace(R.id.fl,orderlistfragment);
                }
                ft.commit();
                if (iscarpooling <= 0) {
                    right_red.setVisibility(View.GONE);
                } else if (iscarpooling < 100) {
                    right_red.setVisibility(View.VISIBLE);
                    right_red.setText(iscarpooling + "");
                } else {
                    right_red.setVisibility(View.VISIBLE);
                    right_red.setText("99+");
                }

                break;
            case "拼车单":
                btLeft.setTextColor(getResources().getColor(R.color.green));
                btLeft.setBackgroundResource(R.mipmap.icon_left_jie);
                btRight.setTextColor(getResources().getColor(R.color.tx_white));
                btRight.setBackgroundResource(R.mipmap.icon_right_choose_song);
                index = v.getId();

                left_red.setVisibility(View.VISIBLE);
                right_red.setVisibility(View.GONE);

                FragmentTransaction ft2 = getFragmentManager().beginTransaction();
                if(ordercarpoolingfragment==null){
                    ft2.replace(R.id.fl,new AdListFragment2());
                }else{
                    ft2.replace(R.id.fl,ordercarpoolingfragment);
                }
                ft2.commit();

                if (isorder <= 0) {
                    left_red.setVisibility(View.GONE);
                } else if (isorder < 100) {
                    left_red.setVisibility(View.VISIBLE);
                    left_red.setText(isorder + "");
                } else {
                    left_red.setVisibility(View.VISIBLE);
                    left_red.setText("99+");
                }


                break;
        }
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
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscriber(tag="reforderred")
    void reforderred(String e){
        isorder--;
        left_red.setText(isorder);
        if (isorder <= 0) {
            left_red.setVisibility(View.GONE);
        } else if (isorder < 100) {
            left_red.setVisibility(View.VISIBLE);
            left_red.setText(isorder + "");
        } else {
            left_red.setVisibility(View.VISIBLE);
            left_red.setText("99+");
        }
    }
    @Subscriber(tag="refcarpoolingred")
    void refcarpoolingred(String e){
       // app.toast("接收到了拼车单");
        iscarpooling--;
        right_red.setText(iscarpooling);
        if (iscarpooling <= 0) {
            right_red.setVisibility(View.GONE);
        } else if (iscarpooling < 100) {
            right_red.setVisibility(View.VISIBLE);
            right_red.setText(iscarpooling + "");
        } else {
            right_red.setVisibility(View.VISIBLE);
            right_red.setText("99+");
        }


    }
}
