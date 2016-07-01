package com.smart.mirrorer.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzm on 16/4/28.
 */
public class CommonUtils {

    public static String getFormatTime(int seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = seconds / 60;
            if (minute < 60) {
                second = seconds % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = seconds - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 显示通用的自定义文案对话框
     * @param context
     * @param tipContent
     */
    public static void showCommonDialog(Context context, String tipContent) {

        final Dialog dialog = new Dialog(context, R.style.common_dialog_theme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogContentView = inflater.inflate(R.layout.common_text_tip_dialog, null);



        TextView tipContentTv = (TextView) dialogContentView.findViewById(R.id.common_tip_tv);
        tipContentTv.setText(tipContent);

        TextView confirmTv = (TextView) dialogContentView.findViewById(R.id.common_tip_confirm_tv);
        confirmTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(dialogContentView);

        int topMargin = DeviceConfiger.dp2px(150);
        int width = DeviceConfiger.dp2px(300);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = topMargin;
        lp.width = width;
        dialogWindow.setAttributes(lp);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 获取call的json值
     * @param orderId
     * @return
     */
    public static String getJsonOrderId(String orderId) {
        if(TextUtils.isEmpty(orderId)) {
            return null;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    /**
     * 创建订单-->接通电话的逻辑都在这里
     * @param baseActivity
     * @param uid
     * @param qid 传0
     * @param guideId
     * @param callback
     */
    public static void getOrderId (final BaseActivity baseActivity, String uid, String qid, String guideId, Response.Listener<JSONObject> callback) {

        L.i("XXXYYY创建订单参数 : uid = "+uid);
        String tag_json_obj = "json_obj_get_orderid_req";
        baseActivity.showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("gid", guideId);
            L.d("gid = "+guideId);
            if(TextUtils.isEmpty(qid)) {
                paramObj.put("qid", "0");
            } else {
                paramObj.put("qid", qid);
                L.d("qid = "+qid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(uid, Request.Method.POST,
                Urls.URL_CREATE_ORDER, paramObj, callback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                L.i("XXXYYY创建订单失败");
                TipsUtils.showShort(baseActivity.getApplicationContext(), error.getMessage());
                baseActivity.dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    /**
     * 获取订单详情
     * @param baseActivity
     * @param uid
     * @param callOrderId
     * @param payStyle
     * @param orderCallback
     */
    public static void getOrderDetails(final BaseActivity baseActivity, String uid, String callOrderId, String payStyle, GsonCallbackListener<OrderDetailsBean> orderCallback) {
        L.i("XXXYYY获取订单详情使用的uid = "+uid);
        L.i("XXXYYY获取订单详情使用的oid = "+callOrderId);
        String tag_json_obj = "json_obj_get_order_details_req";
        baseActivity.showLoadDialog();

        JSONObject paramObj = new JSONObject();
        try {
            paramObj.put("oid", callOrderId);
            paramObj.put("payWay", payStyle); //默认支付宝1  微信：2
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(uid, Request.Method.POST,
                Urls.URL_ORDER_DETAILS, paramObj, orderCallback, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(baseActivity.getApplicationContext(), error.getMessage());
                baseActivity.dismissLoadDialog();
            }
        });

        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void onToutchChangeBGColor(View v,final int color1,final int color2)
    {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(color1);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(color2);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        v.setBackgroundColor(color2);
                        break;
                }
                return false;
            }
        });
    }
}
