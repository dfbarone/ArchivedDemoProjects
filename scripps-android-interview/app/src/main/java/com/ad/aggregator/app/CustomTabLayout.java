package com.ad.aggregator.app;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by hal on 11/7/2016.
 */

class CustomTabLayout extends TabLayout {

    public Typeface mTypeface = null;

    public CustomTabLayout(Context context) {
        super(context);
    }

    public CustomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setupWithViewPager(ViewPager viewPager)
    {
        super.setupWithViewPager(viewPager);

        try {
            mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Times New Roman.ttf");
        } catch (Exception e) {
            Log.d("CustomTabLayout", e.getMessage());
        }

        if (mTypeface != null)
        {
            this.removeAllTabs();

            ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

            PagerAdapter adapter = viewPager.getAdapter();

            for (int i = 0, count = adapter.getCount(); i < count; i++)
            {
                Tab tab = this.newTab();
                this.addTab(tab.setText(adapter.getPageTitle(i)));
                AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
                view.setTypeface(mTypeface, Typeface.NORMAL);
            }
        }
    }
    
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LinearLayout slidingTabStrip = (LinearLayout) getChildAt(0);
        int tabCount = slidingTabStrip.getChildCount();

        final int unspecifiedSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        // First we'll find the largest tab
        int totalWidth = 0;
        int largestTabWidth = 0;
        int largestPossibleWidth = (getMeasuredWidth()) / tabCount;
        for (int i = 0, z = tabCount; i < z; i++) {
            final View child = slidingTabStrip.getChildAt(i);
            child.measure(unspecifiedSpec, heightMeasureSpec);
            largestTabWidth = Math.max(largestTabWidth, child.getMeasuredWidth());
            totalWidth += child.getMeasuredWidth();
        }
        if (largestTabWidth <= 0) {
            // If we don't have a largest child yet, skip until the next measure pass
            return;
        }

        if (totalWidth <= getMeasuredWidth()) {
            // If the tabs fit within our width minus gutters, we will set all tabs to have
            // the same width
            for (int i = 0; i < tabCount; i++) {
                View tabView = slidingTabStrip.getChildAt(i);
                final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = largestPossibleWidth;
                lp.weight = 0;
            }
            setTabMode(MODE_FIXED);
            Log.d("hi", "fixed");
        } else {
            // If the tabs will wrap to be larger than the width minus gutters, we need
            // to switch to GRAVITY_FILL
            setTabMode(MODE_SCROLLABLE);
            Log.d("hi", "scrollable");
        }
        // Now re-measure after our changes
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
