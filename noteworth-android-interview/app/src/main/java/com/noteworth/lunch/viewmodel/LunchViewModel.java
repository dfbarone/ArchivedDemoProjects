package com.noteworth.lunch.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.noteworth.lunch.model.LunchModel;
import com.noteworth.lunch.model.data.json.place.search.GooglePlacesResults;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.model.feature.GooglePlacesApi;
import com.noteworth.lunch.viewmodel.feature.LocationViewModel;
import com.noteworth.lunch.viewmodel.feature.PlaceViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by dfbarone on 2/12/2018.
 */

public class LunchViewModel extends LocationViewModel {

    private String mLastQuery = "";
    private int mCurrentItem = 0;
    private MutableLiveData<GooglePlacesResults> mSearchResult;

    public void onResume() {
        SharedPreferences sharedPref = mLunchModel.getSharedPreferences();
        mLastQuery = sharedPref.getString(LunchModel.KEY_SEARCH, "");
        mCurrentItem = sharedPref.getInt(LunchModel.KEY_FILTER, 0);
    }

    public void onPause() {
        SharedPreferences sharedPref = mLunchModel.getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LunchModel.KEY_SEARCH, mLastQuery);
        editor.putInt(LunchModel.KEY_FILTER, mCurrentItem);

        if (getGoogleLocation() != null) {
            editor.putString(LunchModel.KEY_LATITUDE, String.valueOf(getGoogleLocation().getLatitude()));
            editor.putString(LunchModel.KEY_LONGITUDE, String.valueOf(getGoogleLocation().getLongitude()));
        }

        editor.commit();
    }

    public String getLastQuery() {
        return mLastQuery;
    }

    public void setLastQuery(String mLastQuery) {
        this.mLastQuery = mLastQuery;
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int mCurrentItem) {
        this.mCurrentItem = mCurrentItem;
    }

    public MutableLiveData<GooglePlacesResults> searchResult(String search) {

        if (mSearchResult == null) {
            mSearchResult = new MutableLiveData<>();
        }

        mLunchModel.getGooglePlacesApi().getSearchResult(getGoogleLocation(), search, getCurrentItem() == 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next -> {
                    mSearchResult.setValue(next);
                }, error -> {
                    mSearchResult.setValue(null);
                });

        return mSearchResult;
    }

}
