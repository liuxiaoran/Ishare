package com.galaxy.ishare.usercenter.me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by doqin on 2015/7/16.
 */
public class CardRequestTestActivity extends IShareActivity {

    public static final String CARDREQUEST_TO_PUBLISH = "CARDREQUEST_TO_PUBLISH";

    public ListView requestCardListView;
    public ImageView editCardImageView, deleteCardImageView;

    private Vector<CardRequest> dataList = new Vector<>();
    private CardRequestAdapter cardRequestAdapter;
    private HttpInteract httpInteract;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver updateReceiver;
    private static final String TAG = "CardRequestTestActivity";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_request);

        requestCardListView = (ListView) findViewById(R.id.usenter_request_listview);
        editCardImageView = (ImageView) findViewById(R.id.item_card_request_edit_iv);
        deleteCardImageView = (ImageView) findViewById(R.id.item_card_request_delete_iv);
        cardRequestAdapter = new CardRequestAdapter(this, dataList);

        IShareContext.getInstance().createActionbar(this, true, "我在找的卡");
        httpInteract = new HttpInteract();


        httpInteract.loadData();


        requestCardListView.setAdapter(cardRequestAdapter);
        requestCardListView.setDividerHeight(0);



        //修改了我请求的卡之后，更新列表
        localBroadcastManager = localBroadcastManager.getInstance(this);
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(BroadcastActionConstant.UPDATE_I_REQUEST_CARD)) {
                    dataList.clear();
                    httpInteract.loadData();
                }
            }
        };
        localBroadcastManager.registerReceiver(updateReceiver, new IntentFilter(BroadcastActionConstant.UPDATE_I_REQUEST_CARD));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(updateReceiver);
    }


    class HttpInteract {


        public void loadData() {

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", 1 + ""));
            params.add(new BasicNameValuePair("page_size", 10000 + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_REQUEST_CARD, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    boolean hasMoreData = true;
                    JSONObject jsonObject = null;
                    Log.v(TAG, "in");
                    try {
                        jsonObject = new JSONObject(result);
                        Log.v(TAG, result);
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject card = jsonArray.getJSONObject(i);
                                CardRequest cardRequest = JsonObjectUtil.parseJsonToCardRequest(card);
                                dataList.add(cardRequest);

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
