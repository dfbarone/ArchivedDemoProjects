package com.ad.aggregator.app.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.ad.aggregator.app.model.DummyContent;
import com.ad.aggregator.app.model.MoneyStreamModel;
import com.dfbarone.forgettablerequestcache.VolleyCallback;

import java.util.List;

public class MoneyStreamViewModel {

    private MoneyStreamModel mMoneyStreamModel;

    public MoneyStreamViewModel(Application context) {
        mMoneyStreamModel = new MoneyStreamModel(context);
    }

    // Get function for /start web method
    public void loadNews(final VolleyCallback callback) {
        mMoneyStreamModel.getAsync(MoneyStreamModel.HOME_STREAM_URL, callback);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView){
        MoneyStreamModel.loadImage(context, imageUrl, imageView);
    }

    public List<DummyContent.DummyItem> getStreamList() {
        return mMoneyStreamModel.mStreamList;
    }

    public List<DummyContent.DummyItem> getMarketList() {
        return mMoneyStreamModel.mMarketList;
    }
}
