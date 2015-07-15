package com.galaxy.ishare.usercenter.me;

import android.os.Bundle;
import android.view.MenuItem;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/7/13.
 */
public class SettingActivity extends IShareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IShareContext.getInstance().createActionbar(this, true, "设置");
        setContentView(R.layout.activity_setting);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
