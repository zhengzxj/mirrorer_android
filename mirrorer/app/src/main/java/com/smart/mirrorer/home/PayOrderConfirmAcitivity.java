package com.smart.mirrorer.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.bean.pay.PayResult;
import com.smart.mirrorer.event.BusProvider;
import com.smart.mirrorer.event.FinishActivityEvent;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.SignUtils;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.util.weixin.WeixinPayUtil;
import com.smart.mirrorer.view.SelectPayStylePopupWindow;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/4/28.
 */
public class PayOrderConfirmAcitivity extends BaseTitleActivity implements View.OnClickListener {

    public static final String KEY_DATA_ORDER = "key_data_order";

    // 商户PID
    public static final String PARTNER = "2088221611187694";
    // 商户收款账号
    public static final String SELLER = "2088221611187694";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALFvSTooeF9RCP68+Fwew6p28C87E1IIB9An7eSUMTEwvT+eJUWjQhEC0jvkKohJtyfhQ6RR8y41bvc64mQl7mEjJYzdUh3HW45UpRT9Vfa83sifCaGoMeFP5fxdgwpjNcOhjVF9E5ug2uF/IBloX7hjjkdR2h8CzSRBaQ3ORTn7AgMBAAECgYEAo6VcCEOjIWX/wdJPzcah9+pSg30cyJ+XdSgehJ8Y76FgKfiVr7BvEnljpNE/WQuD3UgptrfObWPiwCqS+oLxYyM83vg40cRs63E1Ck+tpBnQ1RKtRn9FcPdy2OZRmAgPZA1iCG9a4YZ5X8Pa5j8p43lMAjUmwaCmsD6fQkEN/6ECQQDm65bnd4P34LAHGoaWWV5PeRvBwUwb/eG8/9kdOMqddF4lu961VMDblJsxl4R5hFaxfEm7XYAiLzYha7CP6/avAkEAxLSbQIPesENAKe23OlAMmJxIIixB6Q8DtCfRUWyP7oTHrBM5Ro3W8/yAsb85urGx4djQ10h/VdDEaPDonklEdQJAeLgOssD7P5m5zGzkkTL1J8zjYt4/gJ6v5VNHWIx49+l+0nEtk1RUYjaFD17rhW9WtwCHtyjBNJ3DyaL3sbWkxwJAO8ebix7IdY7qmBN7ZMhhG5wbO+9xVG/Qs1aKgZFZqKZHvF7XQD7o/ZtWTS6NT9Si970ZsuOAd0cL+cyFFV205QJBANDwvgIo8spVCfYLSD11ysKKg6tvBiFFUm06Kdg1+gZaPg2CW+heKDvMxJJBqoMsQp+S7bAkZiCh++VTZF6NBb8=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;


    private boolean isWeixinPay = false;
    //微信api对象
    private IWXAPI mWxMsgApi;

    //自定义的弹出框类
    SelectPayStylePopupWindow mPayStyleWindow;


    private CircleImageView mHeadIv;
    private TextView mNickTv;
    private TextView mCompanyTv;
    private TextView mPositionTv;
    private TextView mStartTv;
    private TextView mMinuteTv;
    private TextView mStarCountTv;

    private TextView mQContentTv;
    private TextView mCallTimeTv;
    private TextView mPriceTv;
    private TextView mTotalPriceTv;

    private TextView mPayStyleTv;
    private ImageView mPayStyleIv;

    private OrderDetailsData mDetailData;

    private TextView mPayTv;

    private MirrorSettings mSettings;
    private String mUid;

    private LinearLayout ll_show_money_detail;
    private LinearLayout rl_money_detail;

    private TextView createAt;
    private TextView tv_5m_total_pay;

    private int after5mCount;
    private TextView tv_after_5m_per_pay;
    private TextView tv_after5_total_money;
    private TextView tv_orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_confirm_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle("确认订单");

        BusProvider.getInstance().register(this);

        mDetailData = getIntent().getExtras().getParcelable(KEY_DATA_ORDER);

        mSettings= BaseApplication.getSettings(this);
        mUid = mSettings.APP_UID.getValue();

        initView();

