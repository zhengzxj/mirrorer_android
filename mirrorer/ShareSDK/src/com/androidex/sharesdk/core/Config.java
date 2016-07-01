package com.androidex.sharesdk.core;

public class Config {
    public static int ACTION_AUTH = 1; // 授权
    public static int ACTION_SHARE = 2; // 分享
    //
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    public static final String PARAM_OPEN_ID = "openid";
    public static final String PARAM_EXPIRES_IN = "expires_in";
    //
    public static final String PARAM_MSG = "message";
    //
    public static final int RESLUT_CODE_COMPLETE = 1;
    public static final int RESLUT_CODE_ERROR = 2;
    public static final int RESLUT_CODE_CANCEL = 3;
    //
    public static final String KEY_CALLBACK = "reslut_callback";
    public static final String KEY_PLATFORM = "platform";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PARAMS = "share_params";

    private String appKey;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
