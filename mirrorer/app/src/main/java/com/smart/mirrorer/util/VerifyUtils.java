package com.smart.mirrorer.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyUtils {

    private static final String PATTERN_MOBILE_NUMBER = "^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$";
    private static final String PATTERN_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

    // 判断手机格式是否正确
    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern.compile(PATTERN_MOBILE_NUMBER);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile(PATTERN_EMAIL);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    // 判断是否全是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 判断是否是数字或字母
    public static boolean isPassword(String str) {
        Pattern pattern = Pattern.compile("[A-Z,a-z,0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
