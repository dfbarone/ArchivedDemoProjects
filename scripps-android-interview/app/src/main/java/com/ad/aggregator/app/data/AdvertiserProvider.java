package com.ad.aggregator.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdvertiserProvider extends ContentProvider {
    public static final String CLASSTAG = AdvertiserProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AdvertiserDbHelper mOpenHelper;

    static final int IMPRESSION = 100;
    static final int ADVERTISER = 300;

    private static final SQLiteQueryBuilder sImpressionByAdvertiserQueryBuilder;

    static{
        sImpressionByAdvertiserQueryBuilder = new SQLiteQueryBuilder();
        
        //inner join which looks like impression INNER JOIN advertiser ON impression.advertiser_id = advertiser._id
        sImpressionByAdvertiserQueryBuilder.setTables(
                AdvertiserContract.ImpressionEntry.TABLE_NAME + " INNER JOIN " +
                        AdvertiserContract.AdvertiserEntry .TABLE_NAME +
                        " ON " + AdvertiserContract.ImpressionEntry.TABLE_NAME +
                        "." + AdvertiserContract.ImpressionEntry.COLUMN_ADVERTISER_KEY +
                        " = " + AdvertiserContract.AdvertiserEntry.TABLE_NAME +
                        "." + AdvertiserContract.AdvertiserEntry._ID);
    }

    //advertiser.advertiser_id = ?
    private static final String sAdvertiserIdSelection =
            AdvertiserContract.AdvertiserEntry.TABLE_NAME +
                    "." + AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ? ";

    //advertiser.advertiser_id = ? AND date >= ?
    private static final String sAdvertiserIdWithStartDateSelection =
            AdvertiserContract.AdvertiserEntry.TABLE_NAME +
                    "." + AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ? AND " +
                    AdvertiserContract.ImpressionEntry.COLUMN_DATE + " >= ? ";

    //advertiser.advertiser_id = ? AND date = ?
    private static final String sAdvertiserIdAndDaySelection =
            AdvertiserContract.AdvertiserEntry.TABLE_NAME +
                    "." + AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ? AND " +
                    AdvertiserContract.ImpressionEntry.COLUMN_DATE + " = ? ";

    private Cursor getImpressionGroupByDate(Uri uri, String[] projection, String sortOrder) {
        String query = "SELECT " + AdvertiserContract.ImpressionEntry._ID + ", " +
                AdvertiserContract.ImpressionEntry.COLUMN_ADVERTISER_KEY + ", " +
                AdvertiserContract.ImpressionEntry.COLUMN_DATE +
                ", SUM(" + AdvertiserContract.ImpressionEntry.COLUMN_NUM_IMPRESSIONS + "), " +
                "SUM(" + AdvertiserContract.ImpressionEntry.COLUMN_NUM_CLICKS +
                ") FROM impression GROUP BY date ORDER BY date DESC;";

        return mOpenHelper.getReadableDatabase().rawQuery(query, null);
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AdvertiserContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, AdvertiserContract.PATH_IMPRESSION, IMPRESSION);
        matcher.addURI(authority, AdvertiserContract.PATH_ADVERTISER, ADVERTISER);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AdvertiserDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPRESSION:
                return AdvertiserContract.ImpressionEntry.CONTENT_TYPE;
            case ADVERTISER:
                return AdvertiserContract.AdvertiserEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        int m = sUriMatcher.match(uri);
        Log.d("help", "int: " + m + " uri:" + uri);
        switch (m) {
            // "impression"
            case IMPRESSION: {
                retCursor = getImpressionGroupByDate(uri, projection, sortOrder);
                break;
            }
            // "advertiser"
            case ADVERTISER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AdvertiserContract.AdvertiserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case IMPRESSION: {
                normalizeDate(values);
                long _id = db.insert(AdvertiserContract.ImpressionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AdvertiserContract.ImpressionEntry.buildImpressionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ADVERTISER: {
                long _id = db.insert(AdvertiserContract.AdvertiserEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AdvertiserContract.AdvertiserEntry.buildAdvertiserUri(_id);//AdvertiserContract.AdvertiserEntry.CONTENT_URI;//
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case IMPRESSION:
                rowsDeleted = db.delete(
                        AdvertiserContract.ImpressionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ADVERTISER:
                rowsDeleted = db.delete(
                        AdvertiserContract.AdvertiserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private void normalizeDate(ContentValues values) {
        if (values.containsKey(AdvertiserContract.ImpressionEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(AdvertiserContract.ImpressionEntry.COLUMN_DATE);
            values.put(AdvertiserContract.ImpressionEntry.COLUMN_DATE, AdvertiserContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case IMPRESSION:
                normalizeDate(values);
                rowsUpdated = db.update(AdvertiserContract.ImpressionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ADVERTISER:
                rowsUpdated = db.update(AdvertiserContract.AdvertiserEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case IMPRESSION:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(AdvertiserContract.ImpressionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


    // Utility functions for testing
    public long insertAdvertiser(String advertiserId, String impressions, String clicks) {

        Cursor cr = queryAdvertiser(advertiserId);
        if (cr.moveToFirst()){
            Log.d(CLASSTAG, "insertAdvertiser() id: " + advertiserId + " already exists");
            Log.d(CLASSTAG, "insertAdvertiser() id: " + cr.getString(1) + " impressions: " + cr.getString(2));
        } else {
            Log.d(CLASSTAG, "insertAdvertiser() id: " + advertiserId + " inserted");
            ContentValues cv= new ContentValues();
            cv.put(AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID, Integer.parseInt(advertiserId));
            cv.put(AdvertiserContract.AdvertiserEntry.COLUMN_IMPRESSION_COUNT, Integer.parseInt(impressions));
            cv.put(AdvertiserContract.AdvertiserEntry.COLUMN_CLICK_COUNT, Integer.parseInt(clicks));
            return mOpenHelper.getWritableDatabase().insert(AdvertiserContract.AdvertiserEntry.TABLE_NAME, null, cv);
        }
        return -1;
    }

    public Cursor queryAdvertiser(String advertiserId) {
        Cursor cr;
        cr = mOpenHelper.getReadableDatabase().query(AdvertiserContract.AdvertiserEntry.TABLE_NAME,
                null,
                AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ?",
                new String[] {advertiserId},
                null, null, null);

        return cr;
    }

    public void getAllAdvertisers() {
        Cursor cr;
        cr = mOpenHelper.getReadableDatabase().query(AdvertiserContract.AdvertiserEntry.TABLE_NAME,
                null, null, null, null, null, null);
        cr.moveToFirst();
        while (!cr.isAfterLast()) {
            Log.d(CLASSTAG, "getAllAdvertisers() ad id: " +
                    cr.getString(1) + " impressions: " + cr.getString(2));
            cr.moveToNext();
        }
    }

    public void getAllImpressions(boolean groupByDate) {
        Cursor cr;
        if (groupByDate) {
            String query = "SELECT " +
                    AdvertiserContract.ImpressionEntry._ID + ", " +
                    AdvertiserContract.ImpressionEntry.COLUMN_ADVERTISER_KEY + ", " +
                    AdvertiserContract.ImpressionEntry.COLUMN_DATE + ", " +
                    "SUM(" + AdvertiserContract.ImpressionEntry.COLUMN_NUM_IMPRESSIONS + "), " +
                    "SUM(" + AdvertiserContract.ImpressionEntry.COLUMN_NUM_CLICKS + ") " +
                    "FROM impression GROUP BY date ORDER BY date DESC;";

            cr = mOpenHelper.getReadableDatabase().rawQuery(query, null);
        } else {
            cr = mOpenHelper.getReadableDatabase().query(AdvertiserContract.ImpressionEntry.TABLE_NAME,
                    new String[]{
                            AdvertiserContract.ImpressionEntry.COLUMN_DATE,
                            AdvertiserContract.ImpressionEntry.COLUMN_NUM_IMPRESSIONS,
                            AdvertiserContract.ImpressionEntry.COLUMN_NUM_CLICKS
                    }, "", null, null, null, null);
        }
        cr.moveToFirst();
        while (!cr.isAfterLast()) {

            // convert to readable date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(cr.getLong(2)));

            Log.d(CLASSTAG, "getAllImpressions()" +
                    " date: " + date +
                    " impressions: " + cr.getString(3) +
                    " clicks: " + cr.getString(4));
            cr.moveToNext();
        }
    }
}