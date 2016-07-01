package com.smart.mirrorer.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.KeyboardService;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.videorecorder.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 未匹配到合适回答者
 */
public class NoTutorVoiceVideoActivity extends BaseActivity{

    public static final String KEY_Q_ID = "key_q_id";
    public static final String KEY_Q_DESC = "key_q_desc";
    private String mQid;
    private String mQDesc;
    private MirrorSettings mSettings;
    private String mUid;
    private EditText ev_mQdesc_no_tutor;
//    private TextView tv_mQdesc_no_tutor;

    private KeyboardService mKeyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_tutor_voice_video);

        mKeyService = new KeyboardService(this);

        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();

        mQid = getIntent().getStringExtra("mQid");
        mQDesc = getIntent().getStringExtra("mQDesc");

        //提交
        ImageView iv_reload_no_tutor = (ImageView)findViewById(R.id.iv_reload_no_tutor);
        iv_reload_no_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reload();
            }
        });
        //取消
        ImageView iv_cancle_no_tutor = (ImageView)findViewById(R.id.iv_cancle_no_tutor);
        iv_cancle_no_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ev_mQdesc_no_tutor = (EditText)findViewById(R.id.ev_mQdesc_no_tutor);
        ev_mQdesc_no_tutor.setText(mQDesc);
        if(!TextUtils.isEmpty(mQDesc))
        ev_mQdesc_no_tutor.setSelection(mQDesc.length());

        //编辑
        ImageView iv_edit_no_tutor = (ImageView)findViewById(R.id.iv_edit_no_tutor);
        iv_edit_no_tutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQDesc = ev_mQdesc_no_tutor.getText().toString();
                if (!TextUtils.isEmpty(mQDesc)) {
                    ev_mQdesc_no_tutor.setText(mQDesc);
                    ev_mQdesc_no_tutor.setSelection(mQDesc.length());
                }
                mKeyService.showKeyboard(ev_mQdesc_no_tutor);
            }
        });
        ev_mQdesc_no_tutor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mQDesc = ev_mQdesc_no_tutor.getText().toString();
            }
        });

    }

    /**
     * 重新提问
     */
    private void reload()
    {
        Log.i("lzm","OutOfTimeVoiceVideoActivity:mQid = "+mQid);
        Log.i("lzm","OutOfTimeVoiceVideoActivity:mQDesc = "+mQDesc);
        matchTutor();
    }
    private void matchTutor() {

        String tag_json_obj = "json_obj_tutor_match_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("qid", mQid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_TUTOR_MATCH, paramObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                dismissLoadDialog();

                android.util.Log.e("lzm", "match_id_text=" + response.toString());

                boolean isOk = GloabalRequestUtil.isRequestOk(response);
                if (isOk) {
                    String qid = GloabalRequestUtil.getQId(response);
                    if (TextUtils.isEmpty(qid)) {
                        TipsUtils.showShort(getApplicationContext(), "没有返回qid");
                        return;
                    }

                    Intent intent = new Intent(NoTutorVoiceVideoActivity.this,VoiceVideoActivity.class);
                    intent.putExtra(KEY_Q_ID,mQid);
                    intent.putExtra(KEY_Q_DESC,mQDesc);
                    NoTutorVoiceVideoActivity.this.startActivity(intent);
                    finish();
                } else {
                    TipsUtils.showShort(getApplicationContext(), GloabalRequestUtil.getNetErrorMsg(response));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                android.util.Log.e("lzm", "error:" + error.getMessage());
//                VolleyLog.d("lzm", "Error: " + error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
