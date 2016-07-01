package com.videorecorder.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.mirrorer.R;


public class ProgressDialog extends Dialog {
    private final int MAX_VALUE = 255;
    private Context mContext;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String content = "0/"+MAX_VALUE;
    
    public ProgressDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }
    
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }
    
    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.include_progressbar, null);
        setContentView(view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.progressText);
        mProgressBar.setMax(MAX_VALUE);
        mTextView.setText(content);
    }
    
    public void update(int progress) {
        mProgressBar.setProgress(progress);
        content = (progress*100/MAX_VALUE) + "%";
        if (mTextView != null) {
            mTextView.setText(content);
        }
    }
}
