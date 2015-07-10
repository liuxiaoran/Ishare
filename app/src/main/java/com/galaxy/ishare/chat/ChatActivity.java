package com.galaxy.ishare.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.ChatDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.order.OrderUtil;
import com.galaxy.ishare.utils.DateUtil;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends IShareActivity {
    public static ChatActivity instance;
    private Context mContext;
    private User user;
    private String fromUser = null;
    private String fromAvatar = null;
    private Order order = new Order();
    private ChatDao chatDao;

    private ActionBar actionBar;
    private TextView title;
    private ImageView gender;

    private ImageView shopImage;
    private TextView shopName;
    private TextView shopDistance;
    private TextView cardDiscount;
    private TextView cardType;
    private TextView orderState;
    private TextView btnChangeStatus;

    private View orderView;

    private EditText etMsg;
    private TextView btnSend;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();

    public static boolean isForeground = false;
    public static int PAGE_NUM = 20;
    private static final String TAG="ChatActivity";

    private String[] cardItems;
    private String[] borrowStateItems;
    private String[] borrowStateChange;
    private String[] lendStateItems;
    private String[] lendStateChange;

    private LinearLayout llcomment;
    private TextView tvComment;
    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnComment;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        instance = this;
        user = IShareContext.getInstance().getCurrentUser();
        chatDao = ChatDao.getInstance(mContext);

        cardItems = getResources().getStringArray(R.array.card_items);
        borrowStateItems = getResources().getStringArray(R.array.borrow_state_items);
        borrowStateChange = getResources().getStringArray(R.array.borrow_state_change);
        lendStateItems = getResources().getStringArray(R.array.lend_state_items);
        lendStateChange = getResources().getStringArray(R.array.lend_state_change);

        setActionBar();
        initWidget();
        getOrder();
        initChatData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionBar() {
        actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.chat_action_bar, true);
        title = (TextView) actionBar.getCustomView().findViewById(R.id.title);
        gender = (ImageView) actionBar.getCustomView().findViewById(R.id.gender);
        tvComment = (TextView) actionBar.getCustomView().findViewById(R.id.tv_comment);

        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llcomment.getVisibility() == View.VISIBLE) {
                    llcomment.setVisibility(View.GONE);
                }  else {
                    llcomment.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void initWidget() {
        shopImage = (ImageView) findViewById(R.id.shop_image);
        shopName = (TextView) findViewById(R.id.shop_name);
        shopDistance = (TextView) findViewById(R.id.shop_distance);
        cardDiscount = (TextView) findViewById(R.id.card_discount);
        cardType = (TextView) findViewById(R.id.card_type);
        orderState = (TextView) findViewById(R.id.order_state);
        btnChangeStatus = (TextView) findViewById(R.id.change_state);

        orderView = findViewById(R.id.order_info);

        etMsg = (EditText) findViewById(R.id.chat_et_msg);
        btnSend = (TextView) findViewById(R.id.chat_btn_send);
        chatListView = (ListView) findViewById(R.id.chat_listview);

        llcomment = (LinearLayout) findViewById(R.id.ll_comment);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        etComment = (EditText) findViewById(R.id.et_comment);
        btnComment = (Button) findViewById(R.id.btn_comment);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnChangeStatus.setVisibility(View.GONE);

                switch (order.orderState) {
                    case Order.CHAT_STATE://借出去
                        if(user.getUserId().equals(order.lendId)) {
                            updateOrderStatus(order.id, Order.LEND_STATE, order.borrowId);
                        }
                        break;
                    case Order.LEND_STATE://确认还卡
                        if(user.getUserId().equals(order.lendId)) {
                            updateOrderStatus(order.id, Order.RETURN_STATE, order.borrowId);
                        }
                        break;
                    case Order.RETURN_STATE://付款
                        if(user.getUserId().equals(order.borrowId)) {
                            updateOrderStatus(order.id, Order.PAID_STATE, order.lendId);
                        }
                        break;
                    case Order.PAID_STATE://确认付款
                        if(user.getUserId().equals(order.lendId)) {
                            updateOrderStatus(order.id, Order.END_STATE, order.borrowId);
                        }
                        break;
                }
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(etMsg.getText().toString().trim())) {
                    Toast.makeText(mContext, "请填写发送内容", Toast.LENGTH_LONG);
                } else {
                    Chat chatMsg = new Chat();
                    chatMsg.orderId = order.id;
                    chatMsg.fromUser = user.getUserId();
                    if (user.getUserId().equals(order.borrowId)) {
                        chatMsg.toUser = order.lendId;
                    } else {
                        chatMsg.toUser = order.borrowId;
                    }
                    chatMsg.orderId = order.id;
                    chatMsg.type = 1;
                    chatMsg.content = etMsg.getText().toString().trim();
                    chatMsg.time = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");
                    chatMsg.isRead = 1;
                    chatMsg.isSend = 0;
                    chatMsg.cardId = order.cardId;
                    chatMsg.cardType = order.type;
                    chatMsg.borrowId = order.borrowId;
                    chatMsg.lendId = order.lendId;

                    Log.e(TAG, "chatMsg.cardId: " + chatMsg.cardId);
                    Log.e(TAG, "chatMsg.cardType: " + chatMsg.cardType);

                    chatList.add(chatMsg);
                    chatDao.add(chatMsg);
                    chatAdapter.notifyDataSetChanged();
                    etMsg.setText("");
//                chatListView.setSelection(chatList.size() - 1);
                    sendMsg(chatMsg);
                }
            }
        });

        chatListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (chatListView.getFirstVisiblePosition() == 0) {
                            Log.d(TAG, "scrollState");
                            showChatRecords();
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    showChatRecords();
//                    Log.e("log", "滑到顶部");
//                }
//                if (visibleItemCount + firstVisibleItem == totalItemCount) {
//                    Log.e("log", "滑到底部");
//                }
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llcomment.setVisibility(View.GONE);
                float rating = ratingBar.getRating();
                String comment = etComment.getText().toString();
                ratingBar.setRating(0);
                etComment.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0); //强制隐藏键盘
                if (rating == 0.0 && "".equals(comment.trim())) {
                    Toast.makeText(mContext, "请填写评价内容", Toast.LENGTH_LONG).show();
                } else {
                    sendComment(rating, comment, order.cardId, user.getUserId());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0); //强制隐藏键盘
                llcomment.setVisibility(View.GONE);
            }
        });

