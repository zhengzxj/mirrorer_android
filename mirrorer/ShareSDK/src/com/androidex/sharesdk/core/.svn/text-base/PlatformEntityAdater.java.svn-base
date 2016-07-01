package com.androidex.sharesdk.core;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidex.sharesdk.R;

public class PlatformEntityAdater extends BaseAdapter {

    private Context mContext;
    private List<PlatformEntity> mList;

    public PlatformEntityAdater(Context context, List<PlatformEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup arg2) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.share_item, null);
            holder = new ViewHolder();
            holder.text1 = (TextView) view.findViewById(R.id.tv_text);
            holder.icon1 = (ImageView) view.findViewById(R.id.iv_mylist);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.text1.setText(mList.get(pos).name);
        holder.icon1.setImageResource(mList.get(pos).icon);
        return view;
    }

    static class ViewHolder {
        TextView text1;
        ImageView icon1;
    }
}
