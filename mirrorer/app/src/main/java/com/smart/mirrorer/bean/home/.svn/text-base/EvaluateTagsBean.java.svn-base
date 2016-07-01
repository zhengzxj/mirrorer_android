package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/5/25.
 */

public class EvaluateTagsBean {


    /**
     * status : 10000
     * result : {"tag_list":[{"tag_id":"xxxx-xxxx","tag_name":"很心满意足的人"},{"tag_id":"xxxx-xxxx","tag_name":"很心满意足的人"}]}
     */

    private int status;
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
        /**
         * tag_id : xxxx-xxxx
         * tag_name : 很心满意足的人
         */

        private List<TagListBean> tag_list;

        public List<TagListBean> getTag_list() {
            return tag_list;
        }

        public void setTag_list(List<TagListBean> tag_list) {
            this.tag_list = tag_list;
        }

        public static class TagListBean {
            private String tag_id;
            private String tag_name;
            public boolean isChecked;

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getTag_name() {
                return tag_name;
            }

            public void setTag_name(String tag_name) {
                this.tag_name = tag_name;
            }
        }
    }
}
