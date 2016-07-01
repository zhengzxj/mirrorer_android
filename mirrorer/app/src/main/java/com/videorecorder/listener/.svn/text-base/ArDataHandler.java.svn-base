package com.videorecorder.listener;

import java.util.ArrayList;
import java.util.List;

import com.videorecorder.bean.RecordParam;
import com.videorecorder.bean.TempVideoFileParam;
import com.videorecorder.util.CameraSetting;
import com.videorecorder.util.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.CamcorderProfile;

public class ArDataHandler {
    public final static int AUDIOFORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public final static int CHANNEL_ID = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    public final static int SAMPLERATE = 16000;
    public int mSrcRotate = 90;
    public int mDstRotate = 0;
    //
    private List<TempVideoFileParam> tempVfileParams = new ArrayList<TempVideoFileParam>();
    protected boolean isRecording = false;
    protected AudioRecord mRecorder;
    protected RecordParam mRecordParam;
    protected CameraBridgeListener mBridgeListener;
    protected int mCameraId;
    protected Context context;
    
    @SuppressLint("NewApi")
    public ArDataHandler(Context context) {
        this.context = context;
        this.mCameraId = mBridgeListener != null ? mBridgeListener.cameraId() : 0;
        // int bufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNEL_ID,
        // AUDIOFORMAT);
        // this.mRecorder = new AudioRecord(AudioSource.MIC, SAMPLERATE,
        // CHANNEL_ID, AUDIOFORMAT, bufferSize);
        mRecordParam = new RecordParam();
//        int quality = CameraSetting.Preference_Camera_Quality;
//        CamcorderProfile fProfile = null;
//        if (CameraSetting.minVersionSDK()) {
//            fProfile = CamcorderProfile.get(mCameraId, quality);
//        } else {
//            fProfile = CamcorderProfile.get(quality);
//        }
        mRecordParam.setMaxDuration(CameraSetting.Preference_Camera_Upload_Time_Limit);
    }
    
    public RecordParam getmRecordParam() {
        return mRecordParam;
    }
    
    public void setmRecordParam(RecordParam mRecordParam) {
        this.mRecordParam = mRecordParam;
    }
    
    public List<TempVideoFileParam> getTempVfileParams() {
        return tempVfileParams;
    }
    
    public String[] getPathsFromList() {
        String[] results = null;
        if (tempVfileParams != null && !tempVfileParams.isEmpty()) {
            results = new String[tempVfileParams.size()];
            for (int i = 0; i < tempVfileParams.size(); i++) {
                TempVideoFileParam param = tempVfileParams.get(i);
                results[i] = param.getFileName();
            }
        }
        return results;
    }
    
    public boolean isEmpty() {
        if (tempVfileParams != null && !tempVfileParams.isEmpty()) {
            return false;
        }
        return true;
    }
    
    protected void addTempParams(TempVideoFileParam param) {
        tempVfileParams.add(param);
    }
    
    protected TempVideoFileParam getTempParams() {
        int index = tempVfileParams.size() - 1;
        if (0 <= index) {
            TempVideoFileParam param = tempVfileParams.get(index);
            return param;
            
        }
        return null;
    }
    
    protected void removeTempParams() {
        int index = tempVfileParams.size() - 1;
        if (0 <= index) {
            TempVideoFileParam param = tempVfileParams.get(index);
            FileUtils.delTempVfile(context, param.getFileName());
            tempVfileParams.remove(index);
            if (mBridgeListener != null && mBridgeListener.getProgressViewListener() != null) {
                int _index = tempVfileParams.size();
                long tempTime = 0;
                if (0 < _index) {
                    TempVideoFileParam _param = tempVfileParams.get(_index - 1);
                    tempTime = _param.getTimeNodes();
                }
                mBridgeListener.getProgressViewListener().setRecordTime(tempTime);//..setProgress((int)tempTime);
                if (tempTime <= CameraSetting.Preference_Camera_Upload_Min_Time_Limit) {
                    if (mBridgeListener != null && mBridgeListener.getRecordButtonControllerListener() != null) {
                        mBridgeListener.getRecordButtonControllerListener().unableCommitView();
                    }
                }
            }
        }
        if (tempVfileParams.size() <= 0) {
            if (mBridgeListener != null && mBridgeListener.getRecordButtonControllerListener() != null) {
                mBridgeListener.getRecordButtonControllerListener().onReset();
            }
        }
    }
    
    protected void clearTempParams() {
        FileUtils.delVfileToAll(context, tempVfileParams);
    }
    
}
