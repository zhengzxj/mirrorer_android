package com.videorecorder.util;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Build;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;

public class CameraSetting {
    public final static int Preference_Camera_Quality = Build.VERSION.SDK_INT < 11 ? CamcorderProfile.QUALITY_HIGH
            : CamcorderProfile.QUALITY_480P;
    public static int Preference_Camera_Upload_Time_Limit = 1000 * 30;    // 最大录制30秒
                                                                       // 2014-04-16
                                                                       // 09:43
    public static int Preference_Camera_Upload_Min_Time_Limit = 1000 * 3; // 最小录制5秒
                                                                          // 2014-04-17
                                                                          // 14:01
    public static final int ORIENTATION_HYSTERESIS = 5;
    public final static int VIDEO_PARAM_BITRATE = 1000 * 1024;
    private static boolean AAC_SUPPORTED = Build.VERSION.SDK_INT >= 10;
    public final static int CAMERA_WIDTH = 640;
    public final static int CAMERA_HEIGHT = 480;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    public static final int MILLIS_IN_FUTURE = 4 * 1000;
    public static final boolean ENBLE_EDIT = false; // 启用视频编辑功能
    public static final boolean ENBLE_LOCAL_FILE = false; // 启用本地视频选择
    public static final boolean ENBLE_LOCAL_FILE_AND_EDIT = false; // 启用本地视频选择
    public static final boolean LOCAL_FILE_NOT_EDIT = true; // 本地视频不压缩，不编辑
    
    // 文件大小
    public static String fileSize = ""; 
    // 最大时长
    public static String maxDuration = ""; 
    // 最小时长
    public static String minDuration = ""; 
    // 支持格式
    public static String mediaType = ""; 
    
    public static boolean minVersionSDK() {
        if (Build.VERSION.SDK_INT <= 8) {
            return false;
        } else {
            return true;
        }
    }
    
    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
            return 0;
            case Surface.ROTATION_90:
            return 90;
            case Surface.ROTATION_180:
            return 180;
            case Surface.ROTATION_270:
            return 270;
        }
        return 0;
    }
    
    @SuppressLint("NewApi")
    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for
        // documentation.
        CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
    
    public static Size getOptimalPreviewSize(Activity currentActivity, List<Size> sizes) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null)
            return null;
        int videoWidth = CAMERA_WIDTH;
        int videoHeight = CAMERA_HEIGHT;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        double targetRatio = (double) videoWidth / videoHeight;
        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size
        
        Display display = currentActivity.getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(videoWidth, videoHeight);
        
        if (targetHeight <= 0) {
            // We don't know the size of SurfaceView, use screen height
            targetHeight = display.getHeight();
        }
        
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    
    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, 360 - dist);
            changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }
    
    @SuppressLint("NewApi")
    public CameraInfo getInfo() {
        CameraInfo info = new CameraInfo();
        return null;
    }
    public interface RecorderParams {
        public int videoFrameRate = 15;
        public int audioChannel = 1;
        
        public int audioBitrate = 96000;// 192000;//AAC_SUPPORTED ? 96000 :
                                        // 12200;
        
        public int videoBitrate = 512000;
        
        public int audioSamplingRate = AAC_SUPPORTED ? 44100 : 8000;
        
    }
    
}
