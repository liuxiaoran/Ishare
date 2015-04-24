package com.galaxy.ishare;

import android.content.Context;
import android.os.Handler;

public class IShareContext {
    public static Context mContext;

    public static float mDensity;

    public static Handler mMainThreadHandler = new Handler();

    public static void RunOnUiThread(Runnable task) {
        mMainThreadHandler.post(task);
    }

    public static boolean mIsTest = true;
}
