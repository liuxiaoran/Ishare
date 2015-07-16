package com.galaxy.ishare.usercenter.me;

import android.os.Bundle;
import android.view.MenuItem;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

/**
 * Created by doqin on 2015/7/16.
 */
public class SettingAboutIshareActivity extends IShareActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_aboutishare);
        IShareContext.getInstance().createActionbar(this, true, "关于IShare");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}


