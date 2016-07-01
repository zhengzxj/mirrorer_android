package com.bdj.video.build;

import android.util.Log;



public class NativeSprEditConnect
{
 public static final int SPR_VIDEO_PARAM_WIDTH = 1;
 public static final int SPR_VIDEO_PARAM_HEIGHT = 2;
 public static final int SPR_VIDEO_PARAM_ENABLE_MPEG4 = 12;
 public static final int SPR_VIDEO_PARAM_COMBINE_MODE = 13;
 public static final int SPR_VIDEO_PARAM_PERCENT = 14;
 
 
 private int hedt = 0;
 private int hcom = 0;

 private int width = 0;
 private int height = 0;
 private int mode = 0;
 private int percent = 0;
 private boolean isPause = false;
 
 public NativeSprEditConnect()
 {
 }

 public int SPREDIT_SetVideoSize(int width, int height)
 {
  int ret = 0;
  this.width = width;
  this.height = height;
  return ret;
 }
 public int SPREDIT_SetMode(int mode)
 {
  int ret = 0;
  this.mode = mode;
  return ret;
 }

 public int SPREDIT_GetPercent()
 {
  int per[] = new int[]{0};
  int ret = CombineGetParam(SPR_VIDEO_PARAM_PERCENT, per);
  if (ret >= 0)
  {
   percent = per[0];
  }
  return percent;
 }
 
 /**
  * 暂不使用
  * @param outFileName
  * @param inFileName
  * @param inFileNum
  * @return
  */
 public static int SPREDIT_ConnectFile(String outFileName, String[] inFileName, int inFileNum)
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_ConnectFile [00]");
  
  ret = NativeSprEditConnectFile(outFileName, inFileName, inFileNum);
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_ConnectFile [99]");
  
