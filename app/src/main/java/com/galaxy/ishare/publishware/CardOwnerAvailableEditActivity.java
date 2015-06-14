package com.galaxy.ishare.publishware;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.database.UserAvailableDao;
import com.galaxy.ishare.model.UserAvailable;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardOwnerAvailableEditActivity extends ActionBarActivity {

    private final String TAG = "cardedit";
    public static final String INTENT_ITME_ID = "INTENT_ITME_POSITION";


    public TextView beginTimeTv, endTimeTv, addrTv;
    private FButton confirmBtn;

    private double longitude = 0.0;
    private double latitude = 0.0;
    private String returnedAddr;
    private String toastMessage;
    String beginTime;
    String endTime;
    TimePicker beginTimePicker;
    TimePicker endTimePicker;
    LinearLayout availableLayout;
    UserAvailable item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_location_add_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultHomeActionbar(this, "修改地址");


        initViews();

        int id = getIntent().getIntExtra(INTENT_ITME_ID, 0);
        item = UserAvailableDao.getInstance(this).find(id);
        beginTimeTv.setText(item.beginTime);
        endTimeTv.setText(item.endTime);
        addrTv.setText(item.address);


        beginTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog beginTimeDialog = new MaterialDialog.Builder(CardOwnerAvailableEditActivity.this)
                        .customView(R.layout.publish_card_begin_time_dialog, true)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                int hour = beginTimePicker.getCurrentHour();
                                int minute = beginTimePicker.getCurrentMinute();
                                beginTime = hour + ":" + minute;
                                beginTimeTv.setText(beginTime);

                            }
                        })
                        .positiveText("确定")
                        .build();
                beginTimeDialog.show();
                beginTimePicker = (TimePicker) beginTimeDialog.getCustomView().findViewById(R.id.publishware_available_begin_time_picker);
                beginTimePicker.setIs24HourView(true);
            }
        });

        endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog endTimePickerDialog = new MaterialDialog.Builder(CardOwnerAvailableEditActivity.this)
                        .customView(R.layout.publish_card_begin_time_dialog, true)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                int hour = endTimePicker.getCurrentHour();
                                int minute = endTimePicker.getCurrentMinute();
                                endTime = hour + ":" + minute;
                                endTimeTv.setText(endTime);

                            }
                        })
                        .positiveText("确定")
                        .build();
                endTimePickerDialog.show();
                endTimePicker = (TimePicker) endTimePickerDialog.getCustomView().findViewById(R.id.publishware_available_begin_time_picker);
                endTimePicker.setIs24HourView(true);
            }
        });

        availableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CardOwnerAvailableEditActivity.this, CardOwnerAvailableAddrSearchActivity.class);
                intent.putExtra(CardOwnerAvailableAddrSearchActivity.PARAMETER_ADDR, addrTv.getText().toString());
                intent.putExtra(CardOwnerAvailableAddrSearchActivity.PARAMETER_REQUEST_CODE, CardOwnerAvailableAddrSearchActivity.EDIT_TO_MAP_REQUEST_CODE);
                startActivityForResult(intent, 0);
            }
        });

        beginTimeTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        endTimeTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInfo()) {
                    UserAvailable userAvailable = new UserAvailable(addrTv.getText().toString(), beginTime, endTime, longitude, latitude, item.isSelected);
                    userAvailable.id = item.id;
                    UserAvailableDao.getInstance(CardOwnerAvailableEditActivity.this).update(userAvailable);

                    returnShowActivity();

                } else if (checkInfo() == false) {
                    Toast.makeText(CardOwnerAvailableEditActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private boolean checkInfo() {
        boolean ret = true;
        if (beginTimeTv.getText().toString().length() == 0 || endTimeTv.getText().toString().length() == 0 || addrTv.getText().length() == 0) {
            toastMessage = "请填写完整信息";
            ret = false;
        }

        return ret;
    }

    private void returnShowActivity() {
        Intent intent = new Intent(this, CardOwnerAvailableShowActivity.class);
        setResult(CardOwnerAvailableShowActivity.EDIT_TO_SHOW_RESULT_CODE, intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        longitude = data.getDoubleExtra(CardOwnerAvailableAddrSearchActivity.LOCATION_LONGITUDE, 0);
        latitude = data.getDoubleExtra(CardOwnerAvailableAddrSearchActivity.LOCATION_LATITIDE, 0);
        returnedAddr = data.getStringExtra(CardOwnerAvailableAddrSearchActivity.LOCATION_ADDR);
        addrTv.setText(returnedAddr);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        } else if (item.getItemId() == R.id.menu_delete) {
            UserAvailableDao.getInstance(this).delete(this.item);
            returnShowActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews() {
        addrTv = (TextView) findViewById(R.id.publishware_available_add_addr_tv);
        beginTimeTv = (TextView) findViewById(R.id.publishware_available_add_begin_time_tv);
        endTimeTv = (TextView) findViewById(R.id.publishware_available_add_end_time_tv);
        confirmBtn = (FButton) findViewById(R.id.publishware_available_edit_confirm_btn);
        availableLayout = (LinearLayout) findViewById(R.id.publishware_available_add_layout);

    }
}
