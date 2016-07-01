package com.androidex.sharesdk.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PlatformDB {
    private static final String TOKEN = "token";
    private static final String SECRET = "secret";
    private static final String EXPIRESIN = "expiresIn";
    private static final String EXPIRESTIME = "expiresTime";
    private static final String USERID = "userId";
    private static final String NICKNAME = "nickname";
    private static final String GENDER = "gender";
    private static final String ICON = "icon";
    //
    private SharedPreferences db;
    private String platformName;
    private int platformVersion;

    public PlatformDB(Context context, String platformName, int platformVersion)
    {
        super();
        String name = "sharedsdk_" + platformName + "_" + platformVersion;
        this.db = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.platformName = platformName;
        this.platformVersion = platformVersion;
    }

    public void put(String key, String value) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(key, value);
        localEditor.commit();
    }

    public String get(String key) {
        return this.db.getString(key, "");
    }

    public String getToken() {
        return db.getString(TOKEN, "");
    }

    public void putToken(String token) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(TOKEN, token);
        localEditor.commit();
    }

    public String getSecret() {
        return db.getString(SECRET, "");
    }

    public void putTokenSecret(String secret) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(SECRET, secret);
        localEditor.commit();
    }

    public long getExpiresIn() {
        return db.getLong(EXPIRESIN, 0L);
    }

    public void putExpiresIn(long expiresIn) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putLong(EXPIRESIN, expiresIn);
        localEditor.putLong(EXPIRESTIME, System.currentTimeMillis());
        localEditor.commit();
    }

    public long getExpiresTime() {
        long l1 = this.db.getLong(EXPIRESTIME, 0L);
        long l2 = getExpiresIn();
        return l1 + l2 * 1000L;
    }

    public String getUserId() {
        return db.getString(USERID, "");
    }

    public void putUserId(String userId) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(USERID, userId);
        localEditor.commit();
    }

    public String getNickName() {
        return db.getString(NICKNAME, "");
    }

    public void putNickName(String nickName) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(NICKNAME, nickName);
        localEditor.commit();
    }

    public String getIcon() {
        return db.getString(ICON, "");
    }

    public void putIcon(String icon) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(ICON, icon);
        localEditor.commit();
    }

    public boolean isValid() {
        String token = getToken();
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        if (getExpiresIn() == 0L) {
            return true;
        }
        return getExpiresTime() > System.currentTimeMillis();
    }

    public String getUserGender() {
        String str = this.db.getString(GENDER, "2");
        if ("0".equals(str)) {
            return "m";
        } else if ("1".equals(str)) {
            return "f";
        }
        return null;
    }

    public void putUserGender(String gender) {
        SharedPreferences.Editor localEditor = this.db.edit();
        localEditor.putString(GENDER, gender);
        localEditor.commit();
    }
}
