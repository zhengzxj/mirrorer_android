package com.smart.mirrorer.bean;

/**
 * Created by lzm on 16/3/26.
 */
public class LoginInfo {
    /**
     * errMessage : null
     * code : 10097
     */

    private ResultBean result;
    /**
     * result : {"errMessage":null,"code":10097}
     * status : 10097
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
        private String nick;
        private String uid;
        private String errMessage;
        private int code;

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getErrMessage() {
            return errMessage;
        }

        public void setErrMessage(String errMessage) {
            this.errMessage = errMessage;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
