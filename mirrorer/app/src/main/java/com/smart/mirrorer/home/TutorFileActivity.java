package com.smart.mirrorer.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.CommonAdapter;
import com.smart.mirrorer.adapter.DividerItemDecoration;
import com.smart.mirrorer.adapter.EvaluateListAdapter;
import com.smart.mirrorer.adapter.HomeHeadAdapter;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.EditUserInfoActivity;
import com.smart.mirrorer.base.TutorInfoTypeInActivity;
import com.smart.mirrorer.bean.home.EvaluateListBean;
import com.smart.mirrorer.bean.home.RecordFileBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.FullyLinearLayoutManager;
import com.smart.mirrorer.view.ScrollListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/4/9.
 */
public class TutorFileActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_NICK_NAME = "key_nick_name";
    public static final String KEY_ISNOT_TUTOR = "key_isnot_tutor";
    public static final String KEY_IS_SELF = "key_is_self";
    public static final String KEY_USE_UID = "key_use_uid";

    private boolean isNotTutorType;

    private String mUid;

    private TextView mTitleTv;
    private ImageView mHeadIv;
    private TextView mNameTv;
    private TextView mCompanyTv;
    private TextView mPositionTv;
    private TextView mFiveMinuteTv;
    private TextView mMinuteTv;
    private TextView mStarCountTv;
    private TextView mCallCountTv;
    private TextView mFansCountTv;
    private TextView mFollowCountTv;
    private TextView mDescTv;
//    private TextView mCommonCountTv;

    private ScrollListView mJobListview;
//    private ScrollListView mUserEvaluateListView;
    private RecyclerView mUserEvaluateRecyclerView;
    private CommonAdapter<RecordFileBean.ResultBean.WorkFlowBean> mJobAdapter;
    private CommonAdapter<RecordFileBean.ResultBean.EduFlowBean> mEvaluateAdapter;

    private String mBussnessId; //业务逻辑需要的uid

    private ScrollView sl_all;

    private RelativeLayout mVideoLayout;
