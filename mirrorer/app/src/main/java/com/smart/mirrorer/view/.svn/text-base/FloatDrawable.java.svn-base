package com.smart.mirrorer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class FloatDrawable extends Drawable {
    private Paint mLinePaint = new Paint();
    private Rect mRect;

    public FloatDrawable(Context paramContext) {
        this.mLinePaint.setStrokeWidth(5.0F);
        this.mLinePaint.setStyle(Paint.Style.STROKE);
        this.mLinePaint.setAntiAlias(true);
        this.mLinePaint.setColor(Color.parseColor("#ffffff"));
        this.mRect = new Rect();
    }

    public void draw(Canvas paramCanvas) {
        if (this.mRect.isEmpty()) {
            int i = getBounds().left;
            int j = getBounds().top;
            int k = getBounds().right;
            int m = getBounds().bottom;
            this.mRect.set(i, j, k, m);
        }
        paramCanvas.drawRect(this.mRect, this.mLinePaint);
    }

    public int getOpacity() {
        return 0;
    }

    public void setAlpha(int paramInt) {
    }

    public void setColorFilter(ColorFilter paramColorFilter) {
    }
}