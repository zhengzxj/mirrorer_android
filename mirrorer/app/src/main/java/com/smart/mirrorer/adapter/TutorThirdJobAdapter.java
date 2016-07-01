package com.smart.mirrorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.bean.TestTutorInfoBean;

import java.util.List;

/**
 * Created by lzm on 16/3/25.
 */
public class TutorThirdJobAdapter extends RecyclerView.Adapter {

    private List<TestTutorInfoBean> mListDatas;

    public void setListData(List<TestTutorInfoBean> listDatas) {
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mYearTv;
        private TextView mCompanyEduTv;
        private TextView mJobMajorTv;

        public ViewHolder(View itemView) {
            super(itemView);
            mYearTv = (TextView) itemView.findViewById(R.id.job_edu_year_tv);
            mCompanyEduTv = (TextView) itemView.findViewById(R.id.job_edu_one_tv);
            mJobMajorTv = (TextView) itemView.findViewById(R.id.job_edu_two_tv);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_item_job_edu_layout, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        TestTutorInfoBean itemData = mListDatas.get(position);

        if(itemData == null) {
            return;
        }

        ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.mYearTv.setText(itemData.years);
        itemHolder.mCompanyEduTv.setText(itemData.companyOrEdu);
        itemHolder.mJobMajorTv.setText(itemData.jobOrMajor);
    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }
}
