package com.buslink.busjie.driver.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.entity.MyEvent;
import com.buslink.busjie.driver.view.PickerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class ChooseCarActivity extends AppCompatActivity {

    private String mProvince, mAlphabet;

    @Bind(R.id.pv)
    PickerView pv;
    @Bind(R.id.pv2)
    PickerView pv2;

    @OnClick(R.id.cancel) void cancel(View v) {
        finish();
    }

    @OnClick(R.id.ok) void ok(View v) {
        if (TextUtils.isEmpty(mProvince)) {
            mProvince = "京";
        }
        if (TextUtils.isEmpty(mAlphabet)) {
            mAlphabet = "A";
        }
        MyEvent e = new MyEvent(EventName.ChooseCarLicence);
        e.putExtra("province", mProvince);
        e.putExtra("alphabet", mAlphabet);
        EventBus.getDefault().post(e);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_car_licence);
        ButterKnife.bind(this);
        List<String> provinceList = new ArrayList<>();
        List<String> alphabetList = new ArrayList<>();
        String province = "京沪粤冀津渝豫云辽黑湘皖鲁新苏浙鄂桂甘晋蒙陜吉闽贵青藏川宁琼竷";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < province.length(); i++) {
            provinceList.add(String.valueOf(province.charAt(i)));
        }
        for (int i = 0; i < alphabet.length(); i++) {
            alphabetList.add(String.valueOf(alphabet.charAt(i)));
        }
        pv.setData(provinceList);
        pv.setTextSize(60);
        pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mProvince = text;
            }
        });
        pv2.setData(alphabetList);
        pv2.setTextSize(60);
        pv2.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mAlphabet = text;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getWindow().getDecorView();
        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
        lp.gravity = Gravity.BOTTOM;
        getWindowManager().updateViewLayout(view, lp);
    }
}
