package com.buslink.busjie.driver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;

import java.util.Arrays;
import java.util.List;

public class BusBrandActivity extends Activity {

    private MyAdapter mMyaAdapter;
    private List<String> mCarModeList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carmode_list_layout);
        String[] busbrand = getResources().getStringArray(R.array.busbrand);
        //mCarModeList = ConfigerHelper.getInstance().getCarModeData();
        mCarModeList = Arrays.asList(busbrand);
        initView();
    }

    public void initView() {
        findViewById(R.id.carmode_back_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        mMyaAdapter = new MyAdapter();
        ListView listView = (ListView) findViewById(R.id.carmode_listview);
        listView.setAdapter(mMyaAdapter);

        mMyaAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final String mode = mCarModeList.get(position);

                Intent intent = new Intent();
                intent.putExtra("mode", mode);
                setResult(3, intent);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCarModeList.size();
        }

        @Override
        public Object getItem(int position) {
            return mCarModeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(BusBrandActivity.this).inflate(
                        R.layout.carmode_list_item, parent, false);
            }
            TextView text1 = (TextView) convertView
                    .findViewById(R.id.carmode_textview);
            String name = mCarModeList.get(position);
            text1.setText(name);
            return convertView;
        }
    }
}
