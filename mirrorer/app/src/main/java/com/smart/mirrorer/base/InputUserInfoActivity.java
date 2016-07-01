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
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.event.LogInOkEvent;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.AppFilePath;
import com.smart.mirrorer.util.BitmapPickUtils;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UploadUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.VerifyNumTimeCount;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.util.mUtil.MyTextUtil;
import com.smart.mirrorer.view.ActionSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/25.
 */
public class InputUserInfoActivity extends BaseActivity implements View.OnClickListener, ActionSheet.ActionSheetListener {
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

    private String mAvarUrl;
    private String mShowLocalUrl;
    private String mNameText;

    private String mUid;
    private String mNick;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_info);

        service = new KeyboardService(this);
        intent = getIntent();

        telphone = intent.getStringExtra("telphone");
        code = intent.getStringExtra("code");
        password = intent.getStringExtra("password");

        initView();

    }

    private void initView() {

        iv_upload_head = (CircleImageView) findViewById(R.id.iv_upload_head);
        iv_upload_head.setOnClickListener(this);
        iv_man = (ImageView)findViewById(R.id.iv_man);
        iv_man.setOnClickListener(this);
        iv_upload_head.setOnClickListener(this);
        iv_woman = (ImageView)findViewById(R.id.iv_women);
        iv_woman.setOnClickListener(this);
        iv_upload_head.setOnClickListener(this);

        TextView tv_regist_done = (TextView)findViewById(R.id.tv_regist_done);
        tv_regist_done.setOnClickListener(this);

        et_nick = (EditText)findViewById(R.id.et_nick);
        MyTextUtil.countLimit(et_nick,14,"名字不能超过7个汉字或14个英文");
        et_company = (EditText)findViewById(R.id.et_company);
        MyTextUtil.countLimit(et_company,28,"公司名称不能超过14个汉字或28个英文");
        et_position = (EditText)findViewById(R.id.et_position);
        MyTextUtil.countLimit(et_position,16,"职位不能超过8个汉字或16个英文");
    }

    private KeyboardService service;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_upload_head:
                service.hideKeyboard(v);
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
        }
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

    private File pictureOut;
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
        }
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
            UploadUtils.uploadFile(BaseApplication.getUploadManager(InputUserInfoActivity.this), new File(mFilePath), mUploadCallBack, token.getResult()
                    , UploadUtils.TYPE_UPLOAD_HEAD, "head_"+System.currentTimeMillis() + ".jpg");
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
        }
    };
    /**
     * 上传文件回调
     */
    private UploadUtils.UploadCallback mUploadCallBack = new UploadUtils.UploadCallback() {
        @Override
        public void onPrepare() {
            showLoadDialog();
        }

        @Override
        public void onSuccess(String localFileUri, String serverFilePath) {
            dismissLoadDialog();
            if (!TextUtils.isEmpty(localFileUri)) {
                mShowLocalUrl = localFileUri;
                BaseApplication.mImageLoader.displayImage(localFileUri, iv_upload_head, BaseApplication.headOptions);
            }
            if (!TextUtils.isEmpty(serverFilePath)) {
                mAvarUrl = serverFilePath;
            }
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
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

    private String mInfoParams;
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
            MirrorSettings settings = BaseApplication.getSettings(InputUserInfoActivity.this);
            settings.APP_UID.setValue(mUid);
            settings.USER_ACCOUNT.setValue(telphone);
            settings.USER_NICK.setValue(mNick);

            Intent intent = new Intent(InputUserInfoActivity.this, MainActivity.class);
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
}
