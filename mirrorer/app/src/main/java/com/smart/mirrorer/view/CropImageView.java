package com.smart.mirrorer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.smart.mirrorer.util.TipsUtils;


public class CropImageView extends View {

    // 单点触摸的时候
    private float oldX = 0;
    private float oldY = 0;

    // 多点触摸的时候
    private float oldx_0 = 0;
    private float oldy_0 = 0;

    private float oldx_1 = 0;
    private float oldy_1 = 0;

    // 状态
    private final int STATUS_TOUCH_SINGLE = 1;// 单点
    private final int STATUS_TOUCH_MULTI_START = 2;// 多点开始
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3;// 多点拖拽中

    private int mStatus = STATUS_TOUCH_SINGLE;

    // 默认的裁剪图片宽度与高度
    private final int defaultCropWidth = 300;
    private final int defaultCropHeight = 300;

    private int cropWidth = defaultCropWidth;
    private int cropHeight = defaultCropHeight;

    protected float oriRationWH = 0;// 原始宽高比率
    protected final float maxZoomOut = 5.0f;// 最大扩大到多少倍
    protected final float minZoomIn = 0.333333f;// 最小缩小到多少倍

    protected Drawable mDrawable;// 原图
    protected FloatDrawable mFloatDrawable;// 浮层
    protected Rect mDrawableSrc = new Rect();
    protected Rect mDrawableDst = new Rect();
    protected Rect mDrawableFloat = new Rect();// 浮层选择框，就是头像选择框
    protected boolean isFrist = true;
    protected Context mContext;
    protected Bitmap source = null;
    private float defaultFloatWidth = 300; // dp
    private float defaultFloatHeight = 300;
    private String origenalImagePath;
    private static final int CHECK_BOUNDS = 20;

    public CropImageView(Context context)
    {
        super(context);
        init(context);
    }

    public void setSourseBitmap(Bitmap sour) {
        this.source = sour;
    }

