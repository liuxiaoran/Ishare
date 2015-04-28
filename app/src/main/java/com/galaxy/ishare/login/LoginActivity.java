package com.galaxy.ishare.login;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.galaxy.ishare.R;
import com.galaxy.util.utils.CheckInfoValidity;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/4/25.
 */
public class LoginActivity extends Activity {


    private MaterialEditText accountEt, passwordEt;
    private FButton loginBtn;
    private TextView registerTv, findPwTv;
    private String phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initWidgets();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfoValidity()) {
                    //  向服务器提交

                }
            }


        });

    }

    private void initWidgets() {
        accountEt = (MaterialEditText) findViewById(R.id.login_account_et);
        passwordEt = (MaterialEditText) findViewById(R.id.login_password_et);

        loginBtn = (FButton) findViewById(R.id.login_login_btn);
        registerTv = (TextView) findViewById(R.id.login_register_tv);
        findPwTv = (TextView) findViewById(R.id.login_forgetpw_tv);

    }

    private boolean checkInfoValidity() {

        boolean ret = true;
        phone = accountEt.getText().toString();
        password = passwordEt.getText().toString();
        CheckInfoValidity validity = CheckInfoValidity.getInstance();
        if (!validity.phonePatternMatch(phone)) {
            accountEt.setError("电话格式错误");
            ret = false;
        }
        if (!validity.pwPatternMatch(password)) {
            passwordEt.setError("密码格式错误");
            ret = false;
        }
        return ret;
    }


}
