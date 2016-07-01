package com.smart.mirrorer.agora;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.home.VoiceVideoActivity;

import io.agora.rtc.RtcEngine;

public class ChannelActivity extends BaseEngineEventHandlerActivity {

    private String mQid;
    private String mQDesc;
    private String channelName;
    private String dynamicKey;
    private Intent intent;

    public final static int CALLING_TYPE_VIDEO = 0x100;
    public final static int CALLING_TYPE_VOICE = 0x101;

    public final static String EXTRA_CALLING_TYPE = "EXTRA_CALLING_TYPE";
    public final static String EXTRA_VENDOR_KEY = "EXTRA_VENDOR_KEY";
    public final static String EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID";


    private int mCallingType;
    private SurfaceView mLocalView;
    private String vendorKey = "";
    private String channelId = "";
    private TextView mDuration;
    private TextView mByteCounts;
    private View mCameraEnabler;
    private View mCameraSwitcher;
    private LinearLayout mRemoteUserContainer;
    private AlertDialog alertDialog;
    private int time = 0;

    private int mLastRxBytes = 0;
    private int mLastTxBytes = 0;
    private int mLastDuration = 0;

    private int mRemoteUserViewWidth = 0;

    RtcEngine rtcEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        BusProvider.getInstance().register(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRemoteUserViewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

//        mCallingType = getIntent().getIntExtra(EXTRA_CALLING_TYPE, CALLING_TYPE_VOICE /*default is voice call*/);
        mCallingType = CALLING_TYPE_VOICE;

        initData();

        initViews();

//        if (CALLING_TYPE_VIDEO == mCallingType) {
//            // video call
//
//            View simulateClick = new View(getApplicationContext());
//            simulateClick.setId(R.id.wrapper_action_video_calling);
//            this.onUserInteraction(simulateClick);
//
//
//        } else if (CALLING_TYPE_VOICE == mCallingType) {
//            // voice call
//            View simulateClick = new View(getApplicationContext());
//            simulateClick.setId(R.id.wrapper_action_voice_calling);
//            this.onUserInteraction(simulateClick);
//        }
//
//
//        // check network
//        if (!NetworkConnectivityUtils.isConnectedToNetwork(getApplicationContext())) {
//            onError(104);
//        }

    }
    private void initViews()
    {

    }
    private void initData()
    {
        intent = getIntent();
        mQid = intent.getStringExtra(VoiceVideoActivity.KEY_Q_ID);
        mQDesc = intent.getStringExtra(VoiceVideoActivity.KEY_Q_DESC);
        channelName = intent.getStringExtra(VoiceVideoActivity.KEY_CHANNEL_NAME);
        dynamicKey = intent.getStringExtra(VoiceVideoActivity.KEY_DYNAMIC_KEY);
    }

    void setupRtcEngine() {

        String vendorKey = getIntent().getStringExtra(EXTRA_VENDOR_KEY);
        this.vendorKey = vendorKey;

        // setup engine
//        BaseApplication.getInstance().setRtcEngine(vendorKey);
//        rtcEngine = BaseApplication.getInstance().getRtcEngine();
//        LogUtil.log.d(getApplicationContext().getExternalFilesDir(null).toString() + "/agorasdk.log");
        rtcEngine.setLogFile(getApplicationContext().getExternalFilesDir(null).toString() + "/agorasdk.log");


        // setup engine event activity
        BaseApplication.getInstance().setEngineEventHandlerActivity(this);

        rtcEngine.enableVideo();

    }
}
