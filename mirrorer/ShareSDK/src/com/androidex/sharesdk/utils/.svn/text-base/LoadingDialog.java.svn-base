package com.androidex.sharesdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidex.sharesdk.R;


public class LoadingDialog extends Dialog {

    private Activity context;
    private TextView tvMsg;
    private String msg;

    public LoadingDialog(Activity context)
    {
        super(context, R.style.loading_dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.share_dailog_loading_layout);
        tvMsg = (TextView) findViewById(R.id.emptyText);
        setMsg(msg);
    }

    public void setMsg(String msg) {
        this.msg = msg;
        if (tvMsg != null) {
            tvMsg.setText(msg);
            if (!TextUtils.isEmpty(msg)) {
                tvMsg.setVisibility(View.VISIBLE);
            } else {
                tvMsg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e("wuzhenlin", "LoadingDialog backpressed");
        context.finish();
    }
}
