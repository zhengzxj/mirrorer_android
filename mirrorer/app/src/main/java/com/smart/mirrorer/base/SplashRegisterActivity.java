package com.smart.mirrorer.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.LogInOkEvent;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by lzm on 16/4/11.
 */
public class SplashRegisterActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_register_layout);
        BusProvider.getInstance().register(this);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {

        final TextView tv_go_login = (TextView) findViewById(R.id.tv_go_login);

        final TextView tv_go_register = (TextView) findViewById(R.id.tv_go_register);
        tv_go_register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tv_go_register.setTextColor(Color.parseColor("#000000"));
                        break;
                    case MotionEvent.ACTION_UP:
                        tv_go_register.setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
                return false;
            }
        });
        tv_go_login.setOnClickListener(this);
        tv_go_register.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_go_login:
                processCleanIntent(LoginActivity.class);
                break;
            case R.id.tv_go_register:
                processCleanIntent(RegisterActivity.class);
                break;
        }
    }


    @Subscribe
    public void onEventLoginOk(LogInOkEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}