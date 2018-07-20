package com.ad.aggregator.app.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.ad.aggregator.app.ItemFragment.OnListFragmentInteractionListener;
import com.ad.aggregator.app.MainActivity;
import com.ad.aggregator.app.model.MarketItem;
import com.ad.aggregator.app.model.StreamItem;
import com.ad.aggregator.app.model.DummyContent.DummyItem;
import com.ad.aggregator.app.R;
import com.ad.aggregator.app.model.Utility;
import com.ad.aggregator.app.viewmodel.MoneyStreamViewModel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */



public class MoneyStreamRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnFragmentInteractionListener mListener;
    private Context mContext;
    private int position;

    public MoneyStreamRecyclerViewAdapter(Context context, int position, MoneyStreamViewModel viewModel, OnFragmentInteractionListener listener) {
        this.position = position;
        mValues = (List<DummyItem>)(position == 0 ? viewModel.getStreamList() : viewModel.getMarketList());
        mListener = listener;
        mContext = context;
    }

    public void resetData(List<DummyItem> newValues) {
        final ListDiffCallback diffCallback = new ListDiffCallback(this.mValues, newValues);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mValues.clear();
        this.mValues.addAll(newValues);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflator = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case 0:
                view = inflator.inflate(R.layout.fragment_item, parent, false);
                viewHolder = new StreamViewHolder(view);
                break;
            case 1:
                view = inflator.inflate(R.layout.market_fragment_item, parent, false);
                viewHolder = new MarketViewHolder(view);
                break;
            default:
                view = inflator.inflate(R.layout.fragment_item, parent, false);
                viewHolder = new StreamViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case 0:
                configureStreamViewHolder((StreamViewHolder)holder, position);
                break;
            case 1:
                configureMarketViewHolder((MarketViewHolder) holder, position);
                break;
            default:
                break;
        }
    }

    private void configureStreamViewHolder(final StreamViewHolder holder, int position) {
        StreamItem item = (StreamItem)mValues.get(position);
        holder.mItem = (StreamItem) item;

        holder.mIdView.setText(item.id);
        holder.mContentView.setText(item.title);

        holder.mOwnerView.setTextColor(Color.GRAY);
        holder.mOwnerView.setText("via " + item.source);
        holder.mContentView.setVisibility(View.VISIBLE);
        holder.mImageView.setMaxHeight(200);
        holder.mShareButton.setVisibility(View.VISIBLE);
        if (item.cardType.compareTo("dfp_ad") == 0) {
            holder.mOwnerView.setText(item.source);
            holder.mContentView.setVisibility(View.GONE);
            holder.mShareButton.setVisibility(View.INVISIBLE);
        } else if (item.cardType.compareTo("tweet") == 0) {
            holder.mImageView.setMaxHeight(0);
            holder.mContentView.setTextSize(16f);


            /*final ViewGroup parentView = (ViewGroup) holder.mCardView;
            long tweetId = 631879971628183552L;
            TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    TweetView tweetView = new TweetView(mContext, result.data);
                    parentView.addView(tweetView);
                }
                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Load Tweet failure", exception);
                }
            });*/

        } else {

        }

        holder.mTimeView.setTextColor(Color.GRAY);
        holder.mTimeView.setText("");
        if (item.published != null && !item.published.isEmpty()) {
            holder.mTimeView.setText(Utility.DateToAge((item.published)));
        }

        holder.mImageView.setVisibility(View.GONE);
        if (item.image != null && !item.image.isEmpty()) {
            holder.mImageView.setVisibility(View.VISIBLE);
            MoneyStreamViewModel.loadImage(mContext, item.image, holder.mImageView);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(getClass().toString(), holder.mItem);
                }
            }
        });
    }

    private void configureMarketViewHolder(final MarketViewHolder holder, int position) {
        MarketItem item = (MarketItem)mValues.get(position);
        holder.mItem = (MarketItem) item;

        holder.mIdView.setText(item.id);
        holder.mContentView.setText(item.title);

        if (item.cardType != null && !item.cardType.isEmpty()) {

            holder.mOwnerView.setText(item.value + " / ");
            holder.mOwnerView.setTextColor(Color.GRAY);
            holder.mTimeView.setText((item.changeDirection > 0 ? "+" : "-") + item.changePercent + "%");
            holder.mShareButton.setVisibility(View.GONE);

            if (item.changeDirection > 0) {
                holder.mTimeView.setTextColor(Color.parseColor("#008000"));
                holder.mImageView.setImageResource(R.drawable.triangle_green_up);
            } else {
                holder.mTimeView.setTextColor(Color.parseColor("#800000"));
                holder.mImageView.setImageResource(R.drawable.triangle_red_down);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onFragmentInteraction(getClass().toString(), holder.mItem);
                    }
                }
            });
        } else {
            holder.mCardView.setCardBackgroundColor(Color.LTGRAY);
            //holder.mContentView.setTextColor(Color.WHITE);
            holder.mImageView.setVisibility(View.GONE);
            holder.mImageView.setMaxHeight(0);
            holder.mOwnerView.setVisibility(View.GONE);
            holder.mTimeView.setVisibility(View.GONE);

            LinearLayout l = (LinearLayout)holder.mCardView.findViewById(R.id.linearLayout);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)l.getLayoutParams();
            marginParams.setMargins(0, 0, 0, 0);

        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position).cardType.compareTo("market_list") == 0 ||
                mValues.get(position).cardType.isEmpty())
            return 1;
        else
            return 0;
    }

    public class StreamViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;
        public final ImageView mImageView;
        public final TextView mOwnerView;
        public final TextView mTimeView;
        public final Button mShareButton;
        //public final CardView mCardView;

        public StreamViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mOwnerView = (TextView) view.findViewById(R.id.publisher);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mShareButton = (Button) view.findViewById(R.id.shareButton);
            //mCardView = (CardView) view.findViewById(R.id.root);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class MarketViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;
        public final ImageView mImageView;
        public final TextView mOwnerView;
        public final TextView mTimeView;
        public final Button mShareButton;
        public final CardView mCardView;

        public MarketViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mImageView = (ImageView) view.findViewById(R.id.image);
            mOwnerView = (TextView) view.findViewById(R.id.publisher);
            mTimeView = (TextView) view.findViewById(R.id.time);
            mShareButton = (Button) view.findViewById(R.id.shareButton);
            mCardView = (CardView) view.findViewById(R.id.root);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
