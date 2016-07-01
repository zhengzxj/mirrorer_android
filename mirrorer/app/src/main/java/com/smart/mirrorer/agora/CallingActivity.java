package com.smart.mirrorer.agora;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.CallConnectEvent;
import com.smart.mirrorer.event.PushTutorInfoMsgEvent;
import com.smart.mirrorer.home.PayOrderConfirmAcitivity;
import com.smart.mirrorer.home.VoiceVideoActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.agroa.AgoraManager;
import com.smart.mirrorer.util.mUtil.L;


import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class CallingActivity extends BaseEngineEventHandlerActivity implements View.OnClickListener{

    private Intent intent;
    private MediaPlayer mediaPlayer;

    private MirrorSettings mSettings;
    private String mUid;
    private String mQid;
    private String question;
    public String channelName;
    private String dynamicKey;
    private String company;
    private String title;
    private String realName;
    private String headImgUrl;
    private String targetId;

    //是呼叫还是等待
    private boolean isCall;
    //被call方 等待界面元素
    private ImageView iv_hanup;
    private ImageView iv_connect;
    private ImageView iv_other_head;
    private TextView tv_other_name;
    private TextView tv_other_company;
    private TextView tv_other_title;
    private TextView tv_question;
    //    private TextView tv_guanzhu;//关注
//    private TextView tv_zhineng;//智能
//    private TextView tv_jubao;//举报
    private ImageView iv_draw;//设置齿轮

    private PopupWindow popupWindow;
    private PopupWindow pwForSettingDraw;

    private LinearLayout rl_video_title;//标题
    //    private RelativeLayout rl_video_bottom;//底部
    private
    //通话界面
            ImageView iv_head;
    TextView tv_title1;
    TextView tv_title2;
    TextView tv_title3;

    FrameLayout user_local_view;
    FrameLayout user_remote_view;
    ImageView iv_hang_up;
    RelativeLayout rl_voice;
    RelativeLayout rl_light;

    private boolean canNotGoBack = true;

    private boolean isFinished = false;//当前页面是否已经关闭

    private boolean isActiveHangUp;//true:主动挂断 false:被动挂断

    RelativeLayout rl_call;
    Timer timer;
    private int closeActivityTime = 3;//s

    TimerTask task;

    private boolean hasOrder;//用于退出页面前判断是否要支付订单

    private boolean isJoined;
    //topView 已经进入倒计时等待消失状态
    private boolean isDelayingInVisibleTopView;
    //点击了任何按钮
    private boolean clickAnyWiget;

    private GestureDetector mGestureDetector;

    private float mVolume;//当前音量;

    private float mMaxVolume;//最大音量

    private float mBrightness;

    private AudioManager mAudioManager;

    private ProgressBar pr_voice;

    private ProgressBar pr_light;

    private boolean isTouchVoice;

    private RtcEngine rtcEngine;

    private boolean leaveForjoin;//调用join前先调一次leave 避免卡在服务器里
    //菜单按钮
    FrameLayout fl_menu;
    ImageView iv_qiehuan;//切换镜头
    ImageView iv_duifang_shipin;//我的视频
    ImageView iv_wode_shipin;//对方视频
    ImageView iv_maike;//麦克风
    ImageView iv_jingyin;//静音
    ImageView iv_fanhui ;//返回
    boolean menuIsShow;//菜单是否打开
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d("CallingActivity-oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling_video);
        BusProvider.getInstance().register(this);
        initData();initView();ring();//初始化数据,界面,打开响铃

        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        if(isCall) addChannle();//call方直接进入channle
    }

    private String oid;
    //===========数据============
    private void initData()
    {
//        mQid = intent.getStringExtra(VoiceVideoActivity.KEY_Q_ID);
        L.d("CallingActivity-initData");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = (float)(mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM ));
        L.i("progress_最大音量 = "+mMaxVolume);
        mVolume = (float)(mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM ));
        L.i("progress_当前音量 = "+mVolume);

        mBrightness = getWindow().getAttributes().screenBrightness;
        L.i("progress_当前亮度 = "+mBrightness);

        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();

        initCallOrWait();

        if(isCall)//提问者端,要获取回答者的company和title
        {
            company = intent.getStringExtra(VoiceVideoActivity.KEY_COMPANY);
            title = intent.getStringExtra(VoiceVideoActivity.KEY_TITLE);
        }
        question = intent.getStringExtra(VoiceVideoActivity.KEY_QUESTION);
        channelName = intent.getStringExtra(VoiceVideoActivity.KEY_CHANNEL_NAME);
        dynamicKey = intent.getStringExtra(VoiceVideoActivity.KEY_DYNAMIC_KEY);
        L.i("XXXYYY 通话界面拿到的 channelName = "+channelName+" , dynamicKey = "+dynamicKey);
        L.i("XXXYYY 当前对象地址1 = "+this);
        oid = channelName;
        realName = intent.getStringExtra(VoiceVideoActivity.KEY_REALNAME);
        headImgUrl = intent.getStringExtra(VoiceVideoActivity.KEY_HEADPATH_URL);
        targetId = intent.getStringExtra(VoiceVideoActivity.KEY_TARGETID);

    }

    //初始化isCall
    private void initCallOrWait()
    {
        L.d("CallingActivity-initCallOrWait");
        intent = getIntent();
        isCall = "2".equals(intent.getStringExtra(VoiceVideoActivity.KEY_TYPE))?true:false;
    }

    private TextView mDuration;
    //===========界面============
    private void initView()
    {
        mDuration = (TextView) findViewById(R.id.tv_duaration);
        L.d("CallingActivity-initView");
        rl_call = (RelativeLayout)findViewById(R.id.rl_calling);//本来在else里面,拿出来方便addChannle里的公共代码的实现

        initCommentView();

        if(isCall)//拨打方
        {
            L.i("拨打方");
            iv_hang_up.setVisibility(View.VISIBLE);
            mDuration.setVisibility(View.VISIBLE);

        }else//等待界面visible出来
        {
            iv_connect.setVisibility(View.VISIBLE);
            iv_hanup.setVisibility(View.VISIBLE);
        }

        //菜单
        fl_menu = (FrameLayout)findViewById(R.id.fl_menu);
        iv_qiehuan = (ImageView)findViewById(R.id.iv_qiehuan);//切换镜头
        iv_duifang_shipin = (ImageView)findViewById(R.id.iv_duifang_shipin);//我的视频
        iv_wode_shipin = (ImageView)findViewById(R.id.iv_wode_shipin);//对方视频
        iv_maike = (ImageView)findViewById(R.id.iv_maike);//麦克风
        iv_jingyin = (ImageView)findViewById(R.id.iv_jingyin);//静音
        iv_fanhui = (ImageView)findViewById(R.id.iv_fanhui);//返回
        iv_qiehuan.setOnClickListener(this);
        iv_duifang_shipin.setOnClickListener(this);
        iv_wode_shipin.setOnClickListener(this);
        iv_maike.setOnClickListener(this);
        iv_jingyin.setOnClickListener(this);
        iv_fanhui.setOnClickListener(this);
    }

    /**
     * 等待接通界面
     */
    private void initViewBeCallWait()
    {
        L.d("CallingActivity-initViewBeCallWait");
        //被call方选择挂断挂断1.停铃声2.调拒接接口
        //头像
        iv_other_head= (ImageView)findViewById(R.id.iv_other_head);
        BaseApplication.mImageLoader.displayImage(headImgUrl,iv_other_head,BaseApplication.headOptions);
        //名字
        ((TextView)findViewById(R.id.tv_other_name)).setText(realName);
        //公司
        tv_other_company = (TextView)findViewById(R.id.tv_other_company);
        //职位
        tv_other_title = (TextView)findViewById(R.id.tv_other_title);
        tv_other_title.setText(title);
        //问题
        tv_question= (TextView)findViewById(R.id.tv_question);
        tv_other_company.setText(company);
    }

    /**
     * 共有视频界面
     */
    private void initCommentView()
    {
        L.d("CallingActivity-initCommentView");
        pr_voice = (ProgressBar)findViewById(R.id.pr_voice);
        pr_voice.setProgress((int)(mVolume/mMaxVolume*100));
        rl_voice = (RelativeLayout)findViewById(R.id.rl_voice);
        pr_light = (ProgressBar)findViewById(R.id.pr_light);
        pr_light.setProgress((int)(mVolume/mMaxVolume*100));
        rl_light = (RelativeLayout)findViewById(R.id.rl_light);

        //接通
        iv_connect = (ImageView)findViewById(R.id.iv_connect);
        iv_connect.setOnClickListener(this);
        iv_hanup = (ImageView)findViewById(R.id.iv_hangup_becall);
        iv_hanup.setOnClickListener(this);
        iv_head = (ImageView)findViewById(R.id.iv_head);
        BaseApplication.mImageLoader.displayImage(headImgUrl, iv_head, BaseApplication.headOptions);
        tv_title1 = (TextView)findViewById(R.id.tv_title1);
        if(TextUtils.isEmpty(question))
            tv_title1.setVisibility(View.GONE);
        else
            tv_title1.setText(question);
        tv_title2 = (TextView)findViewById(R.id.tv_title2);
        tv_title2.setText(realName);
        tv_title3 = (TextView)findViewById(R.id.tv_title3);
        if(TextUtils.isEmpty(company))
        {
            tv_title3.setVisibility(View.GONE);
        }else
        {
            tv_title3.setText(company);
        }
        user_remote_view = (FrameLayout)findViewById(R.id.user_remote_view);
        user_remote_view.setOnClickListener(this);
        user_remote_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mGestureDetector.onTouchEvent(motionEvent))
                    return true;
                return false;
            }
        });
        user_local_view = (FrameLayout)findViewById(R.id.user_local_view);
        user_local_view.setOnClickListener(this);
        //设置齿轮
        iv_draw = (ImageView)findViewById(R.id.iv_draw);
        iv_draw.setOnClickListener(this);
        //底部3个按钮
