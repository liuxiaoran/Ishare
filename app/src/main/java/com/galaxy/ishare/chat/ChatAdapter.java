package com.galaxy.ishare.chat;

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
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.DateUtil;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Zhan on 2015/5/19.
 */
public class ChatAdapter extends BaseAdapter {
    private static String TAG = "ChatAdapter";

    private Context mContext;
    private List<Chat> chatList;
    private LayoutInflater mInflater;
    private User user;
    private String leftAvatar;
    private String rightAvatar;

    public ChatAdapter(Context mContext, List<Chat> chatList, String leftAvatar, String rightAvatar) {
        this.mContext = mContext;
        this.chatList = chatList;
        this.leftAvatar = leftAvatar;
        this.rightAvatar = rightAvatar;
        mInflater = LayoutInflater.from(mContext);
        user = IShareContext.getInstance().getCurrentUser();
    }

    public int getCount() {
        return chatList.size();
    }

    public Object getItem(int position) {
        return chatList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Chat msg = chatList.get(position);
        ViewHolder viewHolder = null;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.chat_msg, null);
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.leftLayout = convertView.findViewById(R.id.left_layout);
            viewHolder.ivLeftAvatar = (CircleImageView) convertView.findViewById(R.id.iv_left_avatar);
            viewHolder.tvLeftText = (TextView) convertView.findViewById(R.id.tv_left_text);
            viewHolder.rightLayout = convertView.findViewById(R.id.right_layout);
            viewHolder.ivRightAvatar = (CircleImageView) convertView.findViewById(R.id.iv_right_avatar);
            viewHolder.tvRightText = (TextView) convertView.findViewById(R.id.tv_right_text);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(user.getUserId().equals(msg.fromUser)) {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);

            Log.e(TAG, "rightAvatar: " + "rightAvatar" + rightAvatar + "rightAvatar");

//            if(rightAvatar == null || "".equals(rightAvatar.trim())) {
//                String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(rightAvatar, DisplayUtil.dip2px(mContext, 48), DisplayUtil.dip2px(mContext, 48));
//                ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 48), DisplayUtil.dip2px(mContext, 48));
//                final ViewHolder finalCardHolder = viewHolder;
//                ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions,
//                        new SimpleImageLoadingListener() {
//                            @Override
//                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                finalCardHolder.ivRightAvatar.setImageBitmap(loadedImage);
//                            }
//                        });
//            } else {
//
//            }

            if(rightAvatar == null || !"".equals(rightAvatar.trim())) {
                Log.e(TAG, "rightAvatar");
                String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(rightAvatar, DisplayUtil.dip2px(mContext, 48), DisplayUtil.dip2px(mContext, 48));
                ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.ivRightAvatar);
            } else {
//                viewHolder.ivRightAvatar.setImageResource(R.drawable);
            }

            if(isShowTime(position)) {
                viewHolder.tvSendTime.setText(getTime(position));
            } else {
                viewHolder.tvSendTime.setVisibility(View.GONE);
            }

            viewHolder.tvRightText.setText(msg.content + "");
        } else {
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);

            Log.e(TAG, "leftAvatar: " + leftAvatar);

            if(leftAvatar != null && !"".equals(leftAvatar.trim())) {
                String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(leftAvatar, DisplayUtil.dip2px(mContext, 48), DisplayUtil.dip2px(mContext, 48));
                ImageLoader.getInstance().displayImage(thumbnailUrl, viewHolder.ivLeftAvatar);
            } else {
//                viewHolder.ivLeftAvatar.setImageResource(R.drawable);
            }

//            ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 48), DisplayUtil.dip2px(mContext, 48));
//            final ViewHolder finalCardHolder = viewHolder;
//            ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions,
//                    new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            finalCardHolder.ivLeftAvatar.setImageBitmap(loadedImage);
//                        }
//                    });

            if(isShowTime(position)) {
                viewHolder.tvSendTime.setText(getTime(position));
            } else {
                viewHolder.tvSendTime.setVisibility(View.GONE);
            }

            viewHolder.tvLeftText.setText(msg.content + "");
        }



        return convertView;
    }

    public boolean isShowTime(int position) {
        boolean result =  true;
        String format = "yyyy-MM-dd HH:mm:ss";
        if(position > 0 && position + 1 < chatList.size()) {
            if(DateUtil.date2TimeStamp(chatList.get(position + 1).time, format)
                    - DateUtil.date2TimeStamp(chatList.get(position).time, format) < 300000) {
                result = false;
            }
        } else if(position == chatList.size() - 1) {
            if(DateUtil.getTimeStamp()
                    - DateUtil.date2TimeStamp(chatList.get(position).time, format) < 300000) {
                return false;
            }
        }
        return result;
    }

    public String getTime(int position) {
        String result = "";
        String curDate = DateUtil.getDate(0);
        String nextDate = DateUtil.getDate(1);
        Chat chat = chatList.get(position);

        Log.e(TAG, "chat.time :" + chat.time);

        if(curDate.compareTo(chat.time) > 0
                && nextDate.compareTo(chat.time) < 0) {
            result =  chat.time.substring(0, 11);
        } else {
            result = chat.time.substring(11, 19);
        }

        return result;
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
    }

    class ViewHolder {
        public TextView tvSendTime;
        public View leftLayout;
        public CircleImageView ivLeftAvatar;
        public TextView tvLeftText;
        public View rightLayout;
        public CircleImageView ivRightAvatar;
        public TextView tvRightText;
        public ImageView ivPic;
        public ImageView ivSound;
        public TextView tvSoundTime;
    }
}
