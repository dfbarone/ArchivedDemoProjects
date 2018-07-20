package com.noteworth.lunch.model;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.noteworth.lunch.model.feature.GooglePlacesApi;
import com.noteworth.lunch.model.feature.LunchWebClient;

/**
 * Created by dfbarone on 2/12/2018.
 */

public class LunchModel {

    public static final String KEY_FILTER = "filter";
    public static final String KEY_SEARCH = "search";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE= "longitude";

    // Application context
    private static Context mApplicationContext;
    private SharedPreferences mSharedPreferences;
    private LunchWebClient mLunchWebClient;
    private GooglePlacesApi mGooglePlacesApi;

    private static LunchModel mInstance;

    public static LunchModel getInstance() {
        if (mInstance == null) {
            mInstance = new LunchModel();
        }
        return mInstance;
    }

    public static void init(Application application) {
        mApplicationContext = application;
    }

    private LunchModel() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
        mLunchWebClient = new LunchWebClient(mApplicationContext);
        mGooglePlacesApi = new GooglePlacesApi(mSharedPreferences, mLunchWebClient);
    }

    public GooglePlacesApi getGooglePlacesApi() {
        return mGooglePlacesApi;
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }
}
