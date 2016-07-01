package com.smart.mirrorer.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter2LineData<T> extends BaseAdapter {

    /**
     * 只显示最前面两条的adapter
     */
    protected List<T> mData;
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected int[] layoutIds;
    protected int[] itemTypes;
    // 是否复用convertview
    protected boolean isMultiplexConvertView = true;

    public CommonAdapter2LineData(Context ctx, int layoutId) {
        this(ctx, null, layoutId);
    }

    public CommonAdapter2LineData(Context ctx, List<T> lists, int layoutId) {
        this.mData = lists;
        this.mInflater = LayoutInflater.from(ctx);
        this.mContext = ctx;
        layoutIds = new int[1];
        itemTypes = new int[1];
        this.layoutIds[0] = layoutId;
        this.itemTypes[0] = 0;
    }

    public CommonAdapter2LineData(Context ctx, List<T> lists, int[] itemTypes, int[] layoutIds) {
        this.mData = lists;
        this.mInflater = LayoutInflater.from(ctx);
        this.mContext = ctx;
        this.itemTypes = itemTypes;
        this.layoutIds = layoutIds;
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 0;
        else if(mData.size()<=2)
            return mData.size();
        else return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        if (mData == null)
            return null;

        T t = null;
        try {
            t = mData.get(position);
        } catch (Exception e) {
        }

        return t;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemTypes.length > 1) {
            throw new RuntimeException("child must import getTiemViewType");
        }
        return itemTypes[0];
    }

    @Override
    public int getViewTypeCount() {
        return itemTypes.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        ViewHolderEntity vh = getViewHolder(convertView, mContext, parent, layoutIds[itemViewType], position);
        T itemData = null;
        if (mData != null && mData.size() > position) {
            itemData = mData.get(position);
        }

        convert(vh, itemData, itemViewType);
        return vh.getConvertView();
    }

    abstract protected void convert(ViewHolderEntity vh, T itemData, int itemViewType);

    public boolean isEmpty() {
        if (mData == null || mData.size() == 0)
            return true;
        else
            return false;
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void add(T data) {
        if (mData != null && data != null) {
            mData.add(data);
        }
    }

    public void add(int index, T data) {
        if (mData != null && data != null) {
            mData.add(index, data);
        }
    }

    public void addAll(List<T> data) {
        if (data == null)
            return;

        if (mData == null) {
            mData = data;
        } else {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addAll(int position, List<T> data) {
        if (mData != null && data != null) {
            mData.addAll(position, data);
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void remove(T t) {
        if (mData != null) {
            mData.remove(t);
        }
    }

    public void removeAll() {
        if (mData != null) {
            mData.clear();
            mData = null;
        }
    }

    public void removeAll(List<T> data) {
        if (mData != null) {
            mData.removeAll(data);
        }
    }

    private ViewHolderEntity getViewHolder(View convertView, Context context, ViewGroup parent, int layoutId,
                                           int position) {
        if (isMultiplexConvertView && null != convertView) {
            ViewHolderEntity entity = (ViewHolderEntity) convertView.getTag();
            // 更新position
            entity.setPosition(position);
            return entity;
        }
        return new ViewHolderEntity(context, parent, layoutId, position);
    }

    public class ViewHolderEntity {

        private final SparseArray<View> mView;
        private View mConvertView;
        private ViewGroup mParentView;
        private int mPosition;

        public ViewHolderEntity(Context context, ViewGroup parent, int layoutId, int position) {
            this.mView = new SparseArray<View>();
            this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            this.mConvertView.setTag(this);
            this.mParentView = parent;
            this.mPosition = position;
        }

        public <T extends View> T getView(int id) {
            View view = mView.get(id);
            if (null == view) {
                view = mConvertView.findViewById(id);
                mView.put(id, view);
            }
            return (T) view;
        }

        public View getConvertView() {
            return mConvertView;
        }

        public ViewGroup getParentView() {
            return mParentView;
        }

        public int getPosition() {
            return mPosition;
        }

        public void setPosition(int position) {
            this.mPosition = position;
        }
    }

}
