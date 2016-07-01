package com.smart.mirrorer.commond;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.util.SnsUtils;

/**
 * Created by lzm on 16/3/26.
 */
public class RecommondActivity extends BaseTitleActivity {

    private String mShareUrl="http://www.mirrorer.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommond_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle(R.string.navigation_commend_text);

        initView();
    }

    private void initView() {

        TextView shareTv = (TextView) findViewById(R.id.recommon_award_share_tv);
        shareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SnsUtils.shareApp(RecommondActivity.this, "瞬语", "语音提问，全球众包，即时视频回答", mShareUrl);
            }
        });
    }
}
