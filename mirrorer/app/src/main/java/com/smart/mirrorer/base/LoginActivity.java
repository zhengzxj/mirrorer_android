package com.smart.mirrorer.base;

import android.content.Intent;
import android.media.Image;
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
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.service.MqttService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by lzm on 16/3/25.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mPhoneEdit;

    private boolean isShow = false;
    private EditText mPswEdit;

    private MirrorSettings mSettings;

//    private ImageView mEyeIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TextUtils.isEmpty(BaseApplication.getSettings(this).APP_UID.getValue())) {
//            Intent intent = new Intent(LoginActivity.this, NewMainActivity.class);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login_layout);
        mSettings = BaseApplication.getSettings(this);

        BusProvider.getInstance().register(this);
        initView();
    }
    private ImageView login_back_iv;

    private void initView() {
//        ImageView closeIv = (ImageView) findViewById(R.id.login_close_ic);
//        closeIv.setOnClickListener(this);

        login_back_iv = (ImageView)findViewById(R.id.login_back_iv);
        login_back_iv.setOnClickListener(this);
        String historyAccount = mSettings.USER_ACCOUNT.getValue();

        mPhoneEdit = (EditText) findViewById(R.id.login_phone_edit);
        mPswEdit = (EditText) findViewById(R.id.login_psw_edit);

        if (!TextUtils.isEmpty(historyAccount)) {
            mPhoneEdit.setText(historyAccount);
        }

        TextView loginBtn = (TextView) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

//        mEyeIv = (ImageView) findViewById(R.id.login_psw_eye_iv);
//        mEyeIv.setOnClickListener(this);

        TextView forgetTv = (TextView) findViewById(R.id.login_forget_tv);
        forgetTv.setOnClickListener(this);

//        TextView registerTv = (TextView) findViewById(R.id.login_register_tv);
//        registerTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_back_iv:
                finish();
                break;
            case R.id.login_btn:
//                SoundPoulManager.btnClickSound();
                loginApp();
                break;
//            case R.id.login_psw_eye_iv:
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
//            case R.id.login_register_tv:
//                processCleanIntent(RegisterActivity.class);
//                break;
            case R.id.login_forget_tv:
                processCleanIntent(NewForgetPswActivity1.class);
                break;
        }
    }

    /**
     * 登录请求
     */
    private void loginApp() {
//        BaseApplication.getInstance().rid = JPushInterface.getRegistrationID(this);
        final String phoneValue = mPhoneEdit.getText().toString();
        final String pswValue = mPswEdit.getText().toString();

        if (TextUtils.isEmpty(phoneValue) || TextUtils.isEmpty(pswValue)) {
            TipsUtils.showShort(getApplicationContext(), "手机号和密码不能为空");
            return;
        }

        String tag_json_obj = "json_obj_login_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("telphone", phoneValue);
            paramObj.put("password", pswValue);
//            if(TextUtils.isEmpty(BaseApplication.getInstance().rid))
//            {
//                JPushInterface.init(this);
//                BaseApplication.getInstance().rid = JPushInterface.getRegistrationID(this);
//            }
//            paramObj.put("rid", BaseApplication.getInstance().rid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        L.e("登录 参数 paramObj = "+paramObj);
        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST,
                Urls.URL_LOGIN, paramObj, loginCallBack
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.e("登录 error");
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private GsonCallbackListener<LoginInfo> loginCallBack = new GsonCallbackListener<LoginInfo>() {
        @Override
        public void onResultSuccess(LoginInfo loginInfo) {
            super.onResultSuccess(loginInfo);
            L.e("登录 loginInfo = "+loginInfo);
            dismissLoadDialog();
            if (loginInfo == null) {
                TipsUtils.showShort(getApplicationContext(), "登录异常，没有返回uid");
                return;
            }
            LoginInfo.ResultBean loginResult = loginInfo.getResult();
            if (loginResult == null) {
                TipsUtils.showShort(getApplicationContext(), "登录异常，没有返回uid");
                return;
            }
            mUid = loginResult.getUid();
            mNick = loginResult.getNick();
            if (TextUtils.isEmpty(mUid)) {
                TipsUtils.showShort(getApplicationContext(), "登录异常，没有返回uid");
                return;
            }

            JPushInterface.setAlias(getApplicationContext(), mUid, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.e("lzm", "jupush_i=" + i + "__s=" + s);
                }
            });
            TipsUtils.showShort(getApplicationContext(), "登录成功");
            MqttService.actionStart(BaseApplication.getInstance());

//            JPushInterface.resumePush(LoginActivity.this);
            mSettings.APP_UID.setValue(mUid);
            mSettings.USER_ACCOUNT.setValue(mPhoneEdit.getText().toString());
            mSettings.USER_NICK.setValue(mNick);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            L.e("登录 onFailed");
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    private String mUid;
    private String mNick;

    @Subscribe
    public void onEventFinish(FinishActivityEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }


}
