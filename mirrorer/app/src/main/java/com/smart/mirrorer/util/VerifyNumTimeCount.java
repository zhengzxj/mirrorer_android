package com.smart.mirrorer.util;

import java.lang.ref.SoftReference;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.smart.mirrorer.R;

public class VerifyNumTimeCount extends CountDownTimer {

    private SoftReference<TextView> mTextView;

    public VerifyNumTimeCount(TextView textView, long millisInFuture, long countDownInterval)
    {
        super(millisInFuture, countDownInterval);
        mTextView = new SoftReference<TextView>(textView);
    }

    public void setmTextView(SoftReference<TextView> mTextView) {
        this.mTextView = mTextView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mTextView.get() != null) {
            mTextView.get().setEnabled(false);
            int size = DeviceConfiger.dp2sp(12);
            mTextView.get().setTextSize(size);
            String showText = mTextView.get().getContext().getString(R.string.verifynumTime, Long.toString(millisUntilFinished / 1000));
            mTextView.get().setTextColor(Color.parseColor("#1fbad6"));
            mTextView.get().setText(showText);
        }
    }

    @Override
    public void onFinish() {
        if (mTextView.get() != null) {
            int size = DeviceConfiger.dp2sp(14);
            mTextView.get().setTextSize(size);
            mTextView.get().setText("重新发送验证码");
            mTextView.get().setTextColor(Color.parseColor("#1fbad6"));
            mTextView.get().setEnabled(true);
        }
    }

}