package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.VerifyNumTimeCount;
import com.smart.mirrorer.util.mUtil.MyTextUtil;

/**
 * Created by lzm on 16/3/25.
 */
public class InputPswActivity extends BaseActivity implements View.OnClickListener {
    private TextView mVolidGetTv;
    private EditText input_psw_edit;
    private EditText input_psw_again_edit;
    private Intent intent;
    private String telphone;
    private String code;
    private String password;
    private String password2;
    private KeyboardService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_psw_layout);

        service = new KeyboardService(this);
        intent = getIntent();

        telphone = intent.getStringExtra("telphone");
        code = intent.getStringExtra("code");

        initView();

    }

    private void initView() {

        ImageView backIv = (ImageView) findViewById(R.id.input_psw_done);
        backIv.setOnClickListener(this);

        input_psw_edit = (EditText)findViewById(R.id.input_psw_edit);
        MyTextUtil.countLimit(input_psw_edit,15,"密码不能超过15位");
        input_psw_again_edit = (EditText)findViewById(R.id.input_psw_again_edit);
        MyTextUtil.countLimit(input_psw_again_edit,15,"密码不能超过15位");

        mVolidGetTv = (TextView)findViewById(R.id.count_down_tv);
        VerifyNumTimeCount verifyNumCount = new VerifyNumTimeCount(mVolidGetTv, 60000, 1000);
        verifyNumCount.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_psw_done:
                service.hideKeyboard(v);
                password = input_psw_edit.getText().toString();
                password2 = input_psw_again_edit.getText().toString();
                if (!TextUtils.isEmpty(password)&&password.equals(password2))
                {
                    Intent intent = new Intent(InputPswActivity.this,InputUserInfoActivity.class);
                    intent.putExtra("telphone",telphone);
                    intent.putExtra("code",code);
                    intent.putExtra("password",password);
                    startActivity(intent);
                    finish();
                }else
                {
                    TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"密码不一致请重新输入");
                }

                break;
            case R.id.input_back_iv:
                finish();
                break;
        }
    }
}
