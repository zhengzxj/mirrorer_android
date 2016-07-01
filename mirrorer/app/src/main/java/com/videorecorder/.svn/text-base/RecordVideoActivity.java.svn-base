package com.videorecorder;

import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

import com.bdj.video.build.NativeSprFilter;
import com.smart.mirrorer.R;
import com.videorecorder.util.BdjUtils;
import com.videorecorder.util.CameraSetting;
import com.videorecorder.util.Constant;
import com.videorecorder.util.FileUtils;
import com.videorecorder.util.MakeVideoUtils;
import com.videorecorder.widget.CameraControlPanelLayout;
import com.videorecorder.widget.ControlPanelLayout;
import com.videorecorder.widget.ProgressDialog;
import com.videorecorder.widget.SurfaceViewLayout;
import com.videorecorder.widget.SurfaceViewLayout.Action;
import com.videorecorder.widget.SurfaceViewLayout.VideoAction;


public class RecordVideoActivity extends CameraActivity {
    public final static String EXTERNAL_TARGET_INTENT = "external_target_intent";
    // 大小
    public final static String EXTERNAL_FILE_SIZE = "external_file_size";
    // 最大时长
    public final static String EXTERNAL_MAX_DURATION = "external_max_duration";
    // 最小时长
    public final static String EXTERNAL_MIN_DURATION = "external_min_duration";
    // 支持格式
    public final static String EXTERNAL_MEDIA_TYPE = "external_media_type";
    
    private CameraControlPanelLayout camera_action;
    private SurfaceViewLayout camera_surfaceview;
    private ControlPanelLayout camera_record_action;
    private final static int DEF_TOP_MARGIN = 50;
    private RelativeLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_record_video);
        rootLayout = (RelativeLayout) findViewById(R.id.camera_root_layout);
        camera_action = (CameraControlPanelLayout) findViewById(R.id.camera_action);
        camera_action.init();
        camera_surfaceview = (SurfaceViewLayout) findViewById(R.id.camera_surfaceview);
        camera_record_action = (ControlPanelLayout) findViewById(R.id.camera_record_action);
        camera_record_action.init();
        camera_surfaceview.init(this,videoAction);
        camera_action.setUiCameraController(camera_surfaceview);
        camera_record_action.setUiRecordVideoController(camera_surfaceview);
        initExternalParame();
        Constant.mPendingIntent = getIntent().getParcelableExtra(
                EXTERNAL_TARGET_INTENT);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                setRecordActionLayout();

            }
        });
        enquireAudio();//pcl 2015-04-22
    }

    @Override
    public void initExternalParame() {
        CameraSetting.fileSize = getIntent().getStringExtra(EXTERNAL_FILE_SIZE);
        CameraSetting.maxDuration = getIntent().getStringExtra(EXTERNAL_MAX_DURATION);
        CameraSetting.minDuration = getIntent().getStringExtra(EXTERNAL_MIN_DURATION);
        CameraSetting.mediaType = getIntent().getStringExtra(EXTERNAL_MEDIA_TYPE);
    }

    @Override
    public void setRecordActionLayout() {
        int actionHeight = camera_action.getLayoutParams().height;
        // int screenHeight = BdjUtils.getScreenHeight(this);
        int screenHeight = rootLayout.getRootView().getHeight();
        int screenWidth = BdjUtils.getScreenWidth(this);
        int statusBarHeight = BdjUtils.getStatusBarHeight(this);
        int recordActionHeight = screenHeight - statusBarHeight - screenWidth
                - actionHeight;
        camera_record_action.getLayoutParams().height = recordActionHeight;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (camera_surfaceview
                .getDelayButton().getLayoutParams());
        final int topMarginValue = (int) (getResources().getDisplayMetrics().density
                * DEF_TOP_MARGIN + 0.5f);
        params.topMargin = BdjUtils.getScreenWidth(this) - topMarginValue;
    }
    
    private VideoAction videoAction = new VideoAction(){

        @Override
        public void videoAction(String videoPath, String framePath, Action action) {
            Intent intent = new Intent();
            if(action == Action.PREVIEW){//预览
                intent.setClass(RecordVideoActivity.this, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.VIDEO_URI, videoPath);
                camera_record_action.enableCommitView();
                startActivity(intent);
            }else if(action == Action.UPLOAD){//提交
                intent.putExtra(EXTRAS_VIDEO_PATH, videoPath);
                intent.putExtra(EXTRAS_FIRST_FRAME_PATH, framePath);
                Log.e("lzm", "video path :" + videoPath + ", frame path :" + framePath);
                setResult(RESULT_OK, intent);
                finish();
            }
            
        }
        
    };

    @Override
    protected void onResume() {
        super.onResume();
        camera_surfaceview.enableOrientationEvent();
        camera_surfaceview.resetPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        camera_surfaceview.closeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
        destroyCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            destroyCamera();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void destroyCamera() {
        camera_surfaceview.destroyCamera();
    }

    private String videoPath;
    private boolean isProgress = true;
    private ProgressDialog progressDialog;
    private int mHandlerId;
    boolean ispublish = false;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 2) {
                if (isProgress) {
                    handler.sendEmptyMessage(2);
                } else {
                    startNextStep(videoPath);
                }
            } else if (msg.what == 1) {
                int progress = NativeSprFilter.SPRFILTER_GetPercent();
                if (progress < 255) {
                    if (progressDialog != null) {
                        progressDialog.update(progress);
                    }
                    handler.sendEmptyMessage(1);
                } else {
                    ispublish = false;
                    NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    String file = MakeVideoUtils.getInstance(
                            RecordVideoActivity.this).getFinalFile();
                    startNextStep(file);
                }
            }
        }

    };

    @Override
    public void startNextStep(String file) {
        Intent intent = new Intent();
        intent.putExtra("inner_result", file);
        intent.putExtra("local_file", CameraSetting.LOCAL_FILE_NOT_EDIT);
        if (Constant.mPendingIntent != null) {
            try {
                Constant.mPendingIntent.send(RecordVideoActivity.this, 100, intent);
            } catch (CanceledException e) {
                e.printStackTrace();
            }
        }
        destroyCamera();
        clear();
        finish();
    };

    public void clear() {
        FileUtils.delDirectory(this, FileUtils.FILE_TEMP_DIR);
        FileUtils.delDirectory(this, FileUtils.FILE_IMG_CACHE_DIR);
        MakeVideoUtils.getInstance(this).destroyInstance();
    }
    
    @Override
    public void enquireAudio(){
           AudioRecord mRecorder;
           final int AUDIOFORMAT = AudioFormat.ENCODING_PCM_16BIT;
           final int CHANNEL_ID = AudioFormat.CHANNEL_CONFIGURATION_MONO;
           final int SAMPLERATE = 16000;
           int bufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, CHANNEL_ID, AUDIOFORMAT);
           mRecorder = new AudioRecord(AudioSource.MIC, SAMPLERATE, CHANNEL_ID, AUDIOFORMAT, bufferSize);
           if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED){
            mRecorder.startRecording();
           }
            {
               if (mRecorder != null) {
                   if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                       mRecorder.stop();
                       mRecorder.release();
                   }
                   mRecorder = null;
               }
           }
    }
}
