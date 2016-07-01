package com.androidex.sharesdk.core;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidex.sharesdk.R;

public class ShareActionView implements OnClickListener, OnItemClickListener {
    private static final int TRANSLATE_DURATION = 200;
    private static final int ALPHA_DURATION = 300;
    private LinearLayout mPanel;
    private Activity mActivity;
    private View mContentView;
    private GridView mGridView;
    private TextView tvCancel;
    private Handler mainHandle = new Handler();
    private PlatformEntityAdater mAdapter;
    private PlatformSelectListener listener;

    public ShareActionView(Activity activity)
    {
        super();
        this.mActivity = activity;
        mContentView = View.inflate(activity, R.layout.share_action_view, null);
        mPanel = (LinearLayout) mContentView.findViewById(R.id.bottom_action_ll);
        mContentView.setBackgroundColor(Color.argb(136, 0, 0, 0));
        mContentView.setOnClickListener(this);
        mPanel.setOnClickListener(this);
        //
        tvCancel = (TextView) mContentView.findViewById(R.id.tv_cancel);
        mGridView = (GridView) mContentView.findViewById(R.id.gv_bottom);
        mGridView.setVerticalScrollBarEnabled(true);
        mGridView.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return MotionEvent.ACTION_MOVE == event.getAction() ? true : false;
            }
        });
        mAdapter = new PlatformEntityAdater(activity, PlatformEntity.getAllEnableEntity());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        tvCancel.setOnClickListener(this);
        mainHandle.postDelayed(new Runnable()
        {

            @Override
            public void run() {
                mContentView.setVisibility(View.VISIBLE);
                mContentView.startAnimation(createAlphaInAnimation());
                mPanel.startAnimation(createTranslationInAnimation());
            }
        }, 100);
        mContentView.setVisibility(View.INVISIBLE);
        //
        activity.setContentView(mContentView);
    }

    public void setListener(PlatformSelectListener listener) {
        this.listener = listener;
    }

    private Animation createTranslationInAnimation() {
        int type = Animation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 1, type, 0);
        an.setDuration(TRANSLATE_DURATION);
        return an;
    }

    private Animation createAlphaInAnimation() {
        AlphaAnimation an = new AlphaAnimation(0, 1);
        an.setDuration(ALPHA_DURATION);
        return an;
    }

    private Animation createTranslationOutAnimation(AnimationListener listener) {
        int type = Animation.RELATIVE_TO_SELF;
        TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type, 0, type, 1);
        an.setAnimationListener(listener);
        an.setDuration(TRANSLATE_DURATION);
        an.setFillAfter(true);
        return an;
    }

    private Animation createAlphaOutAnimation(AnimationListener listener) {
        AlphaAnimation an = new AlphaAnimation(1, 0);
        an.setDuration(ALPHA_DURATION);
        an.setAnimationListener(listener);
        an.setFillAfter(true);
        return an;
    }

    @Override
    public void onClick(View v) {
        animaFinishActivity();
    }

    private void animaFinishActivity() {
        mContentView.startAnimation(createAlphaOutAnimation(new AnimationListener()
        {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mContentView.setVisibility(View.INVISIBLE);
                mActivity.finish();
            }
        }));
        mPanel.startAnimation(createTranslationOutAnimation(null));
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        PlatformEntity platform = (PlatformEntity) mAdapter.getItem(arg2);
        if (listener != null) {
            listener.onSelectPlatform(platform);
        }
    }

    public static interface PlatformSelectListener {
        void onSelectPlatform(PlatformEntity platform);
    }
}
