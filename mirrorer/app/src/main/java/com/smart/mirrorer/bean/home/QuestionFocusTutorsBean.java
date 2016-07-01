package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/4/29.
 */
public class QuestionFocusTutorsBean {


    private int status;
    /**
     * uid : xxx
     * headImgUrl : xxxx
     * nickName : yyyy
     * company : bat
     * title : xysii
     * startPrice : 4.4
     * minutePrice : 4.0
     * star : 4.0
     */

    private List<CommonTutorItemData> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<CommonTutorItemData> getResult() {
        return result;
    }

    public void setResult(List<CommonTutorItemData> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "QuestionFocusTutorsBean{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
