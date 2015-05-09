package com.galaxy.ishare;


import android.app.Application;
import android.util.Log;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.galaxy.ishare.model.User;

import java.util.HashMap;


public class IShareApplication extends Application {

    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;


    private static final String TAG = "application";

    @Override
    public void onCreate() {
        super.onCreate();

        Global.mContext = this;
        Global.mDensity = getResources().getDisplayMetrics().density;

        // 初始化sp
        IShareContext.getInstance().init(getApplicationContext());

        // baidu map init
        SDKInitializer.initialize(getApplicationContext());

        // baidu location init
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

        // 将变量读入到Global中
        if (IShareContext.getInstance().getCurrentUser()!=null){
            Global.key = IShareContext.getInstance().getCurrentUser().getKey();
            Global.phone = IShareContext.getInstance().getCurrentUser().getUserPhone();

        }




    }



    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String locationStr= location.getAddrStr();
            String city = location.getCity();
            String province = location.getProvince();
            String district = location.getDistrict();
            String longitude = location.getLongitude()+"";
            String latitude = location.getLatitude()+"";
            IShareContext.getInstance().setUserLocation(new User.UserLocation(city,province,district,locationStr,longitude,latitude));

            mLocationClient.stop();
        }


    }
}
