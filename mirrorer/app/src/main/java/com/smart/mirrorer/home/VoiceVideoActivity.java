package com.smart.mirrorer.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.smart.mirrorer.R;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.DBHistoryData;
import com.smart.mirrorer.bean.PushTutoBean;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.db.VideoHistoryProviderService;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.CallConnectEvent;
import com.smart.mirrorer.event.PushTutorInfoMsgEvent;
import com.smart.mirrorer.event.ReLoadQuestionEvent;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.AppFilePath;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.videorecorder.util.FileUtils;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/30.
 */
public class VoiceVideoActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_REQUEST = "key_request";
    public static final String VALUE_RELOAD = "value_reload";//重新提交
    public static final String KEY_Q_ID = "key_q_id";
    public static final String KEY_Q_DESC = "key_q_desc";
    public static final String KEY_CHANNEL_NAME = "KEY_CHANNEL_NAME";
    public static final String KEY_DYNAMIC_KEY = "KEY_DYNAMIC_KEY";
    public static final String KEY_REALNAME = "KEY_REALNAME";
    public static final String KEY_HEADPATH_URL = "KEY_HEADPATH_URL";
    public static final String KEY_TARGETID = "KEY_TARGETID";
    public static final String KEY_COMPANY = "KEY_COMPANY";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_ACTION = "KEY_ACTION";
    public static final String KEY_QUESTION = "KEY_QUESTION";
    public static final String KEY_TYPE = "KEY_TYPE";

    private boolean isStop;


    private ArrayList<PushTutoBean> mactchItems = new ArrayList<>();

    private LinearLayout mProgressLayout;
    private TextView mProgressTimerTv;
    private CountDownTimer mProgressTimer;

    private RelativeLayout mFistLl;
    private RelativeLayout mSecondLl;
    private RelativeLayout mThirdLl;

    private CircleImageView mFirstHeadIv;
    private TextView mFirstNickTv;
    private TextView mFirstCompanyTv;
    private TextView mFirstTitleTv;
    private TextView mFirstStarCountTv;
    private TextView mFirstFiveTv;
    private TextView mFirstMinuteTv;

    private CircleImageView mSecondHeadIv;
    private TextView mSecondNickTv;
    private TextView mSecondCompanyTv;
    private TextView mSecondTitleTv;
    private TextView mSecondStarCountTv;
    private TextView mSecondFiveTv;
    private TextView mSecondMinuteTv;

    private CircleImageView mThirdHeadIv;
    private TextView mThirdNickTv;
    private TextView mThirdCompanyTv;
    private TextView mThirdTitleTv;
    private TextView mThirdStarCountTv;
    private TextView mThirdFiveTv;
    private TextView mThirdMinuteTv;

    //回答者描述
    private TextView tv_tutor_desc_first;
    private TextView tv_tutor_desc_second;
    private TextView tv_tutor_desc_third;

