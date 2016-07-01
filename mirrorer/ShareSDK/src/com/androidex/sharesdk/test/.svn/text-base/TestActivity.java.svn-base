package com.androidex.sharesdk.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidex.sharesdk.OpenAccountManager;
import com.androidex.sharesdk.OpenAccountManager.ReslutCallback;
import com.androidex.sharesdk.R;
import com.androidex.sharesdk.core.Config;
import com.androidex.sharesdk.core.Platform.ShareParams;
import com.androidex.sharesdk.core.PlatformEntity;

public class TestActivity extends Activity {
    TextView authInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        authInfoText = (TextView) findViewById(R.id.auth_info_text);
        final OpenAccountManager manager = OpenAccountManager.getInstance(this);

        // QQ授权登陆。
        findViewById(R.id.auth_qq).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.authorize(PlatformEntity.QQ, new ReslutCallback()
                {

                    @Override
                    public void onError(int type, PlatformEntity paltform, String errorMsg) {
                        showMsg(paltform.name + " 授权失败：" + errorMsg);
                    }

                    @Override
                    public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
                        String openid = bundle.getString(Config.PARAM_OPEN_ID);
                        String accessToken = bundle.getString(Config.PARAM_ACCESS_TOKEN);
                        authInfoText.setText("openId = " + openid + ", accessToken = " + accessToken);
                        showMsg(paltform.name + " 授权成功");
                    }

                    @Override
                    public void onCancel(int type, PlatformEntity paltform) {
                        showMsg("取消 " + paltform.name + " 授权");
                    }
                });
            }
        });

        findViewById(R.id.auth_wechat).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.authorize(PlatformEntity.WECHAT, new ReslutCallback()
                {

                    @Override
                    public void onError(int type, PlatformEntity paltform, String errorMsg) {
                        showMsg(paltform.name + " 授权失败：" + errorMsg);
                    }

                    @Override
                    public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
                        String openid = bundle.getString(Config.PARAM_OPEN_ID);
                        String accessToken = bundle.getString(Config.PARAM_ACCESS_TOKEN);
                        authInfoText.setText("openId = " + openid + ", accessToken = " + accessToken);
                        showMsg(paltform.name + " 授权成功");
                    }

                    @Override
                    public void onCancel(int type, PlatformEntity paltform) {
                        showMsg("取消 " + paltform.name + " 授权");
                    }
                });
            }
        });

        final ShareParams params = new ShareParams();
        params.setText("分享福利");
        params.setTitle("美女美女来啦！！啊哈哈哈哈哈哈,哦喝喝喝喝呵呵！");
        params.setUrl("http://www.baidu.com");
        params.setImageUrl("http://image.tianjimedia.com/uploadImages/2014/113/24/2KD9S4P5P1HN_1000x500.jpg");

        final ReslutCallback callback = new ReslutCallback()
        {

            @Override
            public void onError(int type, PlatformEntity paltform, String errorMsg) {
                showMsg(paltform.name + " 分享失败：" + errorMsg);
            }

            @Override
            public void onComplete(int type, PlatformEntity paltform, Bundle bundle) {
                showMsg(paltform.name + " 分享成功");
            }

            @Override
            public void onCancel(int type, PlatformEntity paltform) {
                showMsg("取消 " + paltform.name + " 分享");
            }
        };
        // 分享
        findViewById(R.id.button1).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(null, params, callback);
            }
        });
        findViewById(R.id.button1).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(null, params, callback);
            }
        });
        findViewById(R.id.button1).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(null, params, callback);
            }
        });
        findViewById(R.id.share_to_QQ).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(PlatformEntity.QQ, params, callback);
            }
        });
        findViewById(R.id.share_to_QZone).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(PlatformEntity.QZONE, params, callback);
            }
        });
        findViewById(R.id.share_to_wechat).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(PlatformEntity.WECHAT, params, callback);
            }
        });
        findViewById(R.id.share_to_wechat_timeline).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                manager.shareToPlatform(PlatformEntity.WECHAT_TIMELINE, params, callback);
            }
        });

    }

    public void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
