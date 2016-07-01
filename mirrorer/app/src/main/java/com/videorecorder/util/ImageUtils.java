package com.videorecorder.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;


public class ImageUtils {
    public static Bitmap getBitmapFromResource(Context context, String resourceID) {
        if (!TextUtils.isEmpty(resourceID)) {
            int id = context.getResources().getIdentifier(resourceID, "drawable", context.getPackageName());
            Bitmap mBitmap = BitmapFactory.decodeResource(context.getResources(), id);
            return mBitmap;
        }
        return null;
    }
    
    public static byte[] getByteArraysFromResource(Context context, String resourceID) {
        if (!TextUtils.isEmpty(resourceID)) {
            int id = context.getResources().getIdentifier(resourceID, "drawable", context.getPackageName());
            InputStream inStream = context.getResources().openRawResource(id);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = inStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                inStream.close();
                outputStream.close();
                
            } catch (Exception e) {
                // TODO: handle exception
            }
            return outputStream.toByteArray();
        }
        return null;
    }
    
    public static Bitmap getBitmapFromRaw(Context context, String resourceID) {
        if (!TextUtils.isEmpty(resourceID)) {
            int id = context.getResources().getIdentifier(resourceID, "raw", context.getPackageName());
            InputStream inputStream = context.getResources().openRawResource(id);
            Bitmap mBitmap = BitmapFactory.decodeStream(inputStream);
            return mBitmap;
        }
        return null;
    }
    
    public static Bitmap getBitmapFromUrl(Context context, String url) {
        boolean isExist = FileUtils.isExist(context, url);
        if (isExist) {
            InputStream inputStream;
            try {
                inputStream = new FileInputStream(url);
                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                return bm;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static Bitmap mergeBitmap(Bitmap[] bitmaps, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmap);
        for (Bitmap bm : bitmaps) {
            canvas.drawBitmap(bm, 0, 0, paint);
        }
        canvas.save();
        canvas.restore();
        return bitmap;
    }
    
    public static Bitmap convertViewToBitmap(View view, int width, int height) {
        if (view != null) {
            view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            return bitmap;
        }
        return null;
    }
    
    public static Bitmap mergeBitmap(Context context, Bitmap baseBm, String content) {
        Bitmap mergeBm = null;
        if (baseBm != null) {
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 0, 10, 0);
            textView.setText(content);
            textView.setTextSize(BdjUtils.calculateFontSize(content.length()));
            int width = baseBm.getWidth();
            int height = baseBm.getHeight();
            Bitmap bm = convertViewToBitmap(textView, width, height);
            mergeBm = mergeBitmap(new Bitmap[] { baseBm, bm }, width, height);
            bm.recycle();
            bm = null;
        }
        return mergeBm;
    }
    /*
    public static void mergeBitmap(Canvas canvas, Widgets widgets, View view) {
        Bitmap bm = convertViewToBitmap(view, view.getWidth(), view.getHeight());
        Location location = widgets.get_SuperViewParam().get_location();
        canvas.drawBitmap(bm, location.getLeft(), location.getTop(), new Paint());
    }
    */
}
