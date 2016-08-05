package com.buslink.busjie.driver.fragment;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.PickPhotoActivity;
import com.buslink.busjie.driver.base.BackActivity;
import com.buslink.busjie.driver.constant.JsonName;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.UserConstant;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.manager.MyApplication;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.response.ViewDriverResponse;
import com.buslink.busjie.driver.util.CameraUtil;
import com.buslink.busjie.driver.util.CircleTransform;
import com.buslink.busjie.driver.util.MyBitmapUtils;
import com.buslink.busjie.driver.util.MyHelper;
import com.buslink.busjie.driver.util.PhotoSelectOptions;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UserInfoFragment extends LevelTwoFragment {

    boolean editEnable;

    @ViewInject(R.id.head)
    ImageView mHeadView;
    @ViewInject(R.id.head_loading)
    ImageView headLoading;
    @ViewInject(R.id.ordersum)
    TextView ordersum;
    @ViewInject(R.id.sum)
    TextView sum;
    @ViewInject(R.id.ranking)
    TextView ranking;
    @ViewInject(R.id.sex)
    TextView mSexText;
    @ViewInject(R.id.photos)
    TextView photos;
    @ViewInject(R.id.small_name)
    EditText smallName;
    @ViewInject(R.id.second_phone)
    EditText secondPhone;
    @ViewInject(R.id.company)
    EditText company;
    @ViewInject(R.id.ok_btn)
    Button okBtn;
    @ViewInject(R.id.ratingbar)
    RatingBar ratingBar;

    ViewDriverResponse response = new ViewDriverResponse();
    JSONObject infoFromHome;
    MenuItem edit;

    @OnClick({R.id.head, R.id.sex, R.id.photos, R.id.ok_btn})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                doPickPhotoAction(UserConstant.HEAD_REQUEST_CODE, UserConstant.HEAD_IMAGE_NAME);
                break;
            case R.id.sex:
                showSexDialog();
                break;
            case R.id.photos:
                Intent intent = new Intent();
                intent.putExtra("photourl", packageData().toString());
                mActivity.startFragment(BackActivity.class, UserCardPhotoFragment.class, intent);
                break;
            case R.id.ok_btn:
                if (verify()) {
                    sendToServer(collectInfomation());
                }
                break;
        }
    }

    @Override
    public String getTitle() {
        return "司机信息";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_user_info;
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
        ViewUtils.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
        String s = mActivity.getIntent().getStringExtra(MyHelper.homeToDrawerToUserInfo);
        infoFromHome = XString.getJSONObject(s);
    }

    private void displayData(boolean isUpdateImg) {
        ordersum.setText(XString.getStr(infoFromHome, "ordersum"));
        sum.setText(XString.getStr(infoFromHome, "sum"));
        ranking.setText(XString.getStr(infoFromHome, "ranking"));
        if (1 == response.getGender()) {
            mSexText.setText("男");
            mSexText.setTag("1");
        } else if (2 == response.getGender()) {
            mSexText.setText("女");
            mSexText.setTag("2");
        }
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(response.getCardimg())) {
            sb.append("身份证");
        }
        if (!TextUtils.isEmpty(response.getDriverphoto())) {
            if (sb.length() != 0) {
                sb.append("、");
            }
            sb.append("驾驶证");
        }
        if (!TextUtils.isEmpty(response.getWorkphoto())) {
            if (sb.length() != 0) {
                sb.append("、");
            }
            sb.append("从业资格证");
        }
        photos.setText(sb.toString());
        smallName.setText(response.getNickname());
        secondPhone.setText(response.getReservephone());
        company.setText(response.getCompany());
        ratingBar.setRating(XString.getInt(infoFromHome, "star"));
        if (isUpdateImg) {
            Picasso.with(mActivity)
                    .load(Net.IMGURL + response.getDimg())
                    .error(R.mipmap.home_wo_default)
                    .transform(new CircleTransform())
                    .into(mHeadView);
        }

    }

    private void getData() {
        RequestParams params = RequestManager.simplePostParams();
        RequestManager.viewDriver(params, new RequestCallBack<String>() {
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

    private void enableEdit() {
        edit.setTitle("取消");
        smallName.setEnabled(true);
        secondPhone.setEnabled(true);
        company.setEnabled(true);
        mSexText.setEnabled(true);
        photos.setEnabled(true);
        okBtn.setEnabled(true);
        editEnable = true;
    }

    private void disableEdit() {
        edit.setTitle("编辑");
        smallName.setEnabled(false);
        secondPhone.setEnabled(false);
        company.setEnabled(false);
        mSexText.setEnabled(false);
        photos.setEnabled(false);
        okBtn.setEnabled(false);
        editEnable = false;
    }

    private boolean verify() {
        if (TextUtils.isEmpty(smallName.getText().toString())) {
            mActivity.app.toast("姓名不能为空");
            return false;
        }
        return true;
    }

    private RequestParams collectInfomation() {
        RequestParams params = RequestManager.simplePostParams();
        params.addBodyParameter("nickname", smallName.getText().toString());
        params.addBodyParameter("gender", (String) mSexText.getTag());
        if (TextUtils.isEmpty(company.getText().toString())) {
            params.addBodyParameter("company", "个人");
        } else {
            params.addBodyParameter("company", company.getText().toString());
        }
        params.addBodyParameter("reservephone", secondPhone.getText().toString());
        params.addBodyParameter("bdappid", UserHelper.getInstance().getBdappid());
        params.addBodyParameter("bduserid", UserHelper.getInstance().getBduserid());
        params.addBodyParameter("bdchannelid", UserHelper.getInstance().getBdchannelid());
        return params;
    }

    private void sendToServer(RequestParams params) {
        String s=secondPhone.getText().toString();
        if(s.length()!=11){
            mActivity.app.toast("你输入的备用号码不正确");
            return;
        }
        RequestManager.updateDriver(params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                JSONObject jo = XString.getJSONObject(responseInfo.result);
                JSONObject data = XString.getJSONObject(jo, JsonName.DATA);
                if (XString.getBoolean(jo, JsonName.STATUS)) {
                    mActivity.app.toast("更新成功");
                    edit.setTitle("编辑");
                    disableEdit();
                   // editEnable = !editEnable;
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

    private void showSexDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] sex = {"男", "女", "取消"};
        builder.setItems(sex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mSexText.setText("男");
                        mSexText.setTag("1");
                        break;
                    case 1:
                        mSexText.setText("女");
                        mSexText.setTag("2");
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void doPickPhotoAction(int requestCode, String fileName) {
        Intent intent = new Intent(getActivity(), PickPhotoActivity.class);
        intent.putExtra("title", "获取图片");
        intent.putExtra("crop", true);
        intent.putExtra("option", PhotoSelectOptions.DEFALUT);
        intent.putExtra(PickPhotoActivity.FILE_NAME, fileName);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RotateAnimation animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);
        if (resultCode == 1) {
            String result = data.getStringExtra(PickPhotoActivity.PHOTO_PATH);
            if (!TextUtils.isEmpty(result)) {
                Bitmap bitmap = CameraUtil.getCompressBitmap(result, CameraUtil.ICON_SIZE, CameraUtil.ICON_SIZE);
                RequestParams params = new RequestParams();
                params.addBodyParameter("key", UserHelper.getInstance().getUid());
                params.addBodyParameter("isupdate", "1"); // 更新照片
                params.addBodyParameter("avatar", new File(result));
                switch (requestCode) {
                    case UserConstant.HEAD_REQUEST_CODE:
                        mHeadView.setImageBitmap(MyBitmapUtils.createCirclImage(bitmap, bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight()));
                        mHeadView.setTag("upload");
                        headLoading.setVisibility(View.VISIBLE);
                        headLoading.setAnimation(animation);
                        ImageUploadCallBack headCallBack = new ImageUploadCallBack(mHeadView, headLoading);
                        params.addBodyParameter("imgtype", "2");
                        RequestManager.imageUpload(params, headCallBack);
                        break;
                }
            }
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
            getData();
        }

        @Override
        public void onFailure(HttpException e, String s) {
            mActivity.app.toast("图片上传失败，请重新选择照片");
            image.setTag("failure");
            loading.clearAnimation();
            loading.setVisibility(View.GONE);
        }
    }

    private JSONObject packageData() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("sfz", response.getCardimg());
            jo.put("jsz", response.getDriverphoto());
            jo.put("cyzgz", response.getWorkphoto());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
}
