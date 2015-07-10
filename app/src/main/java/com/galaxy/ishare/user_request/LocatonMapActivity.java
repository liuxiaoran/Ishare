package com.galaxy.ishare.user_request;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.baidu.mapapi.map.MapView;
import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/6/16.
 */
public class LocatonMapActivity extends IShareActivity {

    private MapView requesterLocateMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IShareContext.getInstance().createActionbar(this, true, "店的地址");
        setContentView(R.layout.location_map_activity);

        requesterLocateMapView = (MapView) findViewById(R.id.request_location_map_activity);

    }
}
