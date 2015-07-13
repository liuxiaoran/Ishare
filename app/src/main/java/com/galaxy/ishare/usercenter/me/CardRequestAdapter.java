package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by Zhan on 2015/6/16.
 */
public class CardRequestAdapter extends BaseAdapter {
    private static String TAG = "CardRequestAdapter";
    private List<CardRequest> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String[] cardItems;
    private String[] statusItems;

    public CardRequestAdapter(List<CardRequest> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
        cardItems = mContext.getResources().getStringArray(R.array.card_items);
        statusItems = mContext.getResources().getStringArray(R.array.card_items);
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_card_request, null);
            viewHolder = new ViewHolder();
            viewHolder.shopImageIv = (ImageView) convertView.findViewById(R.id.shop_image);
            viewHolder.shopNameTv = (TextView) convertView.findViewById(R.id.shop_name);
            viewHolder.wareTypeTv = (TextView) convertView.findViewById(R.id.ware_type);
            viewHolder.replyNumTv = (TextView) convertView.findViewById(R.id.reply_num);
            viewHolder.statusTv = (TextView) convertView.findViewById(R.id.request_status);
            viewHolder.isReadIv = (ImageView) convertView.findViewById(R.id.is_read);
            viewHolder.lastReplyTv = (TextView) convertView.findViewById(R.id.last_reply);
            viewHolder.lastTimeTv = (TextView) convertView.findViewById(R.id.last_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CardRequest cardRequest = dataList.get(position);
        final ViewHolder finalCardHolder = viewHolder;
        if (cardRequest.shopImage != null && cardRequest.shopImage.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(cardRequest.shopImage[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));
            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));

            ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    finalCardHolder.shopImageIv.setImageBitmap(loadedImage);
                }
            });
        } else {
            finalCardHolder.shopImageIv.setImageResource(R.drawable.load_empty);
        }

        viewHolder.shopNameTv.setText(cardRequest.shopName);
        viewHolder.wareTypeTv.setText(cardItems[cardRequest.wareType]);
        viewHolder.replyNumTv.setText(cardRequest.replyNum + "");
        viewHolder.statusTv.setText(statusItems[cardRequest.status]);

        if(cardRequest.isRead == 1) {
            viewHolder.isReadIv.setVisibility(View.GONE);
        } else {
            viewHolder.isReadIv.setVisibility(View.VISIBLE);
        }

        viewHolder.lastReplyTv.setText(cardRequest.lastReply);
        viewHolder.lastTimeTv.setText(cardRequest.lastTime);

        return convertView;
    }

    class ViewHolder {
        public ImageView shopImageIv;
        public TextView shopNameTv;
        public TextView wareTypeTv;
        public TextView replyNumTv;
        public TextView statusTv;
        public ImageView isReadIv;
        public TextView lastReplyTv;
        public TextView lastTimeTv;
    }
}
