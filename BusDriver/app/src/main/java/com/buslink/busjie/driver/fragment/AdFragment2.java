package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.MapActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.entity.MapSelectAdressInfo;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.ResultType;
import com.buslink.busjie.driver.util.VerifyForm;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.CitySelectDialog;
import com.buslink.busjie.driver.view.TimeSelectDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 空座发布
 */
public class AdFragment2 extends LevelTwoFragment implements VerifyForm {
  //  int ispushad;
    @Bind(R.id.button)
    Button button;
    @Bind(R.id.start_address)
    TextView mStartAddress;
    @Bind(R.id.start_time)
    TextView mStartTime;
    @Bind(R.id.end_address)
    TextView mEndAddress;
    @Bind(R.id.empty_position)
    TextView mEmptyPosition;
    @Bind(R.id.price)
    TextView mPrice;

    List<Map<String, Object>> list;
//
    @OnClick(R.id.button) void ok() {
//        if (ispushad == 2) {
//            mActivity.app.toast("每天只能发布一条广告");
//            return;
//        }
        if (verify()) {
            postAd();
        }
    }

    @OnClick(R.id.start_time) void startTime() {
        selectTime(mStartTime);
    }

    @Override
    public String getTitle() {
        return "空座发布";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_ad_two;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_ad);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ad_history:
                        //mActivity.startActivity(new Intent(mActivity, AdListActivity.class));

                        //这里调到专门的拼车单的页面
                        mActivity.startFragment(BackActivity.class, AdListFragmentfromempty.class);
                }
                return false;
            }
        });
        Calendar c = Calendar.getInstance();
        mStartTime.setTag(new TimeSelectDialog(mActivity, c).setOnSelectTimeListener(new TimeSelectDialog.SelectTimeListener() {
            @Override
            public void onSelect(String date) {
                mStartTime.setText(date);
            }
        }));

