package com.smart.mirrorer.net;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.mirrorer.base.BaseApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzm on 16/4/12.
 */
public class PerfectJsonObjectRequest extends JsonObjectRequest {
    private String mUid;

    public PerfectJsonObjectRequest(String uid, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);

        this.mUid = uid;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("platform","1");
        if(!TextUtils.isEmpty(mUid)) {
            headers.put("uid", mUid);
        }
        return headers;
    }
}
