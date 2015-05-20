package com.galaxy.ishare.sharedcard;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemListFragment extends Fragment  {

    private View mRoot;
    private LinearLayout categoryLayout, discountLayout, distanceLayout, defaultLayout, topSelectorLayout, isLocatingLayout;
    private MyClickListener myClickListener;
    private int topSelectorLayoutWidth;
    private TextView categoryTv;
    private DropDownListView cardListView;
    private static final String TAG = "ItemListFragment";
    private HttpInteract httpInteract;
    private int pageNumber = 1;
    private ArrayList<CardItem> dataList;
    private static final int DISTANCE_LOAD_URL_TYPE = 1;
    private static final int DISCOUNT_LOAD_URL_TYPE = 2;

    private static final int REFRESH_GESTURE=1;
    private static final int LOAD_MORE_GESTURE=2;

    private static final int pageSize = 6;
    private int tradeType = -1;

    // 判断使用哪个url
    private int urlType;
    // 判断是下拉刷新还是加载更多的操作
    private int gestureType;

    private CardListItemAdapter cardListItemAdapter;


    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.share_item_fragment, container, false);
        initViews(mRoot);

        // 现在默认是以折扣排序
        urlType = DISCOUNT_LOAD_URL_TYPE;

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        topSelectorLayout.measure(w, h);
        topSelectorLayoutWidth = topSelectorLayout.getMeasuredWidth();
        Log.v(TAG, topSelectorLayoutWidth + "layout width");
        Log.v(TAG, Global.screenWidth + "");


        myClickListener = new MyClickListener();
        categoryLayout.setOnClickListener(myClickListener);
        discountLayout.setOnClickListener(myClickListener);
        distanceLayout.setOnClickListener(myClickListener);
        defaultLayout.setOnClickListener(myClickListener);

        httpInteract = new HttpInteract();
        dataList = new ArrayList<>();


        cardListItemAdapter = new CardListItemAdapter(dataList, getActivity());



        // set drop down listener
        cardListView.setOnDropDownListener(new DropDownListView.OnDropDownListener() {

            @Override
            public void onDropDown() {
                gestureType=REFRESH_GESTURE;
                pageNumber=1;
                httpInteract.loadData(urlType,tradeType,IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
            }
        });

        // set on bottom listener
        cardListView.setOnBottomListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gestureType=LOAD_MORE_GESTURE;
                pageNumber++;
                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
            }
        });

        cardListView.setAdapter(cardListItemAdapter);

        if (IShareContext.getInstance().getUserLocation() == null) {
            isLocatingLayout.setVisibility(View.VISIBLE);
            cardListView.setVisibility(View.INVISIBLE);
        } else {

            isLocatingLayout.setVisibility(View.GONE);
            cardListView.setVisibility(View.VISIBLE);

            httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                    IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
        }




        // 接收位置更新广播
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                isLocatingLayout.setVisibility(View.GONE);
                cardListView.setVisibility(View.VISIBLE);

                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);

            }
        };
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastActionConstant.UPDATE_USER_LOCATION));
        return mRoot;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        localBroadcastManager.unregisterReceiver(receiver);
    }


    public void initViews(View view) {

        categoryLayout = (LinearLayout) view.findViewById(R.id.share_item_category_layout);
        discountLayout = (LinearLayout) view.findViewById(R.id.share_item_discount_layout);
        distanceLayout = (LinearLayout) view.findViewById(R.id.share_item_distance_layout);
        defaultLayout = (LinearLayout) view.findViewById(R.id.share_item_default_layout);
        topSelectorLayout = (LinearLayout) view.findViewById(R.id.share_item_top_layout);

        categoryTv = (TextView) view.findViewById(R.id.share_item_category_tv);
        isLocatingLayout = (LinearLayout) view.findViewById(R.id.share_item_is_locating_layout);

        cardListView = (DropDownListView) view.findViewById(R.id.share_item_card_listview);



    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.share_item_category_layout) {

                View popUpWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.share_item_popup_window, null);

                // popupwindow 中的listview
                ListView listViewInPopUpWindow = (ListView) popUpWindowView.findViewById(R.id.share_item_popupwindow_listview);

                final String[] wareItems = getResources().getStringArray(R.array.trade_items);
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, wareItems);
                listViewInPopUpWindow.setAdapter(adapter);

                final PopupWindow popupWindow = new PopupWindow(popUpWindowView, topSelectorLayoutWidth, Global.screenHeight * 2 / 3, false);
                popupWindow.showAsDropDown(v, 0, 0);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                listViewInPopUpWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        categoryTv.setText(wareItems[position]);
                        // 选择了某个类别
                        popupWindow.dismiss();

                        if(position!=tradeType) {
                            // 选择的类别与之前的不同
                            tradeType = position;
                            pageNumber=1;
                            dataList.clear();
                            httpInteract.loadData(urlType, tradeType,IShareContext.getInstance().getUserLocation().getLongitude(),
                                    IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                        }

                    }
                });


            } else if (v.getId() == R.id.share_item_discount_layout) {

                if (urlType!=DISCOUNT_LOAD_URL_TYPE) {

                    urlType = DISCOUNT_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();
                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }

            } else if (v.getId() == R.id.share_item_distance_layout) {

                if(urlType!=DISTANCE_LOAD_URL_TYPE) {
                    urlType = DISTANCE_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();
                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }

            } else if (v.getId() == R.id.share_item_default_layout) {

                // 点击了默认排序，现在还没有写

            }
        }
    }


    class HttpInteract {

        public void loadData(int loadType, int tradeType, double longitude, double latitude, int pageNumber, int pageSize) {

            List<NameValuePair> paramsList = new ArrayList<>();
            paramsList.add(new BasicNameValuePair("trade_type", tradeType + ""));
            paramsList.add(new BasicNameValuePair("page_num", pageNumber + ""));
            paramsList.add(new BasicNameValuePair("page_size", pageSize + ""));
            paramsList.add(new BasicNameValuePair("longitude",longitude+""));
            paramsList.add(new BasicNameValuePair("latitude",latitude+""));
            String url = null;
            if (loadType == DISCOUNT_LOAD_URL_TYPE) {
                url = URLConstant.GET_DISCOUNT_CARD_LIST;
            } else if (loadType == DISTANCE_LOAD_URL_TYPE) {
                url = URLConstant.GET_DISTANCE_CARD_LIST;
            }
            Log.v(TAG,tradeType+" "+pageNumber+" "+pageSize);
            HttpTask.startAsyncDataGetRequset(url, paramsList, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result)  {

                    try {

                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.v(TAG, "size" + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject card = jsonArray.getJSONObject(i);
                                double longitude=0.0;
                                double latitude=0.0;
                                if (card.getString("longitude").equals("null")){
                                    longitude=0.0;
                                }else {
                                    longitude=card.getDouble("longitude");
                                }
                                if (card.getString("latitude").equals("null")){
                                    latitude = 0.0;
                                }else {
                                    latitude= card.getDouble("latitude");
                                }
                                CardItem cardItem = new CardItem(card.getInt("id"), card.getString("owner"), card.getString("shop_name"), card.getInt("ware_type"), card.getDouble("discount"),
                                        card.getInt("trade_type"), card.getString("shop_location"), card.getDouble("shop_longitude"), card.getDouble("shop_latitude"),
                                        card.getString("description"), card.getString("img"), card.getString("time"), longitude,latitude,
                                        card.getString("location"), card.getDouble("distance"),card.getDouble("shop_distance"));
                                Log.v(TAG,cardItem.toString());
                                dataList.add(cardItem);
                            }
                            cardListItemAdapter.setData(dataList);
                            cardListItemAdapter.notifyDataSetChanged();
                            if (jsonArray.length()==0){
                                cardListView.setHasMore(false);

                            }
                            if (gestureType==REFRESH_GESTURE){

                                // should call onDropDownComplete function of DropDownListView at end of drop down complete.

                                cardListView.onDropDownComplete();
                            }else if (gestureType==LOAD_MORE_GESTURE){
                                // should call onBottomComplete function of DropDownListView at end of on bottom complete.
                                cardListView.onBottomComplete();
                            }
                        } else {
                            Log.v(TAG, "status is " + status);
                        }
                    } catch (Exception e) {
                        Log.v(TAG,e.toString());
                        e.printStackTrace();
                    }


                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {

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

}
