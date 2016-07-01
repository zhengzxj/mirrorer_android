package com.videorecorder.listener;

import android.hardware.Camera;
import android.view.Surface;

import com.videorecorder.widget.ProgressView;

public interface CameraBridgeListener {
    Camera getCamera();
    
    int cameraId();
    
    Surface getSurface();
    
    ArHandler getMrHandler();
    
    //SeekbarListener getSeekbarListener();
    
    ProgressView getProgressViewListener();
    
    RecordControllerStateListener getRecordButtonControllerListener();
    
    void delayActionEnable();
    
    void delayActionUnable();
    
    int getOrientation();
}