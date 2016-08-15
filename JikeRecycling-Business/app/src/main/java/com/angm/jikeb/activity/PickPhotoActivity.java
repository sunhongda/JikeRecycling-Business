package com.angm.jikeb.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angm.jikeb.R;
import com.angm.jikeb.manager.MyApplication;
import com.angm.jikeb.util.CameraUtil;
import com.angm.jikeb.util.FileUtil;
import com.angm.jikeb.util.PhotoSelectOptions;
import com.angm.jikeb.util.ResultType;
import com.angm.jikeb.util.ToastHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PickPhotoActivity extends Activity implements
        OnClickListener {

    public final static String Model = android.os.Build.MODEL;
    public final static String Device = android.os.Build.DEVICE;
    public final static String Manufacture = android.os.Build.MANUFACTURER;

    public static final String MOTOROLA_MODEL = "MB860";
    public static final String MOTOROLA_DEVICE = "olympus";
    public static final String MOTOROLA_MANUFACTURE = "motorola";
    public static final String OUT_FILE_DIR = FileUtil.getExternalStroragePath(MyApplication.getContext()) + "/buslinkdriver/photo/";
    public static final String FILE_NAME = "filename";

    public static final String PHOTO_PATH = "photopath";

    /**
     * 拍照后生成的照片，在/DCIM/Camera文件夹下，文件名为IMG_yyyyMMdd_HHmmss.jpg
     */
    private File mCurrentPhotoFile;
    private String title;
    private boolean isCrop = true;// 是否需要裁减
    private final int outputX = CameraUtil.ICON_SIZE;
    private final int outputY = CameraUtil.ICON_SIZE;

    private boolean isCustomCrop = false;
    private int customCropWidth = outputX;
    private int customCropHeight = outputY;

    private String resultImagePath;

    /**
     * 裁剪后的图片路径, 在/buslinkdriver/photo文件夹下，
     */
    private String croppedImagePath;

    public static final String OUT_FILE_NAME = "outfile";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            resultImagePath = null;
            Intent intent = new Intent();
            intent.putExtra(PHOTO_PATH, "");
            setResult(ResultType.OK.ordinal(), intent);
            finish();
            return;
        }

        switch (requestCode) {
            case CameraUtil.PHOTO_PICKED_WITH_DATA: {// 从相册返回
                try {
                    Uri uri = data.getData();
                    if (uri != null) {
                        String imagePath = CameraUtil.getImagePath(this, uri);
                        if (isCrop) {
                            doCropPhoto(uri);
                        } else {
                            resultImagePath = imagePath;
                            Intent intent = new Intent();
                            intent.putExtra(PHOTO_PATH, resultImagePath);
                            setResult(ResultType.OK.ordinal(), intent);
                            finish();
                        }

                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            // 这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片
                            Bitmap image = extras.getParcelable("data");
                            if (image != null) {
                                String imagePath = saveMyBitmap(image);
                                if (isCrop) {
                                    doCropPhoto(new File(imagePath));
                                } else {
                                    resultImagePath = imagePath;
                                    Intent intent = new Intent();
                                    intent.putExtra(PHOTO_PATH, resultImagePath);
                                    setResult(ResultType.OK.ordinal(), intent);
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra(PHOTO_PATH, "");
                                setResult(ResultType.OK.ordinal(), intent);
                                resultImagePath = null;
                                finish();
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    ToastHelper.showToast(getResources().getString(R.string.gallay_error));
                }
                break;
            }
            case CameraUtil.CAMERA_WITH_DATA: {// 从相机返回
                if (isCrop) {
                    doCropPhoto(mCurrentPhotoFile);
                } else {
                    resultImagePath = mCurrentPhotoFile.getAbsolutePath();
                    Intent intent = new Intent();
                    intent.putExtra(PHOTO_PATH, resultImagePath);
                    setResult(ResultType.OK.ordinal(), intent);
                    finish();
                }
                break;
            }
            case CameraUtil.CAMERA_COMPLETE:// 从裁剪返回
                Bitmap image = null;
                if (isMotorlaMobile()) {
                    image = data.getParcelableExtra("data");
                    resultImagePath = saveMyBitmap(image);
                    Intent intent = new Intent();
                    intent.putExtra(PHOTO_PATH, resultImagePath);
                    setResult(ResultType.OK.ordinal(), intent);
                    finish();
                } else {
                    resultImagePath = croppedImagePath;
                    Intent intent = new Intent();
                    intent.putExtra(PHOTO_PATH, resultImagePath);
                    setResult(ResultType.OK.ordinal(), intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.BottomInFullScreenDialog);
        setContentView(R.layout.pick_photo_activity);
        if (savedInstanceState != null && savedInstanceState.containsKey(OUT_FILE_NAME)) {
            mCurrentPhotoFile = new File(savedInstanceState.getString(OUT_FILE_NAME));
        }

        final Intent intent = getIntent();
        title = intent.getStringExtra("title");
        isCrop = intent.getBooleanExtra("crop", true);
        isCustomCrop = intent.getBooleanExtra("crop", true);
        croppedImagePath = OUT_FILE_DIR + intent.getStringExtra(FILE_NAME);
        PhotoSelectOptions options = (PhotoSelectOptions) intent.getSerializableExtra("option");

        switch (options) {
            case TAKE_PHOTO_BY_CAMERA:
                String status = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(status)) {// 判断是否有SD卡
                    doTakePhoto();// 用户点击了从照相机获取
                } else {
                    ToastHelper.showToast(getResources().getString(
                            R.string.publish_sd_notexist));
                    resultImagePath = null;
                    finish();
                }
                break;
            case SELECT_PHOTO_FROM_GALLARY:
                doPickPhotoFromGallery();// 从相册中去获取
                break;
            case DEFALUT:

                break;
        }
        init(options);

        // 异常捕获测试
//        String s = null;
//        s.equals("");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoFile != null) {
            outState.putString(OUT_FILE_NAME, mCurrentPhotoFile.getAbsolutePath());
        }
        super.onSaveInstanceState(outState);
    }

    private void init(PhotoSelectOptions options) {
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.layout_root);
        TextView tvTitle = ((TextView) findViewById(R.id.photo_dialog));
        ImageButton takePhotoBtn = (ImageButton) findViewById(R.id.take_photo);
        ImageButton selectPhotoBtn = (ImageButton) findViewById(R.id.from_files);
        Button cancelBtn = (Button) findViewById(R.id.photo_cancle);
        if (PhotoSelectOptions.DEFALUT == options) {
            tvTitle.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            selectPhotoBtn.setVisibility(View.VISIBLE);
            takePhotoBtn.setVisibility(View.VISIBLE);
            rootLayout.setVisibility(View.VISIBLE);
            takePhotoBtn.setOnClickListener(this);
            selectPhotoBtn.setOnClickListener(this);
            cancelBtn.setOnClickListener(this);
            if (title != null) {
                tvTitle.setText(title);
            }
            getWindow().setGravity(Gravity.BOTTOM);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
            cancelBtn.setVisibility(View.INVISIBLE);
            selectPhotoBtn.setVisibility(View.INVISIBLE);
            takePhotoBtn.setVisibility(View.INVISIBLE);
            rootLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.take_photo) {
            String status = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(status)) {// 判断是否有SD卡
                doTakePhoto();// 用户点击了从照相机获取
            } else {
                ToastHelper.showToast(getResources().getString(
                        R.string.publish_sd_notexist));
                Intent intent = new Intent();
                intent.putExtra(PHOTO_PATH, "");
                setResult(ResultType.OK.ordinal(), intent);
                finish();
            }

        } else if (i == R.id.from_files) {
            doPickPhotoFromGallery();// 从相册中去获取
        } else if (i == R.id.photo_cancle) {
            finish();
        }
    }

    /**
     * 打开相机拍照
     */
    public void doTakePhoto() {
        try {
            if (!CameraUtil.PHOTO_DIR.exists()) {
                CameraUtil.PHOTO_DIR.mkdirs();
            }
            mCurrentPhotoFile = new File(CameraUtil.PHOTO_DIR,
                    CameraUtil.getPhotoFileName());
            Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CameraUtil.CAMERA_WITH_DATA);

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent();
            intent.putExtra(PHOTO_PATH, "");
            setResult(ResultType.OK.ordinal(), intent);
            finish();
        }
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 拍照意图
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)); // 拍照结束后的照片保存在mCurrentPhotoFile路径
        return intent;
    }

    public void doPickPhotoFromGallery() {
        try {
            Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, CameraUtil.PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Intent intent = new Intent();
            intent.putExtra(PHOTO_PATH, "");
            setResult(ResultType.OK.ordinal(), intent);
        }
    }

    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); // 从相册选择图片意图
        intent.setType("image/*");
        return intent;
    }

    public void doCropPhoto(Uri uri) {
        try {
            final Intent intent = getCropImageIntent(uri);
            startActivityForResult(intent, CameraUtil.CAMERA_COMPLETE);
        } catch (Exception e) {
            ToastHelper.showLongToast(this.getResources()
                    .getString(R.string.publish_fail));
        }
    }

    public void doCropPhoto(File filePath) {
        try {
            final Intent intent = getCropImageIntent(Uri.fromFile(filePath));
            startActivityForResult(intent, CameraUtil.CAMERA_COMPLETE);
        } catch (Exception e) {
            ToastHelper.showLongToast(this.getResources()
                    .getString(R.string.publish_fail));
        }
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     * @param photoNeedCropped 需要进行裁剪的照片
     */
    private Intent getCropImageIntent(Uri photoNeedCropped) {
        Intent intent = new Intent("com.android.camera.action.CROP"); // 裁剪图片意图
        intent.setDataAndType(photoNeedCropped, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        if (isCustomCrop) {
            intent.putExtra("outputX", customCropWidth);
            intent.putExtra("outputY", customCropHeight);
        } else {
            intent.putExtra("outputX", outputX);
            intent.putExtra("outputY", outputY);
        }
        intent.putExtra("scale", true);
        if (isMotorlaMobile()) {
            intent.putExtra("return-data", true);// 若为false则表示不返回数据
        } else {
            File croppedImageFile = new File(croppedImagePath);
            try {
                if (croppedImageFile.exists()) {
                    croppedImageFile.delete();
                }
                croppedImageFile.getParentFile().mkdirs();
                croppedImageFile.createNewFile();
            } catch (IOException ex) {
                // Log.e("io", ex.getMessage());
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedImageFile)); // 裁剪后的图片保存在croppedImagePath路径
            intent.putExtra("return-data", false);// 若为false则表示不返回数据
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", false);
        }

        return intent;
    }

    public String saveMyBitmap(Bitmap mBitmap) {
        File f;
        FileOutputStream fOut = null;
        String imagePath = "";
        try {
            if (!CameraUtil.CROP_FILE_DIR.exists()) {
                CameraUtil.CROP_FILE_DIR.mkdirs();
            }
            f = new File(CameraUtil.CROP_FILE_DIR,
                    CameraUtil.getPhotoFileName());
            f.createNewFile();
            imagePath = f.getAbsolutePath();
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            return imagePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imagePath;
    }

    public static boolean isMotorlaMobile() {
        if (MOTOROLA_MODEL.equals(Model) && MOTOROLA_DEVICE.equals(Device)
                && MOTOROLA_MANUFACTURE.equals(Manufacture)) {
            return true;
        }
        return false;
    }

    public String getImagePath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor =getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null, MediaStore.Images.Media._ID + " = ? ",
                        new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            resultImagePath = null;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultImagePath = null;
    }
}