//        tv_jubao = (TextView)findViewById(R.id.tv_jubao);
//        tv_jubao.setOnClickListener(this);
//        tv_zhineng = (TextView)findViewById(R.id.tv_zhineng);
//        tv_zhineng.setOnClickListener(this);
//        tv_guanzhu = (TextView)findViewById(R.id.tv_guanzhu);
//        tv_guanzhu.setOnClickListener(this);
        //需要隐藏的view
        iv_hang_up = (ImageView)findViewById(R.id.iv_hang_up);
        iv_hang_up.setOnClickListener(this);
        rl_video_title = (LinearLayout)findViewById(R.id.rl_video_title);
//        rl_video_bottom = (RelativeLayout) findViewById(R.id.rl_video_bottom);
    }
    SurfaceView localView;
    //===========尝试加入channle============

    private void addChannle()
    {

        BaseApplication.getInstance().setEngineEventHandlerActivity(this);
        BaseApplication.getInstance().setRtcEngine(dynamicKey);
        rtcEngine = BaseApplication.getInstance().getRtcEngine();
        rtcEngine.enableVideo();

        L.i("声网 开启网络测试 result = "+rtcEngine.enableNetworkTest());

        localView = rtcEngine.CreateRendererView(getApplicationContext());
        user_local_view.addView(localView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        localView.setZOrderOnTop(true);
        localView.setZOrderMediaOverlay(true);

        //声网简单视频只要这两句话
//        agManager = AgoraManager.initAgora(dynamicKey,localView,this);
//        agManager.setLocalId(R.id.user_local_view);
//        int isAgoraJoinOk = agManager.mJoinChannle(channelName);
//        L.i("声网join返回值 "+isAgoraJoinOk);
//        agManager.mSetEnableSpeakerphone();
//        agManager.mSetSpeakerphoneVolume((int)(mVolume*255/mMaxVolume));

        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rtcEngine.setupLocalVideo(new VideoCanvas(localView));//, VideoCanvas.RENDER_MODE_HIDDEN

        //设置需要的音视频流
        int muteLocalVideoStreamResult = rtcEngine.muteLocalVideoStream(false);
        int muteLocalAudioStreamResult = rtcEngine.muteLocalAudioStream(false);
        int muteAllRemoteVideoStreamsResult = rtcEngine.muteAllRemoteVideoStreams(false);
        L.d("声网muteLocalVideoStream返回值 "+muteLocalVideoStreamResult);
        L.d("声网muteLocalAudioStream返回值 "+muteLocalAudioStreamResult);
        L.d("声网muteAllRemoteVideoStreams返回值 "+muteAllRemoteVideoStreamsResult);

//        rtcEngine = AgoraManager.initAgora(this,dynamicKey,localView,channelName);
//        L.i("rtcEngine 第一次赋值 = "+BaseApplication.getInstance().rtcEngine);
        leaveForjoin = true;
        L.i("声网 dynamicKey = "+dynamicKey+", channelName = "+channelName);
        rtcEngine.joinChannel(dynamicKey,channelName,"0", 0);
        AgoraManager.mSetEnableSpeakerphone(rtcEngine,true);
        delayInvisibleTopView(false);
    }


    private int selfUid;
    //===========成功加入channle============
    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        selfUid = uid;
        L.d("CallingActivity-onJoinChannelSuccess");
        super.onJoinChannelSuccess(channel, uid, elapsed);
        join();
    }

    //===========发现有人加入channle 或者 发现该channl中已经存在的人============

    //===========发现有人离开channle========

    @Override
    public void onUserOffline(int uid) {
        L.d("CallingActivity-onUserOffline");
        L.d("exit CallingActivity-offline time = "+System.currentTimeMillis());
        super.onUserOffline(uid);
        left(true);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        super.onLeaveChannel(stats);

        L.d("exit CallingActivity-onLeaveChannel totalDuration = "+stats.totalDuration);
        L.d("exit CallingActivity-onLeaveChannel time = "+System.currentTimeMillis());

        stopRing();

        if(rtcEngine!=null){
            rtcEngine.leaveChannel();
            rtcEngine = null;
            L.i("rtcEngine onLeaveChannel赋值 = "+rtcEngine);
        }
        canNotGoBack = false;

        left(true);
    }

    SurfaceView remoteView;
    //===========设置remoteView============
    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        L.d("CallingActivity-onFirstRemoteVideoDecoded");
        super.onFirstRemoteVideoDecoded(uid, width, height, elapsed);

        //关掉声音
        stopRing();

        final int tempUid = uid;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                remoteView = RtcEngine.CreateRendererView(getApplicationContext());

                FrameLayout remoteVideoUser = (FrameLayout) findViewById(R.id.user_remote_view);
                remoteVideoUser.addView(remoteView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                rtcEngine.enableVideo();
                int successCode = rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, tempUid));
                L.i("zf,重设remoteView successCode = "+successCode);
                if (successCode < 0) {
                    L.i("zf,第一次重设remoteView successCode = "+successCode);
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int successCode = rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, tempUid));
                            if (successCode < 0) {
                                L.i("zf,第二次重设remoteView successCode = "+successCode);
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        int successCode = rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, tempUid));
                                        remoteView.invalidate();
                                    }
                                }, 500);
                            }
                            remoteView.invalidate();
                        }
                    }, 500);
                }
                remoteView.invalidate();
            }
        });
    }

    /**
     * 离开频道
     */
    private void left(final boolean needFinish)
    {
        if(rtcEngine!=null){rtcEngine.leaveChannel();rtcEngine = null;L.i("rtcEngine left赋值 = "+rtcEngine);}
        L.d("CallingActivity-left");
        String tag_json_obj = "json_obj_left_call_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("oid", channelName);
            L.d("left-oid = "+channelName);
            paramObj.put("targetId", targetId);
            L.d("left-targetId = "+targetId);
            L.d("mUid = "+mUid);
        } catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_CALL_LEFT, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
