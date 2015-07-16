package com.galaxy.ishare.usercenter.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

import static com.galaxy.ishare.R.id.activity_myself_setting_exit_layout;
import static com.galaxy.ishare.R.id.start;

/**
 * Created by liuxiaoran on 15/7/13.
 */
public class SettingActivity extends IShareActivity {

    RelativeLayout receivenewmessageLayout,aboutishareLayout,exitLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IShareContext.getInstance().createActionbar(this, true, "设置");
        setContentView(R.layout.activity_myself_setting);

        receivenewmessageLayout= (RelativeLayout) findViewById(R.id.activity_myself_setting_nofitication_layout);
        aboutishareLayout= (RelativeLayout) findViewById(R.id.activity_myself_setting_aboutishare_layout);
        exitLayout= (RelativeLayout) findViewById(activity_myself_setting_exit_layout);

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
