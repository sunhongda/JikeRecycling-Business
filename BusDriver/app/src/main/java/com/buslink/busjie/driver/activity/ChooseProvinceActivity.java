package com.buslink.busjie.driver.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.util.MyHelper;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChooseProvinceActivity extends Activity {

    private ProvinceAdapter mProvinceAdapter;
    private ArrayList<String> mProvinceList = null;
    private String province;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data != null) {
            String city = data.getStringExtra("city");
            Intent intent = new Intent();
            intent.putExtra("city", city);
            setResult(4, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.province_list_layout);
        mProvinceList = getProvinceData();
        initView();
//        try {
//            fetchXML();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void initView() {
        findViewById(R.id.province_back_btn).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        mProvinceAdapter = new ProvinceAdapter();
        ListView provinceListView = (ListView) findViewById(R.id.province_listview);
        provinceListView.setAdapter(mProvinceAdapter);

        mProvinceAdapter.notifyDataSetChanged();

        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                province = mProvinceList.get(position);
                Intent intent = new Intent(ChooseProvinceActivity.this, ChooseCityActivity.class);
                intent.putExtra("province", province);
                intent.putExtra("provinceIndex", position);
                startActivityForResult(intent, 0);
            }
        });
    }

    private class ProvinceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mProvinceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mProvinceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ChooseProvinceActivity.this).inflate(
                        R.layout.carmode_list_item, parent, false);
            }
            TextView text1 = (TextView) convertView
                    .findViewById(R.id.carmode_textview);
            String name = mProvinceList.get(position);
            text1.setText(name);
            return convertView;
        }
    }

    private ArrayList<String> getProvinceData() {
        ArrayList<String> arrayList = new ArrayList<String>();
        try {
            InputStream inputStream = getAssets().open("china_citys.json");
            JSONArray jsonArray = new JSONArray(MyHelper.getStringFromInputStream(inputStream));
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

    private void fetchXML() throws XmlPullParserException, IOException, JSONException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        //factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(getAssets().open("Provineces.xml"), null);
        parseXML(xpp);
    }

    private void parseXML(XmlPullParser xpp) throws XmlPullParserException, IOException, JSONException {
        String text = null;
        int event = xpp.getEventType();
        JSONArray provinceAr = new JSONArray();
        JSONObject provinceJO = null;
        JSONArray cityJA = null;
        JSONObject cityJO = null;
        int array = 0, string=0;
        String name = "";
        while (event != XmlPullParser.END_DOCUMENT) {
            name = xpp.getName();
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if(TextUtils.equals(xpp.getName(),"array")){
                        array++;
                    }
                    if(array==1){
                        if(TextUtils.equals(xpp.getName(),"array")){
                            provinceAr=new JSONArray();
                        }else if(TextUtils.equals(xpp.getName(),"dict")){
                            provinceJO=new JSONObject();
                            string=0;
                        }else if(TextUtils.equals(xpp.getName(),"string")){
                            string++;
                            switch (string){
                                case 2:
                                    xpp.next();
                                    String s=xpp.getText();
                                    provinceJO.put("name",s);
                                    break;
                            }
                        }
                    }else{
                        if(TextUtils.equals(xpp.getName(),"array")){
                            cityJA=new JSONArray();
                        }else if(TextUtils.equals(xpp.getName(),"dict")){
                            cityJO=new JSONObject();
                            string=0;
                        }else if(TextUtils.equals(xpp.getName(),"string")){
                            string++;
                            switch (string){
                                case 1:
                                    xpp.next();
                                    cityJO.put("name",xpp.getText());
                                    break;
                            }
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if(array==1){
                        if(TextUtils.equals(xpp.getName(),"array")){
                            LogUtils.d(provinceAr.toString());
                        }else if(TextUtils.equals(xpp.getName(),"dict")){
                            provinceAr.put(provinceJO);
                        }
                    }else{
                        if(TextUtils.equals(xpp.getName(),"array")){
                            provinceJO.put("sub",cityJA);
                        }else if(TextUtils.equals(xpp.getName(),"dict")){
                            cityJA.put(cityJO);
                        }
                    }
                    if(TextUtils.equals(xpp.getName(),"array")){
                        array--;
                    }
                    break;
            }
            event = xpp.next();
        }
    }
}
