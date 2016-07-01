package com.videorecorder.handler;

import com.bdj.video.build.NativeSprFilter;
import com.videorecorder.bean.MaterialParam;
import com.videorecorder.listener.OnEditVideoCompleteListener;
import com.videorecorder.util.MakeVideoUtils;
import com.videorecorder.widget.VideoRenderer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.SurfaceView;

public class VideoBuilders implements VBuilders {
    private VideoRenderer mRenderer;
    private SurfaceView mSurfaceView;
    private Context mContext;
    private ProgressDialog progressDialog;
    private OnEditVideoCompleteListener completeListener;
    private VideoPlayBtnStateListener videoPlayBtnStateListener;
    private int mHandlerId = 0;
    
    public VideoBuilders(Context context, SurfaceView surfaceView) {
        this.mContext = context;
        this.mSurfaceView = surfaceView;
    }
    
    Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                int progress = NativeSprFilter.SPRFILTER_GetPercent();
                if (progress < 255) {
                    if (progressDialog != null) {
                        progressDialog.setProgress(progress);//.update();
                    }
                    handler.sendEmptyMessage(1);
                } else {
                    if (videoPlayBtnStateListener != null) {
                        videoPlayBtnStateListener.onViewVisible();
                    }
                    if (mRenderer != null) {
                        mRenderer.stop();
                    }
                    NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (completeListener != null) {
                        completeListener.onComplete();
                    }
                }
            }
        }
    };
    
    private void start() {
        if (videoPlayBtnStateListener != null) {
            videoPlayBtnStateListener.onViewGone();
        }
        mRenderer = new VideoRenderer(mSurfaceView);
        mRenderer.start();
        handler.sendEmptyMessage(1);
    }
    
    @Override
    public void stop() {
        if (videoPlayBtnStateListener != null) {
            videoPlayBtnStateListener.onViewVisible();
        }
        if (mRenderer != null) {
            mRenderer.stop();
            if (mHandlerId != 0) {
                NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void buildWatermark(MaterialParam param, int handlerId) {
        // TODO Auto-generated method stub
        // 停止播放,合成
        stop();
        // 重新初始化，解决点击过快，水印重叠
        mHandlerId = NativeSprFilter.NativeMultiSprInit();
        
        MakeVideoUtils.getInstance(mContext).buildWatermark(param, mHandlerId);
        start();
    }
    
    @Override
    public void buildVoice(MaterialParam param, int handlerId) {
        // TODO Auto-generated method stub
        stop();
        mHandlerId = NativeSprFilter.NativeMultiSprInit();
        MakeVideoUtils.getInstance(mContext).buildVoice(param, mHandlerId);
        start();
    }
    
    @Override
    public void isOpen_OriSound(int orisound, int handlerId) {
        // TODO Auto-generated method stub
        stop();
        mHandlerId = NativeSprFilter.NativeMultiSprInit();
        MakeVideoUtils.getInstance(mContext).isOpen_OriSound(orisound, mHandlerId);
        start();
    }
    
    @Override
    public void publishVideo(OnEditVideoCompleteListener completeListener, int handlerId) {
        // TODO Auto-generated method stub
        this.completeListener = completeListener;
        progressDialog = new ProgressDialog(mContext);
        stop();
        mHandlerId = NativeSprFilter.NativeMultiSprInit();
        MakeVideoUtils.getInstance(mContext).publishVideo(completeListener, mHandlerId);
        // start();
        progressDialog.show();
        progressDialog.setCancelable(false);
        handler.sendEmptyMessage(1);
    }
    
    @Override
    public void interrupt() {
        // TODO Auto-generated method stub
        if (mRenderer != null)
            mRenderer.stop();
        if (mHandlerId != 0) {
            NativeSprFilter.SPRFILTER_SetNStop(mHandlerId);
        }
    }
    
    
    @Override
    public void setVideoPlayBtnStateListener(VideoPlayBtnStateListener listener) {
        // TODO Auto-generated method stub
        videoPlayBtnStateListener = listener;
    }
    
    @Override
    public void play(int handlerId) {
        stop();
        mHandlerId = NativeSprFilter.NativeMultiSprInit();
        MakeVideoUtils.getInstance(mContext).play(mHandlerId);
        start();
    }
    
    public interface VideoPlayBtnStateListener {
        void onViewVisible();
        
        void onViewGone();
    }
}
