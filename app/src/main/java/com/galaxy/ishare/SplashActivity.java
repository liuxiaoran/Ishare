package com.galaxy.ishare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.main.MainActivity;

/**
 * Created by liuxiaoran on 15/4/28.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (IShareContext.getInstance().getCurrentUser()==null){
            Intent  intent = new Intent (this, LoginActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent (this,MainActivity.class);
            startActivity(intent);
        }

        this.finish();

    }
}
