package com.galaxy.ishare.mapLBS;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.MapShop;
import com.galaxy.ishare.utils.MapUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by YangJunLin on 2015/5/18.
 */
public class MapActivity extends Activity {
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    MapView mMapView;
    BaiduMap mBaiduMap;
    private Marker mMarkerA;
    private Marker mMarkerB;
    private Marker mMarkerC;
    private Marker mMarkerD;
    private Marker mMarkerE;
    private Marker mMarkerF;
    private Marker mMarkerG;
    private Marker mMarkerH;
    private Marker mMarkerI;
    private Marker mMarkerJ;
    private InfoWindow mInfoWindow;

    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);
    BitmapDescriptor bdB = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markb);
    BitmapDescriptor bdC = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markc);
    BitmapDescriptor bdD = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markd);
    BitmapDescriptor bdE = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marke);
    BitmapDescriptor bdF = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markf);
    BitmapDescriptor bdG = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markg);
    BitmapDescriptor bdH = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markh);
    BitmapDescriptor bdI = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marki);
    BitmapDescriptor bdJ = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_markj);
    BitmapDescriptor bd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_gcoding);
    BitmapDescriptor bdGround = BitmapDescriptorFactory
            .fromResource(R.drawable.ground_overlay);

    RadioGroup.OnCheckedChangeListener radioButtonListener;
    ImageButton mapShowType;
    ImageButton showLeft;
    ImageButton showRight;
    Button shop_ka_choice;
    boolean isFirstLoc = true;
    private static final String TAG = "MapActivity";
    private static final int page_size = 10;
    private static int page_shop = 1;
    private static int page_card = 1;
    private static List<MapShop> mapShopList = null;
    private static List<CardItem> mapCardList = null;
    private static List<Marker> liveMarkers = null;
    private static int flag = 0;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        mapShowType = (ImageButton) findViewById(R.id.map_button_type);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        mapShowType.setBackgroundResource(R.drawable.icon_map_follow);
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case COMPASS:
                        mapShowType.setBackgroundResource(R.drawable.icon_map_nomal);
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                    case FOLLOWING:
                        mapShowType.setBackgroundResource(R.drawable.icon_map_compass);
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap
                                .setMyLocationConfigeration(new MyLocationConfiguration(
                                        mCurrentMode, true, mCurrentMarker));
                        break;
                }
            }
        };
        mapShowType.setOnClickListener(btnClickListener);

        shop_ka_choice = (Button) findViewById(R.id.shop_ka_type);
        shop_ka_choice.setText("shop");
        View.OnClickListener shopKaListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (shop_ka_choice.getText().equals("shop")) {
                    page_shop = 1;
                    getMapCardInfoFromServer();
                    Toast.makeText(getApplicationContext(), "正展示卡片ing", Toast.LENGTH_SHORT).show();
                    shop_ka_choice.setText("card");
                } else if (shop_ka_choice.getText().equals("card")) {
                    page_card = 1;
                    getMapShopInfoFromServer();
                    Toast.makeText(getApplicationContext(), "正展示商铺ing", Toast.LENGTH_SHORT).show();
                    shop_ka_choice.setText("shop");
                }
//                showInfoWindowsByTime();
            }
        };
        shop_ka_choice.setOnClickListener(shopKaListener);

        showLeft = (ImageButton) findViewById(R.id.map_to_left);
        View.OnClickListener showLeftListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shop_ka_choice.getText().equals("shop")) {
                    if (page_shop > 1) {
                        Toast.makeText(getApplicationContext(), "显示下上一组", Toast.LENGTH_SHORT).show();
                        page_shop--;
                        getMapShopInfoFromServer();
                    } else {
                        Toast.makeText(getApplicationContext(), "已经到顶了", Toast.LENGTH_SHORT).show();
                    }
                } else if (shop_ka_choice.getText().equals("card")) {
                    if (page_card > 1) {
                        Toast.makeText(getApplicationContext(), "显示下上一组", Toast.LENGTH_SHORT).show();
                        page_card--;
                        getMapCardInfoFromServer();
                    } else {
                        Toast.makeText(getApplicationContext(), "已经到顶了", Toast.LENGTH_SHORT).show();
                    }
                }
