package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.publishware.PublishItemActivity;
import com.galaxy.ishare.sharedcard.CardDetailActivity;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.galaxy.ishare.utils.WidgetController;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardIShareAdapter extends BaseAdapter {

    private static final String TAG = "CardIShareAdapter";
    private List<CardItem> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public CardIShareAdapter(List<CardItem> data, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        CardHolder cardHolder = null;

        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.i_share_listview_item, null);
            cardHolder = new CardHolder();
            cardHolder.cardIv = (ImageView) convertView.findViewById(R.id.i_share_item_listview_item_pic_iv);
            cardHolder.shopNameTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_shopname_tv);
            cardHolder.tradeTypeTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_tradetype_tv);
            cardHolder.cardTypeTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_cardtype_tv);
            cardHolder.discountTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_discount_tv);
            cardHolder.shopDistanceTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_shop_distance_tv);
            cardHolder.ratingLayout = (LinearLayout) convertView.findViewById(R.id.i_share_item_listview_item_rating_layout);
            cardHolder.rentCountTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_rent_count_tv);
            cardHolder.commentCountTv = (TextView) convertView.findViewById(R.id.i_share_item_listview_item_comment_count_tv);
            cardHolder.editLayout = (LinearLayout) convertView.findViewById(R.id.i_share_item_listview_item_edit_layout);
            cardHolder.deleteLayout = (LinearLayout) convertView.findViewById(R.id.i_share_item_listview_item_delete_layout);

            convertView.setTag(cardHolder);
        } else {
            cardHolder = (CardHolder) convertView.getTag();
        }
        final CardItem cardItem = dataList.get(position);
        cardHolder.shopNameTv.setText(cardItem.shopName);
        String[] tradeItems = mContext.getResources().getStringArray(R.array.trade_items);
        cardHolder.tradeTypeTv.setText(tradeItems[cardItem.tradeType]);


        String[] cardItems = mContext.getResources().getStringArray(R.array.card_items);
        cardHolder.cardTypeTv.setText(cardItems[cardItem.wareType]);
        cardHolder.discountTv.setText(cardItem.getStringDiscount());
        cardHolder.shopDistanceTv.setText(cardItem.shopDistance + "km");
        cardHolder.rentCountTv.setText(cardItem.rentCount + "");
        cardHolder.commentCountTv.setText(cardItem.commentCount + "");


        int[] colors = {R.color.main_blue, R.color.main_green, R.color.main_orange, R.color.main_purple, R.color.main_red};
        if (cardItem.tradeType >= 1)
            cardHolder.tradeTypeTv.setTextColor(mContext.getResources().getColor(colors[cardItem.tradeType - 1]));
        int[] backgroundRes = {R.drawable.main_trade_tag_blue, R.drawable.main_trade_tag_green, R.drawable.main_trade_tag_orange,
                R.drawable.main_trade_tag_purple, R.drawable.main_trade_tag_red};
        if (cardItem.tradeType >= 1)
            cardHolder.tradeTypeTv.setBackgroundResource(backgroundRes[cardItem.tradeType - 1]);



        WidgetController.getInstance().setRatingLayout(cardItem.ratingCount, mContext, cardHolder.ratingLayout);


        if (cardItem.cardImgs != null && cardItem.cardImgs.length > 0) {

            final String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(cardItem.cardImgs[0], DisplayUtil.dip2px(mContext, 92), DisplayUtil.dip2px(mContext, 69));
//            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));
            final CardHolder finalCardHolder = cardHolder;
            finalCardHolder.cardIv.setTag(thumbnailUrl);

            ImageLoader.getInstance().displayImage(thumbnailUrl, finalCardHolder.cardIv);

            Log.v(TAG, thumbnailUrl + "---thumbnail");
//          ImageLoader.getInstance().displayImage(thumbnailUrl, finalCardHolder.cardIv, new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//
//                    if (((String) view.getTag()).equals(thumbnailUrl)) {
//                        ((ImageView) view).setImageBitmap(loadedImage);
//                    }
//
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                }
//            });

        }


        cardHolder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PublishItemActivity.class);
                intent.putExtra(PublishItemActivity.PARAMETER_WHO_COME, CardIshareActivity.ISHARE_TO_PUBLISH);
                intent.putExtra(PublishItemActivity.PARAMETER_ISHARE_CARD_ITEM, dataList.get(position));
                mContext.startActivity(intent);
            }
        });

        cardHolder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("card_id", cardItem.id + ""));
                HttpTask.startAsyncDataPostRequest(URLConstant.DELETE_SHARE_CARD, params, new HttpDataResponse() {
                    @Override
                    public void onRecvOK(HttpRequestBase request, String result) {
                        Log.e(TAG, result);
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                        dataList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                        Toast.makeText(mContext, "删除失败，请重试", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRecvCancelled(HttpRequestBase request) {

                    }

                    @Override
                    public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                    }
                });
            }
        });

        return convertView;
    }

    class CardHolder {
        public ImageView cardIv;
        public TextView shopNameTv;
        public TextView tradeTypeTv;
        public TextView cardTypeTv;
        public TextView discountTv;
        public TextView shopDistanceTv;
        public LinearLayout ratingLayout;
        public TextView rentCountTv;
        public TextView commentCountTv;
        public LinearLayout editLayout;
        public LinearLayout deleteLayout;
    }
}
