/*
package com.galaxy.ishare.cardState;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.ChatMsg;
import com.galaxy.ishare.model.User;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatActivity extends Activity {
    private Context mContext;
    private User user;
    private String chat;

    private ImageView btnBack;
    private TextView chatUser;
    private EditText chatWith;
    private EditText etText;
    private Button btnSend;
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private List<ChatMsg> chatList = new ArrayList<ChatMsg>();

    public static boolean isForeground = false;

    private static final String TAG="ChatActivity";
    private static final int SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        user = IShareContext.getInstance().getCurrentUser();
//        Intent intent = getIntent();
//        chat = intent.getStringExtra("chat");
        initWidget();
//        getChatList(getCurTime(), SIZE);
        ChatManager.getInstance().addObserver(this);
    }

    public void initWidget() {
        btnBack = (ImageView) findViewById(R.id.chat_back);
        chatUser = (TextView) findViewById(R.id.chat_user);
        chatWith = (EditText) findViewById(R.id.chat_with);
        etText = (EditText) findViewById(R.id.chat_et_text);
        btnSend = (Button) findViewById(R.id.chat_btn_send);
        chatListView = (ListView) findViewById(R.id.chat_listview);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chatUser.setText(chat);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMsg chatMsg = new ChatMsg();
                chatMsg.fromPhone = user.getUserPhone();
                chatMsg.toPhone = chatWith.getText().toString().trim();
                chatMsg.type = 0;
                chatMsg.content = etText.getText().toString().trim();
                chatMsg.time = getCurTime();
                chatMsg.isComMsg = false;

                Log.e(TAG, "user :" + user.getUserPhone());
                Log.e(TAG, "fromPhone :" + chatMsg.fromPhone);
                Log.e(TAG, "toPhone :" + chatMsg.toPhone);

                etText.setText("");

                chatList.add(chatMsg);
                chatAdapter.notifyDataSetChanged();
                sendMsg(chatMsg);
            }
        });

        chatAdapter = new ChatAdapter(mContext, chatList);
        chatListView.setAdapter(chatAdapter);
    }

    public String getCurTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh-mm-ss");
        return sDateFormat.format(new java.util.Date());
    }

    public void getChatList(String time, int size) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", user.getUserPhone()));
        params.add(new BasicNameValuePair("key", user.getKey()));
        if(time == null) {
            params.add(new BasicNameValuePair("time", time));
        }
        params.add(new BasicNameValuePair("user", user.getUserPhone()));
        params.add(new BasicNameValuePair("chat", chat));
        params.add(new BasicNameValuePair("size", size + ""));
        HttpTask.startAsyncDataGetRequset(URLConstant.GET_CHAT_DATA, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                try {
                    Log.v(TAG, result + "result");
                    JSONObject jsonObject = new JSONObject(result);
                    int status = jsonObject.getInt("status");
                    if (status == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<ChatMsg> tmpList = new ArrayList<ChatMsg>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonMsg = jsonArray.getJSONObject(i);
                            ChatMsg tmpMsg = new ChatMsg();
                            if (jsonMsg.has("from_phone")) {
                                tmpMsg.fromPhone = jsonMsg.getString("from_phone");
                            }
                            if (jsonMsg.has("to_phone")) {
                                tmpMsg.toPhone = jsonMsg.getString("to_phone");
                            }
                            if (jsonMsg.has("name")) {
                                tmpMsg.name = jsonMsg.getString("name");
                            }
                            if (jsonMsg.has("type")) {
                                tmpMsg.type = jsonMsg.getInt("type");
                            }
                            if (jsonMsg.has("content")) {
                                tmpMsg.content = jsonMsg.getString("content");
                                Log.v(TAG, tmpMsg.content + "result");
                            }
                            if (jsonMsg.has("time")) {
                                tmpMsg.time = jsonMsg.getString("time");
                                Log.v(TAG, tmpMsg.time + "result");
                            }
                            tmpList.add(tmpMsg);
                        }
                        chatList = changeList(tmpList);
                        chatAdapter.setChatList(chatList);
                        handler.sendEmptyMessage(0);
                    } else {
                        Toast.makeText(mContext, "服务器错误，请稍后重试", Toast.LENGTH_LONG).show();
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

    public List<ChatMsg> changeList(List<ChatMsg> chatList) {
        List<ChatMsg> tmpList = new ArrayList<ChatMsg>();

        for(ChatMsg msg : chatList) {
            tmpList.add(0, msg);
        }
        return tmpList;
    }

    public void sendMsg(ChatMsg chatMsg) {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("phone", user.getUserPhone()));
        params.add(new BasicNameValuePair("key", user.getKey()));
        params.add(new BasicNameValuePair("from_phone", chatMsg.fromPhone));
        params.add(new BasicNameValuePair("to_phone", chatMsg.toPhone));
        params.add(new BasicNameValuePair("type", chatMsg.type + ""));
        params.add(new BasicNameValuePair("content", chatMsg.content));
//        params.add(new BasicNameValuePair("time", chatMsg.time));

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
                case 1:
                    etText.setText("");
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        isForeground = true;
        chatWith.setText(chat);

        ChatManager.getInstance().notifyData();

//        getChatList(null, 10);
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    public void notifyData(List<ChatMsg> tmpList) {
        if(tmpList != null && tmpList.size() != 0) {
            chatList.addAll(tmpList);
            chatAdapter.notifyDataSetChanged();
        }

        if(isForeground) {
            NotificationManager m_NotificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
            m_NotificationManager.cancelAll();
        }

//        if(!isBackground(mContext)) {
//            NotificationManager m_NotificationManager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
//            m_NotificationManager.cancelAll();
//        }
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }
}
*/
