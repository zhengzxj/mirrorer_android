package com.bdj.video.build;

import android.util.Log;


public class NativeSprFilter
{
	public static final int R_FILTER_NONE          = 0;
	// 加水印格式
	public static final int R_FILTER_WATERMARK     = 10;  // int
	public static final int R_FILTER_PHOTOFRAME    = 11;  // int
	public static final int R_FILTER_MOSAIC        = 12;  // Mosaic
	public static final int R_FILTER_FILTERGLASS   = 16;
	public static final int R_FILTER_FILTERBEAUTY  = 17;
	public static final int R_FILTER_MIXAUD        = 60; // afilter
	public static final int R_FILTER_AVTHEME       = 80; // avfiltering
	
	public static final int R_FILET_NONE         = 0; // video+audio
	public static final int R_FILET_MP4          = 1;		// video+audio
	public static final int R_FILET_YUV420       = 10;	// raw video
	public static final int R_FILET_NV21         = 11;
	public static final int R_FILET_NV12         = 12;
	public static final int R_FILET_RGBA         = 13;
	public static final int R_FILET_AYUV         = 14;
	public static final int R_FILET_PNG          = 30;	// pic
	public static final int R_FILET_PCMS16	     = 50;	// audio
	public static final int R_FILET_PCMFLT32     = 51;
	public static final int R_FILET_PCMS32       = 52;
	public static final int R_FILET_WAV          = 61;
	public static final int R_FILET_AAC          = 62;

	private String waterFileName = "/sdcard/time_240320.dat";
	private String frameFileName = "/sdcard/green_240320.dat";

	public static int SPRFILTER_SetNStop(int status)
	{
		int ret = 0;
		ret = NativeSprSetNStop(status);
		return ret;
	}
	
	public static int SPRFILTER_GetPercent()
	{	
		int ret = 0;
		//Log.e("lly", "NativeGetPercent[00]");
		ret = NativeSprGetPercent();
		//Log.e("lly", "NativeGetPercent[99]");
		return ret;
	}

	public static byte[] SPRFILTER_GetFilterVideoFrame(int[] width, int[] height, int[] format)
	{
		int[] reqBufLen = new int[]{0};
		
		byte[] outVideoFrame = new byte[320*240];
		
		reqBufLen[0] = outVideoFrame.length;

		Log.e("lly", "NativeMultiNuiGetVideoFrame [1100]");
		int ret = NativeMultiSprGetVideoFrame(outVideoFrame, reqBufLen, width, height, format);
		Log.e("lly", "NativeMultiNuiGetVideoFrame [1199]" + reqBufLen[0] + " + " + outVideoFrame.length);
		
		if (ret < 0 && reqBufLen[0] > 0 && outVideoFrame.length != reqBufLen[0])
		{
			Log.e("lly", "NativeMultiNuiGetVideoFrame [2200]");
			outVideoFrame = new byte[reqBufLen[0]];
			
			ret = NativeMultiSprGetVideoFrame(outVideoFrame, reqBufLen, width, height, format);

			Log.e("lly", "NativeMultiNuiGetVideoFrame [2299]");
		}
		
		if (ret < 0 || width[0] <= 0 || height[0] <= 0 || outVideoFrame.length <= 0)
		{
			Log.e("lly", "ret=" + ret + "width=" + width[0] + "height=" + height[0]);
			return null;
		}
		
		return outVideoFrame;
	}

	public static short[] SPRFILTER_GetFilterAudioFrame(int[] channel, int[] sample_rate, int[] format)
	{
		int[] reqBufLen = new int[]{0};
		
		short[] outAudioFrame = new short[2048];
		
		reqBufLen[0] = outAudioFrame.length * 2;

		Log.e("cxx", "AVE_GetFilterAudioFrame [1100]");
		int ret = NativeMultiSprGetAudioFrame(outAudioFrame, reqBufLen, channel, sample_rate, format);
		Log.e("cxx", "AVE_GetFilterAudioFrame [1199]" + reqBufLen[0] + " + " + outAudioFrame.length);
		
		if (ret < 0 && reqBufLen[0] > 0 && outAudioFrame.length != reqBufLen[0]/2)
		{
			Log.e("cxx", "AVE_GetFilterAudioFrame [2200]");
			outAudioFrame = new short[reqBufLen[0]/2];
			
			ret = NativeMultiSprGetAudioFrame(outAudioFrame, reqBufLen, channel, sample_rate, format);

			Log.e("cxx", "AVE_GetFilterAudioFrame [2299]");
		}
		
		if (ret < 0 || channel[0] <= 0 || sample_rate[0] <= 0 || outAudioFrame.length <= 0)
		{
			Log.e("cxx", "ret=" + ret + "channel=" + channel[0] + "sample_rate=" + sample_rate[0]);
			return null;
		}
		
		return outAudioFrame;
	}
	
