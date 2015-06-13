package com.galaxy.ishare.user_request;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liuxiaoran on 15/6/3.
 */
public class RequestListAdapter extends BaseAdapter {

    public static final String TAG = "requestListAdapter";

    private Context mContext;
    private Vector<CardItem> dataList;
    private LayoutInflater inflater;

    private int currentMonth, currentDay, currentHour, currentMinute;

    public RequestListAdapter(Context context, Vector dataList) {
        mContext = context;
        this.dataList = dataList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


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

        RequestItemHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.request_listview_item, null);
            holder = new RequestItemHolder();
            holder.avatarIv = (CircleImageView) convertView.findViewById(R.id.request_item_requester_avatar_iv);
            holder.requesterNameTv = (TextView) convertView.findViewById(R.id.request_item_requester_name_tv);
            holder.genderIv = (ImageView) convertView.findViewById(R.id.request_item_requester_gender_iv);
            holder.requesterDistanceTv = (TextView) convertView.findViewById(R.id.request_item_distance_tv);
            holder.timeTv = (TextView) convertView.findViewById(R.id.request_item_time_tv);
            holder.shopNameTv = (TextView) convertView.findViewById(R.id.request_item_card_shopname_tv);
            holder.shopLocationTv = (TextView) convertView.findViewById(R.id.request_item_card_shop_location_tv);
            holder.shopDistanceTv = (TextView) convertView.findViewById(R.id.request_item_shop_distance_tv);
            convertView.setTag(holder);


        } else {
            holder = (RequestItemHolder) convertView.getTag();
        }

        CardItem cardItem = dataList.get(position);
        holder.requesterNameTv.setText(cardItem.ownerName);

        ImageLoader.getInstance().displayImage(QiniuUtil.getInstance().getFileThumbnailUrl(cardItem.getOwnerAvatar(), DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 60)),
                holder.avatarIv);


        if ("男".equals(cardItem.ownerGender)) {
            holder.genderIv.setImageResource(R.drawable.icon_male);
        } else {
            holder.genderIv.setImageResource(R.drawable.icon_female);
        }
        holder.requesterDistanceTv.setText(cardItem.ownerDistance + " km");


        setCurrentTime();
        // 显示过去了多长时间
        Log.v(TAG, cardItem.getShopName() + "   " + cardItem.getPublishTime());

        holder.timeTv.setText(getPresentPassTime(cardItem.getPublishTime()));

        holder.shopNameTv.setText(cardItem.getShopName());
        holder.shopLocationTv.setText(cardItem.getShopLocation());
        holder.shopDistanceTv.setText(cardItem.shopDistance + "");

        return convertView;
    }

    class RequestItemHolder {
        public CircleImageView avatarIv;
        public TextView requesterNameTv;
        public ImageView genderIv;
        public TextView requesterDistanceTv;
        public TextView timeTv;
        public TextView shopNameTv;
        public TextView shopLocationTv;
        public TextView shopDistanceTv;

    }

    private void setCurrentTime() {

        Calendar nowCalendar = Calendar.getInstance();
        currentMonth = nowCalendar.get(Calendar.MONTH) + 1;
        currentDay = nowCalendar.get(Calendar.DAY_OF_MONTH);
        currentHour = nowCalendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = nowCalendar.get(Calendar.MINUTE);


    }

    private String getPresentPassTime(String publishTime) {

        String ret = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟

        Date publishDate = null;
        try {
            publishDate = sdf.parse(publishTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int publishMonth = publishDate.getMonth() + 1;
        int publishDay = publishDate.getDate();
        int publishHour = publishDate.getHours();
        int publishMinute = publishDate.getMinutes();
        Log.v(TAG, "publishMonth: " + publishMonth + "  publishDay:  " + publishDay + "  publishHour: " + publishHour + "  publishMinute:  " + publishMinute);
        if (publishMonth < currentMonth) {
            ret = (currentMonth - publishMonth) + "月前";
        } else if (publishMonth == currentMonth) {
            if (publishDay < currentDay) {
                ret = (currentDay - publishDay) + "天前";
            } else if (publishDay == currentDay) {
                if (publishHour < currentHour) {
                    ret = (currentHour - publishHour) + "小时前";
                } else if (publishHour == currentHour) {
                    if (publishMinute == currentMinute) {
                        ret = "刚刚";
                    } else {
                        ret = (currentMinute - publishMinute) + "分钟前";
                    }
                }
            }

        }

        return ret;
    }


}
