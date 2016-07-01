package com.videorecorder.util;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * 工具类
 * 
 * @author zhangm
 * 
 */
public class BdjUtils {
    
    public static void startActivity(Context context, Class _Class) {
        startActivity(context, null, _Class);
    }
    
    public static void startActivity(Context context, String fileName, Class _Class) {
        Intent intent = new Intent(context, _Class);
        if (!TextUtils.isEmpty(fileName)) {
            //intent.putExtra(EditVideoActivity.VIDEO_PATH, fileName);
        }
        context.startActivity(intent);
    }
    
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }
    
    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
    
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
    
    public static float getDensity(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }
    
    public static boolean checkVersion(int minVer) {
        if (minVer <= Build.VERSION.SDK_INT) {
            return true;
        }
        return false;
    }
    
    public static void openViewAnim(Context context, View view, int offset) {
        view.clearAnimation();
        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, BdjUtils.getScreenHeight(context) - offset, 0);
        tAnimation.setDuration(500);
        view.startAnimation(tAnimation);
        view.setVisibility(View.VISIBLE);
    }
    
    public static void closeViewAnim(Context context, View view, int offset) {
        view.clearAnimation();
        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, BdjUtils.getScreenHeight(context) - offset);
        tAnimation.setDuration(500);
        view.startAnimation(tAnimation);
        view.setVisibility(View.GONE);
    }
    
    public static String millisecondToTimeString(long milliSeconds, boolean displayCentiSeconds) {
        long seconds = milliSeconds / 1000; // round down to compute seconds
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long remainderMinutes = minutes - (hours * 60);
        long remainderSeconds = seconds - (minutes * 60);
        
        StringBuilder timeStringBuilder = new StringBuilder();
        
        // Hours
        if (hours > 0) {
            if (hours < 10) {
                timeStringBuilder.append('0');
            }
            timeStringBuilder.append(hours);
            
            timeStringBuilder.append(':');
        }
        
        // Minutes
        if (remainderMinutes < 10) {
            timeStringBuilder.append('0');
        }
        timeStringBuilder.append(remainderMinutes);
        timeStringBuilder.append(':');
        
        // Seconds
        if (remainderSeconds < 10) {
            timeStringBuilder.append('0');
        }
        timeStringBuilder.append(remainderSeconds);
        
        // Centi seconds
        if (displayCentiSeconds) {
            timeStringBuilder.append('.');
            long remainderCentiSeconds = (milliSeconds - seconds * 1000) / 10;
            if (remainderCentiSeconds < 10) {
                timeStringBuilder.append('0');
            }
            timeStringBuilder.append(remainderCentiSeconds);
        }
        
        return timeStringBuilder.toString();
    }
    
    /**
     * 判断是否隐藏软键盘
     * 
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                // hideSoftInput(v.getWindowToken());
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
    
    /**
     * 是否点击到自己
     * 
     * @param v
     * @param event
     * @return
     */
    public static boolean isFocus(View v, MotionEvent event) {
        if (v != null) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            int left = location[0], top = location[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    /**
     * 计算字体大小
     * 
     * @param length 字数
     * @return
     */
    public static float calculateFontSize(int length) {
        float size = 50;
        if (length <= 2) {
            size = 50;
        } else if (2 < length && length <= 10) {
            size = 40;
        } else if (10 < length && length <= 15) {
            size = 30;
        } else if (15 < length && length <= 20) {
            size = 28;
        } else if (20 < length && length <= 25) {
            size = 25;
        } else if (25 < length && length <= 30) {
            size = 22;
        } else if (30 < length && length <= 40) {
            size = 20;
        } else {
            size = 18;
        }
        return size;
    }
    /*
    public static int[] caculateWidthAndHeight(Context context) {
        int[] wh = new int[2];
        int baseWidth = 480;
        int baseHeight = 480;
        int leftOffset = context.getResources().getDimensionPixelSize(R.dimen.view_width_80);
        int rightOffset = context.getResources().getDimensionPixelSize(R.dimen.margin_10);
        int width = getScreenWidth(context) - leftOffset - rightOffset;
        int height = baseHeight * width / baseWidth;
        wh[0] = width;
        wh[1] = height;
        return wh;
    }
    */
    public static int caculateHeight(Context context) {
        int baseWidth = 480;
        int baseHeight = 480;
        int width = getScreenWidth(context);
        int height = baseHeight * width / baseWidth;
        return height;
    }
    /*
    public static CPoint caculateInitPoint(Context context) {
        int topHeight = context.getResources().getDimensionPixelSize(R.dimen.view_height_45);
        int topMagianHeight = context.getResources().getDimensionPixelSize(R.dimen.margin_20);
        int leftWidth = context.getResources().getDimensionPixelSize(R.dimen.view_width_80);
        int x = getScreenWidth(context) - leftWidth;
        x = x / 2;
        int height = caculateWidthAndHeight(context)[1] / 2;
        // int y = topHeight + topMagianHeight + height;
        int y = height;
        CPoint cPoint = new CPoint(x, y);
        return cPoint;
    }
    */
    public static int getSurfaceViewHeight(Context context, int width, int height) {
        int screenWidth = getScreenWidth(context);
        int h = screenWidth * height / width;
        return h;
    }
}
