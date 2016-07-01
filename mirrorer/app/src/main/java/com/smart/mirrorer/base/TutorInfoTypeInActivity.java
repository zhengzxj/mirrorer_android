package com.smart.mirrorer.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.TutorFourthEduAdapter;
import com.smart.mirrorer.adapter.TutorThirdJobAdapter;
import com.smart.mirrorer.bean.TestTutorInfoBean;
import com.smart.mirrorer.bean.UploadTokenBean;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.AppFilePath;
import com.smart.mirrorer.util.BitmapPickUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UploadUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.util.mUtil.MyTextUtil;
import com.smart.mirrorer.view.ActionSheet;
import com.videorecorder.CameraActivity;
import com.videorecorder.RecordVideoActivity;
import com.videorecorder.VideoPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzm on 16/4/16.
 */
public class TutorInfoTypeInActivity extends BaseActivity implements ActionSheet.ActionSheetListener {
    private KeyboardService service;

    public static final int REQUEST_CODE_VIDEO = 44; // 视频

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_typein_layout);

        service = new KeyboardService(this);
        MirrorSettings settings = BaseApplication.getSettings(this);
        mUid = settings.APP_UID.getValue();

        if (savedInstanceState == null) {
            Fragment newFragment = new FirstStepFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.info_typein_sub_frame_layout, newFragment).commit();
        }
