package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
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
import com.galaxy.ishare.database.CollectionDao;
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

public class CardICollectActivity extends IShareActivity {


    private ListView mCollectionListView;
    private ArrayList<CardItem> dataList;
    private CardListItemAdapter cardICollectAdapter;
    public static final String CARDCOLLECT_TO_DETAIL = "CARDCOLLECT_TO_DETAIL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_ishare);

        IShareContext.getInstance().createActionbar(this, true, "我的收藏");
        dataList = CollectionDao.getInstance(this).query();
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        mCollectionListView = (ListView) findViewById(R.id.usenter_ishare_listview);
        Vector collectionData = new Vector();
        for (int i = 0; i < dataList.size(); i++) {
            collectionData.add(dataList.get(i));
        }
        cardICollectAdapter = new CardListItemAdapter(collectionData, this);
        mCollectionListView.setAdapter(cardICollectAdapter);

        mCollectionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CardICollectActivity.this, CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, dataList.get(position));
                intent.putExtra(CardDetailActivity.PARAMETER_WHO_SEND, CARDCOLLECT_TO_DETAIL);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
