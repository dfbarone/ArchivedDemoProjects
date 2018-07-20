package com.noteworth.lunch.view.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.noteworth.lunch.R;
import com.noteworth.lunch.model.data.json.place.search.GooglePlacesResults;
import com.noteworth.lunch.view.adapter.SearchResultsRecyclerViewAdapter;
import com.noteworth.lunch.viewmodel.LunchViewModel;

import java.util.ArrayList;


public class SearchFragment extends BaseFragment {

    private final String TAG = SearchFragment.class.getSimpleName();
    private FloatingSearchView mSearchView;
    private RecyclerView mSearchResultsList;
    private SearchResultsRecyclerViewAdapter mSearchResultsAdapter;
    private LunchViewModel mLunchViewModel;


    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mLunchViewModel = ViewModelProviders.of(getActivity()).get(LunchViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        mSearchResultsList = (RecyclerView) view.findViewById(R.id.search_results_list);

        setupFloatingSearch();
        setupResultsList();
        setupDrawer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLunchViewModel.onResume();

        mSearchView.setSearchText(mLunchViewModel.getLastQuery());

        refresh(mLunchViewModel.getLastQuery());
    }

    @Override
    public void onPause() {
        super.onPause();
        mLunchViewModel.onPause();
    }

    /**
     * Primary search method
     *
     * @param search
     */
    private void refresh(final String search) {

        if (!TextUtils.isEmpty(search)) {
            mLunchViewModel.searchResult(search)
                    .observe(this, new Observer<GooglePlacesResults>() {
                        @Override
                        public void onChanged(@Nullable GooglePlacesResults searchResults) {
                           mLunchViewModel.setLastQuery(search);
                            if (searchResults != null) {
                                mSearchResultsAdapter.swapData(searchResults.getResults());
                            }
                        }
                    });
        } else {
            mSearchView.clearSuggestions();
            mSearchResultsAdapter.swapData(new ArrayList<>());
        }
    }

    private void setupFloatingSearch() {

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                    mSearchResultsAdapter.swapData(new ArrayList<>());
                } /*else {
                    // Don't retry on every text change
                    refresh(newQuery);
                }*/
                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                Log.d(TAG, "onSuggestionClicked()");
                mLunchViewModel.setLastQuery(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String query) {
                // Only search when key pressed.
                refresh(query);
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLunchViewModel.getLastQuery());

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                mSearchView.setSearchText(mLunchViewModel.getLastQuery());

                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.action_filter) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(getContext().LAYOUT_INFLATER_SERVICE);
                    View dialogView = inflater.inflate(R.layout.alert_search_filter, null);

                    final Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);
                    spinner.setSelection(mLunchViewModel.getCurrentItem());

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(mLunchViewModel.getCurrentItem() != position){
                                mLunchViewModel.setCurrentItem(position);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    builder.setView(dialogView);

                    final AlertDialog alert = builder.create();
                    alert.setTitle("Sort by");
                    alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refresh(mLunchViewModel.getLastQuery());
                        }
                    });

                    alert.show();

                } else {

                    //just print action
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {

                Log.d(TAG, "onHomeClicked()");
            }
        });

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {

                /*ColorSuggestion colorSuggestion = (ColorSuggestion) item;

                String textColor = "#ffffff";
                String textLight = "#bfbfbf";

                if (colorSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = colorSuggestion.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));*/
            }

        });

        //listen for when suggestion list expands/shrinks in order to move down/up the
        //search results list
        mSearchView.setOnSuggestionsListHeightChanged(new FloatingSearchView.OnSuggestionsListHeightChanged() {
            @Override
            public void onSuggestionsListHeightChanged(float newHeight) {
                mSearchResultsList.setTranslationY(newHeight);
            }
        });

        /*
         * When the user types some text into the search field, a clear button (and 'x' to the
         * right) of the search text is shown.
         *
         * This listener provides a callback for when this button is clicked.
         */
        mSearchView.setOnClearSearchActionListener(new FloatingSearchView.OnClearSearchActionListener() {
            @Override
            public void onClearSearchClicked() {
                Log.d(TAG, "onClearSearchClicked()");
                mSearchResultsAdapter.swapData(new ArrayList<>());
            }
        });
    }

    private void setupResultsList() {
        mSearchResultsAdapter = new SearchResultsRecyclerViewAdapter(getContext());
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public boolean onActivityBackPress() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        if (!mSearchView.setSearchFocused(false)) {
            return false;
        }
        return true;
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }

}
