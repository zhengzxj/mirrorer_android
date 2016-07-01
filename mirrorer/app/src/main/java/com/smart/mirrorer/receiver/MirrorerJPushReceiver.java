package com.smart.mirrorer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.PushQuestionMsgEvent;
import com.smart.mirrorer.event.PushTutorInfoMsgEvent;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.service.QuestionService;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lzm on 16/4/16.
 */
public class MirrorerJPushReceiver extends BroadcastReceiver {

    private final String TAG = "lzm";

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.i("lzm","[JPushReceiver] action = "+intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            //收到对方接通的推送
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            if(Urls.aMode)
                /*
                1.提问者取消提问的推送;
                内容:tilte=__message=问题被取消
                __extras={
                        "type":2,
                        "qid":"MQ20160520185425363340",
                        "action":"cancle-question"}
                2.对方拨打给我的推送
                内容:tilte=
                __message=
                __extras={
                        "headImgUrl":"http:\/\/static.mirrorer.com\/head_1463709132744.jpg"
                        ,"dynamicKey":"0043671f14bbf6ccb75eb2accb33c6016b070bf8143f01d3b0ff77942b4b313307cfc79fb09146518741500bd13190000000000",
                        "channelName":"ND20160606123015600195",
                        "numberId":1645031426,
                        "action":"notice-call",
                        "realName":"郑飞",
                        "question":"你好",
                        "type":1}
                 */

                processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {//(OK)
            //1.接到提问者提问的推送:
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            String alertText =bundle.getString(JPushInterface.EXTRA_ALERT);//问题
            String extText =bundle.getString(JPushInterface.EXTRA_EXTRA);//问题json信息串
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);

            Log.e("lzm", "alert=" + alertText + "__extra Text=" + extText + "__msg=" + msg);
/*
1.alert=你好__extra Text={
        "qid":"MQ20160520185425363340",
        "action":"match-guider",
        "ts":1465184872644,
        "type":2,
        "source":"8C344AEA91CC34670D5E2FB8620D3002"
    }__msg=null
 */
            if(!TextUtils.isEmpty(extText)) {
                operatePushMsg(alertText, extText);
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            Log.d(TAG, "[JPushReceiver] 用户点击打开了通知");
            JPushInterface.reportNotificationOpened(context, bundle.getString(JPushInterface.EXTRA_MSG_ID));

            // 打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            JPushInterface.clearAllNotifications(context);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    /**
     * 处理推送过来的通知消息
     * @param alertText
     * @param extText
     */
    private void operatePushMsg(String alertText, String extText) {

        //type 1回答者 2问题
        try {
            JSONObject jsonObject  = new JSONObject(extText);
            int type = jsonObject.optInt("type");
//            if(type == 1) {
//                BusProvider.getInstance().post(new PushTutorInfoMsgEvent(extText));
//            } else
            if(type == 2) {
                jsonObject.put("content", alertText);//把问题放入到content中去
//                BusProvider.getInstance().post(new PushQuestionMsgEvent(jsonObject.toString()));
                updateMatchQuestion(jsonObject.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 在QuestionService中处理推送过来的问题
     * @param msg
     */
    private void updateMatchQuestion(String msg) {
        Gson gson = new Gson();
        PushQuestionBean fromJson = gson.fromJson(msg, PushQuestionBean.class);
        Log.e("lzm", "updateMatchQuestion_fraomJosn=" + fromJson);
        if (fromJson == null) {
            return;
        }

        long times = fromJson.getTs();
        Log.e("lzm", "times="+times);

        String action = fromJson.getAction();
        Intent serviceIntent = new Intent(mContext, QuestionService.class);
        //取消问题
        if ("cancle-question".equals(action)) {
            serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_DELETE);
        } else {//新发来的问题,更新问题
            fromJson.isEnable = true;
            serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_INSERT);
        }
        serviceIntent.putExtra(QuestionService.KEY_DATA, fromJson);
        mContext.startService(serviceIntent);
    }


    /**
     * 对方接通
     * @param context
     * @param bundle
     */
    private void processCustomMessage(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.e("lzm", "tilte="+title+"__message="+message+"__extras="+extras);

        if(!TextUtils.isEmpty(extras)) {
            //type 1回答者2问题
            try {
                JSONObject jsonObject  = new JSONObject(extras);
                int type = jsonObject.optInt("type");
                if(type == 1) {
                    Log.i("lzm","MirrorerJPushReceiver - PushTutorInfoMsgEvent");
                    BusProvider.getInstance().post(new PushTutorInfoMsgEvent(extras));
                } else {
                    Gson gson = new Gson();
                    PushQuestionBean fromJson = gson.fromJson(extras, PushQuestionBean.class);
                    Log.e("lzm", "hehe=" + fromJson);
                    if (fromJson == null) {
                        return;
                    }

                    String action = fromJson.getAction();
                    Intent serviceIntent = new Intent(mContext, QuestionService.class);
                    if ("cancle-question".equals(action)) {
                        serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_DELETE);
                        serviceIntent.putExtra(QuestionService.KEY_DATA, fromJson);
                        mContext.startService(serviceIntent);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
