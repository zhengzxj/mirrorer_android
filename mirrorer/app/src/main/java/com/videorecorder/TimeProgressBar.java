package com.videorecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class TimeProgressBar extends View {
    private final int default_text_color = Color.rgb(0x71, 0x78, 0x7E);
    private final int default_max_text_color = Color.rgb(0x27, 0x31, 0x38);
    private final int default_reached_color = Color.rgb(0xFF, 0x9C, 0x00);
    private final int default_unreached_color = Color.rgb(0x4B, 0x52, 0x67);
    private final int default_min_len_color = Color.rgb(0xFF, 0x60, 0x60);

    private int mMaxProgress = 100;
    private int mMinProgress = 10;
    private int mCurrentProgress = 0;
    private int mReachedBarColor = default_reached_color;
    private int mUnreachedBarColor = default_unreached_color;
    private int mMinLenColor = default_min_len_color;
    private int mTextColor = default_text_color;
    private int mMaxTextColor = default_max_text_color;
    private float mTextSize;
    private float mMaxTextSize;
    private float mReachedBarHeight;
    private float mUnreachedBarHeight;

    private float mDrawTextWidth;  // draw 文字的宽度
    private float mDrawTextHight;  // draw 文字的高度
    private float mDrawTextStart;  // draw 文字的X轴开始位置
    private float mDrawTextEnd;    // draw 文字的y轴开始位置
    private float mOffset;         // 文本距离进度的距离
    private float mMinWidthLen;         // 最小进度宽度

    private String mCurrentDrawText; // 当前要绘制的文本
    //
    private float mDrawMaxTextX;  // draw Max文字的X轴开始位置
    private String mCurrentDrawMaxText; // 当前要绘制的Max文本
    //
    private Paint mReachedBarPaint;
    private Paint mUnreachedBarPaint;
    private Paint mMinReadPaint;
    private Paint mTextPaint;
    private Paint mMaxTextPaint;
    // 剩余进度范围
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    // 进度范围
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);
    // 最小进度位置范围
    private RectF mMinReachedRectF = new RectF(0, 0, 0, 0);

    public TimeProgressBar(Context context) {
        this(context, null);
    }

    public TimeProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mReachedBarHeight = dp2px(5f);
        mUnreachedBarHeight = dp2px(5f);
        mTextSize = sp2px(16);
        mMaxTextSize = sp2px(16);
        mOffset = dp2px(2.0f);
        mMinWidthLen = dp2px(1f);
        //
        setProgress(0);
        setMax(100);
        initializePainters();

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mDrawTextHight = (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) (mReachedBarHeight + mDrawTextHight + mOffset), (int) (mUnreachedBarHeight + mDrawTextHight + mOffset));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateDrawRectFWithoutProgressBottomText();
        canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
        if (isDrawMinLen) {
            canvas.drawRect(mMinReachedRectF, mMinReadPaint);
        }

        canvas.drawRect(mReachedRectF, mReachedBarPaint);
        //
        if (isDrawMaxText) {
            canvas.drawText(mCurrentDrawMaxText, mDrawMaxTextX, mDrawTextEnd, mMaxTextPaint);
        }
        canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
    }

    boolean isDrawMaxText = true;
    boolean isDrawMinLen = false;

    //
    public void setDrawMaxText(boolean isDrawText) {
        this.isDrawMaxText = isDrawText;
        invalidate();
    }

    public void setDrawMinLen(boolean isDrawMinLen) {
        this.isDrawMinLen = isDrawMinLen;
        invalidate();
    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mMaxTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaxTextPaint.setColor(mMaxTextColor);
        mMaxTextPaint.setTextSize(mMaxTextSize);

        mMinReadPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinReadPaint.setColor(mMinLenColor);
    }


    private void calculateDrawRectFWithoutProgressBottomText() {
        // 已读
        mReachedRectF.left = getPaddingLeft();
        float right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft();
        mReachedRectF.right = right;
        mReachedRectF.top = getPaddingTop();
        mReachedRectF.bottom = mReachedBarHeight + getPaddingTop();
        // 剩余
        mUnreachedRectF.left = mReachedRectF.right;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getPaddingTop();
        mUnreachedRectF.bottom = mUnreachedBarHeight + getPaddingTop();

        // 最小范围
        float minLeft = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getMin() + getPaddingLeft();
        mMinReachedRectF.left = minLeft - mMinWidthLen;
        mMinReachedRectF.right = minLeft;
        mMinReachedRectF.top = getPaddingTop();
        mMinReachedRectF.bottom = mUnreachedBarHeight + getPaddingTop();

        mCurrentDrawText = String.format("%d秒", getProgress() / 1000);
        //TODO 获取text实际高度不准确，不实际看的效果要高
        float tempDrawTextHight = mDrawTextHight - 12;
        mDrawTextEnd = Math.max(mReachedBarHeight, mUnreachedBarHeight) + tempDrawTextHight + mOffset + getPaddingTop();
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);
        //
        if (getProgress() == 0 || mReachedRectF.right < mDrawTextWidth) {
            mDrawTextStart = getPaddingLeft();
            // 进度为0时不显示当前进度
            if (getProgress() == 0) {
                mCurrentDrawText = "";
            }
        } else {
            mDrawTextStart = mReachedRectF.right - mDrawTextWidth;
        }

        // 宽度大于View的宽度
        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - mDrawTextWidth - getPaddingRight();
        }

        // max文字
        mCurrentDrawMaxText = String.format("%d秒", getMax() / 1000);
        float drawMaxTextWidth = mMaxTextPaint.measureText(mCurrentDrawMaxText);
        mDrawMaxTextX = getWidth() - drawMaxTextWidth - getPaddingRight();

    }


    public int getTextColor() {
        return mTextColor;
    }

    public float getProgressTextSize() {
        return mTextSize;
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public int getMin() {
        return mMinProgress;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setProgressTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setProgressTextColor(int textColor) {
        this.mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setMaxTextColor(int textColor) {
        this.mMaxTextColor = textColor;
        mMaxTextPaint.setColor(mMaxTextColor);
        invalidate();
    }

    public void setUnreachedBarColor(int barColor) {
        this.mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarColor(int progressColor) {
        this.mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }

    public void setUnreachedBarHeight(float height) {
        mUnreachedBarHeight = height;
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public void setMin(int minProgress) {
        if (minProgress > 0 && minProgress < mMaxProgress) {
            this.mMinProgress = minProgress;
            invalidate();
        }
    }


    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(getProgress() + by);
        }
    }

    public void setProgress(int progress) {
        if (progress <= getMax() && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
        }
        //
        if (mListener != null) {
            mListener.onProgressChange(getProgress(), getMin(), getMax());
        }
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    private OnProgressBarListener mListener;

    public void setOnProgressBarListener(OnProgressBarListener listener) {
        mListener = listener;
    }

    public static interface OnProgressBarListener {
        void onProgressChange(int current, int min, int max);
    }
}
