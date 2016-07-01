package com.smart.mirrorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.util.mUtil.L;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/25.
 */
public class MidTutorMatchAdapter extends RecyclerView.Adapter {

    private List<PushQuestionBean> mListDatas;

    public void setListData(List<PushQuestionBean> listDatas) {
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView iv_head;//头像
        private TextView mQuestionTv;//问题内容
        private TextView tv_stu_name;//回答者名字
        private TextView tv_start;//星星
        private TextView tv_q_num;//问题数
        private TextView mRobTv;//抢单
        private ImageView iv_show_more_or_less;//更多或少
        private LinearLayout ll_stu_detail;
        private TextView tv_blank;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_stu_detail = (LinearLayout)itemView.findViewById(R.id.ll_stu_detail);

            iv_head= (CircleImageView)itemView.findViewById(R.id.iv_head);
            tv_stu_name = (TextView) itemView.findViewById(R.id.tv_stu_name);
            tv_blank = (TextView) itemView.findViewById(R.id.tv_blank);
            tv_start = (TextView) itemView.findViewById(R.id.recommon_like_cell_right_count_tv);
            tv_q_num = (TextView) itemView.findViewById(R.id.tv_q_num);
            iv_show_more_or_less = (ImageView) itemView.findViewById(R.id.iv_show_more_or_less);

            mRobTv = (TextView) itemView.findViewById(R.id.tutor_item_match_rob_tv);
//            mfollowIv = (ImageView) itemView.findViewById(R.id.tutor_item_match_follow_iv);
            mQuestionTv = (TextView) itemView.findViewById(R.id.tutor_item_match_question_tv);

            mRobTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.clickAction(ITutorMatchItemClickListener.WAHT_ACTION_ROB, getAdapterPosition());
                    }
                }
            });

//            mfollowIv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListener != null) {
//                        mListener.clickAction(ITutorMatchItemClickListener.WAHT_ACTION_FOLLOW, getAdapterPosition());
//                    }
//                }
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.clickAction(ITutorMatchItemClickListener.WAHT_ACTION_ITEM, getAdapterPosition());
                    }
                }
            });

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_item_match_question_layout, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        final ViewHolder viewHolder = (ViewHolder) holder;
        PushQuestionBean testItem = mListDatas.get(position);
        if(testItem == null) {
            return;
        }

        /**
         * private de.hdodenhof.circleimageview.CircleImageView iv_head;//头像
         private TextView mQuestionTv;//问题内容
         private TextView tv_stu_name;//回答者名字
         private TextView tv_start;//星星
         private TextView tv_q_num;//问题数
         private TextView mRobTv;//抢单
         private ImageView iv_show_more_or_less;//更多或少
         */
        viewHolder.mQuestionTv.setText(testItem.getQuestion());
        L.i("tv_stu_name = "+testItem.getName());
        viewHolder.tv_stu_name.setText(testItem.getName());
        viewHolder.tv_start.setText(testItem.getStar()+"");
        viewHolder.tv_q_num.setText(testItem.getqCount()+"");
        BaseApplication.mImageLoader.displayImage(testItem.getHeadImgUrl(), viewHolder.iv_head, BaseApplication.headOptions);

        viewHolder.mQuestionTv.post(new Runnable() {
            @Override
            public void run() {
                L.i("question lines AA = "+viewHolder.mQuestionTv.getLineCount());
                if(viewHolder.mQuestionTv.getLineCount()==1)
                {
                    viewHolder.tv_blank.setVisibility(View.VISIBLE);
                    L.i("question lines = "+1);
                }else if(viewHolder.mQuestionTv.getLineCount()==2)
                {
                    L.i("question lines = "+2);
                }else if(viewHolder.mQuestionTv.getLineCount()==3)
                {
                    L.i("question lines = "+3);
                    viewHolder.ll_stu_detail.setVisibility(View.GONE);
                    viewHolder.iv_show_more_or_less.setVisibility(View.VISIBLE);
                }
            }
        });

        viewHolder.iv_show_more_or_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.i("点击了更多 position = "+position+",当前行数 = "+viewHolder.mQuestionTv.getLineCount());
                if(viewHolder.mQuestionTv.getLineCount()==3&&viewHolder.ll_stu_detail.getVisibility()==View.GONE)
                {
                    viewHolder.iv_show_more_or_less.setImageResource(R.drawable.btn_back_up);
                    viewHolder.ll_stu_detail.setVisibility(View.VISIBLE);
                    viewHolder.mQuestionTv.setMaxLines(20);
                }else
                {
                    viewHolder.iv_show_more_or_less.setImageResource(R.drawable.btn_back_down);
                    viewHolder.mQuestionTv.setMaxLines(3);
                    viewHolder.ll_stu_detail.setVisibility(View.GONE);
                }
            }
        });
//        if(testItem.isFollowed) {
//            viewHolder.mfollowIv.setVisibility(View.GONE);
//        } else {
//            viewHolder.mfollowIv.setVisibility(View.VISIBLE);
//        }

        if(testItem.isEnable) {
            viewHolder.mRobTv.setEnabled(true);
//            viewHolder.mfollowIv.setEnabled(true);
        } else {
            viewHolder.mRobTv.setEnabled(false);
//            viewHolder.mfollowIv.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }

    private ITutorMatchItemClickListener mListener;
    public void setItemListener(ITutorMatchItemClickListener listener) {
        this.mListener = listener;
    }

    public interface ITutorMatchItemClickListener{
        int WAHT_ACTION_ROB = 51;
        int WAHT_ACTION_FOLLOW = 52;
        int WAHT_ACTION_ITEM = 53;
        void clickAction(int whatAction, int positon);
    }
}
