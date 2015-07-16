package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.sharedcard.CardDetailActivity;
import com.galaxy.ishare.sharedcard.CardListItemAdapter;
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
import java.util.Vector;

public class CardIshareActivity extends IShareActivity {
    public static final String INTENT_ITEM_TO_DETAIL = "INTENT_ITEM_TO_DETAIL";

    private HttpInteract httpInteract;

    public ListView shareCardsListView;

    private Vector<CardItem> dataList;
    private CardListItemAdapter cardListItemAdapter;

    public static final String CARDISHARE_TO_DETAIL = "CARDISHARE_TO_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_ishare);

        shareCardsListView = (ListView) findViewById(R.id.usenter_ishare_listview);
        httpInteract = new HttpInteract();
        dataList = new Vector<>();
        IShareContext.getInstance().createActionbar(this, true, "我分享的卡");

        httpInteract.loadData();

        cardListItemAdapter = new CardListItemAdapter(dataList, this, false);
        shareCardsListView.setAdapter(cardListItemAdapter);

        shareCardsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CardIshareActivity.this, CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, dataList.get(position));
                intent.putExtra(CardDetailActivity.PARAMETER_WHO_SEND, CARDISHARE_TO_DETAIL);
                startActivityForResult(intent, CardDetailActivity.CARDISHARE_TO_CARD_DETAIL_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        dataList.clear();
        httpInteract.loadData();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class HttpInteract {

        public void loadData() {


            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", 1 + ""));
            params.add(new BasicNameValuePair("page_size", 100000 + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_SHARE_CARD, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(result);
                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject card = jsonArray.getJSONObject(i);
                                        CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);
                                        dataList.add(cardItem);
                                    }

                                }
                                cardListItemAdapter.notifyDataSetChanged();
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
