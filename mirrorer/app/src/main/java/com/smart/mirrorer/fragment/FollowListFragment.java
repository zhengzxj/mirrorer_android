package com.smart.mirrorer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.OrderListAdapter;
import com.smart.mirrorer.adapter.RecommonLikeFollowAdapter;
import com.smart.mirrorer.agora.CallingActivity;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.CommonTutorItemData;
import com.smart.mirrorer.bean.home.QuestionFocusTutorsBean;
import com.smart.mirrorer.home.GuangZhuActivity;
import com.smart.mirrorer.home.VoiceVideoActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class FollowListFragment extends Fragment {
    private static final String KEY_CONTENT = "FollowListFragment:Content";
    private static final String KEY_PAGE = "FollowListFragment:page";
    private static final String KEY_CANREQUEST = "FollowListFragment:canRequest";
    private String mUid;
    private MirrorSettings mSettings;
    private RecommonLikeFollowAdapter mAdapter;
    private String mNickName;
    public static FollowListFragment newInstance(int type) {
        FollowListFragment fragment = new FollowListFragment();
        fragment.type = type;
        return fragment;
    }

    private ArrayList<CommonTutorItemData> mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getParcelableArrayList(KEY_CONTENT);
            page = savedInstanceState.getInt(KEY_PAGE);
            canRequest = savedInstanceState.getBoolean(KEY_CANREQUEST);
        }

        mSettings = BaseApplication.getSettings(getActivity());
        mUid = mSettings.APP_UID.getValue();
        mNickName = mSettings.USER_NICK.getValue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_follow_list, null);
    }

    private String mCallId;
    private String mHisNick;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView followListview = (RecyclerView) view.findViewById(R.id.rv_follow_list);
        followListview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecommonLikeFollowAdapter(getActivity(), true);
        mAdapter.setVideoCallListener(new RecommonLikeFollowAdapter.ICallVideoListener() {
            @Override
            public void callVideo(CommonTutorItemData itemData) {
                mCallId = itemData.getUid();
                mHisNick = itemData.getNickName();
                CommonUtils.getOrderId((BaseActivity)getActivity(), mUid, "0", itemData.getUid(), mOrderCallback);
            }
            public void playVideo(String videoUrl)
            {
                if (TextUtils.isEmpty(videoUrl)) {
                    TipsUtils.showShort(getActivity().getApplicationContext(), "暂无视频介绍");
                    return;
                }

                Uri uri = Uri.parse(videoUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.v("lzm", uri.toString());
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }
        });
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        followListview.setLayoutManager(llm);
        followListview.setAdapter(mAdapter);
        followListview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible  = llm.getChildCount();
//                L.e("关注列表,visible = "+visible);
                int total = llm.getItemCount();
//                L.e("关注列表,total = "+total);
                int past=llm.findFirstCompletelyVisibleItemPosition();
//                L.e("关注列表,past = "+past);
                if ((visible + past) >= total && canRequest){
//                    L.e("关注列表,到底 page++ = "+(page+1));
                    page++;
                    getFollowedList();
                }
            }
        });
        if(mContent!=null)mAdapter.setListData(mContent);
        if(canRequest)getFollowedList();

    }
    //============获取信息开始=========
    private int type;
    private int page;
    private static final int LIMIT = 10;
    private boolean canRequest = true;
    private void getFollowedList() {
        canRequest = false;
        String tag_json_obj = "json_obj_followed_list_req";
        ((BaseActivity)getActivity()).showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("type",type);
            paramObj.put("page",page);
//            L.e("关注列表,当前亲求数据的page = "+page);
            paramObj.put("limit",LIMIT);
        } catch (JSONException e) {e.printStackTrace();}
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_FOLLOWED_TUTOR_LIST, paramObj, mFollowedListCallback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(((BaseActivity)getActivity()).getApplicationContext(), error.getMessage());
                ((BaseActivity)getActivity()).dismissLoadDialog();
                canRequest = true;
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private GsonCallbackListener<QuestionFocusTutorsBean> mFollowedListCallback = new GsonCallbackListener<QuestionFocusTutorsBean>() {
        @Override
        public void onResultSuccess(QuestionFocusTutorsBean questionFocusTutorsBean) {
            super.onResultSuccess(questionFocusTutorsBean);
            L.e("导师关注列表 = "+questionFocusTutorsBean);
            ((BaseActivity)getActivity()).dismissLoadDialog();
            if (questionFocusTutorsBean == null) {
                return;
            }
            ArrayList<CommonTutorItemData> datas = (ArrayList<CommonTutorItemData>)questionFocusTutorsBean.getResult();
            canRequest = datas.size()<LIMIT?false:true;
            if (mContent == null)mContent = new ArrayList<CommonTutorItemData>();
            mContent.addAll(datas);
            mAdapter.setListData(mContent);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            TipsUtils.showShort(((BaseActivity)getActivity()).getApplicationContext(), errorMsg);
            ((BaseActivity)getActivity()).dismissLoadDialog();
        }
    };
    //============获取信息结束=========
    //============拨打开始=========
    private Response.Listener<JSONObject> mOrderCallback = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            ((GuangZhuActivity)getActivity()).dismissLoadDialog();
//            L.i("XXXYYY创建订单结果 : response = "+response);

            boolean isOk = GloabalRequestUtil.isRequestOk(response);
            if (isOk) {
                String orderId = GloabalRequestUtil.parseOrderId(response);
                L.i("XXXYYY 创建订单的 oid = "+orderId);
                callVideo(orderId,response);
            } else {
                String errorMsg = GloabalRequestUtil.getNetErrorMsg(response);
                TipsUtils.showShort(((GuangZhuActivity)getActivity()).getApplicationContext(), errorMsg);
            }
        }
    };

    private String mCallOrderId;
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
        Intent intent = new Intent(getActivity(),CallingActivity.class);

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
    //============拨打结束===============
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_CONTENT,mContent);
        outState.putInt(KEY_PAGE,page);
        outState.putBoolean(KEY_CANREQUEST,canRequest);
    }
}
