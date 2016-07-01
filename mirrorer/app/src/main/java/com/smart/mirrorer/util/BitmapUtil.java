package com.smart.mirrorer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BitmapUtil {

    public static boolean photoZoom(String bitmapPath) {
        int degree = readPictureDegree(bitmapPath);
        if (degree > 0) {
            Options opts = new Options();// 获取缩略图显示到屏幕上
            opts.inSampleSize = 2;
            Bitmap cbitmap = BitmapFactory.decodeFile(bitmapPath, opts);

            /**
             * 把图片旋转为正的方向
             */
            Bitmap newbitmap = rotaingImageView(degree, cbitmap);
            BitmapUtil.saveBitmap2file(newbitmap, bitmapPath);
            return true;
        }
        return false;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //
    public static Bitmap getUploadBitmap(String srcPath) {
        Options newOpts = new Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        newOpts.inSampleSize = calcUploadInSampleSize(w, h);// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        // 压缩好比例大小后再进行质量压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        if (baos.toByteArray().length / 1024 > 100) { // 大于100KB
            baos.reset();// 重置baos即清空baos
            bitmap.compress(CompressFormat.JPEG, 40, baos);// 这里压缩options%，把压缩后的数据存放到baos中
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
            srcBitmap.compress(CompressFormat.JPEG, 100, baos);
            if (baos.toByteArray().length / 1024 > 100) { // 大于100KB
                baos.reset();// 重置baos即清空baos
                srcBitmap.compress(CompressFormat.JPEG, 40, baos);// 这里压缩options%，把压缩后的数据存放到baos中
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
    public static int calcUploadInSampleSize(int outWidth, int outHeight) {
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

    /*
     * 旋转图片
     *
     * @param angle
     *
     * @param bitmap
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 把指定res的文件保存为本地外部存储上的文件
     *
     * @param mContext
     *            -- 上下文
     * @param fileName
     *            -- 文件名称
     * @param resId
     *            -- 资源id
     */
    public static String saveResourceAsCurrentStorage(Context mContext, String fileName, int resId) {
        if(mContext ==null) {
            return null;
        }
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resId);
        if(bmp == null) {
            return null;
        }

        File file = AppFilePath.getBitmapFile(fileName + ".png");
        if(file == null) {
            return null;
        }
        Log.e("wuzhenlin", file.getAbsolutePath());
        if (!file.exists()) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bmp.compress(CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static int computeSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 读取本地的图片
     *
     *
     * @return
     */
    public static Bitmap readBitMap(String imageFile) {
        Bitmap bmp = null;
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
        opts.inJustDecodeBounds = false;
        try {
            bmp = BitmapFactory.decodeFile(imageFile, opts);

        } catch (OutOfMemoryError err) {
        }

        return bmp;

    }

    public static void CompressImage(String path) {
        FileInputStream in;
        byte[] mContent = null;
        Bitmap myBitmap;
        try {
            in = new FileInputStream(path);
            mContent = readStream(in);

        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        myBitmap = BitmapUtil.getBitmap(mContent, 480, 480);
        File file = new File(path);

        int offset = 100;
        long fileSize = file.length();
        if (200 * 1024 < fileSize && fileSize <= 1024 * 1024)
            offset = 90;
        else if (1024 * 1024 < fileSize)
            offset = 85;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myBitmap.compress(CompressFormat.JPEG, offset, baos);
        mContent = baos.toByteArray();

        FileOutputStream fos = null;
        try {
            File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            fos = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        try {
            bos.write(mContent);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    public static Bitmap getBitmap(byte[] data, int scale, Options opts) {
        // Options opts = new Options();
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
    }

    public static Bitmap getBitmap(byte[] data, int widht, int height) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int scaleX = opts.outWidth / widht;
        System.out.println("scaleX" + scaleX);
        int scaleY = opts.outHeight / height;
        System.out.println("scaleY" + scaleY);
        int scale = scaleX > scaleY ? scaleX : scaleY;
        return getBitmap(data, scale, opts);
    }


    /**
     * 把图片变成圆角
     *
     * @param bitmap
     *            需要修改的图片
     * @param pixels
     *            圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 保存图片为PNG
     *
     * @param bitmap
     * @param name
     */
    public static boolean savePNGAfter(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;

        try {
            File tmp = new File(filename);
            boolean flag = false;
            File parent = tmp.getParentFile();
            if (!parent.exists()) {
                flag = parent.mkdir();
            } else {
                flag = true;
            }
            if (parent.exists()) {
                stream = new FileOutputStream(filename);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }

}
