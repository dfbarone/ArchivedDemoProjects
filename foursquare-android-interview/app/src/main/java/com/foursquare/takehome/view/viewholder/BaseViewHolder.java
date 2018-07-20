package com.foursquare.takehome.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.foursquare.takehome.model.data.Person;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    protected Person mData = null;

    public BaseViewHolder(View view) {
        super(view);
    }

    public void bindViewHolder(Person person) {
        mData = person;
    }
}
