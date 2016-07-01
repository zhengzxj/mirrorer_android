package com.smart.mirrorer.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 选择超时
 */
public class OutOfTimeVoiceVideoActivity extends BaseActivity {

    public static final String KEY_Q_ID = "key_q_id";
    public static final String KEY_Q_DESC = "key_q_desc";
    private String mQid;
    private String mQDesc;
    private Intent intent;
    private MirrorSettings mSettings;
    private String mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_of_time_voice_video);

        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();

        intent = getIntent();
        mQid = intent.getStringExtra("mQid");
        mQDesc = intent.getStringExtra("mQDesc");

        //提交
        ImageView iv_reload_out_time = (ImageView)findViewById(R.id.iv_reload_out_time);
        iv_reload_out_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(VoiceVideoActivity.KEY_REQUEST,VoiceVideoActivity.VALUE_RELOAD);
                setResult(0, intent);
                finish();
            }
        });
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(0, intent);
            finish();
        }
        return false;
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

                    Intent intent = new Intent(OutOfTimeVoiceVideoActivity.this,VoiceVideoActivity.class);
                    intent.putExtra(KEY_Q_ID,mQid);
                    intent.putExtra(KEY_Q_DESC,mQDesc);
                    OutOfTimeVoiceVideoActivity.this.startActivity(intent);
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
