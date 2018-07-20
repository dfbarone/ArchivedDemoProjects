package com.ad.aggregator.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ad.aggregator.app.data.AdvertiserContract.AdvertiserEntry;
import com.ad.aggregator.app.data.AdvertiserContract.ImpressionEntry;

/**
 * Manages a local database for advertiser data.
 */
public class AdvertiserDbHelper extends SQLiteOpenHelper {
    public static final String CLASSTAG = AdvertiserDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 12;

    static final String DATABASE_NAME = "advertiser.db";

    public AdvertiserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold all advertiser id's
        final String SQL_CREATE_ADVERTISER_TABLE = "CREATE TABLE if not exists " + AdvertiserEntry.TABLE_NAME + " (" +
                AdvertiserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AdvertiserEntry.COLUMN_ADVERTISER_ID + " INTEGER NOT NULL, " +
                AdvertiserEntry.COLUMN_IMPRESSION_COUNT + " INTEGER NOT NULL, " +
                AdvertiserEntry.COLUMN_CLICK_COUNT + " INTEGER NOT NULL, " +
                " UNIQUE (" + AdvertiserEntry.COLUMN_ADVERTISER_ID +") ON CONFLICT REPLACE );";

        // Create a table to hold all advertiser stats by day
        final String SQL_CREATE_IMPRESSION_TABLE = "CREATE TABLE if not exists " + ImpressionEntry.TABLE_NAME + " (" +
                ImpressionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                // the ID of the advertiser entry associated with this impression data
                ImpressionEntry.COLUMN_ADVERTISER_KEY + " INTEGER NOT NULL, " +
                ImpressionEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ImpressionEntry.COLUMN_NUM_CLICKS + " INTEGER NOT NULL, " +
                ImpressionEntry.COLUMN_NUM_IMPRESSIONS + " INTEGER NOT NULL," +

                // Set up the advertiser column as a foreign key.
                " FOREIGN KEY (" + ImpressionEntry.COLUMN_ADVERTISER_KEY + ") REFERENCES " +
                AdvertiserEntry.TABLE_NAME + " (" + AdvertiserEntry._ID + "), " +

                // only one impression entry for advertiser per day
                " UNIQUE (" + ImpressionEntry.COLUMN_DATE + ", " +
                ImpressionEntry.COLUMN_ADVERTISER_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_ADVERTISER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_IMPRESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // drop on upgrade for now
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AdvertiserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImpressionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
