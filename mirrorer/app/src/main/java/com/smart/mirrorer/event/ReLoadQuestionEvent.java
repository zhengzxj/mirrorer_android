package com.smart.mirrorer.event;

/**
 * Created by zhengfei on 16/5/30.
 * 重发问题的事件
 */
public class ReLoadQuestionEvent {
    public ReLoadQuestionEvent()
    {
        super();
    }
    private String mQid;
    private String mQDesc;

    public String getmQid() {
        return mQid;
    }

    public void setmQid(String mQid) {
        this.mQid = mQid;
    }

    public String getmQDesc() {
        return mQDesc;
    }

    public void setmQDesc(String mQDesc) {
        this.mQDesc = mQDesc;
    }
}
