package com.videorecorder.handler;

import com.bdj.video.build.NativeSprEditConnect;
import com.bdj.video.build.NativeVideoStream;
import com.bdj.video.build.VideoStreamObs;
import com.videorecorder.bean.Frame;
import com.videorecorder.util.FileUtils;
import com.videorecorder.util.MakeVideoUtils;
import com.videorecorder.widget.LargeProgressDialog;

import android.content.Context;
import android.os.Handler;

public class EveryframeHandler2 {
    private Context mContext;
    private boolean isStarted = false;
    private LargeProgressDialog progressDialog;
    private DecodeTaskListener decodeTaskListener;
    
    public EveryframeHandler2(Context context) {
        this.mContext = context;
        progressDialog = new LargeProgressDialog(context);
    }
    
    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                if (isStarted) {
                    handler.sendEmptyMessage(1);
                } else {
                    stop();
                }
            }
        }
    };
    
    public void setStopFlag() {
        isStarted = false;
    }
    
    public void start(DecodeTaskListener listener) {
        createEveryFrame();
        decodeTaskListener = listener;
        isStarted = true;
        handler.removeMessages(1);
        // threadRender.start();
        multifilterTask();
        //
         progressDialog.show();
         progressDialog.setCancelable(false);
        handler.sendEmptyMessage(1);
    }
    
    public void stop() {
        handler.removeMessages(1);
        isStarted = false;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (decodeTaskListener != null) {
            decodeTaskListener.onComplete();
        }
    }
    
    private void createEveryFrame() {
        int frameNaum = NativeSprEditConnect.SPREDIT_GetTotalFrameNum();
        for (int i = 0; i < frameNaum; i++) {
            String path = FileUtils.createFile(mContext, FileUtils.FILE_IMG_CACHE_DIR, System.currentTimeMillis() + "_"
                    + i + ".jpg");
            Frame frame = new Frame();
            frame.setId(i);
            frame.setSource(path);
            FrameFileHandler.getInstance().putFrame(i, frame);
        }
    }
    
    private void multifilterTask() {
        VideoStreamObs obs = new VideoStreamObs();
        obs.SetContext(mContext);
        obs.SetEveryFragment(this);
        NativeVideoStream.NativeAddVideoStreamObs(obs);
        NativeVideoStream.NativeStartGetVideoFrame(MakeVideoUtils.getInstance(mContext).getSourceFile());
    }
    
    public interface DecodeTaskListener {
        void onComplete();
    }
}
