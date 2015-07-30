package com.galaxy.ishare.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
import com.galaxy.ishare.utils.JsonObjectUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/14.
 */
public class SearchActivity extends IShareActivity {

    public static final String INTET_SEARCH_TO_DETAIL = "INTET_SEARCH_TO_DETAIL";

    public static int WHAT_NO_RESULT = 1;
    public static final String PARAMETER_KEY_WORD = "PARAMETER_KEY_WORD";
    public static final String PARAMETER_SEARCH_BTN_CLICK = "PARAMETER_SEARCH_BTN_CLICK";
    private static final int REFRESH_GESTURE = 2;
    private static final int LOAD_MORE_GESTURE = 3;
    int pageNumber = 1;

    private TextView searchTv;
    private EditText contentEt;
    private LinearLayout adLayout;

    private FrameLayout containerLayout;

    private PullToRefreshListView resultRefreshListView;

    private ListView resultListView;

    private CardListItemAdapter itemAdapter;

    private Vector<CardItem> resultDataList;

    private int gestureType;

    private int pageSize = 6;

    private HttpInteract httpInteract;


    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");


    private LinearLayout waitingLayout, noResultLayout;

    private static final String TAG = "SearchActivity";


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_NO_RESULT) {
                noResultLayout.setVisibility(View.VISIBLE);
                waitingLayout.setVisibility(View.INVISIBLE);
                resultRefreshListView.setVisibility(View.INVISIBLE);
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search_activity);

        android.support.v7.app.ActionBar actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.main_search_actionbar, true);

        searchTv = (TextView) actionBar.getCustomView().findViewById(R.id.search_tv);
        contentEt = (EditText) actionBar.getCustomView().findViewById(R.id.search_et);
        contentEt.setHint("搜索店铺等");
        contentEt.setHintTextColor(getResources().getColor(R.color.dark_hint_text));
        adLayout = (LinearLayout) findViewById(R.id.main_search_ad_layout);



        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adLayout.setVisibility(View.INVISIBLE);


                if (!contentEt.getText().toString().equals("") && !contentEt.getText().toString().contains("\n")) {
                    search(contentEt.getText().toString());
                }


            }
        });
        contentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!contentEt.getText().toString().equals("")) {
                        search(contentEt.getText().toString());
                    }
                }
                return true;
            }
        });

        searchTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!contentEt.getText().toString().equals("")) {
                    search(contentEt.getText().toString());
                }

            }
        });


        httpInteract = new HttpInteract();


        resultDataList = new Vector();
        itemAdapter = new CardListItemAdapter(resultDataList, this);

        resultRefreshListView = new PullToRefreshListView(this);
        initPullToRefreshListView(resultRefreshListView);


        noResultLayout = (LinearLayout) findViewById(R.id.search_no_more_layout);
        noResultLayout.setVisibility(View.INVISIBLE);

        waitingLayout = (LinearLayout) findViewById(R.id.main_search_waiting_layout);

        containerLayout = (FrameLayout) findViewById(R.id.main_search_container_framelayout);
        containerLayout.addView(resultRefreshListView);


    }

    public void search(String keyWord) {

        resultDataList.clear();

        // 等待Layout可见,没结果layout不可见，resultList 不可见
        waitingLayout.setVisibility(View.VISIBLE);
        noResultLayout.setVisibility(View.INVISIBLE);
        resultRefreshListView.setVisibility(View.INVISIBLE);

        httpInteract.search(keyWord, pageNumber);

    }

    public void initPullToRefreshListView(PullToRefreshListView resultRefreshListView) {
        resultRefreshListView.setPullLoadEnabled(false);

        // 不能加载更多
        resultRefreshListView.setScrollLoadEnabled(false);
        // 不能下拉刷新
        resultRefreshListView.setPullRefreshEnabled(false);


        resultListView = resultRefreshListView.getRefreshableView();
        resultListView.setAdapter(itemAdapter);


        // listview 条目点击
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchActivity.this, CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, resultDataList.get(position));
                intent.putExtra(CardDetailActivity.PARAMETER_WHO_SEND, INTET_SEARCH_TO_DETAIL);
                startActivity(intent);
            }
        });

//        resultRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                gestureType = REFRESH_GESTURE;
//                pageNumber = 1;
//                httpInteract.search(keyWord, pageNumber);
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//                gestureType = LOAD_MORE_GESTURE;
//                pageNumber++;
//                httpInteract.search(keyWord, pageNumber);
//
//            }
//        });
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        resultRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }

        return mDateFormat.format(new Date(time));
    }


    class HttpInteract {

        public void search(String keyWord, final int pageNumber) {

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("keyword", keyWord));
            params.add(new BasicNameValuePair("longitude", IShareContext.getInstance().getUserLocation().getLongitude() + ""));
            params.add(new BasicNameValuePair("latitude", IShareContext.getInstance().getUserLocation().getLatitude() + ""));
            params.add(new BasicNameValuePair("page_num", pageNumber + ""));
            params.add(new BasicNameValuePair("page_size", pageSize + ""));
            HttpTask.startAsyncDataGetRequset(URLConstant.SEARCH, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {

                    try {
                        boolean hasMoreData = true;
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        Log.v(TAG, "result: " + result.toString());

                        // 等待结束，不显示等待layout
                        waitingLayout.setVisibility(View.INVISIBLE);

                        if (status == 0) {

                            JSONArray data = jsonObject.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject card = data.getJSONObject(i);
                                CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);
                                if (gestureType == REFRESH_GESTURE) {
                                    resultDataList.add(cardItem);

                                } else {
                                    resultDataList.add(cardItem);
                                }
                            }
                            if (data.length() == 0) {

                                hasMoreData = false;

                                // 如果是第一页并且data的length是0，则没有数据
                                if (pageNumber == 1) {
                                    Log.v(TAG, "no data   ");
                                    handler.sendEmptyMessage(WHAT_NO_RESULT);
                                }

                            } else {
                                itemAdapter.notifyDataSetChanged();
                                resultRefreshListView.onPullDownRefreshComplete();
                                //如果有数据，显示列表
                                resultRefreshListView.setVisibility(View.VISIBLE);
                            }

                            resultRefreshListView.setHasMoreData(hasMoreData);
                            setLastUpdateTime();


                        }
                    } catch (JSONException e) {
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
