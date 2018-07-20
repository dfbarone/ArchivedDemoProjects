package com.noteworth.lunch.common.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dominicbarone on 5/13/2017.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public Object mItem;

    public BaseViewHolder(View view) {
        super(view);
    }

    public void onBindViewHolder(Object item) {
        mItem = item;
    }

    public void onViewRecycled() {
    }

    public void onFailedToRecycleView() {
    }

    public void onViewAttachedToWindow() {
    }

    public void onViewDetachedFromWindow() {
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
