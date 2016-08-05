package com.buslink.busjie.driver.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.buslink.busjie.driver.R;
import com.buslink.busjie.driver.activity.PickPhotoActivity;
import com.buslink.busjie.driver.constant.Net;
import com.buslink.busjie.driver.constant.UserConstant;
import com.buslink.busjie.driver.db.UserHelper;
import com.buslink.busjie.driver.manager.RequestManager;
import com.buslink.busjie.driver.util.CameraUtil;
import com.buslink.busjie.driver.util.MyBitmapUtils;
import com.buslink.busjie.driver.util.PhotoSelectOptions;
import com.buslink.busjie.driver.util.ToastHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/19.
 * 车辆证件照片
 */
public class BusCardPhotoFragment extends LevelTwoFragment {

    @Bind(R.id.bxz)
    ImageView bxz;
    @Bind(R.id.bxz_loading)
    ImageView bxzLoading;
    @Bind(R.id.xsz)
    ImageView xsz;
    @Bind(R.id.xsz_loading)
    ImageView xszLoading;
    @Bind(R.id.jyxkz)
    ImageView jyxkz;
    @Bind(R.id.jyxkz_loading)
    ImageView jyxkzLoading;

    @Override
    public String getTitle() {
        return "证件照片";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_bus_card;
    }

    @OnClick(R.id.bxz) void chooseBxz() {
        doPickPhotoAction(UserConstant.INSURANCE_REQUEST_CODE, UserConstant.INSURANCE_IMAGE_NAME);
    }

    @OnClick(R.id.xsz) void chooseXsz() {
        doPickPhotoAction(UserConstant.XSZ_REQUEST_CODE, UserConstant.XSZ_NAME);
    }

    @OnClick(R.id.jyxkz) void chooseJyxkz() {
        doPickPhotoAction(UserConstant.JYXKZ_CODE, UserConstant.JYXKZ_NAME);
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
                        case UserConstant.INSURANCE_REQUEST_CODE: // 保险证
                            bxz.setImageBitmap(bitmap);
                            bxz.setTag("upload");
                            bxzLoading.setVisibility(View.VISIBLE);
                            bxzLoading.setAnimation(animation);
                            ImageUploadCallBack insuranceCallBack = new ImageUploadCallBack(bxz, bxzLoading);
                            params.addBodyParameter("imgtype", "7");
                            RequestManager.imageUpload(params, insuranceCallBack);
                            break;
                        case  UserConstant.XSZ_REQUEST_CODE: // 行驶证
                            xsz.setImageBitmap(bitmap);
                            xsz.setTag("upload");
                            xszLoading.setVisibility(View.VISIBLE);
                            xszLoading.setAnimation(animation);
                            ImageUploadCallBack routetransportCallBack = new ImageUploadCallBack(xsz, xszLoading);
                            params.addBodyParameter("imgtype", "8");
                            RequestManager.imageUpload(params, routetransportCallBack);
                            break;
                        case UserConstant.JYXKZ_CODE: // 经营许可证
                            jyxkz.setImageBitmap(MyBitmapUtils.createCirclImage(bitmap, bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight()));
                            jyxkz.setTag("upload");
                            jyxkzLoading.setVisibility(View.VISIBLE);
                            jyxkzLoading.setAnimation(animation);
                            ImageUploadCallBack busCallBack = new ImageUploadCallBack(jyxkz, jyxkzLoading);
                            params.addBodyParameter("imgtype", "11");
                            RequestManager.imageUpload(params, busCallBack);
                            break;
                    }
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
            if (image.equals(bxz)) {
                mActivity.app.toast("审核中");
            }
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
    protected void initView() {
        String photourl = mActivity.getIntent().getStringExtra("photourl");
        JSONObject jo = XString.getJSONObject(photourl);
        setImg(bxz, XString.getStr(jo, "bxz"));
        setImg(xsz, XString.getStr(jo, "xsz"));
        setImg(jyxkz, XString.getStr(jo, "jyxkz"));
    }

    private void setImg(ImageView imageView, String url) {
        Picasso.with(mActivity)
                .load(Net.IMGURL + url)
                .into(imageView);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
