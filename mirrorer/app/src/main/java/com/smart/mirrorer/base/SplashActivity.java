package com.smart.mirrorer.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.smart.mirrorer.R;
import com.smart.mirrorer.home.MainActivity;
import com.smart.mirrorer.home.NewMainActivity;
import com.smart.mirrorer.service.MqttService;
import com.smart.mirrorer.util.MirrorSettings;

/**
 * Created by lzm on 16/3/25.
 */
public class SplashActivity extends BaseActivity {
    private MirrorSettings mSettings;

    private boolean isHaveLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View splashView = View.inflate(this, R.layout.splash_layout, null);
        setContentView(splashView);

        mSettings = BaseApplication.getSettings(this);
        String uid = mSettings.APP_UID.getValue();
        if (TextUtils.isEmpty(uid)) {
            isHaveLogined = false;
        } else {
            isHaveLogined = true;
        }
        mMainHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            if (isHaveLogined) {
                MqttService.actionStart(BaseApplication.getInstance());
//                intent = new Intent(SplashActivity.this, NewMainActivity.class);
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, SplashRegisterActivity.class);
            }

            startActivity(intent);
            finish();
        }
    };
}