	//**************************************************************/
	// 鍑芥暟锛欰VE_MultiNuiTranscodingFile
	// 鍑芥暟鍔熻兘锛氬褰曞埗鐨勯煶瑙嗛鏂囦欢鍔犲叆涓�涓垨澶氫釜鐗规晥銆�
	// 鍙傛暟璇存槑锛�
	//		outFileName:	杈撳嚭鏂囦欢
	//		inFileName:		杈撳叆鏂囦欢
	//		filterNum:		鐗规晥涓暟
	//		filterType:		鐗规晥绫诲瀷
	//		subType:		鐗规晥瀛愮被鍨�
	//		rcData:			璧勬簮鏁版嵁
	//		rcWidth:		璧勬簮鏁版嵁鐨勫
	//		rcHeight:		璧勬簮鏁版嵁鐨勯珮
	//		reserve:		淇濈暀
	//		--reserve[0]=flagNum			: 鏍囪涓暟
	//		--reserve[1]=muxingFlag 		: 鏄惁鐢熸垚鏂囦欢鐨勬爣璁�
	//		--reserve[2]=videoCallBackFlag	: 鏄惁鍥炶皟瑙嗛甯х殑鏍囪
	//		--reserve[3...N]=reserve		: 鍏跺畠
	//**************************************************************/
/*	public static int AVE_MultiFilter_File(String outFileName, int oRotate, int oFormat, String inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[], int reserve[])
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";
		int bMuxing = 1;
		int bVideoCallBack = 0;
		int []encoderParameters = new int[3];
		int flagNum = 0;
		if (reserve != null && reserve.length >= 1)
		{
			flagNum = reserve[0];
		}
		if (flagNum >= 1 && reserve.length >= 2)
		{
			bMuxing = reserve[1];
		}
		if (flagNum >= 2 && reserve.length >= 3)
		{
			bVideoCallBack = reserve[2];
		}
		if (flagNum >= 3 && reserve.length >= 4)
		{
			encoderParameters[0] = reserve[3];
		}
		if (flagNum >= 4 && reserve.length >= 5)
		{
			encoderParameters[1] = reserve[4];
		}
		if (flagNum >= 5 && reserve.length >= 6)
		{
			encoderParameters[2] = reserve[5];
		}
		int ret = 0;
		ret = NativeMultiNuiTranscodingFile(outFileName2.getBytes(), oRotate, oFormat, inFileName2.getBytes(), iRotate, iFormat, inMarkFileName, filterNum, filterType, subType, rcData, rcWidth, rcHeight, reserve);
		return ret;
	}
*/	
	//**************************************************************/
	// 鍑芥暟锛欰VE_MultiNuiTranscodingFile
	// 鍑芥暟鍔熻兘锛氬褰曞埗鐨勯煶瑙嗛鏂囦欢鍔犲叆涓�涓垨澶氫釜鐗规晥銆�
	// 鍙傛暟璇存槑锛�
	//		outFileName:	杈撳嚭鏂囦欢
	//		inFileName:		杈撳叆鏂囦欢
	//		filterNum:		鐗规晥涓暟
	//		filterType:		鐗规晥绫诲瀷
	//		subType:		鐗规晥瀛愮被鍨�
	//		rcData:			璧勬簮鏁版嵁
	//		rcWidth:		璧勬簮鏁版嵁鐨勫
	//		rcHeight:		璧勬簮鏁版嵁鐨勯珮
	//		bMuxing:		鏄惁鐢熸垚鐩爣鏂囦欢
	//      bOrgSound:      鏄惁寮�鍚師澹�
	//		bVideoCallBack:	鏄惁鍥炶皟瑙嗛甯ф暟鎹�
	//**************************************************************/
	public static int SPRFILTER_MultiFilter_File(String outFileName, int oRotate, int oFormat, String inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[], int pState[], int pEncoder[], int reserve[], int handlerId)
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";

//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterNum=" + filterNum);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterType[0]=" + filterType[0]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterType[1]=" + filterType[1]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 inMarkFileName[0]=" + inMarkFileName[0]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 inMarkFileName[1]=" + inMarkFileName[1]);
		
		// pState
		// flagNum, bMuxing, bOrgSound, bVideoCallBack
		// pEncode
		// flagNum, fp, VideoBitrate, AudioBitrate
		
