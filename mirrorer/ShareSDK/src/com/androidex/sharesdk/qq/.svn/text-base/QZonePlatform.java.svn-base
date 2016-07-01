package com.androidex.sharesdk.qq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.sharesdk.core.PlatformEntity;
import com.androidex.sharesdk.utils.Utils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

public class QZonePlatform extends QQCorePlatform {

    public QZonePlatform(Activity context)
    {
        super(context);
    }

    @Override
    protected void doShare(ShareParams params) {
        Bundle shareParams = getShateParams(params);
        mTencent.shareToQQ(mActivity, shareParams, shareListener);
    }

    private Bundle getShateParams(ShareParams params) {
        final Bundle bundle = new Bundle();
        bundle.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, Utils.getApplicationName(mActivity));
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, params.getTitle());
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, params.getText());
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, params.getUrl());
        //
        if (!TextUtils.isEmpty(params.getImagePath()) || !TextUtils.isEmpty(params.getImageUrl())) {
            String imageUrl = params.getImagePath();
            imageUrl = TextUtils.isEmpty(imageUrl) ? params.getImageUrl() : imageUrl;
            bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        }
        return bundle;
    }

    @Override
    public PlatformEntity getPlatformEntity() {
        return PlatformEntity.QZONE;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        Log.e("lzm", "requestCode="+requestCode+"_resultCode="+resultCode);
        if (requestCode == QQConfig.REQUEST_QZONE_SHARE) {
//            if (resultCode == QQConfig.ACTIVITY_OK) {
                Tencent.handleResultData(data, shareListener);
//            }
        }
    }
}
