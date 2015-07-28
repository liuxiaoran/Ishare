package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.sharedcard.CardListItemAdapter;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.galaxy.ishare.user_request.PublishRequestActivity;
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by doqin on 2015/7/16.
 */
public class CardRequestTestActivity extends IShareActivity {


    public FrameLayout containerLayout;
    public ListView requestCardListView;
    public ImageView editCardImageView, deleteCardImageView;
    public int pageNumber = 1;

    private int gestureType;
    private int REFRESH_GESTURE = 1;
    private int LOAD_MORE_GESTURE = 2;
    private PullToRefreshListView refreshListView;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private Vector<CardItem> dataList = new Vector<>();
    private CardRequestAdapter cardRequestAdapter;
    private HttpInteract httpInteract;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_request);

        requestCardListView = (ListView) findViewById(R.id.usenter_request_listview);
//        editCardImageView = (ImageView) findViewById(R.id.item_card_request_edit_iv);
//        deleteCardImageView = (ImageView) findViewById(R.id.item_card_request_delete_iv);

        IShareContext.getInstance().createActionbar(this, true, "我在找的卡");
        httpInteract = new HttpInteract();


        httpInteract.loadData();
//        cardRequestAdapter = new CardRequestAdapter(this, dataList);

        refreshListView = new PullToRefreshListView(this);

        requestCardListView.setAdapter(cardRequestAdapter);
        requestCardListView.setDividerHeight(0);

//        containerLayout = (FrameLayout) findViewById(R.id.card_request_container_layout);
//        initPullRefreshListView(refreshListView);
//        containerLayout.addView(refreshListView);
//        refreshListView.doPullRefreshing(true, 500);


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        dataList.clear();
//        httpInteract.loadData();
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


//    private void initPullRefreshListView(PullToRefreshListView pullToRefreshListView) {
//        pullToRefreshListView.setScrollLoadEnabled(false);
//        pullToRefreshListView.setPullLoadEnabled(false);
//        requestCardListView = pullToRefreshListView.getRefreshableView();
//        requestCardListView.setAdapter(cardRequestAdapter);
//        requestCardListView.setDividerHeight(0);
//
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                gestureType = REFRESH_GESTURE;
//                pageNumber = 1;
//                httpInteract.loadData(pageNumber);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
////                gestureType = LOAD_MORE_GESTURE;
////                pageNumber = 1;
////                httpInteract.loadData(pageNumber);
//            }
//        });
//
//    }


    class HttpInteract {

//        private void setLastUpdateTime() {
//            String text = formatDateTime(System.currentTimeMillis());
//            refreshListView.setLastUpdatedLabel(text);
//        }

//        private String formatDateTime(long time) {
//            if (0 == time) {
//                return "";
//            }
//            return mDateFormat.format(new Date(time));
//        }

        public void loadData() {
//            if (pageNum == 1) {
//                dataList.clear();
//            }
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", 1 + ""));
            params.add(new BasicNameValuePair("page_size", 10000 + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_REQUEST_CARD, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    boolean hasMoreData = true;
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject card = jsonArray.getJSONObject(i);
                                CardItem cardRequest = JsonObjectUtil.parseJsonObjectToCardItem(card);
                                dataList.add(cardRequest);
//                                if (gestureType == REFRESH_GESTURE) {
//                                    dataList.add(cardRequest);
//                                    setLastUpdateTime();
//                                } else {
//                                    dataList.add(cardRequest);
//                                }
//                                if (jsonArray.length() == 0) {
//                                    hasMoreData = false;
//                                }

//                                cardRequestAdapter.notifyDataSetChanged();
//                                if (gestureType == REFRESH_GESTURE)
//                                    refreshListView.onPullDownRefreshComplete();
//                                else
//                                    refreshListView.onPullUpRefreshComplete();
//                                refreshListView.setHasMoreData(hasMoreData);
                            }
                        }
                        cardRequestAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
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
