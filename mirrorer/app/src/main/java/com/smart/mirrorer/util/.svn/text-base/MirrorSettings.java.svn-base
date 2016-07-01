package com.smart.mirrorer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class MirrorSettings extends AppSettings {

    private static final String SHARED_PREFERENCES_NAME = "mirrorer.settings";

    private final SharedPreferences mGlobalPreferences;

    public MirrorSettings(Context context) {
        mGlobalPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
    }

    @Override
    public SharedPreferences getGlobalPreferences() {
        return mGlobalPreferences;
    }

//    public FloatPreference USER_BANLANCE_MONEY = new FloatPreference("user_blance_money", (float) 0);
//    public IntPreference RECOMMON_BANNER_ID = new IntPreference("recommon_banner_id", 0);

    public StringPreference USER_NICK = new StringPreference("user_nick", "");
    public StringPreference USER_ACCOUNT = new StringPreference("user_account", "");

    public BooleanPreference JUSTALK_IS_LOGINED = new BooleanPreference("justalk_is_logined", false);
    public BooleanPreference APP_IS_TUTOR_TYPE = new BooleanPreference("app_is_tutor_type", false);
    //是否有为支付的订单
    public BooleanPreference APP_IS_UN_PAY = new BooleanPreference("app_is_un_pay", false);

    public StringPreference APP_UID = new StringPreference("app_uid", "");
    public void loginoutUser() {
        clearLoginInfo();
    }

    private void clearLoginInfo() {
        APP_UID.resetToDefault();
        USER_ACCOUNT.resetToDefault();
        USER_NICK.resetToDefault();
        JUSTALK_IS_LOGINED.resetToDefault();
        APP_IS_TUTOR_TYPE.resetToDefault();
        APP_IS_UN_PAY.resetToDefault();
    }
}
