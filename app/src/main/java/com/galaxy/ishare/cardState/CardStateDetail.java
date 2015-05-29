package com.galaxy.ishare.cardState;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ZoomControls;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardState;

/**
 * Created by YangJunLin on 2015/5/26.
 */
public class CardStateDetail extends Activity {

    MapView mMapView;
    BaiduMap mBaiduMap;

    TextView type;
    TextView discount;
    TextView shopName;
    TextView shopDistance;
    TextView cardDistance;
    TextView cardStation;
    ImageButton stateDetailBack;


    BitmapDescriptor basic = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.state_card_detail);

        type = (TextView) findViewById(R.id.card_type_detail);
        discount = (TextView) findViewById(R.id.discount_detail);
        shopName = (TextView) findViewById(R.id.state_shop_name_detail);
        shopDistance = (TextView) findViewById(R.id.shop_distance_detail);
        cardDistance = (TextView) findViewById(R.id.card_distance_detail);
        cardStation = (TextView) findViewById(R.id.card_station_detail);
        stateDetailBack = (ImageButton) findViewById(R.id.card_state_detail_back);
        CardState state = (CardState) getIntent().getSerializableExtra("stateDetail");
        mMapView = (MapView) findViewById(R.id.state_card_detail_bdmap);
        mBaiduMap = mMapView.getMap();
        //隐藏缩放按钮
        int count = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);

        mMapView.removeViewAt(1);

        //设定中心点坐标
        LatLng local = new LatLng(IShareContext.getInstance().getCurrentUser().getUserLocation().getLatitude(), IShareContext.getInstance().getUserLocation().getLongitude());
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(local)
                .zoom(16)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);

        OverlayOptions overlayOptions = new MarkerOptions().position(local).icon(basic);
        mBaiduMap.addOverlay(overlayOptions);

        type.setText(state.getTrade_type());
        discount.setText(String.valueOf(state.getDiscount()));
        shopName.setText(state.getShop_name());
        shopDistance.setText(String.valueOf(state.getShop_distance()));
        cardDistance.setText(String.valueOf(state.getOwner_distance()));
        cardStation.setText(state.getStatus());

        View.OnClickListener stateChangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getApplicationContext()).setTitle("请选择").setSingleChoiceItems(new String[]{}, 0, null).show();
            }
        };

        stateDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