//        listenerSoftInput();
    }

    private void listenerSoftInput() {
        final View activityRootView = findViewById(R.id.chat_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                        if (heightDiff > 300) { // 如果高度差超过300像素，就很有可能是有软键盘...
                            orderView.setVisibility(View.GONE);
                        } else {
                            orderView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void initOrderData() {
        if(order.borrowId.equals(user.getUserId())) {
            title.setText(order.lendName);
            if("女".equals(order.lendGender)) {
                gender.setImageResource(R.drawable.icon_female);
            } else {
                gender.setImageResource(R.drawable.icon_male);
            }
        } else {
            title.setText(order.borrowName);
            if("女".equals(order.borrowGender)) {
                gender.setImageResource(R.drawable.icon_female);
            } else {
                gender.setImageResource(R.drawable.icon_male);
            }
        }

        if(order.shopImage != null && order.shopImage.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 70), DisplayUtil.dip2px(mContext, 100));
            ImageLoader.getInstance().displayImage(thumbnailUrl, shopImage);
        } else {
            shopImage.setImageResource(R.drawable.load_empty);
        }

        setOrderDetail();
    }

    public List<Chat> getChatData() {
        List<Chat> tmpList = new ArrayList<>();
        String date = null;
        if(chatList.size() == 0) {
            date = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");
        } else {
            date = chatList.get(0).time;
        }

        Log.e(TAG, "date: " + date);

        tmpList.addAll(chatDao.query(order.id, date, PAGE_NUM));
        return tmpList;
    }

    public void initChatData() {
        insertHead(chatList, getChatData());
        chatAdapter = new ChatAdapter(mContext, chatList, fromAvatar, user.getAvatar());
        chatListView.setAdapter(chatAdapter);
    }

    public void insertHead(List<Chat> desList, List<Chat> srcList) {
        for(int i = 0; i < srcList.size(); i++) {
            desList.add(0, srcList.get(i));
        }
    }

    public void setOrderDetail() {
        if(order.shopImage != null && order.shopImage.length > 0) {
            String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
            ImageLoader.getInstance().displayImage(thumbnailUrl, shopImage);
        } else {
            shopImage.setImageResource(R.drawable.load_empty);
        }

        shopName.setText(order.shopName);
        shopDistance.setText(order.shopDistance + "");
        cardDiscount.setText(getStringDiscount(order.cardDiscount) + "折");
        cardType.setText(cardItems[order.wareType]);
        updateOrderInfo();
    }

    public void setBorrowInfo() {
        Log.e(TAG, "Log.e(TAG, \"order.orderState: \" + order.orderState);.oBorrowrderState: " + order.orderState);
        orderState.setText(borrowStateItems[order.orderState]);

        if(order.orderState == Order.RETURN_STATE) {
            btnChangeStatus.setText(borrowStateChange[0] + "");
            btnChangeStatus.setVisibility(View.VISIBLE);
        } else {
            btnChangeStatus.setVisibility(View.GONE);
        }
    }

    public void setLendInfo() {
        Log.e(TAG, "Lend.orderState: " + order.orderState);
        orderState.setText(lendStateItems[order.orderState]);

        switch (order.orderState) {
            case Order.CHAT_STATE:
                btnChangeStatus.setText(lendStateChange[order.orderState] + "");
                btnChangeStatus.setVisibility(View.VISIBLE);
                break;
            case Order.LEND_STATE:
                btnChangeStatus.setText(lendStateChange[order.orderState] + "");
                btnChangeStatus.setVisibility(View.VISIBLE);
                break;
            case Order.PAID_STATE:
                btnChangeStatus.setText(lendStateChange[order.orderState] + "");
                btnChangeStatus.setVisibility(View.VISIBLE);
                break;
            default:
                btnChangeStatus.setVisibility(View.GONE);
                break;
        }
    }

    public void updateOrderInfo() {
        if(user.getUserId().equals(order.borrowId)) {
            setBorrowInfo();
        } else {
            setLendInfo();
        }
    }

    public void sendMsg(final Chat chatMsg) {
        List<BasicNameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("from_user", chatMsg.fromUser));
        params.add(new BasicNameValuePair("to_user", chatMsg.toUser));
        params.add(new BasicNameValuePair("type", chatMsg.type + ""));
        params.add(new BasicNameValuePair("content", chatMsg.content));

        params.add(new BasicNameValuePair("order_id", chatMsg.orderId + ""));
        params.add(new BasicNameValuePair("card_id", chatMsg.cardId + ""));
        params.add(new BasicNameValuePair("card_type", chatMsg.cardType + ""));
        params.add(new BasicNameValuePair("borrow_id", chatMsg.borrowId + ""));
        params.add(new BasicNameValuePair("lend_id", chatMsg.lendId + ""));


        HttpTask.startAsyncDataPostRequest(URLConstant.SEND_CHAT_MSG, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        int orderId = jsonObject.getInt("order_id");
                        handler.sendEmptyMessage(1);
                        chatDao.updateUnSend(chatMsg);
                        chatDao.updateOrderId(orderId, chatMsg.fromUser, chatMsg.toUser, chatMsg.cardId);
                        Toast.makeText(mContext, "发送成功", Toast.LENGTH_LONG).show();
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.obj = chatMsg;
                        handler.sendMessageDelayed(msg, 3000);
                        Toast.makeText(mContext, "发送失败, 正在重发！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.v(TAG, "sendMsg: " + retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void updateOrderStatus(int id, final int orderStatus, String toId) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", id + ""));
        params.add(new BasicNameValuePair("status", orderStatus + ""));
        params.add(new BasicNameValuePair("to_id", toId + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_ORDER_STATE, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        Toast.makeText(mContext, "操作成功", Toast.LENGTH_LONG).show();
                        order.orderState = orderStatus;
                        ChatManager.getInstance().updateOrderList(order);
                        updateOrderInfo();
                        handler.sendEmptyMessage(4);
                    } else {
                        Log.e(TAG, "updateOrderStatus: " + jsonObject.toString());

                        Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, "updateOrderStatus1: " +retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    chatAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Chat chat = (Chat) msg.obj;
                    sendMsg(chat);
                    break;
                case 3:
                    initOrderData();
                    break;
                case 4:
                    updateOrderInfo();
                    break;
            }
        }
    };

    // 得到折扣的字符串标示，整数值不显示小数
    public String getStringDiscount(double discount) {
        double d = discount * 10;
        if (d % 10 == 0.0) {
            int ret = (int) discount;
            return Integer.toString(ret);
        }
        return Double.toString(discount);
    }

    public void showChatRecords() {
        List<Chat> tmpList = getChatData();
        if(tmpList != null) {
            if(tmpList.size() == 0) {
                Toast.makeText(mContext, "没有更多聊天记录", Toast.LENGTH_LONG).show();
            } else {
                tmpList.addAll(chatList);
                chatList = tmpList;
                if(chatAdapter != null) {
                    chatAdapter.setChatList(chatList);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        instance = null;
    }

    public void getOrder() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);

        Log.e(TAG,  "type: " + type);

        switch (type) {
            case 0:
                order = (Order) intent.getSerializableExtra("order");
                if(order.borrowId == user.getUserId()) {
                    fromUser = order.lendId;
                    fromAvatar = order.lendAvatar;
                    showNewMessage(order.lendId, order.lendAvatar, order.borrowId, order.id, order.cardId, order.type);
                } else {
                    fromUser = order.borrowId;
                    fromAvatar = order.borrowAvatar;
                    showNewMessage(order.borrowId, order.borrowAvatar, order.lendId, order.id, order.cardId, order.type);
                }
                initOrderData();
                break;
            case 1:
                order.cardId = intent.getIntExtra("cardId", 0);
                order.type = 1;
                Log.e(TAG, "order.cardId: " + order.cardId);
                Log.e(TAG, "order.type: " + order.type);
                fromUser = intent.getStringExtra("fromUser");
                fromAvatar = intent.getStringExtra("fromAvatar");
//                showNewMessage(fromUser, fromAvatar, user.getUserId(), order.id, order.cardId, order.type);
                getCardOrder2Activity(fromUser, user.getUserId(), order.cardId);
                break;
            case 2:
                order.cardId = intent.getIntExtra("requestId", 0);
                order.type = 2;
                fromUser = intent.getStringExtra("fromUser");
                fromAvatar = intent.getStringExtra("fromAvatar");
                showNewMessage(fromUser, fromAvatar, user.getUserId(), order.id, order.cardId, order.type);
                getRequestOrder2Activity(fromUser, user.getUserId(), order.cardId);
                break;
            case 3:
                order.id = intent.getIntExtra("orderId", 0);
                Log.e(TAG, "getOrder: " + order.id);
                order.cardId = intent.getIntExtra("cardId", 0);
                order.type = intent.getIntExtra("cardType", 0);
                fromUser = intent.getStringExtra("fromUser");
                fromAvatar = intent.getStringExtra("fromAvatar");
                showNewMessage(fromUser, fromAvatar, user.getUserId(), order.id, order.cardId, order.type);
                getOrder2Activity(order.id);
                break;
            case 4:
                order.id = intent.getIntExtra("orderId", 0);
                getOrder2Activity(order.id);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    public boolean isSameOrder(int orderId) {
        Log.e(TAG, "order.id: " + order.id);
        return order.id == orderId ? true : false;
    }

    public void showNewMessage(String fromUser, String fromAvatar, String toUser, int orderId, int cardId, int cardType) {
        List<Chat> tmpList = null;
        if(orderId != 0) {
            tmpList = chatDao.queryUnRead(fromUser, toUser, orderId);
        } else {
            tmpList = chatDao.queryUnRead(fromUser, toUser, cardId, cardType);
        }

        Log.e(TAG, "fromUser: " + fromUser);
        Log.e(TAG, "toUser: " + toUser);
        Log.e(TAG, "orderId: " + orderId);
        Log.e(TAG, tmpList.size() + " : order == null");

        chatDao.updateUnRead(tmpList);
        if(tmpList.size() != 0) {
            if(orderId == order.id) {
                chatList.addAll(tmpList);
            } else {
                chatList = tmpList;
            }
            chatAdapter = new ChatAdapter(mContext, chatList, fromAvatar, user.getAvatar());
            chatListView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();
        }

        NotificationManager m_NotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
        m_NotificationManager.cancelAll();
    }

    public void getCardOrder2Activity(String borrowId, String lendId, int cardId) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("borrow_id", borrowId + ""));
        params.add(new BasicNameValuePair("lend_id", lendId + ""));
        params.add(new BasicNameValuePair("card_id", cardId + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.CARD_RECORD_IS_EXIST, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                int status = 0;
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, "result: " + result);
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");

                    if (status == 0) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        order = OrderUtil.parserJSONObjectToOrder(jsonOrder);
                        ChatManager.getInstance().orderList.add(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else if (status == 1) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        order = OrderUtil.parserJSONObjectToOrder(jsonOrder);
                        ChatManager.getInstance().orderList.add(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, "getCardOrder2Activity: " + retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void getRequestOrder2Activity(String borrowId, String lendId, int requestId) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("borrow_id", borrowId + ""));
        params.add(new BasicNameValuePair("lend_id", lendId + ""));
        params.add(new BasicNameValuePair("request_id", requestId + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.REQUEST_RECORD_IS_EXIST, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                try {
                    Log.v(TAG, "result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");

                    Log.e(TAG, "status: " + status);
                    if (status == 0) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        order = OrderUtil.parserJSONObject2RequestOrder(jsonOrder);
                        ChatManager.getInstance().orderList.add(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else if(status == 1) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        order = OrderUtil.parserJSONObject2RequestOrder(jsonOrder);
                        ChatManager.getInstance().orderList.add(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.v(TAG, "getRequestOrder2Activity: " + retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void getOrder2Activity(int orderId) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("id", orderId + ""));

        Log.e(TAG, "orderId: " + orderId);

        HttpTask.startAsyncDataPostRequest(URLConstant.GET_ORDER, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                int status = 0;
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        JSONObject jsonOrder = jsonObject.getJSONObject("data");
                        order = OrderUtil.parserJSONObject2Order(jsonOrder);
                        ChatManager.getInstance().updateOrderList(order);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        handler.sendMessage(msg);
                    }  else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, "getOrder2Activity: " + retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void sendComment(float rating, String content, int cardId, String openId) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("card_id", String.valueOf(cardId)));
        params.add(new BasicNameValuePair("open_id", openId));
        params.add(new BasicNameValuePair("comment", content));
        params.add(new BasicNameValuePair("rating", String.valueOf(rating)));

        HttpTask.startAsyncDataPostRequest(URLConstant.ADD_CARD_COMMENT, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                int status = 0;
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        Toast.makeText(mContext, "评论成功！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "服务器错误！", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, "getOrder2Activity: " + retCode.toString());

                Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
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
