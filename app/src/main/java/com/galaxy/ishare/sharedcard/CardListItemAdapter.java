package com.galaxy.ishare.sharedcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardListItemAdapter extends BaseAdapter {

    private static final String TAG = "cardlistadapter";
    private LinkedList<CardItem> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardListItemAdapter(LinkedList<CardItem> data, Context context) {
        this.dataList = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public void setDataAndRefresh(LinkedList<CardItem> data) {
        dataList = data;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
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
        cardHolder.shopDistanceTv.setText(cardItem.shopDistance + "");
        cardHolder.ownerDistanceTv.setText(cardItem.ownerDistance + "");

        if (cardItem.cardImgs != null && cardItem.cardImgs.length > 0) {

            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(cardItem.cardImgs[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            Log.v(TAG, "arrive" + "   " + thumbnailUrl);
            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            final CardHolder finalCardHolder = cardHolder;
            ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    finalCardHolder.cardIv.setImageBitmap(loadedImage);
                }
            });
        }

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
