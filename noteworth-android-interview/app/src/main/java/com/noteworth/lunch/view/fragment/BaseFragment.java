package com.noteworth.lunch.view.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;

public abstract class BaseFragment extends Fragment {

    private BaseFragmentCallbacks mCallbacks;

    public interface BaseFragmentCallbacks{

        void onAttachSearchViewToDrawer(FloatingSearchView searchView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentCallbacks) {
            mCallbacks = (BaseFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseExampleFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected void attachSearchViewActivityDrawer(FloatingSearchView searchView){
        if(mCallbacks != null){
            mCallbacks.onAttachSearchViewToDrawer(searchView);
        }
    }

    public abstract boolean onActivityBackPress();
}
