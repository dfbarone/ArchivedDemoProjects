package com.noteworth.lunch.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noteworth.lunch.R;
import com.noteworth.lunch.common.view.BaseRecyclerViewAdapter;
import com.noteworth.lunch.common.view.BaseViewHolder;
import com.noteworth.lunch.model.data.json.place.Photo;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.view.viewholder.PhotoViewHolder;
import com.noteworth.lunch.view.viewholder.SearchResultViewHolder;

public class PhotoRecyclerViewAdapter extends BaseRecyclerViewAdapter<Photo> {

    public PhotoRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_photo, parent, false);
        return new PhotoViewHolder(view);
    }

}