//                showInfoWindowsByTime();
            }
        };
        showLeft.setOnClickListener(showLeftListener);

        showRight = (ImageButton) findViewById(R.id.map_to_right);
        View.OnClickListener showRightListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "显示下一组", Toast.LENGTH_SHORT).show();
                if (shop_ka_choice.getText().equals("shop")) {
//                    shop_ka_choice.getTransitionName()
                    page_shop++;
                    getMapShopInfoFromServer();
                } else if (shop_ka_choice.getText().equals("card")) {
                    page_card++;
                    getMapCardInfoFromServer();
                }
//                showInfoWindowsByTime();
            }
        };
        showRight.setOnClickListener(showRightListener);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
//                button.setBackgroundResource(R.drawable.popup);
                LatLng ll = marker.getPosition();
                if (shop_ka_choice.getText().equals("shop")) {
                    if (mapShopList != null && mapShopList.size() > 0) {
                        for (MapShop shop : mapShopList) {
                            if (shop.getShop_latitude() == ll.latitude && shop.getShop_longitude() == ll.longitude) {
                                button.setText("商店名称：" + shop.getShop_name());
                                button.setText("与您的距离为" + shop.getShop_distance());
                                mInfoWindow = new InfoWindow(button, ll, -47);
                                mBaiduMap.showInfoWindow(mInfoWindow);
                            }
                        }
                    }
                } else if (shop_ka_choice.getText().equals("card")) {
                    if (mapCardList != null && mapCardList.size() > 0) {
                        for (CardItem card : mapCardList) {
                            if (card.getOwnerLatitude() == ll.latitude && card.getOwnerLongtude() == ll.longitude) {
                                button.setText("卡片所有人:" + card.getOwner());
                                button.setText("卡片信息:" + card.getDescription());
                                mInfoWindow = new InfoWindow(button, ll, -47);
                                mBaiduMap.showInfoWindow(mInfoWindow);
                            }
                        }
                    }
                }
                return true;
            }
        });

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);
        mBaiduMap.setMapStatus(msu);
        getMapShopInfoFromServer();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Toast.makeText(getApplicationContext(), "this is a test", Toast.LENGTH_LONG).show();
