package com.smart.mirrorer.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.smart.mirrorer.R;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.MyLoadingDialog;
import com.umeng.analytics.MobclickAgent;
import com.videorecorder.util.Log;

/**
 * Created by lzm on 16/3/24.
 */
public class BaseActivity extends MonitoredActivity {

    public static final int PRASSAGE_DIALOG = 0x01;
    private MyLoadingDialog mLoadDialog;

    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("lzm","当前启动的Activity是"+this.getLocalClassName());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initWindow();

        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{android.R.attr.activityOpenEnterAnimation, android.R.attr.activityOpenExitAnimation, android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation});
        int activityOpenEnterAnimation = activityStyle.getResourceId(0, 0);
        int activityOpenExitAnimation = activityStyle.getResourceId(1, 0);
        activityCloseEnterAnimation = activityStyle.getResourceId(2, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(3, 0);
        activityStyle.recycle();

        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
    }


    //TODO 设置沉浸式状态栏
    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 取消加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void dismissLoadDialog() {
        if (mLoadDialog != null && mLoadDialog.isShowing()) {
            dismissDialog(PRASSAGE_DIALOG);
        }
    }
    /**
     * 取消加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void dismissLoadDialog(String log) {
        L.i(log);
        if (mLoadDialog != null && mLoadDialog.isShowing()) {
            dismissDialog(PRASSAGE_DIALOG);
        }
    }

    /**
     * 显示加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void showLoadDialog(String log) {
        L.i(log);
        showDialog(PRASSAGE_DIALOG);
    }
    /**
     * 显示加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void showLoadDialog() {
        showDialog(PRASSAGE_DIALOG);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PRASSAGE_DIALOG:
                // mLoadDialog = new Dialog(this, R.style.dialogTheme);
                // mLoadDialog.setContentView(R.layout.dailog_loading_layout);
                // mLoadDialog.setCancelable(true);
                mLoadDialog = new MyLoadingDialog(this, R.style.dialogTheme);
                return mLoadDialog;
        }
        return super.onCreateDialog(id);
    }

    public void processCleanIntent(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    // Global view click listener
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onUserInteraction(view);
        }
    };


    public View.OnClickListener getViewClickListener(){
        return onClickListener;
    }

    /**
     * Central point of handling all view click events
     * @param view
     */
    public void onUserInteraction(View view){

    }

//    public void log(Object obj) {
//
//        // You can use filter *** to filter out message
//        LoggingUtils.error(getClass().getName(),
//                String.format("*** %s ***",
//                        obj == null ? "--!--"
//                                : obj.toString()));
//    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
