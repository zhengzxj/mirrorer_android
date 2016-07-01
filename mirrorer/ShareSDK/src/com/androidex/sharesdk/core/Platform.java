package com.androidex.sharesdk.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

public abstract class Platform {

    public Context context;
    public PlatformDB platformDB;

    public Platform(Context context) {
        super();
        this.context = context;
        PlatformEntity entity = getPlatformEntity();
        this.platformDB = new PlatformDB(context, entity.name, entity.version);
    }

    public PlatformDB getPlatformDB() {
        return platformDB;
    }

    private PlatformActionListener listener;

    public abstract boolean isClientValid();

    public abstract boolean isValid();

    public abstract PlatformEntity getPlatformEntity();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void release();

    public void authorize() {
        doAuthorize();
    }

    protected abstract void doAuthorize();

    public void share(Platform.ShareParams params) {
        doShare(params);
    }

    protected abstract void doShare(Platform.ShareParams params);

    public void setPlatformActionListener(PlatformActionListener listener) {
        this.listener = listener;
    }

    protected void notifyComplete(int type, Bundle bundle) {
        if (listener != null) {
            listener.onComplete(this, type, bundle);
        }
    }

    protected void notifyError(int type, Throwable error) {
        if (listener != null) {
            listener.onError(this, type, error);
        }
    }

    protected void notifyCancel(int type) {
        if (listener != null) {
            listener.onCancel(this, type);
        }
    }

    public static class ShareParams {

        protected static final String TITLE = "title"; // 标题
        protected static final String TEXT = "text"; // 内容
        protected static final String IMAGE_PATH = "imagePath"; // 图片路径
        protected static final String IMAGE_DATA = "imageData"; // 图片数据
        protected static final String IMAGE_URL = "imageUrl"; // 网络图片
        //
        protected static final String URL = "url"; // 跳转页

        // protected static final String MUSIC_URL = "musicUrl";

        private Bundle params;

        public ShareParams(Bundle params) {
            this.params = params;
        }

        public ShareParams() {
            this.params = new Bundle();
        }

        public <T> T getParams(String key) {
            return (T) params.get(key);
        }

        public void setStrParams(String key, String value) {
            params.putString(key, value);
        }

        public void setIntParams(String key, int value) {
            params.putInt(key, value);
        }

        public void setTitle(String title) {
            params.putString(TITLE, title);
        }

        public void setText(String text) {
            params.putString(TEXT, text);
        }

        public void setImageUrl(String imageUrl) {
            params.putString(IMAGE_URL, imageUrl);
        }

        public void setImagePath(String imagePath) {
            params.putString(IMAGE_PATH, imagePath);
        }

        public void setImageData(Bitmap bitmap) {
            params.putParcelable(IMAGE_DATA, bitmap);
        }

        public void setUrl(String url) {
            params.putString(URL, url);
        }

        public String getUrl() {
            return getParams(URL);
        }

        public String getTitle() {
            return getParams(TITLE);
        }

        public String getText() {
            return getParams(TEXT);
        }

        public String getImageUrl() {
            return getParams(IMAGE_URL);
        }

        public Bitmap getBitmapData() {
            return getParams(IMAGE_DATA);
        }

        public String getImagePath() {
            return getParams(IMAGE_PATH);
        }

        public Bundle toBundle() {
            return params;
        }

        public static ShareParams formBundle(Bundle params) {
            return new ShareParams(params);
        }
    }
}
