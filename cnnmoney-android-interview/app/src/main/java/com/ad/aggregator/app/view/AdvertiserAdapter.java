package com.ad.aggregator.app.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ad.aggregator.app.R;
import com.ad.aggregator.app.model.Utility;

public class AdvertiserAdapter extends CursorRecyclerViewAdapter<AdvertiserAdapter.ViewHolder> {

    public interface ClickListener {
        void onItemClick(int position, View view, Cursor c);
    }

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    private boolean mSortByDate = false;

    private Context mContext;

    private AdvertiserAdapter.ClickListener mClickListener;

    /**
     * Cache of the children views for a advertiser list item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView titleView;
        public final TextView subtitleView;
        public final TextView subtitle2View;
        public Cursor mData;

        private AdvertiserAdapter mParent;

        public ViewHolder(AdvertiserAdapter parent, View view) {
            super(view);
            mParent = parent;

            iconView = (ImageView) view.findViewById(R.id.photo);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            subtitle2View = (TextView) view.findViewById(R.id.subtitle2);
            dateView = (TextView) view.findViewById(R.id.date);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // The user may not set a click listener for list items, in which case our listener
            // will be null, so we need to check for this
            ViewHolder vh = (ViewHolder)v.getTag();
            mParent.mClickListener.onItemClick(getAdapterPosition(), v, (Cursor)vh.mData);
        }
    }

    public AdvertiserAdapter(Context context, Cursor c, int flags) {
        super(context, c);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_advertiser, parent, false);
        ViewHolder viewHolder = new ViewHolder(this, view);
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {

        if (mSortByDate) {
            // date
            String title = Utility.getFriendlyDayString(mContext, cursor.getLong(2));
            viewHolder.titleView.setText(title);

            // total impressions
            String impression = "daily impressions: " + cursor.getString(3);
            viewHolder.subtitleView.setText(impression);

            // total clicks
            String click = "daily clicks: " + cursor.getString(4);
            viewHolder.subtitle2View.setText(click);
        } else {
            // Advertiser id
            String title = "Advertiser Id: " + cursor.getString(AdvertiserFragment.COL_ADVERTISER_ID);
            viewHolder.titleView.setText(title);

            // total impressions
            String impression = "total impressions: " + cursor.getString(AdvertiserFragment.COL_IMPRESSION_COUNT);
            viewHolder.subtitleView.setText(impression);

            //total clicks
            String click = "total clicks: " + cursor.getString(AdvertiserFragment.COL_CLICK_COUNT);
            viewHolder.subtitle2View.setText(click);
        }

        viewHolder.mData = cursor;
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    public void setSortType(boolean sortByDate) {
        mSortByDate = sortByDate;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getItemCount() {
        Cursor cursor = getCursor();
        if (cursor != null)
            return getCursor().getCount();
        else
            return 0;
    }

    public void setOnItemClickListener(AdvertiserAdapter.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}