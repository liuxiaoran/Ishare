package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/20.
 * 传入PARAMETER_ADDR
 */
public class CardOwnerAvailableMapActivity extends ActionBarActivity {

    public  static final String PARAMETER_ADDR = "PARAMETER_ADDR";
    public static final String PARAMETER_REQUEST_CODE="PARAMETER_REQUEST_CODE";

    public static final int ADD_TO_MAP_REQUEST_CODE =1;
    public static final int EDIT_TO_MAP_REQUEST_CODE=2;

    public  static final String LOCATION_LONGITUDE="LOCATION_LONGITUDE";
    public  static final String LOCATION_LATITIDE="LOCATION_LATITIDE";
    private ListView locationAlterListView;
    private MapView mapView;
    private BaiduMap baiduMap;
    private AlterListViewAdapter adapter;

    private ArrayList<PoiInfo> poiList;
    private PoiSearch mPoiSearch;
    private int requstCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_available_map_activity);

        android.support.v7.app.ActionBar actionBar = IShareContext.getInstance().createDefaultActionbar(this);
        TextView titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("确定地址位置");

        String addr = getIntent().getStringExtra(PARAMETER_ADDR);
        requstCode=getIntent().getIntExtra(PARAMETER_REQUEST_CODE,0);
        Log.v("cardpublish","addr"+addr);

        initViews();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        baiduMap.setMapStatus(msu);
        initPoiSearch();

        locationAlterListView.setAdapter(adapter);
        locationAlterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 将地图中点移动到指定目标LatLng

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(poiList.get(position).location);
                baiduMap.animateMapStatus(u);
            }
        });

        searchPoi(addr);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_save) {
            LatLng confirmLatLng = baiduMap.getMapStatus().target;
            // 返回父界面

            Intent intent =null;
            int resultCode=0;
            if (requstCode==ADD_TO_MAP_REQUEST_CODE) {
                intent = new Intent(this, CardOwnerAvailableAddActivity.class);
                resultCode=CardOwnerAvailableAddActivity.MAP_TO_ADD_RESULT_CODE;
            }else {
                intent  = new Intent (this,CardOwnerAvailableEditActivity.class);
                resultCode=CardOwnerAvailableEditActivity.MAP_TO_EDIT_RESULT_CODE;
            }
            intent.putExtra(LOCATION_LONGITUDE,confirmLatLng.longitude);
            intent.putExtra(LOCATION_LATITIDE, confirmLatLng.latitude);
            Log.v("cardpublish", "longitude" + confirmLatLng.longitude);
            Log.v("cardpublish","latitude"+confirmLatLng.latitude);
            setResult(resultCode, intent);
            finish();


        } else if (item.getItemId() == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void initPoiSearch() {
        // poiSearch
        mPoiSearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener poiListenr = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result == null
                        || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    Toast.makeText(CardOwnerAvailableMapActivity.this, "未找到结果", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                    // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                    String strInfo = "在";
                    for (CityInfo cityInfo : result.getSuggestCityList()) {
                        strInfo += cityInfo.city;
                        strInfo += ",";
                    }
                    strInfo += "找到结果";
                    Toast.makeText(CardOwnerAvailableMapActivity.this, strInfo, Toast.LENGTH_LONG)
                            .show();
                }
                if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                    // 将结果加到listview的data中
                    List<PoiInfo> poiBaiduList = result.getAllPoi();
                    for (PoiInfo info : poiBaiduList) {
                        poiList.add(info);
                    }

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        };
        mPoiSearch.setOnGetPoiSearchResultListener(poiListenr);


    }

    private void searchPoi(String addr) {
//        mPoiSearch.searchInCity((new PoiCitySearchOption())
//                .city(IShareContext.getInstance().getUserLocation().getCity())
//                .keyword(addr)
//                .pageNum(0));
        mPoiSearch.searchNearby((new PoiNearbySearchOption())
                .keyword(addr)
                .pageNum(0)
                .location(new LatLng(IShareContext.getInstance().getUserLocation().getLatitude(),IShareContext.getInstance().getUserLocation().getLongitude()))
                .radius(500));
    }


    private void initViews() {
        mapView = (MapView) findViewById(R.id.publishware_owner_available_mapview);
        baiduMap = mapView.getMap();

        locationAlterListView = (ListView) findViewById(R.id.publishware_owner_location_listview);
        adapter = new AlterListViewAdapter(this);
        poiList = new ArrayList<>();
    }

    class AlterListViewAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public AlterListViewAdapter(Context context) {

            layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return poiList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.publishware_owner_poi_listview, null);

            }
            TextView nameTv = (TextView) convertView.findViewById(R.id.publishware_owner_poi_name_tv);
            TextView locationTv = (TextView) convertView.findViewById(R.id.publishware_owner_poi_location_tv);
            nameTv.setText(poiList.get(position).name);
            locationTv.setText(poiList.get(position).address);

            return convertView;
        }
    }


    @Override
    protected void onPause() {
        // MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        mapView.onResume();
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        // MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        super.onDestroy();
        mapView.onDestroy();
        mPoiSearch.destroy();


    }


}
