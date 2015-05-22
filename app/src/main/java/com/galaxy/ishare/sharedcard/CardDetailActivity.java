package com.galaxy.ishare.sharedcard;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/21.
 */
public class CardDetailActivity extends ActionBarActivity {

    public static final String PARAMETER_CARD_ITEM="PARAMETER_CARD_ITEM";

    private FButton  borrowBtn, talkBtn;
    private ViewPager cardPager;
    private TextView shopNameTv,discountTv,ownerNameTv,tradeTypeTv,shopAddrTv,shopDistanceTv,cardTypeTv,
                     ownerAddrTv,ownerDistanceTv,descriptionTv,statusTv;
    private MapView cardMapView;

    private CardItem cardItem;
    //一共有几张图片,即viewpager有几个view
    private int picNumber;
    // viewpager 中的ImageView
    private ImageView[] picIvs;

    private ArrayList<View>pagerList;

    private BaiduMap baiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_item_card_detail_activity);

        cardItem = getIntent().getParcelableExtra(PARAMETER_CARD_ITEM);
        picNumber = cardItem.cardImgs.length;

        picIvs  = new ImageView[picNumber];
        pagerList = new ArrayList<>();

        initViews ();

        initCardPager();

        writeValueIntoViews();

        initMapView();

        // 地图点击进入新的界面，展示三方位置
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });


    }
    private void initMapView(){

        baiduMap= cardMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        baiduMap.setMapStatus(msu);

        // 地图的中点显示店的位置
        float zoomLevel= 10;// 3-20
        MapStatusUpdate statusUpdate=MapStatusUpdateFactory.newLatLngZoom(new LatLng(cardItem.shopLatitude, cardItem.shopLongitude), zoomLevel);
        baiduMap.setMapStatus(statusUpdate);

    }
    private void writeValueIntoViews(){
        shopNameTv.setText(cardItem.shopName);
        discountTv.setText(cardItem.discount+"");
        ownerNameTv.setText(cardItem.ownerName);
        String [] trades = getResources().getStringArray(R.array.trade_items);
        tradeTypeTv.setText(trades[cardItem.tradeType]);
        shopAddrTv.setText(cardItem.shopLocation);
        shopDistanceTv.setText(cardItem.shopDistance+"");
        String [] cardTypes =getResources().getStringArray(R.array.card_items);
        cardTypeTv.setText(cardTypes[cardItem.wareType]);
        ownerAddrTv.setText(cardItem.ownerLocation);
        ownerDistanceTv.setText(cardItem.ownerDistance+"");
        descriptionTv.setText(cardItem.description);
        statusTv.setText(cardItem.cardStatus);


    }

    private void initViews(){

        borrowBtn = (FButton)findViewById(R.id.share_item_detail_borrow_btn);
        talkBtn = (FButton)findViewById(R.id.share_item_detail_talk_btn);

        cardPager = (ViewPager)findViewById(R.id.share_item_detail_viewpager);

        shopNameTv= (TextView)findViewById(R.id.share_item_detail_shop_name_tv);
        discountTv=(TextView)findViewById(R.id.share_item_detail_discount_tv);
        ownerNameTv = (TextView)findViewById(R.id.share_item_detail_owner_name_tv);
        tradeTypeTv = (TextView)findViewById(R.id.share_item_detail_trade_type_tv);
        shopAddrTv = (TextView)findViewById(R.id.share_item_detail_shop_location_tv);
        shopDistanceTv = (TextView)findViewById(R.id.share_item_detail_shop_distance_tv);
        cardTypeTv  = (TextView)findViewById(R.id.share_item_detail_card_type_tv);
        ownerAddrTv = (TextView)findViewById(R.id.share_item_detail_owner_location_tv);
        ownerDistanceTv = (TextView)findViewById(R.id.share_item_detail_owner_distance_tv);
        descriptionTv = (TextView)findViewById(R.id.share_item_detail_card_description_tv);
        statusTv = (TextView)findViewById(R.id.share_item_detail_card_status_tv);

        cardMapView = (MapView)findViewById(R.id.share_item_detail_mapview);


    }
    private void initCardPager(){
        LayoutInflater inflater = getLayoutInflater();
        for (int i=0;i<picNumber;i++){
            View view = inflater.inflate(R.layout.share_item_detail_viewpager,null);
            picIvs[i]=(ImageView)view.findViewById(R.id.share_item_detail_card_pager_iv);
            ImageLoader.getInstance().displayImage(cardItem.cardImgs[i],picIvs[i]);
            pagerList.add(view);
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter();
        cardPager.setAdapter(pagerAdapter);




    }

    public class MyPagerAdapter extends android.support.v4.view.PagerAdapter{

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
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


}
