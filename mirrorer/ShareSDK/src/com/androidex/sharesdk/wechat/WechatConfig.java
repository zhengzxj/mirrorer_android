package com.androidex.sharesdk.wechat;

public class WechatConfig extends com.androidex.sharesdk.core.Config {
    // appkey
    public static final String APP_KEY = "wxfc88431fb68d2797";
    public static final String APP_SECRET = "29831c8c73b1bad85b4c70210e19d237";

    // 接收返回结果广播
    static final String ACTION_WECHAT_RESP = "wechat.action.resp";
    // base
    static final String RESP_WXAPI_BASERESP_TYPE = "_wxapi_baseresp_type";
    // public int errCode;
    static final String RESP_WXAPI_BASERESP_ERRCODE = "_wxapi_baseresp_errcode";
    // public String errStr;
    static final String RESP_WXAPI_BASERESP_ERRSTR = "_wxapi_baseresp_errstr";
    // public String transaction;
    static final String RESP_WXAPI_BASERESP_TRANSACTION = "_wxapi_baseresp_transaction";
    // public String openId;
    static final String RESP_WXAPI_BASERESP_OPENID = "_wxapi_baseresp_openId";

    // Auth request
    // public String code;
    static final String RESP_WXAPI_SENDAUTH_RESP_TOKEN = "_wxapi_sendauth_resp_token";
    // public String state;
    static final String RESP_WXAPI_SENDAUTH_RESP_STATE = "_wxapi_sendauth_resp_state";
    // public String url;
    static final String RESP_WXAPI_SENDAUTH_RESP_URL = "_wxapi_sendauth_resp_url";
    // public String lang;
    static final String RESP_WXAPI_SENDAUTH_RESP_LANG = "_wxapi_baseresp_openId";
    // public String country;
    static final String RESP_WXAPI_SENDAUTH_RESP_COUNTRY = "_wxapi_baseresp_openId";

}