//        else {
//            mGuideData = savedInstanceState.getParcelable(KEY_DATA);
//        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(KEY_DATA, mGuideData);
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
                BitmapPickUtils.startSystemCamera(this, BaseInfoTypeInActivity.PHOTOHRAPH, pictureOut);
                break;
            case 1:
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseInfoTypeInActivity.IMAGE_UNSPECIFIED);
                startActivityForResult(intent1, BaseInfoTypeInActivity.PHOTOZOOM);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("lzm", "requestCode=" + requestCode + "__data=" + data + "--resultCode=" + resultCode);
        if (resultCode == BaseInfoTypeInActivity.NONE)
            return;
        // 拍照
        if (requestCode == BaseInfoTypeInActivity.PHOTOHRAPH) {
            // 设置文件保存路径这里放在跟目录下
            if (pictureOut != null && pictureOut.exists()) {
                BitmapPickUtils.startPhotoClip(this, pictureOut.getAbsolutePath(), BaseInfoTypeInActivity.PHOTORESOULT);
            }
        } else if (requestCode == BaseInfoTypeInActivity.PHOTOZOOM && data != null) {
            // 读取相册缩放图片
            String bitmapPath = BitmapPickUtils.pickResultFromGalleryImage(this, data);
            if (!TextUtils.isEmpty(bitmapPath)) {
                BitmapPickUtils.startPhotoClip(this, bitmapPath, BaseInfoTypeInActivity.PHOTORESOULT);
            }
        } else if (requestCode == BaseInfoTypeInActivity.PHOTORESOULT && data != null) {
            mFilePath = data.getExtras().getString("imagePath");

            Log.e("lzm", "path=" + mFilePath);
            if (TextUtils.isEmpty(mFilePath)) {
                return;
            }

            UploadUtils.getUploadToken(mTokenCallback, UploadUtils.TYPE_UPLOAD_CARD, mUid);
//            File pathFile = new File(path);

//            String localFileUri = "file://" + pathFile.getAbsolutePath();
//
//            BaseApplication.mImageLoader.displayImage(localFileUri, mCardIv, BaseApplication.headOptions);
            //上传
        } else if (requestCode == REQUEST_CODE_VIDEO && data != null) {
            Bundle extras = data.getExtras();
            mVideoFilePath = extras.getString(CameraActivity.EXTRAS_VIDEO_PATH);
            mVideoFramePath = extras.getString(CameraActivity.EXTRAS_FIRST_FRAME_PATH);

            Log.e("lzm", "videoPath=" + mVideoFilePath + "___framePath=" + mVideoFramePath);

            if (!TextUtils.isEmpty(mVideoFilePath) && !TextUtils.isEmpty(mVideoFramePath)) {
                UploadUtils.getUploadToken(mTokenVideoCallback, UploadUtils.TYPE_UPLOAD_DESC_VIDEO, mUid);
            }

        }

    }

    //截取的录制视频一帧图片
    private String mVideoFramePath;

    private String mFilePath;

    private UploadUtils.UploadTokenCallback mTokenCallback = new UploadUtils.UploadTokenCallback() {
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

            UploadUtils.uploadFile(BaseApplication.getUploadManager(TutorInfoTypeInActivity.this), new File(mFilePath), mUploadCallBack, token.getResult()
                    , UploadUtils.TYPE_UPLOAD_CARD, "card_"+System.currentTimeMillis() + ".jpg");
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
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

            Log.e("lzm", "card_serverPath=" + serverFilePath);
            if (!TextUtils.isEmpty(localFileUri)) {
                mTempLocalCardUrl = localFileUri;
                BaseApplication.mImageLoader.displayImage(localFileUri, mCardIv, BaseApplication.headOptions);
            }
            if (!TextUtils.isEmpty(serverFilePath)) {
                mCardUrl = serverFilePath;
            }
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };

    private String mStartPirce;
    private String mMinutePirce;

    @SuppressLint("ValidFragment")
    private class FirstStepFragment extends Fragment implements View.OnClickListener {

        private EditText startEdit;
        private EditText minuteEdit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutor_typein_first_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView iv_first_back = (ImageView) getView().findViewById(R.id.iv_first_back);
            iv_first_back.setOnClickListener(this);
            TextView tv_shenqing = (TextView) getView().findViewById(R.id.tv_shenqing);
            tv_shenqing.setOnClickListener(this);
//
//            startEdit = (EditText) getView().findViewById(R.id.tutor_first_start_edit);
//            minuteEdit = (EditText) getView().findViewById(R.id.tutor_first_minute_edit);
//            startEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
//            minuteEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);


        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_shenqing:
//                    service.hideKeyboard(v);
//                    mStartPirce = startEdit.getText().toString();
//                    mMinutePirce = minuteEdit.getText().toString();
//                    if (TextUtils.isEmpty(mStartPirce) || TextUtils.isEmpty(mMinutePirce)) {
//                        TipsUtils.showShort(getApplicationContext(), "请完善价格信息");
//                        return;
//                    }
                    toSecondSetpFragment();
                    break;
                case R.id.iv_first_back:
                    finish();
                    break;
            }
        }


    }

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


    private ImageView mCardIv;
    private String mCardUrl;
    private String mTempLocalCardUrl;

    private String mNowPosition;
    private String mNowCompany;
    private String mJobYears;

    @SuppressLint("ValidFragment")
    private class TowSetpFragment extends Fragment implements View.OnClickListener {

        private EditText nowPositionEdit;
        private EditText nowCompanyEdit;
        private EditText jobYearsEdit;
        private EditText et_user_desc;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutor_typein_second_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView iv_second_back = (ImageView) getView().findViewById(R.id.iv_second_back);
            iv_second_back.setOnClickListener(this);

            mCardIv = (ImageView)getView().findViewById(R.id.iv_paizhao);
            mCardIv.setOnClickListener(this);
            et_user_desc = (EditText)getView().findViewById(R.id.et_user_desc);
            et_user_desc.setOnClickListener(this);
            MyTextUtil.countLimit(et_user_desc,200,"个人简介不能超过200个字");
            TextView tv_second_next = (TextView) getView().findViewById(R.id.tv_second_next);
            tv_second_next.setOnClickListener(this);
//            ImageView nextIv = (ImageView) getView().findViewById(R.id.tutor_typein_second_next_iv);
//            nextIv.setOnClickListener(this);
//
//            nowPositionEdit = (EditText) getView().findViewById(R.id.tutor_second_now_position);
//            nowCompanyEdit = (EditText) getView().findViewById(R.id.tutor_second_now_company);
//            jobYearsEdit = (EditText) getView().findViewById(R.id.tutor_second_job_years);
//
////            TextView uploadTv = (TextView) getView().findViewById(R.id.tutor_second_upload_card_tv);
////            uploadTv.setOnClickListener(this);
//
//            mCardIv = (ImageView) getView().findViewById(R.id.tutor_second_upload_card_iv);
//            mCardIv.setOnClickListener(this);
//            if(!TextUtils.isEmpty(mTempLocalCardUrl)) {
//                BaseApplication.mImageLoader.displayImage(mTempLocalCardUrl, mCardIv, BaseApplication.headOptions);
//            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_second_back:
                    service.hideKeyboard(v);
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.iv_paizhao:
                    service.hideKeyboard(v);
                    showActionSheet("拍照", "从相册选择");
                    break;
                case R.id.tv_second_next:
                    service.hideKeyboard(v);
                    mTutorDesc = et_user_desc.getText().toString();
                    if (TextUtils.isEmpty(mTutorDesc)) {
                        TipsUtils.showShort(getApplicationContext(), "请输入个人描述信息");
                        return;
                    }
                    if(TextUtils.isEmpty(mCardUrl)) {
                        TipsUtils.showShort(getApplicationContext(), "请上传工卡或者有关职位信息的照片");
                        return;
                    }
                    toThirdSetpFragment();
                    break;
//                case R.id.tutor_typein_second_next_iv:
//                    service.hideKeyboard(v);
//                    mNowPosition = nowPositionEdit.getText().toString();
//                    mNowCompany = nowCompanyEdit.getText().toString();
//                    mJobYears = jobYearsEdit.getText().toString();
//                    if (TextUtils.isEmpty(mNowPosition) || TextUtils.isEmpty(mNowCompany) || TextUtils.isEmpty(mJobYears)) {
//                        TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                        return;
//                    }
//
//                    if(TextUtils.isEmpty(mCardUrl)) {
//                        TipsUtils.showShort(getApplicationContext(), "请上传工卡或者有关职位信息的照片");
//                        return;
//                    }
//
//                    toThirdSetpFragment();
//                    break;
//                case R.id.tutor_second_upload_card_iv:
//                    service.hideKeyboard(v);
//                    showActionSheet("拍照", "从相册选择");
//                    break;
            }
        }


    }

    private List<TestTutorInfoBean> testThreeList = new ArrayList<>();

    private String lastCompany;
    private String lastPosition;
    private String startYear;
    private String endYear;
    @SuppressLint("ValidFragment")
    private class ThreeSetpFragment extends Fragment implements View.OnClickListener {

//        private String startYear = "1989";
//        private String endYear = "2001";

        private EditText jobEdit;
        private EditText companyEdit;

        private TutorThirdJobAdapter mAdapter;
        private EditText et_company;
        private EditText et_start_year;
        private EditText et_position;
        private EditText et_end_year;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutor_typein_third_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView iv_third_back = (ImageView) getView().findViewById(R.id.iv_third_back);
            iv_third_back.setOnClickListener(this);
            TextView tv_third_next = (TextView) getView().findViewById(R.id.tv_third_next);
            tv_third_next.setOnClickListener(this);
            et_company = (EditText) getView().findViewById(R.id.et_company);
            MyTextUtil.countLimit(et_company,28,"公司名称不能超过14个汉字或28个英文");
            et_position = (EditText) getView().findViewById(R.id.et_position);
            MyTextUtil.countLimit(et_company,16,"职位不能超过8个汉字或16个英文");
            et_start_year = (EditText) getView().findViewById(R.id.et_start_year);
            et_end_year = (EditText) getView().findViewById(R.id.et_end_year);
            MyTextUtil.countLimit(et_start_year,4,"年份不能超过4位数字");
            MyTextUtil.countLimit(et_end_year,4,"年份不能超过4位数字");
//
//            ImageView nextIv = (ImageView) getView().findViewById(R.id.tutor_typein_third_next_iv);
//            nextIv.setOnClickListener(this);
//
//            ImageView addIv = (ImageView) getView().findViewById(R.id.tutor_third_job_add_iv);
//            addIv.setOnClickListener(this);
//
//            WheelView startWheelView = (WheelView) getView().findViewById(R.id.tutor_third_start_selview);
//            startWheelView.setOffset(1);
//            startWheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.years)));
//            startWheelView.setSeletion(37);
//            startWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//                @Override
//                public void onSelected(int selectedIndex, String item) {
//                    startYear = item.substring(0, item.length() - 1);
//                }
//            });
//            WheelView endWheelView = (WheelView) getView().findViewById(R.id.tutor_third_end_selview);
//            endWheelView.setOffset(1);
//            endWheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.years)));
//            endWheelView.setSeletion(38);
//            endWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//                @Override
//                public void onSelected(int selectedIndex, String item) {
//                    endYear = item.substring(0, item.length() - 1);
//                }
//            });
//
//            jobEdit = (EditText) getView().findViewById(R.id.tutor_third_job_edit);
//            companyEdit = (EditText) getView().findViewById(R.id.tutor_third_company_edit);
//
//            RecyclerView jobRecyclerView = (RecyclerView) getView().findViewById(R.id.tutor_third_addjob_listview);
//            jobRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            mAdapter = new TutorThirdJobAdapter();
//            jobRecyclerView.setAdapter(mAdapter);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_third_back:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.tv_third_next:
                    lastCompany = et_company.getText().toString();
                    lastPosition = et_position.getText().toString();
                    startYear = et_start_year.getText().toString();
                    endYear = et_end_year.getText().toString();
                    if (TextUtils.isEmpty(lastCompany) || TextUtils.isEmpty(lastPosition)|| TextUtils.isEmpty(startYear)|| TextUtils.isEmpty(endYear)) {
                            TipsUtils.showShort(getApplicationContext(), "请完善信息");
                            return;
                        }
                    toFourthSetpFragment();
                    break;
//                case R.id.tutor_typein_third_next_iv:
//                    if (testThreeList.isEmpty()) {
//                        String jobText = jobEdit.getText().toString();
//                        String companyText = companyEdit.getText().toString();
//                        if (TextUtils.isEmpty(jobText) || TextUtils.isEmpty(companyText)) {
//                            TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                            return;
//                        }
//
//                        jobEdit.setText("");
//                        companyEdit.setText("");
//
//                        TestTutorInfoBean testBean = new TestTutorInfoBean();
//                        testBean.years = startYear + " - " + endYear;
//                        testBean.starYear = startYear;
//                        testBean.endYear = endYear;
//                        testBean.companyOrEdu = companyText;
//                        testBean.jobOrMajor = jobText;
//                        testThreeList.add(testBean);
//                    }
//
//                    toFourthSetpFragment();
//                    break;
//                case R.id.tutor_third_job_add_iv:
//                    String jobText = jobEdit.getText().toString();
//                    String companyText = companyEdit.getText().toString();
//                    if (TextUtils.isEmpty(jobText) || TextUtils.isEmpty(companyText)) {
//                        TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                        return;
//                    }
//
//                    jobEdit.setText("");
//                    companyEdit.setText("");
//
//                    TestTutorInfoBean testBean = new TestTutorInfoBean();
//                    testBean.years = startYear + " - " + endYear;
//                    testBean.starYear = startYear;
//                    testBean.endYear = endYear;
//                    testBean.companyOrEdu = companyText;
//                    testBean.jobOrMajor = jobText;
//                    testThreeList.add(testBean);
//                    mAdapter.setListData(testThreeList);
//                    break;
            }
        }


    }

    private List<TestTutorInfoBean> testFourList = new ArrayList<>();

    private String first5Money;
    private String after5PerMoney;
    @SuppressLint("ValidFragment")
    private class FourSetpFragment extends Fragment implements View.OnClickListener {

        private String startYear = "2001";
        private String endYear = "2005";

        private EditText majorEdit;
        private EditText schoolEdit;
        private TextView tv_commit;
        private TutorFourthEduAdapter mAdapter;
        private EditText et_first5_money;
        private EditText et_after_5_money;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutor_typein_fourth_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView iv_four_back = (ImageView) getView().findViewById(R.id.iv_four_back);
            iv_four_back.setOnClickListener(this);

            TextView tv_commit = (TextView)getView().findViewById(R.id.tv_commit);
            tv_commit.setOnClickListener(this);
            et_first5_money = (EditText)getView().findViewById(R.id.et_first5_money);
            et_after_5_money = (EditText)getView().findViewById(R.id.et_after_5_money);


            mUploadIv = (ImageView) getView().findViewById(R.id.iv_luzhi);
            mUploadIv.setOnClickListener(this);

            mPlayIv = (ImageView) getView().findViewById(R.id.iv_play);
            mPlayIv.setOnClickListener(this);

            tv_video_guide = (TextView)findViewById(R.id.tv_video_guide);
//            ImageView nextIv = (ImageView) getView().findViewById(R.id.tutor_typein_fourth_next_iv);
//            nextIv.setOnClickListener(this);
//
//            ImageView addIv = (ImageView) getView().findViewById(R.id.tutor_fourth_edu_add_iv);
//            addIv.setOnClickListener(this);
//
//            WheelView startWheelView = (WheelView) getView().findViewById(R.id.tutor_fourth_start_selview);
//            startWheelView.setOffset(1);
//            startWheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.years)));
//            startWheelView.setSeletion(37);
//            startWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//                @Override
//                public void onSelected(int selectedIndex, String item) {
//                    startYear = item.substring(0, item.length() - 1);
//                }
//            });
//            WheelView endWheelView = (WheelView) getView().findViewById(R.id.tutor_fourth_end_selview);
//            endWheelView.setOffset(1);
//            endWheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.years)));
//            endWheelView.setSeletion(38);
//            endWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
//                @Override
//                public void onSelected(int selectedIndex, String item) {
//                    endYear = item.substring(0, item.length() - 1);
//                }
//            });
//
//            majorEdit = (EditText) getView().findViewById(R.id.tutor_fourth_major_edit);
//            schoolEdit = (EditText) getView().findViewById(R.id.tutor_fourth_school_edit);
//
//            RecyclerView eduRecyclerView = (RecyclerView) getView().findViewById(R.id.tutor_fourth_edu_listview);
//            eduRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            mAdapter = new TutorFourthEduAdapter();
//            eduRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_four_back:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.tv_commit:
                    /**
                     * et_first5_money = (EditText)getView().findViewById(R.id.et_first5_money);
                     et_after_5_money = (EditText)getView().findViewById(R.id.et_after_5_money);

                     private String first5Money;
                     private String after5PerMoney;
                     */
                    first5Money = et_first5_money.getText().toString();
                    after5PerMoney = et_after_5_money.getText().toString();
                    if(TextUtils.isEmpty(first5Money)||TextUtils.isEmpty(after5PerMoney))
                    {
                        TipsUtils.showShort(getApplicationContext(), "请完善信息");
                        return;
                    }else if(Float.parseFloat(first5Money)>66.66)
                    {
                        TipsUtils.showShort(getApplicationContext(), "前5分钟最高定价为66.66元");
                        return;
                    }else if(Float.parseFloat(after5PerMoney)>10.00)
                    {
                        TipsUtils.showShort(getApplicationContext(), "5分钟后每分钟定价最高为10.00元");
                        return;
                    }

                    if(TextUtils.isEmpty(mTutorVideoUrl)) {
                        TipsUtils.showShort(getApplicationContext(), "请上传视频介绍");
                        return;
                    }
//                    if (testFourList.isEmpty()) {
//                        String majorText = majorEdit.getText().toString();
//                        String schoolText = schoolEdit.getText().toString();
//                        if (TextUtils.isEmpty(majorText) || TextUtils.isEmpty(schoolText)) {
//                            TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                            return;
//                        }
//
//                        majorEdit.setText("");
//                        schoolEdit.setText("");
//
//                        TestTutorInfoBean testBean = new TestTutorInfoBean();
//                        testBean.years = startYear + " - " + endYear;
//                        testBean.starYear = startYear;
//                        testBean.endYear = endYear;
//                        testBean.companyOrEdu = schoolText;
//                        testBean.jobOrMajor = majorText;
//                        testFourList.add(testBean);
//                    }
                    submitTutorInfo();
                    break;
                case R.id.iv_luzhi:
                    recordVideo();
                    TipsUtils.showShort(getApplicationContext(), "录制视频");
                    break;
                case R.id.iv_play:
                    if(!TextUtils.isEmpty(mVideoFilePath)) {
                        Intent playIntent = new Intent();
                        playIntent.setClass(TutorInfoTypeInActivity.this, VideoPlayerActivity.class);
                        playIntent.putExtra(VideoPlayerActivity.VIDEO_URI, mVideoFilePath);
                        startActivity(playIntent);
                    }
                    TipsUtils.showShort(getApplicationContext(), "播放视频");
                    break;

//                case R.id.tutor_fourth_edu_add_iv:
//                    String majorText = majorEdit.getText().toString();
//                    String schoolText = schoolEdit.getText().toString();
//                    if (TextUtils.isEmpty(majorText) || TextUtils.isEmpty(schoolText)) {
//                        TipsUtils.showShort(getApplicationContext(), "请完善信息");
//                        return;
//                    }
//
//                    majorEdit.setText("");
//                    schoolEdit.setText("");
//
//                    TestTutorInfoBean testBean = new TestTutorInfoBean();
//                    testBean.years = startYear + " - " + endYear;
//                    testBean.starYear = startYear;
//                    testBean.endYear = endYear;
//                    testBean.companyOrEdu = schoolText;
//                    testBean.jobOrMajor = majorText;
//                    testFourList.add(testBean);
//                    mAdapter.setListData(testFourList);
//                    break;
            }
        }
    }

    private String mVideoFilePath;

    private UploadUtils.UploadTokenCallback mTokenVideoCallback = new UploadUtils.UploadTokenCallback() {
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

            UploadUtils.uploadFile(BaseApplication.getUploadManager(TutorInfoTypeInActivity.this), new File(mVideoFilePath), mUploadVideoCallBack, token.getResult()
                    , UploadUtils.TYPE_UPLOAD_DESC_VIDEO, System.currentTimeMillis() + ".mp4");
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };

    /**
     * 上传视频文件回调
     */
    private UploadUtils.UploadCallback mUploadVideoCallBack = new UploadUtils.UploadCallback() {
        @Override
        public void onPrepare() {
            showLoadDialog();
        }

        @Override
        public void onSuccess(String localFileUri, String serverFilePath) {
            dismissLoadDialog();

            Log.e("lzm", "video_serverPath=" + serverFilePath);
            if (!TextUtils.isEmpty(serverFilePath)) {
                mTutorVideoUrl = serverFilePath;
                String framePath = new File(mVideoFramePath).getAbsolutePath();
                mUploadIv.setClickable(false);
                tv_video_guide.setVisibility(View.INVISIBLE);
                BaseApplication.mImageLoader.displayImage("file://"+framePath, mUploadIv, BaseApplication.options);
                mPlayIv.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFail(String error) {
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), error);
        }
    };

    private String mTutorDesc;
    private String mTutorVideoUrl;
