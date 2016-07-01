package com.smart.mirrorer.bean;

import java.util.List;

/**
 * Created by lzm on 16/4/12.
 */
public class BaseIndustryBean {


    /**
     * status : 10000
     * result : [{"catId":"1101","catName":"JAVA"},{"catId":"1102","catName":"PHP"},{"catId":"1103","catName":"C"},{"catId":"1104","catName":"C++"},{"catId":"1105","catName":"Python"},{"catId":"1106","catName":"Ruby"},{"catId":"1107","catName":"GO"},{"catId":"1108","catName":"NodeJS"},{"catId":"1109","catName":"ASP"},{"catId":"1110","catName":".NET"},{"catId":"1111","catName":"Delphi"},{"catId":"1112","catName":"Docker"},{"catId":"1113","catName":"Hadoop"},{"catId":"1114","catName":"Storm"},{"catId":"1115","catName":"Flink"},{"catId":"1116","catName":"Kudu"},{"catId":"1117","catName":"Kafka"},{"catId":"1118","catName":"Flume"},{"catId":"1201","catName":"产品助理"},{"catId":"1202","catName":"产品经理"}]
     */

    private int status;
    /**
     * catId : 1101
     * catName : JAVA
     */

    private List<ResultBean> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String catId;
        private String catName;
        public boolean isShow;
        public boolean isChecked;

        public String getCatId() {
            return catId;
        }

        public void setCatId(String catId) {
            this.catId = catId;
        }

        public String getCatName() {
            return catName;
        }

        public void setCatName(String catName) {
            this.catName = catName;
        }
    }
}
