package com.smart.mirrorer.wallet;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.smart.mirrorer.view.UIItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/3/26.
 */
public class WalletActivity extends BaseTitleActivity {
    private UIItem mYueItem;

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle(R.string.navigation_wallet_text);

        MirrorSettings settings = BaseApplication.getSettings(this);
        mUid = settings.APP_UID.getValue();

        mYueItem = (UIItem) findViewById(R.id.wallet_yue_item);
        mYueItem.getRightTextView().setText("0.00元");
        mYueItem.getRightTextView().setTextColor(Color.parseColor("#ff7500"));
        mYueItem.getArrowIv().setVisibility(View.INVISIBLE);

        getUserMoney();
    }

    /**
     * 获取余额
     */
    private void getUserMoney() {
        if(TextUtils.isEmpty(mUid)) {
            TipsUtils.showShort(getApplicationContext(), "用户数据异常");
            return;
        }

        String tag_json_obj = "json_obj_money_req";
        showLoadDialog();

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_MONEY_GET, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissLoadDialog();
                        boolean isSuccess = GloabalRequestUtil.isRequestOk(response);
                        if (isSuccess) {
                            String banlance = GloabalRequestUtil.getBanlance(response);
                            Log.e("lzm", "banlance="+banlance);
                            mYueItem.getRightTextView().setText(banlance+"元");
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
}
