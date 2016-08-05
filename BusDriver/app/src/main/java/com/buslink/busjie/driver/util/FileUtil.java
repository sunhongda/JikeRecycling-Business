package com.buslink.busjie.driver.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.buslink.busjie.driver.util.SdCardInfo.SDCardType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import cz.msebera.android.httpclient.util.EncodingUtils;

public class FileUtil {
    private static final String TAG = "CommonFileUtil";


    //  SD卡的info列表
    private static ArrayList<SdCardInfo> sdCardInfoList;


    public static ArrayList<SdCardInfo> getSDcardInfoList(Context context, boolean isRefersh) {
        if (sdCardInfoList != null) {
            if (isRefersh)
                sdCardInfoList = enumExternalSDcardInfo(context);
            return sdCardInfoList;
        } else
            sdCardInfoList = enumExternalSDcardInfo(context);
        return sdCardInfoList;

    }

    private static String getSDCardPath(Context context, boolean isExtendsSD, boolean isRefresh) {
        final List<SdCardInfo> list = getSDcardInfoList(context, isRefresh);
        for (int i = 0; i < list.size(); i++) {
            SdCardInfo info = list.get(i);
            if (info == null)
                continue;
            if (isExtendsSD) {
                if (info.isExternalCard == SDCardType.INNERCARD)
                    continue;
                if (info.isExternalCard == SDCardType.EXTERNALCARD)
                    return info.path;
            } else {
                if (info.isExternalCard == SDCardType.EXTERNALCARD)
                    continue;
                if (info.isExternalCard == SDCardType.INNERCARD)
                    return info.path;
            }
        }

        return null;
    }


    /**
     * 获取内部sdcard路径
     * 如果已获取过，则默认获取缓存的路径
     *
     * @param context
     * @return
     */
    public static String getInternalSDCardPath(Context context) {
        return getSDCardPath(context, false, false);
    }

    /**
     * 获取内部sdcard路径
     * 该方法会重新获取内部sdcard路径，不使用缓存
     *
     * @param context
     * @return
     */
    public static String getInternalRefreshSDCardPath(Context context) {
        return getSDCardPath(context, false, true);
    }

    /**
     * 获取外部sdcard路径
     * 如果已获取过，则默认获取缓存的路径
     *
     * @param context
     * @return
     */
    public static String getExternalSDCardPath(Context context) {
        return getSDCardPath(context, true, false);
    }

    /**
     * 获取外部sdcard路径
     * 该方法会重新获取外部sdcard路径，不使用缓存
     *
     * @param context
     * @return
     */
    public static String getExternalRefreshSDCardPath(Context context) {
        return getSDCardPath(context, true, true);
    }


