package com.androidex.sharesdk.wechat;

import android.os.Bundle;
import android.text.TextUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

public class AccessTokenRequest {
    static String baseUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    public static Bundle request(String code) throws IOException, JSONException {
        String urlStr = String.format(baseUrl, WechatConfig.APP_KEY, WechatConfig.APP_SECRET, code);

        BufferedReader in = null;
        String content = null;
        try {
            // 定义HttpClient
            HttpClient client = new DefaultHttpClient();
            // 实例化HTTP方法
            HttpGet request = new HttpGet();
            request.setURI(new URI(urlStr));
            HttpResponse response = client.execute(request);

            in = new BufferedReader(new InputStreamReader(response.getEntity()
                    .getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();// 最后要关闭BufferedReader
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return passerReslut(content);
    }

    public static Bundle passerReslut(String result) throws JSONException {
        JSONObject resObj = new JSONObject(result);
        String accessToken = resObj.optString("access_token");
        String openid = resObj.optString("openid");
        String expiresIn = resObj.optString("expires_in");
        String refreshToken = resObj.optString("refresh_token");
        String scope = resObj.optString("scope");
        String unionid = resObj.optString("unionid");
        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(openid) && !TextUtils.isEmpty(expiresIn)) {
            Bundle bundle = new Bundle();
            bundle.putString(WechatConfig.PARAM_ACCESS_TOKEN, accessToken);
            bundle.putString(WechatConfig.PARAM_OPEN_ID, openid);
            bundle.putString(WechatConfig.PARAM_EXPIRES_IN, expiresIn);
            return bundle;
        }
        return null;
    }
}
