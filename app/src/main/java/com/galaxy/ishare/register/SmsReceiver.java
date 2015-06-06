package com.galaxy.ishare.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.EditText;

import com.galaxy.ishare.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by admin on 2015/5/16.
 */
public class SmsReceiver extends BroadcastReceiver {
    public  static String code=null;
    public static String address=null;
    private  static final String TAG ="smsreceiver";
    public EditText et;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.v(TAG, "getmsg");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] message = new SmsMessage[pdus.length];
            StringBuilder sb = new StringBuilder();
            System.out.println("pdus.length" + pdus.length);
            for (int i = 0; i < pdus.length; i++) {
                message[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                code=message[i].getDisplayMessageBody();
                address=message[i].getDisplayOriginatingAddress();
                Log.v(TAG,getCode());
                sb.append("msg from:\n");
                sb.append(message[i].getDisplayOriginatingAddress() + "\n");
                sb.append("msg:" + message[i].getDisplayMessageBody());
            }
            System.out.println(sb.toString());
//            if(address.equals("")){
//                RegisterActivity.confirmCodeEt.setText(getCode());
//            }
        }

    }
    private String getCode(){
        Pattern p;
        p=Pattern.compile("\\d{4}");
        Matcher m;
        m=p.matcher(SmsReceiver.code);
        String confirmcode="";
        while (m.find()){
            confirmcode=m.group();
            System.out.println("res="+confirmcode);
        }
        return confirmcode;
    }

}


