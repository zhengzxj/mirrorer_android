package com.smart.mirrorer.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.LoginActivity;
import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.PushTutorInfoMsgEvent;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;


public class MqttService extends Service implements MqttCallback {
    private static final String ACTION_MATCH_GUIDER = "match-guider";
    public static final String TOPIC_DEV_HEAD = "res/dev/";
    public static final String TOPIC_PRO_HEAD = "res/pro/";
    private MirrorSettings mSettings;
    public static final String DEBUG_TAG = "MqttService"; // Debug TAG

    private static final String MQTT_THREAD_NAME = "MqttService[" + DEBUG_TAG + "]"; // Handler Thread ID
//    112.74.213.175:1884
    private static final String MQTT_BROKER = "112.74.213.175"; // Broker URL or IP Address
    private static final int MQTT_PORT = 1884; // Broker Port

    public static final int MQTT_QOS_0 = 0; // QOS Level 0 ( Delivery Once no confirmation )
    public static final int MQTT_QOS_1 = 1; // QOS Level 1 ( Delevery at least Once with confirmation )
    public static final int MQTT_QOS_2 = 2; // QOS Level 2 ( Delivery only once with confirmation with handshake )

    private static final int MQTT_KEEP_ALIVE = 240000; // KeepAlive Interval in MS
    private static final String MQTT_KEEP_ALIVE_TOPIC_FORAMT = "/users/%s/keepalive"; // Topic format for KeepAlives
    private static final byte[] MQTT_KEEP_ALIVE_MESSAGE = {0}; // Keep Alive message to send
    private static final int MQTT_KEEP_ALIVE_QOS = MQTT_QOS_0; // Default Keepalive QOS

    private static final boolean MQTT_CLEAN_SESSION = true; // Start a clean session?

    private static final String MQTT_URL_FORMAT = "tcp://%s:%d"; // URL Format normally don't change

    public static final String ACTION_START = DEBUG_TAG + ".START"; // Action to start
    private static final String ACTION_STOP = DEBUG_TAG + ".STOP"; // Action to stop
    private static final String ACTION_KEEPALIVE = DEBUG_TAG + ".KEEPALIVE"; // Action to keep alive used by alarm manager
    private static final String ACTION_RECONNECT = DEBUG_TAG + ".RECONNECT"; // Action to reconnect


    private static final String DEVICE_ID_FORMAT = "%s"; // Device ID Format, add any prefix you'd like
    // Note: There is a 23 character limit you will get
    // An NPE if you go over that limit
    private boolean mStarted = false;   // Is the Client started?
    private String mDeviceId;       // Device ID, Secure.ANDROID_ID
    private Handler mConnHandler;     // Seperate Handler thread for networking

//    private MqttDefaultFilePersistence mDataStore; // Defaults to FileStore
    private MemoryPersistence mMemStore; // On Fail reverts to MemoryStore
    private MqttConnectOptions mOpts; // Connection Options

    private MqttTopic mKeepAliveTopic; // Instance Variable for Keepalive topic

    private MqttClient mClient; // Mqtt Client

    private AlarmManager mAlarmManager; // Alarm manager to perform repeating tasks
    private ConnectivityManager mConnectivityManager; // To check for connectivity changes

    /**
     * Start MQTT Client
     *
     * @param
     * @return void
     */
    public static void actionStart(Context ctx) {
        Log.i(DEBUG_TAG, "actionStart");
        Intent i = new Intent(ctx, MqttService.class);
        i.setAction(ACTION_START);
        ctx.startService(i);
    }

    /**
     * Stop MQTT Client
     *
     * @param
     * @return void
     */
    public static void actionStop(Context ctx) {
        Log.i(DEBUG_TAG, "actionStop");
        Intent i = new Intent(ctx, MqttService.class);
        i.setAction(ACTION_STOP);
        ctx.startService(i);
    }

    /**
     * Send a KeepAlive Message
     *
     * @param
     * @return void
     */
    public static void actionKeepalive(Context ctx) {
        Log.i(DEBUG_TAG, "actionKeepalive");
        Intent i = new Intent(ctx, MqttService.class);
        i.setAction(ACTION_KEEPALIVE);
        ctx.startService(i);
    }

    /**
     * Initalizes the DeviceId and most instance variables
     * Including the Connection Handler, Datastore, Alarm Manager
     * and ConnectivityManager.
     */
    private String mUid;

