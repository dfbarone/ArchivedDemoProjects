package com.noteworth.lunch.model.feature;

import android.content.Context;

import com.dfbarone.cachinghttp.CachingHttpClient;
import com.dfbarone.cachinghttp.CachingRequest;
import com.dfbarone.cachinghttp.parsing.GsonResponseParser;

import io.reactivex.Observable;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

/**
 * Created by dfbarone on 2/13/2018.
 */

public class LunchWebClient {
    private static final String CONTENT_TYPE = "application/json";
    private static final String TAG = LunchWebClient.class.getName();
    private CachingHttpClient mHttpClient;

    public LunchWebClient(Context context) {
        mHttpClient = new CachingHttpClient.Builder(context)
                .okHttpClient(new OkHttpClient.Builder().build())
                .cache("caching_okhttp_client", 10 * 1024 * 1024)
                .parser(new GsonResponseParser())
                .build();
    }

    public <T> Observable<T> get(final String url, final Class<T> clazz) {
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", CONTENT_TYPE)
                .build();

        return mHttpClient.getAsync(new CachingRequest.Builder(request).build(), clazz)
                .toObservable();
    }

    public <T> Observable<T> fetch(final String url, final Class<T> clazz) {
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", CONTENT_TYPE)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        return mHttpClient.getAsync(new CachingRequest.Builder(request).build(), clazz)
                .toObservable();
    }
}
