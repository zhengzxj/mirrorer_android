package com.smart.mirrorer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.smart.mirrorer.R;
import com.smart.mirrorer.bean.home.QuestionListBean;
import com.smart.mirrorer.util.DeviceConfiger;
import com.smart.mirrorer.util.IDateUtil;

/**
 * Created by lzm on 16/3/25.
 */
public class HomeQuestionAdapter extends CommonAdapter2LineData<QuestionListBean.ResultBean.ListBean> implements SwipeMenuCreator {

    private Context mContext;

    public HomeQuestionAdapter(Context ctx, int layoutId) {
        super(ctx, layoutId);
        mContext = ctx;
    }

    @Override
    protected void convert(ViewHolderEntity entity, final QuestionListBean.ResultBean.ListBean itemData, int itemViewType) {
        TextView contentTv = entity.getView(R.id.question_content_tv);
        TextView videoIv = entity.getView(R.id.question_video_icon);
        TextView timeTv = entity.getView(R.id.question_time_tv);

        if (itemData == null) {
            return;
        }

        contentTv.setText(itemData.getContent());
        timeTv.setText(itemData.getCreateAt());

        videoIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)v).setTextColor(Color.parseColor("#000000"));
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView)v).setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((TextView)v).setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
                return false;
            }
        });
        videoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!= null) {

                    mListener.matchHisitory(itemData);
                }
            }
        });
    }

    private ICallHistoryListener mListener;
    public void setCallListener(ICallHistoryListener listener) {
        this.mListener = listener;
    }
    public interface ICallHistoryListener{
        void matchHisitory(QuestionListBean.ResultBean.ListBean itemData);
    }

    @Override
    public void create(SwipeMenu menu) {
        // create "delete" item
        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
        // set item background
        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                0x3F, 0x25)));
        // set item width
        deleteItem.setWidth(DeviceConfiger.dp2px(90));
        deleteItem.setTitle("删除");
        // set item title fontsize
        deleteItem.setTitleSize(DeviceConfiger.dp2sp(16));
        // set item title font color
        deleteItem.setTitleColor(Color.WHITE);
        // add to menu
        menu.addMenuItem(deleteItem);
    }

}