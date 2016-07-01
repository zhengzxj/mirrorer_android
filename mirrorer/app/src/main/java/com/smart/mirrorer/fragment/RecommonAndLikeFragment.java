package com.smart.mirrorer.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.RecommonLikeFollowAdapter;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseFragment;
import com.smart.mirrorer.bean.home.CommonTutorItemData;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.bean.home.QuestionFocusTutorsBean;
import com.smart.mirrorer.home.PayOrderConfirmAcitivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lzm on 16/4/6.
 */
public class RecommonAndLikeFragment extends BaseFragment {

    public static final String REQUEST_TYPE = "request_type";
    public static final String TYPE_RECOMMON = "1";
    public static final String TYPE_LIKE = "2";

    public static final String REQUEST_QID = "request_qid";
    private String mQId;

    private String mUid;

    private RecommonLikeFollowAdapter mAdapter;

    private BaseActivity baseAct;
    private String mCallId;
    private String mNickName;
    private String mHisNick;

    private  MirrorSettings mSettings;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.baseAct = (BaseActivity) activity;

//        LocalBroadcastManager.getInstance(baseAct.getApplicationContext()).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallTalkingNotification));
//        LocalBroadcastManager.getInstance(baseAct.getApplicationContext()).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallDidTermNotification));
//        LocalBroadcastManager.getInstance(baseAct.getApplicationContext()).registerReceiver(mCallReceiver, new IntentFilter(MtcCallConstants.MtcCallTermedNotification));

        String type = getArguments().getString(REQUEST_TYPE);
        Log.e("lzm", "question_type="+type);
        mQId = getArguments().getString(REQUEST_QID);
        mAdapter = new RecommonLikeFollowAdapter(activity, false);
        mAdapter.setVideoCallListener(new RecommonLikeFollowAdapter.ICallVideoListener() {
            @Override
            public void callVideo(CommonTutorItemData itemData) {
                mCallId = itemData.getUid();
                mHisNick = itemData.getNickName();
                CommonUtils.getOrderId(baseAct, mUid, "0", itemData.getUid(), mOrderCallback);
            }
            public void playVideo(String videoUrl)
            {
                if (TextUtils.isEmpty(videoUrl)) {
                    TipsUtils.showShort(baseAct.getApplicationContext(), "v1/question/inlist 少了个 视频url");
                    return;
                }

                Uri uri = Uri.parse(videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.v("lzm", uri.toString());
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }
        });

        mSettings = BaseApplication.getSettings(activity);
        mUid = mSettings.APP_UID.getValue();
        mNickName = mSettings.USER_NICK.getValue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_listview_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recommonOrLikedRecyclerView = (RecyclerView) getView().findViewById(R.id.fragment_single_listview);
        recommonOrLikedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recommonOrLikedRecyclerView.setAdapter(mAdapter);

        getFollowedTutors();
    }

    private Response.Listener<JSONObject> mOrderCallback = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            baseAct.dismissLoadDialog();

            L.i("XXXYYY创建订单结果 : response = "+response);

            boolean isOk = GloabalRequestUtil.isRequestOk(response);
            if (isOk) {
                String orderId = GloabalRequestUtil.parseOrderId(response);
                callVideo(orderId);
            } else {
                String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                TipsUtils.showShort(baseAct.getApplicationContext(), errorMsg);
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
        mCallOrderId = orderId;
        HashMap<String, String> callParams = new HashMap<>();
        callParams.put("MtcCallInfoServerUserDataKey", orderId);
    }

    /**
     * 获取感谢的回答者列表
     */
    private void getFollowedTutors() {
        if(TextUtils.isEmpty(mQId)) {
            return;
        }

        showLoadingDialog();

        String tag_json_obj = "json_obj_tutor_followed_list_req";
        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("qid", mQId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_QUESTION_FOLLOW_TUTOR_LIST, paramObj, mTutorsCallback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDialog();
                TipsUtils.showShort(getActivity().getApplicationContext(), error.getMessage());
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private GsonCallbackListener<QuestionFocusTutorsBean> mTutorsCallback = new GsonCallbackListener<QuestionFocusTutorsBean>() {

        @Override
        public void onResultSuccess(QuestionFocusTutorsBean questionFocusTutorsBean) {
            super.onResultSuccess(questionFocusTutorsBean);
            dismissLoadingDialog();

            if(questionFocusTutorsBean == null) {
                return;
            }

            List<CommonTutorItemData> dataList = questionFocusTutorsBean.getResult();
            if(dataList == null || dataList.isEmpty()) {
                return;
            }


            mAdapter.setListData(dataList);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadingDialog();
            TipsUtils.showShort(getActivity().getApplicationContext(), errorMsg);
        }
    };

    private boolean isCallConnected;

//    private BroadcastReceiver mCallReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            String outFile = AppFilePath.getJusVideoRecordFile(System.currentTimeMillis() + FileUtils.FILE_FORMAT).getAbsolutePath();
//            Log.e("lzm", "outFile=" + outFile);
//
//            if (MtcCallConstants.MtcCallTalkingNotification.equals(action)) { //通话接通
//                mSettings.APP_IS_UN_PAY.setValue(true);
//                isCallConnected = true;
//            } else if (MtcCallConstants.MtcCallDidTermNotification.equals(action)) {
//                //通话结束事件，主动挂断
//                if(isCallConnected) {
//                    getOrderDetails();
//                }
//                isCallConnected = false;
//            } else if (MtcCallConstants.MtcCallTermedNotification.equals(action)) {
//                // 通话结束事件，被动挂断
//                if(isCallConnected) {
//                    getOrderDetails();
//                }
//                isCallConnected = false;
//            }
//
//        }
//    };

    private void getOrderDetails() {
        if (TextUtils.isEmpty(mCallOrderId)) {
            return;
        }

        CommonUtils.getOrderDetails(baseAct, mUid, mCallOrderId, "1", mOrderDetailCallback);
    }


    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            L.i("XXXYYY获取订单详情返回:"+orderDetailsBean);
            baseAct.dismissLoadDialog();
            if(orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if(detailData == null) {
                return;
            }

            L.i("XXXYYY,PayOrderConfirmAcitivity 订单页面暂时屏蔽 订单号为:"+detailData.getOrderId());
            Intent payIntent = new Intent(getActivity(), PayOrderConfirmAcitivity.class);
            payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, detailData);
            L.i("弹出订单页面");
            startActivity(payIntent);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            baseAct.dismissLoadDialog();
            TipsUtils.showShort(baseAct.getApplicationContext(), errorMsg);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocalBroadcastManager.getInstance(baseAct.getApplicationContext()).unregisterReceiver(mCallReceiver);
    }
}