		int ret = 0;
		if(pState != null && pEncoder != null)
			Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]"+pState[0]+pState[1]+pState[2]+pEncoder[0]);
		
		ret = NativeMultiSprTranscodingFile(outFileName2.getBytes(), oRotate, oFormat, inFileName2.getBytes(), iRotate, iFormat, inMarkFileName, filterNum, filterType, subType, rcData, rcWidth, rcHeight, pState, pEncoder, reserve, handlerId);
		return ret;
	}

	public static int SPRFILTER_MultiFilter_FileFrame(
	        String outFileName, int oRotate, int oFormat, 
	        String inFileName, int iRotate, int iFormat, 
	        String[] inMarkFileName, int filterNum, int filterType[], 
	        int subType[], byte[][] rcData, int rcWidth[], 
	        int rcHeight[], int pState[], int pEncoder[], int reserve[],
	        int start_frame[], int end_frame[])
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";

//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterNum=" + filterNum);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterType[0]=" + filterType[0]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 filterType[1]=" + filterType[1]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 inMarkFileName[0]=" + inMarkFileName[0]);
//		Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]1234 inMarkFileName[1]=" + inMarkFileName[1]);
		
		// pState
		// flagNum, bMuxing, bOrgSound, bVideoCallBack
		// pEncode
		// flagNum, fp, VideoBitrate, AudioBitrate
		
		int ret = 0;
		if(pState != null && pEncoder != null)
			Log.e("AVE_LOG_zl", "AVE_MultiFilter_File[start]"+pState[0]+pState[1]+pState[2]+pEncoder[0]);
		
		ret = NativeMultiSprTranscodingFileFrame(outFileName2.getBytes(), oRotate, oFormat, inFileName2.getBytes(), iRotate, iFormat, inMarkFileName, filterNum, filterType, subType, rcData, rcWidth, rcHeight, pState, pEncoder, reserve,
					start_frame, end_frame);
		return ret;
	}
	//**************************************************************/
	// 鍑芥暟锛欰VE_MultiNuiTranscodingFile
	// 鍑芥暟鍔熻兘锛氬褰曞埗鐨勯煶瑙嗛鏂囦欢鍔犲叆涓�涓垨澶氫釜鐗规晥銆�
	// 鍙傛暟璇存槑锛�
	//		outFileName:	杈撳嚭鏂囦欢
	//		inFileName:		杈撳叆鏂囦欢
	//		filterNum:		鐗规晥涓暟
	//		filterType:		鐗规晥绫诲瀷
	//		subType:		鐗规晥瀛愮被鍨�
	//		rcData:			璧勬簮鏁版嵁
	//		rcWidth:		璧勬簮鏁版嵁鐨勫
	//		rcHeight:		璧勬簮鏁版嵁鐨勯珮
	//**************************************************************/
/*	public static int AVE_MultiFilter_File(String outFileName, int oRotate, int oFormat, String inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[])
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";
		int ret = 0;
		ret = NativeMultiNuiTranscodingFile(outFileName2.getBytes(), oRotate, oFormat, inFileName2.getBytes(), iRotate, iFormat, inMarkFileName, filterNum, filterType, subType, rcData, rcWidth, rcHeight, 1, 0, null);
		return ret;
	}
*/
	//**************************************************************/
	// 鍑芥暟锛欰VE_MultiNuiTranscodingFile
	// 鍑芥暟鍔熻兘锛氬褰曞埗鐨勯煶瑙嗛鏂囦欢鍔犲叆涓�涓垨澶氫釜鐗规晥骞跺洖璋冩瘡甯ц棰戝抚锛屼絾涓嶇敓鎴愮洰鏍囨枃浠躲��
	// 鍙傛暟璇存槑锛�
	//		outFileName:	杈撳嚭鏂囦欢
	//		inFileName:		杈撳叆鏂囦欢
	//		filterNum:		鐗规晥涓暟
	//		filterType:		鐗规晥绫诲瀷
	//		subType:		鐗规晥瀛愮被鍨�
	//		rcData:			璧勬簮鏁版嵁
	//		rcWidth:		璧勬簮鏁版嵁鐨勫
	//		rcHeight:		璧勬簮鏁版嵁鐨勯珮
	//**************************************************************/
