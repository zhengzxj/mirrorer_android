package com.videorecorder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.smart.mirrorer.R;
import com.videorecorder.listener.UIRecordVideoController;


public class RecordButtonView extends ImageView implements View.OnClickListener {
    private final int MAX_TIME_INTERVAL = 500;
    private long currentTime, lastTime;
    private boolean isRecord = true;
    private UIRecordVideoController recordVideoController;
    
    public RecordButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    public void setRecordVideoController(UIRecordVideoController recordVideoController) {
        this.recordVideoController = recordVideoController;
        setOnClickListener(this);
        //setOnTouchListener(this);
    }
    
    public void setRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (!recordVideoController.isRecording()) {
            setImageResource(R.drawable.ic_pause);
            recordVideoController.start();
        }else{
            setImageResource(R.drawable.ic_play);
            recordVideoController.stop();
        }
    }
    
    
    
//    
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        // TODO Auto-generated method stub
//        
//        if (isRecord) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                currentTime = System.currentTimeMillis();
//                long timestamp = currentTime - lastTime;
//                lastTime = currentTime;
//                if (timestamp < MAX_TIME_INTERVAL) {
//                    return true;
//                }
//                if (!recordVideoController.isRecording()) {
//                    setImageResource(R.drawable.ic_record_stop);
//                    recordVideoController.start();
//                }
//                break;
//                case MotionEvent.ACTION_UP:
//                setImageResource(R.drawable.ic_record_start);
//                if (recordVideoController.isRecording()) {
//                    recordVideoController.stop();
//                }
//                break;
//                
//                default:
//                break;
//            }
//        }
//        return true;
//    }
    
}
