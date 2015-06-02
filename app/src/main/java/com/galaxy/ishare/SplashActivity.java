package com.galaxy.ishare;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.galaxy.ishare.constant.BroadcastActionConstant;
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
        setContentView(R.layout.splash_activity);

        if (IShareContext.getInstance().getCurrentUser()==null){
            // 第一次进入系统


            Intent intent1 = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent1);
            SplashActivity.this.finish();


        }else {


            Intent intent2 = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent2);
            SplashActivity.this.finish();


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
