package com.buslink.busjie.driver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.constant.EventName;
import com.buslink.busjie.driver.entity.MyEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ChooseCityActivity extends Activity {

    private CityAdapter mCityAdapter;
    private ArrayList<String> mCityList = null;
    private String province;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list_layout);
        final Intent intent = getIntent();
        province = intent.getStringExtra("province");
        int provinceIndex = intent.getIntExtra("provinceIndex", 0);
        mCityList = getCityData(provinceIndex);
        initView();
    }

    private void initView() {
        findViewById(R.id.city_back_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(1, null);
                        finish();
                    }
                });
        mCityAdapter = new CityAdapter();
        ListView cityListView = (ListView) findViewById(R.id.city_listview);
        cityListView.setAdapter(mCityAdapter);

        mCityAdapter.notifyDataSetChanged();

        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final String city = mCityList.get(position);
                Intent intent= new Intent();
                intent.putExtra("city", city);
                setResult(0, intent);
                MyEvent e = new MyEvent(EventName.BankCity);
                e.setData(city);
                EventBus.getDefault().post(e);
                finish();
            }
        });
    }

    private class CityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCityList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ChooseCityActivity.this).inflate(
                        R.layout.carmode_list_item, parent, false);
            }
            TextView text1 = (TextView) convertView
                    .findViewById(R.id.carmode_textview);
            String name = mCityList.get(position);
            text1.setText(name);
            return convertView;
        }
    }

    private ArrayList<String> getCityData(int provinceIndex) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("china_citys.json");
            JSONArray jsonArray = new JSONArray(getStringFromInputStream(inputStream));
            JSONObject jsonObject = jsonArray.getJSONObject(provinceIndex);
            JSONArray jsonArray1 = jsonObject.getJSONArray("sub");
            for (int i = 0; i < jsonArray1.length(); i++) {
                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                String city = jsonObject1.getString("name");
                arrayList.add(city);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

}