    /**
     * 获取sd卡可用的存储空间
     * 返回long的数组
     * 下标0为剩余空间
     * 下标1为已用空间
     * 下标2为总空间
     *
     * @param sdPath
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long[] getSDCardSpaceArray(String sdPath) {
        long space[] = new long[3];
        try {
            StatFs stat = new StatFs(sdPath);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long totalCount = stat.getBlockCount();
            space[0] = (long) (availableBlocks * blockSize);
            space[1] = (long) ((totalCount - availableBlocks) * blockSize);
            space[2] = (long) (blockSize * totalCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return space;
    }

    /**
     * 获取data的大小
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getDataDirectorySize() {
        File localFile = Environment.getDataDirectory();
        long l1 = 0L;

        while (true) {
            if (localFile != null) {
                String str = localFile.getPath();
                StatFs localStatFs = new StatFs(str);
                long l2 = localStatFs.getBlockSize();
                l1 = localStatFs.getBlockCount() * l2;
                return l1;
            } else {
                return 0l;
            }
        }
    }

    public static void copy(Context context, String sourceFilename, File dest)
            throws Exception {
        dest.delete();
        InputStream ins = context.getAssets().open(sourceFilename);
        byte[] bytes = new byte[ins.available()];
        ins.read(bytes);
        ins.close();
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(bytes);
        fos.close();
    }


    public static String getInnerSDCardPath(Context context) {

        final ArrayList<SdCardInfo> list = enumExternalSDcardInfo(context);
        for (int i = 0; i < list.size(); i++) {
            SdCardInfo info = list.get(i);
            if (info == null)
                continue;
            if (info.isExternalCard == null)
                continue;
            if (info.isExternalCard == SDCardType.INNERCARD)
                return info.path;
        }
        return null;
    }

    public static String getExterSDCardPath(Context context) {

        final ArrayList<SdCardInfo> list = enumExternalSDcardInfo(context);
        for (int i = 0; i < list.size(); i++) {
            SdCardInfo info = list.get(i);
            if (info == null)
                continue;
            if (info.isExternalCard == null)
                continue;
            if (info.isExternalCard == SDCardType.EXTERNALCARD)
                return info.path;
        }
        return null;
    }


    public static boolean isFileLocked(String file) {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        FileLock lock = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            lock = channel.tryLock();
            if (lock != null) {
                return false;
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (lock != null) {
                    lock.release();
                }
                if (channel != null) {
                    channel.close();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {

            }

        }
        return true;
    }

    /**
     * 检测文件是否存在
     *
     * @param fileName
     * @return boolean
     */
    public static boolean isFileExists(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {// 2014-02-25 Kang, Leo add
            return new File(fileName).exists();
        } else {
            return false;
        }
    }

    /**
     * 获取文本文件内容
     *
     * @param fileName
     * @return string
     * @throws IOException
     */

