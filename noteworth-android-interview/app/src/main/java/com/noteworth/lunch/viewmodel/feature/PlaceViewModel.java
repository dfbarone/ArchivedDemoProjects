package com.noteworth.lunch.viewmodel.feature;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.noteworth.lunch.model.LunchModel;
import com.noteworth.lunch.model.data.json.place.Photo;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.model.feature.GooglePlacesApi;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by dfbarone on 2/16/2018.
 */

public class PlaceViewModel extends BaseLunchViewModel {

    private Result mResult;

    public PlaceViewModel(Result result) {
        mResult = result;
    }

    public String getPrice() {
        String priceLevel = "";
        if (mResult.getPriceLevel() != null) {
            for (int i = 0; i < mResult.getPriceLevel(); i++) {
                priceLevel += "$";
            }
        }
        return priceLevel;
    }

    public String getTags() {
        String tags = "";
        for (String tag : mResult.getTypes()) {
            tag = tag.replace("_", " ");
            tags += tag + ",";
        }
        tags = StringUtils.removeEnd(tags, ",");
        return tags;
    }

    public static String getDistance(Location origin, Result result) {
        String distance = "";
        if (origin != null) {
            Location newLocation = new Location("");
            newLocation.setLatitude(result.getGeometry().getLocation().getLat());
            newLocation.setLongitude(result.getGeometry().getLocation().getLng());

            float distanceInMeters = origin.distanceTo(newLocation);
            float distanceInMiles = distanceInMeters * 0.000621371f;
            distance = String.format(distanceInMiles > 10 ? "%.0f" : "%.1f", distanceInMiles) + "m";
        }
        return distance;
    }

    public String getDistance(Location origin) {
        return getDistance(origin, mResult);
    }

    public float getRating() {
        float rating = 0.0f;
        try {
            rating = mResult.getRating().floatValue();
        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
        return rating;
    }
}
