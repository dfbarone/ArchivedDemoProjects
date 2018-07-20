package com.noteworth.lunch.view.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.noteworth.lunch.R;
import com.noteworth.lunch.common.listeners.OnFragmentInteractionListener;
import com.noteworth.lunch.common.view.BaseActivity;
import com.noteworth.lunch.model.data.json.place.Photo;
import com.noteworth.lunch.model.data.json.place.details.GooglePlacesResult;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.model.feature.GooglePlacesApi;
import com.noteworth.lunch.view.adapter.PhotoRecyclerViewAdapter;
import com.noteworth.lunch.view.viewholder.PlaceViewHolder;
import com.noteworth.lunch.view.viewholder.SearchResultViewHolder;
import com.noteworth.lunch.viewmodel.LunchDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends BaseActivity implements OnFragmentInteractionListener {

    private static LunchDetailViewModel mViewModel;
    private String mPlaceId, mLatitude, mLongitude;

    private RecyclerView mPhotoRecyclerView;
    private ViewGroup mContent;
    private PhotoRecyclerViewAdapter mPhotoAdapter;

    MapView mapView;
    GoogleMap googleMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mViewModel = ViewModelProviders.of(this).get(LunchDetailViewModel.class);
        mViewModel.initializeGoogleLocationService(this, findViewById(R.id.root));

        mContent = findViewById(R.id.content);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.photo_recycler_view);
        mPhotoAdapter = new PhotoRecyclerViewAdapter(this);
        mPhotoRecyclerView.setAdapter(mPhotoAdapter);
        mPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mPhotoRecyclerView);

        mPlaceId = getIntent().getStringExtra("place_id");
        mLatitude = getIntent().getStringExtra("lat");
        mLongitude = getIntent().getStringExtra("long");

        refresh();
    }

    private void refresh() {
        mViewModel.details(mPlaceId).observe(this, new Observer<GooglePlacesResult>() {
            @Override
            public void onChanged(@Nullable final GooglePlacesResult googlePlacesResult) {
                initializeContent(googlePlacesResult);
            }
        });
    }

    private void initializeContent(final GooglePlacesResult googlePlacesResult) {
        if (googlePlacesResult.getResult().getPhotos() != null) {
            mPhotoAdapter.swapData(googlePlacesResult.getResult().getPhotos());
        } else {
            List<Photo> photos = new ArrayList<>();
            photos.add(new Photo());
            mPhotoAdapter.swapData(photos);
        }

        mContent.removeAllViews();

        View view = LayoutInflater.from(this)
                .inflate(R.layout.viewholder_detail, mContent, false);
        PlaceViewHolder vh = new PlaceViewHolder(view);
        vh.onBindViewHolder(googlePlacesResult.getResult());

        mContent.addView(vh.itemView);

        initializeMap(googlePlacesResult.getResult());
    }

    private void initializeMap(final Result result) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                try {

                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                    googleMap.clear();

                    LatLng latLngPoint = new LatLng(result.getGeometry().getLocation().getLat(),
                            result.getGeometry().getLocation().getLng());

                    googleMap.addMarker(new MarkerOptions().position(latLngPoint)
                            .title(result.getName()));


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    builder.include(new LatLng(result.getGeometry().getViewport().getNortheast().getLat(),
                            result.getGeometry().getViewport().getNortheast().getLng()));

                    builder.include(new LatLng(result.getGeometry().getViewport().getSouthwest().getLat(),
                            result.getGeometry().getViewport().getSouthwest().getLng()));

                    LatLngBounds bounds = builder.build();

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);

                    googleMap.moveCamera(cameraUpdate);

                } catch (SecurityException e) {
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.updateGoogleLocationService();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String tag, Object object) {

        if (object instanceof Result) {
            // navigate
        }
    }
}
