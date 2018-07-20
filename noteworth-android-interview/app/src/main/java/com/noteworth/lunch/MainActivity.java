package com.noteworth.lunch;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.noteworth.lunch.common.listeners.OnFragmentInteractionListener;
import com.noteworth.lunch.model.data.json.place.details.Result;
import com.noteworth.lunch.common.view.BaseActivity;
import com.noteworth.lunch.view.activity.DetailsActivity;
import com.noteworth.lunch.view.fragment.BaseFragment;
import com.noteworth.lunch.viewmodel.LunchViewModel;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener, BaseFragment.BaseFragmentCallbacks {

    private LunchViewModel mLunchViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLunchViewModel = ViewModelProviders.of(this).get(LunchViewModel.class);
        mLunchViewModel.initializeGoogleLocationService(this, findViewById(R.id.root));
    }

    @Override
    public void onStart() {
        super.onStart();
        mLunchViewModel.updateGoogleLocationService();
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
        return super.onOptionsItemSelected(item);
    }

    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {

    }

    public void onFragmentInteraction(String tag, Object object) {

        if (object instanceof Result) {
            Result result = (Result) object;
            // navigate
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("place_id", result.getPlaceId());
            intent.putExtra("lat", mLunchViewModel.getGoogleLocation().getLatitude());
            intent.putExtra("long", mLunchViewModel.getGoogleLocation().getLongitude());
            startActivity(intent);
        }
    }
}
