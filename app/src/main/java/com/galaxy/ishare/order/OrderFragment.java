package com.galaxy.ishare.order;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.chat.ChatActivity;
import com.galaxy.ishare.chat.OrderManager;
import com.galaxy.ishare.chat.OrderUtil;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.sharedcard.PullToRefreshBase;
import com.galaxy.ishare.sharedcard.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderFragment extends Fragment {

    private final String TAG = "StateFragmene";

    private User user;

    // 判断是下拉刷新还是加载更多的操作
    private int gestureType;
    private static final int REFRESH_GESTURE = 1;
    private static final int LOAD_MORE_GESTURE = 2;

    public static final String PARAMETER_ODER_TYPE = "orderType";

    private int orderType;
    public static final int BORROW_ORDER = 1;
    public static final int LEND_ORDER = -1;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private View root;

    private FrameLayout listViewFrameLayout;

    List<Order> orderLendList = new ArrayList<>();
    private OrderAdapter orderLendAdapter;
    private ListView orderListView;
    List<Order> orderBorrowList = new ArrayList<>();
    private OrderAdapter orderBorrowAdapter;
    private PullToRefreshListView pullToRefreshListView;
    public RelativeLayout loadingLayout;

    private HttpInteract httpInteract;
    private int pageNumber = 1;
    private final int pageSize = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        root = lf.inflate(R.layout.fragment_order, null);
        user = IShareContext.getInstance().getCurrentUser();
        initWidget();
        setAdapter();

        return root;
    }

    public void initWidget() {
        pullToRefreshListView = new PullToRefreshListView(getActivity());
        initPullToRefreshListView(pullToRefreshListView);

        listViewFrameLayout = (FrameLayout) root.findViewById(R.id.order_listview_framelayout);
        listViewFrameLayout.addView(pullToRefreshListView, 0);
    }

    public void initPullToRefreshListView(PullToRefreshListView mPullListView) {
        httpInteract = new HttpInteract();
        mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
        orderListView = mPullListView.getRefreshableView();
        orderListView.setDivider(null);// 设置不显示分割线
        orderListView.setAdapter(orderBorrowAdapter);

        orderBorrowAdapter = new OrderAdapter(getActivity(), orderBorrowList);
        orderLendAdapter = new OrderAdapter(getActivity(), orderLendList);

        orderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                OrderManager.getInstance().order = orderLendList.get(position);
                startActivity(intent);
            }
        });

        mPullListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = REFRESH_GESTURE;
                pageNumber = 1;
                if (orderType == BORROW_ORDER) {
                    httpInteract.loadData(user.getUserId(), null, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), 0, pageNumber, pageSize);
                } else {
                    httpInteract.loadData(null, user.getUserId(), IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), 0, pageNumber, pageSize);
                }

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                gestureType = LOAD_MORE_GESTURE;
                pageNumber++;
                if (orderType == BORROW_ORDER) {
                    httpInteract.loadData(user.getUserId(), null, IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), 0, pageNumber, pageSize);
                } else {
                    httpInteract.loadData(null, user.getUserId(), IShareContext.getInstance().getUserLocation().getLongitude(),
                            IShareContext.getInstance().getUserLocation().getLatitude(), 0, pageNumber, pageSize);
                }
            }
        });

    }

    public void setAdapter() {
        Bundle bundle = getArguments();
        orderType = bundle.getInt(PARAMETER_ODER_TYPE);
        if (orderType == 1) {
            orderListView.setAdapter(orderBorrowAdapter);
        } else {
            orderListView.setAdapter(orderLendAdapter);
        }
    }

    private void setLastUpdateTime() {
        String text = formatDateTime(System.currentTimeMillis());
        pullToRefreshListView.setLastUpdatedLabel(text);
    }

    private String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class HttpInteract {


        public void loadData(String borrowId, String lendId, double longitude, double latitude, int type, final int pageNumber, int pageSize) {

            if (pageNumber == 1)
                loadingLayout.setVisibility(View.VISIBLE);


            List<NameValuePair> paramsList = new ArrayList<>();
            paramsList.add(new BasicNameValuePair("borrow_id", borrowId + ""));
            paramsList.add(new BasicNameValuePair("lend_id", lendId + ""));
            paramsList.add(new BasicNameValuePair("longitude", longitude + ""));
            paramsList.add(new BasicNameValuePair("latitude", latitude + ""));
            paramsList.add(new BasicNameValuePair("type", type + ""));
            paramsList.add(new BasicNameValuePair("page_num", pageNumber + ""));
            paramsList.add(new BasicNameValuePair("page_size", pageSize + ""));
            String url = null;

            HttpTask.startAsyncDataGetRequset(url, paramsList, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    loadingLayout.setVisibility(View.GONE);

                    try {

                        boolean hasMoreData = true;
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        Log.v(TAG, "result" + result);
                        if (status == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            Log.v(TAG, "size" + jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tmpJson = jsonArray.getJSONObject(i);

                                Order order = OrderUtil.parserJSONObject2Order(tmpJson);
                                Log.v(TAG, order.toString());
                                if (gestureType == REFRESH_GESTURE) {
                                    if (orderType == BORROW_ORDER) {
                                        orderBorrowList.add(0, order);
                                    } else {
                                        orderLendList.add(0, order);
                                    }
                                } else {
                                    if (orderType == BORROW_ORDER) {
                                        orderBorrowList.add(order);
                                    } else {
                                        orderLendList.add(order);
                                    }
                                }
                            }
                            if (jsonArray.length() == 0) {

                                hasMoreData = false;
                            }

                            orderLendAdapter.notifyDataSetChanged();
                            if (gestureType == REFRESH_GESTURE) {
                                pullToRefreshListView.onPullDownRefreshComplete();
                            } else {
                                pullToRefreshListView.onPullUpRefreshComplete();
                            }

                            pullToRefreshListView.setHasMoreData(hasMoreData);
                            setLastUpdateTime();

//                            recoveryAllClickable();
                        } else {
                            Log.v(TAG, "status is " + status);
                        }
                    } catch (Exception e) {
                        Log.v(TAG, e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, "error" + retCode);
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
