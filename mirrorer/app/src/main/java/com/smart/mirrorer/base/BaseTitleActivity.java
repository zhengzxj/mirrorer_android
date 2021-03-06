package com.smart.mirrorer.base;

import android.os.Bundle;
import android.view.View;

import com.smart.mirrorer.view.NavigationBar;
import com.smart.mirrorer.view.NavigationBarController;

/**
 * Created by lzm on 16/3/24.
 */
public class BaseTitleActivity extends BaseActivity implements NavigationBar.IProvideNavigationBar {

    protected NavigationBarController mNvaigationBarMange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public NavigationBar getNavigationBar() {
        if (mNvaigationBarMange == null || mNvaigationBarMange.getNavigationBar() == null) {
            throw new RuntimeException("you may have forgotten to call setupNavigationBar!!");
        }
        return mNvaigationBarMange.getNavigationBar();
    }

    @Override
    public void setupNavigationBar(int resId) {
        NavigationBar mNavigationBar = (NavigationBar) findViewById(resId);
        if (mNavigationBar == null) {
            throw new RuntimeException("R.id.navigation_bar_ex resouce not found!!");
        }
        mNvaigationBarMange = new NavigationBarController(this, mNavigationBar);
    }

    @Override
    public void setCommonTitle(int titleId) {
        mNvaigationBarMange.setTitle(titleId);
    }

    @Override
    public void setCommonTitle(CharSequence title) {
        mNvaigationBarMange.setTitle(title);
    }

    @Override
    public void setCommonImageTitle(int id){mNvaigationBarMange.setImageTitle(id);}
    @Override
    public View addRightButtonText(String text, View.OnClickListener listener) {
        return mNvaigationBarMange.addRightButtonText(text, listener);
    }

    @Override
    public void setCommonImageLeft(int id,View.OnClickListener listener){mNvaigationBarMange.setImageLeft(id,listener);}

    @Override
    public void setCommonImageRight(int id,View.OnClickListener listener){mNvaigationBarMange.setImageRight(id,listener);}
    @Override
    public View addRightButtonText(int resId, View.OnClickListener listener) {
        return mNvaigationBarMange.addRightButtonText(resId, listener);
    }
}
