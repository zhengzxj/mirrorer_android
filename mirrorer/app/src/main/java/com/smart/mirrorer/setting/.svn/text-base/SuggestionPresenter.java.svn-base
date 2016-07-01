package com.smart.mirrorer.setting;

import com.videorecorder.util.Log;

/**
 * Created by lzm on 16/5/13.
 */
public class SuggestionPresenter implements SuggestionContract.Presenter {

    private SuggestionContract.View mSuggestionView;

    public SuggestionPresenter(SuggestionContract.View suggestionView){
        Log.e("lzm", "初始化present _view="+suggestionView);
        this.mSuggestionView = suggestionView;
        mSuggestionView.setPresenter(this);
    }

    @Override
    public void submmitSuggestion(String content) {
        if (content.length() > 250) {
            mSuggestionView.showSubTip("不能超过250个字符");
            return;
        }

        //to submmit
        mSuggestionView.showLoadingView();
        mSuggestionView.dissmissLoadingView();
        mSuggestionView.success();
    }

    @Override
    public void start() {

    }
}
