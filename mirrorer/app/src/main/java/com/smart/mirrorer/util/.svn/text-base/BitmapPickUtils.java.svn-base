package com.smart.mirrorer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.format.DateFormat;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.ClipPictureActivity;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;


public class BitmapPickUtils {

    public static void startSystemCamera(Activity activity, int resultCode, File output) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
        activity.startActivityForResult(intent, resultCode);
    }

    public static void startSystemGallery(Activity activity, int resultCode) {
        Intent intent1 = new Intent(Intent.ACTION_PICK, null);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent1, resultCode);
    }

    public static String pickResultFromGalleryImage(Activity activity, Intent data) {
        /**
         * 当选择的图片不为空的话，在获取到图片的途径
         */
        Uri uri = data.getData();
        try {
            String[] pojo = {MediaColumns.DATA};
            Cursor cursor = activity.managedQuery(uri, pojo, null, null, null);
            if (cursor != null) {
                ContentResolver cr = activity.getContentResolver();
                int colunm_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(colunm_index);
                /***
                 * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话， 你选择的文件就不一定是图片了，
                 * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
                 */
                if (path.endsWith("jpg") || path.endsWith("png")) {
                    // startPhotoClip(path);
                    return path;
                } else {
                    alert(activity);
                }
            } else {
                alert(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 切图
     */
    public static void startPhotoClip(BaseActivity activity, String path, int requestCode) {
        /**
         *
         * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
         */
        int degree = BitmapUtil.readPictureDegree(path);
        if (degree > 0) {
            BitmapFactory.Options opts = new BitmapFactory.Options();// 获取缩略图显示到屏幕上
            opts.inSampleSize = 2;
            Bitmap cbitmap = BitmapFactory.decodeFile(path, opts);
            /**
             * 把图片旋转为正的方向
             */
            Bitmap newbitmap = BitmapUtil.rotaingImageView(degree, cbitmap);
            BitmapUtil.saveBitmap2file(newbitmap, path);
        }
        Intent intent = new Intent(activity.getApplicationContext(), ClipPictureActivity.class);
        intent.putExtra(ClipPictureActivity.IMAGE_PATH, path);
        intent.putExtra(ClipPictureActivity.CROP_HEIGHT, 300);
        intent.putExtra(ClipPictureActivity.CROP_WIDTH, 300);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 弹框提示是否是有效图片
     */
    private static void alert(Activity activity) {
        Dialog dialog = new AlertDialog.Builder(activity).setTitle("提示").setMessage("您选择的不是有效的图片")
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
        dialog.show();
    }

    public static String createCameraPictureName() {
        return DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
    }
}
