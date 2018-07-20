package com.ad.aggregator.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ad.aggregator.app.sync.AdAggregatorSyncAdapter;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ViewPager mViewPager;

    Toolbar mToolbar;
    TabLayout mTabLayout;
    String [] mStringList;
    ViewPagerAdapter mAdapter;
    int mTabPosition = -1;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabLayout.setupWithViewPager(mViewPager);

        CollapsingToolbarLayout mCollapsingToolbarLayout =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);

        // hide ... button
        if (mToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
            mToolbar.hideOverflowMenu();
        }

        mCollapsingToolbarLayout.setTitle(" ");

        mStringList = new String[] {
                "Side Dishes", "Desserts", "Appetizers",
                "Cocktails", "How-To Videos",
                "Top Picks",
                "Slow Cooker", "Weeknights", "Healthy",
                "Game Day", "Turkey", "Stuffing"
        };



            //attach tab layout with ViewPager
            //set gravity for tab bar
            mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        //change selected tab when viewpager changed page
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        //change viewpager page when tab selected
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*mTabLayout.post(new Runnable() {
            @Override
            public void run() {

                if (mTabLayout != null && mTabLayout.getTabCount() > 0) {
                    mTabLayout.getTabAt(0).select();
                    mTabLayout.getTabAt( mTabPosition ).select();
                    mTabPosition = mTabLayout.getSelectedTabPosition();
                }

            }
        });*/
        setupViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewAdvertiserId();
            }
        });

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        if (mAdapter == null) {
            //create and set ViewPager adapter
            mAdapter = new ViewPagerAdapter(getChildFragmentManager(), mStringList);
        }
        mViewPager.setOffscreenPageLimit(mStringList.length/4);
        viewPager.setAdapter(mAdapter);

        if (mTabLayout != null && mTabLayout.getTabCount() > 0) {
            if (mTabPosition < 0) {
                // mTabLayout.getTabAt(0).select();
                mTabLayout.getTabAt(5).select();
                mTabPosition = mTabLayout.getSelectedTabPosition();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri.toString(), uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void addNewAdvertiserId()
    {
        new MaterialDialog.Builder(getActivity())
                .title("Enter Advertiser Id")
                .positiveText("Add")
                .negativeText("Cancel")
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        try {
                            String advertiserId = dialog.getInputEditText().getText().toString();
                            // Ensure the id is a number
                            Integer.parseInt(advertiserId);
                            if (!advertiserId.isEmpty() && which != null &&
                                    which.name().compareToIgnoreCase("positive") == 0) {

                                //showToast("Prompt checked? " + dialog.isPromptCheckBoxChecked());
                                //Snackbar.make(findViewById(android.R.id.content), " has been added.", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                                AdAggregatorSyncAdapter.addAdvertiser(getActivity().getApplicationContext(), advertiserId);
                                //AdAggregatorSyncAdapter.syncImmediately(getApplicationContext());
                            }
                        } catch (NumberFormatException e){}
                    }
                })
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("advertiser id", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                })
                .show();
    }

    protected class ViewPagerAdapter extends FragmentStatePagerAdapter {

        //public static final int LOOPS_COUNT = 1000;
        private String[] mStringList;

        public ViewPagerAdapter(FragmentManager fm, String[] stringList) {
            super(fm);
            mStringList = stringList;
        }

        @Override
        public Fragment getItem(int position) {
            int realPosition = position % mStringList.length;

            // two fragments, with different sets of data,
            // one for advertisers
            // one for stats aggregated by date
            Bundle args = new Bundle();
            Fragment f = new MainRecipeFragment();
            //if (realPosition == 1) {
                args.putInt("position", realPosition);
                //f = new AdvertiserFragment();
            //}
            f.setArguments(args);
            return f;
        }

        @Override
        public String getPageTitle(int position) {
            return mStringList[position];
        }

        @Override
        public int getCount() {
            return /*LOOPS_COUNT * */mStringList.length;
        }
    }
}
