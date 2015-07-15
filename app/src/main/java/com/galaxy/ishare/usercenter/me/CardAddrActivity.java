package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.galaxy.ishare.database.UserLocationDao;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.model.UserLocation;
import com.galaxy.ishare.publishware.CardOwnerAvailableAddrSearchActivity;
import com.galaxy.ishare.sharedcard.CardDetailActivity;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardAddrActivity extends IShareActivity {


    public static final int ADDR_SEARCH_TO_CARD_ADD_RESULT_CODE = 1;
    private ListView availableListView;
    ListViewAdapter listViewAdapter;
    ArrayList<UserLocation> dataList;

    RelativeLayout newaddrLayout;

    private static final String TAG = "CardOwnerShowActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_owner_location_activity);


        IShareContext.getInstance().createActionbar(this, true, "您的地点");

        availableListView = (ListView) findViewById(R.id.publishware_available_listview);
        newaddrLayout= (RelativeLayout) findViewById(R.id.publishware_available_newaddress_layout);

        dataList = UserLocationDao.getInstance(this).query();
        if (dataList == null) {
            dataList = new ArrayList<>();
        }

        setDataList();
        listViewAdapter = new ListViewAdapter(this);
        availableListView.setAdapter(listViewAdapter);
        newaddrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardAddrActivity.this, CardOwnerAvailableAddrSearchActivity.class);
                intent.putExtra(CardOwnerAvailableAddrSearchActivity.PARAMETER_REQUEST_CODE, CardOwnerAvailableAddrSearchActivity.CARDADDR_TO_ADDRSEARCH);
                startActivityForResult(intent, CardOwnerAvailableAddrSearchActivity.CARDADDR_TO_ADDRSEARCH);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        setDataList();
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDataList() {
        dataList = UserLocationDao.getInstance(this).query();
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
            TextView locationTv = (TextView) convertView.findViewById(R.id.publishware_available_listview_item_location_tv);
            LinearLayout itemLayout = (LinearLayout) convertView.findViewById(R.id.publishware_available_select_layout);
            final ImageView deleteIv = (ImageView) convertView.findViewById(R.id.publishware_delete_iv);


            final UserLocation userLocation = dataList.get(position);


            locationTv.setText(userLocation.address);
            deleteIv.setTag(position);

            deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserLocationDao userLocationDao = UserLocationDao.getInstance(CardAddrActivity.this);
                    userLocationDao.delete(dataList.get((int) deleteIv.getTag()));
                    setDataList();
                    listViewAdapter.notifyDataSetChanged();
                }
            });


            return convertView;
        }
    }

}