    @Override
    public void onCreate() {
        Log.i(DEBUG_TAG, "onCreate");
        super.onCreate();

//            mDeviceId = String.format(DEVICE_ID_FORMAT,
//                                      Secure.getString(getContentResolver(), Secure.ANDROID_ID));

        HandlerThread thread = new HandlerThread(MQTT_THREAD_NAME);
        thread.start();

        mConnHandler = new Handler(thread.getLooper());

//        try {
//            mDataStore = new MqttDefaultFilePersistence(getCacheDir().getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//            mDataStore = null;
            mMemStore = new MemoryPersistence();
//        }

        mOpts = new MqttConnectOptions();
        mOpts.setCleanSession(MQTT_CLEAN_SESSION);
        // Do not set keep alive interval on mOpts we keep track of it with alarm's

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    /**
     * Service onStartCommand
     * Handles the action passed via the Intent
     *
     * @return START_REDELIVER_INTENT
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(DEBUG_TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        String action = intent.getAction();

        Log.i(DEBUG_TAG, "Received action of " + action);

        mUid = BaseApplication.getInstance().getSettings(this).APP_UID.getValue();

        mDeviceId = String.format(DEVICE_ID_FORMAT, mUid);

        Log.e(DEBUG_TAG, "onStartCommand uid = " + mUid + ",mDeviceId = " + mDeviceId);

        if (action == null) {
            Log.i(DEBUG_TAG, "Starting service with no action\n Probably from a crash");
        } else {
            if (action.equals(ACTION_START)) {
                Log.i(DEBUG_TAG, "Received ACTION_START");
                start();
            } else if (action.equals(ACTION_STOP)) {
                stop();
            } else if (action.equals(ACTION_KEEPALIVE)) {
                keepAlive();
            } else if (action.equals(ACTION_RECONNECT)) {
                if (isNetworkAvailable()) {
                    reconnectIfNecessary();
                }
            }
        }

        return START_REDELIVER_INTENT;
    }

    /**
     * Attempts connect to the Mqtt Broker
     * and listen for Connectivity changes
     * via ConnectivityManager.CONNECTVITIY_ACTION BroadcastReceiver
     */
    private synchronized void start() {
        Log.i(DEBUG_TAG, "start");
        if (mStarted) {
            Log.i(DEBUG_TAG, "Attempt to start while already started");
            return;
        }

        if (hasScheduledKeepAlives()) {
            stopKeepAlives();
        }

        connect();

        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Attempts to stop the Mqtt client
     * as well as halting all keep alive messages queued
     * in the alarm manager
     */
    private synchronized void stop() {
        Log.i(DEBUG_TAG, "stop");
        if (!mStarted) {
            Log.i(DEBUG_TAG, "Attemtpign to stop connection that isn't running");
            return;
        }

        if (mClient != null) {
            mConnHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mClient.disconnect();
                    } catch (MqttException ex) {
                        ex.printStackTrace();
                    }
                    mClient = null;
                    mStarted = false;

                    stopKeepAlives();
                }
            });
        }

        unregisterReceiver(mConnectivityReceiver);
    }

    /**
     * Connects to the broker with the appropriate datastore
     */
    private synchronized void connect() {
        Log.i(DEBUG_TAG, "connect");
        String url = String.format(Locale.US, MQTT_URL_FORMAT, MQTT_BROKER, MQTT_PORT);
        Log.i(DEBUG_TAG, "Connecting with URL: " + url);
        try {
//            if (mDataStore != null) {
//                Log.i(DEBUG_TAG, "Connecting with DataStore");
//                mClient = new MqttClient(url, mDeviceId, mDataStore);
//            } else {
                Log.i(DEBUG_TAG, "Connecting with MemStore");
                mClient = new MqttClient(url, mDeviceId, mMemStore);

//            }
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mConnHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mClient.connect(mOpts);
                    Log.i(DEBUG_TAG, "subscribed = " + TOPIC_DEV_HEAD + mUid);
                    mClient.subscribe(TOPIC_DEV_HEAD + mUid, 0);

                    mClient.setCallback(MqttService.this);

                    mStarted = true; // Service is now connected

                    Log.i(DEBUG_TAG, "Successfully connected and subscribed starting keep alives");

                    startKeepAlives();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Schedules keep alives via a PendingIntent
     * in the Alarm Manager
     */
    private void startKeepAlives() {
        Log.i(DEBUG_TAG, "startKeepAlives");
        Intent i = new Intent();
        i.setClass(this, MqttService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + MQTT_KEEP_ALIVE,
                MQTT_KEEP_ALIVE, pi);
    }

    /**
     * Cancels the Pending Intent
     * in the alarm manager
     */
    private void stopKeepAlives() {
        Log.i(DEBUG_TAG, "stopKeepAlives");
        Intent i = new Intent();
        i.setClass(this, MqttService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mAlarmManager.cancel(pi);
    }

    /**
     * Publishes a KeepALive to the topic
     * in the broker
     */
    private synchronized void keepAlive() {
        Log.i(DEBUG_TAG, "keepAlive");
        if (isConnected()) {
            try {
                sendKeepAlive();
                return;
            } catch (MqttConnectivityException ex) {
                Log.e(DEBUG_TAG, "MqttConnectivityException");
                ex.printStackTrace();
                reconnectIfNecessary();
            } catch (MqttPersistenceException ex) {
                Log.e(DEBUG_TAG, "MqttPersistenceException");
                ex.printStackTrace();
                stop();
            } catch (MqttException ex) {
                Log.e(DEBUG_TAG, "MqttException");
                ex.printStackTrace();
                stop();
            }
        }
    }

    /**
     * Checkes the current connectivity
     * and reconnects if it is required.
     */
    private synchronized void reconnectIfNecessary() {
        Log.i(DEBUG_TAG, "reconnectIfNecessary");
        if (mStarted && mClient == null) {
            connect();
        }
    }

    /**
     * Query's the NetworkInfo via ConnectivityManager
     * to return the current connected state
     *
     * @return boolean true if we are connected false otherwise
     */
    private boolean isNetworkAvailable() {
        Log.i(DEBUG_TAG, "isNetworkAvailable");
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();

        return (info == null) ? false : info.isConnected();
    }

    /**
     * Verifies the client State with our local connected state
     *
     * @return true if its a match we are connected false if we aren't connected
     */
    private boolean isConnected() {
        Log.i(DEBUG_TAG, "isConnected");
        if (mStarted && mClient != null && !mClient.isConnected()) {
            Log.i(DEBUG_TAG, "Mismatch between what we think is connected and what is connected");
        }

        if (mClient != null) {
            return (mStarted && mClient.isConnected()) ? true : false;
        }

        return false;
    }

    /**
     * Receiver that listens for connectivity chanes
     * via ConnectivityManager
     */
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(DEBUG_TAG, "mConnectivityReceiver");
        }
    };

    /**
     * Sends a Keep Alive message to the specified topic
     *
     * @return MqttDeliveryToken specified token you can choose to wait for completion
     */
    private synchronized MqttDeliveryToken sendKeepAlive()
            throws MqttConnectivityException, MqttPersistenceException, MqttException {
        Log.i(DEBUG_TAG, "sendKeepAlive");
        if (!isConnected())
            throw new MqttConnectivityException();

        if (mKeepAliveTopic == null) {
            mKeepAliveTopic = mClient.getTopic(
                    String.format(Locale.US, MQTT_KEEP_ALIVE_TOPIC_FORAMT, mDeviceId));
        }

        Log.i(DEBUG_TAG, "Sending Keepalive to " + MQTT_BROKER);

        MqttMessage message = new MqttMessage(MQTT_KEEP_ALIVE_MESSAGE);
        message.setQos(MQTT_KEEP_ALIVE_QOS);

        return mKeepAliveTopic.publish(message);
    }

    /**
     * Query's the AlarmManager to check if there is
     * a keep alive currently scheduled
     *
     * @return true if there is currently one scheduled false otherwise
     */
    private synchronized boolean hasScheduledKeepAlives() {
        Log.i(DEBUG_TAG, "hasScheduledKeepAlives");
        Intent i = new Intent();
        i.setClass(this, MqttService.class);
        i.setAction(ACTION_KEEPALIVE);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_NO_CREATE);

        return (pi != null) ? true : false;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Connectivity Lost from broker
     */
    @Override
    public void connectionLost(Throwable arg0) {
        Log.i(DEBUG_TAG, "connectionLost");
        stopKeepAlives();

        mClient = null;

        if (isNetworkAvailable()) {
            reconnectIfNecessary();
        }
    }

    private Context mContext;

    private String getMessageAction(String msg) {
        try {
            JSONObject job = new JSONObject(msg);
            if (job.has("action"))
                return job.getString("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {
        Log.i(DEBUG_TAG, "messageArrived message = " + message);
        this.mContext = getApplicationContext();
        String action = getMessageAction(message.toString());//提问的action = match-guider match-guider
        JSONObject jo = new JSONObject(message.toString());
        int type = 0;
        if (jo.has("type"))
        {
            type = jo.getInt("type");
        }

        if (MqttService.ACTION_MATCH_GUIDER.equals(action)) {

            if (!TextUtils.isEmpty(message.toString())) {
                operatePushMsg(message.toString());
                JSONObject job = new JSONObject(message.toString());
                showNotification(job.getString("name"),job.getString("question"));
            }
            return;
        } else if (type == 2){
            Gson gson = new Gson();
            PushQuestionBean fromJson = gson.fromJson(message.toString(), PushQuestionBean.class);
            Intent serviceIntent = new Intent(mContext, QuestionService.class);
            if("cancle-question".equals(action))
            {
                if (fromJson == null) return;
                serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_DELETE);
                serviceIntent.putExtra(QuestionService.KEY_DATA, fromJson);
            }else
            {
                fromJson.isEnable = true;
                serviceIntent.putExtra(QuestionService.KEY_ACTION, QuestionService.ACTIOIN_INSERT);
            }
            mContext.startService(serviceIntent);
            return;
        } else if(type==1)
        {
            L.i("mqtt type == 1");
            BaseApplication.mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    BusProvider.getInstance().post(new PushTutorInfoMsgEvent(message.toString()));
                }
            });
            return;
        }
        L.e("还有action为:"+action+"的消息为处理");
    }

    /**
     * 对方接通
     *
     * @param context
     */
    private void processCustomMessage(Context context, String extras) {

        if (!TextUtils.isEmpty(extras)) {
            //type 1回答者2问题
            try {
                JSONObject jsonObject = new JSONObject(extras);
                int type = jsonObject.optInt("type");
                if (type == 1) {
                    Log.i("lzm", "MirrorerJPushReceiver - PushTutorInfoMsgEvent");
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

    /**
     * 处理推送过来的通知消息
     */
    private void operatePushMsg(String msg) {

        //type 1回答者 2问题
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.optInt("type") == 2) {
                Gson gson = new Gson();
                PushQuestionBean fromJson = gson.fromJson(msg, PushQuestionBean.class);
                Log.e("lzm", "updateMatchQuestion_fraomJosn=" + fromJson);
                if (fromJson == null) {
                    return;
                }

                long times = fromJson.getTs();
                Log.e("lzm", "times=" + times);

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(DEBUG_TAG, "deliveryComplete");
    }
    private NotificationCompat.Builder mBuilder;
    private int noticId;
    private int noticeTotleCount;
    private NotificationManager mNotificationManager;
    public void cancleCurrentNotice(int id) {
        if(mNotificationManager!=null)
            mNotificationManager.cancel(id);
    }
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
                new Intent(), flags);
        return pendingIntent;
    }
    private void showNotification( String contentTile,String contentText) {
        int id = noticId++;
        noticeTotleCount++;
        // 1.
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 2.
        mBuilder = new NotificationCompat.Builder(getApplicationContext());

        // 3.
        mBuilder.setContentTitle(contentTile)
                // 设置通知栏标题
                .setContentText(contentText)
                // 设置通知栏显示内容
                .setContentIntent(
                        getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                // 设置通知栏点击意图
                // .setNumber(number) //设置通知集合的数量
                .setTicker(contentText)
                // 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT)
                // 设置该通知优先级
                 .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)
                // ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                // .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setDefaults(Notification.DEFAULT_LIGHTS)
                // .setSou
                // Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE
                // permission
                .setSmallIcon(R.drawable.ticker);// 设置通知小ICON
        // 通知首次出现在通知栏，带上升动画效果的
        // if (!isForeground) // 只有后台才显示
        mBuilder.setWhen(System.currentTimeMillis());
        // else
        // {
        // mBuilder.setContent(null);
        // }
        // 没开启群消息免打扰

        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        Notification notification = mBuilder.build();
        // notification.flags = Notification.FLAG_ONGOING_EVENT;
        // notification.flags = Notification.FLAG_NO_CLEAR;// 点击清除的时候不清除
        notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击后消失
        // Intent intent = new Intent(getApplicationContext(),
        // LoginActivity.class);
        // 自定义声音 声音文件放在ram目录下，没有此目录自己创建一个

        String ringtoneStr = "android.resource://" + "com.smart.mirrorer/" + R.raw.notice_sound;
        mBuilder.setSound(TextUtils.isEmpty(ringtoneStr) ? null
                : Uri.parse(ringtoneStr));

        Intent intent = new Intent(getApplicationContext(),
                LoginActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        // Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // .putExtra("number", getIntent().getExtras().getString("number"))
        // .putExtra("name", "*!@*");
        intent.putExtra("id", id);
        L.d("intent.putExtra(id, id);===============" + id);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), (int) System.currentTimeMillis(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(id, mBuilder.build());
    }
    /**
     * MqttConnectivityException Exception class
     */
    private class MqttConnectivityException extends Exception {
        private static final long serialVersionUID = -7385866796799469420L;
    }
}
