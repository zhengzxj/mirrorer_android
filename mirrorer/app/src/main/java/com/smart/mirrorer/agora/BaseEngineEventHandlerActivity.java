package com.smart.mirrorer.agora;

import com.smart.mirrorer.base.BaseActivity;

import io.agora.rtc.IRtcEngineEventHandler;

/**
 *
 * A handler activity act as a bridge to take callbacks from @MessageHandler.
 * Subclasses should override these key methods.
 *
 * Created by on 9/13/15.
 */
public class BaseEngineEventHandlerActivity extends BaseActivity {


    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        System.out.println("hehe-onJoinChannelSuccess");
    }

    public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
        System.out.println("hehe-onRejoinChannelSuccess");
    }

    public void onError(int err) {
        System.out.println("hehe-onError = "+err);
    }


    public void onCameraReady() {
        System.out.println("hehe-onCameraReady");
    }

    public void onAudioQuality(int uid, int quality, short delay, short lost) {
        System.out.println("hehe-onAudioQuality");
    }

    public void onAudioTransportQuality(int uid, short delay, short lost) {
        System.out.println("hehe-onAudioTransportQuality");
    }

    public void onVideoTransportQuality(int uid, short delay, short lost) {
        System.out.println("hehe-onVideoTransportQuality");
    }

    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {
        System.out.println("hehe-onLeaveChannel");
    }

    public void onUpdateSessionStats(IRtcEngineEventHandler.RtcStats stats) {
    }

    public void onRecap(byte[] recap) {System.out.println("hehe-onRecap");
    }

    public void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {System.out.println("hehe-onAudioVolumeIndication");
    }

    public void onNetworkQuality(int quality) {System.out.println("hehe-onNetworkQuality");
    }

    public void onUserJoined(int uid, int elapsed) {System.out.println("hehe-onUserJoined");
    }

    public void onUserOffline(int uid) {System.out.println("hehe-onUserOffline");
    }

    public void onUserMuteAudio(int uid, boolean muted) {System.out.println("hehe-onUserMuteAudio");
    }

    public void onUserMuteVideo(int uid, boolean muted) {System.out.println("hehe-onUserMuteVideo");
    }

    public void onAudioRecorderException(int nLastTimeStamp) {System.out.println("hehe-onAudioRecorderException");
    }

    public void onRemoteVideoStat(int uid, int frameCount, int delay, int receivedBytes) {System.out.println("hehe-onRemoteVideoStat");
    }

    public void onLocalVideoStat(int sentBytes, int sentFrames) {System.out.println("hehe-onLocalVideoStat");
    }

    public void onFirstRemoteVideoFrame(int uid, int width, int height, int elapsed) {System.out.println("hehe-onFirstRemoteVideoFrame");
    }

    public void onFirstLocalVideoFrame(int width, int height, int elapsed) {System.out.println("hehe-onFirstLocalVideoFrame");
    }

    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {System.out.println("hehe-onFirstRemoteVideoDecoded");
    }

    public void onConnectionLost() {System.out.println("hehe-onConnectionLost");
    }

    public void onMediaEngineEvent(int code) {System.out.println("hehe-onMediaEngineEvent");
    }

}
