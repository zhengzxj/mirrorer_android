package com.androidex.sharesdk.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.sharesdk.core.PlatformEntity;
import com.androidex.sharesdk.utils.Utils;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

public class QQPlatform extends QQCorePlatform {

    public QQPlatform(Activity context)
    {
        super(context);
    }

    @Override
    protected void doShare(ShareParams params) {
        Bundle shareParams = getShateParams(params);
        mTencent.shareToQQ(mActivity, shareParams, shareListener);
    }

    private Bundle getShateParams(ShareParams params) {
        Bundle bundle = new Bundle();
        if (TextUtils.isEmpty(params.getUrl()) && !TextUtils.isEmpty(params.getImagePath())) {
            // 纯图片分享
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, params.getImagePath());
        } else {
            // 图文分享
            bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            if (!TextUtils.isEmpty(params.getTitle())) {
                bundle.putString(QQShare.SHARE_TO_QQ_TITLE, params.getTitle());
            }
            if (!TextUtils.isEmpty(params.getText())) {
                bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, params.getText());
            }
            if (!TextUtils.isEmpty(params.getUrl())) {
                bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, params.getUrl());
            }
            if (!TextUtils.isEmpty(params.getImagePath()) || !TextUtils.isEmpty(params.getImageUrl())) {
                String imageUrl = params.getImagePath();
                imageUrl = TextUtils.isEmpty(imageUrl) ? params.getImageUrl() : imageUrl;
                bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            }
        }
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, Utils.getApplicationName(mActivity));
        // bundle.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其他附加功能");

        return bundle;
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.QQ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QQConfig.REQUEST_QQ_SHARE) {
            if (resultCode == QQConfig.ACTIVITY_OK) {
                Tencent.handleResultData(data, shareListener);
            }
        }
    }

}
