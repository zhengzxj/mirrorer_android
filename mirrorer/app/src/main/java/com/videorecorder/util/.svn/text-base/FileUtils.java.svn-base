package com.videorecorder.util;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.smart.mirrorer.R;
import com.videorecorder.bean.Frame;
import com.videorecorder.bean.MaterialParam;
import com.videorecorder.bean.TempVideoFileParam;
import com.videorecorder.handler.FrameFileHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class FileUtils {
    public final static String FILE_FORMAT = ".mp4";
    public final static String FILE_DIRECTORY = "/BDJ_VIDEO/";
    public final static String FILE_TEMP_DIR = "video_temp";
    public final static String FILE_VOICE_DIRECTORY = "voice_res";
    public final static String FILE_WATERMARK_DIRECTORY = "watermark_res";
    public final static String FILE_CVIDEO_DIRECTORY = "complete_video";
    public final static String FILE_IMG_CACHE_DIR = "img_temp";
    
    /**
     * 创建视频缓存文件
     * 
     * @param context
     * @return
     */
    public static String createTempVfile(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_DIRECTORY + FILE_TEMP_DIR;
            String name = System.currentTimeMillis() + FILE_FORMAT;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, name);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return file.getAbsolutePath();
        } else {
            try {
                Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg),
                        Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 删除视频缓存文件
     * 
     * @param context
     * @param path 文件路径
     * @return
     */
    public static boolean delTempVfile(Context context, String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            File file = new File(path);
            if (file.exists()) {
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 删除所有视频缓存文件
     * 
     * @param context
     * @param params 视频地址列表
     */
    public static void delVfileToAll(final Context context, final List<TempVideoFileParam> params) {
//        Thread thread = new Thread()
//        {
//            public void run() {
//                for (TempVideoFileParam param : params) {
//                    delTempVfile(context, param.getFileName());
//                }
//                params.clear();
//            }
//        };
//        thread.start();
    }
    
    /**
     * 创建文件
     * 
     * @param context
     * @param directory 需要创建的目录
     * @param name 需要创建的文件名
     * @return
     */
    public static String createFile(Context context, String directory, String name) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_DIRECTORY + directory;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, name);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return file.getAbsolutePath();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg), Toast.LENGTH_SHORT)
                    .show();
        }
        return null;
    }
    
    /**
     * 判断一个文件是否存在
     * 
     * @param context
     * @param directory 文件目录
     * @param name 文件名
     * @return
     */
    public static boolean isExist(Context context, String directory, String name) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_DIRECTORY + directory;
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, name);
            if (file.exists()) {
                return true;
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg), Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }
    
    /**
     * 判断一个文件是否存在
     * 
     * @param context
     * @param path 路径地址
     * @return
     */
    public static boolean isExist(Context context, String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(path);
            if (file.exists()) {
                return true;
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg), Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }
    
    /**
     * 删除目录
     * 
     * @param context
     * @param directory 目录名字
     * @return
     */
    public static boolean delDirectory(Context context, String directory) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_DIRECTORY + directory;
            File file = new File(dir);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (0 < files.length) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                return file.delete();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg), Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }
    
    /**
     * 得到路径
     * 
     * @param context
     * @param dir 目录
     * @param rawName 文件名
     * @return
     */
    public static String getFilePath(Context context, String dir, String rawName) {
        String dir1 = Environment.getExternalStorageDirectory().getAbsolutePath() + FILE_DIRECTORY + dir;
        File file = new File(dir1, rawName);
        String path = file.getAbsolutePath();
        return path;
    }
    
    /**
     * 读写资源文件到sdcard
     * 
     * @param context
     * @param rawId raw中文件名
     * @param format 文件格式(mp3,mp4,png等)
     * @param dir 存放的目录,没有就创建
     */
    private static String writeMaterialfileFromRaw(Context context, String rawId, String format, String dir) {
        if (!TextUtils.isEmpty(rawId)) {
            int id = context.getResources().getIdentifier(rawId, "raw", context.getPackageName());
            InputStream inputStream = context.getResources().openRawResource(id);
            String path = createFile(context, dir, rawId + "." + format);
            if (TextUtils.isEmpty(path)) {
                return null;
            }
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int read = 0;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                inputStream.close();
                outputStream.close();
                return path;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 拷贝raw中文件到储存卡
     * 
     * @param context
     */
    public static void copyVoiceFile(final Context context) {
        final List<MaterialParam> lists = XmlParseUtils.parseXml(context, R.xml.plist_voice_material);
        Thread thread = new Thread()
        {
            public void run() {
                for (MaterialParam param : lists) {
                    String rawName = param.getResourceName();
                    if ("0".equals(param.getId())) {
                        continue;
                    }
                    if (!isExist(context, FILE_VOICE_DIRECTORY, rawName + ".mp3")) {
                        writeMaterialfileFromRaw(context, rawName, "mp3", FILE_VOICE_DIRECTORY);
                    }
                }
            };
        };
        thread.start();
    }
    
    /**
     * 拷贝raw目录中的图片相关资源到储存卡
     * 
     * @param context
     */
    public static void copyPhotoFile(final Context context) {
        final List<MaterialParam> lists = XmlParseUtils.parseXml(context, R.xml.plist_border_material);
        Thread thread = new Thread()
        {
            public void run() {
                for (MaterialParam param : lists) {
                    String rawName = param.getResourceName();
                    if ("0".equals(param.getId())) {
                        continue;
                    }
                    String format = param.getFormat();
                    if (!isExist(context, FILE_WATERMARK_DIRECTORY, rawName + "." + format)) {
                        writeMaterialfileFromRaw(context, rawName, format, FILE_WATERMARK_DIRECTORY);
                    }
                }
            };
        };
        thread.start();
    }
    
    /**
     * 获取文件在储存卡中的路径地址
     * 
     * @param context
     * @param rawName 文件名
     * @param format 文件格式(mp3,mp4,png等)
     * @param dir 存放的目录,没有就创建
     * @return
     */
    public static String getFilePathFromSdcard(Context context, String rawName, String format, String dir) {
        if (isExist(context, dir, rawName + "." + format)) {
            return getFilePath(context, dir, rawName + "." + format);
        } else {
            return writeMaterialfileFromRaw(context, rawName, format, dir);
        }
    }
    
    /**
     * 把Bitmap数据保存到sdcard中
     * 
     * @param context
     *
     */
    public static String writeBitmapPixelsToFile(Context context, byte[] buffer, String path) {
        boolean isExist = isExist(context, path);
        try {
            if (!isExist) {
                path = createFile(context, FileUtils.FILE_IMG_CACHE_DIR, System.currentTimeMillis() + ".img");
            }
            OutputStream outputStream = new FileOutputStream(path,true);
            outputStream.write(buffer);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return path;
    }
    
    /**
     * 把每一帧数据保存到sdcard中
     * @param context
     * @param data 每帧数据
     * @param width 宽
     * @param height 高
     * @param frameNumber 帧数
     */
    public static void writeFrameByArrays(Context context, byte[] data, int width, int height, int frameNumber) {
        YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(width * height * 3 / 2);
        if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
            return;
        }
        
        Frame frame = FrameFileHandler.getInstance().getFrameForKey(frameNumber - 1);
        if (frame != null) {
            String path = frame.getSource();
            try {
                OutputStream outputStream = new FileOutputStream(path);
                outputStream.write(os.toByteArray());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
