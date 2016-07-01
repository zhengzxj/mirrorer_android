package com.smart.mirrorer.event;

/**
 * Created by zhengfei on 16/5/30.
 */
public class DeleteQuestionEvent {
    public DeleteQuestionEvent()
    {
        super();
    }
    private String questionId;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