//    private ImageView iv_click_more_info_first;
//    private ImageView iv_click_more_info_second;
//    private ImageView iv_click_more_info_third;

    private TextView tv_match_count;

    private String mDisplyMyText;

    private String mUid;
    private String mQid;

    //插入历史纪录用
    private String mQDesc;
    private String outFileUri;
    private String tutorNick;
    private String tutorHead;
    private boolean insertIntoDB;

    //开关回答者描述开关
    private boolean isGoneFirst = true;
    private boolean isGoneSecond = true;
    private boolean isGoneThird = true;

    private static final int OUT_OF_TIME = 0;//60时间到
    private static final int NO_TUTOR = 1;//没有匹配到回答者
    private static final int INPUT_BACK = 2;//手动退出

    //回答者个数
    private int tutorCount;

    //第一个回答者推过来时剩余时间/毫秒
    private long leftTime;

    //是否已经有回答者被推进来
    private boolean isAnyTutor;

    //取消模式
    private int cancleType;
    /**
     private int price;
     private int time;
     private String timeFormat;
     */

    private boolean isFinishCall;
    private MirrorSettings mSettings;

    //标题
    private TextView tv_voice_result_title;

    private ImageView cancelIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d("VoiceVideoActivity-onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_result_layout2);
        BusProvider.getInstance().register(this);
        initView();

//        LocalBroadcastManager.getInstance(this).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallTalkingNotification));
//        LocalBroadcastManager.getInstance(this).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallDidTermNotification));
//        LocalBroadcastManager.getInstance(this).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallTermedNotification));

        mSettings = new MirrorSettings(this);
        mDisplyMyText = mSettings.USER_NICK.getValue();
        mUid = mSettings.APP_UID.getValue();
        startTimerDown();

        Bundle bundle = getIntent().getExtras();
        mQid = bundle.getString(KEY_Q_ID);
        mQDesc = bundle.getString(KEY_Q_DESC);
        if (TextUtils.isEmpty(mQid)) {
            TipsUtils.showShort(getApplicationContext(), "qid进来的是null，没办法取消");
        }
//        mactchItems = bundle.getParcelableArrayList(KEY_MATCH_LIST);
//        if(mactchItems != null && !mactchItems.isEmpty()) {
//            updateUI();
//        }
    }

    private int mSize;
    private String firstUid;

    private String secondUid;

    private String thirdUid;

    private void updateUI() {
        L.d("VoiceVideoActivity-updateUI");
        tv_voice_result_title.setText("已有回答者接单 请选择");
        if (mactchItems.isEmpty()) {
            return;
        }
        isAnyTutor = true;
//        mProgressLayout.setVisibility(View.GONE);
//        mProgressTimer.cancel();
        mFistLl.setVisibility(View.VISIBLE);
        mSize = mactchItems.size();

        tv_match_count.setText("已为您匹配到1位回答者 (1/3)");
        tutorCount = 1;

        switch (mSize) {
            case 2:
                mSecondLl.setVisibility(View.VISIBLE);
                tv_match_count.setText("已为您匹配到2位回答者 (2/3)");
                tutorCount = 2;
                break;
            case 3:
                mSecondLl.setVisibility(View.VISIBLE);
                mThirdLl.setVisibility(View.VISIBLE);
                tv_match_count.setText("已为您匹配到3位回答者 (3/3)");
                tutorCount = 3;
                break;
        }

        for (int i = 0; i < mSize; i++) {
            PushTutoBean itemData = mactchItems.get(i);
            L.d( "id=" + itemData.getUid());
            if (i == 0) {
                firstUid = itemData.getUid();
                updateTopUI(itemData);
            } else if (i == 1) {
                secondUid = itemData.getUid();
                updateLeftUI(itemData);
            } else if (i == 2) {
                thirdUid = itemData.getUid();
                updateRightUI(itemData);
            }
        }
    }

    private String mTopCallNick;
    private String mLeftCallNick;
    private String mRightCallNick;

    private void updateTopUI(PushTutoBean itemData) {
        L.d("VoiceVideoActivity-updateTopUI");
        String hearUrl = itemData.getIcon();
        if (!TextUtils.isEmpty(hearUrl)) {
            BaseApplication.mImageLoader.displayImage(hearUrl, mFirstHeadIv, BaseApplication.headOptions);
        }

        mTopCallNick = itemData.getNick();
        mFirstNickTv.setText(itemData.getNick());
        mFirstCompanyTv.setText(itemData.getCompany());
        mFirstTitleTv.setText(itemData.getTitle());
        mFirstStarCountTv.setText(itemData.getStar() + "");
        mFirstFiveTv.setText(getString(R.string.tutor_five_minute_price, itemData.getStartPrice() + ""));
        mFirstMinuteTv.setText(getString(R.string.tutor_minute_price, itemData.getMinutePrice() + ""));
    }

    private void updateLeftUI(PushTutoBean itemData) {
        L.d("VoiceVideoActivity-updateLeftUI");
        String hearUrl = itemData.getIcon();
        if (!TextUtils.isEmpty(hearUrl)) {
            BaseApplication.mImageLoader.displayImage(hearUrl, mSecondHeadIv, BaseApplication.headOptions);
        }
        mLeftCallNick = itemData.getNick();
        L.d("tv = "+mSecondNickTv);
        L.d("itemData.getNick() = "+itemData.getNick());
        mSecondNickTv.setText(itemData.getNick());
        mSecondCompanyTv.setText(itemData.getCompany());
        mSecondTitleTv.setText(itemData.getTitle());
        mSecondStarCountTv.setText(itemData.getStar() + "");
        mSecondFiveTv.setText(getString(R.string.tutor_five_minute_price, itemData.getStartPrice() + ""));
        mSecondMinuteTv.setText(getString(R.string.tutor_minute_price, itemData.getMinutePrice() + ""));
    }

    private void updateRightUI(PushTutoBean itemData) {
        L.d("VoiceVideoActivity-updateRightUI");
        String hearUrl = itemData.getIcon();
        if (!TextUtils.isEmpty(hearUrl)) {
            BaseApplication.mImageLoader.displayImage(hearUrl, mThirdHeadIv, BaseApplication.headOptions);
        }
        mRightCallNick = itemData.getNick();
        mThirdNickTv.setText(itemData.getNick());
        mThirdCompanyTv.setText(itemData.getCompany());
        mThirdTitleTv.setText(itemData.getTitle());
        mThirdStarCountTv.setText(itemData.getStar() + "");
        mThirdFiveTv.setText(getString(R.string.tutor_five_minute_price, itemData.getStartPrice() + ""));
        mThirdMinuteTv.setText(getString(R.string.tutor_minute_price, itemData.getMinutePrice() + ""));
    }

    private boolean isTimeOver;

    /**
     * 开始倒计时
     */
    private void startTimerDown() {
        L.d("VoiceVideoActivity-startTimerDown");
        if (mProgressTimer == null) {
            mProgressTimer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long l) {
//                    if(l/1000>10)//还没有回答者
//                    {
                    mProgressTimerTv.setText(getResources().getString(R.string.app_senconds_text, (l / 1000) + ""));
//                        L.d("l = "+l);
//                    }else if(isAnyTutor)
//                    {
//                        TipsUtils.showShort(BaseApplication.getInstance().getApplicationContext(),"最后10秒关闭自己,放出二级倒计时对话框");
//                        mProgressTimer.cancel();
//                        //最后10秒关闭自己,放出二级倒计时对话框
//
//                    }
                }

                @Override
                public void onFinish() {
                    isTimeOver = true;
                    if(isStop)return;//界面切换到了通话界面
                    if (tutorCount==0)
                    {
                        cancelCall(NO_TUTOR);
                    }else
                    {
                        cancelCall(OUT_OF_TIME);
                    }
                }
            };

        }
        mProgressTimer.start();
    }

    /**
     * 取消呼叫
     */
    private void cancelCall(int cancleType) {
        L.d("VoiceVideoActivity-cancelCall");

        VoiceVideoActivity.this.cancleType = cancleType;

        String tag_json_obj = "json_obj_cancel_call_req";

        if(cancleType!=INPUT_BACK)
            showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("qid", mQid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_CANCEL_CALL, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if(VoiceVideoActivity.this.cancleType!=INPUT_BACK)
                    dismissLoadDialog();

                L.d("XXXYYY订单取消respone = "+response);

                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    L.d("订单取消成功");
                } else {
                    L.d(GloabalRequestUtil.getNetErrorMsg(response));
                }
                mProgressLayout.setVisibility(View.GONE);
                if (VoiceVideoActivity.this.cancleType==INPUT_BACK)finish();
                else if(!isStop&&VoiceVideoActivity.this.cancleType==OUT_OF_TIME)
                {
                    Intent intent = new Intent(VoiceVideoActivity.this,OutOfTimeVoiceVideoActivity.class);
                    intent.putExtra("mQid",mQid);
                    intent.putExtra("mQDesc",mQDesc);
                    VoiceVideoActivity.this.startActivityForResult(intent,0);
                    mProgressTimer.cancel();
                    cancelIv.setVisibility(View.INVISIBLE);
                }else if(!isStop&&VoiceVideoActivity.this.cancleType==NO_TUTOR)
                {
                    Intent intent = new Intent(VoiceVideoActivity.this,NoTutorVoiceVideoActivity.class);
                    intent.putExtra("mQid",mQid);
                    intent.putExtra("mQDesc",mQDesc);
                    VoiceVideoActivity.this.startActivityForResult(intent,0);
                    finish();
                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
                finish();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("VoiceVideoActivity-onActivityResult");
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 0:
                if (data!=null&&VALUE_RELOAD.equals(data.getStringExtra(KEY_REQUEST)))//选择了重新加载问题
                {
                    L.d("OutOfTimeVoiceVideoActivity页面重新提问了");
                    finish();
                    //发event给mainActivity 让它发起提问
                    ReLoadQuestionEvent event = new ReLoadQuestionEvent();
                    event.setmQid(mQid);
                    event.setmQDesc(mQDesc);
                    BusProvider.getInstance().post(event);

                }else
                {
                    L.d("OutOfTimeVoiceVideoActivity页面直接返回了");
                    finish();
                }
//                else if(VALUE_Finish.equals(data.getStringExtra(KEY_REQUEST)))
//                {
//                    finish();
//                }
                break;
            default:
                break;
        }
    }

    private void initView() {
        L.d("VoiceVideoActivity-initView");
        tv_match_count = (TextView)findViewById(R.id.tv_match_count);

        tv_voice_result_title = (TextView)findViewById(R.id.tv_voice_result_title);

        mProgressLayout = (LinearLayout) findViewById(R.id.voice_video_progressbar_layout);
        mProgressTimerTv = (TextView) findViewById(R.id.video_match_timer_tv);

        mFistLl = (RelativeLayout) findViewById(R.id.video_first);
        mSecondLl = (RelativeLayout) findViewById(R.id.video_second);
        mThirdLl = (RelativeLayout) findViewById(R.id.video_third);

        TextView fistConnBottom = (TextView) findViewById(R.id.video_first_connect_bottom);
        TextView secondConnBottom = (TextView) findViewById(R.id.video_second_connect_bottom);
        TextView thirdConnBottom = (TextView) findViewById(R.id.video_third_connect_bottom);
        fistConnBottom.setOnClickListener(this);
        secondConnBottom.setOnClickListener(this);
        thirdConnBottom.setOnClickListener(this);

        mFirstHeadIv = (CircleImageView) findViewById(R.id.video_first_head_iv);
        mFirstNickTv = (TextView) findViewById(R.id.tutor_first_nick_tv);
        mFirstCompanyTv = (TextView) findViewById(R.id.tutor_first_company_tv);
        mFirstTitleTv = (TextView) findViewById(R.id.tutor_first_title_tv);
        mFirstStarCountTv = (TextView) findViewById(R.id.tutor_first_count_tv);
        mFirstFiveTv = (TextView) findViewById(R.id.tutor_first_five_tv);
        mFirstMinuteTv = (TextView) findViewById(R.id.tutor_first_minute_tv);

        mSecondHeadIv = (CircleImageView) findViewById(R.id.video_second_head_iv);
        mSecondNickTv = (TextView) findViewById(R.id.tutor_second_nick_tv);
        mSecondCompanyTv = (TextView) findViewById(R.id.tutor_second_company_tv);
        mSecondTitleTv = (TextView) findViewById(R.id.tutor_second_title_tv);
        mSecondStarCountTv = (TextView) findViewById(R.id.tutor_second_count_tv);
        mSecondFiveTv = (TextView) findViewById(R.id.tutor_second_five_tv);
        mSecondMinuteTv = (TextView) findViewById(R.id.tutor_second_minute_tv);

        mThirdHeadIv = (CircleImageView) findViewById(R.id.video_third_head_iv);
        mThirdNickTv = (TextView) findViewById(R.id.tutor_third_nick_tv);
        mThirdCompanyTv = (TextView) findViewById(R.id.tutor_third_company_tv);
        mThirdTitleTv = (TextView) findViewById(R.id.tutor_third_title_tv);
        mThirdStarCountTv = (TextView) findViewById(R.id.tutor_third_count_tv);
        mThirdFiveTv = (TextView) findViewById(R.id.tutor_third_five_tv);
        mThirdMinuteTv = (TextView) findViewById(R.id.tutor_third_minute_tv);

        tv_tutor_desc_first = (TextView)findViewById(R.id.tv_tutor_desc_first);
        tv_tutor_desc_second = (TextView)findViewById(R.id.tv_tutor_desc_second);
        tv_tutor_desc_third = (TextView)findViewById(R.id.tv_tutor_desc_third);

//        iv_click_more_info_first = (ImageView)findViewById(R.id.iv_click_more_info_first);
//        iv_click_more_info_second = (ImageView)findViewById(R.id.iv_click_more_info_second);
//        iv_click_more_info_third = (ImageView)findViewById(R.id.iv_click_more_info_third);
//
//        iv_click_more_info_first.setOnClickListener(this);
//        iv_click_more_info_second.setOnClickListener(this);
//        iv_click_more_info_third.setOnClickListener(this);

        cancelIv = (ImageView) findViewById(R.id.voice_video_cancle_iv);
        cancelIv.setOnClickListener(this);
    }

    private boolean isClickAready;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.iv_click_more_info_first:
