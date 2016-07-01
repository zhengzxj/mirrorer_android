package com.smart.mirrorer.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.fragment.FollowListFragment;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by lzm on 16/3/26.
 */
public class GuangZhuActivity extends BaseTitleActivity {

    public static final String FOLLOW_LIST_KEY = "follow_list_key";
    private static final String[] CONTENT = new String[] { "全部", "回答者", "提问者"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanzhu_layout);
        setupNavigationBar(R.id.navigation_bar);
        getNavigationBar().setBackgroundColor(Color.parseColor("#ffffff"));
        setCommonTitle(R.string.navigation_guanzhu_text);

        initView();
    }

    private void initView() {

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.vp_guanzhu);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FollowListFragment.newInstance(position);//正好和后台的 0=全部,1=回答者,2=提问者 一样
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }

}
