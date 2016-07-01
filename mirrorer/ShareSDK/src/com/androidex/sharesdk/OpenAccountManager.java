package com.androidex.sharesdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.androidex.sharesdk.core.Config;
import com.androidex.sharesdk.core.Platform;
import com.androidex.sharesdk.core.PlatformEntity;
import com.androidex.sharesdk.core.ShareHookActivity;

@SuppressLint("ParcelCreator")
public class OpenAccountManager extends ResultReceiver {
    //
    private static OpenAccountManager _instance;

    //
    public static OpenAccountManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new OpenAccountManager(context);
        }
        return _instance;
    }

    private Context mContext;

    //
    private OpenAccountManager(Context context)
    {
        super(new Handler(context.getMainLooper()));
        this.mContext = context;
    }

    private ReslutCallback callback;

    public void setCallback(ReslutCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        int type = resultData.getInt(Config.KEY_TYPE);
        int platform = resultData.getInt(Config.KEY_PLATFORM);

        switch (resultCode) {
        case Config.RESLUT_CODE_COMPLETE:
            if (callback != null) {
                callback.onComplete(type, PlatformEntity.findPlatformById(platform), resultData);
            }
            break;
        case Config.RESLUT_CODE_CANCEL:
            if (callback != null) {
                callback.onCancel(type, PlatformEntity.findPlatformById(platform));
            }
            break;
        case Config.RESLUT_CODE_ERROR:
            String errorMsg = resultData.getString(Config.PARAM_MSG);
            if (callback != null) {
                callback.onError(type, PlatformEntity.findPlatformById(platform), errorMsg);
            }
            break;
        default:
            break;
        }
        callback = null;
    }

    /**
     * 
     * @param paltform
     * @param params
     * @param callback
     */
    public void shareToPlatform(PlatformEntity paltform, Platform.ShareParams params, ReslutCallback callback) {
        this.callback = callback;
        Intent shareInent = new Intent(mContext, ShareHookActivity.class);
        shareInent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //
        if (paltform != null) {
            shareInent.putExtra(Config.KEY_PLATFORM, paltform.id);
        }
        //
        shareInent.putExtra(Config.KEY_CALLBACK, this);
        shareInent.putExtra(Config.KEY_TYPE, Config.ACTION_SHARE);
        shareInent.putExtras(params.toBundle());
        //
        mContext.startActivity(shareInent);
    }

    /**
     * 
     * @param paltform
     * @param callback
     */
    public void authorize(PlatformEntity paltform, ReslutCallback callback) {
        this.callback = callback;
        Intent shareInent = new Intent(mContext, ShareHookActivity.class);
        //

        if (paltform != null) {
            shareInent.putExtra(Config.KEY_PLATFORM, paltform.id);
        }
        //
        shareInent.putExtra(Config.KEY_CALLBACK, this);
        shareInent.putExtra(Config.KEY_TYPE, Config.ACTION_AUTH);
        //
        mContext.startActivity(shareInent);
    }

    public interface ReslutCallback {
        void onComplete(int type, PlatformEntity paltform, Bundle bundle);

        void onError(int type, PlatformEntity paltform, String errorMsg);

        void onCancel(int type, PlatformEntity paltform);
    }
}
