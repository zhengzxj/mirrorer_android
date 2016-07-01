package com.androidex.sharesdk.utils;

import android.app.Dialog;
import android.os.Handler;

import com.androidex.sharesdk.core.HookActivity;
import com.androidex.sharesdk.core.ShareHookActivity;

/**
 * Collection of utility functions used in this package.
 */
public class HookUtil {

    private HookUtil()
    {

    }

    private static class BackgroundJob extends ShareHookActivity.LifeCycleAdapter implements Runnable {

        private final HookActivity mActivity;
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

        public BackgroundJob(HookActivity activity, Runnable job, LoadingDialog dialog, Handler handler)
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
        public void onActivityDestroyed(HookActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(HookActivity activity) {

            mDialog.hide();
        }

        @Override
        public void onActivityStarted(HookActivity activity) {

            mDialog.show();
        }
    }

    public static void startBackgroundJob(HookActivity activity, String message, boolean cancel, Runnable job,
            Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        LoadingDialog dialog = new LoadingDialog(activity);
        dialog.setMsg(message);
        dialog.setCanceledOnTouchOutside(cancel);
        // dialog.setCancelable(cancel);
        dialog.show();
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

}
