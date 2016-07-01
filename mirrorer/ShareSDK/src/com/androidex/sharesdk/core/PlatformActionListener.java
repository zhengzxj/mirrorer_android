package com.androidex.sharesdk.core;

import android.os.Bundle;

public interface PlatformActionListener {

    void onComplete(Platform platform, int type, Bundle bundle);

    void onError(Platform platform, int type, Throwable error);

    void onCancel(Platform platform, int type);
}
