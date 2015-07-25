package com.galaxy.ishare.sharedcard;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.IShareFragment;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpGetExt;
import com.galaxy.ishare.http.HttpPostExt;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.mapLBS.CardActivity;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.melnykov.fab.FloatingActionButton;

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
import java.util.Vector;

public class ItemListFragment extends IShareFragment {

    public static final String INTENT_ITEM_TO_DETAIL = "INTENT_ITEM_TO_DETAIL";

    private static final int DISTANCE_LOAD_URL_TYPE = 1;
    private static final int DISCOUNT_LOAD_URL_TYPE = 2;

    private static final int REFRESH_GESTURE = 1;
    private static final int LOAD_MORE_GESTURE = 2;

    private static final int pageSize = 12;

    private View mRoot;
    private LinearLayout categoryLayout, discountLayout, distanceLayout, isLocatingLayout;
    private MyClickListener myClickListener;
    private TextView categoryTv, discountTv, distanceTv;
    private static final String TAG = "ItemListFragment";
    private HttpInteract httpInteract;
    private int pageNumber = 1;

    // 存储不同tab 的数据
    public Vector<CardItem> dataList;

    public RelativeLayout loadingLayout;

    private int tradeType = 0;

    // 判断使用哪个url
    private int urlType;
    // 判断是下拉刷新还是加载更多的操作
    private int gestureType;

    private CardListItemAdapter cardListItemAdapter;


    private FrameLayout listViewFrameLayout;
    private ListView cardListView;
    private PullToRefreshListView pullToRefreshListView;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver;

    private FloatingActionButton mapStyleBtn;

    private int receiveBroadcastCount;
    private ImageView categoryTabArrowIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        init("ItemListFragment");
        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.share_item_fragment, container, false);
        initViews(mRoot);

        // 现在默认是以折扣排序
        urlType = DISCOUNT_LOAD_URL_TYPE;


        discountTv.setTextColor(getResources().getColor(R.color.red));
        discountLayout.setSelected(true);

        myClickListener = new MyClickListener();
        categoryLayout.setOnClickListener(myClickListener);
        discountLayout.setOnClickListener(myClickListener);
        distanceLayout.setOnClickListener(myClickListener);

        httpInteract = new HttpInteract();
        dataList = new Vector<>();


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
                // 不知为何会收到两次，所以在第二次的时候不执行内部代码
                if (receiveBroadcastCount == 0) {
                    isLocatingLayout.setVisibility(View.GONE);
                    pullToRefreshListView.setVisibility(View.VISIBLE);

                    pullToRefreshListView.doPullRefreshing(true, 500);
                    receiveBroadcastCount++;
                }

            }
        };
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastActionConstant.UPDATE_USER_LOCATION));

        listViewFrameLayout = (FrameLayout) mRoot.findViewById(R.id.share_item_listview_framelayout);
        listViewFrameLayout.addView(pullToRefreshListView, 0);


        mapStyleBtn = (FloatingActionButton) mRoot.findViewById(R.id.map_style_floating_btn);
