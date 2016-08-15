package com.angm.jikeb.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    ImageView fragmentPersonDataUserImage;

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
        fragmentPersonDataUserImage.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
