package com.smart.mirrorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.bean.home.OrderListBean;
import com.smart.mirrorer.bean.home.QuestionListBean;
import com.smart.mirrorer.home.PayOrderConfirmAcitivity;
import com.smart.mirrorer.home.VoiceVideoActivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.CornerImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lzm on 16/3/25.
 */
public class QustionListAdapter extends RecyclerView.Adapter {
    private String mUid;
    private MirrorSettings mSettings;
    private List<QuestionListBean.ResultBean.ListBean> mListDatas;
    private Context context;
    private BaseActivity activity;
    public void setListData(List<QuestionListBean.ResultBean.ListBean> listDatas, Context context) {
        this.context = context;
        activity = (BaseActivity)context;
        this.mListDatas = listDatas;
        notifyDataSetChanged();
        mSettings = BaseApplication.getSettings(activity);
        mUid = mSettings.APP_UID.getValue();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView question_content_tv;
        TextView question_time_tv;
        TextView question_video_icon;


        public ViewHolder(View view) {
            super(view);
            question_content_tv = (TextView) view.findViewById(R.id.question_content_tv);
            question_time_tv = (TextView) view.findViewById(R.id.question_time_tv);
            question_video_icon = (TextView)view.findViewById(R.id.question_video_icon);
            question_video_icon.setText("呼叫");
            question_video_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    matchTutor(mListDatas.get(getAdapterPosition()).getContent());
                }
            });
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_question_list_cell, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void matchTutor(String resultText) {

        String tag_json_obj = "json_obj_tutor_match_req";
        activity.showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("question", resultText);
            paramObj.put("qid", "0");
            L.i("呼叫:question = "+resultText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_MATCH, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                activity.dismissLoadDialog();

                Log.e("lzm", "text="+response.toString());

                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    String qid = GloabalRequestUtil.getQId(response);
                    if(TextUtils.isEmpty(qid)) {
                        TipsUtils.showShort(activity.getApplicationContext(), "没有返回qid");
                        return;
                    }

                    Intent intent = new Intent(activity, VoiceVideoActivity.class);
                    intent.putExtra(VoiceVideoActivity.KEY_Q_ID, qid);
                    activity.startActivity(intent);
                } else {
                    TipsUtils.showShort(activity.getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(activity.getApplicationContext(), error.getMessage());
                activity.dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;

        QuestionListBean.ResultBean.ListBean resultBean = mListDatas.get(position);
//            holder.tv.setText(eval.get(position));
        viewHolder.question_content_tv.setText(resultBean.getContent());
        viewHolder.question_time_tv.setText(resultBean.getCreateAt());


    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }
}
