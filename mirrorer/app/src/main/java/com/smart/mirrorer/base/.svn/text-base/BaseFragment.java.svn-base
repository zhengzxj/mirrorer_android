package com.smart.mirrorer.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.smart.mirrorer.base.BaseActivity;

/**
 * Created by lzm on 16/4/6.
 */
public class BaseFragment extends Fragment {

    private BaseActivity mBaseAct;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            mBaseAct = (BaseActivity) activity;
        }
    }

    /**
     * 显示loadingDialog 仍然使用Activity中的loadingDialog
     */
    public void showLoadingDialog() {
        if (mBaseAct != null) {
            mBaseAct.showLoadDialog();
        }
    }

    public void dismissLoadingDialog() {
        if (mBaseAct != null) {
            mBaseAct.dismissLoadDialog();
        }
    }
}
