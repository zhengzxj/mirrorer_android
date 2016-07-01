package com.smart.mirrorer.base;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.smart.mirrorer.R;
import com.smart.mirrorer.util.MonitoredUtil;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.view.CropImageView;

import java.io.IOException;
import java.io.OutputStream;


/*
 * 
 * input : mod://www.spriteapp.com_daydayup_clip@imagePath={%s}&cropWidth={%d}&cropHeight={%d}&return-data={%b}
 * 
 * output: imagePath 原图片路径、返回裁剪图片的路径
 *         
 *         return-data   返回裁剪后的图片还是裁剪的文件路径
 *         // 如果 return-data值为ture时
 *         data          返回图片的健
 * 
 */
public class ClipPictureActivity extends BaseActivity {
    private static final String TAG = "ClipPictureActivity";
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    public static final int CLIP_CANCEL_CODE = 250;

    public static final String CROP_WIDTH = "cropWidth";
    public static final String CROP_HEIGHT = "cropHeight";
    public static final String CROP_SOURCE_CAP = "is-sourceCap";
    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final String IMAGE_PATH = "imagePath";
    public static final String RETURN_DATA = "return-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";
    private ContentResolver mContentResolver;
    private final Handler mHandler = new Handler();
    private CropImageView mCropImage;
    private String mImagePath;
    private Uri mSaveUri = null;
    private String mSavePath;
    private Bitmap mBitmap;
    private int cropWidth = 300; // px 用户需要的最终裁剪图片大小
    private int cropHeight = 300;
    private Button btnCancel, btnFinish;
    private boolean isSourceCap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cropimage_layout);
        mContentResolver = getContentResolver();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        initView();
        if (extras != null) {
            // 默认返回一个512x512的图片
            cropWidth = extras.getInt(CROP_WIDTH, 300);
            cropHeight = extras.getInt(CROP_HEIGHT, 300);
            mImagePath = extras.getString(IMAGE_PATH);
            isSourceCap = extras.getBoolean(CROP_SOURCE_CAP, false);
            if (TextUtils.isEmpty(mImagePath)) {
                TipsUtils.showShort(getApplicationContext(), "没有选择图片");
                return;
            }

            // 设置原图路径
            mCropImage.setOriengenalImagePath(mImagePath);
            mBitmap = MonitoredUtil.getBitmap(mContentResolver, mImagePath);

            mSavePath = MonitoredUtil.getSaveImagePath(mImagePath);
            mSaveUri = MonitoredUtil.getImageUri(mSavePath);
            mSaving = false;
        }

        if (mBitmap == null) {
            Log.d(TAG, "finish!!!");
            TipsUtils.showShort(getApplicationContext(), "图片加载失败，不能进行图片裁剪");
            finish();
            return;
        }

        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), mBitmap);
        mCropImage.setDrawable(bitmapDrawable, cropWidth, cropHeight);

        btnCancel.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (mCropImage != null) {
                    try {
                        mCropImage.getSourceCropImage().recycle();
                        mSaving = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                finish();
            }
        });

        btnFinish.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    onSaveClicked();
                } catch (Exception e) {
                    mSaving = false;
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        mCropImage = (CropImageView) findViewById(R.id.cropImg);
    }

    boolean mSaving; // Whether the "save" button is already clicked.

    private void onSaveClicked() throws Exception {
        if (mSaving)
            return;
        mSaving = true;
        Bitmap croppedImage = null;
        if (isSourceCap) {
            croppedImage = mCropImage.getSourceCropImage();
        } else {
            croppedImage = mCropImage.getCropImage();
        }

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && myExtras.getBoolean(RETURN_DATA)) {
            Bundle extras = new Bundle();
            extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
            setResult(RESULT_OK, (new Intent()).setAction(ACTION_INLINE_DATA).putExtras(extras));
            mSaving = false;
            finish();
        } else {
            if (croppedImage == null) {
                mSaving = false;
                return;
            } else {

                final Bitmap b = croppedImage;
                MonitoredUtil.startBackgroundJob(this, "正在保存图片", true,
                        new Runnable() {
                            public void run() {
                                saveOutput(b);
                            }
                        }, mHandler);

            }
        }
    }

    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                Log.e(TAG, "mSaveUri: " + mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
                setResult(RESULT_CANCELED);
                finish();
                mSaving = false;
                return;
            } finally {
                MonitoredUtil.closeSilently(outputStream);
            }

            mSaving = false;
            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            intent.putExtra(IMAGE_PATH, mSavePath);
            setResult(RESULT_OK, intent);
        } else {
            Log.e(TAG, "not defined image url");
        }
        croppedImage.recycle();
        finish();
    }

}
