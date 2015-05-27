package com.galaxy.ishare.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

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

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/26.
 */
public class SearchResultListFragment extends Fragment {


    public static int WHAT_NO_RESULT = 1;
    public static final String PARAMETER_KEY_WORD = "PARAMETER_KEY_WORD";

    private static final int REFRESH_GESTURE=2;
    private static final int LOAD_MORE_GESTURE=3;
    int pageNumber;

    private FrameLayout resultFrameLayout;

    private PullToRefreshListView resultRefreshListView;

    private ListView resultListView;

    private CardListItemAdapter itemAdapter;

    private LinkedList<CardItem> resultDataList;

    private int gestureType;

    private int pageSize=6;

    private HttpInteract httpInteract;

    private String keyWord;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private LinearLayout noResultLayout;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_NO_RESULT) {
                noResultLayout.setVisibility(View.VISIBLE);
            }
            super.handleMessage(msg);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View  resultView  =  inflater.inflate(R.layout.main_search_result_fragment,null);

        httpInteract = new HttpInteract();

        resultFrameLayout = (FrameLayout) resultView.findViewById(R.id.main_search_result_framelayout);

        resultDataList = new LinkedList();
        itemAdapter  = new CardListItemAdapter(resultDataList,getActivity());

        resultRefreshListView = new PullToRefreshListView(getActivity());
        initPullToRefreshListView(resultRefreshListView);

        keyWord = getKeyWordFromActivity();
        httpInteract.search(keyWord, pageNumber);


        noResultLayout = (LinearLayout) resultView.findViewById(R.id.search_no_more_layout);
        noResultLayout.setVisibility(View.INVISIBLE);


        FrameLayout resultLayout = (FrameLayout) resultView.findViewById(R.id.main_search_result_framelayout);
        resultLayout.addView(resultRefreshListView);

        return resultView;


    }

    // 返回activity 传过来的keyword
    public String getKeyWordFromActivity() {
        String keyword;
        Bundle bundle = getArguments();
        keyword = bundle.getString(PARAMETER_KEY_WORD);
        return keyword;
    }

    public void initPullToRefreshListView(PullToRefreshListView resultRefreshListView){
        resultRefreshListView.setPullLoadEnabled(false);
        resultRefreshListView.setScrollLoadEnabled(true);
        resultListView = resultRefreshListView.getRefreshableView();
        resultListView.setAdapter(itemAdapter);

        // listview 条目点击
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                intent.putExtra(CardDetailActivity.PARAMETER_CARD_ITEM, resultDataList.get(position));
                startActivity(intent);
            }
        });

        resultRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                httpInteract.search(keyWord, pageNumber);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
                httpInteract.search(keyWord, pageNumber);

            }
        });
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
            params.add(new BasicNameValuePair("keyword",keyWord));
            params.add(new BasicNameValuePair("trade_type",-1+""));
            params.add(new BasicNameValuePair("longitude",IShareContext.getInstance().getUserLocation().getLongitude()+""));
            params.add(new BasicNameValuePair("latitude",IShareContext.getInstance().getUserLocation().getLatitude()+""));
            params.add(new BasicNameValuePair("page_num",pageNumber+""));
            params.add(new BasicNameValuePair("page_size",pageSize+""));
            HttpTask.startAsyncDataGetRequset(URLConstant.GET_DISTANCE_CARD_LIST, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {

                    try {
                        boolean hasMoreData = true;
                        JSONObject jsonObject =  new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status==0){

                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i=0;i<data.length();i++){
                                JSONObject card= data.getJSONObject(i);
                                CardItem cardItem = JsonObjectUtil.parseJsonObjectToCardItem(card);
                                if (gestureType == REFRESH_GESTURE) {
                                    resultDataList.addFirst(cardItem);
                                    itemAdapter.notifyDataSetChanged();
                                    resultRefreshListView.onPullDownRefreshComplete();

                                } else {
                                    resultDataList.add(cardItem);
                                    itemAdapter.notifyDataSetChanged();
                                    resultRefreshListView.onPullUpRefreshComplete();
                                }
                            }
                            if (data.length() == 0) {

                                hasMoreData = false;

                                // 如果是第一页并且data的length是0，则没有数据
                                if (pageNumber == 1) {
                                    handler.sendEmptyMessage(WHAT_NO_RESULT);
                                }

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
