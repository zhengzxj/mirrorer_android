package com.videorecorder;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseTitleActivity;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerActivity extends BaseTitleActivity implements SurfaceHolder.Callback, OnCompletionListener,
        OnErrorListener, OnInfoListener, OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener,
        OnBufferingUpdateListener {

    public static final String VIDEO_URI = "video_uri";

    private MediaPlayer mediaPlayer;

    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private ImageView ivPlay, ivDefault;// 播放按钮，默认图片
    private TimeProgressBar seekBar;// 进度条
    // private TextView tvCurrentTime, tvEndTime;// 当前播放时间，总时间

    private String url;

    private int screenWidth, screenHeight;

    private int currentPosition;

    Timer timer;
    TimerTask task;
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                case 1:
                    seekBar.setProgress(currentPosition);
                    // tvCurrentTime.setText(toTime(currentPosition));
                    break;
                default:
                    break;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };// 用于进度条

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 设置屏幕常亮
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置满屏

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        setContentView(R.layout.activity_video_player);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("视频预览");
        initView();
    }

    private void initView() {
        // TODO Auto-generated method stub
        surfaceView = (SurfaceView) findViewById(R.id.sv_video_player);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ivPlay.setImageResource(R.drawable.ic_play);
                } else {
                    ivPlay.setImageResource(R.drawable.ic_pause);
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    } else {
                        load();
                    }
                }
            }
        });
        ivPlay.setImageResource(R.drawable.ic_pause);
        ivDefault = (ImageView) findViewById(R.id.iv_video);
        // tvCurrentTime = (TextView) findViewById(R.id.player_overlay_time);
        // tvEndTime = (TextView) findViewById(R.id.player_overlay_length);
        seekBar = (TimeProgressBar) findViewById(R.id.player_overlay_seekbar);
        seekBar.setDrawMinLen(false);
        seekBar.setDrawMaxText(false);
    }

    private void initData() {
        url = getIntent().getExtras().getString(VIDEO_URI);
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "播放地址为空", Toast.LENGTH_SHORT).show();
            exit();
            return;
        }
        load();
    }

    public void load() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            // mediaPlayer.release();
        } else {
            mediaPlayer = new MediaPlayer();// 创建多媒体对象
        }
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        start();
    }

    public void start() {
        showLoadDialog();
        try {
            // mediaPlayer.reset();
            // url = "http://192.168.1.146/test/gameofthrones.mp4";
            Uri uri = Uri.parse(url);
            mediaPlayer.setDataSource(VideoPlayerActivity.this, uri);
            mediaPlayer.setDisplay(surfaceHolder);// 媒体图像

            int mediaWidth = mediaPlayer.getVideoWidth();
            int mediaHeight = mediaPlayer.getVideoHeight();
            if (mediaWidth != 0 && mediaHeight != 0) {
                surfaceView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth,
                        (screenWidth * mediaHeight / mediaWidth)));
            } else {
                surfaceView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenWidth));
            }
            mediaPlayer.prepareAsync();// 异步加载，以免造成UI卡死

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Toast.makeText(this, "不支持的视频格式", Toast.LENGTH_SHORT).show();
            // activity.finish();
            exit();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    public String toTime(int time) {

        time /= 1000;
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        dismissLoadDialog();
        ivDefault.setVisibility(View.GONE);
        if (mp != null) {
            mp.start();// 开始播放
            seekBar.setMax(mp.getDuration());// 设置播放进度条最大值
            startTimer();
        } else {
            Toast.makeText(this, "播放出错", Toast.LENGTH_SHORT).show();
            exit();
        }
    }

    private void startTimer() {

        timer = new Timer();
        task = new TimerTask()
        {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if (mediaPlayer != null)
                        currentPosition = mediaPlayer.getCurrentPosition();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        };
        timer.schedule(task, 50, 50);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
        handler.removeMessages(1);
        ivDefault.setVisibility(View.GONE);
        ivPlay.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {// surface创建后才能初始化数据
        // TODO Auto-generated method stub
        initData();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    private void exit() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
