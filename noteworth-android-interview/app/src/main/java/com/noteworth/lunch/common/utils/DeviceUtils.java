package com.noteworth.lunch.common.utils;

import android.content.Context;

import com.noteworth.lunch.R;

public class DeviceUtils {

    public static boolean isPhone(Context context) {
        return !context.getResources().getBoolean(R.bool.tablet_device);
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.tablet_device);
    }

}
