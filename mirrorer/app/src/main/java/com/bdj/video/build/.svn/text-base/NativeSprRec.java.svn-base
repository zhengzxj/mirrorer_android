package com.bdj.video.build;

import android.util.Log;


public class NativeSprRec
{
//	public static final int SPR_VIDEO_PARAM_NONE          = 0;
	public static final int SPR_VIDEO_PARAM_WIDTH         = 1;
	public static final int SPR_VIDEO_PARAM_HEIGHT        = 2;
	public static final int SPR_VIDEO_PARAM_FPS           = 3;
	public static final int SPR_VIDEO_PARAM_FPS_DEN       = 4;
	public static final int SPR_VIDEO_PARAM_FPS_NUM       = 5;
	public static final int SPR_VIDEO_PARAM_VIDEO_BITRATE = 6;
	public static final int SPR_VIDEO_PARAM_VIDEO_BROTATE = 7;
	public static final int SPR_VIDEO_PARAM_VIDEO_ROTATEANGLE  = 8;
	public static final int SPR_VIDEO_PARAM_AUDIO_BITRATE = 9;
	public static final int SPR_VIDEO_PARAM_SAMPLE_RATE   = 10;
	public static final int SPR_VIDEO_PARAM_AUDIO_CHANNEL = 11;
	public static final int SPR_VIDEO_PARAM_ENABLE_MPEG4  = 12;
	
	public static final int SPR_VIDEO_PARAM_SRC_WIDTH     = 101;
	public static final int SPR_VIDEO_PARAM_SRC_HEIGHT    = 102;
	
	public static final int R_FILET_PCMS16 = 50; // audio format
	public static final int R_FILET_NV21 = 11;
	
	private int hrec = 0;
	private boolean isPause = true;

	private int src_width = 640;
	private int src_height = 480;
	private int dst_width = 480;
	private int dst_height = 480;
	private int frameRate = 15;
	private int videoFormat = 17;
	private int src_rotate = 90;
	private int dst_rotate = 0;
	private int bRotate = 1;
	
	private int channel = 1;
	private int sampleRate = 16000;
	private int audioFormat = R_FILET_PCMS16;
	
	private int vframes = 0;
	private int aframes = 0;
	private int audio_total_size = 0;
	
	private int percent = 0;
	
	private long timeStampStart = 0;
	private long lastPauseTime = 0;
	
//	public NativeAveRecord()
//	{
//		hrec = NativeRecordCreate();
//	}
	
	// 设置视频数据源的分辨率,即capture输出帧的分辨率（裁剪之前的分辨率）. 如640x480
	public int SPRREC_SetVideoSrcSize(int src_width, int src_height)
	{
		int ret = 0;
		this.src_width = src_width;
		this.src_height = src_height;
		return ret;
	}

	// 设置生成录制视频的分辨率，即裁剪之后的分辨率. 如480x480
	public int SPRREC_SetVideoDstSize(int dst_width, int dst_height)
	{
		int ret = 0;
		this.dst_width = dst_width;
		this.dst_height = dst_height;
		return ret;
	}
	
	public int SPRREC_SetVideoFrameRate(int frameRate)
	{
		int ret = 0;
		this.frameRate = frameRate;
		return ret;
	}
	
	public int SPRREC_SetVideoFormat(int videoFormat)
	{
		int ret = 0;
		this.videoFormat = videoFormat;
		return ret;
	}
	
	public int SPRREC_SetSrcVideoRotate(int src_rotate)
	{
		int ret = 0;
		this.src_rotate = src_rotate;
		return ret;
	}
	
	public int SPRREC_SetDstVideoRotate(int dst_rotate)
	{
		int ret = 0;
		this.dst_rotate = dst_rotate;
		return ret;
	}
	
	public int SPRREC_SetAudioChannel(int channel)
	{
		int ret = 0;
		this.channel = channel;
		return ret;
	}
	
	public int SPRREC_SetAudioSampleRate(int sampleRate)
	{
		int ret = 0;
		this.sampleRate = sampleRate;
		return ret;
	}
	
	public int SPRREC_SetAudioFormat(int audioFormat) // must be R_FILET_PCMS16
	{
		int ret = 0;
		this.audioFormat = audioFormat;
		return ret;
	}
	
