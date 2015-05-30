package com.galaxy.ishare.sharedcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/21.
 */
public class CardDetailActivity extends ActionBarActivity {

    public static final String PARAMETER_CARD_ITEM = "PARAMETER_CARD_ITEM";
    public static final String PARAMETER_WHO_SEND = "PARAMETER_WHO_SEND";

    private static final String TAG = "carddetail";
    private FButton borrowBtn, talkBtn;
    private ViewPager cardPager;
    private TextView shopNameTv, discountTv, ownerNameTv, tradeTypeTv, shopAddrTv, shopDistanceTv, cardTypeTv,
            ownerAddrTv, ownerDistanceTv, descriptionTv, statusTv;
    private MapView cardMapView;

    private CardItem cardItem;
    //一共有几张图片,即viewpager有几个view
    private int picNumber;
    // viewpager 中的ImageView
    private ImageView[] picIvs;

    private ArrayList<View> pagerList;

    private BaiduMap baiduMap;

    private BitmapDescriptor defaultPoiBitmap;
    private HttpInteract httpInteract;

    private CircleImageView ownerAvatarCv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_item_card_detail_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultActionbar(this);
        TextView titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("卡详情");

        cardItem = getIntent().getParcelableExtra(PARAMETER_CARD_ITEM);
        if (cardItem.cardImgs != null) {
            picNumber = cardItem.cardImgs.length;
        }

        picIvs = new ImageView[picNumber];

        pagerList = new ArrayList<>();
        httpInteract = new HttpInteract();

        initViews();

        initCardPager();

        writeValueIntoViews();

        initMapView();

        // 地图点击进入新的界面，展示三方位置
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Intent intent = new Intent(CardDetailActivity.this, CardDetailMapActivity.class);
                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LONGITUDE, cardItem.ownerLongitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LATITUDE, cardItem.ownerLatitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LONGITUDE, cardItem.shopLongitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LATITUDE,cardItem.shopLatitude);

                startActivity(intent);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        ButtonClickListener clickListener = new ButtonClickListener();
        borrowBtn.setOnClickListener(clickListener);
        talkBtn.setOnClickListener(clickListener);


    }

    private void initMapView() {

        baiduMap = cardMapView.getMap();


        // 地图的中点显示店的位置
        float zoomLevel = 15;// 3-20
        LatLng shopLatLng = new LatLng(cardItem.shopLatitude, cardItem.shopLongitude);
        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLngZoom(shopLatLng, zoomLevel);
        baiduMap.setMapStatus(statusUpdate);

        defaultPoiBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding);

        // 将店的位置的marker显示出来
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(shopLatLng)
                .icon(defaultPoiBitmap)
                .zIndex(9)
                .draggable(false);
        //在地图上添加Marker，并显示
        Marker newMarker = (Marker) baiduMap.addOverlay(option);

    }

    private void writeValueIntoViews() {
        shopNameTv.setText(cardItem.shopName);
        discountTv.setText(cardItem.discount + "折");
        ownerNameTv.setText(cardItem.ownerName);
        String[] trades = getResources().getStringArray(R.array.trade_items);
        tradeTypeTv.setText(trades[cardItem.tradeType]);
        shopAddrTv.setText(cardItem.shopLocation);
        shopDistanceTv.setText(cardItem.shopDistance + "");
        String[] cardTypes = getResources().getStringArray(R.array.card_items);
        cardTypeTv.setText(cardTypes[cardItem.wareType]);
        ownerAddrTv.setText(cardItem.ownerLocation);
        ownerDistanceTv.setText(cardItem.ownerDistance + "");
        descriptionTv.setText(cardItem.description);
        statusTv.setText(cardItem.cardStatus);

        ImageSize avatarSize = new ImageSize(DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 40));
        Log.v(TAG, "avatar link:" + cardItem.ownerAvatar);
        ImageLoader.getInstance().loadImage(cardItem.ownerAvatar, avatarSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                ownerAvatarCv.setImageBitmap(loadedImage);
            }
        });


    }

    private void initViews() {

        borrowBtn = (FButton) findViewById(R.id.share_item_detail_borrow_btn);
        talkBtn = (FButton) findViewById(R.id.share_item_detail_talk_btn);

        cardPager = (ViewPager) findViewById(R.id.share_item_detail_viewpager);

        shopNameTv = (TextView) findViewById(R.id.share_item_detail_shop_name_tv);
        discountTv = (TextView) findViewById(R.id.share_item_detail_discount_tv);
        ownerNameTv = (TextView) findViewById(R.id.share_item_detail_owner_name_tv);
        tradeTypeTv = (TextView) findViewById(R.id.share_item_detail_trade_type_tv);
        shopAddrTv = (TextView) findViewById(R.id.share_item_detail_shop_location_tv);
        shopDistanceTv = (TextView) findViewById(R.id.share_item_detail_shop_distance_tv);
        cardTypeTv = (TextView) findViewById(R.id.share_item_detail_card_type_tv);
        ownerAddrTv = (TextView) findViewById(R.id.share_item_detail_owner_location_tv);
        ownerDistanceTv = (TextView) findViewById(R.id.share_item_detail_owner_distance_tv);
        descriptionTv = (TextView) findViewById(R.id.share_item_detail_card_description_tv);
        statusTv = (TextView) findViewById(R.id.share_item_detail_card_status_tv);

        cardMapView = (MapView) findViewById(R.id.share_item_detail_mapview);

        ownerAvatarCv = (CircleImageView) findViewById(R.id.share_item_detail_owner_avatar_cv);


    }

    private void initCardPager() {
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < picNumber; i++) {
            View view = inflater.inflate(R.layout.share_item_detail_viewpager, null);
            picIvs[i] = (ImageView) view.findViewById(R.id.share_item_detail_card_pager_iv);

            ImageSize imageSize = new ImageSize(Global.screenWidth - DisplayUtil.dip2px(this, 5), DisplayUtil.dip2px(this, 400));
            final int finalI = i;
            Log.v(TAG, "carddetail  " + cardItem.cardImgs[i]);
            ImageLoader.getInstance().loadImage(cardItem.cardImgs[i], imageSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    picIvs[finalI].setImageBitmap(loadedImage);

                }
            });
            pagerList.add(view);
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter();
        cardPager.setAdapter(pagerAdapter);


    }

    class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.share_item_detail_borrow_btn) {
                // 借卡
                httpInteract.borrowCard();

            } else if (v.getId() == R.id.share_item_detail_talk_btn) {
                // 转跳到聊天

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            if (getIntent().getStringExtra(PARAMETER_WHO_SEND).equals(ItemListFragment.INTENT_ITEM_TO_DETAIL)) {
                NavUtils.navigateUpFromSameTask(this);
            } else {
                this.finish();
            }
        }
        return true;
    }

    public class MyPagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position), 0);//添加页卡
            return pagerList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(pagerList.get(position));
        }
    }


    @Override
    protected void onDestroy() {
        defaultPoiBitmap.recycle();
        super.onDestroy();

    }

    class HttpInteract {
        public void borrowCard() {

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("borrow_id", IShareContext.getInstance().getCurrentUser().getUserId()));
            params.add(new BasicNameValuePair("lend_id", cardItem.ownerId));
            params.add(new BasicNameValuePair("card_id", cardItem.id + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.BORROW_CARD, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(result);

                        if (jsonObject.getInt("status") == 0) {
                            Toast.makeText(CardDetailActivity.this, "已发送借卡请求", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CardDetailActivity.this, "借卡失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Toast.makeText(CardDetailActivity.this, "借卡失败，请重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }
    }


}
