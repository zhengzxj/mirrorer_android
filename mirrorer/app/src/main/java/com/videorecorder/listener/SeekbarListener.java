package com.videorecorder.listener;

public interface SeekbarListener {
    void start();
    
    void stop();
    
    //void pause();
    
    void resume();
    
    long getRecordTime();
    
    void setRecordTime(long time);
    
    void flashback(long token, FalshBackCallBack callBack);
}
