package com.androidex.sharesdk.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.sharesdk.core.Platform;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import static com.androidex.sharesdk.core.Config.ACTION_AUTH;
import static com.androidex.sharesdk.core.Config.ACTION_SHARE;

public abstract class QQCorePlatform extends Platform {
    protected Tencent mTencent;
    protected Activity mActivity;

    public QQCorePlatform(Activity context)
    {
        super(context);
        this.mActivity = context;
        mTencent = Tencent.createInstance(QQConfig.APP_KEY, context);
    }

    @Override
    protected void doAuthorize() {
        mTencent.login(mActivity, QQConfig.SCOPE, authListener);
    }

    @Override
    public boolean isClientValid() {
        return mTencent.isSupportSSOLogin(mActivity);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QQConfig.REQUEST_API) {
            if (resultCode == QQConfig.RESULT_LOGIN) {
                mTencent.handleLoginData(data, authListener);
            }
        }
    }

    protected IUiListener authListener = new IUiListener()
    {

        @Override
        public void onError(UiError error) {
            Log.e("lzm","qq_authListener_onError="+error.errorMessage);
            notifyError(ACTION_AUTH, new RuntimeException(error.errorMessage));
        }

        @Override
        public void onComplete(Object response) {
            Log.e("lzm","qq_authListener_onComplete="+response);
            if (null == response) {
                notifyError(ACTION_AUTH, new RuntimeException("返回为空"));
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;

            if (null != jsonResponse && jsonResponse.length() == 0) {
                notifyError(ACTION_AUTH, new RuntimeException("返回为空"));
                return;
            }
            Log.e("lzm","qq_"+jsonResponse.toString());
            try {
                String token = jsonResponse.getString(QQConfig.PARAM_ACCESS_TOKEN);
                String openId = jsonResponse.getString(QQConfig.PARAM_OPEN_ID);
                String expires = jsonResponse.getString(QQConfig.PARAM_EXPIRES_IN);
                if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                    mTencent.setAccessToken(token, expires);
                    mTencent.setOpenId(openId);
                }
                getPlatformDB().putUserId(openId);
                getPlatformDB().putToken(token);
                getPlatformDB().putExpiresIn(Long.parseLong(expires));
                Bundle bundle = new Bundle();
                bundle.putString(QQConfig.PARAM_ACCESS_TOKEN, token);
                bundle.putString(QQConfig.PARAM_OPEN_ID, openId);
                bundle.putString(QQConfig.PARAM_EXPIRES_IN, expires);

                notifyComplete(ACTION_AUTH, bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancel() {
            Log.e("lzm","qq_authListener_onCancel........");
            notifyCancel(ACTION_AUTH);
        }
    };

    protected IUiListener shareListener = new IUiListener()
    {

        @Override
        public void onError(UiError error) {
            Log.e("lzm","qq_shareListener_onError="+error.errorMessage);
            notifyError(ACTION_SHARE, new RuntimeException(error.errorMessage));
        }

        @Override
        public void onComplete(Object obj) {
            Log.e("lzm","qq_shareListener_onComplete="+obj);
            Bundle bundle = new Bundle();
            if (obj != null) {
                bundle.putString(QQConfig.PARAM_MSG, obj.toString());
            }
            notifyComplete(ACTION_SHARE, bundle);
        }

        @Override
        public void onCancel() {
            Log.e("lzm","qq_shareListener_onCancel.......");
            notifyCancel(ACTION_SHARE);
        }
    };

    @Override
    public void release() {
        mTencent.logout(mActivity);
        mTencent = null;
    }
}