//                if(needFinish)lfetFinishActivity();
                if(needFinish)finish();
                L.d("离开频道接口返回:"+response);
                if (GloabalRequestUtil.isRequestOk(response)) {
                    L.d( "left_call_net_ok = "+response.toString());
                } else {
                    L.d( "left_call_net_error ="+GloabalRequestUtil.getNetErrorMsg(response));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                if(needFinish)lfetFinishActivity();
                if (needFinish)finish();
                L.d( "left_call_error:" + error.getMessage());
                dismissLoadDialog();
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * 加入频道
     */
    private void join()
    {
        L.d("exit CallingActivity-join time = "+System.currentTimeMillis());
        L.i("XXXYYY 当前对象地址2 = "+this);
        L.d("XXXYYY join - channelName = "+channelName);
        L.d("XXXYYY join - oid = "+oid);
        try{
            Thread.sleep(500);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        hasOrder = true;
        BusProvider.getInstance().post(new CallConnectEvent());
        canNotGoBack = true;
        String tag_json_obj = "json_obj_join_call_req";
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("oid", oid);paramObj.put("targetId", targetId);} catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_CALL_JOIN, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                closeChooseTutorUI();
                dismissLoadDialog();
                L.d( "join_call_text=" + response.toString());
                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (GloabalRequestUtil.isRequestOk(response)) {L.d( "join_call_net_ok = "+response.toString());}
                else {L.d( "join_call_net_error ="+GloabalRequestUtil.getNetErrorMsg(response));}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.d( "join_call_error:" + error.getMessage());
                closeChooseTutorUI();
                dismissLoadDialog();
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private void closeChooseTutorUI()
    {
        L.d("CallingActivity-closeChooseTutorUI");

    }
    //=======拒接调接口=======
    private void refuseNet(final boolean needFinish)
    {
        L.d("CallingActivity-refuseNet");
        String tag_json_obj = "json_obj_refuse_call_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("oid", channelName);
            paramObj.put("targetId", targetId);
        } catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_CALL_REFUSE, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

//                if(needFinish)lfetFinishActivity();
                if(needFinish)finish();
                dismissLoadDialog();
                L.d( "refuse_call_text=" + response.toString());
                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {L.d( "refuse_call_net_ok = "+response.toString());}
                else {L.d( "refuse_call_net_error ="+GloabalRequestUtil.getNetErrorMsg(response));}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.d( "refuse_call_error:" + error.getMessage());

//                if(needFinish)lfetFinishActivity();
                if(needFinish)finish();

                dismissLoadDialog();
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private int remoteUid;
    //===========数据逻辑========
    @Override
    public void onUserJoined(int uid, int elapsed) {
        remoteUid = uid;
        L.d("CallingActivity-onUserJoined");
        isJoined = true;
        super.onUserJoined(uid, elapsed);
        CallConnectEvent callConnectEvent =  new CallConnectEvent();
        callConnectEvent.setQid(mQid);
        BusProvider.getInstance().post(callConnectEvent);
        join();
        //6秒后topView消失
        delayInvisibleTopView(false);
        setupTime();
    }


    //============被拒绝===============
    private void beRefuse()
    {
        L.d("CallingActivity-beRefuse");
        L.d("被拒绝");
        canNotGoBack = false;
        stopRing();
//        lfetFinishActivity();
        finish();
    }

    //============通话结束,获取订单详情并跳转支付==========
    private void getOrderDetails() {
        L.d("CallingActivity-getOrderDetails");
        if (TextUtils.isEmpty(channelName)) {//这里channelName就是orderid
            return;
        }

        CommonUtils.getOrderDetails(CallingActivity.this, mUid, channelName, "1", mOrderDetailCallback);
    }
    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            dismissLoadDialog();
            if (orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if (detailData == null) {
                return;
            }

            L.d("订单详情"+detailData.toString());

            Intent payIntent = new Intent(CallingActivity.this, PayOrderConfirmAcitivity.class);
            payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, detailData);
            startActivity(payIntent);

            //录制信息本地化
            //TODO:录制
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };
    //===========Subscribe=========
    @Subscribe
    public void onPushTutorInfoMsgEvent(PushTutorInfoMsgEvent event) {
        L.d("CallingActivity-onPushTutorInfoMsgEvent");
        String action = "";
        try {
            action = event.pushTutorInfoMsgObj.getString("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //准备接听电话
        if("notice-refuse".equals(action))
        {
            beRefuse();
        }else if("notice-call-end".equals(action))
        {
            if(isJoined)
            {
                left(true);
            }
        }
    }


    //==========ontutch========
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        return mGestureDectector.onTouchEvent(motionEvent);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    //===========gesturelistener========
//    private class gestureListener implements GestureDetector.OnGestureListener{
//        public boolean onDown(MotionEvent e)
//        {
//            Log.i("MyGesture","onDown");
////            Toast.makeText(NewMainActivity.this,"onDown",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent motionEvent) {
//            Log.i("MyGesture","onShowPress");
////            Toast.makeText(NewMainActivity.this,"onShowPress",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent motionEvent) {
//            Log.i("MyGesture","onSingleTapUp");
////            Toast.makeText(NewMainActivity.this,"onSingleTapUp",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY) {
//            float mOldX = e1.getX(), mOldY = e1.getY();
//            float y = e2.getY();
//            Display disp = getWindowManager().getDefaultDisplay();
//            int windowWidth = disp.getWidth();
//            int windowHeight = disp.getHeight();
//            L.i("progress_windowHeight = " + windowHeight);
//
//            if (mOldX > windowWidth * 3 / 5)// 右边滑动
//            {
//                L.i("progress_移动了"+(mOldY-y));
//                onVolumeSlide((mOldY - y) / windowHeight);
//            }
//
//            else if (mOldX < windowWidth * 2 / 5.0)// 左边滑动
//                onBrightnessSlide((mOldY - y) / windowHeight);
//
//            return true;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent motionEvent) {
//            Log.i("MyGesture","onLongPress");
////            Toast.makeText(NewMainActivity.this,"onLongPress",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//            Log.i("MyGesture","onFling");
//            showTopView(true);
//            if(!isDelayingInVisibleTopView)
//                delayInvisibleTopView(false);
////            Toast.makeText(NewMainActivity.this,"onFling",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//    }
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        /** 双击 */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        /** 滑动 */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float y = e2.getY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            L.i("progress_windowHeight = " + windowHeight);

            if (mOldX > windowWidth * 3 / 5)// 右边滑动
            {
                L.i("progress_移动了"+(mOldY-y));
                onVolumeSlide((mOldY - y) / windowHeight);
                isTouchVoice = true;
                rl_voice.setVisibility(View.VISIBLE);
            }

            else if (mOldX < windowWidth * 2 / 5.0)// 左边滑动
            {
                onBrightnessSlide((mOldY - y) / windowHeight);
                isTouchVoice = false;
                rl_light.setVisibility(View.VISIBLE);
            }


            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        // 显示
        int nowProgress = (int) ((mVolume/mMaxVolume+percent)*100);
        if (nowProgress>=100)
        {
            nowProgress = 100;
        }else if (nowProgress<=0)
        {
            nowProgress = 0;
        }
        pr_voice.setProgress(nowProgress);

        int index = (int) (mMaxVolume*nowProgress / 100);

        L.d("设置progress_ = "+nowProgress+",当前音量index = "+index);

        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, index, 0);
        AgoraManager.mSetSpeakerphoneVolume(rtcEngine,nowProgress*255/100);
    }
    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        pr_light.setProgress((int) (lpa.screenBrightness*100));

    }
    /** 手势结束 */
    private void endGesture() {
        Log.i("MyGesture","手势结束 mVolume = "+mVolume);
        mVolume = pr_voice.getProgress()*mMaxVolume/100;
        mBrightness = (float)(pr_light.getProgress())/100.0f;
        if(isTouchVoice)
        {
            rl_voice.setVisibility(View.INVISIBLE);
        }else
        {
            rl_light.setVisibility(View.INVISIBLE);
        }
    }
    //===========响铃============

    private void ring(){//这里要打开的
        L.d("CallingActivity-ring");
        try{
            if (mediaPlayer!=null)
                mediaPlayer.reset();//从新设置要播放的音乐
            mediaPlayer=MediaPlayer.create(this,R.raw.ring_01);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();//播放音乐
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }

    private void stopRing()
    {
        L.d("CallingActivity-stopRing");
        if(mediaPlayer!=null&&mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    //===========震动============
    private Vibrator vibrator;

    public void vibratorWork(Bundle savedInstanceState) {

    /*
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
     * */
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400,100,400}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern,2); //重复两次上面的pattern 如果只想震动一次，index设为-1
    }
    public void vibratorStop(){
        vibrator.cancel();
    }

    /**
     * dip工具
     * @param dpNum
     * @return
     */
    private int getDipWidth(int dpNum)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpNum, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        L.d("CallingActivity-onDestroy");

        if(timer!=null)
            timer.cancel();
        if(task!=null)
            task.cancel();

        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if(vibrator!=null)
        {
            vibrator.cancel();
            vibrator = null;
        }

        BusProvider.getInstance().unregister(this);
        isFinished = true;
        BaseApplication.getInstance().setEngineEventHandlerActivity(null);
        super.onDestroy();
    }
    //=========通话结束后3秒退出界面
