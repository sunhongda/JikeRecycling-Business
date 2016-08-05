package com.buslink.busjie.driver.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.view.PickerView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public class ProvinceCityActivity extends AppCompatActivity {

    @ViewInject(R.id.province)
    PickerView province;

    @ViewInject(R.id.city)
    PickerView city;

    List<String> provinceList = new ArrayList<>();
    JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province_city);
        ViewUtils.inject(this);

        try {
            InputStream inputStream = getAssets().open("china_citys.json");
           jsonArray = new JSONArray(MyHelper.getStringFromInputStream(inputStream));
            int jsonArrayLength = jsonArray.length();
            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String province = jsonObject.getString("name");
                provinceList.add(province);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        province.setData(new ArrayList<String>(provinceList));
        province.setTextSize(60);
        province.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                int provinceIndex = provinceList.indexOf(text);
                city.setData(getCityData(provinceIndex));
            }
        });

        List<String> list = new ArrayList<>();
        list.add("北京市");
        city.setData(list);
        city.setTextSize(60);
    }

    private List<String> getCityData(int index) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(index);
            JSONArray jsonArray1 = jsonObject.getJSONArray("sub");
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                String city = jsonObject1.getString("name");
                arrayList.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
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
