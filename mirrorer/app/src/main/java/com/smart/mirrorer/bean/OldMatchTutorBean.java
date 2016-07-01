package com.smart.mirrorer.bean;

import java.util.List;

/**
 * Created by lzm on 16/4/22.
 */
public class OldMatchTutorBean {


    /**
     * status : 10000
     * result : {"list":[{"uid":"A0069DEC0E52BC59D406F40383D79ED6","nick":"alex","icon":"","startPrice":10,"minutePrice":1.4,"star":4.99,"company":"NEC","title":"Android"},{"uid":"E11BA72F52B4C4CDB7B6A1312F2DB2B9","nick":"Sonny","icon":"","startPrice":10.01,"minutePrice":1.5,"star":4.93,"company":"谜语者","title":"CEO"},{"uid":"D940C8166BADF6F27CE4485A38861A7F","nick":"walt","icon":"","startPrice":10,"minutePrice":1.6,"star":4.91,"company":"百度","title":"架构师"}]}
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
         * uid : A0069DEC0E52BC59D406F40383D79ED6
         * nick : alex
         * icon :
         * startPrice : 10
         * minutePrice : 1.4
         * star : 4.99
         * company : NEC
         * title : Android
         */

        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            private String uid;
            private String nick;
            private String icon;
            private double startPrice;
            private double minutePrice;
            private double star;
            private String company;
            private String title;

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public double getStartPrice() {
                return startPrice;
            }

            public void setStartPrice(double startPrice) {
                this.startPrice = startPrice;
            }

            public double getMinutePrice() {
                return minutePrice;
            }

            public void setMinutePrice(double minutePrice) {
                this.minutePrice = minutePrice;
            }

            public double getStar() {
                return star;
            }

            public void setStar(double star) {
                this.star = star;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
