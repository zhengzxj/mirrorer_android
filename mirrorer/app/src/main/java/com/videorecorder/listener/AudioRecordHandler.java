package com.videorecorder.listener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

import com.bdj.video.build.NativeSprEditConnect;
import com.bdj.video.build.NativeSprFilter;
import com.bdj.video.build.NativeSprRec;
import com.smart.mirrorer.R;
import com.videorecorder.bean.TempVideoFileParam;
import com.videorecorder.exception.AudioRecordInitException;
import com.videorecorder.util.BdjUtils;
import com.videorecorder.util.CameraSetting;
import com.videorecorder.util.DialogTools;
import com.videorecorder.util.DialogTools.DialogClickEventListener;
import com.videorecorder.util.FileUtils;
import com.videorecorder.util.MakeVideoUtils;
import com.videorecorder.widget.LargeProgressDialog;
import com.videorecorder.widget.ProgressDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AudioRecordHandler extends ArDataHandler implements ArHandler {
    private final int MAKE_VIDEO_PROGRESS = 1;
    private final int RECORD_VIDEO_START = 2;
    private final int RECORD_VIDEO_STOP = 6;
    private final int RECORD_VIDEO_PAUSE = 7;
    private final int MEDIA_UNMOUNTED_MSG = 3;
    private final int AUDIO_INIT_FAILED = 4;
    private final int PUBLISH_VIDEO = 5;
    
    public static final int COMPLETE_RECORD_VIDEO = 8;
    
    private int mOrientation;
    private NativeSprRec mNativeSprRec;
    private NativeSprEditConnect mNativeSprEditConnect;
    private AudioRecordThread recordThread;
    private Thread mThread;
    private String videoPath;
    private boolean isProgress = true;
    private LargeProgressDialog pDialog;
    private final ExecutorService FULL_TASK_EXECUTOR;
    private long mSignToken;
    
    private Handler completeHandler;//视频录制完成时的监听
    
    public AudioRecordHandler(Context context,Handler mHandler) {
        super(context);
        mNativeSprRec = new NativeSprRec();
        mNativeSprEditConnect = new NativeSprEditConnect();
        pDialog = new LargeProgressDialog(context);
        FULL_TASK_EXECUTOR = Executors.newCachedThreadPool();
        completeHandler = mHandler;
    }
    
    private void initHandler() {
        try {
            // 初始化录音
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }
            int bufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNEL_ID, AUDIOFORMAT);
            short[] buffer = new short[bufferSize];
            mRecorder = new AudioRecord(AudioSource.MIC, SAMPLERATE, CHANNEL_ID, AUDIOFORMAT, bufferSize);
            if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                mRecorder.startRecording();
                boolean enableRecording = false;
                while (isRecording) {
                    if (mRecorder != null) {
                        int bufferResult = mRecorder.read(buffer, 0, bufferSize);
                        // 判断读取到的数据是不是0
                        if (bufferResult == 0) {
                        	throw new AudioRecordInitException("maybe audio record disable");
                        }
                        if (!enableRecording) {
                        	for (int i = 0; i<buffer.length; i++) {
                        		if (buffer[i] != 0) {
                        			enableRecording = true;
                        			break;
                        		}
                        		if (i == (buffer.length -1)) {
                        			throw new AudioRecordInitException("maybe audio record disable");
                        		}
                        	}
                        }
                        long timeStemp = System.currentTimeMillis();
                        if (mNativeSprRec != null) {
                            mNativeSprRec.SPRREC_PushAudioFrame(buffer, bufferResult * 2, timeStemp);
                        }
                    }
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            if (e instanceof AudioRecordInitException) {
            	handler.sendEmptyMessage(AUDIO_INIT_FAILED);
            }
        } finally {
            if (mRecorder != null) {
                if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    mRecorder.stop();
                    mRecorder.release();
                }
                mRecorder = null;
            }
        }
    }
    
    private void initNativeSprRec() {
        mThread = new Thread()
        {
            @Override
            public void run() {
                // 初始化文件
                String fileName = FileUtils.createTempVfile(context);
                if (fileName == null) {
                    stopRecord();
                    handler.sendEmptyMessage(MEDIA_UNMOUNTED_MSG);
                    return;
                }
                getmRecordParam().setOutputFileName(fileName);
                // 初始化编码库
                if (mNativeSprRec == null) {
                    mNativeSprRec = new NativeSprRec();
                }
                // 视频输出480x480，输入680x480
                mNativeSprRec.SPRREC_SetVideoSrcSize(CameraSetting.CAMERA_WIDTH, CameraSetting.CAMERA_HEIGHT);
                mNativeSprRec.SPRREC_SetVideoDstSize(CameraSetting.CAMERA_HEIGHT, CameraSetting.CAMERA_HEIGHT);
                mNativeSprRec.SPRREC_SetAudioSampleRate(SAMPLERATE);
                // 录制格式
                mNativeSprRec.SPRREC_SetVideoFormat(mNativeSprRec.R_FILET_NV21);
                // 计算视频角度
                mOrientation = mBridgeListener.getOrientation();
                // 输入视频角度
                mNativeSprRec.SPRREC_SetSrcVideoRotate(0);
                // 输出视频角度
                mNativeSprRec.SPRREC_SetDstVideoRotate(mOrientation);
                // 音频格式
                mNativeSprRec.SPRREC_SetAudioFormat(mNativeSprRec.R_FILET_PCMS16);
                int mChannel = 0;
                if (mChannel == AudioFormat.CHANNEL_IN_MONO) {
                    mNativeSprRec.SPRREC_SetAudioChannel(2);
                } else {
                    mNativeSprRec.SPRREC_SetAudioChannel(1);
                }
                mNativeSprRec.SPRREC_Start(fileName);
            }
        };
        FULL_TASK_EXECUTOR.execute(mThread);
    }
    
    private void saveTempFileParam() {
        long time = mBridgeListener.getProgressViewListener().getRecordTime();
        String fileName = getmRecordParam().getOutputFileName();
        if (!TextUtils.isEmpty(fileName)) {
            addTempParams(new TempVideoFileParam(time, fileName));
        }
    }
    
    @Override
    public void reset() {
        stopRecord();
        clearTempParams();
        if (mBridgeListener != null && mBridgeListener.getProgressViewListener() != null) {
            //mBridgeListener.getProgressViewListener().stop();
            if (mBridgeListener.getRecordButtonControllerListener() != null) {
                mBridgeListener.getRecordButtonControllerListener().onReset();
            }
        }
    }
    
    @Override
    public boolean isRecording() {
        return isRecording;
    }
    
    @SuppressLint("NewApi")
    @Override
    public void startRecord() {
        // TODO Auto-generated method stub
        // if (mRecorder == null) {
        // return;
        // }
        isRecording = true;
        recordThread = new AudioRecordThread();
        if (BdjUtils.checkVersion(11)) {
            recordThread.executeOnExecutor(FULL_TASK_EXECUTOR);
        } else {
            recordThread.execute();
        }
        initNativeSprRec();
        handler.sendEmptyMessage(RECORD_VIDEO_START);// 更新进度
    }
    
    @Override
    public void stopRecord() {
        // TODO Auto-generated method stub
        if (mNativeSprRec != null) {
            mNativeSprRec.SPRREC_Stop();
        }
        isRecording = false;
        if (recordThread != null) {
            if (mBridgeListener != null && mBridgeListener.getProgressViewListener() != null) {
                mBridgeListener.getProgressViewListener().stop();
                mBridgeListener.delayActionEnable();
            }
            saveTempFileParam();
            recordThread.cancel(true);
            recordThread = null;
        }
    }
    
