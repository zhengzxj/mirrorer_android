package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/5/25.
 */

public class OrderListBean {

    private int status;
    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class ResultBean {

        private String orderId;
        private String headImgUrl;
        private String guiderName;
        private String guiderId;
        private String question;
        private int qStatus;
        private float payMoney;
        private int payStatus;
        private int ratedStatus;
        private String createdAt;
        private List<String> tags;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getGuiderName() {
            return guiderName;
        }

        public void setGuiderName(String guiderName) {
            this.guiderName = guiderName;
        }

        public String getGuiderId() {
            return guiderId;
        }

        public void setGuiderId(String guiderId) {
            this.guiderId = guiderId;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public int getqStatus() {
            return qStatus;
        }

        public void setqStatus(int qStatus) {
            this.qStatus = qStatus;
        }

        public float getPayMoney() {
            return payMoney;
        }

        public void setPayMoney(float payMoney) {
            this.payMoney = payMoney;
        }

        public int getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(int payStatus) {
            this.payStatus = payStatus;
        }

        public int getRatedStatus() {
            return ratedStatus;
        }

        public void setRatedStatus(int ratedStatus) {
            this.ratedStatus = ratedStatus;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }
}
