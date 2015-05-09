package com.galaxy.ishare.publishware;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class PublishItemActivity extends ActionBarActivity {


    private MaterialEditText shopNameEt, discountEt, cardDesctiptionEt, shopLocationEt,ownerAvailableLocationEt,ownerAvailableTimeEt;

    private MyClickListener myClickListener;
    private RelativeLayout industryLayout;
    private LinearLayout ownerAvailableLayout;

    private TextView industryTv, addMoreTv;

    private RadioButton chargeRb, memberRb;
    private CheckBox friendCb, indirectFriendCb, allCb;

    private ImageView shopLocationIv,ownerLocationIv;

    private ArrayList <HashMap <String,String >> ownerAvailableList;

    private ArrayList<MaterialEditText>ownerAvailableLocationEtList;
    private ArrayList<MaterialEditText>ownerAvailableTimeEtList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        findViewsById();

        myClickListener = new MyClickListener();
        industryLayout.setOnClickListener(myClickListener);
        addMoreTv.setOnClickListener(myClickListener);
        shopLocationIv.setOnClickListener(myClickListener);
        ownerLocationIv.setOnClickListener(myClickListener);

        ownerAvailableList = new ArrayList<>();
        ownerAvailableLocationEtList = new ArrayList<>();
        ownerAvailableTimeEtList = new ArrayList<>();
        ownerAvailableLocationEtList.add(ownerAvailableLocationEt);
        ownerAvailableLocationEtList.add(ownerAvailableTimeEt);


//        if (IShareContext.getInstance().getUserLocation()!=null){
//            cityTv.setText(IShareContext.getInstance().getUserLocation().getCity());
//            provinceTv.setText(IShareContext.getInstance().getUserLocation().getProvince());
//            locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
//        }

    }

    private void findViewsById() {

        shopNameEt = (MaterialEditText) findViewById(R.id.publish_shop_name_et);

        chargeRb = (RadioButton) findViewById(R.id.publish_type_charge_rb);
        memberRb = (RadioButton) findViewById(R.id.publish_type_member_rb);

        discountEt = (MaterialEditText) findViewById(R.id.publish_discount_et);
        industryLayout = (RelativeLayout) findViewById(R.id.publish_industry_layout);
        industryTv = (TextView) findViewById(R.id.publish_industry_tv);

        shopLocationEt = (MaterialEditText) findViewById(R.id.publish_shop_location_et);
        ownerAvailableLayout = (LinearLayout) findViewById(R.id.publish_layout);

        addMoreTv = (TextView) findViewById(R.id.publish_add_more_tv);


        friendCb = (CheckBox) findViewById(R.id.publish_ware_friend_cb);
        indirectFriendCb = (CheckBox) findViewById(R.id.publish_ware_indirect_friend_cb);
        allCb = (CheckBox) findViewById(R.id.publish_ware_all_cb);


        cardDesctiptionEt = (MaterialEditText) findViewById(R.id.publish_card_description_et);
        shopLocationIv = (ImageView)findViewById(R.id.publish_shop_location_iv);
        ownerLocationIv = (ImageView)findViewById(R.id.publish_owner_location_iv);
        ownerAvailableLocationEt = (MaterialEditText)findViewById(R.id.publish_owner_location_et);
        ownerAvailableTimeEt  = (MaterialEditText)findViewById(R.id.publish_owner_time_et);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {


        }
        return super.onOptionsItemSelected(item);
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.publish_industry_layout) {
                new MaterialDialog.Builder(PublishItemActivity.this)
                        .title("卡类型")
                        .items(R.array.ware_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                String[] array = PublishItemActivity.this.getResources().getStringArray(R.array.ware_items);
                                String selected = array[i];
                                industryTv.setText(selected);

                                return true;
                            }
                        })
                        .positiveText("确认")
                        .show();

            } else if (v.getId() == R.id.publish_add_more_tv) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.publish_owner_item_layout, null);
                ImageView locationIv = (ImageView) view.findViewById(R.id.publish_owner_location_iv);
                final MaterialEditText locationEt = (MaterialEditText)view.findViewById(R.id.publish_owner_location_et);
                MaterialEditText timeEt = (MaterialEditText)view.findViewById(R.id.publish_owner_time_et);
                locationIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());

                    }
                });
                ownerAvailableLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ownerAvailableLocationEtList.add(locationEt);
                ownerAvailableLocationEtList.add(timeEt);

            }else if (v.getId()==R.id.publish_shop_location_iv){



            }else if (v.getId()==R.id.publish_owner_location_iv){
                ownerAvailableLocationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
            }
        }
    }
}
