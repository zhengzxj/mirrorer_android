package com.smart.mirrorer.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smart.mirrorer.R;


public class MyLoadingDialog extends Dialog {

    private Context context;
    private TextView tvMsg;
    private TextView tvRightMsg;

    public MyLoadingDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dailog_loading_layout);
        tvMsg = (TextView) findViewById(R.id.emptyText);
        tvRightMsg = (TextView) findViewById(R.id.loading_text);
    }

    public void setMsg(String msg) {
        if (tvMsg != null) {
            tvMsg.setVisibility(View.VISIBLE);
            tvMsg.setText(msg);
        }
    }

    public void setRightMsg(String msg) {
        if (tvRightMsg != null) {
            tvRightMsg.setVisibility(View.VISIBLE);
            tvRightMsg.setText(msg);
        }
    }

    public void setRightGone() {
        if (tvRightMsg != null) {
            tvRightMsg.setVisibility(View.GONE);
        }
    }

}
