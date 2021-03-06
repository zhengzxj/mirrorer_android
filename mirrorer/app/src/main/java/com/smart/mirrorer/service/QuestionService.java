package com.smart.mirrorer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.db.MatchQuestionProviderService;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.UpdateQuestionEvent;
import com.smart.mirrorer.util.mUtil.L;
import com.videorecorder.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lzm on 16/5/5.
 */
public class QuestionService extends Service {
    public static final String KEY_ACTION = "key_service_action";
    public static final String ACTIOIN_DELETE = "action_delete";
    public static final String ACTIOIN_INSERT = "action_insert";

    public static final String KEY_DATA = "key_data";

    private MatchQuestionProviderService mQuestionService;
    private ExecutorService mSingleThreadExecutor = Executors.newSingleThreadExecutor();

    private String mQid;
    private boolean isDeleteType;
    //QuestionService.KEY_ACTION, QuestionService.ACTIOIN_INSERT

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("lzm", "question_service_oncreate....");
        mQuestionService = new MatchQuestionProviderService(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("lzm", "qustionService启动模式....intent=" + intent + "__flags=" + flags + "__startId=" + startId);

        if(intent!= null) {
            Bundle extra = intent.getExtras();
            String action = extra.getString(KEY_ACTION);
            Log.e("lzm", "action="+action);
            PushQuestionBean questionData = extra.getParcelable(KEY_DATA);
            if(questionData != null) {
                L.i("questionData = "+questionData);
                mQid = questionData.getQid();
                if (ACTIOIN_DELETE.equals(action)) {
                    L.i("取消问题 questionData qid = "+questionData.getQid());
                    isDeleteType = true;
                    operateQuestionDB(questionData, true);
                } else if (ACTIOIN_INSERT.equals(action)) {
                    Log.e("lzm", "insert.....");
                    isDeleteType = false;
                    operateQuestionDB(questionData, false);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        L.i("post UpdateMatchQuestionEvent");
                        BusProvider.getInstance().post(new UpdateQuestionEvent(mQid, isDeleteType));
                    }
                }, 1000);
            }
        }
        return Service.START_REDELIVER_INTENT;
    }

    private void operateQuestionDB(final PushQuestionBean data, final boolean isDelete) {
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronizedDB(data, isDelete);
            }
        });
    }

    private synchronized void synchronizedDB(PushQuestionBean data, boolean isDelete) {
        if (mQuestionService == null) {
            mQuestionService = new MatchQuestionProviderService(getApplicationContext());
        }
        if (isDelete) {
            mQuestionService.deleteHistorysWithQid(data.getQid());
        } else {
            mQuestionService.saveOrUpdateHistory(data, data.getQid());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("lzm", "queston_service_destory....");
        if(mSingleThreadExecutor != null && !mSingleThreadExecutor.isShutdown()) {
            mSingleThreadExecutor.shutdown();
        }
    }
}
