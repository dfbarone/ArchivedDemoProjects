package com.noteworth.lunch.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.noteworth.lunch.model.LunchModel;
import com.noteworth.lunch.model.data.json.place.details.GooglePlacesResult;
import com.noteworth.lunch.viewmodel.feature.LocationViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dfbarone on 2/12/2018.
 */

public class LunchDetailViewModel extends LocationViewModel {

    private MutableLiveData<GooglePlacesResult> mDetails;

    public MutableLiveData<GooglePlacesResult> details(String placesId) {
        if (mDetails == null) {
            mDetails = new MutableLiveData<>();
        }

        mLunchModel.getGooglePlacesApi().getDetails(placesId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> {
                    mDetails.setValue(next);
                }, error -> {
                    mDetails.setValue(null);
                });

        return mDetails;
    }
}