        if (mDetailData != null) {
            updateUI();
        }
    }

    private String timeText;
    private void updateUI() {
        createAt.setText(mDetailData.getCreatedAt());
        String headUrl = mDetailData.getGuiderHeadUrl();
        if (!TextUtils.isEmpty(headUrl)) {
            BaseApplication.mImageLoader.displayImage(headUrl, mHeadIv, BaseApplication.headOptions);
        }
        mNickTv.setText(mDetailData.getGuiderName());
        mCompanyTv.setText(mDetailData.getCompany());
        mPositionTv.setText(mDetailData.getTitle());
        mStartTv.setText(getResources().getString(R.string.tutor_five_minute_price, mDetailData.getStartPrice() + ""));
        mMinuteTv.setText(getResources().getString(R.string.tutor_minute_price, mDetailData.getMinutePrice() + ""));
        mStarCountTv.setText(mDetailData.getGuiderStar() + "");
//
        mQContentTv.setText(mDetailData.getQuestion());
//        timeText = CommonUtils.getFormatTime(mDetailData.getCallTalkDuration());
//        mCallTimeTv.setText(getString(R.string.call_time_text, timeText)); //回答者首页也给的秒
        mPriceTv.setText(getString(R.string.single_price_text, mDetailData.getPayMoney()));
//        mTotalPriceTv.setText(getString(R.string.single_price_text, mDetailData.getPayMoney()));

        tv_5m_total_pay.setText("¥"+mDetailData.getStartPrice());

        if(mDetailData.getCallTalkDuration()>300)
        {
            after5mCount = mDetailData.getCallTalkDuration()/60;
            if (mDetailData.getCallTalkDuration()%60!=0)
            {
                after5mCount++;
            }
        }
        tv_after_5m_per_pay.setText("5分钟后("+after5mCount+"分钟)");
        tv_after5_total_money.setText("¥"+after5mCount*mDetailData.getMinutePrice());
        tv_orderid.setText("订单号:"+mDetailData.getOrderId());




    }

    private ImageView weixinSelIv;
    private ImageView alipaySelIv;

    private TextView tv_pay;
    private void initView() {

        tv_pay = (TextView)findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(this);

        createAt = (TextView)findViewById(R.id.tv_create_at);
        ll_show_money_detail = (LinearLayout)findViewById(R.id.ll_show_money_detail);
        ll_show_money_detail.setOnClickListener(this);
        rl_money_detail = (LinearLayout)findViewById(R.id.rl_money_detail);
        mHeadIv = (CircleImageView) findViewById(R.id.recommon_like_cell_head_iv);
        mNickTv = (TextView) findViewById(R.id.liked_nick_tv);
        mCompanyTv = (TextView) findViewById(R.id.liked_company_tv);
        mPositionTv = (TextView) findViewById(R.id.liked_position_tv);
        mStartTv = (TextView) findViewById(R.id.liked_start_price_tv);
        mMinuteTv = (TextView) findViewById(R.id.liked_minute_price_tv);
        mStarCountTv = (TextView) findViewById(R.id.recommon_like_cell_right_count_tv);
//        ImageView videoIv = (ImageView) findViewById(R.id.recommon_like_cell_video_icon);
//        videoIv.setVisibility(View.GONE);
//
        mQContentTv = (TextView) findViewById(R.id.pay_confirm_q_desc_tv);
//        mCallTimeTv = (TextView) findViewById(R.id.pay_confirm_call_time_tv);
        mPriceTv = (TextView) findViewById(R.id.tv_total_monye);
//        mTotalPriceTv = (TextView) findViewById(R.id.pay_confirm_total_price_tv);
        tv_5m_total_pay = (TextView)findViewById(R.id.tv_5m_total_pay);
        tv_after_5m_per_pay = (TextView)findViewById(R.id.tv_after_5m_per_pay);
        tv_after5_total_money = (TextView)findViewById(R.id.tv_after5_total_money);
        tv_orderid = (TextView)findViewById(R.id.tv_orderid);
//
//        RelativeLayout payStyleItem = (RelativeLayout) findViewById(R.id.pay_confirm_pay_style_item);
//        payStyleItem.setOnClickListener(this);
//        mPayStyleTv = (TextView) findViewById(R.id.pay_confirm_pay_text_tv);
//        mPayStyleIv = (ImageView) findViewById(R.id.pay_confirm_pay_icon_iv);
//
//        mPayTv = (TextView) findViewById(R.id.pay_cnfirm_commit_tv);
//        mPayTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                L.i("点击了支付按钮,跳转选择支付方式页面");
//                break;
//            case R.id.pay_confirm_pay_style_item:
//                //SelectPayStylePopupWindow
                mPayStyleWindow = new SelectPayStylePopupWindow(PayOrderConfirmAcitivity.this, PayOrderConfirmAcitivity.this, isWeixinPay);
                //显示窗口
                mPayStyleWindow.showAtLocation(PayOrderConfirmAcitivity.this.findViewById(R.id.pay_confirm_root),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                weixinSelIv = (ImageView) mPayStyleWindow.getContentView().findViewById(R.id.pop_weixin_sel_iv);
                alipaySelIv = (ImageView) mPayStyleWindow.getContentView().findViewById(R.id.pop_alipey_sel_iv);
                //设置layout在PopupWindow中显示的位置
                break;
            case R.id.pop_pay_style_weixin_layout:
                isWeixinPay = true;

//                mPayStyleWindow.dismiss();

                weixinSelIv.setImageResource(R.drawable.ic_pay_sel);
                alipaySelIv.setImageResource(R.drawable.ic_pay_unsel);

//                mPayStyleTv.setText("微信支付");
//                mPayStyleIv.setImageResource(R.drawable.ic_weixin_img);
                break;
            case R.id.pop_pay_style_alipay_layout:
                isWeixinPay = false;


                weixinSelIv.setImageResource(R.drawable.ic_pay_unsel);
                alipaySelIv.setImageResource(R.drawable.ic_pay_sel);
//                mPayStyleWindow.dismiss();
//
//                mPayStyleTv.setText("支付宝支付");
//                mPayStyleIv.setImageResource(R.drawable.ic_alipay_img);
                break;
            case R.id.pop_sel_commit_tv:
                mPayStyleWindow.dismiss();
                if (isWeixinPay) {
                    if (TextUtils.isEmpty(mPrePayID)) {
                        CommonUtils.getOrderDetails(PayOrderConfirmAcitivity.this, mUid, mDetailData.getOrderId(), "2", mOrderDetailCallback);
                    } else {
                        payFromWeixin();
                    }
                } else {
                    payFromAli();
                }
                break;
            case R.id.ll_show_money_detail:
                if(isDismissDetail)
                {
                    rl_money_detail.setVisibility(View.VISIBLE);
                }else
                {
                    rl_money_detail.setVisibility(View.GONE);
                }
                isDismissDetail = !isDismissDetail;
                break;
        }
    }

    private boolean isDismissDetail = true;

    private String mPrePayID;

    /**
     * 微信支付
     */
    private void payFromWeixin() {
        mWxMsgApi = BaseApplication.getWeixinMsgApi(PayOrderConfirmAcitivity.this);
        if (mWxMsgApi == null) {
            TipsUtils.showShort(getApplicationContext(), "无法获取微信支付对象");
            return;
        }

        PayReq req = WeixinPayUtil.getPayReq(mPrePayID);
        L.i("微信支付 预付订单号:"+mPrePayID);
        mWxMsgApi.sendReq(req);
    }

    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallback = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            L.i("XXXYYY获取订单详情返回:"+orderDetailsBean);
            dismissLoadDialog();
            if (orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if (detailData == null) {
                return;
            }

            mPrePayID = detailData.getPrePayId();
            if (TextUtils.isEmpty(mPrePayID)) {
                TipsUtils.showShort(getApplicationContext(), "没有生成预付订单id，无法完成支付");
                return;
            }

            payFromWeixin();
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };

    /**
     * 支付宝支付
     */
    private void payFromAli() {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }
        String orderInfo = getOrderInfo(mDetailData.getQuestion(), mDetailData.getQuestion(), mDetailData.getPayMoney()+"");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayOrderConfirmAcitivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + mDetailData.getOrderId() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径  https://api.mirrorer.com/v1/alipay/callback
        orderInfo += "&notify_url=" + "\"" + "https://pcall.mirrorer.com/v1/alipay/callback" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {

                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        processToEvaluate();
                        L.i("XXXYYY APP_IS_UN_PAY SET VALUE = FALSE");
                        mSettings.APP_IS_UN_PAY.setValue(false);
                        Toast.makeText(getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                        L.i("未支付  支付成功set false");
                        finish();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getApplicationContext(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getApplicationContext(), "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 去评价
     */
    private void processToEvaluate(){
        if(mDetailData != null) {
            Intent evaluateIntent = new Intent(this, EidtOrShowEvaluateActivity.class);
            evaluateIntent.putExtra(EidtOrShowEvaluateActivity.KEY_ORDER_DATA, mDetailData);
            startActivity(evaluateIntent);
        }
    }

    @Subscribe
    public void onEventFinish(FinishActivityEvent event) {
        processToEvaluate();
        L.i("未支付 onEventFinish set false");
        L.i("XXXYYY APP_IS_UN_PAY SET VALUE = FALSE");
        mSettings.APP_IS_UN_PAY.setValue(false);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
