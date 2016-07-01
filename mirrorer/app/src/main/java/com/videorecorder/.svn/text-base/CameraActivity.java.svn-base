package com.videorecorder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.bdj.video.build.NativeSprFilter;
import com.videorecorder.util.MakeVideoUtils;
import com.videorecorder.widget.ProgressDialog;

public abstract class CameraActivity extends Activity {
    
    public static final String EXTRAS_VIDEO_PATH = "video_path";//视频地址
    public static final String EXTRAS_FIRST_FRAME_PATH = "frame_path";//首帧图片地址
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public abstract void initExternalParame();
    public abstract void setRecordActionLayout();


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        clear();
        destroyCamera();
        
    }

    public abstract void destroyCamera();
    

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
                            CameraActivity.this).getFinalFile();
                    startNextStep(file);
                }
            }
        }

    };

    public abstract void startNextStep(String file) ;

    public abstract void clear();
    
    //pcl 2015-04-22 start
    public abstract void enquireAudio();
    
    
}
