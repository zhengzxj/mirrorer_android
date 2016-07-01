package com.smart.mirrorer.event;

/**
 * Created by lzm on 16/3/26.
 */
public class CallConnectEvent {
    private String qid;

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public CallConnectEvent() {
        super();
    }
}
