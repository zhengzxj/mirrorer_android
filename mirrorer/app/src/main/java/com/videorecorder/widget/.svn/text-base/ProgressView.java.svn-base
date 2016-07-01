package com.videorecorder.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.videorecorder.TimeProgressBar;
import com.videorecorder.listener.FalshBackCallBack;
import com.videorecorder.listener.SeekbarListener;
import com.videorecorder.listener.TimerUpdateListener;
import com.videorecorder.listener.UIRecordVideoController;
import com.videorecorder.util.CameraSetting;

import java.util.Timer;
import java.util.TimerTask;

public class ProgressView extends TimeProgressBar implements SeekbarListener {
    
    private UIRecordVideoController uiRecordVideoController;
    private TimerUpdateListener timerUpdateListener;
    
    private int curTime;
    private State currentState = State.STOP;
    
    private boolean isUpdate = true;
    
    private final int changeTime = 50;
    
    public static enum State {
        START(0x1), PAUSE(0x2), STOP(0x3);
        
        static State mapIntToValue(final int stateInt) {
            for (State value : State.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }
            return PAUSE;
        }
        
        private int mIntValue;
        
        State(int intValue) {
            mIntValue = intValue;
        }
        
        int getIntValue() {
            return mIntValue;
        }
    }
    
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if(currentState == State.START){
                curTime += changeTime;
                setRecordTime(curTime);  
            }
        };
        
    };
    
    private Timer timer;
    private TimerTask task;
    
    private void initTimer(){
        if(timer == null){
            timer = new Timer();
        }
        if(task == null){
            task = new TimerTask()
            {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    handler.sendEmptyMessage(1);
                }
            };
        }
        timer.schedule(task, changeTime, changeTime);
    }
    
    public ProgressView(Context context)
    {
        super(context);
        initTimer();
    }
    
    public ProgressView(Context context, AttributeSet attrs){
        super(context, attrs);
        initTimer();
    }
    
    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        initTimer();
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        currentState = State.START;
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        currentState = State.STOP;
        //curTime = 0;
    }

//    @Override
//    public void pause() {
//        // TODO Auto-generated method stub
//        //currentState = State.PAUSE;
//    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        currentState = State.START;
    }

    @Override
    public long getRecordTime() {
        // TODO Auto-generated method stub
        return getProgress();
    }

    @Override
    public void setRecordTime(long time) {
        // TODO Auto-generated method stub
        setProgress((int) time);
        timerUpdateListener.updateTime(time);
        if (time <= CameraSetting.Preference_Camera_Upload_Time_Limit) {
            if (CameraSetting.Preference_Camera_Upload_Min_Time_Limit <= time) {
                uiRecordVideoController.enableCommitView();
            }
        } else {
            if (uiRecordVideoController != null) {
                uiRecordVideoController.stop();
                //uiRecordVideoController.commit();
            }
        }
    }

    @Override
    public void flashback(long token, FalshBackCallBack callBack) {
        curTime = 0;
        setProgress(0);
        stop();
    }
    
    public void setUiRecordVideoController(UIRecordVideoController uiRecordVideoController) {
        this.uiRecordVideoController = uiRecordVideoController;
    }
    
    public void setTimerUpdateListener(TimerUpdateListener timeUpdateListener) {
        this.timerUpdateListener = timeUpdateListener;
    }
    
}
