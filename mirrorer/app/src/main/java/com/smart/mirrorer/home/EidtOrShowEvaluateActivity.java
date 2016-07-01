package com.smart.mirrorer.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.EvaluateTagAdapter;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.bean.home.EvaluateTagsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.util.mUtil.MyTextUtil;
import com.videorecorder.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/5/13.
 */
public class EidtOrShowEvaluateActivity extends BaseTitleActivity implements View.OnClickListener {

    public static final String KEY_ORDER_DATA = "key_order_data";

    private CircleImageView mHeadIv;
    private TextView mNickTv;
    private TextView mCompanyTv;
    private TextView mPositionTv;
//    private TextView mStartTv;
//    private TextView mMinuteTv;
    private TextView mStarCountTv;

//    private TextView mPayedMoneyTv;

//    private LinearLayout mMoneyLayout;
    private LinearLayout mEditContentLayout;

    private OrderDetailsData mOrderDeatailsData;

    private EvaluateTagAdapter mAdapter;

    private String mUid;

    private ImageView xingxing1;
    private ImageView xingxing2;
    private ImageView xingxing3;
    private ImageView xingxing4;
    private ImageView xingxing5;

    private TextView tag1;
    private TextView tag2;
    private TextView tag3;
    private TextView tag4;
    private TextView tag5;
    private TextView tag6;

    private ArrayList<String> tagsStrs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_layout2);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("评价");

        MirrorSettings settings = BaseApplication.getSettings(this);
        mUid = settings.APP_UID.getValue();
        mOrderDeatailsData = getIntent().getExtras().getParcelable(KEY_ORDER_DATA);
        if(getIntent().hasExtra("tags"))
        {
            tagsStrs = getIntent().getStringArrayListExtra("tags");
        }

        initView();

//        getTags();
        if (mOrderDeatailsData != null) {
            updateTopUI();
        }
    }

    /**
     * 获取评价标签
     */
//    private void getTags() {
//        String tag_json_obj = "json_obj_evaluate_tag_req";
//        showLoadDialog();
//
//        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
//                Urls.URL_EVALUATE_TAGS, null, mTagsCallBack, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dismissLoadDialog();
//                TipsUtils.showShort(getApplicationContext(), error.getMessage());
//            }
//        });
//
//        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
//    }

//    private GsonCallbackListener<EvaluateTagsBean> mTagsCallBack = new GsonCallbackListener<EvaluateTagsBean>() {
//        @Override
//        public void onResultSuccess(EvaluateTagsBean evaluateTagsBeanBean) {
//            super.onResultSuccess(evaluateTagsBeanBean);
//            dismissLoadDialog();
//            if (evaluateTagsBeanBean == null) {
//                return;
//            }
//
//            EvaluateTagsBean.ResultBean resultBean = evaluateTagsBeanBean.getResult();
//            if (resultBean == null) {
//                return;
//            }
//
//            List<EvaluateTagsBean.ResultBean.TagListBean> tagList = resultBean.getTag_list();
//            if (tagList == null || tagList.isEmpty()) {
//                return;
//            }
//
//            mAdap鳄valuateter.setData(tagList);
//        }
//
//        @Override
//        public void onFailed(String errorMsg) {
//            super.onFailed(errorMsg);
//            dismissLoadDialog();
//            TipsUtils.showShort(getApplicationContext(), errorMsg);
//        }
//    };

    private void updateTopUI() {
        String headUrl = mOrderDeatailsData.getGuiderHeadUrl();
        if (!TextUtils.isEmpty(headUrl)) {
            BaseApplication.mImageLoader.displayImage(headUrl, mHeadIv, BaseApplication.headOptions);
        }
        mNickTv.setText(mOrderDeatailsData.getGuiderName());
        mCompanyTv.setText(mOrderDeatailsData.getCompany());
        mPositionTv.setText(mOrderDeatailsData.getTitle());
//        mStartTv.setText(getResources().getString(R.string.tutor_five_minute_price, mOrderDeatailsData.getStartPrice() + ""));
//        mMinuteTv.setText(getResources().getString(R.string.tutor_minute_price, mOrderDeatailsData.getMinutePrice() + ""));
        mStarCountTv.setText(mOrderDeatailsData.getGuiderStar() + "");

//        mPayedMoneyTv.setText(mOrderDeatailsData.getPayMoney() + "");
    }

    private RatingBar mRating;
    private int mScroreInt;
    private EditText mContentEdit;
    private TextView mContentTv;
    private TextView mCommitTv;
    private TextView mNimingCommitTv;
