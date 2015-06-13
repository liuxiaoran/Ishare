package com.galaxy.ishare.sharedcard;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.galaxy.ishare.utils.WidgetController;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardListItemAdapter extends BaseAdapter {

    private static final String TAG = "cardlistadapter";
    private Vector<CardItem> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardListItemAdapter(Vector<CardItem> data, Context context) {
        this.dataList = data;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

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
        CardHolder cardHolder = null;
        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.share_item_listview_item, null);
            cardHolder = new CardHolder();
            cardHolder.cardIv = (ImageView) convertView.findViewById(R.id.share_item_listview_item_pic_iv);
            cardHolder.shopNameTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_shopname_tv);
            cardHolder.tradeTypeTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_tradetype_tv);
            cardHolder.cardTypeTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_cardtype_tv);
            cardHolder.discountTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_discount_tv);
            cardHolder.ownerDistanceTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_owner_distance_tv);
            cardHolder.shopDistanceTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_shop_distance_tv);
            cardHolder.ratingLayout = (LinearLayout) convertView.findViewById(R.id.share_item_listview_item_rating_layout);
            cardHolder.rentCountTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_rent_count_tv);
            cardHolder.commentCountTv = (TextView) convertView.findViewById(R.id.share_item_listview_item_comment_count_tv);

            convertView.setTag(cardHolder);


        } else {
            cardHolder = (CardHolder) convertView.getTag();
        }
        CardItem cardItem = dataList.get(position);
        cardHolder.shopNameTv.setText(cardItem.shopName);
        String[] tradeItems = mContext.getResources().getStringArray(R.array.trade_items);
        cardHolder.tradeTypeTv.setText(tradeItems[cardItem.tradeType]);


        String[] cardItems = mContext.getResources().getStringArray(R.array.card_items);
        cardHolder.cardTypeTv.setText(cardItems[cardItem.wareType]);
        cardHolder.discountTv.setText(cardItem.getStringDiscount());
        cardHolder.shopDistanceTv.setText(cardItem.shopDistance + "");
        cardHolder.ownerDistanceTv.setText(cardItem.ownerDistance + "");
        cardHolder.rentCountTv.setText(cardItem.rentCount + "");
        cardHolder.commentCountTv.setText(cardItem.commentCount + "");


        int[] colors = {R.color.main_blue, R.color.main_green, R.color.main_orange, R.color.main_purple, R.color.main_yellow, R.color.main_red};
        cardHolder.tradeTypeTv.setTextColor(mContext.getResources().getColor(colors[cardItem.tradeType]));
        int[] backgroundRes = {R.drawable.main_trade_tv_blue, R.drawable.main_trade_tv_green, R.drawable.main_trade_tv_orange,
                R.drawable.main_trade_tv_purple, R.drawable.main_trade_tv_yellow, R.drawable.main_trade_tv_red};
        cardHolder.tradeTypeTv.setBackgroundResource(backgroundRes[cardItem.tradeType]);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        WidgetController.getInstance().setRatingLayout(cardItem.ratingCount, mContext, cardHolder.ratingLayout);



        if (cardItem.cardImgs != null && cardItem.cardImgs.length > 0) {

            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(cardItem.cardImgs[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));
            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));
            final CardHolder finalCardHolder = cardHolder;

            ImageLoader.getInstance().displayImage(thumbnailUrl, finalCardHolder.cardIv);

        }

        return convertView;
    }

    class CardHolder {
        public ImageView cardIv;
        public TextView shopNameTv;
        public TextView tradeTypeTv;
        public TextView cardTypeTv;
        public TextView discountTv;
        public TextView shopDistanceTv;
        public TextView ownerDistanceTv;
        public LinearLayout ratingLayout;
        public TextView rentCountTv;
        public TextView commentCountTv;
    }
}
