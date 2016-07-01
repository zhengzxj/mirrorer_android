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
import com.smart.mirrorer.bean.LoginInfo;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.event.LogInOkEvent;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.util.mUtil.MyTextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by lzm on 16/3/25.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_BASEINFO_DATA = "key_baseinfo_data";

    private EditText mPhoneEdit;
    private TextView get_code_btn;
    private EditText mCodeEdit;

    private TextView mVolidGetTv;

    private boolean isShow = false;
    private EditText mPswEdit;
    private ImageView mEyeIv;

    private String mInfoParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_layout);

//        mInfoParams = getIntent().getExtras().getString(KEY_BASEINFO_DATA);
//        Log.e("lzm", "register...._info=" + mInfoParams);
        initView();
    }

    private void initView() {
        mPhoneEdit = (EditText) findViewById(R.id.register_phone_edit);
        MyTextUtil.countLimit(mPhoneEdit,11,getResources().getString(R.string.phone_num_notice));
        get_code_btn = (TextView) findViewById(R.id.get_code_btn);
        get_code_btn.setOnClickListener(this);
        ImageView backIv = (ImageView) findViewById(R.id.register_back_iv);
        backIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_back_iv:
                finish();
                break;
//            case R.id.register_volid_tv:
//                getVolidCode();
//                break;
//            case R.id.register_psw_eye_iv:
//                if (isShow) {
//                    mEyeIv.setImageResource(R.drawable.change_psw_state_colose);
//                    mPswEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                } else {
//                    mEyeIv.setImageResource(R.drawable.change_psw_state_open);
//                    mPswEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                }
//                Editable etext = mPswEdit.getText();
//                Selection.setSelection(etext, etext.length());
//                isShow = !isShow;
//                break;
//            case R.id.register_btn:
//                registerApp();
//                break;
            case R.id.get_code_btn:
                getVolidCode();
                break;
        }
    }

    private void getVolidCode() {
        String phoneNum = mPhoneEdit.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            TipsUtils.showShort(getApplicationContext(), "请输入手机号");
            return;
        }

        String tag_json_obj = "json_obj_valicode_req";
        showLoadDialog();

        JSONObject codeParams = new JSONObject();
        try {
            codeParams.put("telphone", phoneNum);
            codeParams.put("action", "1");
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
                            Intent intent = new Intent(RegisterActivity.this, InputCodeActivity.class);
                            intent.putExtra("telphone",mPhoneEdit.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            TipsUtils.showShort(getApplicationContext(), response.optString("result"));
                        }

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
//                VolleyLog.d("lzm", "Error: " + error.getMessage());
                L.i("注册 发送验证码 error = "+error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    /**
     * 注册请求
     */
    private void registerApp() {
        if (TextUtils.isEmpty(mInfoParams)) {
            TipsUtils.showShort(getApplicationContext(), "录入信息为空，请返回录入用户信息");
            return;
        }

        final String phoneNum = mPhoneEdit.getText().toString();
        final String codeNum = mCodeEdit.getText().toString();
        final String pswText = mPswEdit.getText().toString();

        if (TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(codeNum) || TextUtils.isEmpty(pswText)) {
            TipsUtils.showShort(getApplicationContext(), "请完善输入信息");
            return;
        }

        String tag_json_obj = "json_obj_register_req";
        showLoadDialog();

        JSONObject paramObj = null;
        try {
            paramObj = new JSONObject(mInfoParams);
            paramObj.put("telphone", phoneNum);
            paramObj.put("password", pswText);
            paramObj.put("code", codeNum);
//            if(TextUtils.isEmpty(BaseApplication.getInstance().rid))
//            {
//                JPushInterface.init(this);
//                BaseApplication.getInstance().rid = JPushInterface.getRegistrationID(this);
//            }
//            paramObj.put("rid",BaseApplication.getInstance().rid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST,
                Urls.URL_REGISTER, paramObj, mRegisterCallBack
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private GsonCallbackListener<LoginInfo> mRegisterCallBack = new GsonCallbackListener<LoginInfo>() {

        @Override
        public void onResultSuccess(LoginInfo loginInfo) {
            super.onResultSuccess(loginInfo);
            dismissLoadDialog();
            if (loginInfo == null) {
                TipsUtils.showShort(getApplicationContext(), "注册失败");
                return;
            }

            LoginInfo.ResultBean loginResult = loginInfo.getResult();
            if (loginResult == null) {
                TipsUtils.showShort(getApplicationContext(), "注册异常，没有返回uid");
                return;
            }
            mUid = loginResult.getUid();
            mNick = loginResult.getNick();
            if (TextUtils.isEmpty(mUid)) {
                TipsUtils.showShort(getApplicationContext(), "注册异常，没有返回uid");
                return;
            }

            BusProvider.getInstance().post(new FinishActivityEvent());
            BusProvider.getInstance().post(new LogInOkEvent());

            JPushInterface.setAlias(getApplicationContext(), mUid, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.e("lzm", "jupush_i="+i+"__s="+s);
                }
            });

            TipsUtils.showShort(getApplicationContext(), "注册成功");
            MirrorSettings settings = BaseApplication.getSettings(RegisterActivity.this);
            settings.APP_UID.setValue(mUid);
            settings.USER_ACCOUNT.setValue(mPhoneEdit.getText().toString());
            settings.USER_NICK.setValue(mNick);

            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };
    private String mUid;
    private String mNick;
}
