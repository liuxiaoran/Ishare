package com.galaxy.ishare.order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.DateUtil;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.galaxy.ishare.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

public class OrderAdapter extends BaseAdapter {

    private String TAG = "OrderAdapter";

    private List<Order> dataList;
    private LayoutInflater layoutInflater;
    private Context mContext;
    private User user;
    private String[] borrowStateItems;
    private String[] lendStateItems;

    public OrderAdapter(Context context, List<Order> dataList) {
        this.mContext = context;
        this.dataList = dataList;
        user = IShareContext.getInstance().getCurrentUser();
        this.layoutInflater = LayoutInflater.from(context);
        borrowStateItems = mContext.getResources().getStringArray(R.array.borrow_state_items);
        lendStateItems = mContext.getResources().getStringArray(R.array.lend_state_items);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public class ViewHolder {
        public ImageView orderAvatarIv;
        public TextView orderNameTv;
        public ImageView orderGenderIv;
        public TextView orderDistanceTv;
        public TextView lastChatTimeTv;
        public TextView orderTypeTv;
        public ImageView isReadIv;
        public TextView lastChatTv;
        public ImageView shopImageIv;
        public TextView shopNameTv;
        public TextView shopLocationTv;
        public TextView orderStateTv;
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
            convertView = layoutInflater.inflate(R.layout.item_new_order, null);
            viewHolder.orderAvatarIv = (ImageView) convertView.findViewById(R.id.order_avatar_iv);
            viewHolder.orderNameTv = (TextView) convertView.findViewById(R.id.order_name_tv);
            viewHolder.orderGenderIv = (ImageView) convertView.findViewById(R.id.order_gender_iv);
            viewHolder.orderDistanceTv = (TextView) convertView.findViewById(R.id.order_distance_tv);
            viewHolder.lastChatTimeTv = (TextView) convertView.findViewById(R.id.last_chat_time_tv);
            viewHolder.orderTypeTv = (TextView) convertView.findViewById(R.id.order_type_tv);
            viewHolder.lastChatTv = (TextView) convertView.findViewById(R.id.last_chat_tv);
            viewHolder.shopImageIv = (ImageView) convertView.findViewById(R.id.shop_image_iv);
            viewHolder.shopNameTv = (TextView) convertView.findViewById(R.id.shop_name_tv);
            viewHolder.shopLocationTv = (TextView) convertView.findViewById(R.id.shop_location_tv);
            viewHolder.orderStateTv = (TextView) convertView.findViewById(R.id.order_state_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (user.getUserId().equals(order.borrowId)) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.lendAvatar, DisplayUtil.dip2px(mContext, 50), DisplayUtil.dip2px(mContext, 50));
            ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.orderAvatarIv);
            viewHolder.orderNameTv.setText(order.lendName + "");
            if("男".equals(order.lendGender)) {
                viewHolder.orderGenderIv.setImageResource(R.drawable.icon_male);
            } else {
                viewHolder.orderGenderIv.setImageResource(R.drawable.icon_female);
            }
            if(order.orderState == 0) {
                viewHolder.orderStateTv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.orderStateTv.setText(borrowStateItems[order.orderState]);
                viewHolder.orderStateTv.setVisibility(View.VISIBLE);
            }

            viewHolder.orderTypeTv.setBackgroundResource(R.drawable.borrow_bkg);
            viewHolder.orderTypeTv.setText(mContext.getResources().getString(R.string.borrow_label));
        } else {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.borrowAvatar, DisplayUtil.dip2px(mContext, 50), DisplayUtil.dip2px(mContext, 50));
            ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.orderAvatarIv);
            viewHolder.orderNameTv.setText(order.borrowName + "");
            if("男".equals(order.borrowGender)) {
                viewHolder.orderGenderIv.setImageResource(R.drawable.icon_male);
            } else {
                viewHolder.orderGenderIv.setImageResource(R.drawable.icon_female);
            }
            if(order.orderState == 0) {
                viewHolder.orderStateTv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.orderStateTv.setText(lendStateItems[order.orderState]);
                viewHolder.orderStateTv.setVisibility(View.VISIBLE);
            }
            viewHolder.orderTypeTv.setBackgroundResource(R.drawable.lend_bkg);
            viewHolder.orderTypeTv.setText(mContext.getResources().getString(R.string.lend_label));
        }

        viewHolder.orderDistanceTv.setText(order.lendDistance + "km");
        viewHolder.lastChatTv.setText(order.lastChatContent + "");
        viewHolder.lastChatTimeTv.setText(TimeUtils.getPresentPassTime(order.lastChatTime));

        if (order.shopImage != null && order.shopImage.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 100));
            ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.shopImageIv);
        } else {
            viewHolder.shopImageIv.setImageResource(R.drawable.load_empty);
        }
        viewHolder.shopNameTv.setText(order.shopName);
        viewHolder.shopLocationTv.setText(order.shopLocation);

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
        Log.d(TAG, order.orderState + "");
        switch(order.orderState) {
            case 2: result = order.lendTime; break;
            case 3: result = order.returnTime; break;
            case 4: result = order.payTime; break;
            case 5: result = order.confirmTime; break;
        }
        return result;
    }
}