//    @Override
//    public void pauseRecord() {
//        // TODO Auto-generated method stub
//        if(mNativeSprRec != null){
//            mNativeSprRec.SPRREC_Pause(true);
//        }
//        isRecording = false;
//        if (recordThread != null) {
//            if (mBridgeListener != null && mBridgeListener.getProgressViewListener() != null) {
//                mBridgeListener.getProgressViewListener().stop();
//                mBridgeListener.delayActionEnable();
//            }
//            saveTempFileParam();
//            recordThread.cancel(true);
//            recordThread = null;
//        }
//    }
    
    
    @Override
    public void addBridgeListener(CameraBridgeListener listener) {
        // TODO Auto-generated method stub
        mBridgeListener = listener;
    }
    
    @Override
    public void flashbackData() {
        // TODO Auto-generated method stub
        if (isRecording()) {
            stopRecord();
        }
        
        if (mBridgeListener != null && mBridgeListener.getProgressViewListener() != null) {
            mBridgeListener.getProgressViewListener().flashback(mSignToken, new FalshBackCallBack()
            {
                @Override
                public void onPrepare(long token) {
                    // TODO Auto-generated method stub
                    mSignToken = token;
                }
                
                @Override
                public void onSuccess() {
                    // TODO Auto-generated method stub
                    removeTempParams();
                }
                
                @Override
                public void onFailure() {
                    // TODO Auto-generated method stub
                    
                }
            });
        }
        
    }
    
    @Override
    public void finishRecord() {
        // 合成视频处理
        makeVideo();
    }
    
    @Override
    public void pushVideoFrame(byte[] data, long timeStemp) {
        // TODO Auto-generated method stub
        if (mNativeSprRec != null) {
            mNativeSprRec.SPRREC_PushVideoFrame(data, timeStemp);
        }
    }
    
    private void makeVideo() {
        if (isRecording) {
            stopRecord();
        }
        if (isEmpty()) {
            Toast.makeText(context, "没有发现视频文件,请重新录制", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            pDialog.show();
            pDialog.setCancelable(false);
            handler.sendEmptyMessage(MAKE_VIDEO_PROGRESS);
            Thread makeThread = new Thread()
            {
                @Override
                public void run() {
                	videoPath = FileUtils.createTempVfile(context);
                    isProgress = true;
                    // 生成文件, getPathsFromList 录制文件集合
                    @SuppressWarnings("static-access")
                    int result = mNativeSprEditConnect.SPREDIT_ConnectFileNew(videoPath, getPathsFromList(),
                            getTempVfileParams().size());
                    isProgress = false;
                    handler.sendEmptyMessage(MAKE_VIDEO_PROGRESS);
                }
            };
            makeThread.start();
        } catch (BadTokenException e) {
            e.printStackTrace();
        }
    }
    
    private ProgressDialog progressDialog;
    private int mHandlerId;
    boolean ispublish = false;
    
    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
        	if (msg.what == PUBLISH_VIDEO) {
        		int progress = NativeSprFilter.SPRFILTER_GetPercent();
                if (progress < 255) {
                    if (progressDialog != null) {
                        progressDialog.update(progress);
                    }
                    handler.sendEmptyMessage(PUBLISH_VIDEO);
                } else {
                    ispublish = false;
                    NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    String file = MakeVideoUtils.getInstance(context).getFinalFile();
                    completeHandler.obtainMessage(COMPLETE_RECORD_VIDEO, file).sendToTarget();
                }
        	} else if (msg.what == AUDIO_INIT_FAILED) {
        		stopRecord();
                String text = context.getString(R.string.voice_forbidden_prompt);
    			DialogTools.showCameraExceptionDialog((Activity)context, text, new DialogClickEventListener() {
    				
    				@Override
    				public void onClick(String tag) {
    				    ((Activity)context).finish();
    				}
    			});
        	} else if (msg.what == MEDIA_UNMOUNTED_MSG) {
        		Toast.makeText(context, context.getResources().getString(R.string.media_unmounted_msg), Toast.LENGTH_SHORT)
                .show();
        	} else if (msg.what == RECORD_VIDEO_START) {
        	    Log.d("test","vieo record start, bridgelistener"+mBridgeListener);
                if (mBridgeListener != null) {
                    if (mBridgeListener.getProgressViewListener() != null) {
                        mBridgeListener.getProgressViewListener().start();
                    }
                    if (mBridgeListener.getRecordButtonControllerListener() != null) {
                        mBridgeListener.getRecordButtonControllerListener().enableBackView();
                        mBridgeListener.getRecordButtonControllerListener().enableRecordButtonView();
                    }
                    mBridgeListener.delayActionUnable();
                }
            } else if (msg.what == RECORD_VIDEO_STOP) {
                if (mBridgeListener != null) {
                    if (mBridgeListener.getProgressViewListener() != null) {
                        mBridgeListener.getProgressViewListener().stop();
                    }
                    if (mBridgeListener.getRecordButtonControllerListener() != null) {
                        mBridgeListener.getRecordButtonControllerListener().unableRecordButtonView();
                    }
                    mBridgeListener.delayActionEnable();
                }
            } else if (msg.what == MAKE_VIDEO_PROGRESS) {
                if (!isProgress) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    // TODO ConcurrentModificationException
                    reset();
                    if (CameraSetting.ENBLE_EDIT) {
                        //BdjUtils.startActivity(context, videoPath, EditVideoActivity.class);
                    } else {
                        
                        ispublish = true;
                        MakeVideoUtils.getInstance(context).setTempPath(videoPath);
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setOnKeyListener(new OnKeyListener()
                        {
                            
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (ispublish && keyCode == KeyEvent.KEYCODE_BACK) {
                                    NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                                return false;
                            }
                        });
                        
                        mHandlerId = NativeSprFilter.NativeMultiSprInit();
                        MakeVideoUtils.getInstance(context).publishVideo(null, mHandlerId);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        handler.sendEmptyMessage(PUBLISH_VIDEO);
                    }
                }
            }
        }
    };
    
    private class AudioRecordThread extends AsyncTask<Void, Void, Void> {
        
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            initHandler();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }
    
}
