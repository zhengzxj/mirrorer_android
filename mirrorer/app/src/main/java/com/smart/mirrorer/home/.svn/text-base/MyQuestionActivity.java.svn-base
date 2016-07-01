package com.smart.mirrorer.home;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.base.RadioTabManager;
import com.smart.mirrorer.fragment.RecommonAndLikeFragment;
import com.smart.mirrorer.util.MaterialUtils;

/**
 * Created by lzm on 16/4/6.
 */
public class MyQuestionActivity extends BaseTitleActivity implements RadioTabManager.BuildViewFactory, RadioTabManager.TabChangeListener {

    public static final String KEY_Q_CONTENT = "key_q_content";
    public static final String KEY_Q_ID = "key_q_id";

    private RadioTabManager mManager;
    private RadioGroup mTabContain;

//    private View mLeftLine;
    private View mMidLine;
//    private View mRightLine;

    private String mQContent;
    private String mQId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("我的提问");

        Bundle extra = getIntent().getExtras();
        mQContent = extra.getString(KEY_Q_CONTENT);
        mQId = extra.getString(KEY_Q_ID);

        initView();
    }

    private void initView() {
        TextView questionTv = (TextView) findViewById(R.id.q_content_tv);
        questionTv.setText(mQContent);
//        mLeftLine = findViewById(R.id.question_tab_left_line);
        mMidLine = findViewById(R.id.question_tab_mid_line);
//        mRightLine = findViewById(R.id.question_tab_right_line);

        mTabContain = (RadioGroup) findViewById(R.id.question_tab_contain);
        mManager = new RadioTabManager(this, getSupportFragmentManager(), mTabContain, R.id.question_stub_layout, this);
        mManager.setTabChangeListener(this);
//        Bundle args = new Bundle();
//        args.putString(RecommonAndLikeFragment.REQUEST_TYPE, RecommonAndLikeFragment.TYPE_RECOMMON);
//        mManager.addTab(RecommonAndLikeFragment.TYPE_RECOMMON, RecommonAndLikeFragment.class, args);
        Bundle args = new Bundle();
        args.putString(RecommonAndLikeFragment.REQUEST_TYPE, RecommonAndLikeFragment.TYPE_LIKE);
        args.putString(RecommonAndLikeFragment.REQUEST_QID, mQId);
        mManager.addTab(RecommonAndLikeFragment.TYPE_LIKE, RecommonAndLikeFragment.class, args);
//        args = new Bundle();
//        args.putString(RecordFragment.REQUEST_TYPE,
//                RecordFragment.TYPE_RECORD);
//        mManager.addTab(RecordFragment.TYPE_RECORD, RecordFragment.class,
//                args);
        mManager.setCurrFragmentByTag(RecommonAndLikeFragment.TYPE_LIKE);
    }

    @Override
    public View tabView(String tag, ViewGroup parent) {
        RadioButton tabView = newRadioButton();
//        if (RecommonAndLikeFragment.TYPE_RECOMMON.equals(tag)) {
//            tabView.setText("推荐");
//        } else
        if (RecommonAndLikeFragment.TYPE_LIKE.equals(tag)) {
            tabView.setText("感兴趣");
        }
//        else if (RecordFragment.TYPE_RECORD.equals(tag)) {
//            tabView.setText("记录");
//        }
        return tabView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private RadioButton newRadioButton() {
        RadioButton btn = new RadioButton(this);
        btn.setButtonDrawable(new BitmapDrawable());
        btn.setBackground(new BitmapDrawable());
        btn.setGravity(Gravity.CENTER);
        btn.setTextColor(MaterialUtils.createTabItemTextColor(Color.parseColor("#828282"), Color.parseColor("#ec322b")));
        return btn;
    }

    @Override
    public void onChange(RadioTabManager.TabInfo mLastTab) {
        String tabTag = mLastTab.getTag();
//        if (RecommonAndLikeFragment.TYPE_RECOMMON.equals(tabTag)) {
//            mLeftLine.setVisibility(View.VISIBLE);
//            mMidLine.setVisibility(View.INVISIBLE);
//            mRightLine.setVisibility(View.INVISIBLE);
//        } else
        if (RecommonAndLikeFragment.TYPE_LIKE.equals(tabTag)) {
//            mLeftLine.setVisibility(View.INVISIBLE);
            mMidLine.setVisibility(View.VISIBLE);
//            mRightLine.setVisibility(View.INVISIBLE);
        }
//        else if (RecordFragment.TYPE_RECORD.equals(tabTag)) {
//            mLeftLine.setVisibility(View.INVISIBLE);
//            mMidLine.setVisibility(View.INVISIBLE);
//            mRightLine.setVisibility(View.VISIBLE);
//        }

    }
}