//      mapStyleBtn.attachToListView(cardListView);  // 如果attach 之后会在滑下时隐藏，在滑上时显示。所以不attach

        // 地图模式按钮点击
        mapStyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CardActivity.class);
                startActivity(intent);

            }
        });



        return mRoot;
    }

    public void initPullToRefreshListView(PullToRefreshListView mPullListView) {
        mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
        cardListView = mPullListView.getRefreshableView();
        cardListView.setDivider(new ColorDrawable(getResources().getColor(R.color.listview_divider)));
        cardListView.setDividerHeight(1);
        cardListView.setAdapter(cardListItemAdapter);

        // listview 条目点击
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < dataList.size()) {  // 防止点footer
                    Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                    intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, dataList.get(position));
                    intent.putExtra(CardDetailActivity.PARAMETER_WHO_SEND, INTENT_ITEM_TO_DETAIL);
                    startActivity(intent);
                }
            }
        });

        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                dataList.clear();
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

        categoryTv = (TextView) view.findViewById(R.id.share_item_category_tv);
        distanceTv = (TextView) view.findViewById(R.id.share_item_distance_tv);
        discountTv = (TextView) view.findViewById(R.id.share_item_discount_tv);
        isLocatingLayout = (LinearLayout) view.findViewById(R.id.share_item_is_locating_layout);

        loadingLayout = (RelativeLayout) view.findViewById(R.id.share_item_loading_layout);


        categoryTabArrowIv = (ImageView) view.findViewById(R.id.main_catagory_tab_arrow_iv);



    }



    class MyClickListener implements View.OnClickListener {

        PopupWindow popupWindow;

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.share_item_category_layout) {


                if (popupWindow != null && popupWindow.isShowing()) {
                    categoryTabArrowIv.setSelected(false);
                    popupWindow.dismiss();
                } else {


                    final View popUpWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.share_item_popup_window, null);
                    // popupwindow 中的listview
                    ListView listViewInPopUpWindow = (ListView) popUpWindowView.findViewById(R.id.share_item_popupwindow_listview);

                    LinearLayout whoLinearLayout = (LinearLayout) popUpWindowView.findViewById(R.id.share_item_popupwindow_background);


                    final String[] wareItems = getResources().getStringArray(R.array.trade_items);
                    CategoryWindowAdapter categoryWindowAdapter = new CategoryWindowAdapter(wareItems, getActivity());
                    listViewInPopUpWindow.setAdapter(categoryWindowAdapter);

                    popupWindow = new PopupWindow(popUpWindowView, Global.screenWidth / 3, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    // 点击popupwindow 灰色部分，popuowindow 消失
                    whoLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                        }
                    });

                    // 设置点击popuwindow外的空白位置，popupwindow 能消失
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());

                    popupWindow.showAsDropDown(v, -DisplayUtil.dip2px(getActivity(), 9), DisplayUtil.dip2px(getActivity(), 2));
                    listViewInPopUpWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            categoryTv.setText(wareItems[position]);
                            categoryTv.setTextColor(getResources().getColor(R.color.red));
                            // 选择了某个类别
                            popupWindow.dismiss();

                            if (position != tradeType) {
                                // 选择的类别与之前的不同
                                tradeType = position;
                                pageNumber = 1;
                                dataList.clear();


                                pullToRefreshListView.doPullRefreshing(true, 500);
//                                httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
//                                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                            }

                        }
                    });
                }



            } else if (v.getId() == R.id.share_item_discount_layout) {

                discountLayout.setSelected(true);
                distanceLayout.setSelected(false);

                if (urlType != DISCOUNT_LOAD_URL_TYPE) {

                    urlType = DISCOUNT_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();

                    pullToRefreshListView.doPullRefreshing(true, 500);
//                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
//                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }

                distanceTv.setTextColor(getResources().getColor(R.color.dark_secondary_text));
                discountTv.setTextColor(getResources().getColor(R.color.red));

            } else if (v.getId() == R.id.share_item_distance_layout) {

                distanceLayout.setSelected(true);
                discountLayout.setSelected(false);

                if (urlType != DISTANCE_LOAD_URL_TYPE) {
                    urlType = DISTANCE_LOAD_URL_TYPE;
                    pageNumber = 1;
                    dataList.clear();

//                    setTabsUnPressed();
                    pullToRefreshListView.doPullRefreshing(true, 500);
//                    httpInteract.loadData(urlType, tradeType, IShareContext.getInstance().getUserLocation().getLongitude(),
//                            IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber, pageSize);
                }


                distanceTv.setTextColor(getResources().getColor(R.color.red));
                discountTv.setTextColor(getResources().getColor(R.color.dark_secondary_text));

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

        // 存放这个界面中所有http请求
        ArrayList<HttpGetExt> httpGetExts = new ArrayList<>();

        public void loadData(int loadType, int tradeType, double longitude, double latitude, final int pageNumber, int pageSize) {

//           之前是如果是显示第一页，显示加载layout，现在只是显示下拉刷新，不显示加载layout
//            if (pageNumber == 1)
//                loadingLayout.setVisibility(View.VISIBLE);

            for (int i = 0; i < httpGetExts.size(); i++) {
                HttpGetExt httpGetExt = httpGetExts.get(i);
                if (!httpGetExt.isCancelled()) {
                    httpGetExt.cancel();
                    httpGetExts.remove(httpGetExt);
                }
            }

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
            HttpGetExt currentGet = HttpTask.startAsyncDataGetRequset(url, paramsList, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    loadingLayout.setVisibility(View.GONE);

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
                                    dataList.add(cardItem);
                                    setLastUpdateTime();
                                } else {
                                    dataList.add(cardItem);
                                }
                            }
                            if (jsonArray.length() == 0) {

                                hasMoreData = false;
                            }


                            cardListItemAdapter.notifyDataSetChanged();
                            if (gestureType == REFRESH_GESTURE)
                                pullToRefreshListView.onPullDownRefreshComplete();
                            else
                                pullToRefreshListView.onPullUpRefreshComplete();

                            pullToRefreshListView.setHasMoreData(hasMoreData);




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
                    Log.v(TAG, "error" + retCode);

                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
            httpGetExts.add(currentGet);


        }

    }

}
