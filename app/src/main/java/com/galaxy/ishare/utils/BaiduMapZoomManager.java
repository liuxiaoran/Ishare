package com.galaxy.ishare.utils;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/6/17.
 */
public class BaiduMapZoomManager {

    private static BaiduMapZoomManager instance;
    private MapView mapView;
    private Button zoomOutBtn;
    private Button zoomInBtn;
    float maxZoomLevel;
    float minZoomLevel;

    public BaiduMapZoomManager(MapView mapView, Button zoomOutBtn, Button zoomInBtn) {

        this.mapView = mapView;
        this.zoomOutBtn = zoomOutBtn;
        this.zoomInBtn = zoomInBtn;

    }

    public static BaiduMapZoomManager getInstance(MapView mapView, Button zoomOutBtn, Button zoomInBtn) {
        if (instance == null) {
            instance = new BaiduMapZoomManager(mapView, zoomOutBtn, zoomInBtn);
        }

        return instance;
    }

    public void hideOriginalZoomBtn() {
        int count = mapView.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = mapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }

        maxZoomLevel = mapView.getMap().getMaxZoomLevel();
        minZoomLevel = mapView.getMap().getMinZoomLevel();
    }

    public void setZoomListener() {
        zoomInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoomLevel = mapView.getMap().getMapStatus().zoom;
                if (zoomLevel < maxZoomLevel) {
                    mapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomIn());
                    zoomOutBtn.setEnabled(true);
                } else {
                    zoomInBtn.setEnabled(false);
                }
            }
        });
        zoomOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float zoomLevel = mapView.getMap().getMapStatus().zoom;
                if (zoomLevel > minZoomLevel) {
                    mapView.getMap().setMapStatus(MapStatusUpdateFactory.zoomOut());
                    zoomInBtn.setEnabled(true);
                } else {
                    zoomOutBtn.setEnabled(false);
                }
            }
        });
    }

}
