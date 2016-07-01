package com.smart.mirrorer.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.CommonAdapter;
import com.smart.mirrorer.bean.BaseIndustryBean;
import com.smart.mirrorer.bean.UploadTokenBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.SimpleJsonObjectRequest;
import com.smart.mirrorer.util.AppFilePath;
import com.smart.mirrorer.util.BitmapPickUtils;
import com.smart.mirrorer.util.DeviceConfiger;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MaterialUtils;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UploadUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.MyTextUtil;
import com.smart.mirrorer.view.ActionSheet;
import com.smart.mirrorer.view.NoToggleCheckBox;
import com.smart.mirrorer.view.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/4/11.
 */
public class BaseInfoTypeInActivity extends BaseActivity implements ActionSheet.ActionSheetListener {

    // 图片相关
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放

    public static final int PHOTORESOULT = 3;// 结果
    public static final String IMAGE_UNSPECIFIED = "image/*";

    private KeyboardService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_typein_layout);

        service = new KeyboardService(this);

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
                BitmapPickUtils.startSystemCamera(this, PHOTOHRAPH, pictureOut);
                break;
            case 1:
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                startActivityForResult(intent1, PHOTOZOOM);
                break;
        }
    }

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

    private String mFilePath;
    private String mUploadId;

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
            UploadUtils.uploadFile(BaseApplication.getUploadManager(BaseInfoTypeInActivity.this), new File(mFilePath), mUploadCallBack, token.getResult()
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
                BaseApplication.mImageLoader.displayImage(localFileUri, mHeadIv, BaseApplication.headOptions);
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

    private CircleImageView mHeadIv;
    private String mAvarUrl;
    private String mShowLocalUrl;
    private String mNameText;

    @SuppressLint("ValidFragment")
    private class FirstStepFragment extends Fragment implements View.OnClickListener {

        private EditText nameEdit;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.base_typein_first_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView closeIv = (ImageView) getView().findViewById(R.id.base_typein_first_close_iv);
            closeIv.setOnClickListener(this);
            ImageView nextIv = (ImageView) getView().findViewById(R.id.base_typein_first_next_iv);
            mHeadIv = (CircleImageView) getView().findViewById(R.id.base_typein_first_top_head_iv);
            if (!TextUtils.isEmpty(mShowLocalUrl)) {
                BaseApplication.mImageLoader.displayImage(mShowLocalUrl, mHeadIv, BaseApplication.headOptions);
            }

            TextView uploadTv = (TextView) getView().findViewById(R.id.base_first_update_tv);
            nameEdit = (EditText) getView().findViewById(R.id.base_first_name_edit);
            MyTextUtil.countLimit(nameEdit,14);

            nextIv.setOnClickListener(this);
            uploadTv.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_first_update_tv:
                    service.hideKeyboard(v);
                    showActionSheet("拍照", "从相册选择");
                    break;
                case R.id.base_typein_first_next_iv:
                    service.hideKeyboard(v);

                    if (TextUtils.isEmpty(mAvarUrl)) {
                        TipsUtils.showShort(getApplicationContext(), "请上传头像");
                        return;
                    }
                    mNameText = nameEdit.getText().toString();
                    if (TextUtils.isEmpty(mNameText)) {
                        TipsUtils.showShort(getApplicationContext(), "姓名不能为空");
                        return;
                    }
                    toSecondSetpFragment();
                    break;
                case R.id.base_typein_first_close_iv:
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


    private int mSexType = 1;
    private String mBirthdayYear = "1987";

    @SuppressLint("ValidFragment")
    private class TowSetpFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.base_typein_second_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView backIv = (ImageView) getView().findViewById(R.id.base_typein_second_back_iv);
            backIv.setOnClickListener(this);

            ImageView nextIv = (ImageView) getView().findViewById(R.id.base_typein_second_next_iv);
            nextIv.setOnClickListener(this);

            RadioGroup group = (RadioGroup) getView().findViewById(R.id.base_second_randio_group);
            //绑定一个匿名监听器
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup arg0, int arg1) {
                    // TODO Auto-generated method stub
                    //获取变更后的选中项的ID
                    int radioButtonId = arg0.getCheckedRadioButtonId();
                    //根据ID获取RadioButton的实例
                    RadioButton rb = (RadioButton) getView().findViewById(radioButtonId);
                    //更新文本内容，以符合选中项
                    String rbText = rb.getText().toString();
                    if ("男".equals(rbText)) {
                        mSexType = 1;
                    } else {
                        mSexType = 2;
                    }
                }
            });


            WheelView yearWheelView = (WheelView) getView().findViewById(R.id.base_typein_second_age_selview);
            yearWheelView.setOffset(1);
            yearWheelView.setItems(Arrays.asList(getResources().getStringArray(R.array.years)));
            yearWheelView.setSeletion(37);
            yearWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                @Override
                public void onSelected(int selectedIndex, String item) {
                    mBirthdayYear = item.substring(0, item.length() - 1);
                }
            });

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_typein_second_back_iv:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.base_typein_second_next_iv:
                    toThirdSetpFragment();
                    break;
            }
        }


    }

    private int mPositonType = 3;

    @SuppressLint("ValidFragment")
    private class ThreeSetpFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.base_typein_third_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView backIv = (ImageView) getView().findViewById(R.id.base_typein_third_back_iv);
            backIv.setOnClickListener(this);

            ImageView nextIv = (ImageView) getView().findViewById(R.id.base_typein_third_next_iv);
            nextIv.setOnClickListener(this);

            RadioGroup group = (RadioGroup) getView().findViewById(R.id.base_second_randio_group);
            //绑定一个匿名监听器
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup arg0, int arg1) {
                    // TODO Auto-generated method stub
                    //获取变更后的选中项的ID
                    int radioButtonId = arg0.getCheckedRadioButtonId();
                    //根据ID获取RadioButton的实例
                    RadioButton rb = (RadioButton) getView().findViewById(radioButtonId);
                    //更新文本内容，以符合选中项
                    String rbText = rb.getText().toString();
                    if ("提问者".equals(rbText)) {
                        mPositonType = 1;
                    } else if ("在职者".equals(rbText)) {
                        mPositonType = 2;
                    } else {
                        mPositonType = 3;
                    }
                }
            });

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_typein_third_back_iv:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.base_typein_third_next_iv:
                    toFourthSetpFragment();
                    break;
            }
        }


    }

    private int mLastIndex = 0; //分组设置显示最后一个角标
    private List<BaseIndustryBean.ResultBean> mNewIndustryList;
    private CommonAdapter<BaseIndustryBean.ResultBean> mIndustryAdapter;
    private CommonAdapter<BaseIndustryBean.ResultBean> mSelIndustryAdapter;

    @SuppressLint("ValidFragment")
    private class FourSetpFragment extends Fragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.base_typein_fourth_step_layout, null);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ImageView backIv = (ImageView) getView().findViewById(R.id.base_typein_fourth_back_iv);
            backIv.setOnClickListener(this);

            ImageView nextIv = (ImageView) getView().findViewById(R.id.base_typein_fourth_next_iv);
            nextIv.setOnClickListener(this);

            LinearLayout changeTv = (LinearLayout) getView().findViewById(R.id.base_fourth_change_layout);
            changeTv.setOnClickListener(this);

            GridView selGridView = (GridView) getView().findViewById(R.id.base_fourth_sel_gridview);
            mSelIndustryAdapter = new CommonAdapter<BaseIndustryBean.ResultBean>(getActivity(), R.layout.item_base_fourth_sel_grid_layout) {
                @Override
                protected void convert(ViewHolderEntity entity, final BaseIndustryBean.ResultBean itemData, int itemViewType) {
                    TextView tagTv = entity.getView(R.id.item_base_fourth_sel);
                    tagTv.setText(itemData.getCatName());
                }
            };
            selGridView.setAdapter(mSelIndustryAdapter);

            GridView gridView = (GridView) getView().findViewById(R.id.base_fourth_gridview);
            mIndustryAdapter = new CommonAdapter<BaseIndustryBean.ResultBean>(getActivity(), R.layout.item_base_fourth_grid_layout) {
                @Override
                protected void convert(ViewHolderEntity entity, final BaseIndustryBean.ResultBean itemData, int itemViewType) {
                    NoToggleCheckBox checkBox = entity.getView(R.id.item_base_fourth_checkbox);
                    Drawable bgDraw = MaterialUtils.createGuideCanSelectSolidStrokeBg(Color.parseColor("#8e8e8e"), Color.parseColor("#e93838"));
                    checkBox.setBackgroundDrawable(bgDraw);
                    checkBox.setButtonDrawable(new BitmapDrawable());
                    checkBox.setText(itemData.getCatName());
                    if (itemData.isChecked) {
                        checkBox.setChecked(true);
                        checkBox.setTextColor(Color.parseColor("#ffffff"));
                    } else {
                        checkBox.setChecked(false);
                        checkBox.setTextColor(Color.parseColor("#8e8e8e"));
                    }

                    int size = DeviceConfiger.dp2sp(14);
                    checkBox.setTextSize(size);

                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemData.isChecked) {
                                itemData.isChecked = false;
                            } else {
                                itemData.isChecked = true;
                            }
                            notifyDataSetChanged();
                            updateSelGridview(itemData.getCatId(), itemData.isChecked);
                        }
                    });
                }
            };
            gridView.setAdapter(mIndustryAdapter);
            getIndustryData();
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base_typein_fourth_back_iv:
                    getSupportFragmentManager().popBackStack();
                    break;
                case R.id.base_typein_fourth_next_iv:
                    List<BaseIndustryBean.ResultBean> selIndustrys = mSelIndustryAdapter.getData();
                    if (selIndustrys == null || selIndustrys.isEmpty()) {
                        TipsUtils.showShort(getApplicationContext(), "您还没有选择感兴趣的行业");
                        return;
                    }

                    String baseInfoParams = getBaseInfoParams(selIndustrys);

                    Log.e("lzm", "params=" + baseInfoParams);

                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    intent.putExtra(RegisterActivity.KEY_BASEINFO_DATA, baseInfoParams);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.base_fourth_change_layout:
                    updateChangeGridUI(mNewIndustryList);
                    break;
            }
        }


        private void getIndustryData() {

            String tag_json_obj = "json_obj_base_industry_req";
            showLoadDialog();
            SimpleJsonObjectRequest jsonObjReq = new SimpleJsonObjectRequest(Request.Method.POST,
                    Urls.URL_INDUSTRY_LIST, null, mIndustryCallBack, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissLoadDialog();
                    TipsUtils.showShort(getApplicationContext(), error.getMessage());
                }
            });

            // Adding request to request queue
            BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }

        private GsonCallbackListener<BaseIndustryBean> mIndustryCallBack = new GsonCallbackListener<BaseIndustryBean>() {
            @Override
            public void onResultSuccess(BaseIndustryBean industryBean) {
                super.onResultSuccess(industryBean);
                dismissLoadDialog();
                if (industryBean == null) {
                    return;
                }

                List<BaseIndustryBean.ResultBean> itemList = industryBean.getResult();
                if (itemList == null || itemList.isEmpty()) {
                    return;
                }

                updateChangeGridUI(itemList);
            }

            @Override
            public void onFailed(String errorMsg) {
                super.onFailed(errorMsg);
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), errorMsg);
            }
        };
    }

    /**
     * 拼接信息录入的json参数
     *
     * @param selIndustrys
     * @return
     */
    private String getBaseInfoParams(List<BaseIndustryBean.ResultBean> selIndustrys) {
        StringBuffer sb = new StringBuffer();
        for (BaseIndustryBean.ResultBean itemData : selIndustrys) {
            sb.append(itemData.getCatId() + ",");
        }

        String ids = sb.deleteCharAt(sb.length() - 1).toString();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("headImgUrl", mAvarUrl);
            paramObj.put("uploadId", mUploadId);
            paramObj.put("realName", mNameText);
            paramObj.put("sex", mSexType + "");
            paramObj.put("birthYear", mBirthdayYear);
            paramObj.put("level", mPositonType + "");
            paramObj.put("intIds", ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return paramObj.toString();
    }

    /**
     * 更新已选grid
     *
     * @param catId
     * @param isChecked
     */
    private void updateSelGridview(String catId, boolean isChecked) {
        for (BaseIndustryBean.ResultBean itemData : mNewIndustryList) {
            if (catId.equals(itemData.getCatId())) {
                itemData.isChecked = isChecked;
            }
        }

        List<BaseIndustryBean.ResultBean> tempList = new ArrayList<>();
        for (BaseIndustryBean.ResultBean tempItem : mNewIndustryList) {
            if (tempItem.isChecked) {
                tempList.add(tempItem);
            }
        }
        mSelIndustryAdapter.setData(tempList);
    }

    /**
     * 更新换一组grid
     *
     * @param itemList
     */
    private void updateChangeGridUI(List<BaseIndustryBean.ResultBean> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }

        mNewIndustryList = getNewListData(itemList);
        List<BaseIndustryBean.ResultBean> tempList = new ArrayList<>();
        for (BaseIndustryBean.ResultBean tempItem : mNewIndustryList) {
            if (tempItem.isShow) {
                tempList.add(tempItem);
            }
        }
        mIndustryAdapter.setData(tempList);
    }

    private List<BaseIndustryBean.ResultBean> getNewListData(List<BaseIndustryBean.ResultBean> itemList) {
        for (BaseIndustryBean.ResultBean cleanData : itemList) {
            cleanData.isShow = false;
        }

        int count = 0;
        for (int i = mLastIndex; i < itemList.size(); i++) {
            if (count < 9) {
                BaseIndustryBean.ResultBean itemData = itemList.get(i);
                itemData.isShow = true;
                if (mLastIndex == itemList.size() - 1) {
                    mLastIndex = 0;
                    break;
                }
                count++;
                mLastIndex++;
            }
        }

        return itemList;
    }

    private void toSecondSetpFragment() {
        Fragment newFragment = new TowSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void toThirdSetpFragment() {
        Fragment newFragment = new ThreeSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void toFourthSetpFragment() {
        Fragment newFragment = new FourSetpFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slid_open_enter, R.anim.slid_open_exit, R.anim.slid_colse_enter, R.anim.slid_close_exit);
        ft.replace(R.id.info_typein_sub_frame_layout, newFragment);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }
}
