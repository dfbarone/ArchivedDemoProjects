package com.ad.aggregator.app.view;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ad.aggregator.app.R;
import com.ad.aggregator.app.model.data.AdvertiserContract;

public class AdvertiserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String CLASSTAG = AdvertiserFragment.class.getSimpleName();

    private AdvertiserAdapter mAdvertiserAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mListView;

    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;
    private boolean mSortByDate = false;

    private static final String SELECTED_KEY = "selected_position";

    private static final int ADVERTISER_LOADER = 0;
    // request columns for query
    private static final String[] ADVERTISER_COLUMNS = {
            AdvertiserContract.AdvertiserEntry._ID,
            AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID,
            AdvertiserContract.AdvertiserEntry.COLUMN_IMPRESSION_COUNT,
            AdvertiserContract.AdvertiserEntry.COLUMN_CLICK_COUNT,
    };
    static final int COL_ID = 0;
    static final int COL_ADVERTISER_ID = 1;
    static final int COL_IMPRESSION_COUNT = 2;
    static final int COL_CLICK_COUNT = 3;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public AdvertiserFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey("sort_by_date")){
                setSortType(bundle.getBoolean("sort_by_date"));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.advertiserfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Init adapter
        mAdvertiserAdapter = new AdvertiserAdapter(getActivity(), null, 0);
        mAdvertiserAdapter.setSortType(mSortByDate);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (RecyclerView) rootView.findViewById(R.id.listview_advertiser);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);
        mListView.setAdapter(mAdvertiserAdapter);

        // We'll call our MainActivity
        mAdvertiserAdapter.setOnItemClickListener(new AdvertiserAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view, Cursor c) {
                Cursor cursor = (Cursor) c;
                if (cursor != null) {
                    //tdb
                }
                mPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mAdvertiserAdapter.setUseTodayLayout(mUseTodayLayout);
        onDataChanged();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ADVERTISER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // restart things
    void onDataChanged( ) {
        updateImpressions();
        getLoaderManager().restartLoader(ADVERTISER_LOADER, null, this);
    }

    private void updateImpressions() {
        //AdAggregatorSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort order:  Ascending, by date.
        String sortOrder = AdvertiserContract.AdvertiserEntry.COLUMN_ADVERTISER_ID + " ASC";

        Uri uri = mSortByDate ? AdvertiserContract.ImpressionEntry.CONTENT_URI : AdvertiserContract.AdvertiserEntry.CONTENT_URI;
        return new CursorLoader(getActivity().getApplicationContext(),
                uri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdvertiserAdapter.swapCursor(data);

        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdvertiserAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mAdvertiserAdapter != null) {
            mAdvertiserAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

    public void setSortType(boolean sortByDate) {
        mSortByDate = sortByDate;
        if (mAdvertiserAdapter != null) {
            mAdvertiserAdapter.setSortType(sortByDate);
            setUseTodayLayout(sortByDate);
        }
    }
}