//                L.d("VoiceVideoActivity-click-iv_click_more_info_first");
//                updateTutorDescUi(0);
//                break;
//            case R.id.iv_click_more_info_second:
//                L.d("VoiceVideoActivity-click-iv_click_more_info_second");
//                updateTutorDescUi(1);
//                break;
//            case R.id.iv_click_more_info_third:
//                L.d("VoiceVideoActivity-click-iv_click_more_info_third");
//                updateTutorDescUi(2);
//                break;
            case R.id.video_first_connect_bottom:
                L.d("VoiceVideoActivity-click-video_first_connect_bottom");
                boolean isUnPay = mSettings.APP_IS_UN_PAY.getValue();
//                if (isUnPay) {
//                    TipsUtils.showShort(getApplicationContext(), "您有订单未支付,请支付完成再寻求帮助");
//                    getOrderDetails();
//                    return;
//                }

                tutorNick = mactchItems.get(0).getNick();
                tutorHead = mactchItems.get(0).getIcon();

                clickType = TYPE_TOP;
                if (!isClickAready)
                {
                    isClickAready = true;
                    CommonUtils.getOrderId(VoiceVideoActivity.this, mUid, mQid, firstUid, mOrderCallback);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}finally {isClickAready = false;}}}).start();
                }
                break;
            case R.id.video_second_connect_bottom:
                L.d("VoiceVideoActivity-click-video_second_connect_bottom");
                boolean isLeftUnPay = mSettings.APP_IS_UN_PAY.getValue();
                if (isLeftUnPay) {
                    TipsUtils.showShort(getApplicationContext(), "您有订单未支付，请支付完成再寻求帮助");
                    getOrderDetails();
                    return;
                }

                tutorNick = mactchItems.get(1).getNick();
                tutorHead = mactchItems.get(1).getIcon();

                clickType = TYPE_LEFT;

                CommonUtils.getOrderId(VoiceVideoActivity.this, mUid, mQid, secondUid, mOrderCallback);
                break;
            case R.id.video_third_connect_bottom:
                L.d("VoiceVideoActivity-click-video_third_connect_bottom");
                boolean isRightUnPay = mSettings.APP_IS_UN_PAY.getValue();//TODO:这里要改回来
                if (isRightUnPay) {
                    TipsUtils.showShort(getApplicationContext(), "您有订单未支付，请支付完成再寻求帮助");
                    getOrderDetails();
                    return;
                }

                tutorNick = mactchItems.get(2).getNick();
                tutorHead = mactchItems.get(2).getIcon();

                clickType = TYPE_RIGHT;
                CommonUtils.getOrderId(VoiceVideoActivity.this, mUid, mQid, thirdUid, mOrderCallback);
                break;
            case R.id.voice_video_cancle_iv:
                L.d("VoiceVideoActivity-click-voice_video_cancle_iv");
