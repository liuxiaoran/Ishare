package com.galaxy.ishare.sharedcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.chat.ChatManager;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.CollectionDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardComment;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.publishware.PublishItemActivity;
import com.galaxy.ishare.usercenter.me.CardIShareAdapter;
import com.galaxy.ishare.usercenter.me.CardIshareActivity;
import com.galaxy.ishare.usercenter.me.CardIshareEditActivity;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.galaxy.ishare.utils.WidgetController;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/21.
 */
public class CardDetailActivity extends IShareActivity {

    public static final String PARAMETER_CARD_ITEM = "PARAMETER_CARD_ITEM";
    public static final String PARAMETER_WHO_SEND = "PARAMETER_WHO_SEND";

    public static final int CARDISHARE_TO_CARD_DETAIL_REQUEST_CODE = 1;


    private static final String TAG = "carddetail";

    private ViewPager cardPager;
    private TextView shopNameTv, discountTv, ownerNameTv, shopAddrTv, shopDistanceTv, cardTypeTv,
            ownerDistanceTv, descriptionTv, commentCountTv;

    private FButton contactBtn;
    private CardItem cardItem;
    //一共有几张图片,即viewpager有几个view
    private int picNumber;
    // viewpager 中的ImageView
    private ImageView[] picIvs;

    private ArrayList<View> pagerList;

    public int pageIndex = 1;
    public int pageSize = 6;
    private ArrayList<CardComment> commentsList;


    private HttpInteract httpInteract;

    private CircleImageView ownerAvatarCv;
    private FloatingActionButton mapBtn;
    private ImageView genderIv;

    private LinearLayout ratingLayout, commentLayout;

    public int currentLastCommentIndex;
    private TextView moreCommentTv;
    private FButton collectBtn;
    private LinearLayout editLayout;
    private FButton editBtn, deleteBtn;
    private RelativeLayout ownerLayout;
    private int maxUploadPicCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_item_card_detail_activity);

        IShareContext.getInstance().createDefaultHomeActionbar(this, "卡详情");

        cardItem = getIntent().getParcelableExtra(PARAMETER_CARD_ITEM);

        if (cardItem.cardImgs != null) {
            picNumber = cardItem.cardImgs.length;
        }

        picIvs = new ImageView[picNumber];

        pagerList = new ArrayList<>();
        httpInteract = new HttpInteract();
        commentsList = new ArrayList<>();

        initViews();

        initCardPager();

        writeValueIntoViews();

//        initMapView();

        // 地图点击进入新的界面，展示三方位置
