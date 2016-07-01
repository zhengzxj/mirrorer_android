package com.smart.mirrorer.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.qiniu.android.storage.UploadManager;
import com.smart.mirrorer.R;
import com.smart.mirrorer.agora.BaseEngineEventHandlerActivity;
import com.smart.mirrorer.agora.MessageHandler;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.WechatConfig;
import com.smart.mirrorer.net.RequestManager;
import com.smart.mirrorer.service.MqttService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.mUtil.L;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import io.agora.rtc.RtcEngine;

/**
 * Created by lzm on 16/3/24.
 */
public class BaseApplication extends Application {
    private RtcEngine rtcEngine;
    private MessageHandler messageHandler;
    public static Looper mMainThreadLooper;
    public static Handler mMainThreadHandler;
    public static Thread mMainThread;
    public static long mMainThreadId;
    public static final String TAG = BaseApplication.class.getSimpleName();

//    public String rid;//Jpush的机器id
    private static BaseApplication mInstance;

    //微信api对象
    private IWXAPI mWeixinMsgApi;

    //保证使用同一个请求队列
//    private RequestQueue mRequestQueue;
    private RequestManager mRequestManager;

    public static ImageLoader mImageLoader = ImageLoader.getInstance();

    /**
     * 通用模式值
     */
    public static DisplayImageOptions options;

    /**
     * 头像模式值
     */
    public static DisplayImageOptions headOptions;

    private MirrorSettings mSettings;
    private UploadManager mUploadManager;

    //agora
//    private RtcEngine rtcEngine;
//    private MessageHandler messageHandler;
    @Override
    public void onCreate() {
        super.onCreate();

        messageHandler = new MessageHandler();

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        if (shouldInit()) {
            mInstance = this;

            mSettings = new MirrorSettings(this);
            mUploadManager = new UploadManager();
//            JPushInterface.setDebugMode(true);
//            JPushInterface.resumePush(this);
//            JPushInterface.init(this);

            if(!TextUtils.isEmpty(mSettings.APP_UID.getValue()))
            {
                MqttService.actionStart(this);
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(5000);
//                        rid = JPushInterface.getRegistrationID(BaseApplication.getInstance());
//                        L.e("极光"+rid);
//                        Log.e("lzm","极光"+rid);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

            mWeixinMsgApi = WXAPIFactory.createWXAPI(this, WechatConfig.APP_ID, false);
            boolean isRestgister = mWeixinMsgApi.registerApp(WechatConfig.APP_ID);
            L.d( "registe="+isRestgister);

            /**
             * 初始化语音
             */
            SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getResources().getString(R.string.ifk_voice_key));
//            /**
//             * 初始化justalk相关
//             */
//            int initCode = LoginDelegate.init(this, getString(R.string.justalk_app_key));
//            if (initCode != LoginDelegate.InitStat.MTC_INIT_SUCCESS) {
//                L.d( "初始化justalk sdk失败");
//            } else {
//                L.d( "初始化justalk sdk成功");
//                MtcCallDelegate.init(this);
//                JusCallConfig.setBackIntentAction("com.smart.mirrorer.call.action.backfromcall");
//                MiPush.setCallPushParm();
//            }

            /********** 异步下载图片缓存类 初始化 */
            initImageLoader(getApplicationContext());
            options = optionsInit(0);
            headOptions = optionsInit(1);

            //agora初始化
            //1.初始化 IRtcEngineEventHandler 继承类
//            messageHandler = new MessageHandler();

            mMainThread = Thread.currentThread();
            mMainThread.run();
            mMainThreadId = android.os.Process.myTid();// 当前线程id

            // 主线程handler
            mMainThreadHandler = new Handler();

            //
            mMainThreadLooper = getMainLooper();
        }

    }
    public void setRtcEngine(String vendorKey){

        if(rtcEngine==null) {
            rtcEngine = RtcEngine.create(getApplicationContext(), vendorKey, messageHandler);
        }
    }

    public RtcEngine getRtcEngine(){

        return rtcEngine;
    }

    public void setEngineEventHandlerActivity(BaseEngineEventHandlerActivity engineEventHandlerActivity){
        messageHandler.setActivity(engineEventHandlerActivity);
    }



    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

//    public RequestQueue getRequestQueue() {
//        if (mRequestQueue == null) {
//            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        }
//        return mRequestQueue;
//    }

    public RequestManager getRequestManager() {
        if(mRequestManager == null) {
            mRequestManager =RequestManager.getInstance(getApplicationContext());
        }
        return mRequestManager;
    }

    /**
     * 初始化options
     *
     * flag 根据不同的flag返回不同的option，主要是默认图片不同
     * @return
     */
    private DisplayImageOptions optionsInit(int flag) {
        int sourceId = R.drawable.app_default_head_icon;
//
        if (flag == 0) {
            sourceId = R.drawable.defuat_error_pic;
        } else if (flag == 1) {
            sourceId = R.drawable.app_default_head_icon;
        }

        return new DisplayImageOptions.Builder()
                .showStubImage(sourceId)// 加载等待 时显示的图片
                .showImageForEmptyUri(sourceId)// 加载数据为空时显示的图片
                .showImageOnFail(sourceId)// 加载失败时显示的图片
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory()//在內存中做緩存
                .cacheOnDisc() //在磁盤進行緩
                .build();

    }


    /**
     * 图片加载库初始化
     */
    private void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3)//内部最多三个线程加载数据
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(3 * 1024 * 1024)//内存缓存的大小为3M
                .discCacheSize(30 * 1024 * 1024)//设置磁盘缓存的大小为30M
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()
                .build();
        mImageLoader.init(config);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
//        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestManager().addRequest(req, tag);
//        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
//        req.setTag(TAG);
//        getRequestQueue().add(req);
        getRequestManager().addRequest(req, TAG);
    }

    public void cancelPendingRequests(Object tag) {
//        if (mRequestQueue != null) {
//            mRequestQueue.cancelAll(tag);
//        }
        if(mRequestManager != null) {
            mRequestManager.cancelAll(tag);
        }
    }

    public static MirrorSettings getSettings(Context context) {
        if (context instanceof BaseApplication) {
            return ((BaseApplication) context).getSettings();
        }
        return new MirrorSettings(context);
    }

    private MirrorSettings getSettings() {
        return mSettings;
    }

    public static UploadManager getUploadManager(Activity activity) {
        if (activity.getApplication() instanceof BaseApplication) {
            return ((BaseApplication) activity.getApplication()).getUploadManager();
        }
        return new UploadManager();
    }

    private UploadManager getUploadManager() {
        return mUploadManager;
    }

    public static IWXAPI getWeixinMsgApi(Activity activity) {
        if (activity.getApplication() instanceof BaseApplication) {
            return ((BaseApplication) activity.getApplication()).getWeixinMsgApi();
        }

        IWXAPI weixinMsgApi = WXAPIFactory.createWXAPI(activity, null);
        weixinMsgApi.registerApp(WechatConfig.APP_ID);
        return weixinMsgApi;
    }

    public IWXAPI getWeixinMsgApi() {
        return mWeixinMsgApi;
    }

    public static long getMainThreadId() {
        return mMainThreadId;
    }
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }
}
