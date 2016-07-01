package com.androidex.sharesdk.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.sharesdk.ClientNotInstalledException;
import com.androidex.sharesdk.core.HookActivity;
import com.androidex.sharesdk.core.Platform;
import com.androidex.sharesdk.core.PlatformEntity;
import com.androidex.sharesdk.utils.HookUtil;
import com.androidex.sharesdk.utils.Utils;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import static com.androidex.sharesdk.core.Config.ACTION_AUTH;
import static com.androidex.sharesdk.core.Config.ACTION_SHARE;

public abstract class WechatCorePlatform extends Platform {
    private IWXAPI mWxApi;
    private HookActivity activity;

    public WechatCorePlatform(HookActivity context) {
        super(context);
        activity = context;
        mWxApi = WXAPIFactory.createWXAPI(context, WechatConfig.APP_KEY, true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WechatConfig.ACTION_WECHAT_RESP);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public boolean isClientValid() {
        return mWxApi.isWXAppInstalled();
    }

    @Override
    public boolean isValid() {
        return mWxApi.isWXAppSupportAPI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // nothing
    }

    @Override
    protected void doAuthorize() {
        if (!isClientValid()) {
            notifyError(ACTION_AUTH, new ClientNotInstalledException("微信客户端没有安装"));
            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = Utils.getApplicationName(context);
        mWxApi.sendReq(req);
    }

    @Override
    protected void doShare(ShareParams params) {
        int scene = SendMessageToWX.Req.WXSceneSession;
        if (getPlatformEntity() == PlatformEntity.WECHAT) {
            scene = SendMessageToWX.Req.WXSceneSession;
        } else if (getPlatformEntity() == PlatformEntity.WECHAT_TIMELINE) {
            scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        boolean reslut = false;
        if (!TextUtils.isEmpty(params.getUrl())) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = params.getUrl();
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(params.getImagePath())) {
                bitmap = Utils.scaleAndcompressBitmap(params.getImagePath());
            } else if (params.getBitmapData() != null) {
                bitmap = params.getBitmapData();
            }
            reslut = invokingWinxin(scene, webpage, params.getTitle(), params.getText(), bitmap, "webpage");
        } else if (!TextUtils.isEmpty(params.getImagePath()) || params.getBitmapData() != null
                || !TextUtils.isEmpty(params.getImageUrl())) {
            WXImageObject imageObj = new WXImageObject();
            if (!TextUtils.isEmpty(params.getImagePath())) {
                imageObj.imagePath = params.getImagePath();
            } else if (params.getBitmapData() != null) {
                imageObj.imageData = Utils.bmpToByteArray(params.getBitmapData(), true);
            } else if (!TextUtils.isEmpty(params.getImageUrl())) {
                imageObj.imageUrl = params.getImageUrl();
            }
            reslut = invokingWinxin(scene, imageObj, params.getTitle(), params.getText(), null, "imagepage");
        } else if (!TextUtils.isEmpty(params.getText())) {
            WXTextObject textObj = new WXTextObject();
            textObj.text = params.getText();
            reslut = invokingWinxin(scene, textObj, params.getTitle(), params.getText(), null, "textpage");
        }
        //
        if (!reslut) {
            notifyError(ACTION_SHARE, new RuntimeException("未执行"));
        }
    }

    private boolean invokingWinxin(int scene, IMediaObject obj, String title, String description, Bitmap thumb,
                                   String type) {
        WXMediaMessage msg = new WXMediaMessage(obj);
        msg.title = title;
        msg.description = description;
        if (thumb != null) {
            msg.thumbData = Utils.bmpToByteArray(thumb, false);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction(type);
        req.message = msg;
        req.scene = scene;
        return mWxApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle result = intent.getExtras();
            if (result != null) {
                int type = result.getInt(WechatConfig.RESP_WXAPI_BASERESP_TYPE);
                int errCode = result.getInt(WechatConfig.RESP_WXAPI_BASERESP_ERRCODE);
                String errStr = result.getString(WechatConfig.RESP_WXAPI_BASERESP_ERRSTR);
                String transaction = result.getString(WechatConfig.RESP_WXAPI_BASERESP_TRANSACTION);
                String openId = result.getString(WechatConfig.RESP_WXAPI_BASERESP_OPENID);
                if (WechatConfig.ACTION_SHARE == type) {
                    switch (errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            notifyComplete(ACTION_SHARE, null);
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            notifyCancel(ACTION_SHARE);
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                        case BaseResp.ErrCode.ERR_COMM:
                            notifyError(ACTION_SHARE, new RuntimeException("分享。errorcode = " + errCode));
                            break;
                        default:
                            break;
                    }
                } else if (WechatConfig.ACTION_AUTH == type) {
                    switch (errCode) {
                        case BaseResp.ErrCode.ERR_OK:
                            String code = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_TOKEN);
                            String state = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_STATE);
                            String url = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_URL);
                            String lang = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_LANG);
                            String country = result.getString(WechatConfig.RESP_WXAPI_SENDAUTH_RESP_COUNTRY);
                            if (!TextUtils.isEmpty(code)) {
                                requestAccessToken(code);
                            } else {
                                notifyError(ACTION_AUTH, new RuntimeException("授权失败,授权码为空。"));
                            }
                            break;
                        case BaseResp.ErrCode.ERR_USER_CANCEL:
                            notifyCancel(ACTION_AUTH);
                            break;
                        case BaseResp.ErrCode.ERR_AUTH_DENIED:
                        case BaseResp.ErrCode.ERR_SENT_FAILED:
                        case BaseResp.ErrCode.ERR_UNSUPPORT:
                        case BaseResp.ErrCode.ERR_COMM:
                            notifyError(ACTION_AUTH, new RuntimeException("授权失败。errorcode = " + errCode));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

    };

    private static final int AUTH_COMPLETE = 0x11;
    private static final int AUTH_ERROR = 0x12;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AUTH_COMPLETE:
                    Bundle reslut = (Bundle) msg.obj;
                    getPlatformDB().putUserId(reslut.getString(WechatConfig.PARAM_OPEN_ID));
                    getPlatformDB().putToken(reslut.getString(WechatConfig.PARAM_ACCESS_TOKEN));
                    getPlatformDB().putExpiresIn(Long.parseLong(reslut.getString(WechatConfig.PARAM_EXPIRES_IN)));
                    notifyComplete(ACTION_AUTH, reslut);
                    break;
                case AUTH_ERROR:
                    Exception error = (Exception) msg.obj;
                    notifyError(ACTION_AUTH, error);
                    break;

                default:
                    break;
            }
        }

    };

    private void requestAccessToken(final String code) {
        HookUtil.startBackgroundJob(activity, "正在请求微信授权...", false, new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle reslut = AccessTokenRequest.request(code);
                    if (reslut != null) {
                        Message msg = handler.obtainMessage(AUTH_COMPLETE, reslut);
                        msg.sendToTarget();
                    } else {
                        Message msg = handler.obtainMessage(AUTH_ERROR, new RuntimeException("on reslut"));
                        msg.sendToTarget();
                    }
                } catch (Exception ex) {
                    Message msg = handler.obtainMessage(AUTH_ERROR, ex);
                    msg.sendToTarget();
                }
            }
        }, handler);
    }

    @Override
    public void release() {
        activity.unregisterReceiver(receiver);
        mWxApi.detach();
        mWxApi = null;
    }

}
