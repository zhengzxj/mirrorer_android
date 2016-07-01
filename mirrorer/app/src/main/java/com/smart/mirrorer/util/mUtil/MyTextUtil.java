package com.smart.mirrorer.util.mUtil;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.util.TipsUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by zhengfei on 16/6/16.
 */
public class MyTextUtil {
    public static void countLimit(final TextView tv, final int count)
    {
        tv.addTextChangedListener(new TextWatcher() {
            private String temp;
            private int editStart;
            private int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(temp)) {
                    String limitSubstring = getLimitSubstring(temp,count);
                    if (!TextUtils.isEmpty(limitSubstring)) {
                        if (!limitSubstring.equals(temp)) {
                            TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"字数不能超过"+count/2+"个汉字或"+count+"个英文");
                            tv.setText(limitSubstring);
                            if (tv instanceof EditText)
                            {
                                ((EditText)tv).setSelection(limitSubstring.length());
                            }

                        }
                    }
                }
            }
        });
    }

    public static void countLimit(final TextView tv, final int count,final String showText)
    {
        tv.addTextChangedListener(new TextWatcher() {
            private String temp;
            private int editStart;
            private int editEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(temp)) {
                    String limitSubstring = getLimitSubstring(temp,count);
                    if (!TextUtils.isEmpty(limitSubstring)) {
                        if (!limitSubstring.equals(temp)) {
                            TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),showText);
                            tv.setText(limitSubstring);
                            if (tv instanceof EditText)
                            {
                                ((EditText)tv).setSelection(limitSubstring.length());
                            }

                        }
                    }
                }
            }
        });
    }

    private static String getLimitSubstring(String inputStr,int count) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        String temp = null;
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i, i + 1);
            try {// 3 bytes to indicate chinese word,1 byte to indicate english
                // word ,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 2;
                } else {
                    resultLen++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (resultLen > count) {
                return inputStr.substring(0, i);
            }
        }
        return inputStr;
    }
}
