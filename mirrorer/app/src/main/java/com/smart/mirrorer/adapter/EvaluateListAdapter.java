package com.smart.mirrorer.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.bean.home.EvaluateListBean;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.mUtil.L;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lzm on 16/3/25.
 */
public class EvaluateListAdapter extends RecyclerView.Adapter {

    private List<EvaluateListBean.ResultBean> mListDatas;

    public void setListData(List<EvaluateListBean.ResultBean> listDatas) {
        L.i("evaluate setListData");
        this.mListDatas = listDatas;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView iv_head;
        TextView tv_name;
        TextView tv_start_count;
        TextView tv_time;
        TextView tv_tag1;
        TextView tv_tag2;
        TextView tv_tag3;
        TextView tv_content;

        public ViewHolder(View view) {
            super(view);
            L.i("evaluate MyViewHolder");
            iv_head = (CircleImageView) view.findViewById(R.id.iv_head);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_start_count = (TextView) view.findViewById(R.id.tv_start_count);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_tag1 = (TextView) view.findViewById(R.id.tv_tag1);
            tv_tag2 = (TextView) view.findViewById(R.id.tv_tag2);
            tv_tag3 = (TextView) view.findViewById(R.id.tv_tag3);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        L.i("evaluate onCreateViewHolder");
        L.i("parent = "+parent);
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_evaluate_list,parent, null));
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_evaluate_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        L.i("evaluate onBindViewHolder");
        if (mListDatas == null || mListDatas.isEmpty()) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) holder;

        EvaluateListBean.ResultBean resultBean = mListDatas.get(position);
//            holder.tv.setText(eval.get(position));
        if(!TextUtils.isEmpty(resultBean.getHeadImgUrl()))
        {
            BaseApplication.mImageLoader.displayImage(resultBean.getHeadImgUrl(),viewHolder.iv_head,BaseApplication.headOptions);
        }
        viewHolder.tv_name.setText(resultBean.getRealName());
        viewHolder.tv_start_count.setText(resultBean.getScore());
        viewHolder.tv_content.setText(resultBean.getBody());
        ArrayList<TextView> tags = new ArrayList<TextView>();
        tags.add(viewHolder.tv_tag1);
        tags.add(viewHolder.tv_tag2);
        tags.add(viewHolder.tv_tag3);
        if (resultBean.getTags_list()!=null&&resultBean.getTags_list().size()!=0)
        {
            for (int i = 0;i<resultBean.getTags_list().size()&&i<tags.size();i++)
            {
                tags.get(i).setText(resultBean.getTags_list().get(i));
                tags.get(i).setVisibility(View.VISIBLE);
            }
        }
        viewHolder.tv_time.setText(resultBean.getCreatedAt());

    }

    @Override
    public int getItemCount() {
        L.i("evaluate getItemCount");
        if (mListDatas == null || mListDatas.isEmpty()) {
            return 0;
        }
        return mListDatas.size();
    }
}