//    private TextView mPleaseTv;

    private List<TextView> tags;
    private boolean isShowEdit;
    private View v_showMore;
    private void initView() {
        v_showMore = findViewById(R.id.v_showMore);

        v_separate = (View)findViewById(R.id.v_separate);
        ll_evaluate = (LinearLayout) findViewById(R.id.ll_evaluate);

        mHeadIv = (CircleImageView) findViewById(R.id.recommon_like_cell_head_iv);
        mNickTv = (TextView) findViewById(R.id.liked_nick_tv);
        mCompanyTv = (TextView) findViewById(R.id.liked_company_tv);
        mPositionTv = (TextView) findViewById(R.id.liked_position_tv);
//        mStartTv = (TextView) findViewById(R.id.liked_start_price_tv);
//        mMinuteTv = (TextView) findViewById(R.id.liked_minute_price_tv);
        mStarCountTv = (TextView) findViewById(R.id.recommon_like_cell_right_count_tv);
//        ImageView videoIv = (ImageView) findViewById(R.id.recommon_like_cell_video_icon);
//        videoIv.setVisibility(View.GONE);

//        mMoneyLayout = (LinearLayout) findViewById(R.id.evaluate_money_layout);
//        mPayedMoneyTv = (TextView) findViewById(R.id.evaluate_payed_money_tv);

//        mPleaseTv = (TextView) findViewById(R.id.evaluate_please_choose_tv);

//        mRating = (RatingBar) findViewById(R.id.evaluate_star_ratingbar);
//        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
////                mMoneyLayout.setVisibility(View.GONE);
//                mEditContentLayout.setVisibility(View.VISIBLE);
//
//                if (rating == 1.0) {
//                    mScroreInt = 1;
//                } else if (rating == 2.0) {
//                    mScroreInt = 2;
//                } else if (rating == 3.0) {
//                    mScroreInt = 3;
//                } else if (rating == 4.0) {
//                    mScroreInt = 4;
//                } else if (rating == 5.0) {
//                    mScroreInt = 5;
//                }
//            }
//        });
        xingXingImageViews = new ArrayList<ImageView>();
        xingxing1 = (ImageView)findViewById(R.id.iv_xingxing1);
        xingxing1.setOnClickListener(this);
        xingXingImageViews.add(xingxing1);
        xingxing2 = (ImageView)findViewById(R.id.iv_xingxing2);
        xingxing2.setOnClickListener(this);
        xingXingImageViews.add(xingxing2);
        xingxing3 = (ImageView)findViewById(R.id.iv_xingxing3);
        xingxing3.setOnClickListener(this);
        xingXingImageViews.add(xingxing3);
        xingxing4 = (ImageView)findViewById(R.id.iv_xingxing4);
        xingxing4.setOnClickListener(this);
        xingXingImageViews.add(xingxing4);
        xingxing5 = (ImageView)findViewById(R.id.iv_xingxing5);
        xingxing5.setOnClickListener(this);
        xingXingImageViews.add(xingxing5);

        tags = new ArrayList<TextView>();
        tag1 = (TextView)findViewById(R.id.tv_tag1);
        tag1.setOnClickListener(this);
        tags.add(tag1);
        tag2 = (TextView)findViewById(R.id.tv_tag2);
        tag2.setOnClickListener(this);
        tags.add(tag2);
        tag3 = (TextView)findViewById(R.id.tv_tag3);
        tag3.setOnClickListener(this);
        tags.add(tag3);
        tag4 = (TextView)findViewById(R.id.tv_tag4);
        tag4.setOnClickListener(this);
        tags.add(tag4);
        tag5 = (TextView)findViewById(R.id.tv_tag5);
        tag5.setOnClickListener(this);
        tags.add(tag5);
        tag6 = (TextView)findViewById(R.id.tv_tag6);
        tag6.setOnClickListener(this);
        tags.add(tag6);




//        //标签选择以下的布局
//        mEditContentLayout = (LinearLayout) findViewById(R.id.evaluate_content_layout);
//        ScrollListView listView = (ScrollListView) findViewById(R.id.evaluate_tag_listview);
//        mAdapter = new EvaluateTagAdapter(this, R.layout.item_evaluate_tag_layout);
//        listView.setAdapter(mAdapter);

        mContentEdit = (EditText) findViewById(R.id.evaluate_content_edit);
        MyTextUtil.countLimit(mContentEdit,150);
        mContentTv = (TextView) findViewById(R.id.evaluate_content_tv);
        mCommitTv = (TextView) findViewById(R.id.evaluate_commit_tv);
        mCommitTv.setOnClickListener(this);
//        evaluate_niming_commit_tv
        mNimingCommitTv = (TextView) findViewById(R.id.evaluate_niming_commit_tv);
        mNimingCommitTv.setOnClickListener(this);
    }

    private String mShareUrl = "http://www.mirrorer.com/";

    private boolean isNimingCommite;
    private boolean isClickXingAlready;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.evaluate_commit_tv:
