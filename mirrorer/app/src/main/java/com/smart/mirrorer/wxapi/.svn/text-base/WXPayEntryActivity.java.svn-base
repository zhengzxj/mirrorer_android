package com.smart.mirrorer.wxapi;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.home.WechatConfig;
import com.smart.mirrorer.util.TipsUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.weixin_pay_result);

        api = WXAPIFactory.createWXAPI(this, WechatConfig.APP_ID);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onResp(BaseResp resp) {
        Log.e("lzm", "resp.transaction="+resp.transaction);
        Log.d("lzm", "onPayFinish, errCode = " + resp.errCode+"__resp.errStr="+resp.errStr);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int resultCode = resp.errCode;
            switch (resultCode) {
                case 0://成功
                    TipsUtils.showShort(getApplicationContext(), "支付成功");
					BusProvider.getInstance().post(new FinishActivityEvent());
                    WXPayEntryActivity.this.finish();
                    break;
                case -1: //错误	可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                    TipsUtils.showShort(getApplicationContext(), "支付失败");
                    WXPayEntryActivity.this.finish();
                    break;
                case -2: //用户取消	无需处理。发生场景：用户不支付了，点击取消，返回APP。
                    TipsUtils.showShort(getApplicationContext(), "取消支付");
                    WXPayEntryActivity.this.finish();
                    break;
                default:
                    break;
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}