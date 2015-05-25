package com.galaxy.ishare.sharedcard;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ItemListFragment extends Fragment {

    private View mRoot;
    private LinearLayout categoryLayout, discountLayout, distanceLayout, defaultLayout, topSelectorLayout, isLocatingLayout;
    private MyClickListener myClickListener;
    private int topSelectorLayoutWidth;
    private TextView categoryTv;
    private static final String TAG = "ItemListFragment";
    private HttpInteract httpInteract;
    private int pageNumber = 1;
    public  LinkedList<CardItem> dataList;
    private static final int DISTANCE_LOAD_URL_TYPE = 1;
    private static final int DISCOUNT_LOAD_URL_TYPE = 2;

    private static final int REFRESH_GESTURE = 1;
    private static final int LOAD_MORE_GESTURE = 2;

    private static final int pageSize = 6;
    private int tradeType = -1;

    // 判断使用哪个url
    private int urlType;
    // 判断是下拉刷新还是加载更多的操作
    private int gestureType;

    private CardListItemAdapter cardListItemAdapter;


    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver;

    private FrameLayout listViewFrameLayout;
    private ListView cardListView;
    private PullToRefreshListView pullToRefreshListView;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

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
        dataList = new LinkedList<>();


        cardListItemAdapter = new CardListItemAdapter(dataList, getActivity());


        pullToRefreshListView = new PullToRefreshListView(getActivity());
        initPullToRefreshListView(pullToRefreshListView);


        if (IShareContext.getInstance().getUserLocation() == null) {
            isLocatingLayout.setVisibility(View.VISIBLE);
            pullToRefreshListView.setVisibility(View.INVISIBLE);
        } else {

            isLocatingLayout.setVisibility(View.GONE);
            pullToRefreshListView.setVisibility(View.VISIBLE);

        }

        // 接收位置更新广播
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                isLocatingLayout.setVisibility(View.GONE);
                pullToRefreshListView.setVisibility(View.VISIBLE);

                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);

            }
        };
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastActionConstant.UPDATE_USER_LOCATION));


        listViewFrameLayout = (FrameLayout) mRoot.findViewById(R.id.share_item_listview_framelayout);
        listViewFrameLayout.addView(pullToRefreshListView);

        return mRoot;
    }

    public void initPullToRefreshListView(PullToRefreshListView mPullListView) {
        mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
        cardListView = mPullListView.getRefreshableView();
        cardListView.setAdapter(cardListItemAdapter);

        // listview 条目点击
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, dataList.get(position));
                startActivity(intent);
            }
        });

        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);

            }
        });

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

                        if (position != tradeType) {
                            // 选择的类别与之前的不同
                            tradeType = position;
                            pageNumber = 1;
                            dataList.clear();
                            httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                                    IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                        }

                    }
                });


            } else if (v.getId() == R.id.share_item_discount_layout) {

                if (urlType != DISCOUNT_LOAD_URL_TYPE) {

                    urlType = DISCOUNT_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();
                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }

            } else if (v.getId() == R.id.share_item_distance_layout) {

                if (urlType != DISTANCE_LOAD_URL_TYPE) {
                    urlType = DISTANCE_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();
                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }

            } else if (v.getId() == R.id.share_item_default_layout) {

                // TODO:点击了默认排序，现在还没有写


            }
        }
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        pullToRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return mDateFormat.format(new Date(time));
    }

    class HttpInteract {

        public void loadData(int loadType, int tradeType, double longitude, double latitude, final int pageNumber, int pageSize) {

            List<NameValuePair> paramsList = new ArrayList<>();
            paramsList.add(new BasicNameValuePair("trade_type", tradeType + ""));
            paramsList.add(new BasicNameValuePair("page_num", pageNumber + ""));
            paramsList.add(new BasicNameValuePair("page_size", pageSize + ""));
            paramsList.add(new BasicNameValuePair("longitude", longitude + ""));
            paramsList.add(new BasicNameValuePair("latitude", latitude + ""));
            String url = null;
            if (loadType == DISCOUNT_LOAD_URL_TYPE) {
                url = URLConstant.GET_DISCOUNT_CARD_LIST;
            } else if (loadType == DISTANCE_LOAD_URL_TYPE) {
                url = URLConstant.GET_DISTANCE_CARD_LIST;
            }
            Log.v(TAG, tradeType + " " + pageNumber + " " + pageSize);
            HttpTask.startAsyncDataGetRequset(url, paramsList, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {

                    try {

                        boolean hasMoreData = true;
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        Log.v(TAG, "result" + result);
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.v(TAG, "size" + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject card = jsonArray.getJSONObject(i);

                                CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);
                                Log.v(TAG, cardItem.toString());
                                if (gestureType == REFRESH_GESTURE) {
                                    dataList.addFirst(cardItem);

                                } else {
                                    dataList.add(cardItem);
                                }
                            }
                            if (jsonArray.length() == 0) {

                                hasMoreData = false;
                            }
//                            // 没有内容
//                            if (jsonArray.length() == 0 && pageNumber == 1) {
//                                pullToRefreshListView.setVisibility(View.GONE);
//
//                            }

                            cardListItemAdapter.notifyDataSetChanged();
                            pullToRefreshListView.onPullDownRefreshComplete();
                            pullToRefreshListView.onPullUpRefreshComplete();
                            pullToRefreshListView.setHasMoreData(hasMoreData);
                            setLastUpdateTime();


                        } else {
                            Log.v(TAG, "status is " + status);
                        }
                    } catch (Exception e) {
                        Log.v(TAG, e.toString());
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
