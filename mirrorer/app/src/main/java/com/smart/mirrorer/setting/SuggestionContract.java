package com.smart.mirrorer.setting;

/**
 * Created by lzm on 16/5/12.
 */
public interface SuggestionContract {

    interface View extends BaseView<Presenter> {
        void showLoadingView();
        void dissmissLoadingView();
        void showSubTip(String tipText);
        void success();
    }

    interface Presenter extends BasePresenter {
        void submmitSuggestion(String content);
    }

}
