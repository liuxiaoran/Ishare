package com.galaxy.ishare.publishware;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.model.OwnerAvailableItem;
import com.galaxy.ishare.utils.CheckInfoValidity;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardOwnerAvailableAddActivity extends ActionBarActivity {

    public static final String TAG="CardOwnerAvailableAdd";
    public static final int MAP_TO_ADD_RESULT_CODE=1;
    public static final String AVAILABLE_ITEM="AVAILABLE_ITEM";
    private EditText nameEt, locationEt,phoneEt,timeEt;
    private FButton confirmBtn;
    private ImageView gpsIv;

    private double longitude=0.0;
    private double latitude=0.0;
    private String toastMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_location_add_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultActionbar(this);
        TextView titleTv  = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("增加地址");


        initViews();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (longitude == 0 && latitude == 0 && checkInfo()) {
                    Intent intent = new Intent(CardOwnerAvailableAddActivity.this, CardOwnerAvailableMapActivity.class);
                    intent.putExtra(CardOwnerAvailableMapActivity.PARAMETER_ADDR, locationEt.getText().toString());
                    intent.putExtra(CardOwnerAvailableMapActivity.PARAMETER_REQUEST_CODE,CardOwnerAvailableMapActivity.ADD_TO_MAP_REQUEST_CODE);
                    startActivityForResult(intent, CardOwnerAvailableMapActivity.ADD_TO_MAP_REQUEST_CODE);
                } else if (checkInfo() == false) {
                    Toast.makeText(CardOwnerAvailableAddActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        gpsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationEt==null){
                    Log.v(TAG, "locationEt is null");
                }
                if (IShareContext.getInstance().getUserLocation()==null){
                    Log.v(TAG,"user Location is null");
                }
                locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
            }
        });




    }
    private boolean checkInfo(){
        boolean ret=true;
        if (locationEt.getText().toString().equals("") || phoneEt.getText().toString().equals("") ||nameEt.getText().toString().equals("")||timeEt.getText().toString().equals("")){
            toastMessage="请填写完整信息";
            ret=false;
        }

        else if (! CheckInfoValidity.getInstance().phonePatternMatch(phoneEt.getText().toString())){
            toastMessage ="请填写正确的电话";
            ret= false;
        }
        return ret;
    }

    private void returnShowActivity(){
        Intent intent =new Intent (this,CardOwnerAvailableShowActivity.class);
        OwnerAvailableItem item  = new OwnerAvailableItem(locationEt.getText().toString(),
                timeEt.getText().toString(),nameEt.getText().toString(),phoneEt.getText().toString(),longitude,latitude);
        Log.v("cardpublish",item.location+"  time"+item.time+" "+item.name+" "+item.latitude);
        intent.putExtra(AVAILABLE_ITEM, item);
        setResult(CardOwnerAvailableShowActivity.ADD_TO_SHOW_RESULT_CODE, intent);
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==MAP_TO_ADD_RESULT_CODE){

            longitude = data.getDoubleExtra(CardOwnerAvailableMapActivity.LOCATION_LONGITUDE,0);
            latitude =data.getDoubleExtra(CardOwnerAvailableMapActivity.LOCATION_LATITIDE,0);
            returnShowActivity();
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void initViews(){
        nameEt= (EditText) findViewById(R.id.publishware_available_name_et);
        locationEt= (EditText)findViewById(R.id.publishware_available_location_et);
        phoneEt = (EditText)findViewById(R.id.publishware_available_phone_et);
        timeEt = (EditText)findViewById(R.id.publishware_available_time_et);
        confirmBtn = (FButton)findViewById(R.id.publishware_available_cofirm_btn);
        gpsIv = (ImageView)findViewById(R.id.publish_owner_gps_iv);
    }
}
