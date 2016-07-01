package com.videorecorder.listener;

public interface RecordControllerStateListener {
    void enableBackView();
    
    void unableBackView();
    
    void enableCommitView();
    
    void unableCommitView();
    
    void enableRecordButtonView();
    
    void unableRecordButtonView();
    
    void downRecordButtonView();
    
    void onReset();
}
