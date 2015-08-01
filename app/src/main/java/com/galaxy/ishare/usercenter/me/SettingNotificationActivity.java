package com.galaxy.ishare.usercenter.me;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();

    private float down, up, mCurrentX;
    private int mMoveDelX;
    private boolean misScrolled;
    private Settings settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_setting_notification);
        IShareContext.getInstance().createActionbar(this, true, "新消息通知");


//        receiveNewMessageSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_receivenewMessage_switch);
        openVoiceSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_openvoice_switch);
        openShockSwitch = (SwitchCompat) findViewById(R.id.activity_myself_setting_openshock_switch);


        displayUtil = new DisplayUtil();


        settings = IShareContext.getInstance().getCurrentSettings();


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


        openVoiceSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mVelocityTracker.addMovement(event);
//                final int action = MotionEventCompat.getActionMasked(event);
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:{
//                        down = event.getX();
//                        break;
//                    }
//                    case MotionEvent.ACTION_MOVE:{
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:{
//                        up = event.getX();
//                        Log.v(TAG, String.valueOf(up-down));
//                        break;
//                    }
//                }
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
                return false;
            }
        });
        openShockSwitch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                mVelocityTracker.addMovement(event);
//                final int action = MotionEventCompat.getActionMasked(event);
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:{
//                        down = event.getX();
//                        break;
//                    }
//                    case MotionEvent.ACTION_MOVE:{
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP:{
//                        up = event.getX();
//                        Log.v(TAG, String.valueOf(up-down));
//                        break;
//                    }
//                }
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
                return false;
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

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                down = event.getX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mCurrentX = event.getX();
//                mMoveDelX = (int) (mCurrentX - down);
//                Log.v(TAG, "mMoveDelX:" + mMoveDelX);
//                if (mMoveDelX > 3) {
//                    misScrolled = true;
//                }
//                if ((settings.isOpenVoice() && mMoveDelX > 0) || (!settings.isOpenVoice() && mMoveDelX < 0) ||
//                        (settings.isOpenShock() && mMoveDelX > 0) || (!settings.isOpenShock() && mMoveDelX < 0)) {
//                    mMoveDelX = 0;
//                }
//                if (Math.abs(mMoveDelX) > 60) {
//                    mMoveDelX = mMoveDelX > 0 ? 60 : -60;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (misScrolled) {
//                }
//                break;
//        }
//
//        if (!settings.isOpenVoice()){
//            if (mMoveDelX>0){
//                if (mMoveDelX<30){
//                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
//                    settings.setOpenVoice(false);
//                    openVoiceSwitch.setChecked(settings.isOpenVoice());
//                    Log.v(TAG ,"success");
//                }else{
//                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_track);
//                }
//            }else{
//                openShockSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
//            }
//        }else{
//            if (mMoveDelX<0){
//                if (Math.abs(mMoveDelX)<30){
//                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_track);
//                }else{
//                    openVoiceSwitch.setTrackResource(R.drawable.switch_bar_unable_track);
//                }
//            }else{
//                openVoiceSwitch.setTrackResource(R.drawable.switch_bar_track);
//            }
//        }
//        return true;
//    }

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