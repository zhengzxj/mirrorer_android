package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/4/27.
 * 忘记密码:输入手机号,发送验证码
 */
public class NewForgetPswActivity1 extends BaseActivity implements View.OnClickListener {

    private ImageView forget_psw_back_iv;
    private TextView get_code_btn;
    private KeyboardService service;
    private EditText register_phone_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_layout1);

        service = new KeyboardService(this);

        forget_psw_back_iv = (ImageView)findViewById(R.id.forget_psw_back_iv);
        forget_psw_back_iv.setOnClickListener(this);
        get_code_btn = (TextView)findViewById(R.id.get_code_btn);
        get_code_btn.setOnClickListener(this);
        register_phone_edit = (EditText)findViewById(R.id.register_phone_edit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.forget_psw_back_iv:
                finish();
                break;
            case R.id.get_code_btn:
                getVolidCode();
                break;
        }
    }

    private void getVolidCode() {
        String phoneNum = register_phone_edit.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            TipsUtils.showShort(getApplicationContext(), "请输入手机号");
            return;
        }

        String tag_json_obj = "json_obj_valicode_req";
        showLoadDialog();

        JSONObject codeParams = new JSONObject();
        try {
            codeParams.put("telphone", phoneNum);
            codeParams.put("action", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST, Urls.URL_VOLID_CODE, codeParams,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissLoadDialog();
                        boolean isOk = GloabalRequestUtil.isRequestOk(response);
                        Log.e("lzm", "code_text=" + response.toString());
                        if (isOk) {
//                            VerifyNumTimeCount verifyNumCount = new VerifyNumTimeCount(mVolidGetTv, 60000, 1000);
//                            verifyNumCount.start();
                            TipsUtils.showShort(getApplicationContext(), "发送验证码成功");
                            Intent intent = new Intent(NewForgetPswActivity1.this, NewForgetPswActivity2.class);
                            intent.putExtra("telphone",register_phone_edit.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            TipsUtils.showShort(getApplicationContext(), "发送验证码失败");
                        }

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
//                VolleyLog.d("lzm", "Error: " + error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
