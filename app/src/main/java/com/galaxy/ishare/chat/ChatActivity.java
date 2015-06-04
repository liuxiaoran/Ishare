package com.galaxy.ishare.chat;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareApplication;
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
import com.galaxy.ishare.utils.DateUtil;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class ChatActivity extends ActionBarActivity {
    private Context mContext;
    private User user;
    private Order order;
    private ChatDao chatDao;

    private ActionBar actionBar;
    private ImageView btnBack;
    private TextView title;

    private ImageView shopImage;
    private TextView shopName;
    private TextView shopDistance;
    private TextView cardDiscount;
    private TextView cardType;
    private TextView orderState;
    private ImageView btnChangeStatus;

    private EditText etMsg;
    private TextView btnSend;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<Chat>();

    public static boolean isForeground = false;
    public static int PAGE_NUM = 20;
    private static final String TAG="ChatActivity";

    private String[] borrowStateItems;
    private String[] lendStateItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        user = IShareContext.getInstance().getCurrentUser();
        order = OrderManager.getInstance().order;
        chatDao = ChatDao.getInstance(mContext);

        borrowStateItems = mContext.getResources().getStringArray(R.array.borrow_state_items);
        lendStateItems = mContext.getResources().getStringArray(R.array.lend_state_items);


        setActionBar();
        initWidget();
        ChatManager.getInstance().addObserver(this);
    }

    public void setBundle() {
        Bundle bundle = getIntent().getExtras();
        String fromUser = null;
        String toUser = user.getUserId();
        if(bundle != null && bundle.size() != 0) {
            for (String key : bundle.keySet()) {
                if(key.equals(JPushInterface.EXTRA_EXTRA)) {
                    try {
                        String extra = bundle.getString(key);
                        JSONObject jsonObject = new JSONObject(extra);
                        if(jsonObject.has("open_id")) {
                            fromUser = jsonObject.getString("open_id");
                        }
                    } catch (JSONException e) {
                        Log.v(TAG,e.toString());
                        e.printStackTrace();
                    }
                }
            }
            showNewMessage(fromUser, toUser);
        }
    }

    public void setActionBar() {
        actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.chat_action_bar, false);
        btnBack = (ImageView) actionBar.getCustomView().findViewById(R.id.btn_back);
        title = (TextView) actionBar.getCustomView().findViewById(R.id.title);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        btnChangeStatus = (ImageView) findViewById(R.id.change_state);

        etMsg = (EditText) findViewById(R.id.chat_et_msg);
        btnSend = (TextView) findViewById(R.id.chat_btn_send);
        chatListView = (ListView) findViewById(R.id.chat_listview);

        String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 100), DisplayUtil.dip2px(mContext, 60));
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 100), DisplayUtil.dip2px(mContext, 60));
        ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        shopImage.setImageBitmap(loadedImage);
                    }
                });
        shopName.setText(order.shopName);
        shopDistance.setText(order.shopDistance + "");
        cardDiscount.setText(order.cardDiscount + "折");
        cardType.setText(CardTypeUtil.getCardType(order.cardType));
        if(user.getUserId().equals(order.borrowId)) {
            orderState.setText("状态： " +borrowStateItems[order.orderState]);
        } else {
            orderState.setText("状态： " + lendStateItems[order.orderState]);
        }


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chat chatMsg = new Chat();
                chatMsg.fromUser = user.getUserId();
                chatMsg.toUser = order.borrowId;
                chatMsg.type = 0;
                chatMsg.content = etMsg.getText().toString().trim();
                chatMsg.time = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");

//                chatDao.add(chatMsg);

                Log.d(TAG, chatList.size() + "$$$");
                chatList.add(chatMsg);
                Log.d(TAG, chatList.size() + "$$$");
                chatAdapter.notifyDataSetChanged();
                etMsg.setText("");
//                chatListView.setSelection(chatList.size() - 1);


                sendMsg(chatMsg);
            }
        });

        chatListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if(chatListView.getFirstVisiblePosition() == 0) {
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

        chatList.addAll(getChatData());
        if(OrderManager.getInstance().order.borrowId.equals(user.getUserId())) {
            chatAdapter = new ChatAdapter(mContext, chatList, order.borrowAvatar, order.lendAvatar);
        } else {
            chatAdapter = new ChatAdapter(mContext, chatList, order.lendAvatar, order.borrowAvatar);
        }

        chatListView.setAdapter(chatAdapter);
    }

    public void sendMsg(Chat chatMsg) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("open_id", user.getUserId()));
        params.add(new BasicNameValuePair("key", user.getKey()));
        params.add(new BasicNameValuePair("from_user", chatMsg.fromUser));
        params.add(new BasicNameValuePair("to_user", chatMsg.toUser));
        params.add(new BasicNameValuePair("type", chatMsg.type + ""));
        params.add(new BasicNameValuePair("content", chatMsg.content));

        HttpTask.startAsyncDataPostRequest(URLConstant.SEND_CHAT_MSG, params, new HttpDataResponse() {
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
                        handler.sendEmptyMessage(1);
                        Toast.makeText(mContext, "发送成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                Log.v(TAG, retCode.toString());

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
            }
        }
    };

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

    public List<Chat> getChatData() {
        String date = null;
        if(chatList.size() == 0) {
            date = DateUtil.getCurtime("yyyy-DD-mm hh:mm:ss");
        } else {
            date = chatList.get(0).time;
        }
        return chatDao.query(user.getUserId(), order.borrowId, date, PAGE_NUM);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBundle();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isForeground = false;
    }

    public void showNewMessage(String fromUser, String toUser) {
        Log.d(TAG, "showNewMessage");
        Log.d(TAG, order.borrowId);
        Log.d(TAG, fromUser);
        Log.d(TAG, toUser);
        if(order != null) {
            if(order.borrowId.equals(fromUser) || order.lendId.equals(fromUser)) {
                List<Chat> tmpList = chatDao.queryUnRead(fromUser, toUser);
                chatDao.updateunRead(tmpList);

                Log.d(TAG, chatList.size() + "asdsa");
                if(tmpList.size() != 0) {
                    chatList.addAll(tmpList);
                    chatAdapter.notifyDataSetChanged();
                }
                Log.d(TAG, chatList.size() + "asdfs");

                Log.d(TAG, isForeground + "showNewMessage");
                NotificationManager m_NotificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
                m_NotificationManager.cancelAll();
            }
        } else {

        }

    }
}
