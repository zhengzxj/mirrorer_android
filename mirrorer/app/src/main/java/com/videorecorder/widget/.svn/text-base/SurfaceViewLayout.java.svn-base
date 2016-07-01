package com.videorecorder.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smart.mirrorer.R;
import com.smart.mirrorer.util.AppFilePath;
import com.videorecorder.RecordFileUtils;
import com.videorecorder.exception.CameraInitException;
import com.videorecorder.handler.PrepareTimeCountHandler;
import com.videorecorder.listener.ArHandler;
import com.videorecorder.listener.AudioRecordHandler;
import com.videorecorder.listener.CameraBridgeListener;
import com.videorecorder.listener.RecordControllerStateListener;
import com.videorecorder.listener.TimerUpdateListener;
import com.videorecorder.listener.UICameraController;
import com.videorecorder.listener.UIRecordVideoController;
import com.videorecorder.util.BdjUtils;
import com.videorecorder.util.CameraSetting;
import com.videorecorder.util.DialogTools;
import com.videorecorder.util.DialogTools.DialogClickEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SurfaceViewLayout extends RelativeLayout implements UICameraController, UIRecordVideoController,
        CameraBridgeListener {
    private SurfaceView surfaceView;
    private TextView prepareTimer;
    private TextView delayButton;
    private ImageView ivFrame;
    private View rlPause;
    private SurfaceHolder surfaceHolder;
    private Camera mCamera;
    private int mCameraId;
    private boolean mPreviewing = false;
    private Activity mActivity;
    private ArHandler mMrHandler;
    private PrepareTimeCountHandler mTimeCountHandler;
    private RecordControllerStateListener recordButtonControllerListener;
    private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
    private int mBackCameraId = -1, mFrontCameraId = -1;
    private MyOrientationEventListener orientationEventListener;
    private ProgressView mProgressView;
    private Bitmap bitmap = null, firstFrameBmp = null;

    public static enum Action {
        PREVIEW, UPLOAD;
    }

    public interface VideoAction {
        void videoAction(String videoPath, String framePath, Action action);
    }

    private VideoAction videoAction;
    private Action actionType;

    private boolean canSubmit = false;//能否再次提交
    private String filePath, framePath;

    public SurfaceViewLayout(Context context) {
        super(context);
    }

    public SurfaceViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView getDelayButton() {
        return delayButton;
    }

    private Handler recordFinishHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == AudioRecordHandler.COMPLETE_RECORD_VIDEO) {
                filePath = (String) msg.obj;// 视频地址
                String temp = System.currentTimeMillis() + ".jpg";
                framePath = AppFilePath.getBitmapFile(temp).getAbsolutePath();
                firstFrameBmp = rotateBitmap(firstFrameBmp);
                boolean success = RecordFileUtils.writeJpgImage(firstFrameBmp, framePath, 100);
                if (success) {
                    videoAction.videoAction(filePath, framePath, actionType);
                    canSubmit = true;
                }

            }
        }

        ;
    };

    public void init(Activity activity, VideoAction videoAction) {
        this.mActivity = activity;
        this.videoAction = videoAction;
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mProgressView = (ProgressView) activity.findViewById(R.id.recorder_progress);
        rlPause = findViewById(R.id.rl_pause);
        ivFrame = (ImageView) findViewById(R.id.iv_frame);
        rlPause.setVisibility(View.GONE);
        findViewById(R.id.iv_toPlay).setOnClickListener(playClickListener);
        prepareTimer = (TextView) findViewById(R.id.prepareTimer);
        delayButton = (TextView) findViewById(R.id.delayButton);
        delayButton.setOnClickListener(clickListener);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(callback);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// 低版本需要这个设置
        mProgressView.setUiRecordVideoController(this);
        mProgressView.setDrawMinLen(true);
        initProgressBar();
        // 初始化计时器
        mTimeCountHandler = new PrepareTimeCountHandler(this, prepareTimer, CameraSetting.MILLIS_IN_FUTURE,
                CameraSetting.COUNT_DOWN_INTERVAL);
        initMRHandler();
    }

    @Override
    public void setActionType(Action type) {
        actionType = type;
    }

    public View.OnClickListener playClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            actionType = Action.PREVIEW;
            commit();


        }
    };


    private void initProgressBar() {

        mProgressView.setMax(CameraSetting.Preference_Camera_Upload_Time_Limit);
        mProgressView.setMin(CameraSetting.Preference_Camera_Upload_Min_Time_Limit);
        mProgressView.setProgress(0);
    }

    private void initMRHandler() {
        mMrHandler = new AudioRecordHandler(getContext(), recordFinishHandler);
        mMrHandler.addBridgeListener(this);
    }

    private Camera initCamera(int cameraId) {
        if (cameraId == CameraInfo.CAMERA_FACING_BACK) {// 后置摄像头就打开闪光灯功能
            closeFlashLight(false);
        } else {
            closeFlashLight(true);
        }
        try {
            return open(cameraId);
        } catch (Exception e) {
            String text = getContext().getString(R.string.camera_forbidden_prompt);
            DialogTools.showCameraExceptionDialog(mActivity, text,
                    new DialogClickEventListener() {

                        @Override
                        public void onClick(String tag) {
                            mActivity.finish();
                        }
                    });
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    private Camera open(int cameraId) throws Exception {
        try {
            if (mCamera != null && mCameraId != cameraId) {
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
                mCameraId = -1;
            }
            if (mCamera == null) {
                if (CameraSetting.minVersionSDK()) {
                    mCamera = Camera.open(cameraId);
                } else {
                    mCamera = Camera.open();
                }
                mCameraId = cameraId;
            } else {
                mCamera.reconnect();
            }
            mCamera.setPreviewCallback(previewCallback);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            // 抛出自定义异常 可能是禁用了摄像头
            throw new CameraInitException("可能是禁用了摄像头" + e.getMessage());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return mCamera;
    }

    private void closeFlashLight(boolean close) {// 关掉闪光灯
        if (close) {
            if (mCamera != null) {
                Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
            mActivity.findViewById(R.id.camera_flashlight).setEnabled(false);
        } else {
            mActivity.findViewById(R.id.camera_flashlight).setEnabled(true);
        }
    }

    public void resetPreview() {
        if (mCamera != null) {
            startPreview();
        }
    }

    public void closeCamera() {
        if (mMrHandler != null && mMrHandler.isRecording()) {
            mMrHandler.stopRecord();
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            // mCamera.lock();
        }
    }

    public void destroyCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mMrHandler != null) {
            mMrHandler.reset();
        }
    }

    public void enableOrientationEvent() {
        if (orientationEventListener == null) {
            orientationEventListener = new MyOrientationEventListener(mActivity);
        }
        orientationEventListener.enable();
    }

    private void setCameraParameter(Camera camera) {
        if (camera != null) {
            Parameters parameters = mCamera.getParameters();
            List<Size> sizes = parameters.getSupportedPreviewSizes();
            Size size = CameraSetting.getOptimalPreviewSize(mActivity, sizes);
            // 重新计算view宽和高,重置params
            int height = BdjUtils.getSurfaceViewHeight(mActivity, size.height, size.width);
            surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(BdjUtils.getScreenWidth(mActivity), height));
            setLayoutParams(new LinearLayout.LayoutParams(BdjUtils.getScreenWidth(mActivity), height));

            RelativeLayout.LayoutParams layoutParams = (LayoutParams) delayButton.getLayoutParams();
            final int topMarginValue = (int) (mActivity.getResources().getDisplayMetrics().density * 200 + 0.5f);
            // layoutParams.bottomMargin = topMarginValue *
            // size.height/size.width;
            // delayButton.setLayoutParams(layoutParams);
            //
            parameters.setPreviewSize(size.width, size.height);
            parameters.setPreviewFrameRate(CameraSetting.RecorderParams.videoFrameRate);
            List<String> supportedFocus = parameters.getSupportedFocusModes();
            if (isSupported(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, supportedFocus)) {
                parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            String vstabSupported = parameters.get("video-stabilization-supported");
            if ("true".equals(vstabSupported)) {
                parameters.set("video-stabilization", "true");
            }
            mCamera.setParameters(parameters);
        }
    }

    private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }

    private void startPreview() {
        try {
            if (mPreviewing) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mPreviewing = false;
                }
            }
            int mDisplayRotation = CameraSetting.getDisplayRotation(mActivity);
            int orientation = CameraSetting.getDisplayOrientation(mDisplayRotation, mCameraId);
            mCamera.setDisplayOrientation(orientation);
            mCamera.setPreviewCallback(previewCallback);
            setCameraParameter(mCamera);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mPreviewing = true;
        } catch (Exception e) {
            // TODO: handle exception
            stopPreview();
            e.printStackTrace();
        } catch (Throwable e) {
            // TODO: handle exception
            stopPreview();
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopPreview();
        mCamera = null;
        mPreviewing = false;
    }

    private void startCountdown() {
        mTimeCountHandler.start();
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (recordButtonControllerListener != null) {
                recordButtonControllerListener.unableRecordButtonView();
            }
            delayActionUnable();
            startCountdown();
        }
    };

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            surfaceHolder = null;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            Log.d("test", "surface created");
            surfaceHolder = holder;
            if (mCamera != null) {
                return;
            }
            mCamera = initCamera(mCameraId);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            Log.d("test", "surface changed");
            if (holder.getSurface() == null) {
                return;
            }
            surfaceHolder = holder;
            if (mCamera == null) {
                mCamera = initCamera(mCameraId);
            }
            if (mPreviewing) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                startPreview();
            }
        }
    };

    private PreviewCallback previewCallback = new PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            if (mMrHandler != null) {
                Size size = camera.getParameters().getPreviewSize();
                // Log.e("aaaa",
                // "camera width="+size.width+", camera height="+size.height);
                long timeStemp = System.currentTimeMillis();
                mMrHandler.pushVideoFrame(data, timeStemp);
                if (mMrHandler.isRecording()) {
                    setFrameImage(data, size);
                }

            }
        }
    };

    private void setFrameImage(byte[] data, Size size) {
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, stream);
                bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                stream.close();
                Log.d("test", "first frame btp :" + firstFrameBmp);
                if (firstFrameBmp == null) {
                    firstFrameBmp = bitmap.copy(Config.ARGB_8888, true);// 保存首帧图片
                    Log.d("test", "-----------first frame btp :" + firstFrameBmp);
                }
            }
        } catch (Exception ex) {
            Log.e("test", "Error:" + ex.getMessage());
        }
    }

    @Override
    public void flashbackAction() {
        // TODO Auto-generated method stub
        if (mMrHandler != null) {
            mMrHandler.reset();
        }
    }

    @Override
    public void flashlightAction(boolean isOff) {
        // TODO Auto-generated method stub
        if (isOff) {
            if (mCamera != null) {
                Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        } else {
            if (mCamera != null) {
                Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            }
        }
    }

    @Override
    public void cameraOrientationAction(boolean ifFront) {
        // TODO Auto-generated method stub
        if (!mMrHandler.isRecording()) {
            int tempCameraId = 0;
            if (Build.VERSION.SDK_INT <= 8) {
                Toast.makeText(getContext(), getResources().getString(R.string.camera_version_msg), Toast.LENGTH_SHORT)
                        .show();
            } else {
                if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
                    tempCameraId = CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    tempCameraId = CameraInfo.CAMERA_FACING_BACK;
                }
            }
            initCamera(tempCameraId);
            startPreview();
        }
    }

    @Override
    public void addTimerUpdateListener(TimerUpdateListener listener) {
        // TODO Auto-generated method stub
        // record_seekbar.setTimerUpdateListener(listener);
        mProgressView.setTimerUpdateListener(listener);
    }

    @Override
    public void flashbackActionFromBottom() {
        // TODO Auto-generated method stub
        if (mMrHandler != null) {
            mMrHandler.flashbackData();
        }
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub
        setCanCommit(false);
        mMrHandler.startRecord();
        rlPause.setVisibility(View.GONE);
    }


    @Override
    public void stop() {
        // TODO Auto-generated method stub
        mMrHandler.stopRecord();
        if (bitmap != null) {
            // ExifInterface exi = new ExifInterface();
            bitmap = rotateBitmap(bitmap);
            ivFrame.setImageBitmap(bitmap);
            rlPause.setVisibility(View.VISIBLE);
        }
    }

    private Bitmap rotateBitmap(Bitmap bmp) {
        Matrix matrix = new Matrix();
        // rotate the Bitmap
        matrix.postRotate(90);

        // recreate the new Bitmap
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    @Override
    public boolean isRecording() {
        // TODO Auto-generated method stub
        return mMrHandler.isRecording();
    }

    @Override
    public void openLocalFile() {

    }

    @Override
    public void commit() {
        // TODO Auto-generated method stub
        if (canSubmit) {//直接提交
            videoAction.videoAction(filePath, framePath, actionType);
        } else {
            mMrHandler.finishRecord();
        }
    }

    @Override
    public void enableCommitView() {
        // TODO Auto-generated method stub
        if (recordButtonControllerListener != null) {
            recordButtonControllerListener.enableCommitView();
        } else {
            try {
                throw new Exception("it is called after addRecordControllerStateListener()");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addRecordControllerStateListener(RecordControllerStateListener listener) {
        // TODO Auto-generated method stub
        recordButtonControllerListener = listener;
    }

    @Override
    public Camera getCamera() {
        // TODO Auto-generated method stub
        if (mCamera == null) {
            mCamera = initCamera(mCameraId);
        }
        return mCamera;
    }

    @Override
    public int cameraId() {
        // TODO Auto-generated method stub
        return mCameraId;
    }

    @Override
    public Surface getSurface() {
        // TODO Auto-generated method stub
        if (surfaceHolder != null) {
            return surfaceHolder.getSurface();
        }
        return null;
    }

    @Override
    public ArHandler getMrHandler() {
        // TODO Auto-generated method stub
        if (mMrHandler == null) {
            initMRHandler();
        }
        return mMrHandler;
    }

    // @Override
    // public SeekbarListener getSeekbarListener() {
    // // TODO Auto-generated method stub
    // return record_seekbar;
    // }

    @Override
    public ProgressView getProgressViewListener() {
        // TODO Auto-generated method stub
        return mProgressView;
    }

    @Override
    public RecordControllerStateListener getRecordButtonControllerListener() {
        // TODO Auto-generated method stub
        return recordButtonControllerListener;
    }

    @Override
    public void delayActionEnable() {
        // TODO Auto-generated method stub
        delayButton.setEnabled(true);
        delayButton.setOnClickListener(clickListener);
    }

    @Override
    public void delayActionUnable() {
        // TODO Auto-generated method stub
        delayButton.setEnabled(false);
        delayButton.setOnClickListener(null);
    }

    @Override
    public int getOrientation() {
        // TODO Auto-generated method stub
        return getMorientation();
    }

    private int getMorientation() {
        int mNumberOfCameras = android.hardware.Camera.getNumberOfCameras();
        CameraInfo[] mInfo = new CameraInfo[mNumberOfCameras];
        for (int i = 0; i < mNumberOfCameras; i++) {
            mInfo[i] = new CameraInfo();
            android.hardware.Camera.getCameraInfo(i, mInfo[i]);
            if (mBackCameraId == -1 && mInfo[i].facing == CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraId = i;
            }
            if (mFrontCameraId == -1 && mInfo[i].facing == CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraId = i;
            }
        }
        int rotation = 0;
        CameraInfo info = mInfo[mCameraId];
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - mOrientation + 360) % 360;
        } else { // back-facing camera
            rotation = (info.orientation + mOrientation) % 360;
        }
        return rotation;
    }

    private class MyOrientationEventListener extends OrientationEventListener {
        public MyOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            // We keep the last known orientation. So if the user first orient
            // the camera then point the camera to floor or sky, we still have
            // the correct orientation.
            if (orientation == ORIENTATION_UNKNOWN)
                return;
            mOrientation = CameraSetting.roundOrientation(orientation, mOrientation);
            // When the screen is unlocked, display rotation may change. Always
            // calculate the up-to-date orientationCompensation.
            int orientationCompensation = mOrientation + CameraSetting.getDisplayRotation(mActivity);
        }

    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    @Override
    public void setCanCommit(boolean flag) {
        // TODO Auto-generated method stub
        this.canSubmit = flag;
    }
}
