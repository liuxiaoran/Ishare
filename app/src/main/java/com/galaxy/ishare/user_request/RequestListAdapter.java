package com.galaxy.ishare.user_request;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.galaxy.ishare.utils.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liuxiaoran on 15/6/3.
 */
public class RequestListAdapter extends BaseAdapter {

    public static final String TAG = "requestListAdapter";

    private Context mContext;
    private Vector<CardRequest> dataList;
    private LayoutInflater inflater;


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
    public View getView(final int position, View convertView, ViewGroup parent) {

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
            holder.rippleView = (RippleView) convertView.findViewById(R.id.request_item_ripple_view);
            convertView.setTag(holder);


        } else {
            holder = (RequestItemHolder) convertView.getTag();
        }

        CardRequest cardRequest = dataList.get(position);
        holder.requesterNameTv.setText(cardRequest.requesterName);

        ImageLoader.getInstance().displayImage(QiniuUtil.getInstance().getFileThumbnailUrl(cardRequest.requesterAvatar, DisplayUtil.dip2px(mContext, 60), DisplayUtil.dip2px(mContext, 60)),
                holder.avatarIv);


        if ("男".equals(cardRequest.requesterGender)) {
            holder.genderIv.setImageResource(R.drawable.icon_male);
        } else {
            holder.genderIv.setImageResource(R.drawable.icon_female);
        }
        holder.requesterDistanceTv.setText(cardRequest.requesterDistance + " km");


        if (cardRequest.publishTime != null)
            holder.timeTv.setText(TimeUtils.getPresentPassTime(cardRequest.publishTime));


        holder.shopNameTv.setText(cardRequest.shopName);
        holder.shopLocationTv.setText(cardRequest.shopLocation);
        holder.shopDistanceTv.setText(cardRequest.shopDistance + "");

        holder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Intent intent = new Intent(mContext, RequestDetailActivity.class);
                intent.putExtra(RequestDetailActivity.PARAMETER_REQUEST, dataList.get(position));
                mContext.startActivity(intent);
            }
        });

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
        public RippleView rippleView;
    }


}
