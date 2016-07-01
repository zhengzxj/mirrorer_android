package com.bdj.video.build;

import android.util.Log;


/**
 * 高级编辑 ，每帧数据
 * @author jhw4830
 *
 */
public class NativeVideoStream {
	private static final String TAG = "NativeVideoStream";

	@SuppressWarnings("JniMissingFunction")
	public static native int NativeAddVideoStreamObs(VideoStreamObs obs);
	@SuppressWarnings("JniMissingFunction")
	public static native int NativeStartGetVideoFrame(String file);
	
	static
	{
		String libname = "VideoStream";
    	try
        {
            System.loadLibrary(libname);
        	Log.d(TAG, "System.loadLibrary success  - " + libname);
        	
        	libname = "WeaverVideoCodec";
            System.loadLibrary(libname);
        	Log.d(TAG, "System.loadLibrary success  - " + libname);
        	
           	libname = "dynload";
            System.loadLibrary(libname);
        	Log.d(TAG, "System.loadLibrary success  - " + libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
        	Log.e(TAG, "System.loadLibrary error - " + libname);
        }
    }
}
