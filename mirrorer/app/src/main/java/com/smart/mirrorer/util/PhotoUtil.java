package com.smart.mirrorer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoUtil {

	public static File scal(String impaPath){
		File outputFile = new File(impaPath);
		long fileSize = outputFile.length();

		Log.e("lzm", "sss source " + fileSize);

		final long fileMaxSize = 200 * 1024;
		if (fileSize >= fileMaxSize) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(impaPath, options);
			int height = options.outHeight;
			int width = options.outWidth;

			double scale = Math.sqrt((float) fileSize / fileMaxSize);
			options.outHeight = (int) (height / scale);
			options.outWidth = (int) (width / scale);
			options.inSampleSize = (int) (scale + 0.5);
			options.inJustDecodeBounds = false;

			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeFile(impaPath, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			if(bitmap != null) {

				outputFile = new File(createImageFile().getPath());
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(outputFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.e("lzm", "sss ok " + outputFile.length());
				if (!bitmap.isRecycled()) {
					bitmap.recycle();
				} else {
					File tempFile = outputFile;
					outputFile = new File(createImageFile().getPath());
					PhotoUtil.copyFileUsingFileChannels(tempFile, outputFile);
				}
			}

		}
		return outputFile;

	}

	/**
	 * 创建临时文件
	 * @return
	 */
	public static Uri createImageFile(){
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = new File(getSDPath() + "/img_temp/");
		if (!storageDir.exists()) {
			storageDir.mkdir();
		}
	    File image = null;
		try {
			image = File.createTempFile(
					imageFileName,  /* prefix */
					".jpg",         /* suffix */
					storageDir      /* directory */
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    // Save a file: path for use with ACTION_VIEW intents
	    return Uri.fromFile(image);
	}
	public static void copyFileUsingFileChannels(File source, File dest){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } finally {
            try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

	private static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();//获取跟目录
		}
		return sdDir.toString();

	}

}
