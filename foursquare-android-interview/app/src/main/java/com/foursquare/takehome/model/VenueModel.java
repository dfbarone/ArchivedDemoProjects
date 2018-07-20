package com.foursquare.takehome.model;

import android.content.Context;

import com.foursquare.takehome.model.data.PeopleHereJsonResponse;
import com.foursquare.takehome.model.data.Person;
import com.foursquare.takehome.model.data.Venue;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;

public class VenueModel {

    private Context mContext;

    public VenueModel(Context context) {
        mContext = context;
    }

    public static Venue reorderVisitorList(Venue venue) {
        final int ONE_MINUTE = 60;
        // Order visitors chronologically based on arrival time O(n)
        // Assumption: No more than one visitor arrives at any one time
        Map<Long, Person> visitorsMap = new TreeMap<>();
        for (Person visitor : venue.getVisitors()) {
            visitorsMap.put(visitor.getArriveTime(), visitor);
        }

        // Copy visitorsMap values to list O(n)
        List<Person> orderedVisitorsList = new ArrayList<>(visitorsMap.values());
        // Find idle time between visitors O(n)
        Person lastVisitor = null;
        for (Person visitor : orderedVisitorsList) {
            if (lastVisitor != null) {
                // If idle time > 1 minute, add "no visitors" spot
                long idleTime = visitor.getArriveTime() - lastVisitor.getLeaveTime();
                if (idleTime >= ONE_MINUTE) {
                    Person noVisitor = new Person();
                    noVisitor.setId(-1);
                    noVisitor.setName("No Visitors");
                    noVisitor.setArriveTime(lastVisitor.getLeaveTime());
                    noVisitor.setLeaveTime(visitor.getArriveTime());
                    // Insert empty placeholder in an unused spot in our map
                    // +1 so it won't take a spot of a pre-existing visitor
                    visitorsMap.put(noVisitor.getArriveTime() + 1, noVisitor);
                }
            }
            // Remember the last visitor
            lastVisitor = visitor;
        }

        // Copy vistorsMap values to list O(n)
        venue.setVisitors(new ArrayList<>(visitorsMap.values()));

        // Performance O(4n) = O(n)
        return venue;
    }

    /**
     * Parsing a fake json response from assets/people.json
     */
    public Single<Venue> parseVenueFromResponse() {
        return Single.fromObservable(Observable.fromCallable(new Callable<Venue>() {
            @Override
            public Venue call() throws Exception {
                try {
                    InputStream is = mContext.getAssets().open("people.json");
                    InputStreamReader inputStreamReader = new InputStreamReader(is);

                    PeopleHereJsonResponse response = new Gson().fromJson(inputStreamReader, PeopleHereJsonResponse.class);
                    Venue venue = reorderVisitorList(response.getVenue());
                    return venue;
                } catch (Exception e) {}

                return null;
            }
        }));
    }
}
