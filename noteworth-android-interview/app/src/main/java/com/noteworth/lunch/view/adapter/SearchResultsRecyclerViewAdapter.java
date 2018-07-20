package com.noteworth.lunch.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.arlib.floatingsearchview.util.Util;
import com.noteworth.lunch.R;
import com.noteworth.lunch.common.view.BaseRecyclerViewAdapter;
import com.noteworth.lunch.common.view.BaseViewHolder;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.view.viewholder.SearchResultViewHolder;

public class SearchResultsRecyclerViewAdapter extends BaseRecyclerViewAdapter<Result> {

    private int mLastAnimatedItemPosition = -1;

    public SearchResultsRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_search_results, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }
}
