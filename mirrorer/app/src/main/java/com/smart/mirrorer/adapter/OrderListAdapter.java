package com.smart.mirrorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseActivity;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.EvaluateListBean;
import com.smart.mirrorer.bean.home.OrderDetailsBean;
import com.smart.mirrorer.bean.home.OrderDetailsData;
import com.smart.mirrorer.bean.home.OrderListBean;
import com.smart.mirrorer.home.EidtOrShowEvaluateActivity;
import com.smart.mirrorer.home.PayOrderConfirmAcitivity;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.util.CommonUtils;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.mUtil.L;
import com.smart.mirrorer.view.CornerImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/25.
 */
public class OrderListAdapter extends RecyclerView.Adapter {

    private List<OrderListBean.ResultBean> mListDatas;
    private Context context;
    private BaseActivity activity;
    public void setListData(List<OrderListBean.ResultBean> listDatas,Context context) {
        this.context = context;
        activity = (BaseActivity)context;
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CornerImageView iv_head;
        TextView tv_name;
        TextView tv_ispay;
        TextView tv_time;
        TextView tv_content;
        TextView tv_whose;

        public ViewHolder(View view) {
            super(view);
            iv_head = (CornerImageView) view.findViewById(R.id.iv_head);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_ispay = (TextView) view.findViewById(R.id.tv_ispay);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_whose = (TextView) view.findViewById(R.id.tv_whose);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //paystatus 2 跳评价页面 1.3 跳支付页面
                    OrderListBean.ResultBean orderInfo = mListDatas.get(getAdapterPosition());
                    switch (orderInfo.getPayStatus())
                    {
                        case 2://支付成功
                            goEvaluat(orderInfo);
                            break;
                        case 1://未支付
                        case 3://支付失败
                            //去支付
                            getOrderDetail(orderInfo.getOrderId());
                            break;
                    }
                }
            });
        }
    }

    //去评价活着看评价
    private void goEvaluat(OrderListBean.ResultBean orderInfo)
    {
        OrderDetailsData odd = formatOderInfo(orderInfo);
        int ratedStatus = orderInfo.getRatedStatus();
        Intent evaluateIntent = new Intent(activity, EidtOrShowEvaluateActivity.class);
        evaluateIntent.putExtra(EidtOrShowEvaluateActivity.KEY_ORDER_DATA, odd);
        if (ratedStatus==0)
        {
            //TODO:去评价
        }else if(ratedStatus==1)
        {
            //TODO:看评价
            evaluateIntent.putStringArrayListExtra("tags",(ArrayList<String>)orderInfo.getTags());
        }
        activity.startActivity(evaluateIntent);
    }
    private OrderDetailsData formatOderInfo(OrderListBean.ResultBean orderInfo)
    {
        /**

         private int qStatus;
         private int payStatus;
         private int ratedStatus;
         上面3个不用转过去
         tags另外传
         private List<String> tags;tags另外转
         */
        OrderDetailsData data = new OrderDetailsData();
        data.setPayMoney(orderInfo.getPayMoney());
        data.setOrderId(orderInfo.getOrderId());
        data.setGuiderHeadUrl(orderInfo.getHeadImgUrl());
        data.setGuiderName(orderInfo.getGuiderName());
        data.setGuiderId(orderInfo.getGuiderId());
        data.setQuestion(orderInfo.getQuestion());
        data.setCreatedAt(orderInfo.getCreatedAt());
        return data;
    }
    private void getOrderDetail(String orderId)
    {
        String uid = new MirrorSettings(activity).APP_UID.getValue();
        CommonUtils.getOrderDetails(activity,uid,orderId,"2",mOrderDetailCallbackGoPay);
    }
    private GsonCallbackListener<OrderDetailsBean> mOrderDetailCallbackGoPay = new GsonCallbackListener<OrderDetailsBean>() {

        @Override
        public void onResultSuccess(OrderDetailsBean orderDetailsBean) {
            super.onResultSuccess(orderDetailsBean);
            activity.dismissLoadDialog();
            if (orderDetailsBean == null) {
                return;
            }

            OrderDetailsData detailData = orderDetailsBean.getResult();
            if (detailData == null) {
                return;
            }

            L.d("订单详情"+detailData.toString());

            detailData.setPayMoney(detailData.getPayMoney()/100);
            Intent payIntent = new Intent(activity, PayOrderConfirmAcitivity.class);
            payIntent.putExtra(PayOrderConfirmAcitivity.KEY_DATA_ORDER, detailData);
            activity.startActivity(payIntent);

        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            activity.dismissLoadDialog();
            TipsUtils.showShort(activity.getApplicationContext(), errorMsg);
        }
    };
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_evaluate_list,parent, null));
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;

        OrderListBean.ResultBean resultBean = mListDatas.get(position);
//            holder.tv.setText(eval.get(position));
        if(!TextUtils.isEmpty(resultBean.getHeadImgUrl()))
        {
            BaseApplication.mImageLoader.displayImage(resultBean.getHeadImgUrl(),viewHolder.iv_head,BaseApplication.headOptions);
        }
        viewHolder.tv_name.setText(resultBean.getGuiderName());
        viewHolder.tv_content.setText(resultBean.getQuestion());
        viewHolder.tv_time.setText(resultBean.getCreatedAt());
        viewHolder.tv_ispay.setTextColor(Color.parseColor("#e82309"));
        switch (resultBean.getPayStatus())
        {
            case 0:
                viewHolder.tv_ispay.setText("刚创建");
                break;
            case 1:
                viewHolder.tv_ispay.setText("未支付");
                break;
            case 2:
                viewHolder.tv_ispay.setText("支付成功");
                viewHolder.tv_ispay.setTextColor(Color.parseColor("#313131"));
                break;
            case 3:
                viewHolder.tv_ispay.setText("支付失败");
                break;
        }
        switch (resultBean.getPayStatus())
        {
            case 1:
                viewHolder.tv_whose.setText("我的提问");
                break;
            case 2:
                viewHolder.tv_whose.setText("我的回答");
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }
}
