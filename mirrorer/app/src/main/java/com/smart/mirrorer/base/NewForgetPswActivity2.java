package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.VerifyNumTimeCount;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/3/25.
 * 忘记密码:校验验证码
 */
public class NewForgetPswActivity2 extends BaseActivity implements View.OnClickListener {
    private TextView count_down_tv;
    private EditText input_code_edit;
    private Intent intent;
    private String telphone;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_layout2);

        intent = getIntent();
        telphone = intent.getStringExtra("telphone");

        initView();

    }

    private void initView() {
        input_code_edit = (EditText)findViewById(R.id.input_code_edit);
        input_code_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = input_code_edit.getText().toString();
                if(code.length()==4)
                {
                    checkVolidCode(code);
                }
            }
        });
        count_down_tv = (TextView)findViewById(R.id.count_down_tv);
        count_down_tv.setOnClickListener(this);
        VerifyNumTimeCount verifyNumCount = new VerifyNumTimeCount(count_down_tv, 60000, 1000);
        verifyNumCount.start();

        findViewById(R.id.input_back_iv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.count_down_tv:
                if ("重新发送验证码".equals(count_down_tv.getText().toString()))
                {
                    VerifyNumTimeCount verifyNumCount = new VerifyNumTimeCount(count_down_tv, 60000, 1000);
                    verifyNumCount.start();
                }
                break;
            case R.id.input_back_iv:
                finish();
                break;
        }
    }
    /**
     * 检查验证码
     * @param codeNum
     */
    private void checkVolidCode(String codeNum) {
        String tag_json_obj = "json_obj_check_valicode_req";
        showLoadDialog();

        JSONObject codeParams = new JSONObject();
        try {
            codeParams.put("code", codeNum);
            codeParams.put("act", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST, Urls.URL_PSW_RESET, codeParams,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        L.i("校验验证码回调:"+response.toString());
                        dismissLoadDialog();
                        boolean isOk = GloabalRequestUtil.isRequestOk(response);

                        if (isOk) {
                            L.i("校验验证码回调:isOk-true");
                            Intent intent = new Intent(NewForgetPswActivity2.this,NewForgetPswActivity3.class);
                            code = input_code_edit.getText().toString();
                            intent.putExtra("telphone",telphone);
                            intent.putExtra("code",code);
                            NewForgetPswActivity2.this.startActivity(intent);
                            finish();
                        } else {
                            L.i("校验验证码回调:isOk-false");
                            TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                        }

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                L.i("校验验证码回调:"+error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
