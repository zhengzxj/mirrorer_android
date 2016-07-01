package com.smart.mirrorer.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseTitleActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lzm on 16/4/27.
 */
public class AboutActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("关于瞬语");
        //得到long类型当前时间
        long l = System.currentTimeMillis();
//new日期对象
        Date date = new Date(l);
//转换提日期输出格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmm");
        String time = dateFormat.format(date);
        String vertionName = "V 1.1."+time;
        ((TextView)findViewById(R.id.about_version_tv)).setText(vertionName);
    }
}
