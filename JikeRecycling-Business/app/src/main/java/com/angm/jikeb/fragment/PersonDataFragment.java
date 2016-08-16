package com.angm.jikeb.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.angm.jikeb.R;
import com.angm.jikeb.activity.PickPhotoActivity;
import com.angm.jikeb.constant.UserConstant;
import com.angm.jikeb.util.PhotoSelectOptions;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/10.
 * 个人资料界面
 */
public class PersonDataFragment extends LevelTwoFragment {


    @Bind(R.id.fragment_person_data_user_image)
    ImageView mHeadView;
    @Bind(R.id.head_loading)
    ImageView headLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ButterKnife.bind(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String getTitle() {
        return "个人资料";
    }

    @Override
    protected int getResLayout() {
        return R.layout.fragment_person_data;
    }

    @Override
    protected void initView() {
        Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/buslinkdriver/photo/head.jpg");

        mHeadView.setImageBitmap(bitmap);

        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPickPhotoAction(UserConstant.HEAD_REQUEST_CODE, UserConstant.HEAD_IMAGE_NAME);
            }
        });

    }

    private void doPickPhotoAction(int requestCode, String fileName) {
        Intent intent = new Intent(getActivity(), PickPhotoActivity.class);
        intent.putExtra("title", "获取图片");
        intent.putExtra("crop", true);
        intent.putExtra("option", PhotoSelectOptions.DEFALUT);
        intent.putExtra(PickPhotoActivity.FILE_NAME, fileName);
        startActivityForResult(intent, requestCode);
    }
//    /storage/emulated/0/buslinkdriver/photo/head.jpg
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(2000);

        if (resultCode == 1) {
            String result = data.getStringExtra(PickPhotoActivity.PHOTO_PATH);
            Log.i(" ", "result  : " + result);
            if (!TextUtils.isEmpty(result)) {
                Bitmap bitmap = BitmapFactory.decodeFile(result);
                mHeadView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
