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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.ishare.R;
import com.galaxy.ishare.model.OwnerAvailableItem;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardOwnerAvailableShowActivity extends ActionBarActivity{

    public static final String PARAMETER_RETURN_AVAILABLE_LIST="RETURN_AVAILABLE_LIST";

    public static final int SHOW_TO_ADD_REQUEST_CODE=1;
    public static final int PUBLISH_TO_SHOW_REQUST_CODE=2;

    public static final int ADD_TO_SHOW_RESULT_CODE=1;
    public static final int REMOVE_TO_SHOW_RESULT_CODE=2;
    public static final int EDIT_TO_SHOW_RESULT_CODE=3;

    public static final String INTENT_DELETE_POSITION= "INTENT_DELETE_POSITION";

    private ArrayList<OwnerAvailableItem> dataList;
    private ListView  availableListView;
    ListViewAdapter listViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_owner_location_activity);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.main_action_bar);
        TextView titleTv= (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("方便取卡的时间地点");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(true);

        availableListView = (ListView)findViewById(R.id.publishware_owner_available_location_listview);
        dataList= new ArrayList();


        listViewAdapter = new ListViewAdapter(this);
        availableListView.setAdapter(listViewAdapter);

        availableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent  = new Intent (CardOwnerAvailableShowActivity.this,CardOwnerAvailableEditActivity.class);
                intent.putExtra(CardOwnerAvailableEditActivity.PARAMETER_CARD_AVAILABLE_ITEM,dataList.get(position));
                intent.putExtra(CardOwnerAvailableEditActivity.PARAMETER_CARD_AVAILABLE_POSITION,position);
                startActivityForResult(intent, CardOwnerAvailableEditActivity.SHOW_TO_EDIT_REQUST_CODE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_owner_location_setting,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_add){

            Intent intent = new Intent(this,CardOwnerAvailableAddActivity.class);
            startActivityForResult(intent,SHOW_TO_ADD_REQUEST_CODE);


        }else if (item.getItemId()== android.R.id.home){

            Intent intent = new Intent (this,PublishItemActivity.class);
            intent.putParcelableArrayListExtra(PARAMETER_RETURN_AVAILABLE_LIST, dataList);
            setResult(PublishItemActivity.PARAMETER_AVAILABLE_RESULT_CODE,intent);
            finish();

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==ADD_TO_SHOW_RESULT_CODE){
            OwnerAvailableItem item = data.getParcelableExtra(CardOwnerAvailableAddActivity.AVAILABLE_ITEM);
            dataList.add(item);
            Log.v("cardpublish",dataList.size()+"");
            listViewAdapter.notifyDataSetChanged();

        }else if (resultCode==REMOVE_TO_SHOW_RESULT_CODE){
            int  index  = data.getIntExtra(INTENT_DELETE_POSITION,0);
            dataList.remove(index);
            listViewAdapter.notifyDataSetChanged();
        }else if (resultCode==EDIT_TO_SHOW_RESULT_CODE){
            OwnerAvailableItem item = data.getParcelableExtra(CardOwnerAvailableEditActivity.INTENT_AVAILABLE_ITEM);
            int index= data.getIntExtra(CardOwnerAvailableEditActivity.INTENT_ITME_POSITION,0);
            dataList.set(index,item);
            Log.v("cardpublish",dataList.size()+"");
            listViewAdapter.notifyDataSetChanged();
        }


    }

    class ListViewAdapter extends BaseAdapter{

        private LayoutInflater mLayoutInflater;
        public ListViewAdapter (Context context){
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
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                convertView = mLayoutInflater.inflate(R.layout.listview_publishware_available_item,null);
            }
            TextView locationTv= (TextView)convertView.findViewById(R.id.publishware_available_listview_item_location_tv);
            TextView timeTv =(TextView) convertView.findViewById(R.id.publishware_available_listview_item_time_tv);
            LinearLayout itemLayout  = (LinearLayout)convertView.findViewById(R.id.publishware_available_listview_item_layout);


            locationTv.setText(dataList.get(position).location);
            timeTv.setText(dataList.get(position).time);



            return convertView;
        }
    }

}
