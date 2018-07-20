package com.noteworth.lunch.viewmodel.feature;

import android.app.Activity;
import android.location.Location;
import android.view.View;

import com.noteworth.lunch.model.feature.GooglePlayLocationManager;

/**
 * Created by dfbarone on 2/16/2018.
 */

public class LocationViewModel extends BaseLunchViewModel {

    private GooglePlayLocationManager mGooglePlayLocationManager;

    public Location getGoogleLocation() {
        if (mGooglePlayLocationManager != null) {
            return mGooglePlayLocationManager.getLocation();
        } else {
            return null;
        }
    }

    public void initializeGoogleLocationService(Activity activity, View root) {
        this.mGooglePlayLocationManager = new GooglePlayLocationManager(activity, root);
    }

    public void updateGoogleLocationService() {
        mGooglePlayLocationManager.updateLocation();
    }

}