	public int SPRREC_Start(String outFileName)
	{
		int ret = 0;
		int rotate = 0;
		
		Log.e("cxx", "Start NativeRecordCreate");
		
		percent = 0;
		
		hrec = NativeSprRecCreate();

		rotate = dst_rotate-src_rotate;
		rotate = (rotate>0)?rotate:(360+rotate);
		if (hrec != 0)
		{
			Log.e("cxx", "Start SetParam[00]");
			
			ret = SetParam(SPR_VIDEO_PARAM_ENABLE_MPEG4,  1);
			ret = SetParam(SPR_VIDEO_PARAM_SRC_WIDTH,     src_width);
			ret = SetParam(SPR_VIDEO_PARAM_SRC_HEIGHT,    src_height);
			ret = SetParam(SPR_VIDEO_PARAM_WIDTH,         dst_width);
			ret = SetParam(SPR_VIDEO_PARAM_HEIGHT,        dst_height);
			ret = SetParam(SPR_VIDEO_PARAM_FPS,           frameRate);
			ret = SetParam(SPR_VIDEO_PARAM_VIDEO_BITRATE, dst_width*dst_height*frameRate*8/8);
			ret = SetParam(SPR_VIDEO_PARAM_AUDIO_BITRATE, sampleRate*channel*2*8/8);
			ret = SetParam(SPR_VIDEO_PARAM_SAMPLE_RATE,   sampleRate);
			ret = SetParam(SPR_VIDEO_PARAM_VIDEO_BROTATE, bRotate);
			ret = SetParam(SPR_VIDEO_PARAM_VIDEO_ROTATEANGLE,   rotate);
			ret = SetParam(SPR_VIDEO_PARAM_AUDIO_CHANNEL, channel);

			Log.e("cxx", "Start SetParam[99]");
		}
		
		if (hrec != 0)
		{
			Log.e("cxx", "Start NativeRecordOpen[00]");
			ret = NativeSprRecOpen(hrec, outFileName);
			Log.e("cxx", "Start NativeRecordOpen[99]");
		}

		if (hrec != 0)
		{
			this.isPause = false;
		}
		
		if (hrec != 0)
		{
			Log.e("cxx", "Start NativeRecordStart [00]");
			ret = NativeSprRecStart(hrec);
			Log.e("cxx", "Start NativeRecordStart [99]");
		}

		Log.e("cxx", "Start [99]");
		return ret;
	}
	
	public int SPRREC_Stop()
	{
		int ret = 0;

		this.isPause = true;
		
		if (hrec != 0)
		{
			ret = NativeSprRecStop(hrec);
			ret = NativeSprRecClose(hrec);
			ret = NativeSprRecDelete(hrec);
			percent = 256;
			hrec = 0;
		}
		return ret;
	}
	
	public int SPRREC_Pause(boolean isPause)
	{
		int ret = 0;
		if (hrec != 0)
		{
			if (this.isPause != isPause)
			{
				if (isPause)
				{
					lastPauseTime = System.currentTimeMillis();
				}
				else
				{
					timeStampStart += System.currentTimeMillis() - lastPauseTime;
				}
				this.isPause = isPause;
			}
		}
		return ret;
	}
	
	public boolean getPauseStatus(){
	    return isPause;
	}
	
	public int SPRREC_PushVideoFrame(byte[] data, long timeStamp)
	{
		int ret = 0;

		if (hrec != 0 && !isPause)
		{
			if (vframes == 0 && aframes == 0 && timeStampStart == 0)
			{
				timeStampStart = timeStamp;
			}

			timeStamp -= timeStampStart;
			
//			if (timeStamp - timeStampStart > 8000)
//			{
//				this.Stop();
//				return 0;
//			}
		}
		if (hrec != 0 && !isPause)
		{
			ret = NativeSprRecPushVideoFrame(hrec, data, timeStamp);
			vframes++;
			Log.e("cxx", "java video frames=" + vframes + "time=" + timeStamp);
		}
		return ret;
	}
	
	public int SPRREC_PushAudioFrame(short[] data, int size, long timeStamp)
	{
		int ret = 0;
		if (hrec != 0 && !isPause)
		{
			timeStamp -= 1000*size/channel/sampleRate/2;
			if (timeStamp < 0)
			{
				timeStamp = 0;
			}
		}
		if (hrec != 0 && !isPause)
		{
			if (vframes == 0 && aframes == 0 && timeStampStart == 0)
			{
				timeStampStart = timeStamp;
			}

			timeStamp -= timeStampStart;
			
//			if (timeStamp - timeStampStart > 8000)
//			{
//				this.Stop();
//				return 0;
//			}
		}
		if (hrec != 0 && !isPause)
		{
			ret = NativeSprRecPushAudioFrame(hrec, data, size, timeStamp);
			aframes++;
			audio_total_size += size;
			Log.e("cxx", "java audio frames=" + aframes + "-" + audio_total_size + "time=" + timeStamp);
		}
		return ret;
	}
	
	private int SetParam(int paramType, int value)
	{
		int ret = 0;
		if (hrec != 0)
		{
			ret = NativeSprRecSetParam(hrec, paramType, value);
		}
		return 0;
	}
	
	public int SPRREC_GetPercent()
	{
		if (hrec != 0)
		{
			percent = NativeSprRecGetPercent(hrec);
		}
		return percent;
	}
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecCreate();
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecOpen(int hrec, String outFileName);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecStart(int hrec);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecStop(int hrec);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecClose(int hrec);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecDelete(int hrec);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecSetParam(int hrec, int paramType, int value);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecGetPercent(int hrec);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecPushVideoFrame(int hrec, byte[] data, long timeStamp);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprRecPushAudioFrame(int hrec, short[] data, int size, long timeStamp);

    static
    {
    	String libname = "TranscodingJni";
    	
        try
        {
        	libname = "dynload";
            System.loadLibrary(libname);
            
        	libname = "WeaverVideoCodec";
            System.loadLibrary(libname);

            //libname = "megvii";
            //System.loadLibrary(libname);
            
            libname = "SprAV";
            System.loadLibrary(libname);
            
            libname = "SprTranscoding";
            System.loadLibrary(libname);
            
        	Log.e("test", "System.loadLibrary after  - " + libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
            unsatisfiedlinkerror.printStackTrace();
        	Log.e("test", "System.loadLibrary error!!!! - " + libname);
        }
    }
}
