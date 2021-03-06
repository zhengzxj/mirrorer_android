package com.smart.mirrorer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.CommonTutorItemData;
import com.smart.mirrorer.home.TutorFileActivity;
import com.smart.mirrorer.view.CornerImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/25.
 */
public class RecommonLikeFollowAdapter extends RecyclerView.Adapter {

    private boolean mIsFollowList;
    private List<CommonTutorItemData> mListDatas;

    private Context mContext;

    public RecommonLikeFollowAdapter(Context context, boolean isFollowList) {
        this.mContext = context;
        this.mIsFollowList = isFollowList;
    }

    public void setListData(List<CommonTutorItemData> listDatas) {
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CornerImageView mHeadIv;
        private TextView mNickTv;
        private TextView mCompanyTv;
        private TextView mPositionTv;
        private TextView mStartTv;
        private TextView mMinuteTv;
        private TextView mStarCountTv;
        private TextView mVideoIv;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tutorIntent = new Intent(mContext, TutorFileActivity.class);
                    tutorIntent.putExtra(TutorFileActivity.KEY_ISNOT_TUTOR, false);
                    tutorIntent.putExtra(TutorFileActivity.KEY_USE_UID, mListDatas.get(getAdapterPosition()).getUid());
                    mContext.startActivity(tutorIntent);
                }
            });
//            statuIv = (ImageView) itemView.findViewById(R.id.recommon_like_cell_online_statu_iv);


            mHeadIv = (CornerImageView) itemView.findViewById(R.id.recommon_like_cell_head_iv);
            mNickTv = (TextView) itemView.findViewById(R.id.liked_nick_tv);
            mCompanyTv = (TextView) itemView.findViewById(R.id.liked_company_tv);
            mPositionTv = (TextView) itemView.findViewById(R.id.liked_position_tv);
            mStartTv = (TextView) itemView.findViewById(R.id.liked_start_price_tv);
            mMinuteTv = (TextView) itemView.findViewById(R.id.liked_minute_price_tv);
            mStarCountTv = (TextView) itemView.findViewById(R.id.recommon_like_cell_right_count_tv);
            mVideoIv = (TextView) itemView.findViewById(R.id.recommon_like_cell_video_icon);


            mVideoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonTutorItemData itemData = mListDatas.get(getAdapterPosition());
                    if(itemData == null) {
                        return;
                    }
                    if(mListener != null) {
                        mListener.callVideo(itemData);
                    }
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        CommonTutorItemData itemData = mListDatas.get(position);
        if (itemData == null) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;
//        if (mIsFollowList) {
//            viewHolder.statuIv.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.statuIv.setVisibility(View.GONE);
//        }

        String headUrl = itemData.getHeadImgUrl();
        if (!TextUtils.isEmpty(headUrl)) {
            BaseApplication.mImageLoader.displayImage(headUrl, viewHolder.mHeadIv, BaseApplication.headOptions);
        }
        viewHolder.mNickTv.setText(itemData.getNickName());
        viewHolder.mCompanyTv.setText(itemData.getCompany());
        viewHolder.mPositionTv.setText(itemData.getTitle());
        viewHolder.mStartTv.setText(mContext.getResources().getString(R.string.tutor_five_minute_price, itemData.getStartPrice() + ""));
        viewHolder.mMinuteTv.setText(mContext.getResources().getString(R.string.tutor_minute_price, itemData.getMinutePrice() + ""));
        viewHolder.mStarCountTv.setText(itemData.getStar() + "");

    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }

    private ICallVideoListener mListener;
    public void setVideoCallListener(ICallVideoListener listener) {
        this.mListener = listener;
    }
    public interface ICallVideoListener{
        void callVideo(CommonTutorItemData itemData);
        void playVideo(String mVideoUrl);
    }
}
