package com.smart.mirrorer.util;

import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.MonitoredActivity;
import com.smart.mirrorer.view.MyLoadingDialog;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Collection of utility functions used in this package.
 */
public class MonitoredUtil {

    private static final String TAG = "db.Util";
    private static final int IMAGE_MAX_SIZE = 1024;

    private MonitoredUtil()
    {

    }

    private static class BackgroundJob extends MonitoredActivity.LifeCycleAdapter implements Runnable {

        private final MonitoredActivity mActivity;
        private final Dialog mDialog;
        private final Runnable mJob;
        private final Handler mHandler;
        private final Runnable mCleanupRunner = new Runnable()
        {
            public void run() {

                mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (mDialog.getWindow() != null)
                    mDialog.dismiss();
            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job, MyLoadingDialog dialog, Handler handler)
        {

            mActivity = activity;
            mDialog = dialog;
            mJob = job;
            mActivity.addLifeCycleListener(this);
            mHandler = handler;
        }

        public void run() {

            try {
                mJob.run();
            } finally {
                mHandler.post(mCleanupRunner);
            }
        }

        @Override
        public void onActivityDestroyed(MonitoredActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(MonitoredActivity activity) {

            mDialog.hide();
        }

        @Override
        public void onActivityStarted(MonitoredActivity activity) {

            mDialog.show();
        }
    }

    public static void startBackgroundJob(MonitoredActivity activity, String message, boolean cancel, Runnable job,
            Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        MyLoadingDialog dialog = new MyLoadingDialog(activity, R.style.common_dialog);
        dialog.setMsg(message);
        dialog.setCanceledOnTouchOutside(cancel);
//        dialog.setCancelable(cancel);
        dialog.show();
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

    public static Uri getImageUri(String path) {

        return Uri.fromFile(new File(path));
    }

    public static Bitmap getBitmap(ContentResolver mContentResolver, String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();
            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + path + " not found");
        } catch (IOException e) {
            Log.e(TAG, "file " + path + " not found");
        }
        return null;
    }

    public static String getSaveImagePath(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            int len = imagePath.lastIndexOf("/");
            String fileName = imagePath.substring(len + 1);
            String[] file = fileName.split("\\.");
            if (file != null && file.length >= 2) {
                fileName = file[0] + "_clip" + "." + file[file.length - 1];
            } else {
                fileName = fileName + "_clip";
            }
            return imagePath.substring(0, len + 1) + fileName;
        }
        return imagePath;
    }

}