//    private void lfetFinishActivity()
//    {
//
//        L.d("CallingActivity-lfetFinishActivity");
//        TipsUtils.showShort(BaseApplication.getInstance().getBaseContext(),"通话结束,3秒后自动返回");
////
//        timer = new Timer();
//
//        task = new TimerTask() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {      // UI thread
//                    @Override
//                    public void run() {
//                        L.d("timer-run");
//                        CallingActivity.this.finish();
//                    }
//                });
//            }
//        };
//        timer.schedule(task, 3000, 1000);
//
//
////        if(isCall&&hasOrder)
////        {
////            //设置微支付状态:
////            mSettings.APP_IS_UN_PAY.setValue(true);
////            getOrderDetails();
////        }
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        L.d("CallingActivity-onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            L.d("点击了CallingActivityActvity页面的返回键");
            if(!canNotGoBack)CallingActivity.this.finish();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private boolean isViewChanged;
    private boolean isSelfShowInBigSurfaceView;//自己显示在大view中(切换显示镜头用的)
    private boolean isLocalVideoStreamEnable;//当前false 代表发送
    private boolean isRemoteVideoSteamEnable;
    private boolean isSpeakerphoneEnable = true;//当前false代表听筒
    private boolean isMuteLocalAudioStream;//当前false代表没有静音
    @Override
    public void onClick(View view) {
//        SoundPoulManager.btnClickSound();
        clickAnyWiget = true;
        switch (view.getId())
        {
            case R.id.user_local_view:
                L.i("user_local_view_click");
                isViewChanged = AgoraManager.changeViewPosition(this,user_local_view,user_remote_view,localView,remoteView,isViewChanged);
                break;
            //选择是否接听界面
            case R.id.iv_hangup_becall:
                L.d("CallingActivity-click-iv_hangup_becall");
                canNotGoBack = false;
                stopRing();
                refuseNet(true);//true 关闭页面
                break;
            //视频界面
            case R.id.iv_hang_up:
                L.d("CallingActivity-click-iv_hang_up");
                L.d("exit CallingActivity-挂断 time = "+System.currentTimeMillis());
                if(isJoined)
                {
                    left(true);
                } else
                    refuseNet(true);
                break;
            case R.id.iv_connect:
                L.d("CallingActivity-click-iv_connect");
                iv_connect.setVisibility(View.GONE);
                iv_hanup.setVisibility(View.GONE);
                iv_hang_up.setVisibility(View.VISIBLE);
                mDuration.setVisibility(View.VISIBLE);
                stopRing();
                addChannle();

                break;
            case R.id.user_remote_view:
                L.i("user_remote_view_click");
                menuGone();
                if(menuIsShow)return;
                endGesture();
                showTopView(true);
                if(!isDelayingInVisibleTopView)
                    delayInvisibleTopView(false);
                break;
//            case R.id.tv_jubao:
//                TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"举报成功");
//                break;
//            case R.id.tv_guanzhu:
//                TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"关注成功");
//                break;
//            case R.id.tv_zhineng:
////                TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"已设置成智能模式");
//                int[] zhiNengIds = {R.id.tv_normal_mode,R.id.tv_high_mode,R.id.tv_smart_mode};
//                popupWindow = showPopUp(view,R.layout.pop_video_start_mode,50,84,zhiNengIds);
//                break;
            case R.id.iv_draw:
                //原来用pupwindow
//                int[] drawIds = {R.id.ic_room_button_change,R.id.iv_stop_local_video,R.id.iv_stop_remote_stream,R.id.ic_microphone,R.id.ic_no_voice};
//                pwForSettingDraw = showPopUp(view,R.layout.pop_video_draw,35,200,drawIds);
                //现在九宫格
                menuIn();
                menuIsShow = true;
                break;
            case R.id.iv_qiehuan:
                if (rtcEngine==null)return;
                rtcEngine.switchCamera();
                view.setBackgroundResource(isSelfShowInBigSurfaceView?R.drawable.btn_qiehuan:R.drawable.btn_qhiehuan);
                isSelfShowInBigSurfaceView = !isSelfShowInBigSurfaceView;
                break;
            //关闭本地视频
            case R.id.iv_wode_shipin:
                if (rtcEngine==null)return;
                String localVideoResult = AgoraManager.mMuteLocalVideoStream(rtcEngine,isLocalVideoStreamEnable);
                view.setBackgroundResource(isLocalVideoStreamEnable?R.drawable.btn_bwodeshipin:R.drawable.btn_wodeshipin);
                isLocalVideoStreamEnable = !isLocalVideoStreamEnable;
                break;
            //关闭远程视频
            case R.id.iv_duifang_shipin:
                if (rtcEngine==null)return;
                String remoteStreamResult = AgoraManager.mMuteRemoteVideoStream(rtcEngine,remoteUid,isRemoteVideoSteamEnable);
//                TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),remoteStreamResult);
                view.setBackgroundResource(isRemoteVideoSteamEnable?R.drawable.btn_gbiduifangshipin:R.drawable.btn_iduifangshipin);
                isRemoteVideoSteamEnable = !isRemoteVideoSteamEnable;
//                popDismiss(pwForSettingDraw);pwForSettingDraw = null;
                break;
            //麦克风
            case R.id.iv_maike:
                if (rtcEngine==null)return;
                String ic_microphoneResult = AgoraManager.mSetEnableSpeakerphone(rtcEngine,isSpeakerphoneEnable);
                view.setBackgroundResource(isSpeakerphoneEnable?R.drawable.btn_guanbimaike:R.drawable.btn_maike);
                isSpeakerphoneEnable = !isSpeakerphoneEnable;
                break;
            //静音
            case R.id.iv_jingyin:
                if (rtcEngine==null)return;
                String result = AgoraManager.mMuteLocalAudioStream(rtcEngine,isMuteLocalAudioStream);
                view.setBackgroundResource(isMuteLocalAudioStream?R.drawable.btn_shenyin:R.drawable.btn_gbshenyin);
                isMuteLocalAudioStream = !isMuteLocalAudioStream;
                break;
            case R.id.iv_fanhui:
                setMenuOutAnimationSet(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {fl_menu.setVisibility(View.GONE);}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                iv_qiehuan.setVisibility(View.GONE);
                iv_duifang_shipin.setVisibility(View.GONE);
                iv_wode_shipin.setVisibility(View.GONE);
                iv_maike.setVisibility(View.GONE);
                iv_jingyin.setVisibility(View.GONE);
                iv_fanhui.setVisibility(View.GONE);
                menuIsShow = false;
                break;
        }

    }
    //==============显示隐藏topView=============
    private void showTopView(final boolean isVisible)
    {
        if(menuIsShow)
        {
            menuGone();
            return;
        }
        if (!isJoined)return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int visibleOrInVisible = isVisible?View.VISIBLE:View.GONE;
                mDuration.setVisibility(visibleOrInVisible);
                iv_hang_up.setVisibility(visibleOrInVisible);
                rl_video_title.setVisibility(visibleOrInVisible);
                iv_draw.setVisibility(visibleOrInVisible);
                if (!isVisible)
                {
                    popDismiss(popupWindow);popupWindow = null;
                    popDismiss(pwForSettingDraw);pwForSettingDraw = null;
                }
            }
        });
    }
    //========6秒后topView消失==========
    private void delayInvisibleTopView(final boolean showTopView)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                L.i("倒计时 设置 countDownTime = 3000");
                int countDownTime = 3000;
                while(countDownTime>0)
                {
                    isDelayingInVisibleTopView = true;
//                    L.i("倒计时 当前 countDownTime = "+countDownTime);
                    try {
                        Thread.sleep(100);
                        countDownTime-=100;
                        //点击了任何控件,计数重置为初始值;
                        if(clickAnyWiget)
                        {
                            clickAnyWiget = false;
                            countDownTime = 3000;
                        }
                        if(countDownTime == 0)
                        {
                            L.i("倒计时 收掉topViews");
                            isDelayingInVisibleTopView = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showTopView(showTopView);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
    //=======pop在view上方弹出=====
    private PopupWindow showPopUp(View v,int res,int width,int height,int[] ids) {
        View popupWindow_view = getLayoutInflater().inflate(res, null,false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        final PopupWindow popupWindow = new PopupWindow(popupWindow_view, getDipWidth(width), getDipWidth(height), true);
//        // 设置动画效果
//        popupWindow.setAnimationStyle(R.style.AnimationFade);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-popupWindow.getHeight()-10);
        popupWindow_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popDismiss(popupWindow);
                return false;
            }
        });

        for (int i = 0;i<ids.length;i++)
        {
            popupWindow_view.findViewById(ids[i]).setOnClickListener(this);
        }
        return popupWindow;
    }
    //=====pop消失====
    private void popDismiss(PopupWindow pop)
    {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
        }
    }
    private int time;
    //显示时间
    void setupTime() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        time++;

                        if (time >= 3600) {
                            mDuration.setText(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
                        } else {
                            mDuration.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
                        }
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
    }
    private AnimationSet menuSetIn;
    private void setMenuInAnimationSet(Animation.AnimationListener listener)
    {
        //从0-1 变大
        ScaleAnimation sa = new ScaleAnimation(
                0.1f, 1.0f,
                0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        //旋转720度
        RotateAnimation ra = new RotateAnimation(
                0, -360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(500);
        sa.setDuration(500);
        //透明度
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        //停在最终位置
        sa.setFillAfter(true);
        ra.setFillAfter(true);
        aa.setFillAfter(true);
        //创建动画集对象
        AnimationSet menuSetIn = new AnimationSet(true);
        menuSetIn.addAnimation(sa);
        menuSetIn.addAnimation(ra);
        menuSetIn.addAnimation(aa);

        iv_qiehuan.setAnimation(menuSetIn);
        iv_duifang_shipin.setAnimation(menuSetIn);
        iv_wode_shipin.setAnimation(menuSetIn);
        iv_fanhui.setAnimation(menuSetIn);
        iv_maike.setAnimation(menuSetIn);
        iv_jingyin.setAnimation(menuSetIn);

        menuSetIn.setAnimationListener(listener);

        menuSetIn.startNow();

    }
    private void setMenuOutAnimationSet(Animation.AnimationListener listener)
    {
        //从0-1 变大
        ScaleAnimation sa = new ScaleAnimation(
                1.0f, 0.1f,
                1.0f, 0.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        //旋转720度
        RotateAnimation ra = new RotateAnimation(
                0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(500);
        sa.setDuration(500);
        //停在最终位置
        sa.setFillAfter(true);
        ra.setFillAfter(true);

        //创建动画集对象
        AnimationSet menuSetIn = new AnimationSet(true);
        menuSetIn.addAnimation(sa);
        menuSetIn.addAnimation(ra);

        iv_qiehuan.setAnimation(menuSetIn);
        iv_duifang_shipin.setAnimation(menuSetIn);
        iv_wode_shipin.setAnimation(menuSetIn);
        iv_fanhui.setAnimation(menuSetIn);
        iv_maike.setAnimation(menuSetIn);
        iv_jingyin.setAnimation(menuSetIn);

        menuSetIn.setAnimationListener(listener);

        menuSetIn.startNow();
    }
    private void menuGone()
    {
        setMenuOutAnimationSet(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {fl_menu.setVisibility(View.GONE);}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        iv_qiehuan.setVisibility(View.GONE);
        iv_duifang_shipin.setVisibility(View.GONE);
        iv_wode_shipin.setVisibility(View.GONE);
        iv_maike.setVisibility(View.GONE);
        iv_jingyin.setVisibility(View.GONE);
        iv_fanhui.setVisibility(View.GONE);
        menuIsShow = false;
    }
    private void menuIn()
    {

        fl_menu.setVisibility(View.VISIBLE);
        iv_qiehuan.setVisibility(View.VISIBLE);
        iv_duifang_shipin.setVisibility(View.VISIBLE);
        iv_wode_shipin.setVisibility(View.VISIBLE);
        iv_maike.setVisibility(View.VISIBLE);
        iv_jingyin.setVisibility(View.VISIBLE);
        iv_fanhui.setVisibility(View.VISIBLE);
        setMenuInAnimationSet(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                iv_qiehuan.clearAnimation();
                iv_duifang_shipin.clearAnimation();
                iv_wode_shipin.clearAnimation();
                iv_maike.clearAnimation();
                iv_jingyin.clearAnimation();
                iv_fanhui.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    public void onNetworkQuality(int quality) {
        super.onNetworkQuality(quality);
        L.i("声网 网络状态码 = "+quality);
    }
}
