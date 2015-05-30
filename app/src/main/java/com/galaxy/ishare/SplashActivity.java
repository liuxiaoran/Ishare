package com.galaxy.ishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.main.MainActivity;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class SplashActivity extends Activity {


    public static final String TAG = "splashactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (IShareContext.getInstance().getCurrentUser()==null){
            // 第一次进入系统

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent (this,MainActivity.class);
            startActivity(intent);
        }

        this.finish();


    }
}
