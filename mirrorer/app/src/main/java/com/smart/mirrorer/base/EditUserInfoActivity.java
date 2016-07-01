package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.bean.LoginInfo;
import com.smart.mirrorer.bean.UploadTokenBean;
import com.smart.mirrorer.db.VideoHistoryProviderService;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.event.LogInOkEvent;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.AppFilePath;
import com.smart.mirrorer.util.BitmapPickUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UploadUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.ActionSheet;
import com.videorecorder.CameraActivity;
import com.videorecorder.RecordVideoActivity;
import com.videorecorder.VideoPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/26.
 */
public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {

    public static final int REQUEST_CODE_VIDEO = 44; // 视频
    private TextView mVolidGetTv;
    private EditText et_nick;
    private EditText et_position;
    private EditText et_company;
    private ImageView iv_man;
    private ImageView iv_woman;
    private Intent intent;
    private String telphone;
    private String code;
    private String password;
    private String company;
    private String sex = "1";
    private String realName;
    private String title;
    private String headImgUrl;
    private String uploadId;
    private CircleImageView iv_upload_head;


    // 图片相关
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放

    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private String mFilePath;
    private String mUploadId;
    private MirrorSettings mSettings;
    private String mAvarUrl;
    private String mNameText;

    private String mUid;
    private String mNick;
    private String mTutorDesc;
    private String videoUrl;
    //    private TextView mUploadTv;
    private ImageView mUploadIv;
//    private ImageView mPlayIv;
    private TextView tv_video_guide;
    private EditText et_first5_money;
    private EditText et_after_5_money;
    private String startPrice;
    private String minutePrice;
    //截取的录制视频一帧图片
    private String mVideoFramePath;
    private EditText et_user_desc;
    private boolean isTutor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MirrorSettings settings = BaseApplication.getSettings(this);
        mUid = settings.APP_UID.getValue();
        service = new KeyboardService(this);
        setContentView(R.layout.activity_edit_user_info);

        mSettings = BaseApplication.getSettings(this);
        isTutor = mSettings.APP_IS_TUTOR_TYPE.getValue();

        initView();
        initData();
        updateUI();
    }

    private TextView tv_commite_date;
    private ImageView iv_luzhi;
    private ImageView iv_back;
    private void initView() {
        if(!isTutor)
        {
            findViewById(R.id.tutor_view1).setVisibility(View.GONE);
            findViewById(R.id.tutor_view2).setVisibility(View.GONE);
            findViewById(R.id.tutor_view3).setVisibility(View.GONE);
            findViewById(R.id.tutor_view4).setVisibility(View.GONE);
            findViewById(R.id.tutor_view5).setVisibility(View.GONE);
        }

        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_luzhi= (ImageView)findViewById(R.id.iv_luzhi);
        iv_luzhi.setOnClickListener(this);
        tv_reupload_video = (TextView)findViewById(R.id.tv_reupload_video);
        tv_reupload_video.setOnClickListener(this);
        et_user_desc = (EditText)findViewById(R.id.et_user_desc);
        tv_commite_date = (TextView)findViewById(R.id.tv_commite_date);
        tv_commite_date.setOnClickListener(this);
        iv_upload_head = (CircleImageView) findViewById(R.id.iv_upload_head);
        iv_upload_head.setOnClickListener(this);
        iv_man = (ImageView)findViewById(R.id.iv_man);
        iv_man.setOnClickListener(this);
        iv_upload_head.setOnClickListener(this);
        iv_woman = (ImageView)findViewById(R.id.iv_women);
        iv_woman.setOnClickListener(this);
        iv_upload_head.setOnClickListener(this);

        et_nick = (EditText)findViewById(R.id.et_nick);
        et_company = (EditText)findViewById(R.id.et_company);
        et_position = (EditText)findViewById(R.id.et_position);

        //收费和视频

        et_first5_money = (EditText)findViewById(R.id.et_first5_money);
        et_after_5_money = (EditText)findViewById(R.id.et_after_5_money);


//        mUploadIv = (ImageView)findViewById(R.id.iv_luzhi);
//        mUploadIv.setOnClickListener(this);

//        mPlayIv = (ImageView)findViewById(R.id.iv_play);
//        mPlayIv.setOnClickListener(this);

        tv_video_guide = (TextView)findViewById(R.id.tv_video_guide);
    }

    private String introduce;
    private TextView tv_reupload_video;
    private void initData()
    {
        intent = getIntent();
        headImgUrl = intent.getStringExtra("headImgUrl");
        realName = intent.getStringExtra("realName");
        company = intent.getStringExtra("company");
        title = intent.getStringExtra("title");
        sex = intent.getStringExtra("sex");
        startPrice = intent.getFloatExtra("startPrice",0.00f)+"";
        minutePrice = intent.getFloatExtra("minutePrice",0.00f)+"";
        videoUrl = intent.getStringExtra("videoUrl");
        introduce = intent.getStringExtra("introduce");
    }
    private void updateUI()
    {
        //头像
        if (!TextUtils.isEmpty(headImgUrl)) {
            BaseApplication.mImageLoader.displayImage(headImgUrl, iv_upload_head, BaseApplication.headOptions);
        }
        //性别
        iv_man.setImageResource("1".equals(sex)?R.drawable.btn_nanshi_se:R.drawable.btn_nanshi);
        iv_woman.setImageResource("2".equals(sex)?R.drawable.btn_se_woman:R.drawable.btn_woman);
        //昵称
        et_nick.setText(realName);
        //公司
        et_company.setText(company);
        //职位
        et_position.setText(title);
        //起步价
        et_first5_money.setText(startPrice);
        //分钟单价
        et_after_5_money.setText(minutePrice);
        //个人简介
        et_user_desc.setText(introduce);
        //视频(现在没封面)
//        videoUrl=null;测试界面用的
        if(TextUtils.isEmpty(videoUrl))
        {
            tv_reupload_video.setText("上传");
            iv_luzhi.setImageResource(R.mipmap.btn_paizhao);
            iv_luzhi.setClickable(false);
        }else
        {
            tv_reupload_video.setText("重新上传");
            iv_luzhi.setImageResource(R.mipmap.by_user_info_shipin);
            iv_luzhi.setClickable(true);
        }
    }



    private VideoHistoryProviderService mVideoHistoryProviderService;

    private VideoHistoryProviderService getHistoryDBService() {

        if (mVideoHistoryProviderService == null) {
            mVideoHistoryProviderService = new VideoHistoryProviderService(getApplicationContext());
        }
        return mVideoHistoryProviderService;
    }

    /**
     * 获取上传凭证回调
     */
    private UploadUtils.UploadTokenCallback mtokenCallback = new UploadUtils.UploadTokenCallback() {
        @Override
        public void onPrepare() {
            showLoadDialog();
        }

        @Override
        public void onSuccess(UploadTokenBean token) {
            dismissLoadDialog();
            if (token == null || token.getResult() == null) {
                TipsUtils.showShort(getApplicationContext(), "文件上传凭证获取失败");
                return;
            }

            mUploadId = token.getResult().getUploadId();

            L.i("上传头像 开始");
            UploadUtils.uploadFile(BaseApplication.getUploadManager(EditUserInfoActivity.this), new File(mFilePath), mUploadCallBack, token.getResult()
                    , UploadUtils.TYPE_UPLOAD_HEAD, "head_"+System.currentTimeMillis() + ".jpg");

        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
        }
    };
    /**
     * 上传视频文件回调
     */
    private UploadUtils.UploadCallback mUploadVideoCallBack = new UploadUtils.UploadCallback() {
        @Override
        public void onPrepare() {
            L.i("mUploadVideoCallBack 上传视频 onPrepare");
            showLoadDialog();
        }

        @Override
        public void onSuccess(String localFileUri, String serverFilePath) {
            L.i("mUploadVideoCallBack 上传视频 onSuccess");
            dismissLoadDialog();

            Log.e("lzm", "video_serverPath=" + serverFilePath);
            if (!TextUtils.isEmpty(serverFilePath)) {
                videoUrl = serverFilePath;
                String framePath = new File(mVideoFramePath).getAbsolutePath();
                iv_luzhi.setClickable(true);
//                tv_video_guide.setVisibility(View.INVISIBLE);
                tv_reupload_video.setText("重新上传");
                iv_luzhi.setImageResource(R.mipmap.by_user_info_shipin);
//                BaseApplication.mImageLoader.displayImage("file://"+framePath, iv_luzhi, BaseApplication.options);
//                mPlayIv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFail(String error) {
            L.i("mUploadVideoCallBack 上传视频 onFail");
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NONE)
            return;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            // 设置文件保存路径这里放在跟目录下
            if (pictureOut != null && pictureOut.exists()) {
                BitmapPickUtils.startPhotoClip(this, pictureOut.getAbsolutePath(), PHOTORESOULT);
            }
        } else if (requestCode == PHOTOZOOM && data != null) {
            // 读取相册缩放图片
            String bitmapPath = BitmapPickUtils.pickResultFromGalleryImage(this, data);
            if (!TextUtils.isEmpty(bitmapPath)) {
                BitmapPickUtils.startPhotoClip(this, bitmapPath, PHOTORESOULT);
            }
        } else if (requestCode == PHOTORESOULT && data != null) {
            mFilePath = data.getExtras().getString("imagePath");

            Log.e("lzm", "path=" + mFilePath);
            if (TextUtils.isEmpty(mFilePath)) {
                return;
            }

            //上传头像
            UploadUtils.getUploadToken(mtokenCallback, UploadUtils.TYPE_UPLOAD_HEAD);
        } else if (requestCode == REQUEST_CODE_VIDEO && data != null) {
            Bundle extras = data.getExtras();
            videoUrl = extras.getString(CameraActivity.EXTRAS_VIDEO_PATH);
            mVideoFramePath = extras.getString(CameraActivity.EXTRAS_FIRST_FRAME_PATH);

            Log.e("lzm", "videoPath=" + videoUrl + "___framePath=" + mVideoFramePath);

            if (!TextUtils.isEmpty(videoUrl) && !TextUtils.isEmpty(mVideoFramePath)) {
                UploadUtils.getUploadToken(mTokenVideoCallback, UploadUtils.TYPE_UPLOAD_DESC_VIDEO, mUid);
            }

        }


    }
    private UploadUtils.UploadTokenCallback mTokenVideoCallback = new UploadUtils.UploadTokenCallback() {
        @Override
        public void onPrepare() {
            L.i("mTokenVideoCallback 上传视频 onPrepare");
            showLoadDialog();
        }

        @Override
        public void onSuccess(UploadTokenBean token) {
            dismissLoadDialog();
            if (token == null || token.getResult() == null) {
                TipsUtils.showShort(getApplicationContext(), "文件上传凭证获取失败");
                return;
            }
            L.i("mTokenVideoCallback 上传视频 开始");
            UploadUtils.uploadFile(BaseApplication.getUploadManager(EditUserInfoActivity.this), new File(videoUrl), mUploadVideoCallBack, token.getResult()
                    , UploadUtils.TYPE_UPLOAD_DESC_VIDEO, System.currentTimeMillis() + ".mp4");
        }

        @Override
        public void onFail(String error) {
            L.i("mTokenVideoCallback 上传视频 onFail");
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };
    /**
     * 上传文件回调
     */
    private String serverHeadUrl;
    private UploadUtils.UploadCallback mUploadCallBack = new UploadUtils.UploadCallback() {
        @Override
        public void onPrepare() {
            L.i("上传头像 onPrepare");
            showLoadDialog();
        }

        @Override
        public void onSuccess(String localFileUri, String serverFilePath) {
            L.i("上传头像 onSuccess");
            dismissLoadDialog();
            if (!TextUtils.isEmpty(localFileUri)) {
                headImgUrl = localFileUri;
                serverHeadUrl = serverFilePath;
                BaseApplication.mImageLoader.displayImage(localFileUri, iv_upload_head, BaseApplication.headOptions);
            }
            if (!TextUtils.isEmpty(serverFilePath)) {
                mAvarUrl = serverFilePath;
            }
        }

        @Override
        public void onFail(String error) {
            L.i("上传头像 onFail");
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };
    private void canRequestRegist()
    {
        realName = et_nick.getText().toString();
        company = et_company.getText().toString();
        title = et_position.getText().toString();
        if(TextUtils.isEmpty(realName)||TextUtils.isEmpty(company)|| TextUtils.isEmpty(title))
        {
            TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"请填完善个人资料");
            return;
        }
        if (TextUtils.isEmpty(mAvarUrl)) {
            TipsUtils.showShort(getApplicationContext(), "请上传头像");
            return;
        }

        registerApp();
    }
    /**
     * 注册请求
     */
    private void registerApp() {

        String tag_json_obj = "json_obj_register_req";
        showLoadDialog();

        /**
         * 1)telphone(手机号)
         2)password(密码)
         3)code(短信验证码)
         4)company(目前所在公司)
         5)sex(性别，1-男，2-女)
         6)realName(真实姓名)
         7)title(目前职位)
         8)headImgUrl(上传头像成功后返回的url地址)
         9)uploadId(上传头像成功后返回的uploadId)
         */
        JSONObject paramObj = null;
        try {
            paramObj = new JSONObject();
            paramObj.put("telphone", telphone);
            paramObj.put("password", password);
            paramObj.put("code", code);
            paramObj.put("company", company);
            paramObj.put("title", title);
            paramObj.put("sex", sex);
            paramObj.put("realName", realName);
            paramObj.put("headImgUrl", mAvarUrl);
            paramObj.put("uploadId", mUploadId);
//            paramObj.put("rid",BaseApplication.getInstance().rid);
            L.i("lzm,regist paramObj = "+paramObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST,
                Urls.URL_REGISTER, paramObj, mRegisterCallBack
                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
    private GsonCallbackListener<LoginInfo> mRegisterCallBack = new GsonCallbackListener<LoginInfo>() {

        @Override
        public void onResultSuccess(LoginInfo loginInfo) {
            super.onResultSuccess(loginInfo);
            dismissLoadDialog();
            if (loginInfo == null) {
                TipsUtils.showShort(getApplicationContext(), "注册失败");
                return;
            }

            LoginInfo.ResultBean loginResult = loginInfo.getResult();
            if (loginResult == null) {
                TipsUtils.showShort(getApplicationContext(), "注册异常，没有返回uid");
                return;
            }
            mUid = loginResult.getUid();
            mNick = loginResult.getNick();
            if (TextUtils.isEmpty(mUid)) {
                TipsUtils.showShort(getApplicationContext(), "注册异常，没有返回uid");
                return;
            }

            BusProvider.getInstance().post(new FinishActivityEvent());
            BusProvider.getInstance().post(new LogInOkEvent());

            JPushInterface.setAlias(getApplicationContext(), mUid, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.e("lzm", "jupush_i="+i+"__s="+s);
                }
            });

            TipsUtils.showShort(getApplicationContext(), "注册成功");
            MirrorSettings settings = BaseApplication.getSettings(EditUserInfoActivity.this);
            settings.APP_UID.setValue(mUid);
            settings.USER_ACCOUNT.setValue(telphone);
            settings.USER_NICK.setValue(mNick);

            Intent intent = new Intent(EditUserInfoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    /**
     * 仿ios7风格弹框
     *
     * @param str
     */
    public void showActionSheet(String... str) {
        setTheme(R.style.ActionSheetStyleIOS7);
        ActionSheet.Builder builder = ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("取消").setCancelableOnTouchOutside(true);
        builder.setOtherButtonTitles(str);
        builder.setListener(this).show();
    }
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }
    private File pictureOut;
    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                pictureOut = AppFilePath.getPictureFile(BitmapPickUtils.createCameraPictureName());
                BitmapPickUtils.startSystemCamera(this, PHOTOHRAPH, pictureOut);
                break;
            case 1:
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent1, PHOTOZOOM);
                break;
        }
    }
    private KeyboardService service;
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_commite_date:
                initCurrentData();
                commite();
                break;
            case R.id.iv_upload_head:
                service.hideKeyboard(view);
                showActionSheet("拍照", "从相册选择");
                break;
            case R.id.iv_man:
                chooseSex(sex,R.id.iv_man);
                break;
            case R.id.iv_women:
                chooseSex(sex,R.id.iv_women);
                break;
            case R.id.tv_regist_done:
                canRequestRegist();
                break;
            case R.id.iv_four_back:
                getSupportFragmentManager().popBackStack();
                break;
            case R.id.tv_commit:
//                startPrice = et_first5_money.getText().toString();
//                minutePrice = et_after_5_money.getText().toString();
//                if(TextUtils.isEmpty(startPrice)||TextUtils.isEmpty(minutePrice))
//                {
//                    TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                    return;
//                }
//
//                if(TextUtils.isEmpty(videoUrl)) {
//                    TipsUtils.showShort(getApplicationContext(), "请上传视频介绍");
//                    return;
//                }
//                submitTutorInfo();
                break;
            case R.id.tv_reupload_video:
                recordVideo();
                TipsUtils.showShort(getApplicationContext(), "录制视频");
                break;
            case R.id.iv_luzhi:
                if(!TextUtils.isEmpty(videoUrl)) {
                    Intent playIntent = new Intent();
                    playIntent.setClass(EditUserInfoActivity.this, VideoPlayerActivity.class);
                    playIntent.putExtra(VideoPlayerActivity.VIDEO_URI, videoUrl);
                    startActivity(playIntent);
                }
                TipsUtils.showShort(getApplicationContext(), "播放视频");
                break;

        }
    }
    private void initCurrentData()
    {
        company = et_company.getText().toString();
        title = et_position.getText().toString();
        realName = et_nick.getText().toString();
        startPrice = et_first5_money.getText().toString();
        minutePrice = et_after_5_money.getText().toString();
        introduce = et_user_desc.getText().toString();
    }
    private void commite()
    {
        if(TextUtils.isEmpty(company)||TextUtils.isEmpty(title)||TextUtils.isEmpty(realName)||TextUtils.isEmpty(headImgUrl))
        {
            TipsUtils.showShort(getApplicationContext(),"请完善信息");
            return;
        }
        if(isTutor)
        {
            if (TextUtils.isEmpty(startPrice)||TextUtils.isEmpty(minutePrice))
            {
                TipsUtils.showShort(getApplicationContext(),"请完善价格信息");
                return;
            }
        }
        commiteNet();
    }
    private void recordVideo() {
        Intent vIntent = new Intent(this, RecordVideoActivity.class);
        startActivityForResult(vIntent, REQUEST_CODE_VIDEO);
    }
    private void commiteNet() {
        showLoadDialog();
        String tag_json_obj = "json_obj_user_edit";
        JSONObject paramObj = getParamsObj();

        Log.e("lzm", "tutor_parems=" + paramObj.toString());

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_USER_EDIT, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    TipsUtils.showShort(getApplicationContext(),"修改成功");
                    finish();
                } else {
                    String netErrorText = GloabalRequestUtil.getNetErrorMsg(response);
                    TipsUtils.showShort(getApplicationContext(), netErrorText);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    /**
     * 获取参数
     *
     * @return
     */
    private JSONObject getParamsObj() {
        /**
         * 1)company(目前所在公司)
         2)sex(性别，1-男，2-女)
         3)title(目前职位)
         4)headImgUrl(上传头像成功后返回的url地址)
         5)videoUrl(自我介绍视频上传后返回的地址)
         6)introduce(自我介绍文本)
         */
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("company", company);
            paramObj.put("sex", sex);
            paramObj.put("title", title);
            paramObj.put("realName", realName);
            paramObj.put("headImgUrl", serverHeadUrl);
            if(!TextUtils.isEmpty(videoUrl))
            paramObj.put("videoUrl", videoUrl);
            if(!TextUtils.isEmpty(introduce))
            paramObj.put("introduce", introduce);
            if (isTutor)
            {
                paramObj.put("startPrice", startPrice);
                paramObj.put("minutePrice", minutePrice);
            }
            L.i("当前页面个人信息:"+paramObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramObj;
    }
    private void chooseSex(String sex,int res)
    {
        L.i("改性别 sex = "+sex);
        if("1".equals(sex)&&R.id.iv_man==res)
        {
            L.i("改性别 点男 原sex = 1");
            return;
        }
        if("2".equals(sex)&&R.id.iv_women==res)
        {
            L.i("改性别 点女 原sex = 2");
            return;
        }
        if("1".equals(sex))
        {
            this.sex = "2";
            iv_man.setImageResource(R.drawable.btn_nanshi);
            iv_woman.setImageResource(R.drawable.btn_se_woman);
            L.i("改性别 原sex = 1 改成 2");
        }else
        {
            this.sex = "1";
            iv_man.setImageResource(R.drawable.btn_nanshi_se);
            iv_woman.setImageResource(R.drawable.btn_woman);
            L.i("改性别 原sex = 2 改成 1");
        }
    }
}
