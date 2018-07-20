package com.noteworth.lunch.common.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dominicbarone on 12/6/17.
 */

public class BaseRecyclerViewAdapter<T extends Object> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> mValues;
    private Context mContext;

    public BaseRecyclerViewAdapter(Context context) {
        mValues = new ArrayList<>();
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void swapData(List<T> newValues) {
        this.mValues.clear();
        this.mValues.addAll(newValues);
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BaseViewHolder viewHolder;
        viewHolder = new BaseViewHolder(new LinearLayout(parent.getContext()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        T item = mValues.get(position);
        holder.onBindViewHolder(item);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        holder.onViewRecycled();
    }

    @Override
    public boolean onFailedToRecycleView(BaseViewHolder holder) {
        holder.onFailedToRecycleView();
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        holder.onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        holder.onViewDetachedFromWindow();
    }
}
