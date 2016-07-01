package com.smart.mirrorer.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.event.LogInOkEvent;
import com.smart.mirrorer.util.TipsUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by lzm on 16/4/11.
 */
public class LoginRegisterChooseActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    // 定义ViewPager对象
    private ViewPager viewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> views;
    // 引导图片资源
    private static final int[] pics = { R.drawable.guide_one,  R.drawable.guide_two,
            R.drawable.guide_three, R.drawable.guide_four};
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_guide_layout);
        BusProvider.getInstance().register(this);
        initView();
        initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        views = new ArrayList<View>();
        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.choose_guide_viewpager);
        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);

        final TextView loginBtn = (TextView) findViewById(R.id.choose_guide_login_btn);
        loginBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        loginBtn.setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_UP:
                        loginBtn.setTextColor(Color.parseColor("#000000"));
                        break;
                }
                return false;
            }
        });
        final TextView registerBtn = (TextView) findViewById(R.id.choose_guide_register_btn);
        registerBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        registerBtn.setTextColor(Color.parseColor("#000000"));
                        break;
                    case MotionEvent.ACTION_UP:
                        registerBtn.setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
                return false;
            }
        });
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    /**
     * 初始化数据
     */
    private void initData() {
        LayoutInflater lf = getLayoutInflater().from(this);
        View view1 = lf.inflate(R.layout.guide_one_layout, null);
        View view2 = lf.inflate(R.layout.guide_two_layout, null);
        View view3 = lf.inflate(R.layout.guide_three_layout, null);
        View view4 = lf.inflate(R.layout.guide_four_layout, null);

        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);

        // 设置数据
        viewPager.setAdapter(vpAdapter);
        // 设置监听
        viewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.choose_guide_point_ll);
        points = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    setCurView(position);
                    setCurDot(position);
                }
            });
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当前页面滑动时调用
     */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 新的页面被选中时调用
     */
    @Override
    public void onPageSelected(int arg0) {
        // 设置底部小点选中状态
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_guide_login_btn:
                processCleanIntent(LoginActivity.class);
                break;
            case R.id.choose_guide_register_btn:
                processCleanIntent(BaseInfoTypeInActivity.class);
                break;
        }
    }



    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }


    class ViewPagerAdapter extends PagerAdapter {
        //界面列表
        private ArrayList<View> views;
        public ViewPagerAdapter(ArrayList<View> views)
        {
            this.views = views;
        }

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }
            else return 0;
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            return views.get(position);
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