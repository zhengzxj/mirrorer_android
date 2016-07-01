package com.smart.mirrorer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.smart.mirrorer.R;
import com.smart.mirrorer.util.DeviceConfiger;

public class DividerLineLayout extends RelativeLayout {
    //
    private boolean drawTopDivider;
    private boolean topDashedDivider;
    private int dividerTopLeftMarge;
    //
    private boolean drawBottomDivider;
    private boolean bottomDashedDivider;
    private int dividerBottomLeftMarge;

    private Paint mTopLinePaint;
    private Paint mBottomLinePaint;

    public DividerLineLayout(Context context) {
        super(context);
    }

    public DividerLineLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DividerLineLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readParameters(context, attrs);

        mTopLinePaint = createPaint();
        mBottomLinePaint = createPaint();
        // 顶部虚线
        if (topDashedDivider) {
            PathEffect effects = new DashPathEffect(new float[]{5, 2, 5, 8}, 1);
            mTopLinePaint.setPathEffect(effects);
        }
        // 底部虚线
        if (bottomDashedDivider) {
            PathEffect effects = new DashPathEffect(new float[]{5, 2, 5, 8}, 1);
            mBottomLinePaint.setPathEffect(effects);
        }

        int paddingDP = DeviceConfiger.dp2px(1);
        int pt = getPaddingTop();
        int pb = getPaddingBottom();
        int pl = getPaddingLeft();
        int pr = getPaddingRight();

        if (drawTopDivider && pt < paddingDP) {
            pt = paddingDP;
        }
        if (drawBottomDivider && pb < paddingDP) {
            pb = paddingDP;
        }
        setPadding(pl, pt, pr, pb);
    }

    private Paint createPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#dddddd")); // 设置画笔颜色
        int sw = DeviceConfiger.dp2px(1);
        paint.setStrokeWidth(sw);
        return paint;
    }

    private void readParameters(Context context, AttributeSet attrs) {
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.DividerLineLayout);
        try {
            dividerTopLeftMarge = types.getDimensionPixelSize(R.styleable.DividerLineLayout_dividerTopLeftMarge, 0);
            dividerBottomLeftMarge = types.getDimensionPixelSize(R.styleable.DividerLineLayout_dividerBottomLeftMarge, 0);
            drawTopDivider = types.getBoolean(R.styleable.DividerLineLayout_drawTopDivider, false);
            drawBottomDivider = types.getBoolean(R.styleable.DividerLineLayout_drawBottomDivider, true);
            topDashedDivider = types.getBoolean(R.styleable.DividerLineLayout_topDashedDivider, false);
            bottomDashedDivider = types.getBoolean(R.styleable.DividerLineLayout_bottomDashedDivider, false);
        } finally {
            types.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int hight = getMeasuredHeight();

        if (drawTopDivider) {
            // 是否是虚线
            Path path = new Path();
            path.moveTo(0 + dividerTopLeftMarge, 0);
            path.lineTo(width, 0);
            canvas.drawPath(path, mTopLinePaint);
            // drawLine 虚线不生效
//            canvas.drawLine(0 + dividerTopLeftMarge, 0, width, 0, mTopLinePaint);
        }

        if (drawBottomDivider) {
            Path path = new Path();
            path.moveTo(0 + dividerBottomLeftMarge, hight);
            path.lineTo(width, hight);
            canvas.drawPath(path, mBottomLinePaint);
            // drawLine 虚线不生效
//            canvas.drawLine(0 + dividerBottomLeftMarge, hight, width, hight, p);
        }
    }

    public void setDrawTopDivider(boolean drawTopDivider) {
        this.drawTopDivider = drawTopDivider;
        invalidate();
    }

    public void setDrawBottomDivider(boolean drawBottomDivider) {
        this.drawBottomDivider = drawBottomDivider;
        invalidate();
    }
}
