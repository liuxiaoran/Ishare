package com.galaxy.ishare.register;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.FocusFinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.galaxy.controller.ConfirmCodeController;
import com.galaxy.controller.WidgetController;
import com.galaxy.ishare.R;
import com.galaxy.model.ConfirmCode;
import com.galaxy.util.utils.CheckInfoValidity;

import java.util.Date;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class RegisterActivity extends Activity {

    private EditText phoneEt, cofirmCodeEt, passwordEt, confirmPwEt;
    private Button getConfirmBtn, registerBtn;
    private String phone, confirmCode, password, passwordAgain;
    private ConfirmCodeController confirmCodeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        initWidgets();
        confirmCodeController = ConfirmCodeController.getInstance(RegisterActivity.this, new Timer(60000, 1000));

        // 点击获取验证码
        getConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phoneEt.getText().toString();
                if (CheckInfoValidity.getInstance().phonePatternMatch(phone)) {
                    WidgetController.getInstance().setWidgetUnClickable(getConfirmBtn, RegisterActivity.this);
                    confirmCodeController.sendConfirmCode(phone);
                }


            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = phoneEt.getText().toString();
                confirmCode = cofirmCodeEt.getText().toString();
                password = passwordEt.getText().toString();
                passwordAgain = confirmPwEt.getText().toString();

                if (checkUserInfo()) {
                    // 向服务器提交数据

                }
            }
        });
    }

    private void initWidgets() {
        phoneEt = (EditText) findViewById(R.id.register_phone_et);
        cofirmCodeEt = (EditText) findViewById(R.id.register_confirm_et);
        passwordEt = (EditText) findViewById(R.id.register_pw_et);
        confirmPwEt = (EditText) findViewById(R.id.register_pw_again_et);

        getConfirmBtn = (Button) findViewById(R.id.register_get_confirm_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
    }

    private boolean checkUserInfo() {

        //检查用户电话
        if (CheckInfoValidity.getInstance().pwPatternMatch(phone) == false) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_LONG);
            return false;
        } else if (CheckInfoValidity.getInstance().pwPatternMatch(password) == false) {
            Toast.makeText(this, "密码格式错误", Toast.LENGTH_LONG);
            return false;
        } else if (!confirmCodeController.checkCode(confirmCode)) {
            Toast.makeText(this, confirmCodeController.getConfirmCodeErrorMessage(), Toast.LENGTH_LONG);
            return false;
        } else if (!passwordAgain.equals(password)) {

            Toast.makeText(this, "两次密码输入不同", Toast.LENGTH_LONG);
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
            WidgetController.getInstance().setWidgetClickable(getConfirmBtn, RegisterActivity.this);
        }
    }


}
