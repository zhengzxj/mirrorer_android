package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/5/25.
 */

public class EvaluateListBean {


    /**
     * status : 10000
     * result : {"tag_list":[{"tag_id":"xxxx-xxxx","tag_name":"很心满意足的人"},{"tag_id":"xxxx-xxxx","tag_name":"很心满意足的人"}]}
     */

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

    /**
     {
     'headImgUrl':<为空表示为匿名评价，用默认头像替代>,
     'realNam':<匿名或用户名称>,
     'uid':<评介者的uid,为后续扩展预留>,
     'tags_list':<评价的标签列表>,
     'score':<评介者打的几星，整型>,
     'body':<评介内容>,
     'createdAt':<评介的日期，字符串，格式：YYYY-mm-dd>
     }
     ]
     }
     */
    public static class ResultBean {

        private String headImgUrl;
        private String realName;
        private String uid;
        private String body;
        private String createdAt;
        private String score;
        private List<String> tags_list;


        public List<String> getTags_list() {
            return tags_list;
        }

        public void setTags_list(List<String> tags_list) {
            this.tags_list = tags_list;
        }

        public String getHeadImgUrl() {
            return headImgUrl;
        }

        public void setHeadImgUrl(String headImgUrl) {
            this.headImgUrl = headImgUrl;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

//        private static class TagBean
//        {
//            private String tag;
//
//            public String getTag() {
//                return tag;
//            }
//
//            public void setTag(String tag) {
//                this.tag = tag;
//            }
//        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "headImgUrl='" + headImgUrl + '\'' +
                    ", realName='" + realName + '\'' +
                    ", uid='" + uid + '\'' +
                    ", body='" + body + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", score='" + score + '\'' +
                    ", tags_list=" + tags_list +
                    '}';
        }
    }
}
