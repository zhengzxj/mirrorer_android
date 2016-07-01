package com.smart.mirrorer.util.agroa;

import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.smart.mirrorer.agora.BaseEngineEventHandlerActivity;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.agora.MessageHandler;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.util.mUtil.L;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by zhengfei on 16/6/3.
 */

/**
 *  public class Singleton {
 2     private static Singleton instance;
 3     private Singleton (){}
 4     public static synchronized Singleton getInstance() {
 5     if (instance == null) {
 6         instance = new Singleton();
 7     }
 8     return instance;
 9     }
 10 }
 */
public class AgoraManager {


    private int remoteId;

    private int localId;

    public static AgoraManager mAgoraManager;



    private AgoraManager(String signKey,SurfaceView localView,BaseEngineEventHandlerActivity vActivity){
        this.signKey = signKey;
        this.localView = localView;
//        this.vActivity = vActivity;
    }

    public static synchronized AgoraManager initAgora(String signKey,SurfaceView localView,BaseEngineEventHandlerActivity vActivity)
    {
        L.d("声网初始化Agora key = "+signKey);
        if (mAgoraManager == null) {
            mAgoraManager = new AgoraManager(signKey,localView,vActivity);
        }else
        {
            mAgoraManager.signKey = signKey;
            mAgoraManager.localView = localView;
//            mAgoraManager.vActivity = vActivity;
        }
//        mAgoraManager.initAgora();
        return mAgoraManager;
//        mAgoraManager = new AgoraManager(signKey,localView,vActivity);
//        return mAgoraManager;
    }

    //MessageHandler
    private MessageHandler messageHandler;

    //RtcEngine
    private RtcEngine rtcEngine;

    //sing key
    private String signKey;

    //localview
    private SurfaceView localView;

    /**
     * 初始化agora
     */
    public static RtcEngine initAgora(CallingActivity vActivity,String signKey,SurfaceView localView,String channelName)
    {
        //屏幕常亮
        vActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置回调关联
        MessageHandler messageHandler = new MessageHandler();
        messageHandler.setActivity(vActivity);

        L.d("XXXYYY initAgora activity 里面的 channelName = "+((CallingActivity)vActivity).channelName);
        //初始化引擎
        L.d("声网create 初始化agora key = "+signKey);
        RtcEngine rtcEngine = RtcEngine.create(BaseApplication.getInstance().getApplicationContext(), signKey, messageHandler);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView));//, VideoCanvas.RENDER_MODE_HIDDEN

        //设置需要的音视频流
        int muteLocalVideoStreamResult = rtcEngine.muteLocalVideoStream(false);
        int muteLocalAudioStreamResult = rtcEngine.muteLocalAudioStream(false);
        int muteAllRemoteVideoStreamsResult = rtcEngine.muteAllRemoteVideoStreams(false);
        L.d("声网muteLocalVideoStream返回值 "+muteLocalVideoStreamResult);
        L.d("声网muteLocalAudioStream返回值 "+muteLocalAudioStreamResult);
        L.d("声网muteAllRemoteVideoStreams返回值 "+muteAllRemoteVideoStreamsResult);
        return rtcEngine;
    }

    /**
     * 加入频道
     * @param channelName
     * @return
     */
    public int mJoinChannle(String channelName)
    {
        L.d("加入agora-channelName = "+channelName);
        return rtcEngine.joinChannel(signKey,channelName,"0", 0);
    }


    public static void setRemoteView(final RtcEngine rtcEngine,final CallingActivity vActivity,int uid,final int remoteLayoutId,final SurfaceView remoteView) {

        L.d("声网setRemoteView");
        final int tempUid = uid;

        vActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                FrameLayout remoteVideoUser = (FrameLayout) vActivity.findViewById(remoteLayoutId);
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

    //======设置localView的resId;
    public void  setLocalId(int id)
    {
        localId = id;
    }


    //======静音=======
    public static String mMuteLocalAudioStream(RtcEngine rtcEngine,boolean isMuteLocalAudioStream)
    {
        rtcEngine.muteLocalAudioStream(!isMuteLocalAudioStream);//调用相反属性=静音
        isMuteLocalAudioStream = !isMuteLocalAudioStream;
        return isMuteLocalAudioStream?"静音":"取消静音";//提示静音
    }

    //======麦克风开关=======
    public static String mSetEnableSpeakerphone(RtcEngine rtcEngine,boolean isSpeakerphoneEnable)
    {
        isSpeakerphoneEnable = rtcEngine.isSpeakerphoneEnabled();
        rtcEngine.setEnableSpeakerphone(!isSpeakerphoneEnable);
        rtcEngine.setSpeakerphoneVolume(200);
//        rtcEngine.muteLocalAudioStream(!isSpeakerphoneEnable);//调用相反属性=静音
        isSpeakerphoneEnable = !isSpeakerphoneEnable;
        return isSpeakerphoneEnable?"切换到扬声器模式":"切换到听筒模式";
    }


    //=======关闭本地视频流,不影响自己看======
    public static String mMuteLocalVideoStream(RtcEngine rtcEngine,boolean isLocalVideoStreamEnable)
    {
        rtcEngine.muteLocalVideoStream(!isLocalVideoStreamEnable);
        isLocalVideoStreamEnable = !isLocalVideoStreamEnable;
        return isLocalVideoStreamEnable?"已关闭本地视频流(对方看不到我)":"恢复本地视频流";
    }
    private int remoteUid;
    //======获取对方uid=================
    public void setRemoteUid(int uid)
    {
        remoteUid = uid;
    }

    //=======关闭某uid的远程视频流接收=====
    public static String mMuteRemoteVideoStream(RtcEngine rtcEngine,int remoteUid,boolean isRemoteVideoSteamEnable)
    {
        rtcEngine.muteRemoteVideoStream(remoteUid,!isRemoteVideoSteamEnable);
        isRemoteVideoSteamEnable = !isRemoteVideoSteamEnable;
        return isRemoteVideoSteamEnable?"关闭远程视频":"恢复远程视频";
    }
    private int selfUid;
    public void setSelfUid(int uid)
    {
        selfUid = uid;
    }
    //设置本地视频清晰度
    public void mSetVideoProfile(int profile)
    {
        rtcEngine.setVideoProfile(profile);
    }
    //=======对换本地和远程view的位置======
    public static boolean changeViewPosition(CallingActivity vActivity,FrameLayout localFrameLayout,FrameLayout remoteFrameLayout,SurfaceView localView,SurfaceView remoteView,boolean isViewChanged)
    {

        if(remoteFrameLayout==null||localFrameLayout==null||remoteView==null||localView==null)return isViewChanged;

        localFrameLayout.removeAllViews();
        remoteFrameLayout.removeAllViews();

        if(!isViewChanged)//正常显示:远程在remoteView 本地在localView
        {
            localFrameLayout.addView(remoteView);
            remoteView.setZOrderOnTop(true);
            remoteView.setZOrderMediaOverlay(true);
            remoteFrameLayout.addView(localView);
        }else
        {
            localFrameLayout.addView(localView);
            localView.setZOrderOnTop(true);
            localView.setZOrderMediaOverlay(true);
            remoteFrameLayout.addView(remoteView);
        }
        return !isViewChanged;
    }
    public static void mSetSpeakerphoneVolume(RtcEngine rtcEngine,int voice)
    {
        L.i("设置扬声器音量为 "+voice);
        rtcEngine.setSpeakerphoneVolume(voice);
    }
}
