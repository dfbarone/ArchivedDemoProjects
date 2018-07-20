package com.foursquare.takehome.view.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foursquare.takehome.model.data.Person;
import com.foursquare.takehome.R;
import com.foursquare.takehome.viewmodel.VenueViewModel;

public class PersonViewHolder extends BaseViewHolder {

    protected TextView mTitle;
    protected TextView mTime;

    public PersonViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.person_layout, null));

        mTitle = (TextView) itemView.findViewById(R.id.title);
        mTime = (TextView) itemView.findViewById(R.id.time);
    }

    public void bindViewHolder(Person person) {
        super.bindViewHolder(person);

        mTitle.setText(person.getName());
        mTime.setText(VenueViewModel.formatTimeRange(person));
    }
}
