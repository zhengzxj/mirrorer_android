package com.smart.mirrorer.util;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidex.sharesdk.OpenAccountManager;
import com.androidex.sharesdk.OpenAccountManager.ReslutCallback;
import com.androidex.sharesdk.core.Platform.ShareParams;
import com.androidex.sharesdk.core.PlatformEntity;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseFragment;


public final class SnsUtils {

    public static void shareApp(final BaseActivity activity, final String title, final String Content, String shareTempUrl) {
        Log.e("lzm", "shareUrl=" + shareTempUrl);

        if (!TextUtils.isEmpty(shareTempUrl)) {
            if (!shareTempUrl.startsWith("http://")) {
                shareTempUrl = "http://" + shareTempUrl;
            }

            String shareLitterImageName = BitmapUtil.saveResourceAsCurrentStorage(activity.getApplicationContext(),
                    "shareLitteAppImage", R.mipmap.ic_launcher);
            ShareParams shareBean = new ShareParams();
            shareBean.setImagePath(shareLitterImageName);
            shareBean.setTitle(title);
            shareBean.setText(Content);
//             shareBean.setContent(title);
            shareBean.setUrl(shareTempUrl);
            // 上传用到参数
            shareBean.setStrParams("topicType", "3");
            // 配置所有平台分享
            configPlatformNomarl();
            executeShare(activity.getApplicationContext(), null, shareBean);
        } else {
            TipsUtils.showShort(activity.getApplicationContext(), "没找到分享地址，不能分享");
        }

    }

    public static void shareApp(final BaseFragment activity, final String title, final String Content, String shareTempUrl) {

        if (!TextUtils.isEmpty(shareTempUrl)) {
            if (!shareTempUrl.startsWith("http://")) {
                shareTempUrl = "http://" + shareTempUrl;
            }

            String shareLitterImageName = BitmapUtil.saveResourceAsCurrentStorage(activity.getActivity(),
                    "shareLitteAppImage", R.mipmap.ic_launcher);
            ShareParams shareBean = new ShareParams();
            shareBean.setImagePath(shareLitterImageName);
            shareBean.setTitle(title);
            shareBean.setText(Content);
//             shareBean.setContent(title);
            shareBean.setUrl(shareTempUrl);
            // 上传用到参数
            shareBean.setStrParams("topicType", "3");
            // 配置所有平台分享
            configPlatformNomarl();
            executeShare(activity.getActivity(), null, shareBean);
        } else {
            TipsUtils.showShort(activity.getActivity().getApplicationContext(), "没找到分享地址，不能分享");
        }

    }

    private static void executeShare(final Context context, final PlatformEntity entity,
                                     final ShareParams params) {
        final OpenAccountManager manager = OpenAccountManager.getInstance(context);
        manager.shareToPlatform(entity, params, new ReslutCallback() {

            @Override
            public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
                TipsUtils.showShort(context, "分享成功");
            }

            @Override
            public void onError(int type, PlatformEntity paltform, String errorMsg) {
                TipsUtils.showShort(context, "分享失败");
            }

            @Override
            public void onCancel(int type, PlatformEntity paltform) {
                TipsUtils.showShort(context, "取消分享");
            }
        });
    }


    private static void configPlatformNomarl() {
        PlatformEntity.QQ.enable = true;
        PlatformEntity.QZONE.enable = true;
        PlatformEntity.WECHAT.enable = true;
        PlatformEntity.WECHAT_TIMELINE.enable = true;
    }
}
