package com.galaxy.ishare.publishware;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class PublishItemActivity extends ActionBarActivity {

    private ImageView warePicIv;
    private MaterialEditText wareNameEt,wareDescriptionEt,locationEt;
    private RelativeLayout wareTypeLayout,cityLayout,provinceLayout,locationLayout,seenPeopleLayout;
    private MyClickListener myClickListener;

    private TextView cityTv,provinceTv, wareTypeTv;

    private CheckBox friendCb,indirectFriendCb, allCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        findViewsById();

        myClickListener= new MyClickListener();

        wareTypeLayout.setOnClickListener(myClickListener);



        if (IShareContext.getInstance().getUserLocation()!=null){
            cityTv.setText(IShareContext.getInstance().getUserLocation().getCity());
            provinceTv.setText(IShareContext.getInstance().getUserLocation().getProvince());
            locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
        }


    }

    private void findViewsById(){
        warePicIv = (ImageView)findViewById(R.id.publish_ware_picture_iv);
        wareNameEt =(MaterialEditText)findViewById(R.id.publish_ware_name_et);
        wareDescriptionEt= (MaterialEditText)findViewById(R.id.publish_ware_description_et);
        locationEt = (MaterialEditText)findViewById(R.id.publish_ware_location_et);
        wareTypeLayout= (RelativeLayout)findViewById(R.id.publish_ware_type_layout);
        cityLayout=(RelativeLayout)findViewById(R.id.publish_ware_city_layout);
        provinceLayout=(RelativeLayout)findViewById(R.id.publish_ware_province_layout);
        locationLayout=(RelativeLayout)findViewById(R.id.publish_ware_location_layout);
        seenPeopleLayout=(RelativeLayout)findViewById(R.id.publish_ware_sharetype_layout);

        friendCb =(CheckBox) findViewById(R.id.publish_ware_friend_cb);
        indirectFriendCb =(CheckBox)findViewById(R.id.publish_ware_indirect_friend_cb);
        allCb = (CheckBox)findViewById(R.id.publish_ware_all_cb);


        cityTv= (TextView)findViewById(R.id.publish_ware_city_tv);
        provinceTv=(TextView)findViewById(R.id.publish_ware_province_tv);
        wareTypeTv=(TextView)findViewById(R.id.publish_ware_type_tv);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_save){


        }
        return super.onOptionsItemSelected(item);
    }

    class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            if (v.getId()==R.id.publish_ware_type_layout){
                new MaterialDialog.Builder(PublishItemActivity.this)
                        .title("商品类型")
                        .items(R.array.ware_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                String[] array = PublishItemActivity.this.getResources().getStringArray(R.array.ware_items);
                                String selected = array[i];
                                wareTypeTv.setText(selected);

                                return true;
                            }
                        })
                        .positiveText("确认")
                        .show();

            }
        }
    }
}