//    private ImageView mFollowIv;

    private MirrorSettings mSettings;

    boolean isSelft;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutor_file_layout);

        uid = getIntent().getStringExtra(KEY_USE_UID);
        isNotTutorType = getIntent().getExtras().getBoolean(KEY_ISNOT_TUTOR);
        mBussnessId = getIntent().getExtras().getString(KEY_USE_UID);
        isSelft = getIntent().getBooleanExtra(KEY_IS_SELF,false);

        initView();

        mSettings = new MirrorSettings(this);
        mUid = mSettings.APP_UID.getValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPingjiaData();
        getFileData();
    }

    /**
     * 获取回答者档案
     */
    private void getFileData() {
        String tag_json_obj = "json_obj_get_file_req";
        showLoadDialog();
        canRequest = false;
        L.i("TutorFileActivity canRequest = "+canRequest);
        JSONObject param = new JSONObject();
        try {
            if (TextUtils.isEmpty(mBussnessId)) {
                param.put("gid", "0");
            } else {
                param.put("gid", mBussnessId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_RECORD_FILE, param, mFileCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
                canRequest = true;
                L.i("TutorFileActivity canRequest = "+canRequest);
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private RecordFileBean.ResultBean resultBean;
    private GsonCallbackListener<RecordFileBean> mFileCallBack = new GsonCallbackListener<RecordFileBean>() {
        @Override
        public void onResultSuccess(RecordFileBean recordFileBean) {
            super.onResultSuccess(recordFileBean);
            dismissLoadDialog();
            canRequest = true;
            L.i("TutorFileActivity canRequest = "+canRequest);
            resultBean = recordFileBean.getResult();
            if (recordFileBean == null || resultBean == null) {
                return;
            }

            updateUI(resultBean);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            canRequest = true;
            L.i("TutorFileActivity canRequest = "+canRequest);
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    /**
     * 获取评价列表
     */
    /**
     * 1)gid(回答者uid)
     2)limit(每页面返回多少条记录，默认为10)
     3)page(返回第几页的数据，默认为第1页)
     */
    private void getPingjiaData() {
        String tag_json_obj = "json_obj_get_file_req";
        showLoadDialog();
        JSONObject param = new JSONObject();
        try {
            if(isSelft)
            {
                param.put("gid", mUid);
            }
            else if (TextUtils.isEmpty(mBussnessId)) {
                param.put("gid", "0");
            } else {
                param.put("gid", mBussnessId);
            }

            param.put("limit",10);
            param.put("page",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        L.d( "evaluate param =" + param);
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_GUIDER_RATED_LIST, param, mPingjiaCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private GsonCallbackListener<EvaluateListBean> mPingjiaCallBack = new GsonCallbackListener<EvaluateListBean>() {
        @Override
        public void onResultSuccess(EvaluateListBean recordFileBean) {
            super.onResultSuccess(recordFileBean);
            L.i("evaluate list size = "+recordFileBean.getResult().size());
            dismissLoadDialog();
            if (recordFileBean == null || recordFileBean.getResult() == null) {
                return;
            }
            updateEvaluateUserUI(recordFileBean.getResult());
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    private ArrayList<EvaluateListBean.ResultBean> evaluateList;
    private EvaluateListAdapter mAdapter;
    private void updateEvaluateUserUI(List<EvaluateListBean.ResultBean> beans)
    {

        mAdapter.setListData(beans);

        tv_info_title.setFocusable(true);
        tv_info_title.setFocusableInTouchMode(true);
        tv_info_title.requestFocus();
//        sl_all.scrollTo(0,0);
//        sl_all.fullScroll(ScrollView.FOCUS_UP);
//        sl_all.smoothScrollTo(0,20);
//        //设置布局管理器
//        mUserEvaluateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        //设置adapter
//        mUserEvaluateRecyclerView.setAdapter(mAdapter);
//        //设置Item增加、移除动画
//        mUserEvaluateRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        //添加分割线
//        mUserEvaluateRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        /**
         * {
         'headImgUrl':<为空表示为匿名评价，用默认头像替代>,
         'realNam':<匿名或用户名称>,
         'uid':<评介者的uid,为后续扩展预留>,
         'tags_list':<评价的标签列表>,
         'score':<评介者打的几星，整型>,
         'body':<评介内容>,
         'createdAt':<评介的日期，字符串，格式：YYYY-mm-dd>
         }
         */
        String headImgUrl;
        String realName;
        String uid;
        List<String> tags_list;
        String score;
        String body;
        String createdAt;

    }
    /**
     * 更新UI
     *
     * @param recordFileBean
     */
    private void updateUI(RecordFileBean.ResultBean recordFileBean) {
        String headUrl = recordFileBean.getHeadImgUrl();
        Log.e("lzm", "headUrl =" + headUrl);
        if (TextUtils.isEmpty(headUrl)) {
            mHeadIv.setImageResource(R.drawable.app_default_head_icon);
        } else {
            BaseApplication.mImageLoader.displayImage(headUrl, mHeadIv, BaseApplication.headOptions);
        }

        mOneNick = recordFileBean.getRealName();

        isFollowed = recordFileBean.getIsFollowed() == 1 ? true : false;
        if (isFollowed) {
            iv_guanzhu.setImageResource(R.drawable.ic_title_follow_al);
        } else {
            iv_guanzhu.setImageResource(R.drawable.ic_title_follow);
        }

        mVideoUrl = recordFileBean.getVideoUrl();
        findViewById(R.id.rl_video).setVisibility(View.GONE);

        mTitleTv.setText(recordFileBean.getRealName());
        mCompanyTv.setText(recordFileBean.getCompany());
        mPositionTv.setText(recordFileBean.getTitle());
        mFiveMinuteTv.setText(getString(R.string.tutor_five_minute_price, recordFileBean.getStartPrice() + ""));
        mMinuteTv.setText(getString(R.string.tutor_minute_price, recordFileBean.getMinutePrice() + ""));
        mStarCountTv.setText(recordFileBean.getStar() + "");
        String desc = recordFileBean.getIntroduce();
        if (TextUtils.isEmpty(desc))desc = "无";
        mDescTv.setText(desc);
//        mCommonCountTv.setText(getString(R.string.file_common_count_text, recordFileBean.getCommentCount()+""));

        List<RecordFileBean.ResultBean.WorkFlowBean> jobList = recordFileBean.getWorkFlow();
        if (jobList != null && !jobList.isEmpty()) {
            mJobAdapter.setData(jobList);
        }

        tv_info_title.setFocusable(true);
        tv_info_title.setFocusableInTouchMode(true);
        tv_info_title.requestFocus();
//        sl_all.scrollTo(0,0);
//        sl_all.fullScroll(ScrollView.FOCUS_UP);
//        sl_all.smoothScrollTo(0,20);
//        List<RecordFileBean.ResultBean.EduFlowBean> eduList = recordFileBean.getEduFlow();
//        if (eduList != null && !eduList.isEmpty()) {
////            TipsUtils.showShort(getApplicationContext(),"有评价数据,没展示出来");
////            mEvaluateAdapter.setData(eduList);
//        }else
//        {
////            TipsUtils.showShort(getApplicationContext(),"没有评价数据");
//        }
    }

    private ImageView iv_edit_my_info;
    private TextView tv_info_title;
    private int page =1 ;
    private boolean canRequest = true;
    private ImageView iv_guanzhu;
    private TextView tv_hujiao;
    private void initView() {
        tv_hujiao = (TextView)findViewById(R.id.tv_hujiao);
        if(isSelft)tv_hujiao.setVisibility(View.GONE);
        tv_hujiao.setOnClickListener(this);
        iv_guanzhu = (ImageView)findViewById(R.id.iv_guanzhu);
        iv_guanzhu.setOnClickListener(this);
        tv_info_title = (TextView)findViewById(R.id.tv_info_title);
        //编辑按钮

        if(isSelft)
        {
            iv_edit_my_info = (ImageView)findViewById(R.id.iv_edit_my_info);
            iv_edit_my_info.setVisibility(View.VISIBLE);
            iv_edit_my_info.setOnClickListener(this);
            iv_guanzhu.setVisibility(View.GONE);
        }

        LinearLayout backLayout = (LinearLayout) findViewById(R.id.tutor_file_back_ll);
        backLayout.setOnClickListener(this);

        mTitleTv = (TextView) findViewById(R.id.file_title_tv);
//        mFollowIv = (ImageView) findViewById(R.id.tutor_file_follow_iv);
//        mFollowIv.setOnClickListener(this);

        RelativeLayout tutorPriceLayout = (RelativeLayout) findViewById(R.id.tutor_file_price_layout);
        if (isNotTutorType) {
            tutorPriceLayout.setVisibility(View.GONE);
//            mFollowIv.setVisibility(View.INVISIBLE);
            mTitleTv.setText("我的档案");
        } else {
            tutorPriceLayout.setVisibility(View.VISIBLE);
//            mFollowIv.setVisibility(View.VISIBLE);
        }
        ImageView playIv = (ImageView) findViewById(R.id.file_video_play_iv);
        playIv.setOnClickListener(this);

        mHeadIv = (ImageView) findViewById(R.id.tutor_file_head_iv);
        mCompanyTv = (TextView) findViewById(R.id.file_company_tv);
        mPositionTv = (TextView) findViewById(R.id.file_position_tv);
        mFiveMinuteTv = (TextView) findViewById(R.id.file_five_minute_tv);
        mMinuteTv = (TextView) findViewById(R.id.file_minute_tv);
        mStarCountTv = (TextView) findViewById(R.id.tutor_file_right_count_tv);
        mDescTv = (TextView) findViewById(R.id.file_desc_tv);

        //工作经历
        mJobListview = (ScrollListView) findViewById(R.id.file_job_listview);
        mJobAdapter = new CommonAdapter<RecordFileBean.ResultBean.WorkFlowBean>(this, R.layout.item_file_job_layout_cell) {
            @Override
            protected void convert(ViewHolderEntity vh, RecordFileBean.ResultBean.WorkFlowBean itemData, int itemViewType) {
                TextView timeTv = vh.getView(R.id.item_file_job_time_tv);
                TextView companyTv = vh.getView(R.id.item_file_job_company_tv);
                TextView positionTv = vh.getView(R.id.item_file_job_position_tv);

                timeTv.setText(itemData.getStartYear() + " - " + itemData.getEndYear());
                companyTv.setText(itemData.getCompany());
                positionTv.setText(itemData.getTitle());
            }
        };
        mJobListview.setAdapter(mJobAdapter);



        //新用户评价
        mUserEvaluateRecyclerView = (RecyclerView) findViewById(R.id.file_evaluate_listview);
        final FullyLinearLayoutManager fllm = new FullyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        mUserEvaluateRecyclerView.setLayoutManager(fllm);
        mAdapter = new EvaluateListAdapter();
        mUserEvaluateRecyclerView.setAdapter(mAdapter);

        mUserEvaluateRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible  = fllm.getChildCount();
                int total = fllm.getItemCount();
                int past=fllm.findFirstCompletelyVisibleItemPosition();
                if ((visible + past) >= total && canRequest){
                    page++;
                    L.i("TutorFileActivity page = "+page);
                    getFileData();
                }
            }
        });

        //旧用户评价
//        mUserEvaluateListView = (ScrollListView) findViewById(R.id.file_evaluate_listview);
//        mEvaluateAdapter = new CommonAdapter<RecordFileBean.ResultBean.EduFlowBean>(this, R.layout.item_file_evaluate_layout_cell) {
//            @Override
//            protected void convert(ViewHolderEntity vh, RecordFileBean.ResultBean.EduFlowBean itemData, int itemViewType) {
////                TextView timeTv = vh.getView(R.id.item_file_edu_time_tv);
////                TextView schoolTv = vh.getView(R.id.item_file_edu_shcool_tv);
////                TextView professionalTv = vh.getView(R.id.item_file_edu_professional_tv);
////
////                timeTv.setText(itemData.getStartYear() + " - " + itemData.getEndYear());
////                schoolTv.setText(itemData.getSchool());
////                professionalTv.setText(itemData.getProfessional());
//            }
//        };
//        mUserEvaluateListView.setAdapter(mEvaluateAdapter);

    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            L.i("evaluate onCreateViewHolder");
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    TutorFileActivity.this).inflate(R.layout.item_user_evaluate_list, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            L.i("evaluate onBindViewHolder");
            EvaluateListBean.ResultBean resultBean = evaluateList.get(position);
//            holder.tv.setText(eval.get(position));
            if(!TextUtils.isEmpty(resultBean.getHeadImgUrl()))
            {
                BaseApplication.mImageLoader.displayImage(resultBean.getHeadImgUrl(),holder.iv_head,BaseApplication.headOptions);
            }
            holder.tv_name.setText(resultBean.getRealName());
            holder.tv_start_count.setText(resultBean.getScore());
            holder.tv_content.setText(resultBean.getBody());
            ArrayList<TextView> tags = new ArrayList<TextView>();
            tags.add(holder.tv_tag1);
            tags.add(holder.tv_tag2);
            tags.add(holder.tv_tag3);
            if (resultBean.getTags_list()!=null&&resultBean.getTags_list().size()!=0)
            {
                for (int i = 0;i<resultBean.getTags_list().size()&&i<tags.size();i++)
                {
                    tags.get(i).setText(resultBean.getTags_list().get(i));
                }
            }
            holder.tv_time.setText(resultBean.getCreatedAt());
        }

        @Override
        public int getItemCount()
        {
            L.i("evaluate getItemCount = "+evaluateList.size());
            return evaluateList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            CircleImageView iv_head;
            TextView tv_name;
            TextView tv_start_count;
            TextView tv_time;
            TextView tv_tag1;
            TextView tv_tag2;
            TextView tv_tag3;
            TextView tv_content;

            public MyViewHolder(View view)
            {
                super(view);
                iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                tv_start_count = (TextView) view.findViewById(R.id.tv_start_count);
                tv_time = (TextView) view.findViewById(R.id.tv_time);
                tv_tag1 = (TextView) view.findViewById(R.id.tv_tag1);
                tv_tag2 = (TextView) view.findViewById(R.id.tv_tag2);
                tv_tag3 = (TextView) view.findViewById(R.id.tv_tag3);
                tv_content = (TextView) view.findViewById(R.id.tv_content);
            }
        }
    }

    private String mOneNick;

    private boolean isFollowed;//是否已经关注
    private String mVideoUrl;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_my_info:
                Intent intent = new Intent(this,EditUserInfoActivity.class);
                intent.putExtra("headImgUrl",resultBean.getHeadImgUrl());
                intent.putExtra("realName",resultBean.getRealName());
                intent.putExtra("company",resultBean.getCompany());
                intent.putExtra("title",resultBean.getTitle());
                intent.putExtra("sex",resultBean.getSex()+"");
                intent.putExtra("startPrice",resultBean.getStartPrice());
                intent.putExtra("minutePrice",resultBean.getMinutePrice());
                intent.putExtra("videoUrl",resultBean.getVideoUrl());
                intent.putExtra("introduce",resultBean.getIntroduce());

                /**
                 * "headImgUrl":"xxx.jpg",(用户头像地址)
                 "realName":"张三",(用户真实姓名)
                 "company":"BAT",(用户目前任职的公司名称)
                 "title":"CTO",(职位名称)
                 "helpCount":0 ,(表示已帮助解决多少问题数，接了多少单)
                 "fansCount":0,(粉丝数)
                 "focusCount":0,(关注数)
                 "star":0.0(评分数),
                 "introduce":"自我介绍",(自我介绍文本),
                 "videoUrl":"导师介绍视频地址",
                 "sex":<性别， 1-男，2-女>
                 "qCount":"导师提问个数",
                 "startPrice":<起步价，浮点型>,
                 "minutePrice":<每分钟价格，浮点型>,
                 "workFlow":[
                 {"startYear:2004,"endYear":2008,"title":"CTO","company":"BAT"},
                 {"startYear:2004,"endYear":2008,"title":"COO","company":"BAT"}
                 ],
                 "commentCount":0,(被评论数)
                 */
                startActivity(intent);
                break;
            case R.id.tutor_file_back_ll:
                finish();
                break;
            case R.id.file_video_play_iv:
                if (TextUtils.isEmpty(mVideoUrl)) {
                    return;
                }

                Uri uri = Uri.parse(mVideoUrl);
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                Log.v("lzm", uri.toString());
                intent2.setDataAndType(uri, "video/mp4");
                startActivity(intent2);
                break;
            case R.id.iv_guanzhu:
                guanzhu();
                break;
            case R.id.tv_hujiao:
                callTutor();
                break;
        }
    }

    private void callTutor()
    {
        CommonUtils.getOrderId(TutorFileActivity.this, mUid, "0", uid, mOrderCallback);
    }
    private Response.Listener<JSONObject> mOrderCallback = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            dismissLoadDialog();
//            L.i("XXXYYY创建订单结果 : response = "+response);
            boolean isOk = GloabalRequestUtil.isRequestOk(response);
            if (isOk) {
                String orderId = GloabalRequestUtil.parseOrderId(response);
                L.i("XXXYYY 创建订单的 oid = "+orderId);
                callVideo(orderId,response);
            } else {
                String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                TipsUtils.showShort(getApplicationContext(), errorMsg);
            }
        }
    };
    /**
     * 呼叫视频
     *
     * @param orderId
     */
    private void callVideo(String orderId,JSONObject response) {
        String channelName = orderId;
        String dynamicKey = GloabalRequestUtil.parseDynamicKey(response);
        String realName = GloabalRequestUtil.parseRealName(response);
        String headImgUrl = GloabalRequestUtil.parseHeadImgUrl(response);
        String company = GloabalRequestUtil.parseCompany(response);
        String title = GloabalRequestUtil.parseTitle(response);
        String targetId = GloabalRequestUtil.parseTargetId(response);

        L.d("VoiceVideoActivity-callTutor");
        Intent intent = new Intent(TutorFileActivity.this,CallingActivity.class);

        intent.putExtra(VoiceVideoActivity.KEY_QUESTION,"");
        intent.putExtra(VoiceVideoActivity.KEY_TYPE,"2");//提问者
        L.i("XXXYYY 传到通话界面的 channelName = "+channelName);
        intent.putExtra(VoiceVideoActivity.KEY_CHANNEL_NAME,channelName);
        intent.putExtra(VoiceVideoActivity.KEY_DYNAMIC_KEY,dynamicKey);
        intent.putExtra(VoiceVideoActivity.KEY_REALNAME,realName);
        intent.putExtra(VoiceVideoActivity.KEY_HEADPATH_URL,headImgUrl);
        intent.putExtra(VoiceVideoActivity.KEY_COMPANY,company);
        intent.putExtra(VoiceVideoActivity.KEY_TITLE,title);
        intent.putExtra(VoiceVideoActivity.KEY_TARGETID,targetId);
        startActivity(intent);
    }
    private void guanzhu()
    {
        if (isFollowed) {
            cancelFollowTutor();
        } else {
            followTutor();
        }
    }

    /**
     * 关注
     */
    private void followTutor() {
        String tag_json_obj = "json_obj_follow_tutor_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("gid", mBussnessId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_FOLLOW_TUTOR, paramObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();
                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    TipsUtils.showShort(getApplicationContext(), "关注成功");
                    isFollowed = true;
                    iv_guanzhu.setImageResource(R.drawable.ic_title_follow_al);
                } else {
                    String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                    TipsUtils.showShort(getApplicationContext(), errorMsg);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    /**
     * 取消关注
     */
    private void cancelFollowTutor() {
        String tag_json_obj = "json_obj_cancel_follow_tutor_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("gid", mBussnessId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_CANCEL_FOLLOW_TUTOR, paramObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();
                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    TipsUtils.showShort(getApplicationContext(), "取消关注");
                    isFollowed = false;
                    iv_guanzhu.setImageResource(R.drawable.ic_title_follow);
                } else {
                    String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                    TipsUtils.showShort(getApplicationContext(), errorMsg);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //评论列表
//    URL_GUIDER_RATED_LIST
}
