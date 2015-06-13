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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.OrderConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.ChatDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.Order;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.order.OrderManager;
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

public class ChatActivity extends ActionBarActivity {
    private Context mContext;
    private User user;
    private Order order;
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
    private ImageView btnChangeStatus;

    private View orderView;
    private View firstView;
    private View secondView;
    private View thirdView;
    private View forthView;
    private TextView firstContent;
    private TextView firstDate;
    private TextView firstConfirm;
    private TextView secondContent;
    private TextView secondDate;
    private TextView secondConfirm;
    private TextView thirdContent;
    private TextView thirdDate;
    private TextView thirdConfirm;
    private TextView forthContent;
    private TextView forthDate;
    private TextView forthConfirm;

    private EditText etMsg;
    private TextView btnSend;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<Chat>();

    public static boolean isForeground = false;
    public static int PAGE_NUM = 20;
    private static final String TAG="ChatActivity";

    private String[] cardItems;
    private String[] borrowStateItems;
    private String[] lendStateItems;

    private String[] borrowStateDetailBegin;
    private String[] borrowStateDetailEnd;
    private String[] lendStateDetailBegin;
    private String[] lendStateDetailEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mContext = this;
        user = IShareContext.getInstance().getCurrentUser();
        order = (Order) getIntent().getSerializableExtra("order");
        chatDao = ChatDao.getInstance(mContext);

        cardItems = getResources().getStringArray(R.array.card_items);
        borrowStateItems = getResources().getStringArray(R.array.borrow_state_items);
        lendStateItems = getResources().getStringArray(R.array.lend_state_items);
        borrowStateDetailBegin = getResources().getStringArray(R.array.borrow_state_begin);
        borrowStateDetailEnd = getResources().getStringArray(R.array.borrow_state_end);
        lendStateDetailBegin = getResources().getStringArray(R.array.lend_state_begin);
        lendStateDetailEnd = getResources().getStringArray(R.array.lend_state_end);

        setActionBar();
        initWidget();

