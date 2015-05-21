package com.galaxy.ishare.utils;

import com.baidu.location.BDLocation;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.model.User;

/**
 * Created by YangJunLin on 2015/5/19.
 */
public class MapUtil {

    /**
     * ��ʵʱ���û�λ����Ϣ���浽��̬�������档
     * @param location
     */
    public static void updateLocation(BDLocation location) {
        String locationStr = location.getAddrStr();
        String city = location.getCity();
        String province = location.getProvince();
        String district = location.getDistrict();
        double longitude = location.getLongitude() ;
        double latitude = location.getLatitude();
        IShareContext.getInstance().setUserLocation(new User.UserLocation(city, province, district, locationStr, longitude, latitude));
    }
}
