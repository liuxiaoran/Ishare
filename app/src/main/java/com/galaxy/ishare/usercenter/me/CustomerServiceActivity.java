package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.chat.ChatAdapter;
import com.galaxy.ishare.chat.ChatUtil;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.ChatDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Chat;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.DateUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhan on 2015/7/16.
 */
public class CustomerServiceActivity extends ActionBarActivity {
    private static final String TAG = "CustomerServiceActivity";
    public static CustomerServiceActivity instance;
    public static boolean isForeground = false;

    private String ishare = "oyIsQt8l9QupElMamo7Ww6ixk1FE";
    private Context mContext;
    private ListView chatListView;
    private EditText chatMsgEv;
    private TextView chatSendTv;
    private List<Chat> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private ChatDao chatDao;

    private User user;
    public static int PAGE_SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        IShareContext.getInstance().createDefaultHomeActionbar(this, getResources().getString(R.string.customer_service));
        mContext = this;
        instance = this;
        user = IShareContext.getInstance().getCurrentUser();
        chatDao = ChatDao.getInstance(mContext);

        initWidget();
        initChatData();
    }

    @Override
    public void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    public void initWidget() {
        chatListView = (ListView) findViewById(R.id.chat_listview);
        chatMsgEv = (EditText) findViewById(R.id.chat_msg);
        chatSendTv = (TextView) findViewById(R.id.chat_send_btn);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        chatAdapter = new ChatAdapter(mContext, chatList, "ishare", user.getAvatar());
        chatListView.setAdapter(chatAdapter);

        chatSendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = chatMsgEv.getText().toString().trim();
                if ("".equals(msg)) {
                    Toast.makeText(mContext, getResources().getString(R.string.empty_msg_tip), Toast.LENGTH_LONG);
                } else {
                    Chat chatMsg = new Chat();
                    chatMsg.fromUser = user.getUserId();
                    chatMsg.toUser = ishare;
                    chatMsg.content = msg;
                    chatMsg.time = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");
                    chatMsgEv.setText("");
                    chatList.add(chatMsg);
                    chatDao.add(chatMsg);
                    chatAdapter.notifyDataSetChanged();
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
            }
        });
    }

    public void closeBoard() {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        if (imm.isActive())  //一直是true
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void initChatData() {
        List<Chat> tmpList = getUnReadChat();
        if(tmpList.size() != 0) {
            addChatList(tmpList);
            chatAdapter.notifyDataSetChanged();
        } else {
            tmpList = getChatData();
            if(tmpList.size() != 0) {
                addChatList(tmpList);
                chatAdapter.notifyDataSetChanged();
            } else {
                getChatFromNet();
            }
        }
    }

    public void sendMsg(final Chat chatMsg) {
        List<BasicNameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("from_user", chatMsg.fromUser));
        params.add(new BasicNameValuePair("to_user", chatMsg.toUser));
        params.add(new BasicNameValuePair("content", chatMsg.content));

        HttpTask.startAsyncDataPostRequest(URLConstant.SEND_CUSTOMER_SERVICE, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                JSONObject jsonObject = null;
                try {
                    Log.v(TAG, result + "result");
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    chatAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Chat chat = (Chat) msg.obj;
                    sendMsg(chat);
                    break;
            }
        }
    };

    public void getChatFromNet() {
        List<BasicNameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("user", user.getUserId()));
        params.add(new BasicNameValuePair("time", getLastTime()));
        params.add(new BasicNameValuePair("size", String.valueOf(PAGE_SIZE)));

        HttpTask.startAsyncDataPostRequest(URLConstant.GET_CUSTOMER_SERVICE, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    Log.e(TAG, jsonObject.toString());
                    if (status == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        if(jsonArray.length() == 0) {
                            if(chatList.size() != 0) {
                                Toast.makeText(mContext, getResources().getString(R.string.http_error_tip), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            List<Chat> tmpList = new ArrayList<Chat>();
                            Log.e(TAG, jsonArray.toString() + "jsonArray");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tmpJson = jsonArray.getJSONObject(i);
                                Chat chat = ChatUtil.parserJSONObject2Chat(tmpJson);
                                tmpList.add(chat);
                            }
                            addChatList(tmpList);
                            chatDao.addList(tmpList);
                            handler.sendEmptyMessage(1);
                        }
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.http_error_tip), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.v(TAG, "sendMsg: " + retCode.toString());
                Toast.makeText(mContext, getResources().getString(R.string.http_error_tip), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    public void showChatRecords() {
        List<Chat> tmpList = getChatData();
        if(tmpList != null) {
            Log.e(TAG, tmpList.size() + "");
            if(tmpList.size() != 0) {
                tmpList.addAll(chatList);
                chatList = tmpList;
                if(chatAdapter != null) {
                    chatAdapter.setChatList(chatList);
                    chatAdapter.notifyDataSetChanged();
                }
            }
        } else {
            getChatFromNet();
        }
    }

    public List<Chat> getChatData() {
        List<Chat> tmpList = new ArrayList<>();
        String date = getLastTime();
        tmpList.addAll(chatDao.query(user.getUserId(), ishare, date, PAGE_SIZE));
        return tmpList;
    }

    public String getLastTime() {
        String date = null;
        if(chatList.size() == 0) {
            date = DateUtil.getCurtime("yyyy-MM-dd HH:mm:ss");
        } else {
            date = chatList.get(0).time;
        }
        Log.e(TAG, date + "");

        return date;
    }

    public void addChatList(List<Chat> tmpList) {
        for(int i = 0; i < tmpList.size(); i++) {
            chatList.add(0, tmpList.get(i));
        }
    }

    public void showNewMessage(Chat chat) {
        chatList.add(chat);
        chatAdapter.notifyDataSetChanged();
    }

    public List<Chat> getUnReadChat() {
        return chatDao.queryUnRead(user.getUserId(), ishare);
    }
}
