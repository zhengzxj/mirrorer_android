package com.videorecorder.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.bdj.video.build.NativeSprEditConnect;
import com.bdj.video.build.NativeSprFilter;
import com.smart.mirrorer.util.AppFilePath;
import com.videorecorder.bean.MaterialParam;
import com.videorecorder.bean.MultiFilter;
import com.videorecorder.handler.VBuilders;
import com.videorecorder.handler.VideoBuilders.VideoPlayBtnStateListener;
import com.videorecorder.listener.OnEditVideoCompleteListener;

import java.nio.ByteBuffer;


public class MakeVideoUtils implements VBuilders {
    private static MakeVideoUtils instance;
    private MultiFilter multiFilter;
    private Context mContext;
    private String tempPath;// 临时文件路径
    private String sourceFile;// 原文件路径
    private String finalFile;
    private NativeSprEditConnect mNativeSprEditConnect;//pcl 20150318

	private MakeVideoUtils(Context context) {
        this.mContext = context;
        multiFilter = new MultiFilter();
        // multiFilter.setOutFileName(FileUtils.createTempVfile(mContext));
        // 指定格式  mp4
        multiFilter.setoFormat(NativeSprFilter.R_FILET_MP4);
        multiFilter.setiFormat(NativeSprFilter.R_FILET_MP4);
        // TODO ? 只支持音效和水印，所以是2
        multiFilter.setFilterNum(2);
        int[] filterType = new int[multiFilter.getFilterNum()];
        filterType[0] = NativeSprFilter.R_FILTER_NONE;
        filterType[1] = NativeSprFilter.R_FILTER_NONE;
        multiFilter.setFilterType(filterType);
        int[] subType = new int[multiFilter.getFilterNum()];
        // TODO ?
        subType[0] = 1;
        subType[1] = 1;
        multiFilter.setSubType(subType);
        //pcl 20150325
        int[] rcWidth = { 480, 480 };
        int[] rcHeight = { 480, 480 };
//        int[] rcWidth = { 240, 240 };
//        int[] rcHeight = { 240, 240 };
        //end
        // 资源数据
        byte[][] rcData = new byte[multiFilter.getFilterNum()][];
        multiFilter.setRcData(rcData);
        multiFilter.setInMarkFileName(new String[multiFilter.getFilterNum()]);
        multiFilter.setRcWidth(rcWidth);
        multiFilter.setRcHeight(rcHeight);
        int[] pState = new int[4];
        int[] pEncoder = new int[1];
        
        // TODO ? 
        pState[0] = 3; // flagNum
        pState[1] = 0; // bMuxing
        pState[2] = 1; // bOrgSound
        pState[3] = 1; // bVideoCallBack
        
        pEncoder[0] = 0;
        
        multiFilter.setpState(pState);
        multiFilter.setpEncoder(pEncoder);
        multiFilter.setReserve(null);
    }
    
    public static MakeVideoUtils getInstance(Context context) {
        if (instance == null) {
            instance = new MakeVideoUtils(context);
        }
        return instance;
    }
    
    public MultiFilter getMultiFilter() {
        return multiFilter;
    }
    
    public void setMultiFilter(MultiFilter multiFilter) {
        this.multiFilter = multiFilter;
    }
    
    public String getTempPath() {
        return tempPath;
    }
    
    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
    
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    public String getSourceFile() {
        return sourceFile;
    }
    
    public String getFinalFile() {
        return finalFile;
    }
    
    public void setFinalFile(String finalFile) {
        this.finalFile = finalFile;
    }
    
