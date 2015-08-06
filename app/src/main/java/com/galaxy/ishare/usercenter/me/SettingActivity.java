package com.galaxy.ishare.usercenter.me;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.main.MainActivity;
import com.galaxy.ishare.user_request.PublishRequestActivity;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import static com.galaxy.ishare.R.id.activity_myself_setting_exit_layout;
import static com.galaxy.ishare.R.id.start;

/**
 * Created by liuxiaoran on 15/7/13.
 */
public class SettingActivity extends IShareActivity implements PlatformActionListener {
    public static final String TAG = "SettingActivity";

    RelativeLayout receivenewmessageLayout, aboutishareLayout, logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IShareContext.getInstance().createActionbar(this, true, "设置");
        setContentView(R.layout.activity_myself_setting);

        receivenewmessageLayout= (RelativeLayout) findViewById(R.id.activity_myself_setting_nofitication_layout);
        aboutishareLayout= (RelativeLayout) findViewById(R.id.activity_myself_setting_aboutishare_layout);
        logoutLayout = (RelativeLayout) findViewById(activity_myself_setting_exit_layout);

        receivenewmessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,SettingNotificationActivity.class);
                startActivity(intent);
            }
        });

        aboutishareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingActivity.this,SettingAboutIshareActivity.class);
                startActivity(intent);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog logoutDialog = new MaterialDialog.Builder(SettingActivity.this)
                        .title("退出当前账号")
                        .content("退出当前账号后不会删除任何历史数据，下次登录依然可以使用本账号。")
                        .positiveText("确定")
                        .negativeText("取消")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Log.v(TAG, "sure");
                                ShareSDK.initSDK(getApplicationContext());
                                Platform wechat = ShareSDK.getPlatform(SettingActivity.this, Wechat.NAME);
                                if (wechat.isValid()) {
                                    wechat.removeAccount();
                                }
                                wechat.setPlatformActionListener(SettingActivity.this);
                                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                SettingActivity.this.finish();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        }).build();
                logoutDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