//        mStartAddress.setTag(new CitySelectDialog(mActivity).setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
//            @Override
//            public void onSelect(String province, String city) {
//                mStartAddress.setText(city);
//            }
//        }));
//        mEndAddress.setTag(new CitySelectDialog(mActivity, true).setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
//            @Override
//            public void onSelect(String province, String city) {
//                mEndAddress.setText(city);
//            }
//        }));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        initData();
//        Bundle bundle = getArguments();
//        ispushad = bundle.getInt("ispushad");
    }

    private void initData() {
        list = new LinkedList<>();
        list.add(new HashMap<String, Object>());
        list.add(new HashMap<String, Object>());
    }

    private void postAd() {
//        @Bind(R.id.button)
//        Button button;
//        @Bind(R.id.start_address)
//        TextView mStartAddress;
//        @Bind(R.id.start_time)
//        TextView mStartTime;
//        @Bind(R.id.end_address)
//        TextView mEndAddress;
//        @Bind(R.id.empty_position)
//        TextView mEmptyPosition;
//        @Bind(R.id.price)
//        TextView mPrice;
        Map<String, Object> m = list.get(0);
        MapSelectAdressInfo address = (MapSelectAdressInfo) m.get("address");
        HttpUtils http = new HttpUtils();
        RequestParams params = RequestManager.simplePostParams();
//        params.addBodyParameter("startdate", mStartTime.getText().toString());
//        params.addBodyParameter("enddate", mEndTime.getText().toString());
//        params.addBodyParameter("scity", mStartAddress.getText().toString());
//        params.addBodyParameter("ecity", mEndAddress.getText().toString());
        params.addBodyParameter(JsonName.START_DATE, mStartTime.getText().toString());
        params.addBodyParameter(JsonName.START_PROVINCE,address.mProvince);
        params.addBodyParameter(JsonName.START_CITY,address.mCity);
        params.addBodyParameter(JsonName.START_ADDRESS,address.mAdress);
        params.addBodyParameter(JsonName.SLON, address.lon);
        params.addBodyParameter(JsonName.SLAT, address.lat);
        address = (MapSelectAdressInfo) list.get(1).get("address");//终点
        params.addBodyParameter(JsonName.END_PROVINCE,address.mProvince);
        params.addBodyParameter(JsonName.END_CITY,address.mCity);
        params.addBodyParameter(JsonName.END_ADDRESS,address.mAdress);
        params.addBodyParameter(JsonName.ELON, address.lon);
        params.addBodyParameter(JsonName.ELAT, address.lat);
        params.addBodyParameter(JsonName.TOTAL,mEmptyPosition.getText().toString());
        params.addBodyParameter(JsonName.PRICE,mPrice.getText().toString());




        http.send(HttpRequest.HttpMethod.POST, Net.CARPOOLING, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    mActivity.app.toast("拼车广告发布成功，请在我的发布里查看！");

                    mActivity.startFragment(BackActivity.class, AdListFragmentfromempty.class);
                   // mActivity.startFragment(BackActivity.class, AdListFragment2.class);

                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.d(s, e);
                mActivity.app.toast("广告发布失败");
            }
        });
    }

    private void selectTime(final TextView textView) {
        TimeSelectDialog d = (TimeSelectDialog) textView.getTag();
        d.show();
    }

    private void selectLocation(final TextView textView) {
        CitySelectDialog dialog = (CitySelectDialog) textView.getTag();
        dialog.show();
    }


    @OnClick({R.id.start_address, R.id.end_address})
    void selectAdress(View v) {
        switch (v.getId()) {
            case R.id.start_address:
                startActivityForResult(new Intent(getActivity(), MapActivity.class),0);
                break;
            case R.id.end_address:
                startActivityForResult(new Intent(getActivity(), MapActivity.class),1);
                break;

        }
    }


    @Override
    public boolean verify() {
//        try {
//            if (TextUtils.isEmpty(mStartTime.getText().toString())) {
//                mActivity.app.toast("请选择出发时间");
//                return false;
//            }
//            if (TextUtils.isEmpty(mEndTime.getText().toString())) {
//                mActivity.app.toast("请选择结束时间");
//                return false;
//            }
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//            long startTime, endTime;
//            startTime = format.parse(mStartTime.getText().toString()).getTime();
//            endTime = format.parse(mEndTime.getText().toString()).getTime();
//            if (endTime <= startTime) {
//                mActivity.app.toast("结束时间必须大于出发时间");
//                return false;
//            }
//
//            if (TextUtils.isEmpty(mStartAddress.getText().toString())) {
//                mActivity.app.toast("请选择出发城市");
//                return false;
//            }
//            if (TextUtils.isEmpty(mEndAddress.getText().toString())) {
//                mActivity.app.toast("请选择终点城市");
//                return false;
//            }
//            return true;
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }

        if (TextUtils.isEmpty(mStartAddress.getText().toString())) {
          mActivity.app.toast("请选择出发地");
            return false;
        }

        if ( TextUtils.isEmpty(mStartTime.getText().toString())) {
            mActivity.app.toast("请选择出发时间");
            return false;
        }
        if (mStartTime.getText().toString().compareTo(DateUtils.getSimpleFormatTime(System.currentTimeMillis())) <= 0) {
            mActivity.app.toast("出发时间需大于当前时间");
            return false;
        }
        if ( TextUtils.isEmpty(mEndAddress.getText().toString())) {
            mActivity.app.toast("请选择终点");
            return false;
        }
        if ( TextUtils.isEmpty(mEmptyPosition.getText().toString())) {
            mActivity.app.toast("请选择余座数量");
            return false;
        }else if(mEmptyPosition.getText().toString().equals("0")){
            mActivity.app.toast("余座数量不能为0");
            return false;
        }else if(Integer.parseInt(mEmptyPosition.getText().toString())>68){
            mActivity.app.toast("余座数量不能超过68");
            return false;
        }

        if ( TextUtils.isEmpty(mPrice.getText().toString())) {
            mActivity.app.toast("请填写价格");
            return false;
        }else if(Integer.parseInt(mPrice.getText().toString())==0){
            mActivity.app.toast("价格不能为0");
            return false;
        }
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResultType.OK.ordinal() && data != null) {

                MapSelectAdressInfo info = (MapSelectAdressInfo) data.getSerializableExtra("result");
            if(requestCode==0){
                list.get(0).put(JsonName.ADDRESS,info);
                mStartAddress.setText(info.getmAdress());
            }else if(requestCode==1){
                list.get(1).put(JsonName.ADDRESS,info);
                mEndAddress.setText(info.getmAdress());
            }

//            if (requestCode % 2 == 0) {
//                MapSelectAdressInfo info = (MapSelectAdressInfo) data.getSerializableExtra("result");
//                list.get(requestCode / 2).put("address", info);
//                if (requestCode / 2 == 0) {
//                    mStartAddress.setText(info.getmAdress());
//                } else if (requestCode / 2 == list.size() - 1) {
//                    mEndAddress.setText(info.getmAdress());
//                } else if (requestCode / 2 == list.size() - 2) {
//                    tvBack.setText(info.getmAdress());
//                } else {
//                    adapter.notifyDataSetChanged();
//                }
//            } else {
//                String result = data.getStringExtra(PickPhotoActivity.PHOTO_PATH);
//                if (!TextUtils.isEmpty(result)) {
//                    Map<String, String> m;
//                    if (requestCode / 2 >= listImg.size()) {
//                        m = new HashMap<>();
//                        listImg.add(m);
//                    } else {
//                        m = listImg.get(requestCode / 2);
//                    }
//                    m.put("img", result);
//                    m.put("loading", "1");
//                    uploadImage(requestCode / 2, result);
//                    Log.d("result", result);
//                    adapterImg.notifyDataSetChanged();
//                }
//            }
        }
    }
}