//                if (isShareType) {
//                    SnsUtils.shareApp(EidtOrShowEvaluateActivity.this, "瞬语", "语音提问，全球众包，即时视频回答", mShareUrl);
//                } else {
                    commitEvaluate(0);
//                }
                break;
            case R.id.evaluate_niming_commit_tv:
                    commitEvaluate(1);
                break;
            case R.id.iv_xingxing1:
                clickXingXing(1);
                break;
            case R.id.iv_xingxing2:
                clickXingXing(2);
                break;
            case R.id.iv_xingxing3:
                clickXingXing(3);
                break;
            case R.id.iv_xingxing4:
                clickXingXing(4);
                break;
            case R.id.iv_xingxing5:
                clickXingXing(5);
                break;
            case R.id.tv_tag1:
                clickTag((TextView) v);
                break;
            case R.id.tv_tag2:
                clickTag((TextView) v);
                break;
            case R.id.tv_tag3:
                clickTag((TextView) v);
                break;
            case R.id.tv_tag4:
                clickTag((TextView) v);
                break;
            case R.id.tv_tag5:
                clickTag((TextView) v);
                break;
            case R.id.tv_tag6:
                clickTag((TextView) v);
                break;
        }
    }

    /**
     * flag = 1 匿名 0 非匿名
     * @param commiteType
     */
    private void commitEvaluate(int commiteType) {
        if (mOrderDeatailsData == null) {
            TipsUtils.showShort(getApplicationContext(), "订单信息异常");
            return;
        }

        String tag_json_obj = "json_obj_evaluate_commit_req";
        showLoadDialog();

        JSONObject paramObj = getParamsObj(commiteType);

        Log.e("lzm", "paramobj=" + paramObj.toString());

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_EVALUATE_COMMIT, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();
                Log.e("lzm", "commit_res_text =" + response.toString());
                TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    showSucUI();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });

        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    boolean xingxingClickable = true;
    boolean tagClickable = true;
    /**
     * 显示评论成功ui
     */
    private void showSucUI() {
        setCommonTitle("评价详情");
//        mMoneyLayout.setVisibility(View.VISIBLE);
//        mRating.setClickable(false);
//        mRating.setEnabled(false);
//        mAdapter.setUnCheck();

        xingxingClickable = false;
        tagClickable = false;

        mContentTv.setVisibility(View.VISIBLE);
        mContentTv.setText(mContentEdit.getText().toString());
        mContentEdit.setVisibility(View.GONE);
//        mContentTv.setText(mContentEdit.getText().toString());

//        mPleaseTv.setVisibility(View.GONE);


        ((LinearLayout)findViewById(R.id.ll_tag2)).setVisibility(View.GONE);
        tag1.setVisibility(View.GONE);
        tag2.setVisibility(View.GONE);
        tag3.setVisibility(View.GONE);
        int position = 0;
        for (TextView tv : tags)
        {
            L.i("AAABBB tag = "+(String)(tv.getTag()));
            if(!TextUtils.isEmpty((String)tv.getTag()))
            {
                position++;
                switch (position)
                {
                    case 1:
                        tag1.setText((String)tv.getTag());
                        tag1.setBackgroundResource(R.drawable.btn_se_biaoqian);
                        tag1.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        tag2.setText((String)tv.getTag());
                        tag2.setBackgroundResource(R.drawable.btn_se_biaoqian);
                        tag2.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        tag3.setText((String)tv.getTag());
                        tag3.setBackgroundResource(R.drawable.btn_se_biaoqian);
                        tag3.setVisibility(View.VISIBLE);
                        break;
                }

            }
        }
        mNimingCommitTv.setVisibility(View.GONE);
        mCommitTv.setVisibility(View.GONE);
//        mCommitTv.setText("分享");
//        isShareType = true;

    }

