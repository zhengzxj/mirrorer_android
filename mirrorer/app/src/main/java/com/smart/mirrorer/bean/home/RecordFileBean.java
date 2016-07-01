package com.smart.mirrorer.bean.home;

import java.util.List;

/**
 * Created by lzm on 16/4/23.
 */
public class RecordFileBean {

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
        private String headImgUrl;
        private int sex;

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        private String realName;
        private float startPrice;
        private int fansCount;
        private float minutePrice;
        private String introduce;
        private int helpCount;
        private int commentCount;
        private String title;
        private int focusCount;
        private float star;
        private String company;
        private String videoUrl;
        private int isFollowed;

        /**
         * company : 人人网
         * endYear : 1994
         * startYear : 1989
         * title : 按时
         */

        private List<WorkFlowBean> workFlow;
        /**
         * professional : 计算机
         * school : 清华大学
         * endYear : 1995
         * startYear : 1989
         */

        private List<EduFlowBean> eduFlow;

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

        public float getStartPrice() {
            return startPrice;
        }

        public void setStartPrice(float startPrice) {
            this.startPrice = startPrice;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public int getIsFollowed() {
            return isFollowed;
        }

        public void setIsFollowed(int isFollowed) {
            this.isFollowed = isFollowed;
        }

        public int getFansCount() {
            return fansCount;
        }

        public void setFansCount(int fansCount) {
            this.fansCount = fansCount;
        }

        public float getMinutePrice() {
            return minutePrice;
        }

        public void setMinutePrice(float minutePrice) {
            this.minutePrice = minutePrice;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public int getHelpCount() {
            return helpCount;
        }

        public void setHelpCount(int helpCount) {
            this.helpCount = helpCount;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getFocusCount() {
            return focusCount;
        }

        public void setFocusCount(int focusCount) {
            this.focusCount = focusCount;
        }

        public float getStar() {
            return star;
        }

        public void setStar(float star) {
            this.star = star;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public List<WorkFlowBean> getWorkFlow() {
            return workFlow;
        }

        public void setWorkFlow(List<WorkFlowBean> workFlow) {
            this.workFlow = workFlow;
        }

        public List<EduFlowBean> getEduFlow() {
            return eduFlow;
        }

        public void setEduFlow(List<EduFlowBean> eduFlow) {
            this.eduFlow = eduFlow;
        }

        public static class WorkFlowBean {
            private String company;
            private String endYear;
            private String startYear;
            private String title;

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getEndYear() {
                return endYear;
            }

            public void setEndYear(String endYear) {
                this.endYear = endYear;
            }

            public String getStartYear() {
                return startYear;
            }

            public void setStartYear(String startYear) {
                this.startYear = startYear;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class EduFlowBean {
            private String professional;
            private String school;
            private String endYear;
            private String startYear;

            public String getProfessional() {
                return professional;
            }

            public void setProfessional(String professional) {
                this.professional = professional;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public String getEndYear() {
                return endYear;
            }

            public void setEndYear(String endYear) {
                this.endYear = endYear;
            }

            public String getStartYear() {
                return startYear;
            }

            public void setStartYear(String startYear) {
                this.startYear = startYear;
            }
        }

        @Override
        public String toString() {
            return "ResultBean{" +
                    "headImgUrl='" + headImgUrl + '\'' +
                    ", sex=" + sex +
                    ", realName='" + realName + '\'' +
                    ", startPrice=" + startPrice +
                    ", fansCount=" + fansCount +
                    ", minutePrice=" + minutePrice +
                    ", introduce='" + introduce + '\'' +
                    ", helpCount=" + helpCount +
                    ", commentCount=" + commentCount +
                    ", title='" + title + '\'' +
                    ", focusCount=" + focusCount +
                    ", star=" + star +
                    ", company='" + company + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", isFollowed=" + isFollowed +
                    ", workFlow=" + workFlow +
                    ", eduFlow=" + eduFlow +
                    '}';
        }
    }
}