//                if (isTimeOver || isFinishCall) {
//                    finish();
//                } else {
                cancelCall(INPUT_BACK);
//                }
                break;
        }
    }

    /**
     * 弹出或收起回答者描述UI
     * @param whitchTutor 0:第一个回答者 1:第二个回答者 2:第三个回答者
     */
//    private void updateTutorDescUi(int whitchTutor)
//    {
//        switch (whitchTutor) {
//            case 0:
//                L.d("VoiceVideoActivity-updateTutorDescUi-0");
//                if (isGoneFirst)closeAll();
//                closeFirst();
//                break;
//            case 1:
//                L.d("VoiceVideoActivity-updateTutorDescUi-1");
//                if (isGoneSecond)closeAll();
//                closeSecond();
//                break;
//            case 2:
//                L.d("VoiceVideoActivity-updateTutorDescUi-2");
//                if (isGoneThird)closeAll();
//                closeThird();
//                break;
//        }
//
//    }

//    private void closeFirst()
//    {
//        L.d("VoiceVideoActivity-closeFirst");
//        tv_tutor_desc_first.setVisibility(isGoneFirst?View.VISIBLE:View.GONE);
//        iv_click_more_info_first.setImageResource(isGoneFirst?R.drawable.arrow_up:R.drawable.arrow_down);
//        isGoneFirst = !isGoneFirst;
//    }
//
//    private void closeSecond()
//    {
//        L.d("VoiceVideoActivity-closeSecond");
//        tv_tutor_desc_second.setVisibility(isGoneSecond?View.VISIBLE:View.GONE);
//        iv_click_more_info_second.setImageResource(isGoneSecond?R.drawable.arrow_up:R.drawable.arrow_down);
//        isGoneSecond = !isGoneSecond;
//    }
//
//    private void closeThird()
//    {
//        L.d("VoiceVideoActivity-closeThird");
//        tv_tutor_desc_third.setVisibility(isGoneThird?View.VISIBLE:View.GONE);
//        iv_click_more_info_third.setImageResource(isGoneThird?R.drawable.arrow_up:R.drawable.arrow_down);
//        isGoneThird = !isGoneThird;
//    }
//    private void closeAll()
//    {
//        L.d("VoiceVideoActivity-closeAll");
//        if(!isGoneFirst)closeFirst();
//        if(!isGoneSecond)closeSecond();
//        if(!isGoneThird)closeThird();
//    }

    private String clickType;
    private final String TYPE_TOP = "top";
    private final String TYPE_LEFT = "left";
    private final String TYPE_RIGHT = "right";

    private Response.Listener<JSONObject> mOrderCallback = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            dismissLoadDialog();

            L.d( "XXXYYY创建订单返回的respone=" + response.toString());

            boolean isOk = GloabalRequestUtil.isRequestOk(response);
            if (isOk) {
                String orderId = GloabalRequestUtil.parseOrderId(response);
                L.d("创建订单response: "+response.toString());
                //得到订单id,唤起拨打视频界面
                if (Urls.aMode) {
                    String channelName = orderId;
                    String dynamicKey = GloabalRequestUtil.parseDynamicKey(response);
                    String realName = GloabalRequestUtil.parseRealName(response);
                    String headImgUrl = GloabalRequestUtil.parseHeadImgUrl(response);
                    String company = GloabalRequestUtil.parseCompany(response);
                    String title = GloabalRequestUtil.parseTitle(response);
                    String targetId = GloabalRequestUtil.parseTargetId(response);

                    callTutor(channelName,dynamicKey,realName,headImgUrl,company,title,targetId);
                    return;
                }
                callVideo(orderId);
            } else {
                String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                TipsUtils.showShort(getApplicationContext(), errorMsg);
            }

        }
    };

    private String mCallOrderId;

    /**
     * 呼叫视频
     *
     * @param orderId
     */
    private void callVideo(String orderId) {
        L.d("VoiceVideoActivity-callVideo");
        mCallOrderId = orderId;
    }

    private int mCallId;

    private boolean isCallConnected;

    private void getOrderDetails() {
        L.d("VoiceVideoActivity-getOrderDetails");
        if (TextUtils.isEmpty(mCallOrderId)) {
            return;
        }

        CommonUtils.getOrderDetails(VoiceVideoActivity.this, mUid, mCallOrderId, "1", mOrderDetailCallback);
    }


    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            L.i("XXXYYY获取订单详情返回:"+orderDetailsBean);
            dismissLoadDialog();
            if (orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if (detailData == null) {
                return;
            }

            L.d("订单详情"+detailData.toString());

            L.i("XXXYYY,PayOrderConfirmAcitivity 订单页面暂时屏蔽 订单号为:"+orderDetailsBean.getResult().getOrderId());
            Intent payIntent = new Intent(VoiceVideoActivity.this, PayOrderConfirmAcitivity.class);
            payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, detailData);
            L.i("弹出订单页面");
            startActivity(payIntent);

            //录制信息本地化
            if(!saveInToDB(detailData))L.d("录制插入数据库失败");
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    @Subscribe
    public void onEventPushTutorMsg(PushTutorInfoMsgEvent event) {
        L.d("onEventPushTutorMsg = "+event.pushTutorInfoMsgObj);
        String msg = event.msgContent;

        String action="";
        String type="";
        try {
            action = event.pushTutorInfoMsgObj.getString("action");
            type = event.pushTutorInfoMsgObj.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(msg)) {
            if("notice-refuse".equals(action)){
                L.d("更改可选回答者状态");
            }else
            {
                updateMatchTutor(msg);
            }
        }
    }
    @Subscribe
    public void onCallConnectEvent(CallConnectEvent event) {
        L.d("VoiceVideoActivity-onCallConnectEvent");
        isCallConnected = true;
        finish();
    }

    private void updateMatchTutor(String msg) {
        L.d("VoiceVideoActivity-updateMatchTutor");
        Gson gson = new Gson();
        PushTutoBean fromJson = gson.fromJson(msg, PushTutoBean.class);
        if (fromJson == null) {
            return;
        }

        mactchItems.add(fromJson);
        updateUI();
    }


    @Override
    protected void onDestroy() {
        L.d("VoiceVideoActivity-onDestroy");
        super.onDestroy();
        if(!isCallConnected)//未接通电话 把关闭当前页面的话 就要调用cancelCall方法
            cancelCall(INPUT_BACK);
        isFinishCall = false;
        isTimeOver = false;
        if (mProgressTimer != null) {
            mProgressTimer.cancel();
        }
        BusProvider.getInstance().unregister(this);
    }

    private VideoHistoryProviderService mVideoHistoryProviderService;

    /**
     * 获取历史纪录数据库操作对象
     *
     * @return 历史纪录dbProvider对象
     */
    private VideoHistoryProviderService getHistoryDBService() {
        L.d("VoiceVideoActivity-getHistoryDBService");
        if (mVideoHistoryProviderService == null) {
            mVideoHistoryProviderService = new VideoHistoryProviderService(getApplicationContext());
        }
        return mVideoHistoryProviderService;
    }

    private DBHistoryData dbHistoryData;
    /**
     * 将录制的视频地址插入到表中
     */
    private boolean saveInToDB(OrderDetailsData detailData)
    {
        L.d("VoiceVideoActivity-saveInToDB");
        if(!insertIntoDB)return false;
        //获取db
        getHistoryDBService();
        //封装录像信息
        initDBHistoryData(detailData);
        //保存录像信息到表
        L.d("存入数据库的时间:"+dbHistoryData.time);
        L.d("存入数据库的金额:"+dbHistoryData.price);
        mVideoHistoryProviderService.saveOrUpdateHistory(dbHistoryData,mQid);
        return true;
    }


    private void initDBHistoryData(OrderDetailsData detailData)
    {
        L.d("VoiceVideoActivity-initDBHistoryData");
        L.d("封装录像信息");
        if(dbHistoryData==null)dbHistoryData = new DBHistoryData();
        //上个页面给的值
        L.d("mQDesc = "+mQDesc);
        dbHistoryData.setQuestionDesc(mQDesc);
        dbHistoryData.setQuestionId(mQid);
        //拨打电话后确定的值
        dbHistoryData.setTutorNick(tutorNick);
        dbHistoryData.setTutorHead(tutorHead);
        //挂断电话后确定的值
        dbHistoryData.setTimeFormat(formatTime());//预留字段 暂时不用
        L.d(dbHistoryData.toString());
        //录制完成确定的值
        dbHistoryData.setTime(CommonUtils.getFormatTime(detailData.getCallTalkDuration()));
        dbHistoryData.setPrice(detailData.getPayMoney());
    }

    private String formatTime()
    {
        L.d("VoiceVideoActivity-formatTime");
        String time;
        //使用默认时区和语言环境获得一个日历
        Calendar cale = Calendar.getInstance();
        //将Calendar类型转换成Date类型
        Date tasktime=cale.getTime();
        //设置日期输出的格式
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //格式化输出
        System.out.println(df.format(tasktime));
        return df.format(tasktime);
    }
    //=============agora=============
    /**
     * 去等待接通界面
     */
    private void callTutor(String channelName, String dynamicKey,String realName,String headImgUrl,String company,String title,String targetId)
    {
        L.d("VoiceVideoActivity-callTutor");
        Intent intent = new Intent(VoiceVideoActivity.this,CallingActivity.class);

        intent.putExtra(KEY_QUESTION,mQDesc);
        intent.putExtra(KEY_TYPE,"2");//提问者
        intent.putExtra(KEY_CHANNEL_NAME,channelName);
        intent.putExtra(KEY_DYNAMIC_KEY,dynamicKey);
        intent.putExtra(KEY_REALNAME,realName);
        intent.putExtra(KEY_HEADPATH_URL,headImgUrl);
        intent.putExtra(KEY_COMPANY,company);
        intent.putExtra(KEY_TITLE,title);
        intent.putExtra(KEY_TARGETID,targetId);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        L.d("VoiceVideoActivity-onPause");
        super.onPause();
        isStop = true;
    }

    @Override
    protected void onResume() {
        L.d("VoiceVideoActivity-onResume");
        super.onResume();
        isStop = false;
    }
}
