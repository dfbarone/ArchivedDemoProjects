package com.foursquare.takehome.view.viewholder;

import android.graphics.Color;
import android.view.ViewGroup;

import com.foursquare.takehome.model.data.Person;

public class EmptyViewHolder extends PersonViewHolder {

    public EmptyViewHolder(ViewGroup parent) {
        super(parent);
    }

    public void bindViewHolder(Person person) {
        super.bindViewHolder(person);

        // Grey out textview colors
        mTitle.setTextColor(Color.LTGRAY);
        mTime.setTextColor(Color.LTGRAY);
    }
}
