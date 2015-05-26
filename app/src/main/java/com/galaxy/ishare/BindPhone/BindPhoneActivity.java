package com.galaxy.ishare.BindPhone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.main.MainActivity;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.publishware.PublishItemActivity;
import com.galaxy.ishare.utils.ConfirmCodeController;
import com.galaxy.ishare.utils.WidgetController;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.CheckInfoValidity;
import com.galaxy.ishare.utils.Encrypt;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class BindPhoneActivity extends ActionBarActivity {

    private EditText phoneEt, confirmCodeEt;
    private Button getConfirmBtn, bingBtn;
    private String phone, confirmCode;
    private ConfirmCodeController confirmCodeController;

    private static final String  TAG ="registeractivity";

    public static final String PARAMETER_WHO_COME= "PARAMETER_WHO_COME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);


        ActionBar actionBar=IShareContext.getInstance().createDefaultActionbar(this);
        TextView tv  = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        tv.setText("绑定手机号");

        if (getIntent().getStringExtra(PARAMETER_WHO_COME).equals(MainActivity.PUBLISH_TO_BING_PHONE)) {
            Toast.makeText(this,"发卡前请绑定手机号",Toast.LENGTH_LONG).show();

        }

        initWidgets();
        confirmCodeController = ConfirmCodeController.getInstance(BindPhoneActivity.this, new Timer(60000, 1000));

        // 点击获取验证码
        getConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phoneEt.getText().toString();
                if (CheckInfoValidity.getInstance().phonePatternMatch(phone)) {
                    WidgetController.getInstance().setWidgetUnClickable(getConfirmBtn, BindPhoneActivity.this);
                    confirmCodeController.sendConfirmCode(phone);
                    WidgetController.getInstance().widgetGetFoucus(confirmCodeEt);


                }


            }
        });

        bingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEt.getText().toString();
                confirmCode = confirmCodeEt.getText().toString();


                if (checkUserInfo()) {
                    // 向服务器提交数据

                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("phone", phone));

                    HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_USER_INFO, params, new HttpDataResponse() {
                        @Override
                        public void onRecvOK(HttpRequestBase request, String result) {

                            String key = null;
                            int status = 0;
                            try {
                                JSONObject object = new JSONObject(result);
                                status = object.getInt("status");
                                if (status == 0) {

                                    // 保存手机号到本地user
                                    User user = IShareContext.getInstance().getCurrentUser();
                                    user.setUserPhone(phone);
                                    IShareContext.getInstance().saveCurrentUser(user);

                                    Toast.makeText(BindPhoneActivity.this, "绑定成功", Toast.LENGTH_LONG).show();

                                    if (getIntent().getStringExtra(PARAMETER_WHO_COME).equals(MainActivity.PUBLISH_TO_BING_PHONE)) {
                                        Intent intent = new Intent(BindPhoneActivity.this, PublishItemActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                } else {

                                    Toast.makeText(BindPhoneActivity.this, "该手机号已绑定过", Toast.LENGTH_LONG).show();

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                            Log.v(TAG, "ret code:" + retCode);
                            Toast.makeText(BindPhoneActivity.this, "网络不佳,请重试", Toast.LENGTH_LONG).show();

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
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initWidgets() {
        phoneEt = (EditText) findViewById(R.id.register_phone_et);
        confirmCodeEt = (EditText) findViewById(R.id.register_confirm_et);

        getConfirmBtn = (Button) findViewById(R.id.register_get_confirm_btn);
        bingBtn = (Button) findViewById(R.id.register_btn);
    }

    private boolean checkUserInfo() {

        //检查用户电话
        if (CheckInfoValidity.getInstance().pwPatternMatch(phone) == false) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_LONG).show();
            return false;
        }  else if (!confirmCodeController.checkCode(confirmCode)) {
            Toast.makeText(this, confirmCodeController.getConfirmCodeErrorMessage(), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    class Timer extends CountDownTimer {

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getConfirmBtn.setText("重新获取(" + millisUntilFinished / 1000 + "s)");

        }

        @Override
        public void onFinish() {
            getConfirmBtn.setText("获取验证码");
            WidgetController.getInstance().setWidgetClickable(getConfirmBtn, BindPhoneActivity.this);
        }
    }


}
