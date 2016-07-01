package com.smart.mirrorer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.smart.mirrorer.R;


/**
 * 用于个人资料item的显示 将几个系统原生的控件组合到一起，这样创建出的控件就被称为组合控件
 *
 * @author lzm
 */
public class UIItem extends DividerLineLayout {
    private TextView leftTextView;
    private TextView rightTextView;
    private ImageView arrowIv;

    public UIItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIItem);

        String leftText = a.getString(R.styleable.UIItem_leftText);
        String rightText = a.getString(R.styleable.UIItem_rightText);
        a.recycle();
        inflate(context, R.layout.item_ui, this);

        leftTextView = (TextView) findViewById(R.id.item_tvLeft);
        rightTextView = (TextView) findViewById(R.id.item_tvRight);
        arrowIv = (ImageView) findViewById(R.id.item_right_iv);
        //
        leftTextView.setText(leftText);
        rightTextView.setText(rightText);
    }

    public void setLeftText(String text) {
        leftTextView.setText(text);
    }

    public void setRightText(String text) {
        rightTextView.setText(text);
    }

    public TextView getLeftTextView() {
        return leftTextView;
    }
    public TextView getRightTextView() {
        return rightTextView;
    }

    public ImageView getArrowIv() {
        return arrowIv;
    }
}
