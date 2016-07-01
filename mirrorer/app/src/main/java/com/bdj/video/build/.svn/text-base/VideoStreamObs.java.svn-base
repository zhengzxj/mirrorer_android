package com.bdj.video.build;


import com.videorecorder.handler.EveryframeHandler2;
import com.videorecorder.util.FileUtils;

import android.content.Context;
import android.util.Log;


public class VideoStreamObs {
	private static final String TAG = "VideoStreamObs";
	private final int MIN_LIMIT = 6;
	//pcl2050325 start
//	private byte[]yuv420sp = new byte[480*480*3/2];
	private byte[]yuv420sp = new byte[1024*1024*3/2];
	//end
	private byte[]yuv420sp_rot = new byte[480*480*3/2];
	private static Context mContext;
	private static EveryframeHandler2 mFrameHandler;
    public void SetContext(Context context) {
        this.mContext = context;
    }
    public void SetEveryFragment(EveryframeHandler2 everyframeHandler2) {
    	Log.e(TAG,"SetEveryFragment");
        this.mFrameHandler = everyframeHandler2;
    }
    public void setStopFlag() {
        mFrameHandler.setStopFlag();
    }
    
	public void yuv420ptoyuv420sp(byte[]src, byte[]dst, int width, int height)
	{
		int framesize = width*height;
		int k=0;
		for (int i=0;i<framesize;i++) {
			dst[k] = src[k];
			k++;
		}
		for (int i=0;i<framesize/4;i++) {
			dst[k+1] = src[framesize + i];
			dst[k] = src[framesize + framesize/4 + i];
			k+=2;
		}
	}
	public void yuv420sp_rotate_1(byte[]src, byte[]dst, int width, int height)
	{
		int framesize = width*height;
		int k=0;
		for (int i=0;i<width;i++) {
			for (int j=0;j<height;j++) {
				dst[k] = src[(height-1-j)*width+i];
				k++;
			}
		}
		for (int i=0;i<width;i+=2) {
			for (int j=0;j<height/2;j++) {
				dst[k] = src[framesize + (height/2 - 1 - j)*width + i];
				dst[k+1] = src[framesize + (height/2 - 1 - j)*width + i + 1];
				k+=2;
			}
		}
	}

	public void OnGetVideoFrame(byte[] buf, 
							   int size, 
							   int width, 
							   int height, 
							   int frame_index)
	{
        if (MIN_LIMIT < frame_index) {
            setStopFlag();
            // return;
        }
		Log.d(TAG, "call GetVideoFrame: " + 
				   "size = " + size + 
				   ", width = " + width +			   
				   ", height = " + height+ 
				   ", frame_index = " + frame_index);
		//byte[]yuv420sp = new byte[width*height*3/2];
		yuv420ptoyuv420sp(buf, yuv420sp, width, height);
		//byte[]yuv420sp_rot = new byte[width*height*3/2];
		//yuv420sp_rotate_1(yuv420sp, yuv420sp_rot, width, height);
		FileUtils.writeFrameByArrays(mContext, yuv420sp, width, height, frame_index);
	}
}