package com.noteworth.lunch.view.viewholder;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.noteworth.lunch.R;
import com.noteworth.lunch.common.listeners.OnFragmentInteractionListener;
import com.noteworth.lunch.common.view.BaseViewHolder;
import com.noteworth.lunch.model.data.json.place.Photo;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.viewmodel.LunchViewModel;
import com.noteworth.lunch.viewmodel.feature.PlaceViewModel;

/**
 * Created by dfbarone on 2/13/2018.
 */

public class PhotoViewHolder extends BaseViewHolder {

    public final ImageView mImage;

    private LunchViewModel mViewModel;

    public PhotoViewHolder(View view) {
        super(view);

        mImage = (ImageView)view.findViewById(R.id.image);

        if (getContext() instanceof FragmentActivity) {
            mViewModel = ViewModelProviders.of((FragmentActivity)getContext()).get(LunchViewModel.class);
        } else {
            mViewModel = new LunchViewModel();
        }

    }

    @Override
    public void onBindViewHolder(Object item) {
        super.onBindViewHolder(item);

        if (mItem instanceof Photo) {
            Photo photo = (Photo) item;

            // Load image
            mViewModel.loadImage(getContext(),
                    LunchViewModel.getImageUrl(photo, 1200, 675),
                    mImage,
                    R.drawable.placeholder_square);

        }
    }
}
