package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/4/30.
 */
public class HomeIndexBean {

    private int status;
    /**
     * headImgUrl : http://www.xo.com/aa.jpg
     * nickName : Mathroz
     * orderList : ["NO20160429102948897927"]
     */

    private ResultBean result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String headImgUrl;
        private String nickName;
        private List<String> orderList;
        private int flag;
        private int helpCount;
        private int talkTime;
        private float amountTotal;

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public List<String> getOrderList() {
            return orderList;
        }

        public void setOrderList(List<String> orderList) {
            this.orderList = orderList;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public int getHelpCount() {
            return helpCount;
        }

        public void setHelpCount(int helpCount) {
            this.helpCount = helpCount;
        }

        public int getTalkTime() {
            return talkTime;
        }

        public void setTalkTime(int talkTime) {
            this.talkTime = talkTime;
        }

        public float getAmountTotal() {
            return amountTotal;
        }

        public void setAmountTotal(float amountTotal) {
            this.amountTotal = amountTotal;
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "headImgUrl='" + headImgUrl + '\'' +
                    ", nickName='" + nickName + '\'' +
                    ", orderList=" + orderList +
                    ", flag=" + flag +
                    ", helpCount=" + helpCount +
                    ", talkTime=" + talkTime +
                    ", amountTotal=" + amountTotal +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HomeIndexBean{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
