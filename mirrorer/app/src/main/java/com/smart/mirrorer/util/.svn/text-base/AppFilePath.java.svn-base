package com.smart.mirrorer.util;

import android.content.Context;
import android.os.Environment;

import com.smart.mirrorer.base.BaseApplication;

import java.io.File;

public class AppFilePath {
    // ------------------------------ 手机系统相关 ------------------------------
    public static final String NEWLINE = System.getProperty("line.separator");// 系统的换行符
    public static final String ASSERT_PATH = "file:///android_asset";// apk的assert目录
    public static final String RES_PATH = "file:///android_res";// apk的assert目录

    // ------------------------------------数据的缓存目录-------------------------------------------------------

    public static File CURRENT_FILE_PATH;
    public static File CURRENT_CACHE_PATH;
    //
    public static String FILE_BITMAP_SUFFIX = "bitmap";  // 存放分享生成的文件
    public static String FILE_PICTURE_SUFFIX = "picture"; // 拍照生成图片存放路径
    public static String FILE_VOICE_SUFFIX = "voice";   // 录音存放路径
    public static String FILE_VIDEO_SUFFIX = "video";   // 视频存放路径
    public static String FILE_VIDEO_RECORD_SUFFIX = "jus_video_record";   // justalk录制视频存放路径

    static {
        init(BaseApplication.getInstance().getApplicationContext());
    }

    public static void init(Context context) {
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            CURRENT_FILE_PATH = getExternalFileDir(context);
            CURRENT_CACHE_PATH = getExternalCacheDir(context);
        } else {
            CURRENT_FILE_PATH = context.getFilesDir();
            CURRENT_CACHE_PATH = context.getCacheDir();
        }
    }

    public static File getBitmapFile(String fileName) {
        return new File(getBitmapDir(), fileName);
    }

    public static File getPictureFile(String fileName) {
        return new File(getPictureDir(), fileName);
    }

    public static File getVoiceFile(String fileName) {
        return new File(getVoiceDir(), fileName);
    }
    
    public static File getVideoFile(String fileName){
        return new File(getVideoDir(),fileName);
    }

    public static File getJusVideoRecordFile(String fileName) {
        return new File(getJustalkVideoDir(), fileName);
    }

    public static File getBitmapDir() {
        File dir = new File(CURRENT_FILE_PATH, FILE_BITMAP_SUFFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getPictureDir() {
        File dir = new File(CURRENT_FILE_PATH, FILE_PICTURE_SUFFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getVoiceDir() {
        File dir = new File(CURRENT_FILE_PATH, FILE_VOICE_SUFFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
    
    public static File getVideoDir(){
        File dir = new File(FILE_VIDEO_SUFFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static File getJustalkVideoDir() {
        File dir = new File(CURRENT_FILE_PATH, FILE_VIDEO_RECORD_SUFFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取可以使用的缓存目录
     *
     * @param uniqueName 目录名称
     * @return
     */
    public static File getDiskCacheDir(String uniqueName) {
        return new File(CURRENT_CACHE_PATH, uniqueName);
    }

    /**
     * 获取程序外部的缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 获取程序外部的文件目录
     *
     * @param context
     * @return
     */
    public static File getExternalFileDir(Context context) {
        final String fileDir = "/Android/data/" + context.getPackageName() + "/files/";
        return new File(Environment.getExternalStorageDirectory().getPath() + fileDir);
    }
}
