package com.buslink.busjie.driver.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.BusBrandActivity;
import com.buslink.busjie.driver.activity.PickPhotoActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.UserConstant;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.response.ViewCarResponse;
import com.buslink.busjie.driver.util.CameraUtil;
import com.buslink.busjie.driver.util.DateUtils;
import com.buslink.busjie.driver.util.MyBitmapUtils;
import com.buslink.busjie.driver.util.PhotoSelectOptions;
import com.buslink.busjie.driver.util.XString;
import com.buslink.busjie.driver.view.CitySelectDialog;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/15.
 */
public class BusInfoFragment extends LevelTwoFragment {

    MenuItem edit;
    ViewCarResponse response = new ViewCarResponse();
    boolean editEnable;
    CitySelectDialog dialog;
    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };
    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };
    @Bind(R.id.choose_bus_photo)
    LinearLayout busLl;
    @Bind(R.id.choose_bus_card)
    LinearLayout cardLl;
    @Bind(R.id.bus_photo)
    ImageView busPhoto;
    @Bind(R.id.bus_loading)
    ImageView busLoading;
    @Bind(R.id.bus_number)
    EditText busNumber;
    @Bind(R.id.brand)
    TextView brand;
    @Bind(R.id.type)
    TextView type;
    @Bind(R.id.city)
    TextView city;
    @Bind(R.id.seat_number)
    EditText seatNumber;
    @Bind(R.id.photo)
    TextView photo;
    @Bind(R.id.ok_btn)
    Button okBtn;
    @Bind({R.id.choose_bus_photo, R.id.choose_bus_card, R.id.bus_number, R.id.brand,
        R.id.type, R.id.city, R.id.seat_number, R.id.photo, R.id.ok_btn, R.id.choose_brand,
        R.id.choose_type, R.id.choose_city})
    List<View> group;


    @Override
    public String getTitle() {
        return "车辆信息";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_bus_info;
    }

    @Override
    protected void initView() {
        mToolbar.inflateMenu(R.menu.menu_user_info);
        Menu menu = mToolbar.getMenu();
        edit = menu.getItem(0);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_user_info_edit:
                        if (!editEnable) {
                            enableEdit();
                        } else {
                            disableEdit();
                            if (response.isStatus()) {
                                displayData(false);
                            }
                        }
                        break;
                }
                return false;
            }
        });
        disableEdit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick(R.id.choose_bus_photo) void chooseCarPhoto() {
        mActivity.startFragment(BackActivity.class, CarPhotoEditFragment.class);
    }

    @OnClick(R.id.choose_brand) void chooseBrand() {
        Intent busBrandIntent = new Intent(mActivity, BusBrandActivity.class);
        startActivityForResult(busBrandIntent, 0);
    }

    @OnClick(R.id.choose_type) void chooseType() {
        showTypeDialog();
    }

    @OnClick(R.id.choose_city) void chooseCity() {
        showCityDialog(city);
    }

    @OnClick(R.id.choose_bus_card) void chooseBusCard() {
        Intent intent = new Intent();
        intent.putExtra("photourl", packageData().toString());
        mActivity.startFragment(BackActivity.class, BusCardPhotoFragment.class, intent);
    }

    @OnClick(R.id.ok_btn) void modifyProfile() {
        if (verify()) {
            submitDataToServer(collectInfofmation());
        }
    }

    private void doPickPhotoAction(int requestCode, String fileName) {
        Intent intent = new Intent(getActivity(), PickPhotoActivity.class);
        intent.putExtra("title", "获取图片");
        intent.putExtra("crop", true);
        intent.putExtra("option", PhotoSelectOptions.DEFALUT);
        intent.putExtra(PickPhotoActivity.FILE_NAME, fileName);
        startActivityForResult(intent, requestCode);
    }

    private void showCityDialog(final TextView textView) {
        if (dialog == null) {
            dialog = new CitySelectDialog(mActivity);
            dialog.setOnSelectCityListener(new CitySelectDialog.selectCityListener() {
                @Override
                public void onSelect(String province, String city) {
                    textView.setText(city);
                }
            });
        }
        dialog.show();
    }

    private void showTypeDialog() {
        final Calendar c;
        if(type.getTag()==null){
            c=Calendar.getInstance();
            type.setTag(c);
        }else{
            c=(Calendar)type.getTag();
        }
        DatePickerDialog datePickerDialog= new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year,monthOfYear,dayOfMonth);
                type.setText(DateUtils.getTyyyyMMdd(c.getTimeInMillis()));
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        String[] cartype = {"普通型", "舒适型", "豪华型", "取消"};
//        builder.setItems(cartype, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        type.setText("普通型");
//                        type.setTag("1");
//                        break;
//                    case 1:
//                        type.setText("舒适型");
//                        type.setTag("2");
//                        break;
//                    case 2:
//                        type.setText("豪华型");
//                        type.setTag("3");
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        builder.create().show();
    }

    private void displayData(boolean isUpdateImg) {
        busNumber.setText(response.getCarnumber());
        brand.setText(response.getBrand());
        if(response.getCarbuydate()!=0){
            type.setText(DateUtils.getTyyyyMMdd(response.getCarbuydate()));
            Calendar c=Calendar.getInstance();
            c.setTime(new Date(response.getCarbuydate()));
            type.setTag(c);
        }else{
            type.setHint("请补充车辆购买日期");
        }
        city.setText(response.getCity());
        seatNumber.setText(response.getSeat() + "");

        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(response.getInsuranceimg())) {
            sb.append("保险证");
        }
        if (!TextUtils.isEmpty(response.getRoadphoto())) {
            if (sb.length() != 0) {
                sb.append("、");
            }
            sb.append("行驶证");
        }
        if (!TextUtils.isEmpty(response.getTransportcertificate())) {
            if (sb.length() != 0) {
                sb.append("、");
            }
            sb.append("经营许可/租赁证");
        }
        photo.setText(sb.toString());
        if (isUpdateImg) {
            Picasso.with(mActivity)
                    .load(Net.IMGURL + response.getCarimg())
                    .into(busPhoto);
        }
    }

    private void enableEdit() {
        edit.setTitle("取消");
//        busLl.setEnabled(true);
//        cardLl.setEnabled(true);
//        busNumber.setEnabled(true);
//        brand.setEnabled(true);
//        type.setEnabled(true);
//        city.setEnabled(true);
//        seatNumber.setEnabled(true);
//        photo.setEnabled(true);
//        okBtn.setEnabled(true);
        editEnable = true;
        ButterKnife.apply(group, ENABLED, true);
    }

    private void disableEdit() {
        edit.setTitle("编辑");
//        busLl.setEnabled(false);
//        cardLl.setEnabled(false);
//        busNumber.setEnabled(false);
//        brand.setEnabled(false);
//        type.setEnabled(false);
//        city.setEnabled(false);
//        seatNumber.setEnabled(false);
//        photo.setEnabled(false);
//        okBtn.setEnabled(false);
        editEnable = false;
        ButterKnife.apply(group, ENABLED, false);
    }

    private void getData() {
        RequestParams params = RequestManager.simplePostParams();
        RequestManager.viewCar(params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                response.parse(responseInfo.result);
                if (response.isStatus()) {
                    displayData(true);
                } else {
                    mActivity.app.toast(response.getMsg());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.app.toast("您的网络不给力");
            }
        });
    }

    private boolean verify() {
        if (TextUtils.isEmpty(busNumber.getText().toString())) {
            mActivity.app.toast("车牌号不正确");
            return false;
        }
        if (busNumber.getText().length() != 7) {
            mActivity.app.toast("车牌号不正确");
            return false;
        }
        if (TextUtils.isEmpty(seatNumber.getText().toString())) {
            mActivity.app.toast("座位数不能为空");
            return false;
        }
        if (TextUtils.equals("0", seatNumber.getText().toString())) {
            mActivity.app.toast("座位数不能为0");
            return false;
        }
        return true;
    }

    private void submitDataToServer(RequestParams params) {
        RequestManager.updateCar(params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    mActivity.app.toast("更新成功");
                    edit.setTitle("编辑");
                    disableEdit();
                    //editEnable = !editEnable;
                } else {
                    mActivity.app.toast(XString.getStr(data, JsonName.MSG));
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                mActivity.app.toast("很抱歉，更新失败");
            }
        });
    }

    private RequestParams collectInfofmation() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("carnumber", busNumber.getText().toString());
        params.addBodyParameter("city", city.getText().toString());
        params.addBodyParameter("brand", brand.getText().toString());
        params.addBodyParameter("seat", seatNumber.getText().toString());
        params.addBodyParameter("carbuydate", type.getText().toString());
        return params;
    }

    private JSONObject packageData() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("bxz", response.getInsuranceimg());
            jo.put("xsz", response.getRoadphoto());
            jo.put("jyxkz", response.getTransportcertificate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RotateAnimation animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);
        switch (resultCode) {
            case 1: // PickPhotoActivity返回
                String result = data.getStringExtra(PickPhotoActivity.PHOTO_PATH);
                LogUtils.d("path:" + result);
                if (!TextUtils.isEmpty(result)) {
                    Bitmap bitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                    RequestParams params = new RequestParams();
                    params.addBodyParameter("key", UserHelper.getInstance().getCid());
                    params.addBodyParameter("isupdate", "1"); // 更新照片
                    params.addBodyParameter("avatar", new File(result));
                    switch (requestCode) {
                        case UserConstant.BUS_REQUEST_CODE:
                            busPhoto.setImageBitmap(MyBitmapUtils.createCirclImage(bitmap, bitmap.getWidth()<bitmap.getHeight()?bitmap.getWidth():bitmap.getHeight()));
                            busPhoto.setTag("upload");
                            busLoading.setVisibility(View.VISIBLE);
                            busLoading.setAnimation(animation);
                            ImageUploadCallBack busCallBack = new ImageUploadCallBack(busPhoto, busLoading);
                            params.addBodyParameter("imgtype", "3");
                            RequestManager.imageUpload(params, busCallBack);
                            break;
                    }
                }
                break;
            case 3:
                String mode = data.getStringExtra("mode");
                if (!TextUtils.isEmpty(mode)) {
                    brand.setText(mode);
                }
                break;
        }
    }

    class ImageUploadCallBack extends RequestCallBack<String> {
        ImageView image;
        ImageView loading;

        public ImageUploadCallBack(ImageView image, ImageView loading) {
            this.image = image;
            this.loading = loading;
        }

        @Override
        public void onSuccess(ResponseInfo responseInfo) {
            mActivity.app.toast("图片上传成功");
            image.setTag("success");
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.app.toast("图片上传失败，请重新选择照片");
            image.setTag("failure");
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
