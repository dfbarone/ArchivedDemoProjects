package com.ad.aggregator.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.ad.aggregator.app.data.AdvertiserContract;
import com.ad.aggregator.app.BuildConfig;
import com.ad.aggregator.app.MainActivity;
import com.ad.aggregator.app.R;
import com.ad.aggregator.app.Utility;
import com.ad.aggregator.app.data.AdvertiserContract.ImpressionEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class AdAggregatorSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = AdAggregatorSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the impression, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int IMPRESSION_NOTIFICATION_ID = 3004;

    private static final String[] NOTIFY_ADVERTISER_PROJECTION = new String[] {
            AdvertiserContract.AdvertiserEntry._ID,
            AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID,
            AdvertiserContract.AdvertiserEntry.COLUMN_IMPRESSION_COUNT,
            AdvertiserContract.AdvertiserEntry.COLUMN_CLICK_COUNT
    };

    private static final String[] NOTIFY_IMPRESSION_PROJECTION = new String[] {
            ImpressionEntry.COLUMN_ADVERTISER_KEY,
            ImpressionEntry.COLUMN_DATE,
            ImpressionEntry.COLUMN_NUM_CLICKS,
            ImpressionEntry.COLUMN_NUM_IMPRESSIONS
    };

    // these indices must match the projection
    private static final int INDEX_ADVERTISER_ID = 0;
    private static final int INDEX_DATE = 1;
    private static final int INDEX_NUM_CLICKS = 2;
    private static final int INDEX_NUM_IMPRESSIONS = 3;

    public AdAggregatorSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // Query all advertisers
        Cursor cr = getContext().getContentResolver().query(
                AdvertiserContract.AdvertiserEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        // get impressions for each advertiser
        if (cr != null) {
            while (cr.moveToNext()) {
                int advertiserIdIndex = cr.getColumnIndex(AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID);
                long advertiserId = cr.getLong(advertiserIdIndex);
                Log.d(LOG_TAG, "advertiser id: " + advertiserId);

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                try {
                    final String BASE_URL = "http://dan.triplelift.net/code_test.php?";
                    final String QUERY_PARAM = "advertiser_id";

                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, Long.toString(advertiserId))
                            .build();

                    URL url = new URL(builtUri.toString());

                    // Create the request to triplelift, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    // Stream was empty, no point in parsing.
                    if (buffer.length() == 0) {
                        return;
                    }

                    getImpressionDataFromJson(buffer.toString(), Long.toString(advertiserId));
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
            }
        }
        try { cr.close(); } catch (Exception ignore) {}
    }

    private void getImpressionDataFromJson(String impressionJsonStr,
                                        String strAdvertiserId)
            throws JSONException {

        // Impression information
        final String OWM_ADVERTISER_ID = "advertiser_id";
        final String OWM_YMD = "ymd";
        final String OWM_NUM_CLICKS = "num_clicks";
        final String OWN_NUM_IMPRESSIONS = "num_impressions";

        try {
            JSONArray impressionArray = new JSONArray(impressionJsonStr);

            // Insert the new impression information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(impressionArray.length());

            int impressionCount = 0;
            int clickCount = 0;
            for(int i = 0; i < impressionArray.length(); i++) {
                // These are the values that will be collected.
                int advertiserId = 0;
                String ymd = "";
                int numClicks = 0;
                int numImpressions = 0;

                // Get the JSON object representing the day
                JSONObject impressionObj = impressionArray.getJSONObject(i);

                advertiserId = impressionObj.getInt(OWM_ADVERTISER_ID);
                ymd = impressionObj.getString(OWM_YMD);
                numClicks = impressionObj.getInt(OWM_NUM_CLICKS);
                numImpressions = impressionObj.getInt(OWN_NUM_IMPRESSIONS);

                impressionCount += numImpressions;
                clickCount += numClicks;

                Date date = new Date();
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    date = sdf.parse(ymd);
                }
                catch (java.text.ParseException e) { }

                ContentValues impressionValues = new ContentValues();
                impressionValues.put(ImpressionEntry.COLUMN_ADVERTISER_KEY, advertiserId);
                impressionValues.put(ImpressionEntry.COLUMN_DATE, date.getTime());
                impressionValues.put(ImpressionEntry.COLUMN_NUM_CLICKS, numClicks);
                impressionValues.put(ImpressionEntry.COLUMN_NUM_IMPRESSIONS, numImpressions);
                cVVector.add(impressionValues);
            }

            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(AdvertiserContract.ImpressionEntry.CONTENT_URI, cvArray);

                ContentValues advertiserValues = new ContentValues();
                advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID, strAdvertiserId);
                advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_IMPRESSION_COUNT, impressionCount);
                advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_CLICK_COUNT, clickCount);

                // update advertiser information totals
                getContext().getContentResolver().update(AdvertiserContract.AdvertiserEntry.CONTENT_URI,
                        advertiserValues,
                        AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ?",
                        new String[] {strAdvertiserId});
            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " aggregated");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to handle insertion of a new advertiser in the database.
     *
     * @param advertiserId The advertiser string used to request updates from the server.
     * @return the row ID of the added advertiser.
     */
    public static long addAdvertiser(Context context, String advertiserId) {
        long rowId = -1;

        // First, check if the advertiser exists in the db
        Cursor advertiserCursor = context.getContentResolver().query(
                AdvertiserContract.AdvertiserEntry.CONTENT_URI,
                null,
                AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " = ?",
                new String[]{advertiserId},
                null);

        if (advertiserCursor.moveToFirst()) {
            // don't add if already exists
            int advertiserIdIndex = advertiserCursor.getColumnIndex(AdvertiserContract.AdvertiserEntry._ID);
            rowId = advertiserCursor.getLong(advertiserIdIndex);
        } else {
            // Insert if not found
            ContentValues advertiserValues = new ContentValues();
            advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID, advertiserId);
            advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_IMPRESSION_COUNT, "0");
            advertiserValues.put(AdvertiserContract.AdvertiserEntry.COLUMN_CLICK_COUNT, "0");

            Uri insertedUri = context.getContentResolver().insert(
                    AdvertiserContract.AdvertiserEntry.CONTENT_URI,
                    advertiserValues
            );

            // The resulting URI contains the ID for the row.
            // Extract the advertiserId from the Uri.
            rowId = ContentUris.parseId(insertedUri);
        }

        try { advertiserCursor.close(); } catch (Exception ignore) {}

        return rowId;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        AdAggregatorSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}