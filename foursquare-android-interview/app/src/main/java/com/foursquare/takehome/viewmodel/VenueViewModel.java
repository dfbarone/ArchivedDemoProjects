package com.foursquare.takehome.viewmodel;

import android.app.Activity;

import com.foursquare.takehome.model.VenueModel;
import com.foursquare.takehome.model.data.Person;
import com.foursquare.takehome.model.data.Venue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.reactivex.Single;

public class VenueViewModel {

    private VenueModel mMainActivityModel;

    public VenueViewModel(Activity activity) {
        mMainActivityModel = new VenueModel(activity);
    }

    public Single<Venue> parseVenueFromResponse() {
        return mMainActivityModel.parseVenueFromResponse();
    }

    public static String formatTime(Long timeSinceEpoch) {
        // Convert seconds since 1970 to ms since 1970.
        Date date = new Date(timeSinceEpoch * 1000);
        // Format as expected hours, minutes and AM/PM
        DateFormat format = new SimpleDateFormat("HH:mm a");
        // TBD which timezone is correct?
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    public static String formatTimeRange(Person person) {
        // Format dash between arrival and departure time
        return String.format("%s - %s",
                formatTime(person.getArriveTime()),
                formatTime(person.getLeaveTime()));
    }

}
