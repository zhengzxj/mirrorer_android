package com.videorecorder.listener;


public interface ArHandler {
    void startRecord();
    
    void stopRecord();
    
    //void pauseRecord();
    
    void finishRecord();
    
    void reset();
    
    boolean isRecording();
    
    void flashbackData();
    
    void addBridgeListener(CameraBridgeListener listener);
    
    void pushVideoFrame(byte[] data, long timeStemp);
}
