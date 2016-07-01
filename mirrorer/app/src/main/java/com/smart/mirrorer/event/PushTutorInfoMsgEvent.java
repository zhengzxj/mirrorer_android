package com.smart.mirrorer.event;

import com.google.gson.JsonObject;
import com.videorecorder.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/4/21.
 */
public class PushTutorInfoMsgEvent {
    public String msgContent;
    public JSONObject pushTutorInfoMsgObj;
    public PushTutorInfoMsgEvent(String msg) {
        super();
        this.msgContent = msg;
        try
        {
            pushTutorInfoMsgObj = new JSONObject(msg);
        }
        catch (JSONException e)
        {
            Log.e("lzm","PushTutorInfoMsgObj 转化异常");
            e.printStackTrace();
        }
    }
}
