package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import com.smart.mirrorer.util.mUtil.MyTextUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/3/25.
 * 忘记密码:重置密码
 */
public class NewForgetPswActivity3 extends BaseActivity implements View.OnClickListener {
    private TextView mVolidGetTv;
    private EditText input_psw_edit;
    private EditText input_psw_again_edit;
    private Intent intent;
    private String telphone;
    private String code;
    private String password;
    private String password2;

    private KeyboardService service;
    private TextView tv_commit_change_psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_psw_layout3);

        service = new KeyboardService(this);
        intent = getIntent();

        telphone = intent.getStringExtra("telphone");
        code = intent.getStringExtra("code");

        initView();

    }

    private void initView() {

//        ImageView backIv = (ImageView) findViewById(R.id.input_psw_done);
//        backIv.setOnClickListener(this);

        input_psw_edit = (EditText)findViewById(R.id.input_psw_edit);
        input_psw_again_edit = (EditText)findViewById(R.id.input_psw_again_edit);
        MyTextUtil.countLimit(input_psw_edit,15,"密码不能超过15个字符");
        MyTextUtil.countLimit(input_psw_again_edit,15,"密码不能超过15个字符");

        tv_commit_change_psw = (TextView)findViewById(R.id.tv_commit_change_psw);
        tv_commit_change_psw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit_change_psw:
                service.hideKeyboard(v);
                password = input_psw_edit.getText().toString();
                password2 = input_psw_again_edit.getText().toString();
                if (!TextUtils.isEmpty(password)&&password.equals(password2))
                {
                    changePsw();
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
    /**
     * 重置密码
     */
    private void changePsw() {
        String tag_json_obj = "json_obj_check_valicode_req";
        showLoadDialog();

        JSONObject codeParams = new JSONObject();
        try {
            codeParams.put("telphone",telphone);
            codeParams.put("password",password);
            codeParams.put("act", "2");
            codeParams.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST, Urls.URL_PSW_RESET, codeParams,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissLoadDialog();
                        boolean isOk = GloabalRequestUtil.isRequestOk(response);
                        if (isOk) {
                            TipsUtils.showShort(getApplicationContext(), "密码重置成功");
                            NewForgetPswActivity3.this.finish();
                        } else {
                            TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                        }

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
