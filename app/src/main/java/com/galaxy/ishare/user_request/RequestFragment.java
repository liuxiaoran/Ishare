package com.galaxy.ishare.user_request;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;
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
import java.util.List;

/**
 * Created by liuxiaoran on 15/6/2.
 */
public class RequestFragment extends Fragment {

    private PullToRefreshListView refreshListView;
    private ListView requestListView;
    private RequestListAdapter requestListAdapter;
    private ArrayList<CardItem> dataList;

    private int gestureType;
    private int REFRESH_GESTURE = 1;
    private int LOAD_MORE_GESTURE = 2;
    public static final int pageSize = 12;
    public int pageNumber = 1;
    private HttpInteract httpInteract;

    private static final String TAG = "RequestFragment";

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.request_fragment, null);
        FrameLayout containerLayout = (FrameLayout) view.findViewById(R.id.request_container_layout);

        initViews(view);
        dataList = new ArrayList<>();
        requestListAdapter = new RequestListAdapter(getActivity(), dataList);
        httpInteract = new HttpInteract();
        initPullRefreshListView(refreshListView);


        containerLayout.addView(refreshListView);
        refreshListView.doPullRefreshing(true, 500);
        return view;
    }

    private void initViews(View view) {

        refreshListView = new PullToRefreshListView(getActivity());

    }

    private void initPullRefreshListView(PullToRefreshListView pullToRefreshListView) {
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setScrollLoadEnabled(true);
        requestListView = pullToRefreshListView.getRefreshableView();
        requestListView.setDivider(null);// 设置不显示分割线
        requestListView.setAdapter(requestListAdapter);

        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                httpInteract.loadData(IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
                httpInteract.loadData(IShareContext.getInstance().getUserLocation().getLongitude(),
                        IShareContext.getInstance().getUserLocation().getLatitude(), pageNumber);

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

        public void loadData(double longitude, double latitude, int currentPageNumber) {

            if (currentPageNumber == 1) {
                dataList.clear();
            }

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_longitude", longitude + ""));
            params.add(new BasicNameValuePair("user_latitude", latitude + ""));
            params.add(new BasicNameValuePair("page_num", currentPageNumber + ""));
            params.add(new BasicNameValuePair("page_size", pageSize + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.REQUEST_CARD_GET, params, new HttpDataResponse() {
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
                                            dataList.add(cardItem);
                                            setLastUpdateTime();
                                        } else {
                                            dataList.add(cardItem);
                                        }
                                    }
                                    if (jsonArray.length() == 0) {

                                        hasMoreData = false;
                                    }


                                    requestListAdapter.notifyDataSetChanged();
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