/*	public static int AVE_MultiFilter_CallBack(String outFileName, int oRotate, int oFormat, String inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[])
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";
		int ret = 0;
		ret = NativeMultiNuiTranscodingFile(outFileName2.getBytes(), oRotate, oFormat, inFileName2.getBytes(), iRotate, iFormat, inMarkFileName, filterNum, filterType, subType, rcData, rcWidth, rcHeight, 0, 1, null);
		return ret;
	}
/*	
	public static int AVE_MultiFilter_File(String outFileName, String inFileName, int filterNum, int filterType[], int subType[], String[] rcFileName)
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";
		
		byte[][] rcData = new byte[filterNum][];
		int[] rcWidth = new int[filterNum];
		int[] rcHeight = new int[filterNum];

		for (int i = 0; i < filterNum; i++)
		{
			Bitmap bm = BitmapFactory.decodeFile(rcFileName[i]);
			
			rcWidth[i] = bm.getWidth();
			rcHeight[i] = bm.getHeight();
			rcData[i] = new byte[rcWidth[i] * rcHeight[i] * 4];

			ByteBuffer buffer = ByteBuffer.wrap(rcData[i]);
			bm.copyPixelsToBuffer(buffer);
		}
		
		int ret = 0;
		ret = NativeMultiNuiTranscodingFile(outFileName2.getBytes(), inFileName2.getBytes(), filterNum, filterType, subType, rcData, rcWidth, rcHeight);
		return ret;
	}
	
	public int AVE_NuiTranscodingFile(String outFileName, String inFileName, int filterType, byte[] rcData, int rcWidth, int rcHeight)
	{
		String inFileName2 = inFileName + "\0";
		String outFileName2 = outFileName + "\0";
		int ret = 0;
		//ret = TranscodingFile(this, main_type, sub_type, infile_name.getBytes(), outfile_name.getBytes());
		//ret = NativeNuiTranscodingFile(outFileName2.getBytes(), inFileName2.getBytes(), filterType, rcData, rcWidth, rcHeight);
		return ret;
	}

	public int WaterFilterTranscodingFile(int main_type, int sub_type, String outfile_name, String infile_name)
	{
		String infile_name2 = infile_name + "\0";
		String tmpfile_name2 = waterFileName + "\0";
		String outfile_name2 = outfile_name + "\0";
		int ret = 0;
		//ret = TranscodingFile(this, main_type, sub_type, infile_name.getBytes(), outfile_name.getBytes());
		ret = TranscodingFile2(this, 10, 1, infile_name2.getBytes(), tmpfile_name2.getBytes(), outfile_name2.getBytes());
		return ret;
	}

	public int FrameFilterTranscodingFile(int main_type, int sub_type, String outfile_name, String infile_name)
	{
		String infile_name2 = infile_name + "\0";
		String tmpfile_name2 = frameFileName + "\0";
		String outfile_name2 = outfile_name + "\0";
		int ret = 0;
		//ret = TranscodingFile(this, main_type, sub_type, infile_name.getBytes(), outfile_name.getBytes());
		ret = TranscodingFile2(this, 11, 1, infile_name2.getBytes(), tmpfile_name2.getBytes(), outfile_name2.getBytes());
		return ret;
	}
	
	public int MultFilterTranscodingFile(String outfile_name, String infile_name)
	{
		String infile_name2 = infile_name + "\0";
		String outfile_name2 = outfile_name + "\0";
		int ret = 0;
		//ret = TranscodingFile(this, main_type, sub_type, infile_name.getBytes(), outfile_name.getBytes());
		ret = MultFilterTranscodingFile(this, infile_name2.getBytes(), outfile_name2.getBytes());
		return ret;
	}
*/
	//private static native int NativeNuiTranscodingFile(byte[] outFileName, byte[] inFileName, int filterType, byte[] rcData, int rcWidth, int rcHeight);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeMultiSprTranscodingFile(byte[] outFileName, int oRotate, int oFormat, byte[] inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[], int pState[], int pEncoder[], int reserve[],
	                    int handler);
	@SuppressWarnings("JniMissingFunction")
	public static native int NativeMultiSprInit();
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeMultiSprTranscodingFileFrame(byte[] outFileName, int oRotate, int oFormat, byte[] inFileName, int iRotate, int iFormat, String[] inMarkFileName, int filterNum, int filterType[], int subType[], byte[][] rcData, int rcWidth[], int rcHeight[], int pState[], int pEncoder[], int reserve[],
				int[] start_frame, int[] end_frame);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeMultiSprGetVideoFrame(byte[] outBuf, int[] reqBufLen, int[] width, int[] height, int[] format);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeMultiSprGetAudioFrame(short[] outBuf, int[] reqBufLen, int[] channel, int[] sample_rate, int[] format);
	@SuppressWarnings("JniMissingFunction")
    private static native int SprTranscodingFile(NativeSprFilter pThis, int main_type, int sub_type, byte[] infile_name, byte[] outfile_name);
	@SuppressWarnings("JniMissingFunction")
    private static native int SprTranscodingFile2(NativeSprFilter pThis, int main_type, int sub_type, byte[] infile_name, byte[] tmpfile_name, byte[] outfile_name);
	@SuppressWarnings("JniMissingFunction")
	private static native int SprMultFilterTranscodingFile(NativeSprFilter pThis, byte[] infile_name, byte[] outfile_name);
	@SuppressWarnings("JniMissingFunction")
	private static native int NativeSprSetNStop(int handler);
	@SuppressWarnings("JniMissingFunction")
    private static native int NativeSprGetPercent();

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
            
        	Log.e("cxx", "System.loadLibrary after  - " + libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
        	Log.e("cxx", "System.loadLibrary error!!!! - " + libname);
        }
    }
}
