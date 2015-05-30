package com.galaxy.ishare.publishware;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.http.ISdCacheStrategy;
import com.galaxy.ishare.model.OwnerAvailableItem;
import com.galaxy.ishare.utils.CheckInfoValidity;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardOwnerAvailableEditActivity extends ActionBarActivity {

    private final String TAG="cardedit";
    public static final String INTENT_AVAILABLE_ITEM="INTENT_AVAILABLE_ITEM";
    public static final String INTENT_ITME_POSITION="INTENT_ITME_POSITION";
    public static final int MAP_TO_EDIT_RESULT_CODE=1;
    public static final int SHOW_TO_EDIT_REQUST_CODE=1;
    public static final String PARAMETER_CARD_AVAILABLE_ITEM="PARAMETER_CARD_AVAILABLE_ITEM";
    public static final String PARAMETER_CARD_AVAILABLE_POSITION= "PARAMETER_CARD_AVAILABLE_POSITION";
    private EditText nameEt, locationEt, timeEt;
    private FButton confirmBtn;
    private ImageView gpsIv;

    private double longitude=0.0;
    private double latitude=0.0;
    private String toastMessage;

    private OwnerAvailableItem item ;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_location_add_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultActionbar(this);
        TextView titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("修改地址");

        item= getIntent().getParcelableExtra(PARAMETER_CARD_AVAILABLE_ITEM);
        position = getIntent().getIntExtra(PARAMETER_CARD_AVAILABLE_POSITION, 0);
        initViews();
        longitude=item.longitude;
        latitude=item.latitude;

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( locationIsEdit() && checkInfo()) {
                    Intent intent = new Intent(CardOwnerAvailableEditActivity.this, CardOwnerAvailableMapActivity.class);
                    intent.putExtra(CardOwnerAvailableMapActivity.PARAMETER_ADDR, locationEt.getText().toString());
                    intent.putExtra(CardOwnerAvailableMapActivity.PARAMETER_REQUEST_CODE,CardOwnerAvailableMapActivity.EDIT_TO_MAP_REQUEST_CODE);
                    startActivityForResult(intent, CardOwnerAvailableMapActivity.EDIT_TO_MAP_REQUEST_CODE);
                } else if (checkInfo() == false) {
                    Toast.makeText(CardOwnerAvailableEditActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }else if (locationIsEdit()==false){
                    Intent intent =new Intent (CardOwnerAvailableEditActivity.this,CardOwnerAvailableShowActivity.class);
                    OwnerAvailableItem item  = new OwnerAvailableItem(locationEt.getText().toString(),
                            timeEt.getText().toString(), nameEt.getText().toString(), IShareContext.getInstance().getCurrentUser().getUserPhone(), longitude, latitude);
                    Log.v("cardpublish",item.location+"  time"+item.time+" "+item.name+" "+item.latitude);
                    intent.putExtra(INTENT_AVAILABLE_ITEM, item);
                    intent.putExtra(INTENT_ITME_POSITION,position);
                    setResult(CardOwnerAvailableShowActivity.EDIT_TO_SHOW_RESULT_CODE, intent);
                    finish();
                }

            }
        });
        gpsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
            }
        });

    }
    private boolean locationIsEdit(){
        boolean ret=true;
        if (item.location.equals(locationEt.getText().toString())){
            ret=false;
        }

        return ret;
    }
    private boolean checkInfo(){
        boolean ret=true;
        if (locationEt.getText().toString().equals("") || nameEt.getText().toString().equals("") || timeEt.getText().toString().equals("")) {
            toastMessage="请填写完整信息";
            ret=false;
        }


        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

         getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_delete){

            Intent intent = new Intent (this, CardOwnerAvailableShowActivity.class);
            intent.putExtra(CardOwnerAvailableShowActivity.INTENT_DELETE_POSITION,position);
            setResult(CardOwnerAvailableShowActivity.REMOVE_TO_SHOW_RESULT_CODE, intent);
            finish();
        }else  if (item.getItemId()== android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews(){
        nameEt= (EditText) findViewById(R.id.publishware_available_name_et);
        locationEt= (EditText)findViewById(R.id.publishware_available_location_et);
        timeEt = (EditText)findViewById(R.id.publishware_available_time_et);
        confirmBtn = (FButton)findViewById(R.id.publishware_available_cofirm_btn);
        gpsIv = (ImageView)findViewById(R.id.publish_owner_gps_iv);

        nameEt.setText(item.name);
        locationEt.setText(item.location);
        timeEt.setText(item.time);

    }

    private void returnShowActivity(){
        Intent intent =new Intent (this,CardOwnerAvailableShowActivity.class);
        OwnerAvailableItem item  = new OwnerAvailableItem(locationEt.getText().toString(),
                timeEt.getText().toString(), nameEt.getText().toString(), IShareContext.getInstance().getCurrentUser().getUserPhone(), longitude, latitude);
        Log.v("cardpublish",item.location+"  time"+item.time+" "+item.name+" "+item.latitude);
        intent.putExtra(INTENT_AVAILABLE_ITEM, item);
        intent.putExtra(INTENT_ITME_POSITION,position);
        setResult(CardOwnerAvailableShowActivity.EDIT_TO_SHOW_RESULT_CODE, intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==MAP_TO_EDIT_RESULT_CODE){

            longitude = data.getDoubleExtra(CardOwnerAvailableMapActivity.LOCATION_LONGITUDE,0);
            latitude =data.getDoubleExtra(CardOwnerAvailableMapActivity.LOCATION_LATITIDE,0);
            returnShowActivity();
        }


    }
}
