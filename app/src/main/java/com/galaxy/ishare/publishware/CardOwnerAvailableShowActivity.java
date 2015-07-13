package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.database.UserAvailableDao;
import com.galaxy.ishare.model.UserAvailable;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardOwnerAvailableShowActivity extends IShareActivity {

    public static final String PARAMETER_RETURN_AVAILABLE_LIST = "RETURN_AVAILABLE_LIST";

    public static final int SHOW_TO_ADD_REQUEST_CODE = 1;
    public static final int SHOW_TO_EDIT_REQUEST_CODE = 2;

    public static final int ADD_TO_SHOW_RESULT_CODE = 1;
    public static final int REMOVE_TO_SHOW_RESULT_CODE = 2;
    public static final int EDIT_TO_SHOW_RESULT_CODE = 3;


    private ListView availableListView;
    ListViewAdapter listViewAdapter;
    ArrayList<UserAvailable> dataList;
    int[] clickCount;
    RelativeLayout newaddrLayout;

    private static final String TAG = "CardOwnerShowActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_owner_location_activity);


        android.support.v7.app.ActionBar actionBar = IShareContext.getInstance().createDefaultHomeActionbar(this, "方便取卡的时间地点");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        availableListView = (ListView) findViewById(R.id.publishware_available_listview);
        newaddrLayout= (RelativeLayout) findViewById(R.id.publishware_available_newaddress_layout);

        dataList = UserAvailableDao.getInstance(this).query();
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        clickCount = new int[100000];

        listViewAdapter = new ListViewAdapter(this);
        availableListView.setAdapter(listViewAdapter);

        newaddrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CardOwnerAvailableShowActivity.this,CardOwnerAvailableAddActivity.class);
                startActivity(intent);
            }
        });


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_owner_location_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {

            Intent intent = new Intent(this, CardOwnerAvailableAddActivity.class);
            startActivity(intent);


        } else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, PublishItemActivity.class);
            setResult(PublishItemActivity.PARAMETER_AVAILABLE_RESULT_CODE, intent);
            Log.v(TAG, "arrive click home");
            this.finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ADD_TO_SHOW_RESULT_CODE) {
            dataList = UserAvailableDao.getInstance(this).query();
            listViewAdapter.notifyDataSetChanged();

        } else if (resultCode == EDIT_TO_SHOW_RESULT_CODE) {
            dataList = UserAvailableDao.getInstance(this).query();
            listViewAdapter.notifyDataSetChanged();
        }

    }


    class ListViewAdapter extends BaseAdapter {


        private LayoutInflater mLayoutInflater;

        public ListViewAdapter(Context context) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.listview_publishware_available_item, null);
            }
            final TextView checkTv = (TextView) convertView.findViewById(R.id.publishware_check_tv);
            TextView locationTv = (TextView) convertView.findViewById(R.id.publishware_available_listview_item_location_tv);
            TextView timeTv = (TextView) convertView.findViewById(R.id.publishware_available_listview_item_time_tv);
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.publishware_available_select_layout);
            final ImageView editIv = (ImageView) convertView.findViewById(R.id.publishware_edit_iv);

            editIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CardOwnerAvailableShowActivity.this, CardOwnerAvailableEditActivity.class);
                    intent.putExtra(CardOwnerAvailableEditActivity.INTENT_ITME_ID, (int) editIv.getTag());

                    startActivityForResult(intent, CardOwnerAvailableShowActivity.SHOW_TO_EDIT_REQUEST_CODE);
                }
            });
            final UserAvailable userAvailable = dataList.get(position);
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCount[position]++;
                    if (clickCount[position] % 2 == 0) {
                        checkTv.setBackgroundResource(R.drawable.icon_circle);
                        userAvailable.isSelected = 0;
                        UserAvailableDao.getInstance(CardOwnerAvailableShowActivity.this).update(userAvailable);
                    } else {
                        checkTv.setBackgroundResource(R.drawable.icon_circle_check);

                        userAvailable.isSelected = 1;
                        UserAvailableDao.getInstance(CardOwnerAvailableShowActivity.this).update(userAvailable);
                    }
                }
            });


            locationTv.setText(userAvailable.address);
            String presentTime = userAvailable.beginTime + "-" + userAvailable.endTime;
            timeTv.setText(presentTime);
            editIv.setTag(userAvailable.id);

            if (userAvailable.isSelected == 1) {
                clickCount[position]++;
                checkTv.setBackgroundResource(R.drawable.icon_circle_check);
            }


            return convertView;
        }
    }

}