        initData();
        ChatManager.getInstance().addObserver(this);
        if(order.borrowId == user.getUserId()) {
            showNewMessage(order.lendId, order.lendAvatar, order.borrowId, order.id);
        } else {
            showNewMessage(order.borrowId, order.borrowAvatar, order.lendId, order.id);
        }
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
    }

    public void initWidget() {
        title = (TextView) findViewById(R.id.title);
        if(order.borrowId.equals(user.getUserId())) {
            if("男".equals(order.lendGender)) {
                gender.setImageResource(R.drawable.icon_male);
            } else {
                gender.setImageResource(R.drawable.icon_female);
            }
        } else {
            if("男".equals(order.borrowGender)) {
                gender.setImageResource(R.drawable.icon_male);
            } else {
                gender.setImageResource(R.drawable.icon_female);
            }
        }

        shopImage = (ImageView) findViewById(R.id.shop_image);
        shopName = (TextView) findViewById(R.id.shop_name);
        shopDistance = (TextView) findViewById(R.id.shop_distance);
        cardDiscount = (TextView) findViewById(R.id.card_discount);
        cardType = (TextView) findViewById(R.id.card_type);
        orderState = (TextView) findViewById(R.id.order_state);
        btnChangeStatus = (ImageView) findViewById(R.id.change_state);

        orderView = findViewById(R.id.order_info);
        firstView = findViewById(R.id.first_item);
        secondView = findViewById(R.id.second_item);
        thirdView = findViewById(R.id.third_item);
        forthView = findViewById(R.id.forth_item);
        firstContent = (TextView) findViewById(R.id.first_content);
        firstDate = (TextView) findViewById(R.id.first_date);
        firstConfirm = (TextView) findViewById(R.id.first_confirm);
        secondContent = (TextView) findViewById(R.id.second_content);
        secondDate = (TextView) findViewById(R.id.second_date);
        secondConfirm = (TextView) findViewById(R.id.second_confirm);
        thirdContent = (TextView) findViewById(R.id.third_content);
        thirdDate = (TextView) findViewById(R.id.third_date);
        thirdConfirm = (TextView) findViewById(R.id.third_confirm);
        forthContent = (TextView) findViewById(R.id.third_content);
        forthDate = (TextView) findViewById(R.id.third_date);
        forthConfirm = (TextView) findViewById(R.id.third_confirm);

        etMsg = (EditText) findViewById(R.id.chat_et_msg);
        btnSend = (TextView) findViewById(R.id.chat_btn_send);
        chatListView = (ListView) findViewById(R.id.chat_listview);

        firstConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstConfirm.getText().equals(getResources().getString(R.string.lend))) {
                    if(user.getUserId().equals(order.lendId)) {
                        addOrder(order.borrowId, order.lendId, order.cardId, order.cardType);
                    }
                }
            }
        });

        secondConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secondConfirm.getText().equals(getResources().getString(R.string.confirm))) {
                    if(user.getUserId().equals(order.lendId)) {
                        updateOrderStatus(order.id, OrderConstant.RETURN, 2);
                    }
                }
            }
        });

        thirdConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thirdConfirm.getText().equals(getResources().getString(R.string.pay))) {
                    if(user.getUserId().equals(order.borrowId)) {
                        updateOrderStatus(order.id, OrderConstant.PAY, 3);
                    }
                }
            }
        });

        forthConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(thirdConfirm.getText().equals(getResources().getString(R.string.confirm))) {
                    if(user.getUserId().equals(order.lendId)) {
                        updateOrderStatus(order.id, OrderConstant.CONFIRM, 4);
                    }
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
                    chatMsg.type = 0;
                    chatMsg.content = etMsg.getText().toString().trim();
                    chatMsg.time = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");
                    chatMsg.isSend = 0;

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

        listenerSoftInput();
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

    public void initData() {
        String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 70), DisplayUtil.dip2px(mContext, 100));
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 100), DisplayUtil.dip2px(mContext, 70));
        ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        shopImage.setImageBitmap(loadedImage);
                    }
                });

        setOrderDetail();

        chatList.addAll(getChatData());
        if(order.borrowId.equals(user.getUserId())) {
            chatAdapter = new ChatAdapter(mContext, chatList, order.lendAvatar, order.borrowAvatar);
        } else {
            chatAdapter = new ChatAdapter(mContext, chatList, order.borrowAvatar, order.lendAvatar);
        }

        chatListView.setAdapter(chatAdapter);
    }

    public void sendMsg(final Chat chatMsg) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("open_id", user.getUserId()));
        params.add(new BasicNameValuePair("key", user.getKey()));
        params.add(new BasicNameValuePair("order_id", chatMsg.orderId + ""));
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
                        chatDao.updateUnSend(chatMsg);
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

    public void addOrder(String borrowId, String lendId, int cardId, int type) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("borrow_id", borrowId + ""));
        params.add(new BasicNameValuePair("lend_id", lendId + ""));
        params.add(new BasicNameValuePair("card_id", cardId + ""));
        params.add(new BasicNameValuePair("type", type + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.ADD_ORDER, params, new HttpDataResponse() {
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
                        Toast.makeText(mContext, "借卡成功", Toast.LENGTH_LONG).show();
                        order.id = jsonObject.getInt("id");
                        orderHandler.sendEmptyMessage(1);
                        chatDao.updateOrderId(order.id, order.borrowId, order.lendId, order.cardId);
                    } else {
                        Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
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

    public void updateOrderStatus(int id, int status, final int button) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", id + ""));
        params.add(new BasicNameValuePair("status", status + ""));

        HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_ORDER_STATE, params, new HttpDataResponse() {
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
                        Toast.makeText(mContext, "成功", Toast.LENGTH_LONG).show();
                        orderHandler.sendEmptyMessage(button);
                    } else {
                        Toast.makeText(mContext, "网络错误，请稍后重试", Toast.LENGTH_LONG).show();
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
                case 2:
                    Chat chat = (Chat) msg.obj;
                    sendMsg(chat);
                    break;
            }
        }
    };

    private Handler orderHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    firstConfirm.setText(getResources().getString(R.string.have_lent));
                    firstConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                    firstConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                    secondView.setVisibility(View.VISIBLE);
                    secondContent.setText(lendStateDetailBegin[0] + lendStateDetailEnd[1]);
                    secondDate.setText("");
                    secondConfirm.setText(getResources().getString(R.string.confirm));
                    secondConfirm.setTextAppearance(mContext, R.style.order_state_un_confirm);
                    secondConfirm.setBackgroundResource(R.drawable.order_state_tv_un_confirm);
                    break;
                case 2:
                    secondConfirm.setText(getResources().getString(R.string.have_confirmed));
                    secondConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                    secondConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                    break;
                case 3:
                    thirdConfirm.setText(getResources().getString(R.string.have_paid));
                    thirdConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                    thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                    break;
                case 4:
                    forthConfirm.setText(getResources().getString(R.string.have_confirmed));
                    forthConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                    forthConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                    break;
            }
        }
    };

    public void setOrderDetail() {
        String thumbnailUrl = QiniuUtil.getInstance().getFileThumbnailUrl(order.shopImage[0], DisplayUtil.dip2px(mContext, 80), DisplayUtil.dip2px(mContext, 100));
        ImageSize imageSize = new ImageSize(DisplayUtil.dip2px(mContext, 100), DisplayUtil.dip2px(mContext, 80));
        ImageLoader.getInstance().loadImage(thumbnailUrl, imageSize, IShareApplication.defaultOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                shopImage.setImageBitmap(loadedImage);
            }
        });

        shopName.setText(order.shopName);
        shopDistance.setText(order.shopDistance + "");
        cardDiscount.setText(order.cardDiscount + "折");
        cardType.setText(cardItems[order.cardType]);

        if(user.getUserId().equals(order.borrowId)) {
            orderState.setText(borrowStateItems[order.orderState]);
            setBorrowDetail();
        } else {
            orderState.setText(lendStateItems[order.orderState]);
            setLendDetail();
        }

    }

    public void setBorrowDetail() {
        switch (order.orderState) {
            case 0:
                firstView.setVisibility(View.GONE);
                secondView.setVisibility(View.GONE);
                thirdView.setVisibility(View.GONE);
                forthView.setVisibility(View.GONE);
                break;
            case 2:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(borrowStateDetailBegin[0] + order.lendName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setVisibility(View.INVISIBLE);
                secondView.setVisibility(View.GONE);
                thirdView.setVisibility(View.GONE);
                forthView.setVisibility(View.GONE);
                break;
            case 3:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(borrowStateDetailBegin[0] + order.lendName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setVisibility(View.INVISIBLE);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(borrowStateDetailBegin[1] + borrowStateDetailEnd[1]);
                secondDate.setText(order.returnTime);
                secondConfirm.setVisibility(View.INVISIBLE);
                thirdView.setVisibility(View.VISIBLE);
                thirdContent.setText(borrowStateDetailBegin[2] + borrowStateDetailEnd[2]);
                thirdDate.setText("");
                thirdConfirm.setText(getResources().getString(R.string.pay));
                thirdConfirm.setTextAppearance(mContext, R.style.order_state_un_confirm);
                thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_un_confirm);
                forthView.setVisibility(View.GONE);
                break;
            case 4:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(borrowStateDetailBegin[0] + order.lendName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setVisibility(View.INVISIBLE);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(borrowStateDetailBegin[1] + borrowStateDetailEnd[1]);
                secondDate.setText(order.returnTime);
                secondConfirm.setVisibility(View.INVISIBLE);
                thirdView.setVisibility(View.VISIBLE);
                thirdContent.setText(borrowStateDetailBegin[2] + borrowStateDetailEnd[2]);
                thirdDate.setText(order.payTime);
                thirdConfirm.setText(getResources().getString(R.string.have_paid));
                thirdConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                forthView.setVisibility(View.GONE);
                break;
            case 5:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(borrowStateDetailBegin[0] + order.lendName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setVisibility(View.INVISIBLE);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(borrowStateDetailBegin[1] + borrowStateDetailEnd[1]);
                secondDate.setText(order.returnTime);
                secondConfirm.setVisibility(View.INVISIBLE);
                thirdView.setVisibility(View.VISIBLE);
                thirdContent.setText(borrowStateDetailBegin[2] + borrowStateDetailEnd[2]);
                thirdDate.setText(order.payTime);
                thirdConfirm.setText(getResources().getString(R.string.have_paid));
                thirdConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                forthView.setVisibility(View.VISIBLE);
                forthContent.setText(borrowStateDetailBegin[3] + borrowStateDetailEnd[3]);
                forthDate.setText(order.confirmTime);
                forthConfirm.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setLendDetail() {
        switch (order.orderState) {
            case 0:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(lendStateDetailBegin[0] + lendStateDetailEnd[0]);
                firstDate.setText("");
                firstConfirm.setText(getResources().getString(R.string.lend));
                firstConfirm.setTextAppearance(mContext, R.style.order_state_un_confirm);
                secondView.setVisibility(View.GONE);
                thirdView.setVisibility(View.GONE);
                forthView.setVisibility(View.GONE);
                break;
            case 2:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(lendStateDetailBegin[0] + order.borrowName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setText(getResources().getString(R.string.have_lent));
                firstConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                firstConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(lendStateDetailBegin[1] + lendStateDetailEnd[1]);
                secondDate.setText("");
                secondConfirm.setText(getResources().getString(R.string.confirm));
                thirdView.setVisibility(View.GONE);
                forthView.setVisibility(View.GONE);
                break;
            case 3:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(lendStateDetailBegin[0] + order.borrowName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setText(getResources().getString(R.string.have_lent));
                firstConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                firstConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(lendStateDetailBegin[1] + lendStateDetailEnd[1]);
                secondDate.setText("");
                secondConfirm.setText(getResources().getString(R.string.confirm));
                thirdView.setVisibility(View.GONE);
                forthView.setVisibility(View.GONE);
                break;
            case 4:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(lendStateDetailBegin[0] + order.borrowName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setText(getResources().getString(R.string.have_lent));
                firstConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                firstConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(lendStateDetailBegin[1] + lendStateDetailEnd[1]);
                secondDate.setText(order.returnTime);
                secondConfirm.setText(getResources().getString(R.string.have_confirmed));
                thirdView.setVisibility(View.VISIBLE);
                thirdContent.setText(lendStateDetailBegin[2] + lendStateDetailEnd[2]);
                thirdDate.setText(order.payTime);
                thirdConfirm.setText(getResources().getString(R.string.have_paid));
                thirdConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                forthView.setVisibility(View.VISIBLE);
                forthContent.setText(lendStateDetailBegin[3] + lendStateDetailEnd[3]);
                forthDate.setText("");
                forthConfirm.setText(getResources().getString(R.string.confirm));
                forthConfirm.setTextAppearance(mContext, R.style.order_state_un_confirm);
                forthConfirm.setBackgroundResource(R.drawable.order_state_tv_un_confirm);
                break;
            case 5:
                firstView.setVisibility(View.VISIBLE);
                firstContent.setText(lendStateDetailBegin[0] + order.borrowName + borrowStateDetailEnd[0]);
                firstDate.setText(order.lendTime);
                firstConfirm.setText(getResources().getString(R.string.have_lent));
                firstConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                firstConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                secondView.setVisibility(View.VISIBLE);
                secondContent.setText(lendStateDetailBegin[1] + lendStateDetailEnd[1]);
                secondDate.setText(order.returnTime);
                secondConfirm.setText(getResources().getString(R.string.have_confirmed));
                thirdView.setVisibility(View.VISIBLE);
                thirdContent.setText(lendStateDetailBegin[2] + lendStateDetailEnd[2]);
                thirdDate.setText(order.payTime);
                thirdConfirm.setText(getResources().getString(R.string.have_paid));
                thirdConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                thirdConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                forthView.setVisibility(View.VISIBLE);
                forthContent.setText(lendStateDetailBegin[3] + lendStateDetailEnd[3]);
                forthDate.setText(order.confirmTime);
                forthConfirm.setText(getResources().getString(R.string.have_confirmed));
                forthConfirm.setTextAppearance(mContext, R.style.order_state_confirm);
                forthConfirm.setBackgroundResource(R.drawable.order_state_tv_confirm);
                break;
        }
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

    public List<Chat> getChatData() {
        String date = null;
        if(chatList.size() == 0) {
            date = DateUtil.getCurtime("yyyy-DD-mm hh:mm:ss");
        } else {
            date = chatList.get(0).time;
        }
        return chatDao.query(order.borrowId, order.lendId, order.id, date, PAGE_NUM);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isForeground = false;
    }

    public void showNewMessage(String fromUser, String fromAvatar, String toUser, int orderId) {
        Log.e(TAG, (order == null) + " : order");
        Log.e(TAG, fromUser + " : fromUser");
        Log.e(TAG, toUser + " : toUser");
        if(order != null) {
            if((order.borrowId.equals(fromUser) || order.lendId.equals(fromUser))
                    && (order.id == orderId)) {

                List<Chat> tmpList = chatDao.queryUnRead(fromUser, toUser, order.id);
                Log.e(TAG, tmpList.size() + " : order != null");
                chatDao.updateUnRead(tmpList);

                if(tmpList.size() != 0) {
                    chatList.addAll(tmpList);
                    if(chatAdapter == null) {
                        chatAdapter = new ChatAdapter(mContext, chatList, fromAvatar, user.getAvatar());
                    }
                    chatAdapter.notifyDataSetChanged();
                }

                NotificationManager m_NotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
                m_NotificationManager.cancelAll();
            }
        } else {
            List<Chat> tmpList = chatDao.queryUnRead(fromUser, toUser, orderId);
            Log.e(TAG, tmpList.size() + " : order == null");
            chatDao.updateUnRead(tmpList);

            if(tmpList.size() != 0) {
                chatList.addAll(tmpList);
                chatAdapter = new ChatAdapter(mContext, chatList, fromAvatar, user.getAvatar());
                chatListView.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }

            NotificationManager m_NotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
            m_NotificationManager.cancelAll();
        }
    }
}