//        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//
//                Intent intent = new Intent(CardDetailActivity.this, CardDetailMapActivity.class);
//                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LONGITUDE, cardItem.ownerLongitude);
//                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LATITUDE, cardItem.ownerLatitude);
//                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LONGITUDE, cardItem.shopLongitude);
//                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LATITUDE,cardItem.shopLatitude);
//
//                startActivity(intent);
//            }
//
//            @Override
//            public boolean onMapPoiClick(MapPoi mapPoi) {
//                return false;
//            }
//        });


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailActivity.this, CardDetailMapActivity.class);
                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LONGITUDE, cardItem.ownerLongitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_OWNER_LATITUDE, cardItem.ownerLatitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LONGITUDE, cardItem.shopLongitude);
                intent.putExtra(CardDetailMapActivity.PARAMETER_SHOP_LATITUDE, cardItem.shopLatitude);

                startActivity(intent);
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳到聊天
                User currentUser = IShareContext.getInstance().getCurrentUser();
                ChatManager.getInstance().startActivityFromShare(cardItem.getId(), cardItem.ownerId, cardItem.getOwnerAvatar(), currentUser.getUserId());

            }
        });
        moreCommentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageIndex++;
                getComments(cardItem.getId());
            }
        });
        WidgetController.getInstance().setRatingLayout(cardItem.ratingCount, this, ratingLayout);

        getComments(cardItem.id);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailActivity.this, CardIshareEditActivity.class);

                intent.putExtra(CardIshareEditActivity.PARAMETER_SHOP_NAME, cardItem.shopName);
                intent.putExtra(CardIshareEditActivity.PARAMETER_SHOP_LOCATION, cardItem.shopLocation);
                intent.putExtra(CardIshareEditActivity.PARAMETER_DISCOUNT, cardItem.getStringDiscount());
                intent.putExtra(CardIshareEditActivity.PARAMETER_SHOP_LONGITUDE, cardItem.shopLongitude);
                intent.putExtra(CardIshareEditActivity.PARAMETER_SHOP_LATITUDE, cardItem.shopLatitude);
                intent.putExtra(CardIshareEditActivity.PARAMETER_WARE_TYPE, cardItem.wareType);
                intent.putExtra(CardIshareEditActivity.PARAMETER_TRADE_TYPE, cardItem.tradeType);
                intent.putExtra(CardIshareEditActivity.PARAMETER_OWNER_AVAILABLE, cardItem.ownerLocation);
                intent.putExtra(CardIshareEditActivity.PARAMETER_DESCRIPTION, cardItem.description);
                intent.putExtra(CardIshareEditActivity.PARAMETER_CARD_ID,cardItem.id);
                if (cardItem.cardImgs != null) {
                    if (picNumber == maxUploadPicCount) {
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG1, cardItem.cardImgs[0]);
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG2, cardItem.cardImgs[1]);
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG3, cardItem.cardImgs[2]);
                    }
                    if (picNumber == maxUploadPicCount - 1) {
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG1, cardItem.cardImgs[0]);
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG2, cardItem.cardImgs[1]);
                    }
                    if (picNumber == maxUploadPicCount - 2) {
                        intent.putExtra(CardIshareEditActivity.PARAMETER_IMG1, cardItem.cardImgs[0]);
                    }

                }


                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("card_ids", JsonObjectUtil.parseArrayToJsonString(new String[]{cardItem.id + ""})));
                HttpTask.startAsyncDataPostRequest(URLConstant.DELETE_SHARE_CARD, params, new HttpDataResponse() {
                    @Override
                    public void onRecvOK(HttpRequestBase request, String result) {
                        Log.e(TAG, result);
                        Toast.makeText(CardDetailActivity.this, "删除成功", Toast.LENGTH_LONG).show();
                        setResult(0, new Intent(CardDetailActivity.this, CardIshareActivity.class));
                        CardDetailActivity.this.finish();

//                        JSONObject jsonObject = null;
//                        try{
//                            jsonObject = new JSONObject(result);
//                            int status=jsonObject.getInt("status");
//                            if (status==0){
//                                Log.e(TAG, "result");
//                                Toast.makeText(CardDetailActivity.this,"删除成功",Toast.LENGTH_LONG).show();
//                                setResult(0, new Intent(CardDetailActivity.this, CardIshareActivity.class));
//                                CardDetailActivity.this.finish();
//                            }
//                        }catch (JSONException e){
//                            e.printStackTrace();
//                        }
                    }

                    @Override
                    public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                        Toast.makeText(CardDetailActivity.this, "删除失败，请重试", Toast.LENGTH_LONG).show();
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
        // 从我-》我的分享中进来的，需要隐藏一些视图,显示一些视图
        if (getIntent().getStringExtra(PARAMETER_WHO_SEND).equals(CardIshareActivity.CARDISHARE_TO_DETAIL)) {
            editLayout.setVisibility(View.VISIBLE);
            mapBtn.setVisibility(View.INVISIBLE);
            ownerLayout.setVisibility(View.GONE);
            collectBtn.setVisibility(View.INVISIBLE);
        }

        if (IShareContext.getInstance().getCurrentUser().getUserId().equals(cardItem.getOwnerId())) {
            contactBtn.setVisibility(View.INVISIBLE);
        }

    }

//    private void initMapView() {
//
//        baiduMap = cardMapView.getMap();
//
//
//        // 地图的中点显示店的位置
//        float zoomLevel = 15;// 3-20
//        LatLng shopLatLng = new LatLng(cardItem.shopLatitude, cardItem.shopLongitude);
//        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLngZoom(shopLatLng, zoomLevel);
//        baiduMap.setMapStatus(statusUpdate);
//
//        defaultPoiBitmap = BitmapDescriptorFactory
//                .fromResource(R.drawable.icon_gcoding);
//
//        // 将店的位置的marker显示出来
//        //构建MarkerOption，用于在地图上添加Marker
//        OverlayOptions option = new MarkerOptions()
//                .position(shopLatLng)
//                .icon(defaultPoiBitmap)
//                .zIndex(9)
//                .draggable(false);
//        //在地图上添加Marker，并显示
//        Marker newMarker = (Marker) baiduMap.addOverlay(option);
//
//    }

    private void writeValueIntoViews() {
        shopNameTv.setText(cardItem.shopName);
        discountTv.setText(cardItem.discount + "折");
        ownerNameTv.setText(cardItem.ownerName);
        String[] trades = getResources().getStringArray(R.array.trade_items);
        shopAddrTv.setText(cardItem.shopLocation);
        shopDistanceTv.setText(cardItem.shopDistance + "");
        final String[] cardTypes = getResources().getStringArray(R.array.card_items);
        cardTypeTv.setText(cardTypes[cardItem.wareType]);
        ownerDistanceTv.setText(cardItem.ownerDistance + "");
        descriptionTv.setText(cardItem.description);

        ImageSize avatarSize = new ImageSize(DisplayUtil.dip2px(this, 40), DisplayUtil.dip2px(this, 40));
        Log.v(TAG, "avatar link:" + cardItem.ownerAvatar);
        ImageLoader.getInstance().loadImage(cardItem.ownerAvatar, avatarSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                ownerAvatarCv.setImageBitmap(loadedImage);
            }
        });

        commentCountTv.setText(cardItem.getCommentCount() + "");


        if ("男".equals(cardItem.ownerGender)) {
            genderIv.setImageResource(R.drawable.icon_male);
        }

        if (CollectionDao.getInstance(this).find(cardItem.id, IShareContext.getInstance().getCurrentUser().getUserId()) != null) {
            collectBtn.setText("取消收藏");
            collectBtn.setButtonColor(getResources().getColor(R.color.gray));
        } else {
            collectBtn.setText("收藏");
            collectBtn.setButtonColor(getResources().getColor(R.color.color_primary));
        }


        collectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((FButton) v).getText().toString().equals("收藏")) {
                    cardItem.setUserId(IShareContext.getInstance().getCurrentUser().getUserId());
                    CollectionDao.getInstance(CardDetailActivity.this).add(cardItem);
                    collectBtn.setText("取消收藏");
                    collectBtn.setButtonColor(getResources().getColor(R.color.gray));
                    httpInteract.collectCard(cardItem.id + "");
                } else {
                    CollectionDao.getInstance(CardDetailActivity.this).delete(cardItem);
                    collectBtn.setText("收藏");
                    collectBtn.setButtonColor(getResources().getColor(R.color.color_primary));
                    httpInteract.unCollectCard(cardItem.id + "");
                }

            }
        });


    }

    private void initViews() {


        cardPager = (ViewPager) findViewById(R.id.share_item_detail_viewpager);

        shopNameTv = (TextView) findViewById(R.id.share_item_detail_shop_name_tv);
        discountTv = (TextView) findViewById(R.id.share_item_detail_discount_tv);
        ownerNameTv = (TextView) findViewById(R.id.share_item_detail_owner_name_tv);

        shopAddrTv = (TextView) findViewById(R.id.share_item_detail_shop_location_tv);
        shopDistanceTv = (TextView) findViewById(R.id.share_item_detail_shop_distance_tv);
        cardTypeTv = (TextView) findViewById(R.id.share_item_detail_card_type_tv);
        ownerDistanceTv = (TextView) findViewById(R.id.share_item_detail_owner_distance_tv);
        descriptionTv = (TextView) findViewById(R.id.share_item_detail_card_description_tv);


        ownerAvatarCv = (CircleImageView) findViewById(R.id.share_item_detail_owner_avatar_cv);

        mapBtn = (FloatingActionButton) findViewById(R.id.share_item_detail_map_btn);
        contactBtn = (FButton) findViewById(R.id.share_item_detail_contact_btn);
        commentCountTv = (TextView) findViewById(R.id.share_item_detail_comment_number_tv);

        ratingLayout = (LinearLayout) findViewById(R.id.share_item_detail_rating_layout);
        commentLayout = (LinearLayout) findViewById(R.id.share_item_detail_comments_layout);
        genderIv = (ImageView) findViewById(R.id.share_item_detail_owner_gender_iv);

        moreCommentTv = (TextView) findViewById(R.id.share_item_detail_more_comment_tv);
        collectBtn = (FButton) findViewById(R.id.share_item_collect_btn);
        editLayout = (LinearLayout) findViewById(R.id.share_item_detail_edit_layout);
        editBtn = (FButton) findViewById(R.id.share_item_detail_edit_btn);
        deleteBtn = (FButton) findViewById(R.id.share_item_detail_delete_btn);
        ownerLayout = (RelativeLayout) findViewById(R.id.share_item_detail_owner_layout);

    }

    private void initCardPager() {
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < picNumber; i++) {
            View view = inflater.inflate(R.layout.share_item_detail_viewpager, null);
            picIvs[i] = (ImageView) view.findViewById(R.id.share_item_detail_card_pager_iv);

            final int finalI = i;
            Log.v(TAG, "carddetail  " + cardItem.cardImgs[i]);
            ImageLoader.getInstance().displayImage(cardItem.cardImgs[i], picIvs[finalI], IShareApplication.defaultOptions);
            pagerList.add(view);
        }

        MyPagerAdapter pagerAdapter = new MyPagerAdapter();
        cardPager.setAdapter(pagerAdapter);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (ItemListFragment.INTENT_ITEM_TO_DETAIL.equals(getIntent().getStringExtra(PARAMETER_WHO_SEND))) {
                NavUtils.navigateUpFromSameTask(this);
            } else {
                this.finish();
            }
        }
        return true;
    }

    public class MyPagerAdapter extends android.support.v4.view.PagerAdapter {

        @Override
        public int getCount() {
            return pagerList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pagerList.get(position), 0);//添加页卡
            return pagerList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView(pagerList.get(position));
        }
    }


    @Override
    protected void onDestroy() {
//        defaultPoiBitmap.recycle();
        super.onDestroy();

    }

    class HttpInteract {
        public void borrowCard() {

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("borrow_id", IShareContext.getInstance().getCurrentUser().getUserId()));
            params.add(new BasicNameValuePair("lend_id", cardItem.ownerId));
            params.add(new BasicNameValuePair("card_id", cardItem.id + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.BORROW_CARD, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    JSONObject jsonObject = null;
                    try {

                        jsonObject = new JSONObject(result);

                        if (jsonObject.getInt("status") == 0) {
                            Toast.makeText(CardDetailActivity.this, "已发送借卡请求", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CardDetailActivity.this, "借卡失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Toast.makeText(CardDetailActivity.this, "借卡失败，请重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }


        public void collectCard(String cardId) {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("card_id", cardId));
            HttpTask.startAsyncDataPostRequest(URLConstant.ADD_COLLECTION, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    Log.v(TAG, "collect success");
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, "collect is error");
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }

        public void unCollectCard(String cardId) {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("card_id", JsonObjectUtil.parseArrayToJsonString(new String[]{cardId})));
            HttpTask.startAsyncDataPostRequest(URLConstant.REMOVE_COLLOECTION, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    Log.v(TAG, "uncollect success");
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, "collect is error");
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
        }

    }

    private void putCommentIntoLayout(int beginIndex, int size) {


        for (int i = beginIndex; i <= beginIndex + size - 1; i++) {
            View comment = createCommentView(commentsList.get(i));
            commentLayout.addView(comment, i);
        }


    }

    private View createCommentView(CardComment cardComment) {
        LayoutInflater inflater = getLayoutInflater();
        View commentView = inflater.inflate(R.layout.share_item_detail_comment, null);
        CircleImageView avatarIv = (CircleImageView) commentView.findViewById(R.id.share_item_detail_comment_avatar_iv);
        int avatarEdge = DisplayUtil.dip2px(this, 40);
        String avatarThumbnail = QiniuUtil.getInstance().getFileThumbnailUrl(cardComment.commenterAvatar, avatarEdge, avatarEdge);
        ImageLoader.getInstance().displayImage(avatarThumbnail, avatarIv);

        TextView commenterNameTv = (TextView) commentView.findViewById(R.id.share_item_detail_commenter_name_tv);
        ImageView genderIv = (ImageView) commentView.findViewById(R.id.share_item_detail_comment_gender_iv);
        TextView commentTv = (TextView) commentView.findViewById(R.id.share_item_detail_comment_tv);
        TextView timeTv = (TextView) commentView.findViewById(R.id.share_item_detail_time_tv);
        LinearLayout ratingLayout = (LinearLayout) commentView.findViewById(R.id.share_item_comment_rating_layout);

        commenterNameTv.setText(cardComment.nickName);
        if (cardComment.gender.equals("男")) {
            genderIv.setImageResource(R.drawable.icon_male);
        } else {
            genderIv.setImageResource(R.drawable.icon_female);
        }
        commentTv.setText(cardComment.commentContent);

        String time = cardComment.comment_time.split(" ")[0];
        time = time.substring(2);
        timeTv.setText(time);


        WidgetController.getInstance().setRatingLayout(cardComment.rating, this, ratingLayout);

        return commentView;


    }

    public void getComments(int cardId) {

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("card_id", cardId + ""));
        params.add(new BasicNameValuePair("page_num", pageIndex + ""));
        params.add(new BasicNameValuePair("page_size", pageSize + ""));
        HttpTask.startAsyncDataPostRequest(URLConstant.GET_CARD_COMMENTS, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 0) {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Log.v(TAG, "jsonArray:" + jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject comment = jsonArray.getJSONObject(i);
                            commentsList.add(JsonObjectUtil.parseJsonToComment(comment));
                        }


                        if (jsonArray.length() < pageSize) {
                            moreCommentTv.setText("全部评论了");
                            // 不能点击了
                            moreCommentTv.setEnabled(false);
                        }
                        putCommentIntoLayout(currentLastCommentIndex, jsonArray.length());

                        currentLastCommentIndex += jsonArray.length();
                    }
                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.v(TAG, "get detail is error");
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

}
