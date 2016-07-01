package com.videorecorder.widget;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.util.MaterialUtils;
import com.videorecorder.listener.RecordControllerStateListener;
import com.videorecorder.listener.UIRecordVideoController;
import com.videorecorder.util.CameraSetting;
import com.videorecorder.widget.SurfaceViewLayout.Action;


public class ControlPanelLayout extends RelativeLayout implements RecordControllerStateListener {
    private TextView record_flashbackbutton;
    private ImageView record_button;
    private TextView record_commitbutton;
    private ImageView record_localfilebutton;
    private UIRecordVideoController uiRecordVideoController;

    private Drawable voiceNomaleDrawable;
    private Drawable voicePlayDrawable;
    private Drawable voicePauseDrawable;

    public ControlPanelLayout(Context context) {
        super(context);
    }

    public ControlPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init() {
        record_flashbackbutton = (TextView) findViewById(R.id.camera_record_flashback);
        record_button = (ImageView) findViewById(R.id.camera_record_button);
        record_commitbutton = (TextView) findViewById(R.id.camera_record_commitbutton);
        record_localfilebutton = (ImageView) findViewById(R.id.camera_record_selectlocalfile);
        record_localfilebutton.setVisibility(View.GONE);
        record_flashbackbutton.setVisibility(View.GONE);
        record_flashbackbutton.setOnClickListener(onClickListener);

        voiceNomaleDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play));
        voicePlayDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_play));
        voicePauseDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause));

        Drawable recordingDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_title_follow));
        record_flashbackbutton.setCompoundDrawables(null, recordingDrawable, null, null);
        record_flashbackbutton.setTextColor(Color.WHITE);
        //
        Drawable nextDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.ic_enter_arrow_navigation));
        record_commitbutton.setCompoundDrawables(null, nextDrawable, null, null);
        record_commitbutton.setTextColor(Color.WHITE);
        record_commitbutton.setOnClickListener(onClickListener);
        record_commitbutton.setVisibility(View.GONE);
        //
        Drawable videoBgDrawable = MaterialUtils.createSolidStrokeBg(Color.parseColor("#031826"), Color.parseColor("#4fb4e9"), 8, 360);
        record_button.setBackgroundDrawable(videoBgDrawable);
        record_button.setImageDrawable(voiceNomaleDrawable);
        record_button.setOnClickListener(onClickListener);
    }

    public void setUiRecordVideoController(UIRecordVideoController uiRecordVideoController) {
        this.uiRecordVideoController = uiRecordVideoController;
        this.uiRecordVideoController.addRecordControllerStateListener(this);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == record_flashbackbutton) {
                uiRecordVideoController.flashbackActionFromBottom();
                record_button.setImageDrawable(voiceNomaleDrawable);
                unableCommitView();
            } else if (v == record_commitbutton) {
                uiRecordVideoController.setActionType(Action.UPLOAD);
                uiRecordVideoController.commit();
            } else if (v == record_localfilebutton) {
                uiRecordVideoController.openLocalFile();
            }
            if (v == record_button) {
                if (!uiRecordVideoController.isRecording()) {
                    record_button.setImageDrawable(voicePauseDrawable);
                    uiRecordVideoController.start();
                } else {
                    record_button.setImageDrawable(voicePlayDrawable);
                    uiRecordVideoController.stop();
                }
            }
        }
    };

    @Override
    public void onReset() {
        unableBackView();
        unableCommitView();
    }

    @Override
    public void enableBackView() {
        if (CameraSetting.ENBLE_LOCAL_FILE) {
            record_flashbackbutton.setVisibility(View.VISIBLE);
            record_localfilebutton.setVisibility(View.GONE);
        }
        record_flashbackbutton.setFocusable(true);
        record_flashbackbutton.setVisibility(View.VISIBLE);
        ;
        record_flashbackbutton.setOnClickListener(onClickListener);
    }

    @Override
    public void unableBackView() {
        // 回退按钮状态
        record_flashbackbutton.setVisibility(View.GONE);
        record_flashbackbutton.setVisibility(View.GONE);
//        record_button.setImageResource(R.drawable.ic_record_start);
    }

    @Override
    public void enableCommitView() {
        record_commitbutton.setVisibility(View.VISIBLE);
    }

    @Override
    public void unableCommitView() {
        // 提交按钮状态
        record_commitbutton.setVisibility(View.GONE);
    }

    @Override
    public void enableRecordButtonView() {
        record_button.setImageDrawable(voicePauseDrawable);
    }

    @Override
    public void unableRecordButtonView() {
//        record_button.setEnabled(false);
        record_button.setImageDrawable(voicePlayDrawable);
    }

    @Override
    public void downRecordButtonView() {
        record_button.setImageDrawable(voicePlayDrawable);
    }
}