//    private TextView mUploadTv;
    private ImageView mUploadIv;
    private ImageView mPlayIv;
    private TextView tv_video_guide;

    @SuppressLint("ValidFragment")
    private class FivSetpFragment extends Fragment implements View.OnClickListener {

        private EditText mDescEdit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.tutor_typein_five_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView iv_five_back = (ImageView) getView().findViewById(R.id.iv_five_back);
            iv_five_back.setOnClickListener(this);

            TextView tv_done = (TextView)findViewById(R.id.tv_done);
            tv_done.setOnClickListener(this);

//            ImageView nextIv = (ImageView) getView().findViewById(R.id.tutor_typein_five_next_iv);
//            nextIv.setOnClickListener(this);
//
//            mDescEdit = (EditText) getView().findViewById(R.id.tutor_five_edit);
//            mUploadIv = (ImageView) getView().findViewById(R.id.tutor_five_upload_video_iv);
//            mUploadIv.setOnClickListener(this);
//            mPlayIv = (ImageView) getView().findViewById(R.id.tutor_five_video_play_iv);
//            mPlayIv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_five_back:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.tv_done:
                    finish();
                    break;
//                case R.id.tutor_typein_five_next_iv:
//                    mTutorDesc = mDescEdit.getText().toString();
//                    if (TextUtils.isEmpty(mTutorDesc)) {
//                        TipsUtils.showShort(getApplicationContext(), "请输入个人描述信息");
//                        return;
//                    }
//
//                    if(TextUtils.isEmpty(mTutorVideoUrl)) {
//                        TipsUtils.showShort(getApplicationContext(), "请上传视频介绍");
//                        return;
//                    }
//
//                    submitTutorInfo();
//                    break;
//                case R.id.tutor_five_upload_video_iv:
//                    // TODO 录制视频
//                    recordVideo();
//                    break;
//                case R.id.tutor_five_video_play_iv:
//                    if(!TextUtils.isEmpty(mVideoFilePath)) {
//                        Intent playIntent = new Intent();
//                        playIntent.setClass(TutorInfoTypeInActivity.this, VideoPlayerActivity.class);
//                        playIntent.putExtra(VideoPlayerActivity.VIDEO_URI, mVideoFilePath);
//                        startActivity(playIntent);
//                    }
//                    break;
            }
        }
    }

    private void submitTutorInfo() {
        showLoadDialog();
        String tag_json_obj = "json_obj_tutor_typein_req";
        JSONObject paramObj = getParamsObj();

        Log.e("lzm", "tutor_parems=" + paramObj.toString());

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_TYPE_IN, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("lzm", "tutor_typein res=" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    setResult(RESULT_OK);
                    toFiveSetpFragment();
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
         * Header:
         1)uid(注册、登录成功返回的uid)
         2)platform(客户端平台信息，1-Android,2-IOS)
         Body(JSON格式):
         1)startPrice(起步价，开始5分钟多少钱，单位：元/分钟)
         2)minutePrice(起步价之后每分钟多少钱，单位：元/分钟)
         3)jobCardUrl(工卡或名片扫描件图片地址)
         4)workFlow(工作经历，JSON数组，里面是JSON对象{"startYear:2004,"endYear":2008,"title":"CTO","company":"BAT"})
         5)videoUrl(回答者介绍视频地址)
         6)introduce(自我介绍)
         响应内容(JSON)：
         1)成功
         {
         "status":10000,
         "result":"回答者信息添加成功"
         }
         2)失败
         {
         "status": 100XX,
         "resutl": "xxxxXXX"
         }
         */
        JSONObject paramObj = new JSONObject();
        try {
            /**
             * 1)startPrice(起步价，开始5分钟多少钱，单位：元/分钟)
             2)minutePrice(起步价之后每分钟多少钱，单位：元/分钟)
             3)jobCardUrl(工卡或名片扫描件图片地址)
             4)workFlow(工作经历，JSON数组，里面是JSON对象{"startYear:2004,"endYear":2008,"title":"CTO","company":"BAT"})
             5)videoUrl(回答者介绍视频地址)
             6)introduce(自我介绍)
             */

            JSONArray jsonThreeArray = new JSONArray();
            JSONObject itemObj = new JSONObject();
            itemObj.put("startYear", startYear);
            itemObj.put("endYear", endYear);
            itemObj.put("title", lastPosition);
            itemObj.put("company", lastCompany);
            jsonThreeArray.put(itemObj);


            paramObj.put("startPrice", first5Money);
            paramObj.put("minutePrice", after5PerMoney);
            paramObj.put("jobCardUrl", mCardUrl);
            paramObj.putOpt("workFlow", jsonThreeArray);
            paramObj.put("videoUrl", mTutorVideoUrl);
            paramObj.put("introduce", mTutorDesc);

            L.i("回答者注册信息:"+paramObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramObj;
    }

    private void recordVideo() {
        Intent vIntent = new Intent(this, RecordVideoActivity.class);
        startActivityForResult(vIntent, REQUEST_CODE_VIDEO);
    }

    private void toSecondSetpFragment() {
        Fragment newFragment = new TowSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void toThirdSetpFragment() {
        Fragment newFragment = new ThreeSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void toFourthSetpFragment() {
        Fragment newFragment = new FourSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void toFiveSetpFragment() {
        Fragment newFragment = new FivSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