//                showInfoWindowsByTime();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null)
                return;
            MapUtil.updateLocation(location);
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    //从服务端获得商铺信息
    private void getMapShopInfoFromServer() {
        mBaiduMap.clear();
        double lat = IShareContext.getInstance().getUserLocation().getLatitude();
        double lon = IShareContext.getInstance().getUserLocation().getLongitude();
        if (lat != 0 && lon != 0) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
            params.add(new BasicNameValuePair("page_num", String.valueOf(page_shop)));
            params.add(new BasicNameValuePair("page_size", String.valueOf(page_size)));
            HttpTask.startAsyncDataGetRequset(URLConstant.MAP_SHOP_PAGE, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    int status = 0;
                    JSONObject jsonObject = null;
                    List<MapShop> mapShops = new ArrayList<MapShop>();
                    try {
                        jsonObject = new JSONObject(result);
                        status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jo = jsonArray.getJSONObject(i);
                                MapShop mapShop = new MapShop();
                                mapShop.setShop_distance(jo.getDouble("shop_distance"));
                                mapShop.setShop_latitude(jo.getDouble("shop_latitude"));
                                mapShop.setShop_longitude(jo.getDouble("shop_longitude"));
                                mapShop.setShop_location(jo.getString("shop_location"));
                                mapShop.setShop_name(jo.getString("shop_name"));
                                mapShops.add(mapShop);
                            }
                            mapShopList = mapShops;
                            Map<Double, Double> tmp = new HashMap<>();
                            for (MapShop shop : mapShops) {
                                tmp.put(shop.getShop_latitude(), shop.getShop_longitude());
                            }
                            initOverlay(tmp);
                        } else {
                            Toast.makeText(MapActivity.this, "由于网络原因，请求数据失败，请重试。", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, retCode.toString());
                    Toast.makeText(MapActivity.this, "网络不佳,请重试", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {
                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "请确定您已经联网并打开gps定位系统。", Toast.LENGTH_LONG).show();
        }
    }

    private void showInfoWindowsByTime() {
        flag = 0;
        Timer timer = new Timer();
        timer.schedule(new windowsTask(), 1, 2000);
    }

    private class windowsTask extends TimerTask {
        @Override
        public void run() {
            Button button = new Button(getApplicationContext());
//            button.setBackgroundResource(R.drawable.popup);
//            InfoWindow.OnInfoWindowClickListener listener = null;
            if (flag >= liveMarkers.size()) {
                flag = 0;
            }
            LatLng ll = liveMarkers.get(flag).getPosition();
            if (shop_ka_choice.getText().equals("shop")) {
                if (mapShopList != null && mapShopList.size() > 0) {
                    for (MapShop shop : mapShopList) {
                        if (shop.getShop_latitude() == ll.latitude && shop.getShop_longitude() == ll.longitude) {
                            button.setText("商店名称：" + shop.getShop_name());
                            button.setText("与您的距离为" + shop.getShop_distance());
                            mInfoWindow = new InfoWindow(button, ll, -47);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        }
                    }
                }
            } else if (shop_ka_choice.getText().equals("card")) {
                if (mapCardList != null && mapCardList.size() > 0) {
                    for (CardItem card : mapCardList) {
                        if (card.getOwnerLatitude() == ll.latitude && card.getOwnerLongtude() == ll.longitude) {
                            button.setText("卡片所有人:" + card.getOwner());
                            button.setText("卡片信息:" + card.getDescription());
                            mInfoWindow = new InfoWindow(button, ll, -47);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        }
                    }
                }
            }
            flag++;
        }
    }

    //从服务端获得卡片信息
    private void getMapCardInfoFromServer() {
        mBaiduMap.clear();
        String lat = String.valueOf(IShareContext.getInstance().getUserLocation().getLatitude());
        String lon = String.valueOf(IShareContext.getInstance().getUserLocation().getLongitude());
        if (lat != null && lat.length() > 0 && lon != null && lon.length() > 0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
            params.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
            params.add(new BasicNameValuePair("page_num", String.valueOf(page_card)));
            params.add(new BasicNameValuePair("page_size", String.valueOf(page_size)));
            HttpTask.startAsyncDataGetRequset(URLConstant.MAP_CARD_PAGE, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    int status = 0;
                    JSONObject jsonObject = null;
                    List<CardItem> mapCards = new ArrayList<CardItem>();
                    try {
                        jsonObject = new JSONObject(result);
                        status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject card = jsonArray.getJSONObject(i);
                                CardItem cardItem = new CardItem(card.getInt("id"), card.getString("owner"), card.getString("shop_name"), card.getInt("ware_type"), card.getDouble("discount"),
                                        card.getInt("trade_type"), card.getString("shop_location"), card.getDouble("shop_longitude"), card.getDouble("shop_latitude"),
                                        card.getString("description"), card.getString("img"), card.getString("time"), card.getDouble("longitude"), card.getDouble("latitude"),
                                        card.getString("location"), card.getDouble("distance"), card.getDouble("shop_distance"));

                                mapCards.add(cardItem);
                            }
                            mapCardList = mapCards;
                            Map<Double, Double> tmp = new HashMap<>();
                            for (CardItem card : mapCards) {
                                tmp.put(card.getOwnerLatitude(), card.getOwnerLongtude());
                            }
                            initOverlay(tmp);
                        } else {
                            Toast.makeText(MapActivity.this, "由于网络原因，请求数据失败，请重试。", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, retCode.toString());
                    Toast.makeText(MapActivity.this, "网络不佳,请重试", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {
                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "请确定您已经联网并打开gps定位系统。", Toast.LENGTH_LONG).show();
        }
    }

    private void initOverlay(Map<Double, Double> tmp) {
        liveMarkers = new ArrayList<>();
        List<Marker> markers = new ArrayList<>();
        // add marker overlay
        LatLng llA = null;
        LatLng llB = null;
        LatLng llC = null;
        LatLng llD = null;
        LatLng llE = null;
        LatLng llF = null;
        LatLng llG = null;
        LatLng llH = null;
        LatLng llI = null;
        LatLng llJ = null;

        OverlayOptions ooA = null;
        OverlayOptions ooB = null;
        OverlayOptions ooC = null;
        OverlayOptions ooD = null;
        OverlayOptions ooE = null;
        OverlayOptions ooF = null;
        OverlayOptions ooG = null;
        OverlayOptions ooH = null;
        OverlayOptions ooI = null;
        OverlayOptions ooJ = null;

        Set<Double> list = tmp.keySet();
        for (Double lat : list) {
            if (llA == null) {
                llA = new LatLng(lat, tmp.get(lat));
                ooA = new MarkerOptions().position(llA).icon(bdA)
                        .zIndex(9);
                mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));
                markers.add(mMarkerA);
                continue;
            }
            if (llB == null) {
                llB = new LatLng(lat, tmp.get(lat));
                ooB = new MarkerOptions().position(llB).icon(bdB)
                        .zIndex(9);
                mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
                markers.add(mMarkerB);
                continue;
            }
            if (llC == null) {
                llC = new LatLng(lat, tmp.get(lat));
                ooC = new MarkerOptions().position(llC).icon(bdC)
                        .zIndex(9);
                mMarkerC = (Marker) (mBaiduMap.addOverlay(ooC));
                markers.add(mMarkerC);
                continue;
            }
            if (llD == null) {
                llD = new LatLng(lat, tmp.get(lat));
                ooD = new MarkerOptions().position(llD).icon(bdD)
                        .zIndex(9);
                mMarkerD = (Marker) (mBaiduMap.addOverlay(ooD));
                markers.add(mMarkerD);
                continue;
            }
            if (llE == null) {
                llE = new LatLng(lat, tmp.get(lat));
                ooE = new MarkerOptions().position(llE).icon(bdE)
                        .zIndex(9);
                mMarkerE = (Marker) (mBaiduMap.addOverlay(ooE));
                markers.add(mMarkerE);
                continue;
            }
            if (llF == null) {
                llF = new LatLng(lat, tmp.get(lat));
                ooF = new MarkerOptions().position(llF).icon(bdF)
                        .zIndex(9);
                mMarkerF = (Marker) (mBaiduMap.addOverlay(ooF));
                markers.add(mMarkerF);
                continue;
            }
            if (llG == null) {
                llG = new LatLng(lat, tmp.get(lat));
                ooG = new MarkerOptions().position(llG).icon(bdG)
                        .zIndex(9);
                mMarkerG = (Marker) (mBaiduMap.addOverlay(ooG));
                markers.add(mMarkerG);
                continue;
            }
            if (llH == null) {
                llH = new LatLng(lat, tmp.get(lat));
                ooH = new MarkerOptions().position(llH).icon(bdH)
                        .zIndex(9);
                mMarkerH = (Marker) (mBaiduMap.addOverlay(ooH));
                markers.add(mMarkerH);
                continue;
            }
            if (llI == null) {
                llI = new LatLng(lat, tmp.get(lat));
                ooI = new MarkerOptions().position(llI).icon(bdI)
                        .zIndex(9);
                mMarkerI = (Marker) (mBaiduMap.addOverlay(ooI));
                markers.add(mMarkerI);
                continue;
            }
            if (llJ == null) {
                llJ = new LatLng(lat, tmp.get(lat));
                ooJ = new MarkerOptions().position(llJ).icon(bdJ)
                        .zIndex(9);
                mMarkerJ = (Marker) (mBaiduMap.addOverlay(ooJ));
                markers.add(mMarkerJ);
                continue;
            }
        }
        liveMarkers.addAll(markers);
    }

    @Override
    protected void onPause() {
        page_card = 1;
        page_shop = 1;
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        bdA.recycle();
        bdB.recycle();
        bdC.recycle();
        bdD.recycle();
        bdE.recycle();
        bdF.recycle();
        bdG.recycle();
        bdH.recycle();
        bdI.recycle();
        bdJ.recycle();
        super.onDestroy();
    }

}

