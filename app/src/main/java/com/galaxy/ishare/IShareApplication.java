package com.galaxy.ishare;


import android.app.Application;


public class IShareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Global.mContext = this;
        Global.mDensity = getResources().getDisplayMetrics().density;

        // 初始化sp
        IShareContext.getInstance().init(getApplicationContext());

        // 将变量读入到Global中
        if (IShareContext.getInstance().getCurrentUser()!=null){
            Global.key = IShareContext.getInstance().getCurrentUser().getKey();
            Global.phone = IShareContext.getInstance().getCurrentUser().getUserPhone();
        }

    }
}
