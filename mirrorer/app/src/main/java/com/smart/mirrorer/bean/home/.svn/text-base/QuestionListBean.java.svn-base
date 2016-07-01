package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/3/28.
 */
public class QuestionListBean {

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
         * content : 如何成为一个成功的产品经理？
         * follows : 2
         * status : 1
         * favorites : 1
         */

        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private String content;
            private String createAt;
            private int status;
            private String qid;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCreateAt() {
                return createAt;
            }

            public void setCreateAt(String createAt) {
                this.createAt = createAt;
            }

            public String getQid() {
                return qid;
            }

            public void setQid(String qid) {
                this.qid = qid;
            }
        }
    }
}
