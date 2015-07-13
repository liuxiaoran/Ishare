package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.galaxy.ishare.user_request.RequestDetailActivity;
import com.galaxy.ishare.user_request.RequestListAdapter;
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class CardRequestActivity extends IShareActivity {
    private static String TAG = "CardRequestActivity";

    private FrameLayout containerLayout;
    private PullToRefreshListView refreshListView;
    private ListView requestListView;
    private RequestListAdapter cardRequestAdapter;
    private Vector<CardItem> requestList = new Vector<>();

    private int gestureType;
    private int REFRESH_GESTURE = 1;
    private int LOAD_MORE_GESTURE = 2;
    public static final int pageSize = 12;
    public int pageNumber = 1;
    private HttpInteract httpInteract;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_request);
        mContext = this;
        httpInteract = new HttpInteract();
        setActionBar();
        initWidget();
        refreshListView.doPullRefreshing(true, 500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
//            Intent intent = new Intent(this, CardOwnerAvailableAddActivity.class);
//            startActivityForResult(intent, SHOW_TO_ADD_REQUEST_CODE);
        } else if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_owner_location_setting, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void setActionBar() {
        ActionBar actionBar = IShareContext.getInstance().createDefaultHomeActionbar(this, "我要找的卡");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initWidget() {
        containerLayout = (FrameLayout) findViewById(R.id.card_request_container_layout);
        refreshListView = new PullToRefreshListView(this);
        cardRequestAdapter = new RequestListAdapter(mContext, requestList);
        initPullRefreshListView(refreshListView);
        containerLayout.addView(refreshListView);
    }

    private void initPullRefreshListView(PullToRefreshListView pullToRefreshListView) {
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setScrollLoadEnabled(true);
        requestListView = pullToRefreshListView.getRefreshableView();
//        cardListView.setDivider(null);// 设置不显示分割线
        requestListView.setAdapter(cardRequestAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, RequestDetailActivity.class);
                intent.putExtra(RequestDetailActivity.PARAMETER_REQUEST, requestList.get(position));
                startActivity(intent);
            }
        });

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                httpInteract.loadData(pageNumber);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
                httpInteract.loadData(pageNumber);

            }
        });
    }

    class HttpInteract {

        private void setLastUpdateTime() {
            String text = formatDateTime(System.currentTimeMillis());
            refreshListView.setLastUpdatedLabel(text);
        }

        private String formatDateTime(long time) {
            if (0 == time) {
                return "";
            }

            return mDateFormat.format(new Date(time));
        }

        public void loadData(int pageNum) {

            if (pageNum == 1) {
                requestList.clear();
            }

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", pageNum + ""));
            params.add(new BasicNameValuePair("page_size", pageSize + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_SHARE_CARD, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {
                            boolean hasMoreData = true;
                            JSONObject jsonObject = null;
                            Log.v(TAG, "result: " + result);
                            try {
                                jsonObject = new JSONObject(result);
                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject requestJson = jsonArray.getJSONObject(i);

                                        CardItem cardRequest = JsonObjectUtil.parseJsonObjectToCardItem(requestJson);

                                        if (gestureType == REFRESH_GESTURE) {
                                            requestList.add(cardRequest);
                                            setLastUpdateTime();
                                        } else {
                                            requestList.add(cardRequest);
                                        }
                                    }
                                    if (jsonArray.length() == 0) {
                                        hasMoreData = false;
                                    }

                                    cardRequestAdapter.notifyDataSetChanged();
                                    if (gestureType == REFRESH_GESTURE)
                                        refreshListView.onPullDownRefreshComplete();
                                    else
                                        refreshListView.onPullUpRefreshComplete();

                                    refreshListView.setHasMoreData(hasMoreData);
                                }
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
                    }
            );
        }
    }

}
