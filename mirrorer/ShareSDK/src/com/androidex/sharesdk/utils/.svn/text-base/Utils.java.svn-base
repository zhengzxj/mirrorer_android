package com.androidex.sharesdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class Utils {

    //
    public static Bitmap scaleAndcompressBitmap(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        newOpts.inSampleSize = calcInSampleSize(w, h);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        // 压缩好比例大小后再进行质量压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        if (baos.toByteArray().length / 1024 > 100) { // 大于100KB
            baos.reset();// 重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        bitmap.recycle();
        bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        // 压缩好比例大小后再进行质量压缩
        return compressImage(bitmap);
    }

    public static Bitmap compressImage(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream iaos = null;
        Bitmap bitmap = null;
        try {
            baos = new ByteArrayOutputStream();
            // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            if (baos.toByteArray().length / 1024 > 100) { // 大于100KB
                baos.reset();// 重置baos即清空baos
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            }
            iaos = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            bitmap = BitmapFactory.decodeStream(iaos, null, null);// 把ByteArrayInputStream数据生成图片
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (iaos != null) {
                try {
                    iaos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    // 计算图片上传的缩放大小
    public static int calcInSampleSize(int outWidth, int outHeight) {
        int be = 1;
        float baseWidth = 480f;
        float baseHight = 800f;
        // 最小宽高比
        float minRate = 0.3f;
        float maxRate = 1.7f;
        float whRate = outWidth / outHeight;
        if (whRate < minRate) {
            // 很瘦很长的情况， 使用宽度做基准
            if (outWidth > baseWidth) {
                be = Math.round(outWidth * 1.0f / baseWidth);
            }
        } else if (whRate > maxRate) {
            // 很宽的情况， 使用高度做基准
            if (outHeight > baseHight) {
                be = Math.round(outHeight * 1.0f / baseHight);
            }
        } else {
            // 如果宽度大的话根据宽度固定大小缩放
            if (outWidth > outHeight && outWidth > baseWidth) {
                be = Math.round(outWidth * 1.0f / baseWidth);
                // 如果高度高的话根据宽度固定大小缩放
            } else if (outWidth < outHeight && outHeight > baseHight) {
                be = Math.round(outHeight * 1.0f / baseHight);
            }
        }
        if (be <= 0)
            be = 1;
        return be;
    }

    /**
     * 获取APP名字
     */
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    //
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 保存图片到SDCard

}
