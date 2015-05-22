package com.galaxy.ishare.sharedcard;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/5/22.
 */
public class CardDetailMapActivity extends ActionBarActivity {

    public static final String PARAMETER_SHOP_LATITUDE = "PARAMETER_SHOP_LATITUDE";
    public static final String PARAMETER_SHOP_LONGITUDE = "PARAMENTE_SHOP_LONGITUDE";
    public static final String PARAMETER_OWNER_LATITUDE = "PARAMETER_OWNER_LATITUDE";
    public static final String PARAMETER_OWNER_LONGITUDE = "PARAMETER_OWNER_LONGITUDE";


    private double shopLatitude;
    private double shopLongitude;
    private double ownerLatitude;
    private double ownerLongitude;
    private double userLatitude;
    private double userLongitude;

    private BaiduMap mBaiduMap;


    private LatLng shopLatLng;
    private LatLng ownerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_item_detail_map_activity);

        mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.share_item_detail_map_fragment))).getBaiduMap();

        shopLatitude = getIntent().getDoubleExtra(PARAMETER_SHOP_LATITUDE, 0);
        shopLongitude = getIntent().getDoubleExtra(PARAMETER_SHOP_LONGITUDE, 0);
        ownerLatitude = getIntent().getDoubleExtra(PARAMETER_OWNER_LATITUDE, 0);
        ownerLongitude = getIntent().getDoubleExtra(PARAMETER_OWNER_LONGITUDE, 0);
        userLatitude = IShareContext.getInstance().getUserLocation().getLatitude();
        userLongitude = IShareContext.getInstance().getUserLocation().getLongitude();

        shopLatLng = new LatLng(shopLatitude, shopLongitude);
        ownerLatLng = new LatLng(ownerLatitude, ownerLongitude);

        initMap();



    }
    public void initMap(){
        // 选出三点中东南西北的边界坐标
        double northLatitude = shopLatitude;
        double southLatitude = shopLatitude;
        double westLongitude = shopLongitude;
        double eastLongitude = shopLongitude;

        if (ownerLatitude>userLatitude){
            if (ownerLatitude>northLatitude){
                northLatitude=ownerLatitude;
            }
        }else {
            if (userLatitude >northLatitude){
                northLatitude=userLatitude;
            }
        }

        if (ownerLatitude<userLatitude){
            if (ownerLatitude<southLatitude){
                southLatitude=ownerLatitude;
            }
        }else {
            if (userLatitude<southLatitude){
                southLatitude=userLatitude;
            }
        }

        if (ownerLongitude<userLongitude){
            if (ownerLongitude<westLongitude) {
                westLongitude=ownerLongitude;
            }
        }else {
            if (userLongitude<westLongitude){
                westLongitude=userLongitude;
            }
        }

        if (ownerLongitude>userLongitude){
            if (ownerLongitude>eastLongitude){
                eastLongitude=ownerLongitude;
            }
        }else {
            if (userLongitude>eastLongitude){
                eastLongitude=userLongitude;
            }
        }

        // 设置地图范围，要最大显示三个点
        LatLngBounds latLngBounds =new LatLngBounds.Builder().include(new LatLng(northLatitude+20,eastLongitude+20))
                .include(new  LatLng(southLatitude-20,westLongitude-20)).build();

        MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newLatLngBounds(latLngBounds);
        mBaiduMap.setMapStatus(mapStatusUpdate);


        // 设置Marker


    }
}