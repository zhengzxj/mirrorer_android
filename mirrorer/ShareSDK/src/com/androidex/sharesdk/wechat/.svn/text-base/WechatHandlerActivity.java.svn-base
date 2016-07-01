package com.androidex.sharesdk.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WechatConfig.APP_KEY, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Bundle respBundle = new Bundle();
        resp.toBundle(respBundle);
        int type = resp.getType();
        Intent intent = new Intent(WechatConfig.ACTION_WECHAT_RESP);
        intent.putExtras(respBundle);
        intent.putExtra(WechatConfig.RESP_WXAPI_BASERESP_TYPE, type);
        sendBroadcast(intent);
        finish();
    }

}
