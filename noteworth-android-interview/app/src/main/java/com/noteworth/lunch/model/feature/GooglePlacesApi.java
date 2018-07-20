package com.noteworth.lunch.model.feature;

import android.content.SharedPreferences;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.noteworth.lunch.BuildConfig;
import com.noteworth.lunch.model.LunchModel;
import com.noteworth.lunch.model.data.json.place.details.GooglePlacesResult;
import com.noteworth.lunch.model.data.json.place.search.GooglePlacesResults;

import io.reactivex.Observable;

/**
 * Created by dfbarone on 2/12/2018.
 */

public class GooglePlacesApi {

    private static final String TAG = GooglePlacesApi.class.getName();

    private static final String API_KEY = BuildConfig.GooglePlacesApiKey;

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    private static final String SEARCH_BY_RELEVANCE = BASE_URL +  "nearbysearch/json?location=%s&radius=32184&type=restaraunt&keyword=%s&key=%s";
    private static final String SEARCH_BY_DISTANCE = BASE_URL +  "nearbysearch/json?location=%s&rankby=distance&type=restaraunt&keyword=%s&key=%s";
    private static final String DETAILS = BASE_URL + "details/json?placeid=%s&key=%s";
    private static final String PHOTO = BASE_URL + "photo?maxwidth=%s&maxheight=%s&photoreference=%s&key=%s";

    private SharedPreferences mSharedPreferences;
    private LunchWebClient mHttpClient;

    public GooglePlacesApi(SharedPreferences prefs, LunchWebClient webclient) {
        mSharedPreferences = prefs;
        mHttpClient = webclient;
    }

    public Observable<GooglePlacesResults> getSearchResult(Location location, String search, boolean sortByDistance) {

        String latLong;
        if (location == null) {
            latLong = mSharedPreferences.getString(LunchModel.KEY_LATITUDE, "") + "," + mSharedPreferences.getString(LunchModel.KEY_LONGITUDE, "");
            if (TextUtils.isEmpty(latLong)) {
                return Observable.empty();
            }
        } else {
            latLong = (location.getLatitude() + "," + location.getLongitude());
        }

        search = search.replace(" ", "+");
        String url = String.format(sortByDistance ? SEARCH_BY_DISTANCE : SEARCH_BY_RELEVANCE,
                latLong,
                search,
                API_KEY);

        return mHttpClient.get(url, GooglePlacesResults.class)
                .doOnNext(result -> {
                    if (!result.getStatus().equalsIgnoreCase("ok")) {
                        Log.d(TAG, "getSearchResult status " + result.getStatus());
                    }
                });
    }

    public Observable<GooglePlacesResult> getDetails(String placeId) {
        String url = String.format(DETAILS, placeId, API_KEY);
        return mHttpClient.get(url, GooglePlacesResult.class)
                .doOnNext(result -> {
                    if (!result.getStatus().equalsIgnoreCase("ok")) {
                        Log.d(TAG, "getDetails status " + result.getStatus());
                    }
                });
    }

    public static String getImageUrl(String photoReference, int width, int height) {
        return String.format(PHOTO, String.valueOf(width), String.valueOf(height), photoReference, API_KEY);
    }

}
