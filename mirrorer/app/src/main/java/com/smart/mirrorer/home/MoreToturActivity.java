package com.smart.mirrorer.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.RecommonLikeFollowAdapter;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.bean.home.CommonTutorItemData;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.bean.home.QuestionFocusTutorsBean;
import com.smart.mirrorer.bean.home.RecommonBean;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.CallConnectEvent;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 主页 更多按钮:更多回答者页面
 */
public class MoreToturActivity extends BaseTitleActivity {
    public static final String KEY_IS_FOLLOW = "key_is_follow";

    private RecommonLikeFollowAdapter mAdapter;

    private String mUid;

    private boolean isFollowType; //关注界面

    private MirrorSettings mSettings;
    private String mCallId;
    private String mNickName;
    private String mHisNick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.e("FollowActivity : 进入页面oncReate方法");
//        TipsUtils.showShort(getApplicationContext(),"进入页面oncReate方法");

        setContentView(R.layout.activity_follow_layout);
        setupNavigationBar(R.id.navigation_bar);

        isFollowType = getIntent().getExtras().getBoolean(KEY_IS_FOLLOW);
        if (isFollowType) {
            setCommonTitle(R.string.my_follow_text);
        } else {
            setCommonTitle("推荐回答者");
        }

        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();
        mNickName = mSettings.USER_NICK.getValue();
        initView();

        if (isFollowType) {
            getFollowedList();
        } else {
            getRecommonList();
        }
    }
    private void initView() {
        RecyclerView followListview = (RecyclerView) findViewById(R.id.follow_recycle_view);
        followListview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecommonLikeFollowAdapter(this, isFollowType);
        mAdapter.setVideoCallListener(new RecommonLikeFollowAdapter.ICallVideoListener() {
            @Override
            public void callVideo(CommonTutorItemData itemData) {
                mCallId = itemData.getUid();
                mHisNick = itemData.getNickName();
                CommonUtils.getOrderId(MoreToturActivity.this, mUid, "0", itemData.getUid(), mOrderCallback);
            }
            public void playVideo(String videoUrl)
            {
                if (TextUtils.isEmpty(videoUrl)) {
                    TipsUtils.showShort(MoreToturActivity.this.getApplicationContext(), "暂无视频介绍");
                    return;
                }

                Uri uri = Uri.parse(videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.v("lzm", uri.toString());
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }
        });
        followListview.setAdapter(mAdapter);
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


    private String mCallOrderId;

    /**
     * 呼叫视频
     *
     * @param orderId
     */
    private void callVideo(String orderId,JSONObject response) {
        mCallOrderId = orderId;
        String channelName = orderId;
        String dynamicKey = GloabalRequestUtil.parseDynamicKey(response);
        String realName = GloabalRequestUtil.parseRealName(response);
        String headImgUrl = GloabalRequestUtil.parseHeadImgUrl(response);
        String company = GloabalRequestUtil.parseCompany(response);
        String title = GloabalRequestUtil.parseTitle(response);
        String targetId = GloabalRequestUtil.parseTargetId(response);

        L.d("VoiceVideoActivity-callTutor");
        Intent intent = new Intent(MoreToturActivity.this,CallingActivity.class);

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

    /**
     * 获取推荐回答者
     */
    private void getRecommonList() {
        showLoadDialog();
        String tag_json_obj = "json_obj_home_recommond_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("limit", "4");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_RECOMMOND, paramObj, mRecommondCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadDialog();
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private GsonCallbackListener<RecommonBean> mRecommondCallBack = new GsonCallbackListener<RecommonBean>() {
        @Override
        public void onResultSuccess(RecommonBean recommonBean) {
            super.onResultSuccess(recommonBean);
            dismissLoadDialog();
            if (recommonBean == null) {
                return;
            }

            RecommonBean.ResultBean recommondResult = recommonBean.getResult();
            if (recommondResult == null) {
                return;
            }

            List<CommonTutorItemData> listDatas = recommondResult.getList();
            if (listDatas == null || listDatas.isEmpty()) {
                return;
            }

            mAdapter.setListData(listDatas);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    /**
     * 获取关注列表
     */
    private void getFollowedList() {
        String tag_json_obj = "json_obj_followed_list_req";
        showLoadDialog();

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_FOLLOWED_TUTOR_LIST, null, mFollowedListCallback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private GsonCallbackListener<QuestionFocusTutorsBean> mFollowedListCallback = new GsonCallbackListener<QuestionFocusTutorsBean>() {
        @Override
        public void onResultSuccess(QuestionFocusTutorsBean questionFocusTutorsBean) {
            super.onResultSuccess(questionFocusTutorsBean);
            dismissLoadDialog();

            if (questionFocusTutorsBean == null) {
                return;
            }

            List<CommonTutorItemData> itemList = questionFocusTutorsBean.getResult();
            if (itemList == null || itemList.isEmpty()) {
                return;
            }

            mAdapter.setListData(itemList);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            TipsUtils.showShort(getApplicationContext(), errorMsg);
            dismissLoadDialog();
        }
    };

    private boolean isCallConnected;



    private void getOrderDetails() {
        if (TextUtils.isEmpty(mCallOrderId)) {
            return;
        }

        CommonUtils.getOrderDetails(MoreToturActivity.this, mUid, mCallOrderId, "1", mOrderDetailCallback);
    }


    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            L.i("XXXYYY获取订单详情返回:"+orderDetailsBean);
            dismissLoadDialog();
            if(orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if(detailData == null) {
                return;
            }
            L.i("XXXYYY,PayOrderConfirmAcitivity 订单页面暂时屏蔽 订单号为:"+detailData.getOrderId());
            Intent payIntent = new Intent(MoreToturActivity.this, PayOrderConfirmAcitivity.class);
            payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, detailData);
            L.i("弹出订单页面");
            startActivity(payIntent);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //calling
    @Subscribe
    public void onCallConnectEvent(CallConnectEvent event) {
        L.d("VoiceVideoActivity-onCallConnectEvent");
        isCallConnected = true;
        L.i("XXXYYY APP_IS_UN_PAY SET VALUE = TRUE");
        mSettings.APP_IS_UN_PAY.setValue(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
        if(mSettings.APP_IS_UN_PAY.getValue())
        {
            L.i("onResume APP_IS_UN_PAY = "+mSettings.APP_IS_UN_PAY.getValue());
            getOrderDetails();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        L.i("XXXYYY is_un_pay = "+mSettings.APP_IS_UN_PAY.getValue());
    }
}
