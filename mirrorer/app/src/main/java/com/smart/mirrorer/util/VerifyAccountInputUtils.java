package com.smart.mirrorer.util;

import android.content.Context;
import android.text.TextUtils;


public class VerifyAccountInputUtils {

//    public static boolean verifyPasswordAvailable(Context context, String password) {
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.showMsg(context, R.string.please_inputPassword);
//            return false;
//        }
//        if (password.length() < 6 || password.length() > 16) {
//            ToastUtils.showMsg(context, R.string.password_cannot_be_lessThan_4digits);
//            return false;
//        }
//        return true;
//    }

    public static boolean verifyPhoneNumberAvailable(Context context, String mPhoneNumber) {
        if (TextUtils.isEmpty(mPhoneNumber)) {
            TipsUtils.showShort(context, "请输入您的手机号码");
            return false;
        }
        if (!VerifyUtils.isMobileNO(mPhoneNumber)) {
            TipsUtils.showShort(context, "手机号码不正确");
            return false;
        }
        return true;
    }

//    public static boolean verifyEmailAvailable(Context context, String email) {
//        if (TextUtils.isEmpty(email)) {
//            ToastUtils.showMsg(context, context.getResources().getString(R.string.verify_bind_email_tips));
//            return false;
//        }
//        if (!VerifyUtils.isEmail(email)) {
//            ToastUtils.showMsg(context, context.getResources().getString(R.string.verify_email_error_tips));
//            return false;
//        }
//        return true;
//    }

    public static boolean verifyVerifyCodeAvailable(Context context, String code) {
        if (TextUtils.isEmpty(code)) {
            TipsUtils.showShort(context, "请输入短信验证码");
            return false;
        }
        if (code.length() < 6) {
            TipsUtils.showShort(context, "验证码少于6位");
            return false;
        }
        return true;
    }
}
