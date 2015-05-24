package com.galaxy.ishare.sharedcard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardListItemAdapter extends BaseAdapter {

    private ArrayList<CardItem> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardListItemAdapter(ArrayList<CardItem> data, Context context) {
        this.dataList = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public void setData(ArrayList data){
        dataList=data;

    }

    @Override
    public int getCount() {
        Log.v("ItemListFragment","cardListItemAdapter: "+dataList.size()+"data size");
        return dataList.size();
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

        CardHolder cardHolder=null;
        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.share_item_listview_item,null);
            cardHolder = new CardHolder();
            cardHolder.cardIv = (ImageView) convertView.findViewById(R.id.share_item_listview_item_pic_iv);
            cardHolder.shopNameTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_shopname_tv);
            cardHolder.tradeTypeTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_tradetype_tv);
            cardHolder.cardTypeTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_cardtype_tv);
            cardHolder.discountTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_discount_tv);
            cardHolder.shopLocationTv = (TextView)convertView.findViewById(R.id.share_item_listview_item_shop_location_tv);
            cardHolder.shopDistanceTv= (TextView)convertView.findViewById(R.id.share_item_listview_item_shop_distance_tv);
            cardHolder.ownerDistanceTv = (TextView)convertView.findViewById(R.id.share_item_listview_item_owner_distance_tv);
            cardHolder.shopDistanceTv = (TextView)convertView.findViewById(R.id.share_item_listview_item_shop_distance_tv);
            convertView.setTag(cardHolder);



        }else {
            cardHolder = (CardHolder) convertView.getTag();
        }
        CardItem cardItem = dataList.get(position);
        cardHolder.shopNameTv.setText(cardItem.shopName);
        String [] tradeItems =mContext.getResources().getStringArray(R.array.trade_items);
        cardHolder.tradeTypeTv.setText(tradeItems[cardItem.tradeType]);
        String [] cardItems = mContext.getResources().getStringArray(R.array.card_items);
        cardHolder.cardTypeTv.setText(cardItems[cardItem.wareType]);
        cardHolder.discountTv.setText(cardItem.discount+"");
        cardHolder.shopLocationTv.setText(cardItem.shopLocation);
        cardHolder.shopDistanceTv.setText(cardItem.shopDistance+"");
        cardHolder.ownerDistanceTv.setText(cardItem.ownerDistance+"");

        return convertView;
    }

    class CardHolder {
        public ImageView cardIv;
        public TextView shopNameTv;
        public TextView tradeTypeTv;
        public TextView cardTypeTv;
        public TextView discountTv;
        public TextView shopLocationTv;
        public TextView shopDistanceTv;
        public TextView ownerDistanceTv;
    }
}
