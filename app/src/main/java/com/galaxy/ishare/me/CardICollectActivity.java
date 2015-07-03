package com.galaxy.ishare.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.sharedcard.CardDetailActivity;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardICollectActivity extends ActionBarActivity {
    public static final String INTENT_ITEM_TO_DETAIL = "INTENT_ITEM_TO_DETAIL";

    private ActionBar actionBar;
    private ImageView addCardIv;

    private FrameLayout containerLayout;
    private PullToRefreshListView refreshListView;
    private ListView cardListView;
    private CardICollectAdapter cardAdapter;
    private List<CardItem> cardList = new ArrayList<>();

    private int gestureType;
    private int REFRESH_GESTURE = 1;
    private int LOAD_MORE_GESTURE = 2;
    public static final int pageSize = 12;
    public int pageNumber = 1;
    private HttpInteract httpInteract;

    private static final String TAG = "RequestFragment";
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_ishare);
        mContext = this;
        httpInteract = new HttpInteract();
        setActionBar();
        initWidget();
        refreshListView.doPullRefreshing(true, 500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBar() {
        actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.i_collect_action_bar, true);
        addCardIv = (ImageView) actionBar.getCustomView().findViewById(R.id.add_card_iv);
    }

    public void initWidget() {
        containerLayout = (FrameLayout) findViewById(R.id.card_container_layout);
        refreshListView = new PullToRefreshListView(this);
        cardAdapter = new CardICollectAdapter (cardList, this);
        initPullRefreshListView(refreshListView);
        containerLayout.addView(refreshListView);
    }

    private void initPullRefreshListView(PullToRefreshListView pullToRefreshListView) {
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setScrollLoadEnabled(true);
        cardListView = pullToRefreshListView.getRefreshableView();
//        cardListView.setDivider(null);// 设置不显示分割线
        cardListView.setAdapter(cardAdapter);

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < cardList.size()) {  // 防止点footer
                    Intent intent = new Intent(mContext, CardDetailActivity.class);
                    intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, cardList.get(position));
                    intent.putExtra(CardDetailActivity.PARAMETER_WHO_SEND, INTENT_ITEM_TO_DETAIL);
                    startActivity(intent);
                }
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
                cardList.clear();
            }

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", pageNum + ""));
            params.add(new BasicNameValuePair("page_size", pageSize + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_COLLECT_CARD, params, new HttpDataResponse() {
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

                                        JSONObject card = jsonArray.getJSONObject(i);

                                        CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);

                                        if (gestureType == REFRESH_GESTURE) {
                                            cardList.add(cardItem);
                                            setLastUpdateTime();
                                        } else {
                                            cardList.add(cardItem);
                                        }
                                    }
                                    if (jsonArray.length() == 0) {
                                        hasMoreData = false;
                                    }

                                    cardAdapter.notifyDataSetChanged();
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
