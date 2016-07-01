package com.androidex.sharesdk.core;

import com.androidex.sharesdk.qq.QQPlatform;
import com.androidex.sharesdk.qq.QZonePlatform;
import com.androidex.sharesdk.wechat.WechatFriendPlatform;
import com.androidex.sharesdk.wechat.WechatTimelinePlatform;

public class PlatformFactory {

    public static Platform createPlatform(ShareHookActivity context, PlatformEntity entity) {
        Platform platform = null;
        switch (entity) {
        case QQ:
            platform = new QQPlatform(context);
            break;
        case QZONE:
            platform = new QZonePlatform(context);
            break;
        case WECHAT:
            platform = new WechatFriendPlatform(context);
            break;
        case WECHAT_TIMELINE:
            platform = new WechatTimelinePlatform(context);
            break;
        default:
            break;
        }
        return platform;
    }
}
