package com.galaxy.ishare;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.galaxy.ishare.constant.BroadcastActionConstant;
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

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Global.screenWidth = wm.getDefaultDisplay().getWidth();
        Global.screenHeight = wm.getDefaultDisplay().getHeight();

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
            Log.v(TAG, IShareContext.getInstance().getCurrentUser().getKey() + "KEY");
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
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            User.UserLocation userLocation = new User.UserLocation(city, province, district, locationStr, longitude, latitude);
            IShareContext.getInstance().setUserLocation(userLocation);

            // 获取新的location ，发出广播,更新位置
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(BroadcastActionConstant.UPDATE_USER_LOCATION));



            mLocationClient.stop();

            // 将新的位置存在sp中
            User user =null;
            user=IShareContext.getInstance().getCurrentUser();
            if (user!=null){
                user.setUserLocation(userLocation);
            }else {
                user = new User ();
                user.setUserLocation(userLocation);
            }
            IShareContext.getInstance().saveCurrentUser(user);

        }


    }
}
