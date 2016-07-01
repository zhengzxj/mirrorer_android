package com.smart.mirrorer.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lzm on 16/3/25.
 */
public class TipsUtils {

    public static boolean isShow = true;
    private static Toast mToast;

    /**
     * 短时间显示Toast
     *
     * @param context 上下文对象
     * @param message 需要显示的字符串信息
     */
    public static void showShort(Context context, CharSequence message)
    {
        if (isShow)  {
            if (mToast == null){
                mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }else {
                mToast.setText(message);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param context 上下文对象
     * @param message 资源ID
     */
    public static void showShort(Context context, int message)
    {
        if (isShow)  {
            if (mToast == null){
                mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }else {
                mToast.setText(message);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }

    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