    public CropImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);

    }

    Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case CHECK_BOUNDS:
                checkBounds();
                break;
            default:
                break;
            }
        }

    };

    @SuppressLint("NewApi")
    private void init(Context context) {
        this.mContext = context;
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mFloatDrawable = new FloatDrawable(context);// 头像选择框
        mHandler.sendEmptyMessageDelayed(CHECK_BOUNDS, 500);
    }

    public void setDrawable(Drawable mDrawable, int cropWidth, int cropHeight) {
        this.mDrawable = mDrawable;
        this.cropWidth = cropWidth;
        this.cropHeight = cropHeight;
        this.isFrist = true;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            if (mStatus == STATUS_TOUCH_SINGLE) {
                mStatus = STATUS_TOUCH_MULTI_START;

                oldx_0 = event.getX(0);
                oldy_0 = event.getY(0);

                oldx_1 = event.getX(1);
                oldy_1 = event.getY(1);
            } else if (mStatus == STATUS_TOUCH_MULTI_START) {
                mStatus = STATUS_TOUCH_MULTI_TOUCHING;
            }
        } else {
            if (mStatus == STATUS_TOUCH_MULTI_START || mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                oldx_0 = 0;
                oldy_0 = 0;

                oldx_1 = 0;
                oldy_1 = 0;

                oldX = event.getX();
                oldY = event.getY();
            }

            mStatus = STATUS_TOUCH_SINGLE;
        }

        // Log.v("count currentTouch"+currentTouch, "-------");

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // Log.v("count ACTION_DOWN", "-------");
            oldX = event.getX();
            oldY = event.getY();
            break;

        case MotionEvent.ACTION_UP:
            // Log.v("count ACTION_UP", "-------");
            checkBounds();
            break;

        case MotionEvent.ACTION_POINTER_1_DOWN:
            // Log.v("count ACTION_POINTER_1_DOWN", "-------");
            break;

        case MotionEvent.ACTION_POINTER_UP:
            // Log.v("count ACTION_POINTER_UP", "-------");
            break;

        case MotionEvent.ACTION_MOVE:
            // Log.v("count ACTION_MOVE", "-------");
            if (mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                float newx_0 = event.getX(0);
                float newy_0 = event.getY(0);

                float newx_1 = event.getX(1);
                float newy_1 = event.getY(1);

                float oldWidth = Math.abs(oldx_1 - oldx_0);
                float oldHeight = Math.abs(oldy_1 - oldy_0);

                float newWidth = Math.abs(newx_1 - newx_0);
                float newHeight = Math.abs(newy_1 - newy_0);

                boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math.abs(newWidth - oldWidth);

                float ration = isDependHeight ? ((float) newHeight / (float) oldHeight)
                        : ((float) newWidth / (float) oldWidth);
                int centerX = mDrawableDst.centerX();
                int centerY = mDrawableDst.centerY();
                int _newWidth = (int) (mDrawableDst.width() * ration);
                int _newHeight = (int) ((float) _newWidth / oriRationWH);

                float tmpZoomRation = (float) _newWidth / (float) mDrawableSrc.width();
                if (tmpZoomRation >= maxZoomOut) {
                    _newWidth = (int) (maxZoomOut * mDrawableSrc.width());
                    _newHeight = (int) ((float) _newWidth / oriRationWH);
                } else if (tmpZoomRation <= minZoomIn) {
                    _newWidth = (int) (minZoomIn * mDrawableSrc.width());
                    _newHeight = (int) ((float) _newWidth / oriRationWH);
                }

                mDrawableDst.set(centerX - _newWidth / 2, centerY - _newHeight / 2, centerX + _newWidth / 2, centerY
                        + _newHeight / 2);
                invalidate();

                Log.e("wuzhenlin", "width():" + (mDrawableSrc.width()) + "height():" + (mDrawableSrc.height())
                        + " new width():" + (mDrawableDst.width()) + "new height():" + (mDrawableDst.height()));
                Log.e("wuzhenlin", (float) mDrawableSrc.height() / (float) mDrawableSrc.width() + " mDrawableDst:"
                        + (float) mDrawableDst.height() / (float) mDrawableDst.width());

                oldx_0 = newx_0;
                oldy_0 = newy_0;

                oldx_1 = newx_1;
                oldy_1 = newy_1;
            } else if (mStatus == STATUS_TOUCH_SINGLE) {
                int dx = (int) (event.getX() - oldX);
                int dy = (int) (event.getY() - oldY);

                oldX = event.getX();
                oldY = event.getY();

                if (!(dx == 0 && dy == 0)) {
                    mDrawableDst.offset((int) dx, (int) dy);
                    invalidate();
                }
            }
            break;
        }

        // Log.v("event.getAction()："+event.getAction()+"count："+event.getPointerCount(),
        // "-------getX:"+event.getX()+"--------getY:"+event.getY());
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return; // nothing to draw (empty bounds)
        }

        configureBounds();
        mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
    }

    protected void configureBounds() {
        if (isFrist) {
            oriRationWH = ((float) mDrawable.getIntrinsicWidth()) / ((float) mDrawable.getIntrinsicHeight());

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth() * scale + 0.5f));
            int h = (int) (w / oriRationWH);

            int left = (getWidth() - w) / 2;
            int top = (getHeight() - h) / 2;
            int right = left + w;
            int bottom = top + h;

            mDrawableSrc.set(left, top, right, bottom);
            mDrawableDst.set(mDrawableSrc);

            int floatWidth = dipTopx(mContext, cropWidth);
            int floatHeight = dipTopx(mContext, cropHeight);
            int floatLeft = (getWidth() - floatWidth) / 2;
            int floatTop = (getHeight() - floatHeight) / 2;
            mDrawableFloat.set(floatLeft, floatTop, floatLeft + floatWidth, floatTop + floatHeight);

            isFrist = false;
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }

    protected void checkBounds() {
        // 1.判断是否是否缩放比选取框小
        float oldWidth = mDrawableDst.width();
        float oldHeight = mDrawableDst.height();

        float newWidth = mDrawableFloat.width();
        float newHeight = mDrawableFloat.height();

        if (oldWidth < newWidth || oldHeight < newHeight) {
            boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math.abs(newWidth - oldWidth);
            float ration = isDependHeight ? ((float) newHeight / (float) oldHeight)
                    : ((float) newWidth / (float) oldWidth);
            int centerX = mDrawableDst.centerX();
            int centerY = mDrawableDst.centerY();
            int _newWidth = (int) (mDrawableDst.width() * ration);
            int _newHeight = (int) ((float) _newWidth / oriRationWH);
            mDrawableDst.set(centerX - _newWidth / 2, centerY - _newHeight / 2, centerX + _newWidth / 2, centerY
                    + _newHeight / 2);
            invalidate();
        }

        // 2.判断是否拖出边界。
        int newLeft = mDrawableDst.left;
        int newTop = mDrawableDst.top;
        int floatLeft = mDrawableFloat.left;
        boolean isChange = false;

        if (newLeft > floatLeft) {
            newLeft = this.mDrawableFloat.left;
            isChange = true;
        }

        if (this.mDrawableDst.right < this.mDrawableFloat.right) {
            newLeft = this.mDrawableFloat.left; // -(this.mDrawableDst.width() -
                                                // this.mDrawableFloat.right);
            isChange = true;
        }

        if (this.mDrawableDst.top > this.mDrawableFloat.top) {
            newTop = this.mDrawableFloat.top;
            isChange = true;
        }

        if (this.mDrawableDst.bottom < this.mDrawableFloat.bottom) {
            newTop = -(this.mDrawableDst.height() - this.mDrawableFloat.bottom);
            isChange = true;
        }
        if (isChange) {
            this.mDrawableDst.offsetTo(newLeft, newTop);
            invalidate();
        }
    }

    public Bitmap getSourceCropImage() throws Exception {

        // 原始图片，从文件里面读取
        Bitmap sourceBitmap = BitmapFactory.decodeFile(origenalImagePath);

        // 计算屏幕上裁剪框的左上角距离 “显示在屏幕上图片”的左上角距离dx,dy和“显示在屏幕上图片”的宽高showW, showH
        // 比值factX,factY
        float factX = (mFloatDrawable.getBounds().left - mDrawable.getBounds().left)
                / (mDrawable.getBounds().width() * 1.0f);
        float factY = (mFloatDrawable.getBounds().top - mDrawable.getBounds().top)
                / (mDrawable.getBounds().height() * 1.0f);

        if (factX < 0 || factY < 0) {
//            TipsUtils.showShort(mContext, "请指定裁剪区");
            return null;
        }

        // 计算屏幕上裁剪框的宽与“显示在屏幕上的图片”的宽de比值factW;屏幕上裁剪框的高与“显示在屏幕上的图片”的高factH
        float factW = mFloatDrawable.getBounds().width() / (mDrawable.getBounds().width() * 1.0f);
        float factH = mFloatDrawable.getBounds().height() / (mDrawable.getBounds().height() * 1.0f);

        // 从原始图片中裁剪图片
        int x = (int) (sourceBitmap.getWidth() * factX);
        int y = (int) (sourceBitmap.getHeight() * factY);

        int width = (int) (sourceBitmap.getWidth() * factW);
        int height = (int) (sourceBitmap.getHeight() * factH);

        Bitmap ret = sourceBitmap.createBitmap(sourceBitmap, x, y, width, height);

        // Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
        // Config.RGB_565);
        // Canvas canvas = new Canvas(tmpBitmap);
        // mDrawable.draw(canvas);
        //
        // Matrix matrix=new Matrix();
        // float
        // scale=(float)(mDrawableSrc.width())/(float)(mDrawableDst.width());
        // matrix.postScale(scale, scale);
        //
        // // mDrawableSrc 原始图
        // // mDrawbleDst 被缩放过的图
        // // mDrawableFloat 浮动窗口
        // Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left,
        // mDrawableFloat.top,
        // mDrawableFloat.width(), mDrawableFloat.height(), matrix, true);
        // tmpBitmap.recycle();
        // tmpBitmap = null;
        // if (cropWidth < mDrawableFloat.width() && cropHeight <
        // mDrawableFloat.height()) {
        // Bitmap newRet = Bitmap.createScaledBitmap(ret, cropWidth, cropHeight,
        // false);
        // ret.recycle();
        // ret = newRet;
        // }
        return ret;
    }

    /**
     * 预览图片被裁剪后，还需要根据传进来的尺寸再裁剪一次，这个方法就是只获取预览被裁剪之后的图片大小 不在根据具体 尺寸再裁剪
     * 
     * @return
     */
    public Bitmap getCropImage() {
        Bitmap tmpBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(tmpBitmap);
        mDrawable.draw(canvas);

        Matrix matrix = new Matrix();
        float scale = (float) (mDrawableSrc.width()) / (float) (mDrawableDst.width());
        matrix.postScale(scale, scale);

        // mDrawableSrc 原始图
        // mDrawbleDst 被缩放过的图
        // mDrawableFloat 浮动窗口
        Bitmap ret = Bitmap.createBitmap(tmpBitmap, mDrawableFloat.left, mDrawableFloat.top, mDrawableFloat.width(),
                mDrawableFloat.height(), matrix, true);
        tmpBitmap.recycle();
        tmpBitmap = null;
        Bitmap newRet = Bitmap.createScaledBitmap(ret, cropWidth, cropHeight, false);
        ret.recycle();
        ret = newRet;
        return ret;
    }

    public int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOriengenalImagePath(String mImagePath) {
        this.origenalImagePath = mImagePath;
    }
}
