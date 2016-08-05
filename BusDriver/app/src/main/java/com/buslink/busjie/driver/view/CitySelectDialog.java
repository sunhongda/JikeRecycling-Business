package com.buslink.busjie.driver.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.DensityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class CitySelectDialog extends Dialog implements View.OnClickListener {
    Context context;
    selectCityListener listener;
    private TextView tvTile;
    private TextView tvLeft,tvRight;
    private PickerView pvP,pvC;
    String province, city;
    List<String> pString,cString;
    private JSONArray jsonArray;
    private boolean hasLimit = false;

    public CitySelectDialog(Context context) {
        this(context, false);
    }

    public CitySelectDialog(Context context, boolean hasLimit) {
        super(context);
        this.context=context;
        this.hasLimit = hasLimit;
        initView();
        initListener();
        initData();
    }

    private void initView(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_select_city);
        tvTile=(TextView)findViewById(R.id.tv_title);
        tvLeft=(TextView)findViewById(R.id.left);
        tvRight=(TextView)findViewById(R.id.right);
        pvP=(PickerView)findViewById(R.id.pv);
        pvC=(PickerView)findViewById(R.id.pv_1);
        pvC.setTextSize(DensityUtils.dip2px(context, 20));
        pvP.setTextSize(DensityUtils.dip2px(context, 20));
        setCancelable(false);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.down2up);
    }
    public void initListener(){
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);

        pvP.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                province = text;
                cString = getCityData(pString.indexOf(text));
                city = cString.get(0);
                pvC.setData(cString);
            }
        });
        pvC.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                city = text;
            }
        });
    }

    public void initData(){
        pString=getProvinceData();
        province=pString.get(0);
        cString=getCityData(0);
        city=cString.get(0);
        List<String> pl=new ArrayList<>();
        pl.addAll(pString);
        pvP.setData(pl);
        pvC.setData(cString);
    }

    public interface selectCityListener{
        void onSelect(String province, String city);
    }

    private ArrayList<String> getProvinceData() {
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            InputStream inputStream = context.getAssets().open("china_citys.json");
            if (hasLimit) {
                //arrayList.add("不限");
                String s0 = getStringFromInputStream(inputStream);
                String s1 = s0.substring(0, 1);
                String s2 = s0.substring(1, s0.length());
                String s3 = s1 + "{\"sub\":[{\"name\":\"不限\"}],\"name\":\"不限\"}," + s2;
                jsonArray = new JSONArray(s3);
            } else {
                jsonArray = new JSONArray(getStringFromInputStream(inputStream));
            }
            int jsonArrayLength = jsonArray.length();

            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String province = jsonObject.getString("name");
                arrayList.add(province);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private ArrayList<String> getCityData(int provinceIndex) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(provinceIndex);
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
    private String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left:
                this.dismiss();
                break;
            case R.id.right:
                listener.onSelect(province, city);
                this.dismiss();
                break;
        }
    }

    public CitySelectDialog setOnSelectCityListener(selectCityListener listener){
        this.listener=listener;
        return this;
    }
}
