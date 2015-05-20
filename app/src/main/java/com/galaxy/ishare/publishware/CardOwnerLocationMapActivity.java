package com.galaxy.ishare.publishware;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.galaxy.ishare.R;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/11.
 */
public class CardOwnerLocationMapActivity extends ActionBarActivity implements
        OnGetGeoCoderResultListener {


    private static final String PARAMETER_latitude = "latitude";
    private static final String PARAMETER_longitude = "longitude";


    private MapView ownerMapView;
    private BaiduMap ownerBaduMap;


    private LatLng ownerLatLng;

    private GeoCoder mSearch;

    private FButton confirmBtn;

    private String ownerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_owner_location_map_activity);

        ownerMapView = (MapView) findViewById(R.id.publishware_owner_location_mapview);
        confirmBtn = (FButton) findViewById(R.id.publishware_owner_location_confirm_btn);
        ownerBaduMap = ownerMapView.getMap();


        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        Double latitude = getIntent().getDoubleExtra(PARAMETER_latitude, 0);
        Double longitude = getIntent().getDoubleExtra(PARAMETER_longitude, 0);
        ownerLatLng = new LatLng(latitude, longitude);

        // 将中点移动到传入的LatLng
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ownerLatLng);
        ownerBaduMap.animateMapStatus(u);


    }

    public void confirmLocation() {
        LatLng ownerLatLng = ownerBaduMap.getMapStatus().target;


        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ownerLatLng));

        Intent retIntent = new Intent(CardOwnerLocationMapActivity.this, PublishItemActivity.class);
        retIntent.putExtra(PublishItemActivity.PARAMETER_RET_OWNER_ADDR, ownerAddress);
        setResult(PublishItemActivity.PARAMETER_OWNER_LOCATION_RESULT_CODE, retIntent);
        finish();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

        ownerAddress = reverseGeoCodeResult.getAddress();

    }

    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        super.onPause();
        ownerMapView.onPause();

    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        super.onResume();
        ownerMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        super.onDestroy();
        ownerMapView.onPause();
    }
}
