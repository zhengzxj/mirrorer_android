package com.smart.mirrorer.bean.home;

/**
 * Created by lzm on 16/4/29.
 */
public class OrderDetailsBean {

    private OrderDetailsData result;

    private int status;

    public OrderDetailsData getResult() {
        return result;
    }

    public void setResult(OrderDetailsData result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderDetailsBean{" +
                "result=" + result +
                ", status=" + status +
                '}';
    }
}
