package com.foursquare.takehome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.foursquare.takehome.view.adapter.PersonAdapter;
import com.foursquare.takehome.viewmodel.VenueViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    public final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView rvRecyclerView;
    private PersonAdapter personAdapter;
    private VenueViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = new VenueViewModel(this);

        rvRecyclerView = (RecyclerView) findViewById(R.id.rvRecyclerView);
        personAdapter = new PersonAdapter();

        // Add layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecyclerView.setLayoutManager(layoutManager);

        // Add line separator
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRecyclerView.getContext(),
                layoutManager.getOrientation());
        rvRecyclerView.addItemDecoration(dividerItemDecoration);

        // Set adapter
        rvRecyclerView.setAdapter(personAdapter);

        // observe response
        mViewModel.parseVenueFromResponse()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(venue -> {
                    // update list with new visitors
                   personAdapter.update(venue.getVisitors());
                }, error -> {
                    // If error, might as well output the message
                    Log.d(TAG, error.getMessage());
                });
    }
}
