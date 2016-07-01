package com.videorecorder.handler;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.videorecorder.listener.CameraBridgeListener;

import java.lang.ref.SoftReference;


public class PrepareTimeCountHandler extends CountDownTimer {
    
    private SoftReference<TextView> mTextView;
    private CameraBridgeListener mCbListener;
    
    public PrepareTimeCountHandler(CameraBridgeListener cameraBridgeListener, TextView textView, long millisInFuture,
            long countDownInterval) {
        super(millisInFuture, countDownInterval);
        mTextView = new SoftReference<TextView>(textView);
        this.mCbListener = cameraBridgeListener;
    }
    
    public void setmTextView(SoftReference<TextView> mTextView) {
        this.mTextView = mTextView;
    }
    
    @Override
    public void onTick(long millisUntilFinished) {
        if (mTextView.get() != null) {
            mTextView.get().setVisibility(View.VISIBLE);
            String showText = Long.toString(millisUntilFinished / 1000);
            mTextView.get().setText(showText);
            executeAnim(mTextView.get());
        }
    }
    
    @Override
    public void onFinish() {
        if (mTextView.get() != null) {
            mTextView.get().setVisibility(View.GONE);
        }
        if (mCbListener != null) {
            mCbListener.getMrHandler().startRecord();
        }
        if (mCbListener != null && mCbListener.getRecordButtonControllerListener() != null) {
            mCbListener.getRecordButtonControllerListener().downRecordButtonView();
        }
    }
    
    private void executeAnim(final TextView view) {
        view.clearAnimation();
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.zoom_anim);
        view.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener()
        {
            
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                if ("1".equals(view.getText())) {
                    if (mTextView.get() != null) {
                        mTextView.get().setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}