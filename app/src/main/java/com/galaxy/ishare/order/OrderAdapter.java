package com.galaxy.ishare.order;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.DateUtil;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    private String TAG = "OrderAdapter";

    private List<Order> dataList;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private User user;
    private String[] cardItems;
    private String[] tradeItems;
    private String[] borrowStateItems;
    private String[] borrowLastStateBegin;
    private String[] borrowLastStateEnd;
    private String[] lendStateItems;
    private String[] lendLastStateBegin;
    private String[] lendLastStateEnd;
    private int[] colors = {R.color.main_blue, R.color.main_green, R.color.main_orange, R.color.main_purple, R.color.main_yellow, R.color.main_red};
    private int[] backgroundRes = {R.drawable.main_trade_tv_blue, R.drawable.main_trade_tv_green, R.drawable.main_trade_tv_orange,
            R.drawable.main_trade_tv_purple, R.drawable.main_trade_tv_yellow, R.drawable.main_trade_tv_red};

    public OrderAdapter(Context context, List<Order> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        user = IShareContext.getInstance().getCurrentUser();
        this.layoutInflater = LayoutInflater.from(context);
        cardItems = mContext.getResources().getStringArray(R.array.card_items);
        tradeItems = mContext.getResources().getStringArray(R.array.trade_items);
        borrowStateItems = mContext.getResources().getStringArray(R.array.borrow_state_items);
        borrowLastStateBegin = mContext.getResources().getStringArray(R.array.borrow_last_state_begin);
        borrowLastStateEnd = mContext.getResources().getStringArray(R.array.borrow_last_state_end);
        lendStateItems = mContext.getResources().getStringArray(R.array.lend_state_items);
        lendLastStateBegin = mContext.getResources().getStringArray(R.array.lend_last_state_begin);
        lendLastStateEnd = mContext.getResources().getStringArray(R.array.lend_last_state_end);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public class ViewHolder {
        public TextView shopName;
        public ImageView shopImage;
        public TextView cardDiscount;
        public TextView cardType;
//        public TextView cardTag;
        public TextView orderState;
        public ImageView isRead;
        public TextView orderLastState;
        public TextView orderLastTime;
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
            convertView = layoutInflater.inflate(R.layout.item_order, null);
            viewHolder.shopName = (TextView) convertView.findViewById(R.id.shop_name);
            viewHolder.shopImage = (ImageView) convertView.findViewById(R.id.shop_image);
            viewHolder.cardDiscount = (TextView) convertView.findViewById(R.id.card_discount);
            viewHolder.cardType = (TextView) convertView.findViewById(R.id.card_type);
//            viewHolder.cardTag = (TextView) convertView.findViewById(R.id.card_tag);
            viewHolder.orderState = (TextView) convertView.findViewById(R.id.order_state);
            viewHolder.isRead = (ImageView) convertView.findViewById(R.id.is_read);
            viewHolder.orderLastState = (TextView) convertView.findViewById(R.id.order_last_state);
            viewHolder.orderLastTime = (TextView) convertView.findViewById(R.id.order_last_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.shopName.setText(order.shopName + "");

        if (order.shopImage != null && order.shopImage.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 100), DisplayUtil.dip2px(mContext, 80));
            final ViewHolder finalHolder = viewHolder;
            ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    finalHolder.shopImage.setImageBitmap(loadedImage);
                }
            });
        }

        viewHolder.cardDiscount.setText(getStringDiscount(order.cardDiscount) + "折");
        viewHolder.cardType.setText(cardItems[order.cardType]);
//        viewHolder.cardTag.setText(tradeItems[order.tradeType]);
//        viewHolder.cardTag.setTextColor(mContext.getResources().getColor(colors[order.tradeType]));
//        viewHolder.cardTag.setBackgroundResource(backgroundRes[order.tradeType]);

        Log.d(TAG, (order == null) + "");
        Log.d(TAG, (order.borrowId == null) + "");
        Log.d(TAG, (user == null) + "");
        if(order.borrowId.equals(user.getUserId())) {
            viewHolder.orderState.setText(borrowStateItems[order.orderState]);
            viewHolder.orderLastState.setText(borrowLastStateBegin[order.orderState] + order.lendName + borrowLastStateEnd[order.orderState]);
        } else {
            viewHolder.orderState.setText(lendStateItems[order.orderState]);
            viewHolder.orderLastState.setText(lendLastStateBegin[order.orderState] + order.borrowName + lendLastStateEnd[order.orderState]);
        }
        viewHolder.orderLastTime.setText(getShowTimeFormat(getShowTime(order)));

        return convertView;
    }

    // 得到折扣的字符串标示，整数值不显示小数
    public String getStringDiscount(double discount) {
        double d = discount * 10;
        if (d % 10 == 0.0) {
            int ret = (int) discount;
            return Integer.toString(ret);
        }
        return Double.toString(discount);
    }

    public String getShowTime(Order order) {
        String result = null;
        switch(order.orderState) {
            case 2: result = order.lendTime; break;
            case 3: result = order.returnTime; break;
            case 4: result = order.payTime; break;
            case 5: result = order.confirmTime; break;
            default: result = "服务器数据错误"; break;
        }
        return result;
    }

    public String getShowTimeFormat(String time) {
        Log.d(TAG, "time : " + time);
        String result = null;
        if(time != null && !"null".equals(time)) {
            Long curTime = DateUtil.getTimeStamp() / 1000;
            Long stateTime = DateUtil.date2TimeStamp(time, "yyyy-MM-dd hh:mm:ss") / 1000;
            Long timeDif = curTime - stateTime;
            if(timeDif < 10 * 60 ) {
                result = timeDif + "分钟前";
            } else if(timeDif < 60 * 60 * 24){
                result = time.substring(11, time.length());
            } else if(timeDif < 2 * 60 * 60 * 24) {
                result = "昨天";
            } else {
                result = time.substring(2, 10);
            }
        }
        return result;
    }
}

