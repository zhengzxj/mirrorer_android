package com.smart.mirrorer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.smart.mirrorer.R;
import com.smart.mirrorer.bean.home.EvaluateTagsBean;
import com.smart.mirrorer.bean.home.QuestionListBean;
import com.smart.mirrorer.util.DeviceConfiger;
import com.smart.mirrorer.util.MaterialUtils;
import com.smart.mirrorer.view.NoToggleCheckBox;

/**
 * Created by lzm on 16/3/25.
 */
public class EvaluateTagAdapter extends CommonAdapter<EvaluateTagsBean.ResultBean.TagListBean> {

    private Context mContext;

    public EvaluateTagAdapter(Context ctx, int layoutId) {
        super(ctx, layoutId);
        mContext = ctx;
    }

    private boolean isUnCheck;
    public void setUnCheck(){
        isUnCheck = true;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(ViewHolderEntity entity, final EvaluateTagsBean.ResultBean.TagListBean itemData, int itemViewType) {
        NoToggleCheckBox checkBox = entity.getView(R.id.item_evaluate_tag_checkbox);
        Drawable bgDraw = MaterialUtils.createEvaluateCanSelectSolidStrokeBg(Color.parseColor("#999999"), Color.parseColor("#ffba00"));
        checkBox.setBackgroundDrawable(bgDraw);
        checkBox.setButtonDrawable(new BitmapDrawable());
        checkBox.setText(itemData.getTag_name());
        if (itemData.isChecked) {
            checkBox.setChecked(true);
            checkBox.setTextColor(Color.parseColor("#ffba00"));
        } else {
            checkBox.setChecked(false);
            checkBox.setTextColor(Color.parseColor("#666666"));
        }

        int size = DeviceConfiger.dp2sp(14);
        checkBox.setTextSize(size);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemData.isChecked) {
                    itemData.isChecked = false;
                } else {
                    itemData.isChecked = true;
                }
                notifyDataSetChanged();
            }
        });

        if(isUnCheck) {
            checkBox.setClickable(false);
            checkBox.setEnabled(false);
        }
    }


}
