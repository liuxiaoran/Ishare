package com.galaxy.ishare.user_request;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.IShareFragment;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardItem;
import com.galaxy.ishare.model.CardRequest;
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

/**
 * Created by liuxiaoran on 15/6/2.
 */
public class RequestFragment extends IShareFragment {

    private PullToRefreshListView refreshRefreshView;
    private ListView requestListView;
    private RequestListAdapter requestListAdapter;
    private Vector<CardRequest> dataList;

    private int gestureType;
    private int REFRESH_GESTURE = 1;
    private int LOAD_MORE_GESTURE = 2;
    public static final int pageSize = 12;
    public int pageNumber = 1;
    private HttpInteract httpInteract;

    private static final String TAG = "RequestFragment";

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver;
    int receiveBroadcastCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.request_fragment, null);
        FrameLayout containerLayout = (FrameLayout) view.findViewById(R.id.request_container_layout);


        dataList = new Vector<>();
        requestListAdapter = new RequestListAdapter(getActivity(), dataList);
        httpInteract = new HttpInteract();

        refreshRefreshView = new PullToRefreshListView(getActivity());
        initPullRefreshListView(refreshRefreshView);

        if (IShareContext.getInstance().getUserLocation() == null) {
            refreshRefreshView.setVisibility(View.INVISIBLE);
        } else {

            refreshRefreshView.setVisibility(View.VISIBLE);

        }

        // 接收位置更新广播
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 不知为何会收到两次，所以在第二次的时候不执行内部代码
                if (receiveBroadcastCount == 0) {
                    refreshRefreshView.setVisibility(View.VISIBLE);

                    refreshRefreshView.doPullRefreshing(true, 500);
                    receiveBroadcastCount++;
                }

            }
        };
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastActionConstant.UPDATE_USER_LOCATION));


        containerLayout.addView(refreshRefreshView, 0);
        refreshRefreshView.doPullRefreshing(true, 500);
        return view;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        localBroadcastManager.unregisterReceiver(receiver);

    }


    private void initPullRefreshListView(final PullToRefreshListView pullToRefreshListView) {
        pullToRefreshListView.setPullLoadEnabled(false);
        pullToRefreshListView.setScrollLoadEnabled(true);
        requestListView = (ListView) pullToRefreshListView.getRefreshableView();
//        requestListView.setDivider(null);// 设置不显示分割线
        requestListView.setAdapter(requestListAdapter);
        requestListView.setSelector(R.color.window_background);// 设置listview 条目选中的背景

//        requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent intent = new Intent(getActivity(), RequestDetailActivity.class);
//                intent.putExtra(RequestDetailActivity.PARAMETER_REQUEST, dataList.get(position));
//                startActivity(intent);
//            }
//        });

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.v(TAG, "pull down");
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                dataList.clear();

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
            refreshRefreshView.setLastUpdatedLabel(text);
        }

        private String formatDateTime(long time) {
            if (0 == time) {
                return "";
            }

            return mDateFormat.format(new Date(time));
        }

        public void loadData(double longitude, double latitude, int currentPageNumber) {


            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_longitude", longitude + ""));
            params.add(new BasicNameValuePair("user_latitude", latitude + ""));
            params.add(new BasicNameValuePair("page_num", currentPageNumber + ""));
            params.add(new BasicNameValuePair("page_size", pageSize + ""));

            HttpTask.startAsyncDataPostRequest(URLConstant.REQUEST_CARD_GET, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {
                            Log.v(TAG, "arrvie ok ");
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

                                        CardRequest cardRequest = JsonObjectUtil.parseJsonToCardRequest(card);

                                        if (gestureType == REFRESH_GESTURE) {
                                            dataList.add(cardRequest);
                                            setLastUpdateTime();
                                        } else {
                                            dataList.add(cardRequest);
                                        }
                                    }
                                    if (jsonArray.length() == 0) {

                                        hasMoreData = false;
                                    }

                                    requestListAdapter.notifyDataSetChanged();
                                    if (gestureType == REFRESH_GESTURE)
                                        refreshRefreshView.onPullDownRefreshComplete();
                                    else
                                        refreshRefreshView.onPullUpRefreshComplete();

                                    refreshRefreshView.setHasMoreData(hasMoreData);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                        @Override
                        public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                            Log.v(TAG, "error http");
                        }

                        @Override
                        public void onRecvCancelled(HttpRequestBase request) {
                            Log.v(TAG, "error 3 http");
                        }

                        @Override
                        public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                            Log.v(TAG, "error2 http");
                        }
                    }

            );

        }


    }
}
