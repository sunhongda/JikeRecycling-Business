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
import com.buslink.busjie.driver.util.PhotoSelectOptions;
import com.buslink.busjie.driver.util.ToastHelper;
import com.buslink.busjie.driver.util.XString;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/10/17.
 * 司机证件照片
 */
public class UserCardPhotoFragment extends LevelTwoFragment {

    @Bind(R.id.id_card)
    ImageView idCard;
    @Bind(R.id.idcard_loading)
    ImageView idcardLoading;
    @Bind(R.id.licence)
    ImageView licence;
    @Bind(R.id.licence_loading)
    ImageView licenceLoading;
    @Bind(R.id.jobcard)
    ImageView jobcard;
    @Bind(R.id.jop_card_loading)
    ImageView jopCardLoading;

    @OnClick(R.id.id_card) void setIdCard() {
        doPickPhotoAction(UserConstant.SFZ_CODE, UserConstant.SFZ_NAME);
    }

    @OnClick(R.id.licence) void setLicence() {
        doPickPhotoAction(UserConstant.LICENCE_REQUEST_CODE, UserConstant.LICENCE_IMAGE_NAME);
    }

    @OnClick(R.id.jobcard) void setJobcard() {
        doPickPhotoAction(UserConstant.JOBCARD_REQUEST_CODE, UserConstant.JOBCARD_IMAGE_NAME);
    }

    @Override
    public String getTitle() {
        return "证件照片";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_user_card;
    }

    @Override
    protected void initView() {
        String jsonString = mActivity.getIntent().getStringExtra("photourl");
        JSONObject jo = XString.getJSONObject(jsonString);
        setImg(idCard, XString.getStr(jo, "sfz"));
        setImg(licence, XString.getStr(jo, "jsz"));
        setImg(jobcard, XString.getStr(jo, "cyzgz"));
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
                    case UserConstant.LICENCE_REQUEST_CODE:
                        licence.setImageBitmap(bitmap);
                        licence.setTag("upload");
                        licenceLoading.setVisibility(View.VISIBLE);
                        licenceLoading.setAnimation(animation);
                        ImageUploadCallBack licenceCallBack = new ImageUploadCallBack(licence, licenceLoading);
                        params.addBodyParameter("imgtype", "10");
                        RequestManager.imageUpload(params, licenceCallBack);
                        break;
                    case UserConstant.JOBCARD_REQUEST_CODE:
                        jobcard.setImageBitmap(bitmap);
                        jobcard.setTag("upload");
                        jopCardLoading.setVisibility(View.VISIBLE);
                        jopCardLoading.setAnimation(animation);
                        ImageUploadCallBack jobcardCallBack = new ImageUploadCallBack(jobcard, jopCardLoading);
                        params.addBodyParameter("imgtype", "9");
                        RequestManager.imageUpload(params, jobcardCallBack);
                        break;
                    case UserConstant.SFZ_CODE:
                        idCard.setImageBitmap(bitmap);
                        idCard.setTag("upload");
                        idcardLoading.setVisibility(View.VISIBLE);
                        idcardLoading.setAnimation(animation);
                        ImageUploadCallBack idcardCallBack = new ImageUploadCallBack(idCard, idcardLoading);
                        params.addBodyParameter("imgtype", "4");
                        RequestManager.imageUpload(params, idcardCallBack);
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
            if (image.equals(licence)) {
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
}
