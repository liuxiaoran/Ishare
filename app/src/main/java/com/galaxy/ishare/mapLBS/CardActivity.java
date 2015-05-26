package com.galaxy.ishare.mapLBS;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.baidu.mapapi.model.LatLngBounds;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.JsonObjectUtil;
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
public class CardActivity extends Activity {
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
    private InfoWindow mShopInfoWindow;

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
    BitmapDescriptor cardC = BitmapDescriptorFactory.fromResource(R.drawable.card_choiced);

    RadioGroup.OnCheckedChangeListener radioButtonListener;
    ImageButton mapShowType;
    Button cardGroupShowLeft;
    Button cardGroupShowRight;
    Button shop_ka_choice;
    Button cardShowLeft;
    Button cardShowRight;
    boolean isFirstLoc = true;
    Marker cardMarker = null;
    Marker shopMarker = null;
    private static final String TAG = "CardActivity";
    private static final int page_size = 10;
    private static int page_card = 1;
    private static List<CardItem> mapCardList = null;
    private static List<Marker> liveMarkers = null;
    private static int flag = 0;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_map);
        mapShowType = (ImageButton) findViewById(R.id.card_map_type);
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

        cardGroupShowLeft = (Button) findViewById(R.id.card_group_to_left);
        View.OnClickListener showLeftListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page_card > 1) {
                    page_card--;
                    getMapCardInfoFromServer();
                } else {
                    Toast.makeText(getApplicationContext(), "到顶了", Toast.LENGTH_SHORT).show();
                }
            }
        };
        cardGroupShowLeft.setOnClickListener(showLeftListener);

        cardGroupShowRight = (Button) findViewById(R.id.card_group_to_right);
        View.OnClickListener showRightListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_card++;
                getMapCardInfoFromServer();
            }
        };
        cardGroupShowRight.setOnClickListener(showRightListener);

        cardShowLeft = (Button) findViewById(R.id.card_to_left);
        View.OnClickListener cardShowLeftListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag--;
                showInfoWindowByTime();
            }
        };
        cardShowLeft.setOnClickListener(cardShowLeftListener);

        cardShowRight = (Button) findViewById(R.id.card_to_right);
        View.OnClickListener cardShowRightListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag++;
                showInfoWindowByTime();
            }
        };
        cardShowRight.setOnClickListener(cardShowRightListener);

        mMapView = (MapView) findViewById(R.id.bmapView);

        mMapView.showScaleControl(true);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        mLocClient.start();

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                if (shopMarker != null) {
                    shopMarker.remove();
                }
                Button shopButton = new Button(getApplicationContext());
                Button cardButton = new Button(getApplicationContext());
                marker.setToTop();
                marker.setIcon(cardC);
                LatLng ll = marker.getPosition();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                if (mapCardList != null && mapCardList.size() > 0) {
                    for (CardItem card : mapCardList) {
                        if (card.getOwnerLatitude() == ll.latitude && card.getOwnerLongitude() == ll.longitude) {
                            marker.setTitle("sadsa");
                            LatLng shop = new LatLng(card.getShopLatitude(), card.getShopLongitude());
                            builder.include(shop);
                            OverlayOptions shopOverlay = new MarkerOptions().position(shop).icon(BitmapDescriptorFactory.fromResource(R.drawable.card_shop)).zIndex(9);
                            shopMarker = (Marker) mBaiduMap.addOverlay(shopOverlay);
                            Button cardInfo = new Button(getApplicationContext());
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(card.getOwnerName() + "折，");
                            buffer.append(card.getShopName());
                            cardInfo.setText(buffer);
                            mInfoWindow = new InfoWindow(cardInfo, ll, -47);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        }
                    }
                    /*if (marker.getPosition().latitude == shopMarker.getPosition().latitude && marker.getPosition().longitude == shopMarker.getPosition().longitude) {
                        Intent intent = new Intent(CardActivity.this, PanoramActivity.class);
                        intent.putExtra("lat", shopMarker.getPosition().latitude);
                        intent.putExtra("lon", shopMarker.getPosition().longitude);
                        startActivity(intent);
                    }*/
                }
                return true;
            }
        });

//        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(12.0f);
//        mBaiduMap.setMapStatus(msu);
        getMapCardInfoFromServer();

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                flag = 0;
                timer.schedule(task, 0, 10000);
            }
        });
    }

    Timer timer = new Timer();
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    flag++;
                    showInfoWindowByTime();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


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

    private void showInfoWindowByTime() {
        if (liveMarkers != null) {
            if (flag >= liveMarkers.size()) {
                flag = 0;
            } else if (flag < 0) {
                flag = liveMarkers.size() - 1;
            }

            if (shopMarker != null) {
                shopMarker.remove();
            }
            LatLng cardLo = liveMarkers.get(flag).getPosition();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(IShareContext.getInstance().getUserLocation().getLatitude(), IShareContext.getInstance().getUserLocation().getLongitude()));
            if (mapCardList != null && mapCardList.size() > 0) {
                for (CardItem card : mapCardList) {
//                builder.include(new LatLng(card.getOwnerLatitude(), card.getOwnerLongtude()));
                    if (card.getOwnerLatitude() == cardLo.latitude && card.getOwnerLongitude() == cardLo.longitude) {
                        LatLng shop = new LatLng(card.getShopLatitude(), card.getShopLongitude());
                        builder.include(shop);
                        builder.include(new LatLng(cardLo.latitude, cardLo.longitude));
                        OverlayOptions shopOverlay = new MarkerOptions().position(shop).icon(BitmapDescriptorFactory.fromResource(R.drawable.card_shop)).zIndex(9);
                        shopMarker = (Marker) mBaiduMap.addOverlay(shopOverlay);
                        Button cardInfo = new Button(getApplicationContext());
                        cardInfo.setBackgroundResource(R.drawable.popup_big);
//                    cardInfo.setBackgroundResource(R.drawable.button_shape);
                        StringBuffer cardbuffer = new StringBuffer();
                        cardbuffer.append(card.getDiscount() + "折，");
                        cardbuffer.append(card.getShopName());
                        int i = 12;
                        while (i < cardbuffer.length()) {
                            cardbuffer.insert(i, "\n");
                            i += 12;
                        }
                        cardInfo.setText(cardbuffer);
                        cardInfo.setTextColor(getResources().getColor(R.color.color_primary));
                        mInfoWindow = new InfoWindow(cardInfo, cardLo, -47);
                        mBaiduMap.showInfoWindow(mInfoWindow);
                    }
                }
            }
            LatLngBounds latLngBounds = builder.build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(latLngBounds);
            mBaiduMap.animateMapStatus(mapStatusUpdate);
        }
    }

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
                public User onRecvOK(HttpRequestBase request, String result) {
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
                                CardItem cardItem = JsonObjectUtil.parseJsonToCardItem(card.toString());
                                mapCards.add(cardItem);
                            }
                            mapCardList = mapCards;
                            Map<Double, Double> tmp = new HashMap<>();
                            for (CardItem card : mapCards) {
                                tmp.put(card.getOwnerLatitude(), card.getOwnerLongitude());
                            }

                            initOverlay(tmp);
                        } else {
                            Toast.makeText(CardActivity.this, "由于网络原因，请求失败，请重试", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.toString());
                    }
                    return null;
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, retCode.toString());
                    Toast.makeText(CardActivity.this, "由于网络原因，请求失败，请重试", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {
                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "请确认您已经联网并打开GPS", Toast.LENGTH_LONG).show();
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
        mMapView.onPause();
        timer.cancel();
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

