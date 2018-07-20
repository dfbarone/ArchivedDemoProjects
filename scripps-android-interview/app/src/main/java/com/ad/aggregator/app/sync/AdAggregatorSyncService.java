package com.ad.aggregator.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AdAggregatorSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static AdAggregatorSyncAdapter sAdAggregatorSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AdAggregatorSyncService", "onCreate - AdAggregatorSyncService");
        synchronized (sSyncAdapterLock) {
            if (sAdAggregatorSyncAdapter == null) {
                sAdAggregatorSyncAdapter = new AdAggregatorSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sAdAggregatorSyncAdapter.getSyncAdapterBinder();
    }
}