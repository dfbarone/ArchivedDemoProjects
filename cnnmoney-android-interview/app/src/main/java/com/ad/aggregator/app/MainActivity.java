package com.ad.aggregator.app;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ad.aggregator.app.model.DummyContent;
import com.ad.aggregator.app.model.sync.AdAggregatorSyncAdapter;
import com.ad.aggregator.app.view.HomeFragment;
import com.ad.aggregator.app.view.OnFragmentInteractionListener;
import com.ad.aggregator.app.model.StreamItem;

public class MainActivity extends AppCompatActivity
    implements OnFragmentInteractionListener {

    public static final String CLASSTAG = MainActivity.class.getSimpleName();

    BottomNavigationView mBottomNavigationView;
    ViewPager mNavViewPager;
    Toolbar mToolbar;

    private void MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mNavViewPager = (ViewPager) findViewById(R.id.bottom_nav_view_pager);

        setSupportActionBar(mToolbar);

        // hide ... button
        if (mToolbar != null)
            mToolbar.hideOverflowMenu();

        setupViewPager(mNavViewPager);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                mNavViewPager.setCurrentItem(0);
                                break;
                            case R.id.action_chefs:
                                mNavViewPager.setCurrentItem(1);
                                break;
                            /*case R.id.action_shows:
                                mNavViewPager.setCurrentItem(2);
                                break;

                            case R.id.action_search:
                                mNavViewPager.setCurrentItem(3);
                                break;

                            case R.id.action_mystuff:
                                mNavViewPager.setCurrentItem(4);
                                break;*/
                        }
                        return false;
                    }
                });

        ViewCompat.setOnApplyWindowInsetsListener(mNavViewPager,
                new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        insets = ViewCompat.onApplyWindowInsets(v, insets);
                        if (insets.isConsumed()) {
                            return insets;
                        }

                        boolean consumed = false;
                        for (int i = 0, count = mNavViewPager.getChildCount(); i <  count; i++) {
                            ViewCompat.dispatchApplyWindowInsets(mNavViewPager.getChildAt(i), insets);
                            if (insets.isConsumed()) {
                                consumed = true;
                            }
                        }
                        return consumed ? insets.consumeSystemWindowInsets() : insets;
                    }
                });

        AdAggregatorSyncAdapter.initializeSyncAdapter(this);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment f = new HomeFragment();

                return f;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //if (id == R.id.action_refresh) {
            //AdAggregatorSyncAdapter.syncImmediately(getApplicationContext());
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String tag, Object obj){

        try {
            DummyContent.DummyItem item = (DummyContent.DummyItem) obj;
            Log.d(CLASSTAG, "Uri: " + item.details);

            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(item.details));
            startActivity(i);

        } catch (Exception e) {

        }
    }
}
