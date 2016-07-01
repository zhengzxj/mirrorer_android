package com.smart.mirrorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smart.mirrorer.R;

import java.util.List;

/**
 * Created by lzm on 16/3/25.
 */
public class QuestionRecordAdapter extends RecyclerView.Adapter {

    private List<String> mListDatas;


    public void setListData(List<String> listDatas) {
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_record_layout, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

//        ViewHolder viewHolder = (ViewHolder) holder;


    }

    @Override
    public int getItemCount() {
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }
}
