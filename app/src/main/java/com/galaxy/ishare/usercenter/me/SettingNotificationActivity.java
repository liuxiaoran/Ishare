package com.galaxy.ishare.usercenter.me;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.Settings;
import com.galaxy.ishare.utils.DisplayUtil;

import static com.galaxy.ishare.R.id.activity_myself_setting_openshock_switch;

/**
 * Created by doqin on 2015/7/14.
 */
public class SettingNotificationActivity extends IShareActivity {

    private static final String TAG = "SettingNotificationActivity";
    private SwitchCompat receiveNewMessageSwitch, openVoiceSwitch, openShockSwitch;
    private DisplayUtil displayUtil;

    private float down, up, distance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_setting_notification);
        IShareContext.getInstance().createActionbar(this, true, "新消息通知");



//        receiveNewMessageSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_receivenewMessage_switch);
        openVoiceSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_openvoice_switch);
        openShockSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_openshock_switch);


        displayUtil = new DisplayUtil();


        final Settings settings = IShareContext.getInstance().getCurrentSettings();




        if (settings.isOpenVoice()) {
            openVoiceSwitch.setTrackResource(R.drawable.switch_bar_track);
            openVoiceSwitch.setChecked(settings.isOpenVoice());
        } else {
            openVoiceSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
            openVoiceSwitch.setChecked(settings.isOpenVoice());
        }


        if (settings.isOpenShock()) {
            openShockSwitch.setTrackResource(R.drawable.switch_bar_track);
            openShockSwitch.setChecked(settings.isOpenShock());
        } else {
            openShockSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
            openShockSwitch.setChecked(settings.isOpenShock());
        }



        openVoiceSwitch.setOnTouchListener(new CompoundButton.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    down = event.getX();
                    Log.v(TAG, String.valueOf(down));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    up = event.getX();
                    Log.v(TAG, String.valueOf(up));
                }
            return true;
        }
    });

        openVoiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_track);
                    settings.setOpenVoice(true);
                } else {
                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
                    settings.setOpenVoice(false);
                }
                IShareContext.getInstance().saveSettings(settings);
            }
        });

        openShockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openShockSwitch.setTrackResource(R.drawable.switch_bar_track);
                    settings.setOpenShock(true);
                } else {
                    openShockSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
                    settings.setOpenShock(false);
                }
                IShareContext.getInstance().saveSettings(settings);
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


//        Settings settings = IShareContext.getInstance().getCurrentSettings();
//        settings.setOpenShock(false);
//        IShareContext.getInstance().saveSettings(settings);