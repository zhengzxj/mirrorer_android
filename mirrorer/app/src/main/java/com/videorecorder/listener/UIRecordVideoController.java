package com.videorecorder.listener;

import com.videorecorder.widget.SurfaceViewLayout.Action;

public interface UIRecordVideoController {
    void flashbackActionFromBottom();
    
    void start();
    
    //void pause();
    
    void stop();
    
    void openLocalFile();
    
    void commit();
    
    void setCanCommit(boolean flag);
    
    void setActionType(Action type);
    
    boolean isRecording();
    
    void enableCommitView();
    
    void addRecordControllerStateListener(RecordControllerStateListener listener);
    
}
