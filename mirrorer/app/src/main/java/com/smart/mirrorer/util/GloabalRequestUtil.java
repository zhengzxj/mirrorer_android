package com.smart.mirrorer.util;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/3/26.
 */
public class GloabalRequestUtil {

    /**
     * 判断当次请求是否成功
     *
     * @param obj
     * @return
     */
    public static boolean isRequestOk(JSONObject obj) {
        if (obj == null) {
            return false;
        }

        int responseStatus = obj.optInt("status");
        if (responseStatus == 10000) {
            return true;
        }
        return false;
    }

    public static boolean isRequestOk(String jsonText) {
        try {
            JSONObject obj = new JSONObject(jsonText);
            if (obj == null) {
                return false;
            }

            int responseStatus = obj.optInt("status");
            if (responseStatus == 10000) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解析抢单是否过期
     * @param obj
     * @return
     */
    public static boolean isPastDate(JSONObject obj) {
        if (obj == null) {
            return false;
        }

        JSONObject resObj = obj.optJSONObject("result");
        if(resObj == null) {
            return false;
        }

        int liveTag = resObj.optInt("live_to_time");
        if (liveTag == 1) {
            return false;
        } else if(liveTag == 0) { //过期
            return true;
        }
        return false;
    }

    /**
     * 获取余额
     * @param obj
     * @return
     */
    public static String getBanlance(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject resObj = obj.optJSONObject("result");
        if(resObj == null) {
            return "";
        }
        double amount = resObj.optDouble("real_amount");

        return amount+"";
    }

    public static String getNetErrorMsg(JSONObject obj) {
        if (obj == null) {
            return "请求失败";
        }

        String errorMsg = obj.optString("result");
        if (TextUtils.isEmpty(errorMsg)) {
            return "请求失败";
        }

        return errorMsg;
    }

    /**
     * 获取qid
     * @param obj
     * @return
     */
    public static String getQId(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String qid = qidObj.optString("qid");

        if (TextUtils.isEmpty(qid)) {
            return "";
        }

        return qid;
    }
    /**
     * 获取qid
     * @param obj
     * @return
     */
    public static String getQuestionDesc(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String qid = qidObj.optString("qid");

        if (TextUtils.isEmpty(qid)) {
            return "";
        }

        return qid;
    }

    /**
     * 获取orderId
     * @param obj
     * @return
     */
    public static String parseOrderId(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String orderid = qidObj.optString("orderId");

        if (TextUtils.isEmpty(orderid)) {
            return "";
        }

        return orderid;
    }

    /**
     * 获取channelName
     * @param obj
     * @return
     */
    public static String parseChannelName(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String channelName = qidObj.optString("orderId");

        if (TextUtils.isEmpty(channelName)) {
            return "";
        }

        return channelName;
    }

    /**realName
     * 获取
     * @param obj
     * @return
     */
    public static String parseRealName(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String realName = qidObj.optString("realName");

        if (TextUtils.isEmpty(realName)) {
            return "";
        }

        return realName;
    }

    /**
     * 获取dynamicKey
     * @param obj
     * @return
     */
    public static String parseDynamicKey(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String dynamicKey = qidObj.optString("dynamicKey");

        if (TextUtils.isEmpty(dynamicKey)) {
            return "";
        }

        return dynamicKey;
    }
    /**
     * 获取headImgUrl
     * @param obj
     * @return
     */
    public static String parseHeadImgUrl(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String headImgUrl = qidObj.optString("headImgUrl");

        if (TextUtils.isEmpty(headImgUrl)) {
            return "";
        }

        return headImgUrl;
    }

    /**
     * 获取headImgUrl
     * @param obj
     * @return
     */
    public static String parseCompany(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String company = qidObj.optString("company");

        if (TextUtils.isEmpty(company)) {
            return "";
        }

        return company;
    }

    /**
     * 获取headImgUrl
     * @param obj
     * @return
     */
    public static String parseTitle(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String title = qidObj.optString("title");

        if (TextUtils.isEmpty(title)) {
            return "";
        }

        return title;
    }
    /**
     * 获取parseNumberId
     * @param obj
     * @return
     */
    public static String parseTargetId(JSONObject obj) {
        if (obj == null) {
            return "";
        }

        JSONObject qidObj = obj.optJSONObject("result");
        if (qidObj == null) {
            return "";
        }

        String targetId = qidObj.optString("targetId");

        if (TextUtils.isEmpty(targetId)) {
            return "";
        }

        return targetId;
    }
}
