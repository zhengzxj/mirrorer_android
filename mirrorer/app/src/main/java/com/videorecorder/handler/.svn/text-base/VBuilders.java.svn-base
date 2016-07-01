package com.videorecorder.handler;

import com.videorecorder.bean.MaterialParam;
import com.videorecorder.handler.VideoBuilders.VideoPlayBtnStateListener;
import com.videorecorder.listener.OnEditVideoCompleteListener;

/**
 * 视频编辑
 * @author jhw4830
 *
 */
public interface VBuilders {
    /**
     * 加水印
     */
    void buildWatermark(MaterialParam param, int handlerId);
    
    /**
     * 加音效
     * 
     * @param param
     * @param handlerId
     */
    void buildVoice(MaterialParam param, int handlerId);
    
    /**
     * 是否原声
     * @param orisound
     * @param handlerId
     */
    void isOpen_OriSound(int orisound, int handlerId);
    
    /**
     * 合成视频
     * @param completeListener
     * @param handlerId
     */
    void publishVideo(OnEditVideoCompleteListener completeListener, int handlerId);
    
    /**
     * 中断
     */
    void interrupt();
    
    /**
     * 
     * @param listener
     */
    void setVideoPlayBtnStateListener(VideoPlayBtnStateListener listener);
    
    /**
     * 
     * @param handlerId
     */
    void play(int handlerId);
    
    /**
     * 
     */
    void stop();
    
}
