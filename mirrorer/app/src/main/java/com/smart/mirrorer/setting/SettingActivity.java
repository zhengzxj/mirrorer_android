package com.smart.mirrorer.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.base.LoginRegisterChooseActivity;
import com.smart.mirrorer.base.SplashRegisterActivity;
import com.smart.mirrorer.bean.home.ApkUpdateBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.service.MqttService;
import com.smart.mirrorer.util.DataCleanManager;
import com.smart.mirrorer.util.GloabalRequestUtil;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.UpdateAppManager;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.UIItem;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by lzm on 16/3/26.
 */
public class SettingActivity extends BaseTitleActivity implements View.OnClickListener {
    private UpdateAppManager updateManager;
    private int versionCode = -1;
    private MirrorSettings mSettings;
    private UIItem mClearItem;
    private UIItem mUpdateItem;
    private String mCacheSize = "0KB";
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle(R.string.navigation_settings_text);
        mSettings = BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();
        initView();
    }

    private void initView() {
//        UIItem suggestionItem = (UIItem) findViewById(R.id.setting_suggestion_item);
//        suggestionItem.setOnClickListener(this);
        mUpdateItem = (UIItem) findViewById(R.id.setting_auto_update_item);
        mUpdateItem.setOnClickListener(this);

        mClearItem = (UIItem) findViewById(R.id.setting_clear_item);
        mClearItem.setOnClickListener(this);
        mClearItem.getArrowIv().setVisibility(View.INVISIBLE);
        try {
            mCacheSize = DataCleanManager.getTotalCacheSize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mClearItem.setRightText(mCacheSize);

        UIItem aboutItem = (UIItem) findViewById(R.id.setting_about_item);
        aboutItem.setOnClickListener(this);

        RelativeLayout logoutLayout = (RelativeLayout) findViewById(R.id.setting_logout_item);
        logoutLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_auto_update_item:
                autoUpdateApp();
                break;
//            case R.id.setting_suggestion_item:
//                processCleanIntent(SuggestionBackActivity.class);
//                break;
            case R.id.setting_clear_item:
                if ("0KB".equals(mCacheSize)) {
                    TipsUtils.showShort(getApplicationContext(), "手机清洁如新，无需再次清理哦");
                    return;
                }

                DataCleanManager.clearAllCache(this);
                TipsUtils.showShort(getApplicationContext(), "成功清理" + mCacheSize + "缓存");
                mClearItem.setRightText("0KB");
                mCacheSize = "0KB";
                break;
            case R.id.setting_about_item:
                processCleanIntent(AboutActivity.class);
                break;
            case R.id.setting_logout_item:
                logoutApp();
                break;
        }
    }

    //========版本更新 点击按钮用的==============
    public void autoUpdateApp() {
        //初始化当前apk版本号
        updateManager = new UpdateAppManager(SettingActivity.this);
        versionCode = updateManager.getVersionCode();

        if(versionCode==-1)return;

        //访问服务器询问是否有新版本:request
        String tag_json_obj = "apk_update_request";

        L.i("自动更新参数:mUid = "+mUid);
        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_UPDATE, null, mVersionUpdateCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, "tag_json_obj");
    }
    //版本更新:callback
    private GsonCallbackListener<ApkUpdateBean> mVersionUpdateCallBack = new GsonCallbackListener<ApkUpdateBean>() {
        @Override
        public void onResultSuccess(ApkUpdateBean updateBean) {
            super.onResultSuccess(updateBean);

            ApkUpdateBean.ResultBean result = updateBean.getResult();

            if(updateBean==null||result==null)//服务器返回空数据
                return;
            else if(Integer.parseInt(result.getVer())>versionCode)//存在新版本
                updateManager.checkUpdateInfo(result.getUrl(),result.getTitle(),result.getDesc());
            else
            {
                TipsUtils.showShort(SettingActivity.this,"当前已经是最新版本");
            }
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);

        }
    };
    /**
     * 登出app
     */
    private void logoutApp() {
        String uid = mSettings.APP_UID.getValue();
        if (TextUtils.isEmpty(uid)) {
            TipsUtils.showShort(this, "uid为空");
            return;
        }

        String tag_json_obj = "json_obj_logout_req";
        showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(uid, Request.Method.POST,
                Urls.URL_LOGIN_OUT, paramObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dismissLoadDialog();
                        boolean isSuccess = GloabalRequestUtil.isRequestOk(response);
                        if (isSuccess) {
                            mSettings.loginoutUser();
//                            TipsUtils.showShort(getApplicationContext(), "退出登录成功");
                            Intent intent = new Intent(SettingActivity.this, SplashRegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
//                            JPushInterface.stopPush(SettingActivity.this);
                            finish();
                            MqttService.actionStop(SettingActivity.this);
                        } else {
                            TipsUtils.showShort(getApplicationContext(), "退出登录失败");
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), "退出登录失败");
                Log.e("lzm", "error:" + error.getMessage());
                dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