//    private boolean isShareType;

    /**
     * 拼接提交参数
     *
     * @return
     */
    private JSONObject getParamsObj(int commiteType) {
//        List<EvaluateTagsBean.ResultBean.TagListBean> tagListData = mAdapter.getData();
        JSONArray arr = new JSONArray();
//        for (EvaluateTagsBean.ResultBean.TagListBean itemTag : tagListData) {
//            if (itemTag.isChecked) {
//                arr.put(itemTag.getTag_name());
//            }
//        }
        for (TextView tv : tags)
        {
            if(!TextUtils.isEmpty((String)tv.getTag()))
            arr.put((String)tv.getTag());
        }

        JSONObject object = new JSONObject();
        try {
            object.putOpt("gid", mOrderDeatailsData.getGuiderId());
            object.putOpt("oid", mOrderDeatailsData.getOrderId());
            object.putOpt("score", mScroreInt + "");
            object.putOpt("tags", arr);
            object.putOpt("body", mContentEdit.getText().toString());
            object.putOpt("flag", commiteType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    private List<ImageView> xingXingImageViews;
    private void removeAllXing()
    {
        for (ImageView v : xingXingImageViews)
        {
            v.setImageResource(R.drawable.btn_xingxing1);
        }
    }
    private LinearLayout ll_evaluate;
    private View v_separate;
    private void clickXingXing(int position)
    {
        //点击星星后才去掉星顶部空白View和展示下面的评价内容Viewgroup
        if(!isClickXingAlready)
        {
            isClickXingAlready = true;
            v_showMore.setVisibility(View.GONE);
            ll_evaluate.setVisibility(View.VISIBLE);
        }

        if(!xingxingClickable)return;
        mScroreInt = position;
        removeAllXing();
        if (!isShowEdit)
        {
            v_separate.setVisibility(View.GONE);
            ll_evaluate.setVisibility(View.VISIBLE);
            isShowEdit = true;
            L.i("isShowEdit = false");
        }else
            L.i("isShowEdit = true");
        switch (position)
        {
            case 5:
                xingxing5.setImageResource(R.drawable.btn_se_xingxing);
            case 4:
                xingxing4.setImageResource(R.drawable.btn_se_xingxing);
            case 3:
                xingxing3.setImageResource(R.drawable.btn_se_xingxing);
            case 2:
                xingxing2.setImageResource(R.drawable.btn_se_xingxing);
            case 1:
                xingxing1.setImageResource(R.drawable.btn_se_xingxing);
                break;
        }
    }
    int tagCount;
    private void clickTag(TextView tv)
    {
        if(!tagClickable)return;
        String currentTag = (String)tv.getTag();
        if(TextUtils.isEmpty(currentTag))
        {
            if(tagCount<3)
            {
                tv.setTag(tv.getText().toString());
                tv.setBackgroundResource(R.drawable.btn_se_biaoqian);
                tagCount++;
            }else
            {
                TipsUtils.showShort(getApplicationContext(),"最多只能选择3个评价标签");
            }

        }else
        {
            tv.setTag(null);
            tv.setBackgroundResource(R.drawable.btn_biaoqian);
            tagCount--;
        }
    }


}
