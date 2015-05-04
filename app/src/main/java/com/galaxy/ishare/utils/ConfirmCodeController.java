package com.galaxy.ishare.utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.galaxy.ishare.R;
import com.galaxy.ishare.model.ConfirmCode;
import com.galaxy.ishare.URLConstant;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class ConfirmCodeController {

    private static final String TAG = "ConfirmCodeController";

    private static ConfirmCodeController instance;
    private static ArrayList<ConfirmCode> codeGroup;
    private static CountDownTimer timer;
    private static Context context;
    private String confirmCodeErrorMessage;

    public static ConfirmCodeController getInstance(Context context, CountDownTimer timer) {
        if (instance == null) {
            instance = new ConfirmCodeController();
            codeGroup = new ArrayList();
            ConfirmCodeController.timer = timer;
            ConfirmCodeController.context = context;
        }
        return instance;
    }


    public void sendConfirmCode(String phone) {

        Log.v(TAG, phone);
        ConfirmCode code = new ConfirmCode(String.valueOf((int) (Math.random() * 9000) + 1000), System.currentTimeMillis());
        codeGroup.add(code);

        String content = context.getResources().getString(R.string.msm_content);
        content = String.format(content, code.getValue());
        String url = URLConstant.GET_CHANGZHUO_SHORT_MESSAGE;
        url = url.replace("tmpPhoneNumber", phone).replace("tmpContent", content);

        AppAsyncHttpClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v(TAG, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.v(TAG, responseString);
                if (responseString.contains("<returnstatus>Success</returnstatus>")) {
                    timer.start();
                } else if (responseString.contains("手机号码为空"))
                    Toast.makeText(context, "请填写手机号", Toast.LENGTH_LONG).show();
                else if (responseString.contains("错误的手机号码"))
                    Toast.makeText(context, "手机号码有误", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean checkCode(String inputCode) {

        if (inputCode == null) {

            confirmCodeErrorMessage = "验证码是空";
            return false;


        }

        boolean isRight = false;

        Date now = new Date();
        for (ConfirmCode tempCode : codeGroup)
            if (tempCode.getValue().equals(inputCode)) {
                if ((now.getTime() - tempCode.getBeginTime().getTime()) / 1000 > 180)
                    confirmCodeErrorMessage = "验证码已过期";
                else
                    isRight = true;
                break;
            }
        if (confirmCodeErrorMessage == null && !isRight)
            confirmCodeErrorMessage = "验证码错误";

        return isRight;
    }

    public String getConfirmCodeErrorMessage() {
        String ret = "";
        if (confirmCodeErrorMessage != null) {
            return confirmCodeErrorMessage;
        }
        return ret;
    }


}
