package com.galaxy.ishare;


import android.app.Application;


public class IShareApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Global.mContext = this;
        Global.mDensity = getResources().getDisplayMetrics().density;
    }    
}
