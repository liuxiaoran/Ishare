package com.galaxy.ishare.order;

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
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by YangJunLin on 2015/5/26.
 */
public class OrderAdapter extends BaseAdapter {

    private String TAG = "OrderAdapter";

    private List<Order> dataList;
    private LayoutInflater layoutInflater;
    private Context mContext;

    public OrderAdapter(Context context, List<Order> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public class ViewHolder {
        public TextView shopName;
        public TextView shopDistance;
        public ImageView shopImage;
        public TextView cardDiscount;
        public TextView cardType;
        public ImageView cardTag;
        public TextView cardDistance;
        public TextView cardState;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Order order = dataList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.item_state_card, null);
            viewHolder.shopName = (TextView) convertView.findViewById(R.id.shop_name);
            viewHolder.shopDistance = (TextView) convertView.findViewById(R.id.shop_distance);
            viewHolder.shopImage = (ImageView) convertView.findViewById(R.id.shop_image);
            viewHolder.cardDiscount = (TextView) convertView.findViewById(R.id.card_discount);
            viewHolder.cardType = (TextView) convertView.findViewById(R.id.card_type);
            viewHolder.cardTag = (ImageView) convertView.findViewById(R.id.card_tag);
            viewHolder.cardDistance = (TextView) convertView.findViewById(R.id.card_distance);
            viewHolder.cardState = (TextView) convertView.findViewById(R.id.card_state);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.shopName.setText(order.shopName + "");
        viewHolder.shopDistance.setText(order.shopDistance + "");

        if (order.shopImage != null) {

            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage, DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            Log.v(TAG, "arrive" + "   " + thumbnailUrl);
            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            final ViewHolder finalCardHolder = viewHolder;
            ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions,
                    new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    finalCardHolder.shopImage.setImageBitmap(loadedImage);
                }
            });
        }

        viewHolder.cardDiscount.setText(order.cardDiscount + "");
        setCardType(viewHolder.cardType, order.cardType);
        setCardTag(viewHolder.cardTag, order.tradeType);
        viewHolder.cardDistance.setText(order.borrowDistance + "");
        viewHolder.cardState.setText(order.orderState + "");

        return convertView;
    }

    public void setCardTag(ImageView image, int tradeType) {
        switch (tradeType) {
            case 0:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
            case 1:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
            case 2:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
            case 3:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
            case 4:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
            case 5:image.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha); break;
        }
    }

    public void setCardType(TextView text, int cardType) {
        switch (cardType) {
            case 0:text.setText(R.string.vip_card); break;
            case 1:text.setText(R.string.charge_card); break;
        }
    }

}

