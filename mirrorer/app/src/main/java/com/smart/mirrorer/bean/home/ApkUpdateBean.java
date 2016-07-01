package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by zhengfei on 16/5/27.
 */
public class ApkUpdateBean {
    private ResultBean result;
    /**
     * result : {"list":[{"content":"如何成为一个成功的产品经理？","follows":2,"status":1,"favorites":1},{"content":"如何成为CTO？","follows":10,"status":1,"favorites":2},{"content":"如何成为音乐家？","follows":30,"status":1,"favorites":3}]}
     * status : 10000
     */

    private int status;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class ResultBean {
        /**
         * url : apk地址
         * ver : version code
         * title :标题
         * desc : 描述
         */
        private String url;
        private String ver;
        private String title;
        private String desc;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }
    }
}
