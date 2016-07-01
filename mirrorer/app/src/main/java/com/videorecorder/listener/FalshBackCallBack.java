package com.videorecorder.listener;

public interface FalshBackCallBack {
    void onFailure();
    
    void onPrepare(long token);
    
    void onSuccess();
}
