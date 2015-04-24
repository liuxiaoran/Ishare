package com.galaxy.ishare;


import android.app.Application;


public class IShareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IShareContext.mContext = this;
        IShareContext.mDensity = getResources().getDisplayMetrics().density;
    }
}
