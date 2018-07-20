package com.foursquare.takehome.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.foursquare.takehome.model.data.Person;
import com.foursquare.takehome.view.viewholder.BaseViewHolder;
import com.foursquare.takehome.view.viewholder.EmptyViewHolder;
import com.foursquare.takehome.view.viewholder.PersonViewHolder;

import java.util.ArrayList;
import java.util.List;


final public class PersonAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int VIEW_TYPE_EMPTY = 0;
    private final int VIEW_TYPE_PERSON = 1;

    private List<Person> mData = new ArrayList<>();

    public void update(List<Person> people) {
        mData = people;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_PERSON:
                return new PersonViewHolder(parent);
            case VIEW_TYPE_EMPTY:
            default:
                return new EmptyViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        Person person = mData.get(position);
        holder.bindViewHolder(person);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        Person person = mData.get(position);
        if (person.getId() > -1) {
            // This is a person without a valid id
            return VIEW_TYPE_PERSON;
        }

        return VIEW_TYPE_EMPTY;
    }
}
