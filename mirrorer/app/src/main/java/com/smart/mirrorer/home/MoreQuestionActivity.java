package com.smart.mirrorer.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.HomeQuestionAdapter;
import com.smart.mirrorer.adapter.MoreQuestionAdapter;
import com.smart.mirrorer.adapter.OrderListAdapter;
import com.smart.mirrorer.adapter.QustionListAdapter;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.bean.home.OrderListBean;
import com.smart.mirrorer.bean.home.QuestionListBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.RecyclerViewDivider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzm on 16/3/26.
 */
public class MoreQuestionActivity extends BaseTitleActivity {

    private String mUid;
    private List<OrderListBean.ResultBean> mHistoryOrderList = new ArrayList<OrderListBean.ResultBean>();
    private MirrorSettings mSettings;

    private MoreQuestionAdapter mQuestionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle(R.string.navigation_history_text);

        mSettings = new MirrorSettings(this);
        mUid = mSettings.APP_UID.getValue();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getQuestionData();
    }

    private void initView() {

//        RecyclerView historyListview = (RecyclerView) findViewById(R.id.history_recycle_view);
//        historyListview.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.VERTICAL));
//        final LinearLayoutManager llm = new LinearLayoutManager(this);
//        historyListview.setLayoutManager(llm);
//        mAdapter = new QustionListAdapter();
//        historyListview.setAdapter(mAdapter);
        SwipeMenuListView swipQuestionView = (SwipeMenuListView) findViewById(R.id.history_recycle_view);
        mQuestionAdapter = new MoreQuestionAdapter(this, R.layout.home_question_list_cell);

        //---------历史问题---改成了直接提问(qid传0)--------
        mQuestionAdapter.setCallListener(new MoreQuestionAdapter.ICallHistoryListener() {
            @Override
            public void matchHisitory(QuestionListBean.ResultBean.ListBean itemData) {
                if (itemData != null)
                    if (!isClickAready)
                    {
                        isClickAready = true;
                        matchTutor(itemData.getQid(),itemData.getContent());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}finally {isClickAready = false;}}}).start();
                    }

            }
        });
        //---------删除问题-----------
        swipQuestionView.setAdapter(mQuestionAdapter);
        swipQuestionView.setMenuCreator(mQuestionAdapter);
        swipQuestionView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        swipQuestionView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index==0)
                    deleteQuestion(position);
                return false;//关闭菜单
            }
        });
    }

    private boolean isClickAready;
    private boolean canRequest = true;
    private int page = 1;


    private ArrayList<OrderListBean.ResultBean> orderList = new ArrayList<OrderListBean.ResultBean>();

    private void getQuestionData() {
        L.d("getQuestionData");
        if (TextUtils.isEmpty(mUid)) {return;}
        String tag_json_obj = "json_obj_home_question_req";
        showLoadDialog();
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("uid", mUid);} catch (JSONException e) {e.printStackTrace();}
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_HOME_QUESTION, paramObj, mQuestionCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.d( "error:" + error.getMessage());
                dismissLoadDialog();
            }
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    private GsonCallbackListener<QuestionListBean> mQuestionCallBack = new GsonCallbackListener<QuestionListBean>() {
        @Override
        public void onResultSuccess(QuestionListBean questionListBean) {
            super.onResultSuccess(questionListBean);
            dismissLoadDialog();
            if (questionListBean != null&&questionListBean.getResult()!=null)
            {
                mQuestionAdapter.setData(questionListBean.getResult().getList());
            }
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
        }
    };
    private void matchTutor(String qid,String resultText) {

        String tag_json_obj = "json_obj_tutor_match_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("question", resultText);

            paramObj.put("qid",qid);
            L.i("呼叫:question = "+resultText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_MATCH, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();

                Log.e("lzm", "text="+response.toString());

                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    String qid = GloabalRequestUtil.getQId(response);
                    if(TextUtils.isEmpty(qid)) {
                        TipsUtils.showShort(getApplicationContext().getApplicationContext(), "没有返回qid");
                        return;
                    }

                    Intent intent = new Intent(MoreQuestionActivity.this, VoiceVideoActivity.class);
                    intent.putExtra(VoiceVideoActivity.KEY_Q_ID, qid);
                    startActivity(intent);
                } else {
                    TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
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

    private void deleteQuestion(int position) {
        L.d("deleteQuestion");

        if (mQuestionAdapter == null) {return;}
        final QuestionListBean.ResultBean.ListBean itemData = mQuestionAdapter.getItem(position);
        if (itemData == null) {return;}
        String qid = itemData.getQid();
        if (TextUtils.isEmpty(qid)) {return;}

        showLoadDialog();

        String tag_json_obj = "json_obj_delete_question_req";
        JSONObject paramObj = new JSONObject();
        try {paramObj.put("qid", qid);} catch (JSONException e) {e.printStackTrace();}

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_HOME_QUESTION_DELETE, paramObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d( "delet_quesiton res=" + response);
                dismissLoadDialog();
                boolean isRequestOk = GloabalRequestUtil.isRequestOk(response);
                if (isRequestOk) {
                    mQuestionAdapter.remove(itemData);
                    mQuestionAdapter.notifyDataSetChanged();
                } else {TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {dismissLoadDialog();}
        });
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
}
