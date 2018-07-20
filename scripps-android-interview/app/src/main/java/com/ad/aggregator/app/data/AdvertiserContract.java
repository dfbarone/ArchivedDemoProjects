package com.ad.aggregator.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ad.aggregator.app.Utility;

/**
 * Defines table and column names for the database.
 */
public class AdvertiserContract {

    // The "Content authority" is a name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.ad.aggregator.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ADVERTISER = "advertiser";
    public static final String PATH_IMPRESSION = "impression";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        return Utility.normalizeDate(startDate);
    }

    /* Inner class that defines the table contents of the advertiser table */
    public static final class AdvertiserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADVERTISER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADVERTISER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADVERTISER;

        // Table name "advertiser"
        public static final String TABLE_NAME = PATH_ADVERTISER;

        public static final String COLUMN_ADVERTISER_ID = "advertiser_id";
        public static final String COLUMN_IMPRESSION_COUNT = "impression_count";
        public static final String COLUMN_CLICK_COUNT = "click_count";

        public static Uri buildAdvertiserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the impressions table */
    public static final class ImpressionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMPRESSION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMPRESSION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMPRESSION;

        public static final String TABLE_NAME = PATH_IMPRESSION;

        // Column with the foreign key into the advertiser table.
        public static final String COLUMN_ADVERTISER_KEY = "advertiser_key";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_NUM_CLICKS = "num_clicks";
        public static final String COLUMN_NUM_IMPRESSIONS = "num_impressions";

        public static Uri buildImpressionUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getAdvertiserSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
}
