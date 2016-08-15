package com.angm.jikeb.util;

/**
 * 这个工具类是关于通过相机或者是媒体库取图片的函数，但是有时候startactivityforResult()没有返回值。
 * 所以我把这些函数又放回到各个类里面了，这个类我也不删了。
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtil {

    public static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");
    public static final File CROP_FILE_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/CROP");
    public static final int ICON_SIZE = 400;
    public static final int ICON_WIDTH = 400;
    public static final int ICON_HEIGHT = 320;
    public File currentPhotoFile;// 照相机拍照得到的图片

    /* 处理图片功能 */
    public static final int CAMERA_WITH_DATA = 0x3023;
    public static final int CAMERA_COMPLETE = 0x3022;
    public static final int CAMERA_COMPLETE_FRAGMENT = 0x3024;
    public static final int PHOTO_PICKED_WITH_DATA = 0x3021;

    Activity mActivity;
    private static CameraUtil instance = null;

    private CameraUtil(Activity activity) {
        mActivity = activity;
    }

    public static CameraUtil getInstance(Activity activity) {
        if (instance == null)
            instance = new CameraUtil(activity);
        return instance;
    }

    /**
     * 拍照
     *
     * @param file
     * @return
     */
    public static Intent getTakePickIntent(File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return intent;
    }

    /**
     * 获得一个文件名，文件名为当前的时间
     *
     * @return
     */
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss", Locale.getDefault());
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 图片浏览器
     *
     * @return
     */
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setType("image/*");
        return intent;
    }

    /***
     * 获得压缩后的图片，大分辨率图片的话，这个方法比较耗时
     *
     * @param picPath  源图片路径
     * @param toWidth  期望图片的宽
     * @param toHeight 期望图片的高
     * @return
     */
    public static Bitmap getCompressBitmap(String picPath, int toWidth,
                                           int toHeight) {
        if ("".equals(picPath))
            return null;
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        Bitmap comBitmap;
        int srcWidth = op.outWidth;
        int srcHeight = op.outHeight;
        int wRadio = 1, hRadio = 1;

        if (srcWidth < srcHeight) {
            if (srcWidth > toWidth || srcHeight > toHeight) {
                wRadio = (int) Math.ceil((float) op.outWidth / toWidth);
                hRadio = (int) Math.ceil((float) op.outHeight / toHeight);
            }

        } else {
            if (srcWidth > toWidth || srcHeight > toHeight) {
                wRadio = (int) Math.ceil((float) op.outWidth / toHeight);
                hRadio = (int) Math.ceil((float) op.outHeight / toWidth);
            }

        }

        if (wRadio >= 1 && hRadio >= 1) {
            if (wRadio > hRadio) {
                op.inSampleSize = wRadio;
            } else {
                op.inSampleSize = hRadio;
            }
        }
        op.inJustDecodeBounds = false;
        comBitmap = BitmapFactory.decodeFile(picPath, op);
        return comBitmap;
    }

    /***
     * 保存一个图片到CameraUtil.CROP_FILE_DIR目录，并返回得到的路径
     *
     * @param mBitmap
     * @return
     */
    public static String saveMyBitmap(Bitmap mBitmap) {
        File file;
        FileOutputStream fOut = null;
        String imagePath = "";
        try {
            if (!CROP_FILE_DIR.exists()) {
                CROP_FILE_DIR.mkdirs();
            }
            file = new File(CROP_FILE_DIR, getPhotoFileName());
            file.createNewFile();
            imagePath = file.getAbsolutePath();
            fOut = new FileOutputStream(file);
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

    /**
     * 获取图像路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getImagePath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            return getImagePathKitKat(context, uri);
        } else {
            return getImagePathOld(context, uri);
        }
    }

    // 旧版本
    public static String getImagePathOld(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();
        cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ",
                new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    /**
     * 以下图片结局方案源自：http://blog.csdn.net/tempersitu/article/details/20557383
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImagePathKitKat(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