    public static String getFileContents(String fileName) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        File fHandler = new File(fileName);
        FileInputStream inputStream = new FileInputStream(fHandler);
        while ((inputStream.read(bytes)) != -1) {
            arrayOutputStream.write(bytes, 0, bytes.length);
        }
        String content = new String(arrayOutputStream.toByteArray());
        inputStream.close();
        arrayOutputStream.close();
        return content.trim();
    }

    /**
     * 循环删除文件及文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0, len = files.length; i < len; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }


    // 我的页面点击离线下载的时候，判断存储卡是不是存在或者可写不，如果不则不可以进入下载页面，toast提示用户
    public static boolean iSHasSdcardPath(Context context) {

        ArrayList<SdCardInfo> m_total_storage = FileUtil
                .enumExternalSDcardInfo(context);
        boolean isHasSdcard = false;
        if (m_total_storage == null || m_total_storage.size() == 0) {
            isHasSdcard = false;
        } else if (m_total_storage != null && m_total_storage.size() > 0) {
            for (int i = 0; i < m_total_storage.size(); i++) {
                SdCardInfo sdCardInfo = m_total_storage.get(i);
                if (sdCardInfo.path != null) {
                    File file = new File(sdCardInfo.path);
                    if (file.exists() && file.isDirectory()) {
                        if (file.canWrite()) {
                            isHasSdcard |= true;
                        } else {
                            isHasSdcard |= false;
                        }
                    }
                }
            }
        }
        return isHasSdcard;

    }

    /**
     * 判断是否可以切卡。
     *
     * @return
     */
    public static String getCanWritePath(Context context) {
        ArrayList<SdCardInfo> m_total_storage = enumExternalSDcardInfo(context);
        String basePath = null;
        if (m_total_storage == null || m_total_storage.size() == 0) {
            basePath = null;
        } else if (m_total_storage != null && m_total_storage.size() > 0) {
            for (int i = 0; i < m_total_storage.size(); i++) {
                SdCardInfo sdCardInfo = m_total_storage.get(i);
                if (!TextUtils.isEmpty(sdCardInfo.path)) {
                    File file = new File(sdCardInfo.path);
                    if (file.isDirectory()) {
                        if (file.canWrite()) {
                            basePath = sdCardInfo.path;
                        }
                    }
                }
            }
        }
        return basePath;
    }

    /**
     * 获取当前使用中的离线数据下载路径
     *
     * @param context
     * @return
     */
    public static String getCurrentOfflineDataStorage(Context context) {
        String PATH_FLAG = "offline_data_storage";
        return context.getSharedPreferences("base_path", Context.MODE_PRIVATE)
                .getString(PATH_FLAG, "");
    }

    /**
     * 设置 当前离线数据存储路径(用户切卡保存 数据)
     *
     * @param context
     * @param path
     */
    public static void setCurrentOfflineDataStorage(Context context, String path) {
        String PATH_FLAG = "offline_data_storage";
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        sp.edit().putString(PATH_FLAG, path).commit();
    }

    /**
     * 设置 外置卡路径
     *
     * @param context
     * @param path
     */
    public static void setOfflineDataExternalSDCardStorage(Context context,
                                                           String path) {
        String PATH_FLAG = "offline_data_storage_sdcard";
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        sp.edit().putString(PATH_FLAG, path).commit();
    }

    /**
     * 获取外置卡路径
     *
     * @param context
     */
    public static String getOfflineDataExternalSDCardStorage(Context context) {
        String PATH_FLAG = "offline_data_storage_sdcard";
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        return sp.getString(PATH_FLAG, "");
    }

    /**
     * 获取离线下载的路径 优先内置SD卡 然后外置SD卡
     *
     * @param context
     * @return
     */
    public static String getOfflineDataStorage(Context context) {
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        String PATH_FLAG = "offline_data_storage";
        String offlinePath = sp.getString(PATH_FLAG, "");
        if (!TextUtils.isEmpty(offlinePath)) {
            File file = new File(offlinePath);
            if (file.isDirectory()) {
                if (file.canWrite()) {
                    createNoMediaFileIfNotExist(offlinePath);

                }
                return offlinePath;
            }
        }
        offlinePath = getOfflineInnerStroragePath(context);
        if (!TextUtils.isEmpty(offlinePath)) {
            File file = new File(offlinePath);
            if (file.isDirectory()) {
                if (file.canWrite()) {
                    createNoMediaFileIfNotExist(offlinePath);
                }
                sp.edit().putString(PATH_FLAG, offlinePath).commit();
                return offlinePath;
            }
        }
        return null;
    }

    /**
     * 判断是否可以切卡。
     *
     * @return
     */
    public static String canWritePath(Context context) {
        ArrayList<SdCardInfo> m_total_storage = enumExternalSDcardInfo(context);
        String basePath = null;
        if (m_total_storage == null || m_total_storage.size() == 0) {
            basePath = null;
        } else if (m_total_storage != null && m_total_storage.size() > 0) {
            for (int i = 0; i < m_total_storage.size(); i++) {
                SdCardInfo sdCardInfo = m_total_storage.get(i);
                if (!TextUtils.isEmpty(sdCardInfo.path)) {
                    File file = new File(sdCardInfo.path);
                    if (file.isDirectory()) {
                        if (file.canWrite()) {
                            basePath = sdCardInfo.path;
                            break;
                        }
                    }
                }
            }
        }
        return basePath;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getOfflineInnerStroragePath(Context context) {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {// 12 is HONEYCOMB_MR1
            try {

                StorageManager manager = (StorageManager) context
                        .getSystemService(Context.STORAGE_SERVICE);
                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod(
                        "getVolumeList", (Class<?>[])null);
                Method getVolumeState = StorageManager.class.getMethod(
                        "getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager, (Object[])null);
                String state = null;
                String path = null;
                Boolean isRemove = false;
                String sdPath = "";
                String innerPath = "";
                String sdState = "";
                String innerState = "";
                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath",
                            (Class<?>[])null);
                    Method isRemovable = volume.getClass().getMethod(
                            "isRemovable", (Class<?>[])null);
                    path = (String) getPath.invoke(volume, (Object[])null);

                    state = (String) getVolumeState.invoke(manager,
                            getPath.invoke(volume, (Object[])null));
                    isRemove = (Boolean) isRemovable.invoke(volume, (Object[])null);

//		    Logs.i(TAG, "path = " + path);
//		    Logs.i(TAG, "isRemove = " + isRemove);
//		    Logs.i(TAG, "state = " + state);
                    // 三星S5存储卡分区问题
                    if (!TextUtils.isEmpty(path)
                            && path.toLowerCase().contains("private")) {
                        continue;
                    }
                    if (isRemove) {
                        sdPath = path;
                        sdState = state;
                        // 如果sd卡路径存在
                        if (null != sdPath && null != sdState
                                && sdState.equals(Environment.MEDIA_MOUNTED)) {
                            setOfflineDataExternalSDCardStorage(context, sdPath);
                            if (currentapiVersion <= 18) {

                            } else {
                                try {
                                    File[] files = context
                                            .getExternalFilesDirs(null);
                                    if (files != null) {
                                        if (files.length > 1 && null != files[1])
                                            sdPath = files[1].getAbsolutePath();
                                        else
                                            sdPath = path;
                                    }
                                } catch (Exception ex) {
                                }
                            }
                        }
                    } else {
                        innerPath = path;
                        innerState = state;
                        if (!TextUtils.isEmpty(innerPath) && null != innerState
                                && innerState.equals(Environment.MEDIA_MOUNTED)) {
                            return innerPath;
                        }
                    }
                }

                // 如果内置卡不可用,检测外置SD卡
                if (!TextUtils.isEmpty(innerPath) && null != innerState
                        && innerState.equals(Environment.MEDIA_MOUNTED)) {
                    return innerPath;
                } else if (!TextUtils.isEmpty(sdPath) && null != sdState
                        && sdState.equals(Environment.MEDIA_MOUNTED)) {
                    setOfflineDataExternalSDCardStorage(context, sdPath);
                    return sdPath;
                }
                return null;
            } catch (Exception e) {
            }
        }

        {
            // 得到存储卡路径
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡
            // 或可存储空间是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
                return sdDir.toString();
            }

            return null;
        }
    }

    /**
     * 获取710之前版本离线数据存储路径 兼容版本用
     *
     * @param context
     * @return
     */
    public static String getMapBaseDBStorage(Context context) {
        int currentapiVersion = Build.VERSION.SDK_INT;
        String PATH_FLAG = "map_base_path";
        if (currentapiVersion > 18)
            PATH_FLAG = "map_base_path_v44";

        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        String map_base_path = sp.getString(PATH_FLAG, "");

        return map_base_path;
    }

    /**
     * 判断当前路径是否可写
     *
     * @param map_base_path
     * @return
     */
    public static boolean getPathIsCanWrite(String map_base_path) {
        if (map_base_path != null && map_base_path.length() > 2) {
            File file = new File(map_base_path);
            if (file.isDirectory()) {
                if (file.canWrite()) {
                    // createNoMediaFileIfNotExist(map_base_path);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 切卡的时候需要清楚本地数据 以便与下次init会重新初始化数据
     *
     * @param context
     * @param isClear
     */
    public static void setClearDataOperation(Context context, boolean isClear) {
        String KEY = "clear_local_data";
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY, isClear).commit();
    }

    /**
     * 切卡的时候需要清楚本地数据 以便与下次init会重新初始化数据
     *
     * @param context
     */
    public static boolean getIsClearDataOperation(Context context) {
        String KEY = "clear_local_data";
        SharedPreferences sp = context.getSharedPreferences("base_path",
                Context.MODE_PRIVATE);
        return sp.getBoolean(KEY, false);
    }

    /**
     * 获取外置SD路径
     *
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getExternalStroragePath(Context context) {

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {// 12 is HONEYCOMB_MR1
            try {
                StorageManager manager = (StorageManager) context
                        .getSystemService(Context.STORAGE_SERVICE);
                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod(
                        "getVolumeList", (Class<?>[])null);
                Method getVolumeState = StorageManager.class.getMethod(
                        "getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
                        (Object[])null);
                String state = null;
                String path = null;
                Boolean isRemove = false;
                String sdPath = "";
                String innerPath = "";
                String sdState = "";
                String innerState = "";
                String storageDir = null;
                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath",
                            (Class<?>[])null);
                    Method isRemovable = volume.getClass().getMethod(
                            "isRemovable", (Class<?>[])null);
                    path = (String) getPath.invoke(volume, (Object[])null);

                    state = (String) getVolumeState.invoke(manager,
                            getPath.invoke(volume, (Object[])null));
                    isRemove = (Boolean) isRemovable.invoke(volume, (Object[])null);

//		    Logs.i(TAG, "path = " + path);
//		    Logs.i(TAG, "isRemove = " + isRemove);
//		    Logs.i(TAG, "state = " + state);
                    // 三星S5存储卡分区问题
                    if (path.toLowerCase().contains("private")) {
                        continue;
                    }
                    if (isRemove) {
                        sdPath = path;
                        sdState = state;
                        // 如果sd卡路径存在
                        if (null != sdPath && null != sdState
                                && sdState.equals(Environment.MEDIA_MOUNTED)) {

                            if (currentapiVersion <= 18) {
                                storageDir = sdPath;
                            } else {
                                try {
                                    File[] files = context
                                            .getExternalFilesDirs(null);
                                    if (files != null) {
                                        if (files.length > 1 && null != files[1])
                                            storageDir = files[1]
                                                    .getAbsolutePath();
                                        else
                                            storageDir = path;
                                    }
                                } catch (Exception ex) {
                                    // 此处保护java.lang.NoSuchMethodError:
                                    // android.content.Context.getExternalFilesDirs
                                    storageDir = sdPath;
                                }
                            }
                            break;
                        }
                    } else {
                        innerPath = path;
                        innerState = state;
                    }
                }

                // 如果sd卡路径为null,检测内部存储空间
                if (currentapiVersion <= 18) {// 18 is JELLY_BEAN_MR2
                    if (null == storageDir && null != innerPath) {
                        if (null != innerState
                                && innerState.equals(Environment.MEDIA_MOUNTED)) {
                            storageDir = innerPath;
                        }
                    }
                    return storageDir;
                } else {// 4.4以上系统有限内部存储卡
                    if (null != innerPath) {
                        if (null != innerState
                                && innerState.equals(Environment.MEDIA_MOUNTED)) {
                            storageDir = innerPath;
                        }
                    }
                    return storageDir;
                }
            } catch (Exception e) {
            }
        }

        {
            // 得到存储卡路径
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡
            // 或可存储空间是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
                return sdDir.toString();
            }

            return null;
        }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String[] enumExternalStroragePath(Context context) {

        String[] enumResult = null;
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {
            try {
                StorageManager manager = (StorageManager) context
                        .getSystemService(Context.STORAGE_SERVICE);

                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod(
                        "getVolumeList", (Class<?>[])null);
                Method getVolumeState = StorageManager.class.getMethod(
                        "getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
                        (Object[])null);
                String state = null;
                String path = null;
                boolean isRemove = false;
                enumResult = new String[Volumes.length];
                int index = 0;
                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath",
                            (Class<?>[])null);
                    path = (String) getPath.invoke(volume, (Object[])null);
                    state = (String) getVolumeState.invoke(manager,
                            getPath.invoke(volume, (Object[])null));
                    Method isRemovable = volume.getClass().getMethod(
                            "isRemovable", (Class<?>[])null);
                    isRemove = (Boolean) isRemovable.invoke(volume, (Object[])null);
                    if (null != path && null != state
                            && state.equals(Environment.MEDIA_MOUNTED)) {

                        if (currentapiVersion > 18 && Volumes.length > 1
                                && isRemove == true) {
                            File[] files = context.getExternalFilesDirs(null);
                            if (files != null) {
                                if (files.length > 1 && null != files[1])
                                    enumResult[index++] = files[1]
                                            .getAbsolutePath();
                                else
                                    enumResult[index++] = path;
                            }
                        } else {
                            enumResult[index++] = path;
                        }
                    }

                }

                return enumResult;

            } catch (Exception e) {
            }
        }

        {
            // 得到存储卡路径
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡
            // 或可存储空间是否存在
            if (sdCardExist) {
                enumResult = new String[1];
                sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
                enumResult[0] = sdDir.toString();
                return enumResult;
            }

            return null;
        }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static ArrayList<SdCardInfo> enumExternalSDcardInfo(Context context) {

        sdCardInfoList = new ArrayList<SdCardInfo>();

        SdCardInfo[] enumResult = null;
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 12) {
            try {
                StorageManager manager = (StorageManager) context
                        .getSystemService(Context.STORAGE_SERVICE);

                /************** StorageManager的方法 ***********************/
                Method getVolumeList = StorageManager.class.getMethod(
                        "getVolumeList", (Class<?>[])null);
                Method getVolumeState = StorageManager.class.getMethod(
                        "getVolumeState", String.class);

                Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
                        (Object[])null);
                String state = null;
                String path = null;
                boolean isRemove = false;
                int index = 0;
                for (Object volume : Volumes) {
                    /************** StorageVolume的方法 ***********************/
                    Method getPath = volume.getClass().getMethod("getPath",
                            (Class<?>[])null);
                    path = (String) getPath.invoke(volume, (Object[])null);
                    state = (String) getVolumeState.invoke(manager,
                            getPath.invoke(volume, (Object[])null));
                    Method isRemovable = volume.getClass().getMethod(
                            "isRemovable", (Class<?>[])null);
                    isRemove = (Boolean) isRemovable.invoke(volume, (Object[])null);
                    if (null != path && null != state
                            && state.equals(Environment.MEDIA_MOUNTED)) {

                        if (currentapiVersion > 18 && Volumes.length > 1
                                && isRemove == true) {
                            File[] files = context.getExternalFilesDirs(null);
                            if (files != null) {
                                String sdPath = null;
                                if (files.length > 1 && null != files[1])
                                    sdPath = files[1].getAbsolutePath();
                                else
                                    sdPath = path;
                                SdCardInfo sdCard = new SdCardInfo(
                                        SDCardType.EXTERNALCARD, sdPath);

                                StatFs statFs = new StatFs(sdPath);
                                long blockSize = statFs.getBlockSize();
                                long totalBlocks = statFs.getBlockCount();
                                long availableBlocks = statFs.getAvailableBlocks();

                                sdCard.totalSize = Formatter.formatFileSize(context, totalBlocks * blockSize);
                                sdCard.availableSize = Formatter.formatFileSize(context, availableBlocks * blockSize);
                                sdCard.usedSize = Formatter.formatFileSize(context, (totalBlocks - availableBlocks) * blockSize);
                                sdCardInfoList.add(sdCard);
                            }
                            setOfflineDataExternalSDCardStorage(context, path);
                        } else {
                            SdCardInfo sdCard;
                            if (isRemove) {
                                sdCard = new SdCardInfo(
                                        SDCardType.EXTERNALCARD, path);
                                setOfflineDataExternalSDCardStorage(context,
                                        path);
                            } else {
                                sdCard = new SdCardInfo(
                                        SDCardType.INNERCARD, path);
                            }
                            StatFs statFs = new StatFs(path);
                            long blockSize = statFs.getBlockSize();
                            long totalBlocks = statFs.getBlockCount();
                            long availableBlocks = statFs.getAvailableBlocks();

                            sdCard.totalSize = Formatter.formatFileSize(context, totalBlocks * blockSize);
                            sdCard.availableSize = Formatter.formatFileSize(context, availableBlocks * blockSize);
                            sdCard.usedSize = Formatter.formatFileSize(context, (totalBlocks - availableBlocks) * blockSize);
                            sdCardInfoList.add(sdCard);
                        }
                    }

                }
            } catch (Exception e) {
            }
        } else {
            // 得到存储卡路径
            File sdDir = null;
            boolean sdCardExist = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED); // 判断sd卡
            // 或可存储空间是否存在
            if (sdCardExist) {
                sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
                SdCardInfo sdCard = new SdCardInfo(SDCardType.INNERCARD,
                        sdDir.toString());
                sdCardInfoList.add(sdCard);
            }
        }
        return sdCardInfoList;
    }

    /**
     * 将字符串保存到指定的文件
     *
     * @param file
     * @param data
     */
    public static void writeTextFile(File file, String data) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        WriteLock writelock = lock.writeLock();
        writelock.lock();
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                parent.mkdirs();
            }
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }
            file.createNewFile();
            byte[] bytes = data.getBytes();
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writelock.unlock();
        }
    }

    /**
     * 将字符数组保存到指定的文件
     *
     * @param file
     * @param data
     */
    public static void writeTextFile(File file, byte[] data) {

        try {
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }

            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void writeDataToFile(File file, byte[] data) {

    }

    /**
     * 将byte数据保存到文件
     *
     * @param fileName
     * @param data
     */
    public static void writeDatasToFile(String fileName, byte[] data) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        WriteLock writelock = lock.writeLock();
        writelock.lock();
        try {
            if (data == null || data.length == 0)
                return;
            File file = new File(fileName);
            if (file.exists()) { // 判断当前文件是否存在
                file.delete(); // 存在就删除
            }
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            writelock.unlock();
        }
    }

    public static byte[] readFileContents(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        try {
            File fHandler = new File(fileName);
            if (!fHandler.exists()) { // 判断当前文件是否存在
                return null;
            }

            FileInputStream inputStream = new FileInputStream(fHandler);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }

            outStream.close();
            inputStream.close();
            return outStream.toByteArray();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    /**
     * 在sd卡应用目录下创建.nomedia文件，使得应用中的图片不被系统图库扫描
     *
     * @param filePath
     */
    public static void createNoMediaFileIfNotExist(String filePath) {
        //此处try catch 不能抛出
        try {

            String appFilePath = filePath;
            File file = new File(appFilePath + "/autonavi/.nomedia");
            if (!file.exists()) {
                file.createNewFile(); // 创建.nomedia文件，使得应用中的图片不被系统图库扫描
            }

            // 解决android系统的bug(如果目录下的图片在创建.nomedia之前已经被图库收录了，创建之后也不会从图库中消失)
            long mtime = file.lastModified();
            // Calendar cal = Calendar.getInstance();
            // cal.set(1970, 0, 1, 8, 0, 0); // 将时间设置成1970-1-1 8:00:00
            // cal.getTimeInMillis();
            long time = 0;
            if (mtime > time) {
                file.setLastModified(time); // 将文件时间修改成1970-1-1 8:00:00
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readData(String fileName) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReadLock readlock = lock.readLock();

        File file = new File(fileName);
        FileInputStream fin;
        String res = "";
        try {
            readlock.lock();
            if (file.exists()) {
                fin = new FileInputStream(file);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
                res = new String(buffer, "UTF-8");
                fin.close();
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            readlock.unlock();
        }

        return res;
    }

    /*----------------Kang, Leo 2014-02-25 start-----------------*/

    /**
     * 获得SD卡总大小
     *
     * @param context
     * @return
     */
    public static String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @param context
     * @return
     */
    public static String getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /**
     * 获得机身内存总大小
     *
     * @param context
     * @return
     */
    public static String getRomTotalSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @param context
     * @return
     */
    public static String getRomAvailableSize(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(context, blockSize * availableBlocks);
    }

    /*----------------Kang, Leo 2014-02-25 end-----------------*/

    /**
     * 递归删除文件和文件夹
     *
     * @param fileName 要删除的根目录名称
     * @param isCancel 是否取消删除
     * @author yi.kang
     * @date 2014-03-20 Kang, Leo add
     */
    public static void recursionDeleteFile(String fileName, boolean isCancel) {
        if (isCancel)
            return;
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f.getName(), isCancel);
            }
            file.delete();
        }
    }

    /**
     * 将指定内容保存到sd卡autonavi目录下指定文件中
     *
     * @param content   指定内容
     * @param aFileName 指定文件名
     */
    public static void saveLogToFile(String content, String aFileName) {

        String fileName = getAppSDCardFileDir();
        if (fileName == null)
            return;
        fileName = fileName + "/" + aFileName;
        try {
            File file = new File(fileName);
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.write("\r\n-------------------\r\n");
            writer.close();
        } catch (Exception e) {

        }
    }

    /**
     * 将byte数据保存到sd卡autonavi目录下指定文件中
     *
     * @param data      byte数组
     * @param afileName 文件名
     */
    public static void saveDataToFile(byte[] data, String afileName) {
        String fileName = getAppSDCardFileDir();
        if (fileName == null)
            return;
        fileName = fileName + "/" + afileName;
        writeDatasToFile(fileName, data);
    }

    /**
     * 取在sd卡中的目录
     *
     * @return
     */
    public static String getAppSDCardFileDir() {
        // 得到存储卡路径
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡
        // 或可存储空间是否存在
        if (sdCardExist) {
            File fExternalStorageDirectory = Environment
                    .getExternalStorageDirectory();
            sdDir = new File(fExternalStorageDirectory, "autonavi"); // 错误日志存储到SD卡autonavi目录下
            if (!sdDir.exists()) {
                sdDir.mkdir();
            }
        }
        if (sdDir == null)
            return null;

        return sdDir.toString();
    }

    /**
     * 文件下载完成之后，将临时文件改成.zip文件名
     */
    public static void dealTheFileByCompelete(String tempFile, String apkFile) {
        // 将.xxx.tmp扩展名改成.xxx
        if (TextUtils.isEmpty(tempFile) || TextUtils.isEmpty(apkFile))
            return;

        File tmpFile = new File(tempFile);
        File zipFile = new File(apkFile);
        if (zipFile.exists()) {// 文件是否存在
            if (!tmpFile.renameTo(zipFile)) {
                // 文件重命名成功
            }
        } else {
            if (!tmpFile.renameTo(zipFile)) {
                // 文件重命名成功
            }
        }
    }

    public static void launchApp(Context context, String packageName) {
        Intent launchIntent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);//
        try {
            context.startActivity(launchIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readStringFromAsets(Context context, String fileName) {
        String result = "";
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            // 获取文件的字节数
            int lenght = in.available();
            // 创建byte数组
            byte[] buffer = new byte[lenght];
            // 将文件中的数据读到byte数组中
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf-8");
            in.close();
        } catch (Exception e) {

        }
        return result;
    }

    public static String saveBitmap(Bitmap bmp) throws IOException {
        return saveBitmap(bmp, 90);
    }

    public static String saveBitmap(Bitmap bmp, int percent) throws IOException {

        if (bmp == null)
            return null;

        String root = getAppSDCardFileDir();
        if (root == null) {
            root = Environment.getExternalStorageDirectory().toString();
        }
        File myDir = new File(root + "/saved_images");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + System.currentTimeMillis() + "-" + percent
                + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            if (percent > 0 && percent <= 100) {
                bmp.compress(Bitmap.CompressFormat.JPEG, percent, out);
            } else {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file.getAbsolutePath();
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public static int getResourceByReflect(Context context, String imageName) {
        return context.getResources().getIdentifier(imageName, "drawable",
                context.getPackageName());
    }
}