    @Override
    public void publishVideo(OnEditVideoCompleteListener completeListener, final int handlerId) {
        multiFilter.setInFileName(getTempPath());
//        String outFile = FileUtils.createFile(mContext, FileUtils.FILE_CVIDEO_DIRECTORY,
//                System.currentTimeMillis() + FileUtils.FILE_FORMAT);
        String outFile = AppFilePath.getVoiceFile(System.currentTimeMillis() + FileUtils.FILE_FORMAT).getAbsolutePath();
        setFinalFile(outFile);
        multiFilter.setOutFileName(outFile);
        int[] pState = new int[4];
        int[] pEncoder = new int[4];
        
        pState[0] = 3; // flagNum
        // 声音
        pState[1] = 1; // bMuxing
        
        pState[2] = multiFilter.getpState()[2]; // bOrgSound
        pState[3] = 0; // bVideoCallBack
        
        pEncoder[0] = 3; // flagNum
        pEncoder[1] = 900 * 1024; // Video bitrate
        pEncoder[2] = 50000; // Audio bitrate
        pEncoder[3] = 2; // Audio chan
        multiFilter.setpState(pState);
        multiFilter.setpEncoder(pEncoder);
        new Thread()
        {
            @Override
            public void run() {
                NativeSprFilter.SPRFILTER_MultiFilter_File(multiFilter.getOutFileName(), multiFilter.getoRotate(),
                        multiFilter.getoFormat(), multiFilter.getInFileName(), multiFilter.getiRotate(),
                        multiFilter.getiFormat(), multiFilter.getInMarkFileName(), multiFilter.getFilterNum(),
                        multiFilter.getFilterType(), multiFilter.getSubType(), multiFilter.getRcData(),
                        multiFilter.getRcWidth(), multiFilter.getRcHeight(), multiFilter.getpState(),
                        multiFilter.getpEncoder(), multiFilter.getReserve(), handlerId);
            }
        }.start();
    }
    
    @Override
    public void buildWatermark(MaterialParam param, final int handlerId) {
        // TODO Auto-generated method stub
        String resName = param.getResourceName();
        int type = param.getType();
        multiFilter.setInFileName(getTempPath());
        multiFilter.setOutFileName(FileUtils.createTempVfile(mContext));
        // 无水印
        if ("0".equals(param.getId())) {
            multiFilter.getFilterType()[1] = NativeSprFilter.R_FILTER_NONE;
            multiFilter.getSubType()[1] = 1;
            multiFilter.getRcData()[1] = null;
            multiFilter.getInMarkFileName()[1] = null;
            // 有水印,png gif
        } else {
            if (type == Constant.TYPE_WATERMARK) {
                multiFilter.getFilterType()[1] = NativeSprFilter.R_FILTER_WATERMARK;
            } else if (type == Constant.TYPE_FRAME) {
                multiFilter.getFilterType()[1] = NativeSprFilter.R_FILTER_PHOTOFRAME;
            }
            String format = param.getFormat();
            if ("png".equals(format)) {
                multiFilter.getSubType()[1] = 1;
                Bitmap bm = ImageUtils.getBitmapFromRaw(mContext, resName);
                multiFilter.getRcWidth()[1] = bm.getWidth();
                multiFilter.getRcHeight()[1] = bm.getHeight();
                byte[] array = new byte[multiFilter.getRcWidth()[1] * multiFilter.getRcHeight()[1] * 4];
                ByteBuffer buffer = ByteBuffer.wrap(array);
                bm.copyPixelsToBuffer(buffer);
                buffer.position(0);
                bm.recycle();
                bm = null;
                multiFilter.getRcData()[1] = array;
            } else if ("mp4".equals(format)) {
                multiFilter.getSubType()[1] = 2;
            }
            String fileName = FileUtils.getFilePathFromSdcard(mContext, resName, format,
                    FileUtils.FILE_WATERMARK_DIRECTORY);
            multiFilter.getInMarkFileName()[1] = fileName;
        }
        new Thread()
        {
            @Override
            public void run() {
                NativeSprFilter.SPRFILTER_MultiFilter_File(multiFilter.getOutFileName(), multiFilter.getoRotate(),
                        multiFilter.getoFormat(), multiFilter.getInFileName(), multiFilter.getiRotate(),
                        multiFilter.getiFormat(), multiFilter.getInMarkFileName(), multiFilter.getFilterNum(),
                        multiFilter.getFilterType(), multiFilter.getSubType(), multiFilter.getRcData(),
                        multiFilter.getRcWidth(), multiFilter.getRcHeight(), multiFilter.getpState(),
                        multiFilter.getpEncoder(), multiFilter.getReserve(), handlerId);
            }
        }.start();
    }
    
