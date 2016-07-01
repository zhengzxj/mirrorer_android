package com.smart.mirrorer.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;

/**
 * Created by lzm on 16/4/28.
 */
public class SelectPayStylePopupWindow extends PopupWindow {

    private RelativeLayout weixinLayout, alipayLayout;
    private ImageView weixinSelIv, alipaySelIv;
    private TextView closeTv;
    private View mMenuView;


    public SelectPayStylePopupWindow(Activity context,View.OnClickListener itemsOnClick, boolean isWeixinType) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_pay_style_sel_layout, null);
        weixinLayout = (RelativeLayout) mMenuView.findViewById(R.id.pop_pay_style_weixin_layout);
        alipayLayout = (RelativeLayout) mMenuView.findViewById(R.id.pop_pay_style_alipay_layout);
        //设置监听
        weixinLayout.setOnClickListener(itemsOnClick);
        alipayLayout.setOnClickListener(itemsOnClick);

        weixinSelIv = (ImageView) mMenuView.findViewById(R.id.pop_weixin_sel_iv);
        alipaySelIv = (ImageView) mMenuView.findViewById(R.id.pop_alipey_sel_iv);

        if(isWeixinType) {
            weixinSelIv.setImageResource(R.drawable.ic_pay_sel);
            alipaySelIv.setImageResource(R.drawable.ic_pay_unsel);
        } else {
            weixinSelIv.setImageResource(R.drawable.ic_pay_unsel);
            alipaySelIv.setImageResource(R.drawable.ic_pay_sel);
        }

        closeTv = (TextView) mMenuView.findViewById(R.id.pop_sel_commit_tv);
        closeTv.setOnClickListener(itemsOnClick);
//        //提交按钮
//        closeTv.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //销毁弹出框
//                dismiss();
//            }
//        });

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
            }
        });

    }

}
