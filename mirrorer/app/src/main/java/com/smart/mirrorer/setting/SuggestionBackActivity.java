package com.smart.mirrorer.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iflytek.cloud.Setting;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.base.LoginRegisterChooseActivity;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.LogOutEvent;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.videorecorder.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 2016/4/27.
 */
public class SuggestionBackActivity extends BaseTitleActivity {

    private EditText backEdit;

    private MirrorSettings mSetting;

    private static String SUGGESTION = "1";
    private static String BUG = "2";
    private String type = SUGGESTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_back_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("意见反馈");

        mSetting = BaseApplication.getSettings(this);

        TextView commit = (TextView) findViewById(R.id.tv_commit);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String backText = backEdit.getText().toString();
                if (TextUtils.isEmpty(backText)) {
                    TipsUtils.showShort(getApplicationContext(), "反馈内容不能为空");
                    return;
                }

                JSONObject paramObj = new JSONObject();
                try {
                    paramObj.put("body", backText);
                    paramObj.put("type", type);
                    L.i("调用反馈接口的参数: body = "+backText+",type = "+ type);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mSetting.APP_UID.getValue(), Request.Method.POST,
                        Urls.URL_FEED_BACK, paramObj,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                dismissLoadDialog();
                                boolean isSuccess = GloabalRequestUtil.isRequestOk(response);
                                TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                                if (isSuccess) {
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TipsUtils.showShort(getApplicationContext(), "L.d(失败");
                        dismissLoadDialog();
                    }
                });

                // Adding request to request queue
                BaseApplication.getInstance().addToRequestQueue(jsonObjReq, "feed_back");
            }
        });
//        addRightButtonText("提交", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

        LinearLayout ll_suggestion = (LinearLayout)findViewById(R.id.ll_suggestion);
        LinearLayout ll_bug = (LinearLayout)findViewById(R.id.ll_bug);
        final ImageView iv_suggestion = (ImageView)findViewById(R.id.iv_suggestion);
        final ImageView iv_bug = (ImageView)findViewById(R.id.iv_bug);
        ll_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BUG.equals(type))
                {
                    iv_suggestion.setImageResource(R.drawable.btn_duide);
                    iv_bug.setImageResource(R.drawable.btn_weixuan);
                    type = SUGGESTION;
                }
            }
        });
        ll_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SUGGESTION.equals(type))
                {
                    iv_bug.setImageResource(R.drawable.btn_duide);
                    iv_suggestion.setImageResource(R.drawable.btn_weixuan);
                    type = BUG;
                }
            }
        });
        backEdit = (EditText) findViewById(R.id.feedback_edit);
    }
}
