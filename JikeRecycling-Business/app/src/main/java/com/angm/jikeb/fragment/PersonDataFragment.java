package com.angm.jikeb.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.angm.jikeb.R;
import com.angm.jikeb.view.dialog.ActionSheet.OnOperItemClickL;
import com.angm.jikeb.view.dialog.ActionSheetDialog;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.Gao on 2016/8/10.
 * 个人资料界面
 */
public class PersonDataFragment extends LevelTwoFragment {

    //图片名称
    protected static final String IMAGE_FILE_NAME = "photo.png";

    protected static final int REQUESTCODE_PICK = 100;
    protected static final int REQUESTCODE_TAKE = 200;
    private static final int REQUESTCODE_CUTTING = 300;
    private String student;
    private Bitmap photo;
    //判断是否渠道信息，显示初始值
    private boolean hasHead = false;
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
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImg();
            }
        });

    }

    //选择照片
    private void selectImg() {
        final String[] stringItems = {"拍照", "从手机相册选择"};
        final ActionSheetDialog dialog = new ActionSheetDialog(getActivity(), stringItems, getActivity().findViewById(R.id.student_layout));
        dialog.isTitleShow(false).show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    // 拍照
                    case 0:
                        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 下面这句指定调用相机拍照后的照片存储的路径
                        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                                .fromFile(new File(Environment
                                        .getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                        startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                        break;
                    // 相册选择图片
                    case 1:
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                        // 如果朋友们要限制上传到服务器的图片类型时可以直接写如：image/jpeg 、 image/png等的类型
                        pickIntent
                                .setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                        startActivityForResult(pickIntent, REQUESTCODE_PICK);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
    }
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            // Drawable drawable = new BitmapDrawable(null, photo);
            mHeadView.setImageBitmap(photo);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                    String[] proj = {MediaStore.Images.Media.DATA};

                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getActivity().getContentResolver().query(data.getData(), proj, null, null, null);

                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    student = path;
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/" + IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                hasHead = true;
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    setPicToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
