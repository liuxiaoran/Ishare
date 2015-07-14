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

public class CardIshareActivity extends IShareActivity {
    public static final String INTENT_ITEM_TO_DETAIL = "INTENT_ITEM_TO_DETAIL";



    private HttpInteract httpInteract;


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_ishare);

        mContext = this;
        httpInteract = new HttpInteract();
        IShareContext.getInstance().createActionbar(this, true, "我分享的卡");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }




    class HttpInteract {



        public void loadData(int pageNum) {


            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("page_num", pageNum + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.GET_I_SHARE_CARD, params, new HttpDataResponse() {
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

                                        CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);

                                    }
                                    if (jsonArray.length() == 0) {
                                        hasMoreData = false;
                                    }


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
