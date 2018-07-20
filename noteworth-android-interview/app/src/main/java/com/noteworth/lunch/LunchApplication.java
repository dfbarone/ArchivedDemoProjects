package com.noteworth.lunch;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.noteworth.lunch.model.LunchModel;

public class LunchApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Init http stack and main model class
        LunchModel.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
