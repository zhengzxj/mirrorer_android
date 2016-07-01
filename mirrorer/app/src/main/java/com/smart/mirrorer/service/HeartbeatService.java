package com.smart.mirrorer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.util.Urls;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzm on 16/3/26.
 */
public class HeartbeatService extends Service {

    private static final long DEF_LOOP_TIME = 1000 * 60 * 3;
    private static volatile boolean isRunLooper = false;

    private static LoopHandler sLoopHandler;
    private static Handler sMainHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        // ui线程
        sMainHandler = new Handler(getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!isRunLooper)
                    return;
                int what = msg.what;
                if (what == 1) {
//                    Log.e("lzm", "心跳正常");
                }
            }

        };

        //子线程
        HandlerThread looperThread = new HandlerThread("msg-looper-thread");
        looperThread.start();
        Looper looper = looperThread.getLooper();
        sLoopHandler = new LoopHandler(looper);
    }

    private String mUid;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (intent != null) {
            mUid = intent.getStringExtra("app_uid_service");
        }

        Log.e("lzm", "onstart__mUid=" + mUid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class LoopHandler extends Handler {

        public LoopHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isRunLooper)
                return;
            long loopDelayed = DEF_LOOP_TIME;
            int what = msg.what;
            if (what == 1) {
                requestHeart();
            }

            // 延迟发送。
            this.sendEmptyMessageDelayed(1, loopDelayed);
        }

    }

    private void requestHeart() {
        if (TextUtils.isEmpty(mUid)) {
            return;
        }

        String tag_json_obj = "json_string_heart_req";

        StringRequest jsonObjReq = new StringRequest(Request.Method.HEAD,
                Urls.URL_HEART_CONNECT,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("lzm", "heart___ =" + response);
//                        if(GloabalRequestUtil.isRequestOk(response)) {
                        sMainHandler.sendEmptyMessage(1);
//                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("lzm", "heart_error:" + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("platform", "1");
                headers.put("uid", mUid);
                return headers;
            }

        };

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    // 启动轮询
    public static boolean startLoop() {
        if (sLoopHandler != null) {
            isRunLooper = true;
            sLoopHandler.sendEmptyMessage(1);
            return true;
        }
        return false;
    }

    // 停止轮询
    public static void stopLoop() {
        isRunLooper = false;
        if (sLoopHandler != null) {
            sLoopHandler.removeMessages(1);
        }
    }
}
