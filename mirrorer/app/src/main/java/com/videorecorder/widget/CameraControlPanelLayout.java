package com.videorecorder.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.videorecorder.listener.TimerUpdateListener;
import com.videorecorder.listener.UICameraController;
import com.videorecorder.util.BdjUtils;


public class CameraControlPanelLayout extends RelativeLayout implements
        TimerUpdateListener {
    private ImageView camera_flashback;
    private TextView camera_timer;
    private CheckBox camera_flashlight;
    private CheckBox camera_orientation;
    private UICameraController uiCameraController;
    private Context context;
    private String text;

    public CameraControlPanelLayout(Context context) {
        super(context);
        this.context = context;
    }

    public CameraControlPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init() {
        camera_flashback = (ImageView) findViewById(R.id.camera_flashback);
        camera_timer = (TextView) findViewById(R.id.camera_timer);

        text = (String) camera_timer.getText();
        camera_flashlight = (CheckBox) findViewById(R.id.camera_flashlight);
        camera_orientation = (CheckBox) findViewById(R.id.camera_orientation);
        camera_flashback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Activity) context).finish();
                // String time = (String) camera_timer.getText();
                // if (text.equals(time) || TextUtils.isEmpty(time)) {
//				DialogTools.showCommonDialog((Activity)context, new String[] { "取消拍摄", "重新拍摄" }, new DialogClickEventListener()
//		        {
//		            @Override
//		            public void onClick(String tag) {
//		                if ("取消拍摄".equals(tag)) {
//		                	((Activity)context).finish();
//		                } else if ("重新拍摄".equals(tag)) {
//		                	uiCameraController.flashbackAction();
//		                }
//		            }
//		        }, false);
//				
                // }
            }
        });
        camera_flashlight
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        uiCameraController.flashlightAction(isChecked);
                    }
                });
        camera_orientation
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        uiCameraController.cameraOrientationAction(isChecked);
                    }
                });

    }

    public void setUiCameraController(UICameraController uiCameraController) {
        this.uiCameraController = uiCameraController;
        this.uiCameraController.addTimerUpdateListener(this);
    }

    @Override
    public void updateTime(long millisecond) {
        // TODO Auto-generated method stub
        if (millisecond > 0) {
            String textTime = BdjUtils.millisecondToTimeString(millisecond,
                    false);
            String text = getResources().getString(R.string.rec_time, textTime);
            camera_timer.setText(text);
        } else {
            camera_timer.setText(null);
        }
    }

}
