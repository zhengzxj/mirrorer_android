package com.videorecorder.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.smart.mirrorer.R;


public class LargeProgressDialog extends Dialog {
    private Context mContext;
    private ProgressBar mProgressBar;
    
    public LargeProgressDialog(Context context) {
        super(context, R.style.Dialog_edittext);
        this.mContext = context;
        init();
    }
    
    public LargeProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        init();
    }
    
    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.include_large_progressbar, null);
        setContentView(view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }
}
