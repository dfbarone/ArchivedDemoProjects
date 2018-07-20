package com.noteworth.lunch.view.viewholder;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.noteworth.lunch.R;
import com.noteworth.lunch.common.listeners.OnFragmentInteractionListener;
import com.noteworth.lunch.common.view.BaseViewHolder;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.viewmodel.LunchViewModel;
import com.noteworth.lunch.viewmodel.feature.PlaceViewModel;

/**
 * Created by dfbarone on 2/13/2018.
 */

public class PlaceViewHolder extends BaseViewHolder {

    public final TextView mTitle, mTags, mAddress, mDistance, mPrice;
    public final RatingBar mRating;
    public final View mTextContainer;
    private LunchViewModel mViewModel;

    public PlaceViewHolder(View view) {
        super(view);

        mTextContainer = view.findViewById(R.id.text_container);
        mTitle = (TextView) view.findViewById(R.id.title);
        mRating = (RatingBar) view.findViewById(R.id.rating);
        mTags = (TextView) view.findViewById(R.id.tags);
        mAddress = (TextView) view.findViewById(R.id.address);
        mDistance = (TextView) view.findViewById(R.id.distance);
        mPrice = (TextView) view.findViewById(R.id.price);

        if (getContext() instanceof FragmentActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity) getContext()).get(LunchViewModel.class);
        } else {
            mViewModel = new LunchViewModel();
        }

    }

    @Override
    public void onBindViewHolder(Object item) {
        super.onBindViewHolder(item);

        if (mItem instanceof Result) {
            Result result = (Result) item;

            // Use this view model to do all the heavy lifting in formatting the reslult data
            PlaceViewModel placeViewModel = new PlaceViewModel(result);

            // Set title
            String title = result.getName();
            mTitle.setText(title);

            // Set rating
            Float rating = placeViewModel.getRating();
            mRating.setRating(rating);

            // Set address
            mAddress.setText("");
            if (!TextUtils.isEmpty(result.getFormattedAddress())) {
                mAddress.setText(result.getFormattedAddress());
            }

            // Set tags
            String tags = placeViewModel.getTags();
            mTags.setText(tags);

            // Set price
            String price = placeViewModel.getPrice();
            mPrice.setText(price);

            // Set distance
            String distance = placeViewModel.getDistance(mViewModel.getGoogleLocation());
            mDistance.setText(distance);

        }

    }
}