    @Override
    public void buildVoice(MaterialParam param, final int handlerId) {
        // TODO Auto-generated method stub
        String resName = param.getResourceName();
        multiFilter.setInFileName(getTempPath());
        multiFilter.setOutFileName(FileUtils.createTempVfile(mContext));
        // 原声
        if ("0".equals(param.getId())) {
            multiFilter.getFilterType()[0] = NativeSprFilter.R_FILTER_NONE;
            multiFilter.getSubType()[0] = 1;
            multiFilter.getInMarkFileName()[0] = null;
        } else {
            multiFilter.getFilterType()[0] = NativeSprFilter.R_FILTER_MIXAUD;
            multiFilter.getSubType()[0] = 1;
            String format = param.getFormat();
            String fileName = FileUtils
                    .getFilePathFromSdcard(mContext, resName, format, FileUtils.FILE_VOICE_DIRECTORY);
            multiFilter.getInMarkFileName()[0] = fileName;
        }
        new Thread()
        {
            @Override
            public void run() {
                NativeSprFilter.SPRFILTER_MultiFilter_File(multiFilter.getOutFileName(), multiFilter.getoRotate(),
                        multiFilter.getoFormat(), multiFilter.getInFileName(), multiFilter.getiRotate(),
                        multiFilter.getiFormat(), multiFilter.getInMarkFileName(), multiFilter.getFilterNum(),
                        multiFilter.getFilterType(), multiFilter.getSubType(), multiFilter.getRcData(),
                        multiFilter.getRcWidth(), multiFilter.getRcHeight(), multiFilter.getpState(),
                        multiFilter.getpEncoder(), multiFilter.getReserve(), handlerId);
            }
        }.start();
    }
    
    @Override
    public void isOpen_OriSound(int orisound, final int handlerId) {
        // TODO Auto-generated method stub
        multiFilter.setInFileName(getTempPath());
        multiFilter.setOutFileName(FileUtils.createTempVfile(mContext));
        multiFilter.getpState()[2] = orisound;
        new Thread()
        {
            @Override
            public void run() {
                NativeSprFilter.SPRFILTER_MultiFilter_File(multiFilter.getOutFileName(), multiFilter.getoRotate(),
                        multiFilter.getoFormat(), multiFilter.getInFileName(), multiFilter.getiRotate(),
                        multiFilter.getiFormat(), multiFilter.getInMarkFileName(), multiFilter.getFilterNum(),
                        multiFilter.getFilterType(), multiFilter.getSubType(), multiFilter.getRcData(),
                        multiFilter.getRcWidth(), multiFilter.getRcHeight(), multiFilter.getpState(),
                        multiFilter.getpEncoder(), multiFilter.getReserve(), handlerId);
            }
        }.start();
    }
    
    @Override
    public void interrupt() {
        // TODO Auto-generated method stub
        
    }
    
    public void destroyInstance() {
        instance = null;
    }
    
    @Override
    public void setVideoPlayBtnStateListener(VideoPlayBtnStateListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void play(final int handlerId) {
        // TODO Auto-generated method stub
        multiFilter.setInFileName(getTempPath());
        multiFilter.setOutFileName(FileUtils.createTempVfile(mContext));
        if(!SourceFrom.getInstance().isCameraOrLocal())
        {
        	mNativeSprEditConnect = new NativeSprEditConnect();//pcl 20150318
        	String videoPath = multiFilter.getOutFileName();// "/storage/sdcard0/BDJ_VIDEO/video_temp/14266491541810.mp4";//FileUtils.createTempVfile(context);
            
            // 生成文件, getPathsFromList 录制文件集合
        	String ll = multiFilter.getInFileName()  ;//getSDPath() + "/" +"slow.mp4";
        	String [] list = {ll};
            int result = mNativeSprEditConnect.SPREDIT_ConnectFileNew(videoPath, list,1);
            android.util.Log.e("results", "code:" + result);
      
        }
        new Thread()
        {
            @Override
            public void run() {
                // 
   
                NativeSprFilter.SPRFILTER_MultiFilter_File(multiFilter.getOutFileName(), multiFilter.getoRotate(),
                        multiFilter.getoFormat(), multiFilter.getInFileName(), multiFilter.getiRotate(),
                        multiFilter.getiFormat(), multiFilter.getInMarkFileName(), multiFilter.getFilterNum(),
                        multiFilter.getFilterType(), multiFilter.getSubType(), multiFilter.getRcData(),
                        multiFilter.getRcWidth(), multiFilter.getRcHeight(), multiFilter.getpState(),
                        multiFilter.getpEncoder(), multiFilter.getReserve(), handlerId);
            }
        }.start();
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        
    }
    
}
