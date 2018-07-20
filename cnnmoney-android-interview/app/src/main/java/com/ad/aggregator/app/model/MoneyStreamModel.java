package com.ad.aggregator.app.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dfbarone.forgettablerequestcache.ForgettableRequestCacheHelper;
import com.dfbarone.forgettablerequestcache.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoneyStreamModel {

    private final String TAG = MoneyStreamModel.class.getName();
    private Context mContext;
    public static String HOME_STREAM_URL = "https://api.cnnmoneystream.com/home_stream";

    public static List<DummyContent.DummyItem> mStreamList = new ArrayList<>();
    public static List<DummyContent.DummyItem> mMarketList = new ArrayList<>();

    public MoneyStreamModel(Application application) {
        mContext = application;
        ForgettableRequestCacheHelper.getInstance().init(application);
    }

    // Get function for /start web method
    public void getAsync(String url, final VolleyCallback callback) {

        ForgettableRequestCacheHelper.getInstance().get(url, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                parseJSON(result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(VolleyError error) {
                callback.onError(error);
            }
        });
    }

    private void parseJSON(JSONObject result) {

        List<DummyContent.DummyItem> news = new ArrayList<>();
        List<DummyContent.DummyItem> markets = new ArrayList<>();
        try {
            JSONArray cards = result.getJSONArray("cards");
            for (int i = 0; i < cards.length(); i++) {
                JSONObject itemObj = (JSONObject) cards.get(i);
                StreamItem item = new StreamItem(itemObj);
                if (item != null && item.cardType != null && item.cardType.compareTo("article") == 0) {
                    news.add(item);
                } else if (item != null && item.cardType != null && item.cardType.compareTo("tweet") == 0) {
                    news.add(item);
                } else if (item != null && item.cardType != null && item.cardType.compareTo("dfp_ad") == 0) {
                    news.add(item);
                } else if (item != null && item.cardType != null && item.cardType.compareTo("market_list") == 0) {
                    String id = itemObj.getString("id");
                    String cardType = itemObj.getString("card_type");
                    JSONArray marketJSONArray = itemObj.getJSONArray("markets");

                    MarketItem marketItem;
                    marketItem = new MarketItem("U.S. Indexes");
                    markets.add(marketItem);
                    for (int j = 0; j < marketJSONArray.length(); j++) {
                        marketItem = new MarketItem(id, cardType, (JSONObject) marketJSONArray.get(j));
                        if (marketItem.title.toLowerCase().contains("dow") ||
                                marketItem.title.toLowerCase().contains("s&p") ||
                                marketItem.title.toLowerCase().contains("nasdaq")) {
                            markets.add(marketItem);
                        }
                    }

                    marketItem = new MarketItem("World Markets");
                    markets.add(marketItem);

                    for (int j = 0; j < marketJSONArray.length(); j++) {
                        marketItem = new MarketItem(id, cardType, (JSONObject) marketJSONArray.get(j));
                        if (!marketItem.title.toLowerCase().contains("dow") &&
                                !marketItem.title.toLowerCase().contains("s&p") &&
                                !marketItem.title.toLowerCase().contains("nasdaq")) {
                            markets.add(marketItem);
                        }
                    }
                } else {
                    Log.d(TAG, "Card Type: " + item.cardType);

                }
            }

            //Collections.sort(list, Collections.<StreamItem>reverseOrder());
            mStreamList = news;
            mMarketList = markets;

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            mStreamList.clear();
            mMarketList.clear();
        }
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        try {

            Glide.with(context)
                    .load(imageUrl)
                    .crossFade(500)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(imageView);

        } catch (Exception e) {
            Log.d("", e.getMessage());
        }
    }
}