  return 0;
 }
 
 public static int SPREDIT_ConnectFileNew(String outFileName, String[] inFileName, int inFileNum)
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_ConnectFileNew [00]");
  
  ret = NativeSprEditConnectFileNew(outFileName, inFileName, inFileNum);
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_ConnectFileNew [99]");
  
  return ret;
 }
 
  public static int SPREDIT_GetTotalFrameNum()
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_GetTotalFrameNum [00]");
  
  ret = NativeSprEditGetTotalFrameNum();
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_GetTotalFrameNum [99]");
  
  return ret;
 }
  
 public static int SPREDIT_CombineFile(String outFileName, String[] inFileName, int inFileNum, int mode)
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_CombineFile [00]");
  
  ret = NativeSprEditCombineFile(outFileName, inFileName, inFileNum, mode);
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.SPREDIT_CombineFile [99]");
  
  return 0;
 }
 

 public int SPREDIT_CombineStart(String outFileName, String[] inFileName, int inFileNum)
 {
  int ret = 0;
  
  Log.e("AVE_LOG_cxx", "Start SPREDIT_CombineStart[00]");
  
  percent = 0;
  
  hcom = CombineCreate();
  
  if (hcom != 0)
  {
   Log.e("AVE_LOG_cxx", "Start SetParam[00]");
   
   //ret = CombineSetParam(SPR_VIDEO_PARAM_ENABLE_MPEG4, 1);
   ret = CombineSetParam(SPR_VIDEO_PARAM_WIDTH, width);
   ret = CombineSetParam(SPR_VIDEO_PARAM_HEIGHT, height);
   ret = CombineSetParam(SPR_VIDEO_PARAM_COMBINE_MODE, mode);

   Log.e("AVE_LOG_cxx", "Start SetParam[99]");
  }
  
  if (hcom != 0)
  {
   Log.e("AVE_LOG_cxx", "Start CombineOpen[00]");
   ret = CombineOpen(outFileName, inFileName, inFileNum);
   Log.e("AVE_LOG_cxx", "Start CombineOpen[99]");
  }

  if (hcom != 0)
  {
   this.isPause = false;
  }
  
  if (hcom != 0)
  {
   Log.e("AVE_LOG_cxx", "Start CombineStart [00]");
   ret = CombineStart();
   Log.e("AVE_LOG_cxx", "Start CombineStart [99]");
  }

  Log.e("AVE_LOG_cxx", "Start SPREDIT_CombineStart[99]");
  return ret;
 }
 
 public int SPREDIT_CombineStop()
 {
  int ret = 0;

  this.isPause = true;
  
  if (hcom != 0)
  {
   ret = CombineStop();
   ret = CombineClose();
   ret = CombineDelete();
   percent = 256;
   hcom = 0;
  }
  return ret;
 }

 private int CombineCreate()
 {
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineCreate [00]");
  
  if (mode == 2)
  {
   hcom = NativeSprEditConnectCreate();
  }
  else
  {
   hcom = NativeSprEditCombineCreate();
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineCreate [99]");
  
  return hcom;
 }
 private int CombineOpen(String outFileName, String[] inFileName, int inFileNum)
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineOpen [00]");

  if (mode == 2)
  {
   ret = NativeSprEditConnectOpen(hcom, outFileName, inFileName, inFileNum);
  }
  else
  {
   ret = NativeSprEditCombineOpen(hcom, outFileName, inFileName, inFileNum);
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineOpen [99]");
  
  return ret;
 }
//	private int AVEEdit_CombineProcess(int hcom);
 private int CombineStart()
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineStart [00]");

  if (mode == 2)
  {
   ret = NativeSprEditConnectStart(hcom);
  }
  else
  {
   ret = NativeSprEditCombineStart(hcom);
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineStart [99]");
  
  return ret;
 }
 private int CombineStop()
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineStop [00]");

  if (mode == 2)
  {
   ret = NativeSprEditConnectStop(hcom);
  }
  else
  {
   ret = NativeSprEditCombineStop(hcom);
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineStop [99]");
  
  return ret;
 }
 private int CombineClose()
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineClose [00]");

  if (mode == 2)
  {
   ret = NativeSprEditConnectClose(hcom);
  }
  else
  {
   ret = NativeSprEditCombineClose(hcom);
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineClose [99]");
  
  return ret;
 }
 private int CombineDelete()
 {
  int ret = 0;

  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineDelete [00]");

  if (mode == 2)
  {
   ret = NativeSprEditConnectDelete(hcom);
  }
  else
  {
   ret = NativeSprEditCombineDelete(hcom);
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineDelete [99]");
  
  return ret;
 }
 
 private int CombineSetParam(int paramType, int value)
 {
  int ret = 0;
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineSetParam [00]");
  
  if (hcom != 0)
  {
   if (mode == 2)
   {
    ret = NativeSprEditConnectSetParam(hcom, paramType, value);
   }
   else
   {
    ret = NativeSprEditCombineSetParam(hcom, paramType, value);
   }
  }
  
  Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineSetParam [99]");
  
  return 0;
 }
 
 private int CombineGetParam(int paramType, int[] value)
 {
  int ret = 0;
  
  //Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineGetParam [00]");
  
  if (hcom != 0)
  {
   if (mode == 2)
   {
    ret = NativeSprEditConnectGetParam(hcom, paramType, value);
   }
   else
   {
    ret = NativeSprEditCombineGetParam(hcom, paramType, value);
   }
  }
  
  //Log.e("AVE_LOG_cxx", "NativeAveEditConnect.AVEEdit_CombineGetParam [99]");
  
  return 0;
 }

 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectFile(String outFileName, String[] inFileName, int inFileNum);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineFile(String outFileName, String[] inFileName, int inFileNum, int mode);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectCreate();
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditGetTotalFrameNum();
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectOpen(int hcom, String outFileName, String[] inFileName, int inFileNum);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectFileNew(String outFileName, String[] inFileName, int inFileNum);
//	private static native int NativeSprEditConnectProcess(int hcom);
@SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectStart(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectStop(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectClose(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectDelete(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectSetParam(int hcom, int paramType, int value);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditConnectGetParam(int hcom, int paramType, int[] value);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineCreate();
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineOpen(int hcom, String outFileName, String[] inFileName, int inFileNum);
//	private static native int NativeSprEditCombineProcess(int hcom);
@SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineStart(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineStop(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineClose(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineDelete(int hcom);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineSetParam(int hcom, int paramType, int value);
 @SuppressWarnings("JniMissingFunction")
 private static native int NativeSprEditCombineGetParam(int hcom, int paramType, int[] value);
 
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
            
         Log.e("cxx", "System.loadLibrary after - " + libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror)
        {
         Log.e("cxx", "System.loadLibrary error!!!! - " + libname);
        }
    }
}
