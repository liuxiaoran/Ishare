package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.user_request.PublishRequestActivity;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Zhan on 2015/6/16.
 */
public class CardRequestAdapter extends BaseAdapter {
    private static String TAG = "CardRequestAdapter";
    private List<CardItem> dataList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CardItem cardItem;
    private String[] cardItems;
    private String[] statusItems;
    boolean isHasShopLatLng = false;

    public CardRequestAdapter(Context mContext, Vector dataList) {
        this.dataList = dataList;
        this.mContext = mContext;
        cardItems = mContext.getResources().getStringArray(R.array.card_items);
        statusItems = mContext.getResources().getStringArray(R.array.card_items);
        mLayoutInflater = LayoutInflater.from(mContext);
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_card_request, null);
            viewHolder = new ViewHolder();

            viewHolder.shopImageIv = (ImageView) convertView.findViewById(R.id.item_card_request_shopimgs_iv);
            viewHolder.shopNameTv = (TextView) convertView.findViewById(R.id.item_card_request_shopname_tv);
            viewHolder.wareTypeTv = (TextView) convertView.findViewById(R.id.item_card_request_cardtype_tv);
            viewHolder.shopLocationTv = (TextView) convertView.findViewById(R.id.item_card_request_shop_location_tv);
            viewHolder.deleteCardIv = (ImageView) convertView.findViewById(R.id.item_card_request_delete_iv);
            viewHolder.editCardIv = (ImageView) convertView.findViewById(R.id.item_card_request_edit_iv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final CardItem cardRequest = dataList.get(position);
        final ViewHolder finalCardHolder = viewHolder;
        if (cardRequest.cardImgs != null && cardRequest.cardImgs.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(cardRequest.cardImgs[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 60));
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
        viewHolder.shopLocationTv.setText(cardRequest.shopLocation);
        viewHolder.wareTypeTv.setText(cardItems[cardRequest.wareType]);

        final int curPosition = position;
        final boolean isHasShopLatLng = false;


        viewHolder.editCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CardRequestEditActivity.class);
//                intent.putExtra("editCardRequest", cardRequest);
//                mContext.startActivity(intent);
                Intent intent = new Intent(mContext, PublishRequestActivity.class);
                intent.putExtra(PublishRequestActivity.PARAMETER_WHO_COME, CardRequestTestActivity.CARDREQUEST_TO_PUBLISH);
                intent.putExtra(PublishRequestActivity.PARAMETER_CARDREQUEST_CARD_ITEM, dataList.get(position));
                mContext.startActivity(intent);
            }
        });

        viewHolder.deleteCardIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("id", cardRequest.id + ""));
                HttpTask.startAsyncDataPostRequest(URLConstant.DELETE_I_REQUEST_CARD, params, new HttpDataResponse() {
                    @Override
                    public void onRecvOK(HttpRequestBase request, String result) {
                        JSONObject jsonObject = null;
                        Log.v(TAG, "result" + result);
                        try {
                            jsonObject = new JSONObject(result);
                            int status = jsonObject.getInt("status");
                            if (status == 0) {
                                handler.sendEmptyMessage(0);
                                dataList.remove(curPosition);
                                notifyDataSetChanged();
                            } else {
                                handler.sendEmptyMessage(1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                        Log.v(TAG, "retCode" + retCode);
                        handler.sendEmptyMessage(1);
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

    private Handler handler = new Handler() {
        public void handlerMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(mContext, "删除失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    notifyDataSetChanged();
                    break;
            }
        }
    };

    class ViewHolder {
        public ImageView shopImageIv;
        public TextView shopNameTv;
        public TextView shopLocationTv;
        public TextView wareTypeTv;
        public ImageView isReadIv;
        public TextView lastReplyTv;
        public ImageView deleteCardIv;
        public ImageView editCardIv;
    }
}
