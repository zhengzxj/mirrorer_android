package com.smart.mirrorer.util;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.UploadTokenBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class UploadUtils {

    public static final String TYPE_UPLOAD_HEAD = "head";
    public static final String TYPE_UPLOAD_CARD = "card-id"; //工作卡
    public static final String TYPE_UPLOAD_DESC_VIDEO = "introduce-video"; //回答者介绍视频
    public static final String TYPE_UPLOAD_CALL_VIDEO = "call-video"; //上传录制视频的（暂时未做）

    /**
     * 获取上传凭证，无uid获取
     * @param callback
     * @param uploadType
     */
    public static void getUploadToken(final UploadTokenCallback callback, String uploadType) {
        if (callback != null) {
            callback.onPrepare();
        }

        String tag_json_obj = "json_obj_upload_token_simple_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("actionId", uploadType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST,
                Urls.URL_UPLOAD_TOKEN, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("lzm", "gettoken=" + response.toString());
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    UploadTokenBean tokenBean = parseTokenJson(response.toString());
                    callback.onSuccess(tokenBean);
                } else {
                    String netErrorText = GloabalRequestUtil.getNetErrorMsg(response);
                    callback.onFail(netErrorText);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    /**
     * 获取上传凭证  带uid
     * @param callback
     * @param uploadType
     * @param uid
     */
    public static void getUploadToken(final UploadTokenCallback callback, String uploadType, String uid) {
        if (callback != null) {
            callback.onPrepare();
        }

        String tag_json_obj = "json_obj_upload_token_req";
        JSONObject paramObj = new JSONObject();
        try {
            L.i("getUploadToken actionId = "+uploadType);
            paramObj.put("actionId", uploadType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
            L.i("getUploadToken uid = "+uid);
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(uid, Request.Method.POST,
                Urls.URL_UPLOAD_TOKEN, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                L.i("getUploadToken per_gettoken=" + response.toString());
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    UploadTokenBean tokenBean = parseTokenJson(response.toString());
                    callback.onSuccess(tokenBean);
                } else {
                    String netErrorText = GloabalRequestUtil.getNetErrorMsg(response);
                    callback.onFail(netErrorText);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private static UploadTokenBean parseTokenJson(String tokenJson) {
        Gson gson = new Gson();
        UploadTokenBean tokenBean = gson.fromJson(tokenJson, UploadTokenBean.class);
        return tokenBean;
    }

    /**
     *   上传文件
     * @param uploadManager
     * @param file
     * @param callback
     * @param tokenBean
     * @param actionId
     * @param fileKey
     */
    public static void uploadFile(UploadManager uploadManager, final File file, final UploadCallback callback, UploadTokenBean.ResultBean tokenBean, String actionId, String fileKey) {
        if (callback != null) {
            callback.onPrepare();
        }

        String uploadId = tokenBean.getUploadId();
        String tokenStr = tokenBean.getToken();

        if(TextUtils.isEmpty(uploadId) || TextUtils.isEmpty(tokenStr)) {
            callback.onFail("上传凭证异常");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("x:uploadId", uploadId);
        params.put("x:actionId", actionId);

        UploadOptions uploadOptions = new UploadOptions(params, null, false, null, null);
        uploadManager.put(file, fileKey, tokenStr, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {

//                Log.e("lzm", "key="+key);
//                Log.e("lzm", "info="+info);
//                Log.e("lzm", "res="+res);

                if(info == null || !info.isOK()) {
                    Log.e("lzm", "error="+info.error+"_isok="+info.isOK());
                    callback.onFail("上传失败");
                    return;
                }

                String serverFilePath = null;
                boolean isOk = GloabalRequestUtil.isRequestOk(res);
                if(isOk) {
                    JSONObject resultObj = res.optJSONObject("result");
                    if(resultObj != null) {
                        serverFilePath = resultObj.optString("name");
                    }
                } else {
                    L.e("isOk = no 上传失败");
                    callback.onFail("上传失败");
                    return;
                }

                if(!TextUtils.isEmpty(serverFilePath)) {
                    String localFileUri = "file://" + file.getAbsolutePath();
                    if (callback != null) {
                        callback.onSuccess(localFileUri, serverFilePath);
                    }
                }else {
                    if (callback != null) {
                        L.e("callback = null");
                        callback.onFail("上传失败");
                    }
                }


                //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                Log.e("lzm", "key="+key);
                Log.e("lzm", "isok="+info.isOK());
                Log.e("lzm",  "res=" + res);
            }
        }, uploadOptions);

    }

    public interface UploadCallback {
        void onPrepare();
        void onSuccess(String localFileUri, String serverFilePath);
        void onFail(String error);
    }

    public interface UploadTokenCallback {
        void onPrepare();
        void onSuccess(UploadTokenBean token);
        void onFail(String error);
    }
}
