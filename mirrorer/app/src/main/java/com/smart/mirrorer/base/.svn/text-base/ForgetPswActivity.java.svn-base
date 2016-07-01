package com.smart.mirrorer.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.smart.mirrorer.util.VerifyNumTimeCount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/4/27.
 */
public class ForgetPswActivity extends BaseActivity {

    private KeyboardService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_typein_layout);

        service = new KeyboardService(this);

        if (savedInstanceState == null) {
            Fragment newFragment = new FirstStepFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.info_typein_sub_frame_layout, newFragment).commit();
        }

    }

    @SuppressLint("ValidFragment")
    private class FirstStepFragment extends Fragment implements View.OnClickListener {

        private EditText mPhoneEdit;
        private TextView mVolidGetTv;
        private EditText mCodeEdit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.forget_psw_first_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            ImageView closeIv = (ImageView) getView().findViewById(R.id.forget_close_iv);
            closeIv.setOnClickListener(this);

            mPhoneEdit = (EditText) getView().findViewById(R.id.forger_phone_edit);
            mVolidGetTv = (TextView) getView().findViewById(R.id.forget_volid_tv);
            mVolidGetTv.setOnClickListener(this);
            mCodeEdit = (EditText) getView().findViewById(R.id.forget_volid_edit);

            TextView nextTv = (TextView) getView().findViewById(R.id.forget_next_btn);
            nextTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forget_volid_tv:
                    getVolidCode();
                    break;
                case R.id.forget_next_btn:
                    service.hideKeyboard(v);
                    String phoneNum = mPhoneEdit.getText().toString();
                    String codeNum = mCodeEdit.getText().toString();

                    if(TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(codeNum)) {
                        TipsUtils.showShort(getApplicationContext(), "请填写完整");
                        return;
                    }

                    checkVolidCode(phoneNum, codeNum);
                    break;
                case R.id.forget_close_iv:
                    finish();
                    break;
            }
        }

        /**
         * 检查验证码
         * @param phoneNum
         * @param codeNum
         */
        private void checkVolidCode(final String phoneNum, String codeNum) {
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
                            dismissLoadDialog();
                            boolean isOk = GloabalRequestUtil.isRequestOk(response);
                            if (isOk) {
                                mRealPhoneNum = phoneNum;
                                toSecondSetpFragment();
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

        private void getVolidCode() {
            String phoneNum = mPhoneEdit.getText().toString();
            if (TextUtils.isEmpty(phoneNum)) {
                TipsUtils.showShort(getApplicationContext(), "请输入手机号");
                return;
            }

            String tag_json_obj = "json_obj_forget_valicode_req";
            showLoadDialog();

            JSONObject codeParams = new JSONObject();
            try {
                codeParams.put("telphone", phoneNum);
                codeParams.put("action", "2"); //1 注册  2忘记密码
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
                                VerifyNumTimeCount verifyNumCount = new VerifyNumTimeCount(mVolidGetTv, 60000, 1000);
                                verifyNumCount.start();
                                TipsUtils.showShort(getApplicationContext(), "发送验证码成功");
                            } else {
                                TipsUtils.showShort(getApplicationContext(), "发送验证码失败");
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

    private String mRealPhoneNum;

    @SuppressLint("ValidFragment")
    private class TowSetpFragment extends Fragment implements View.OnClickListener {

        private boolean isShow = false;
        private ImageView mEyeIv;

        private EditText mPswEdit;
        private EditText mPswAginEdit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.forget_psw_second_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView backIv = (ImageView) getView().findViewById(R.id.forget_second_back_iv);
            backIv.setOnClickListener(this);

            mPswEdit = (EditText) getView().findViewById(R.id.forger_psw_edit);
            mPswAginEdit = (EditText) getView().findViewById(R.id.forger_psw_agin_edit);

            mEyeIv = (ImageView) getView().findViewById(R.id.forget_psw_eye_iv);
            mEyeIv.setOnClickListener(this);

            TextView finishTv = (TextView) getView().findViewById(R.id.forget_finish_btn);
            finishTv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.forget_second_back_iv:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.forget_psw_eye_iv:
                    if (isShow) {
                        mEyeIv.setImageResource(R.drawable.change_psw_state_colose);
                        mPswEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        mPswAginEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    } else {
                        mEyeIv.setImageResource(R.drawable.change_psw_state_open);
                        mPswEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        mPswAginEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                    Editable etext = mPswEdit.getText();
                    Selection.setSelection(etext, etext.length());

                    Editable etextAgin = mPswAginEdit.getText();
                    Selection.setSelection(etextAgin, etextAgin.length());
                    isShow = !isShow;
                    break;
                case R.id.forget_finish_btn:
                    service.hideKeyboard(v);
                    String pswText = mPswEdit.getText().toString();
                    String pswAginText = mPswAginEdit.getText().toString();
                    if(TextUtils.isEmpty(pswText) || TextUtils.isEmpty(pswAginText)) {
                        TipsUtils.showShort(getApplicationContext(), "请完善密码信息");
                        return;
                    }

                    if(!pswText.equals(pswAginText)) {
                        TipsUtils.showShort(getApplicationContext(), "两次密码输入不一致");
                        return;
                    }

                    resetPsw(pswText);
                    break;
            }
        }

        private void resetPsw(String pswText) {
            String tag_json_obj = "json_obj_reset_psw_req";
            showLoadDialog();

            JSONObject codeParams = new JSONObject();
            try {
                codeParams.put("telphone", mRealPhoneNum);
                codeParams.put("password", pswText);
                codeParams.put("act", "2");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST, Urls.URL_PSW_RESET, codeParams,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            dismissLoadDialog();
                            TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                            boolean isOk = GloabalRequestUtil.isRequestOk(response);
                            if (isOk) {
                                finish();
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

    private void toSecondSetpFragment() {
        Fragment newFragment = new TowSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
