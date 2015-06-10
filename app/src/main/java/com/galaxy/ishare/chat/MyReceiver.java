package com.galaxy.ishare.chat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.database.ChatDao;
import com.galaxy.ishare.model.Chat;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private ChatDao chatDao = ChatDao.getInstance(IShareContext.mContext);

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		Chat chat = getChatMsg(bundle);

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			ChatManager.getInstance().notifyData(chat);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
			ChatManager.getInstance().startActivityFromNotification(chat);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey1:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey2:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey3:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	public Chat getChatMsg(Bundle bundle) {
		Chat chatMsg = new Chat();
		for (String key : bundle.keySet()) {
			if(key.equals(JPushInterface.EXTRA_NOTIFICATION_TITLE)) {
				chatMsg.fromName = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			} else if(key.equals(JPushInterface.EXTRA_ALERT)) {
				chatMsg.content = bundle.getString(key);
			} else if(key.equals(JPushInterface.EXTRA_EXTRA)) {
				try {
					String extra = bundle.getString(key);
					JSONObject jsonObject = new JSONObject(extra);
					if(jsonObject.has("from_user")) {
						chatMsg.fromUser = jsonObject.getString("from_user");
					}
					if(jsonObject.has("from_gender")) {
						chatMsg.fromUser = jsonObject.getString("from_gender");
					}
					if(jsonObject.has("from_avatar")) {
						chatMsg.fromAvatar = jsonObject.getString("from_avatar");
					}
					if(jsonObject.has("order_id")) {
						chatMsg.orderId = jsonObject.getInt("order_id");
						Log.d(TAG, "order_id: " + chatMsg.orderId);
					}
					if(jsonObject.has("type")) {
						chatMsg.type = jsonObject.getInt("type");
					}
					if(jsonObject.has("time")) {
						chatMsg.time = jsonObject.getString("time");
					}
				} catch (JSONException e) {
					Log.v(TAG,e.toString());
					e.printStackTrace();
				}
			}
		}

		if(chatMsg.content != null) {
			chatMsg.toUser = IShareContext.getInstance().getCurrentUser().getUserId();
			chatMsg.toName = IShareContext.getInstance().getCurrentUser().getUserName();
			chatMsg.toGender = IShareContext.getInstance().getCurrentUser().getGender();
			chatMsg.isRead = 0;
			chatDao.add(chatMsg);
		}

		return chatMsg;
	}

	public void notifyData() {

	}
	
	//send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!JPushUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
//	}
}
