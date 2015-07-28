package com.galaxy.ishare.usercenter.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Toast;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.UserLocationDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.login.LoginActivity;
import com.galaxy.ishare.model.UserLocation;
import com.galaxy.ishare.publishware.CardOwnerAvailableAddrSearchActivity;
import com.galaxy.ishare.publishware.PublishItemActivity;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/19.
 */
public class CardAddrActivity extends IShareActivity {


    public static final int ADDR_SEARCH_TO_CARD_ADD_RESULT_CODE = 1;
    public static String PARAMETER_WHO_COME = "PARAMETER_WHO_COME";
    private ListView availableListView;
    ListViewAdapter listViewAdapter;
    ArrayList<UserLocation> dataList;

    RelativeLayout newaddrLayout;

    private static final String TAG = "CardOwnerShowActivity";
    private HttpInteract httpInteract;
    private int lastPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_owner_location_activity);


        IShareContext.getInstance().createActionbar(this, true, "您的地点");
        httpInteract = new HttpInteract();

        availableListView = (ListView) findViewById(R.id.publishware_available_listview);
        newaddrLayout = (RelativeLayout) findViewById(R.id.publishware_available_newaddress_layout);

        dataList = UserLocationDao.getInstance(this).query(IShareContext.getInstance().getCurrentUser().getUserId());
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

        // 通知服务器增加地址,增加的那个一定是在dataList 的最后一个
        httpInteract.addLocation(dataList.size());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getIntent().getIntExtra(PARAMETER_WHO_COME, 0) == PublishItemActivity.PUBLISH_TO_ADDR) {
                Intent intent = new Intent(this, PublishItemActivity.class);
                setResult(PublishItemActivity.ADDR_TO_PUBLISH_RESULT_CODE, intent);
            }
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDataList() {
        dataList = UserLocationDao.getInstance(this).query(IShareContext.getInstance().getCurrentUser().getUserId());
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


            if (getIntent().getIntExtra(PARAMETER_WHO_COME, 0) == PublishItemActivity.PUBLISH_TO_ADDR) {
                // 从发卡进入的  需要将删除图标变成单选图标

                deleteIv.setImageResource(R.drawable.radiobutton_selector);
                if (dataList.get(position).isChoosed == false) {
                    deleteIv.setSelected(false);
                } else {
                    deleteIv.setSelected(true);
                    lastPosition = position;
                    Log.v(TAG, "last position:" + lastPosition);
                }


                deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) v.getTag();
                        if (dataList.get(position).isChoosed == false) {
                            ((ImageView) v).setSelected(true);
                            dataList.get(position).isChoosed = true;
                            UserLocationDao.getInstance(CardAddrActivity.this).update(dataList.get(position));
                            Log.v(TAG, " position click " + position);
                            if (lastPosition != -1 && lastPosition != position) {

                                dataList.get(lastPosition).isChoosed = false;
                                UserLocationDao.getInstance(CardAddrActivity.this).update(dataList.get(lastPosition));
                            }
                            lastPosition = position;
                            notifyDataSetChanged();
                        }


                    }
                });
            } else {
                deleteIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserLocationDao userLocationDao = UserLocationDao.getInstance(CardAddrActivity.this);
                        userLocationDao.delete(dataList.get((int) deleteIv.getTag()));
                        setDataList();
                        listViewAdapter.notifyDataSetChanged();
                        httpInteract.deleteLocation((int) v.getTag());
                    }
                });
            }


            return convertView;
        }
    }

    class HttpInteract {
        public void addLocation(int position) {
            ArrayList<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("longitude", dataList.get(position).longitude + ""));
            params.add(new BasicNameValuePair("latitude", dataList.get(position).latitude + ""));
            params.add(new BasicNameValuePair("location", dataList.get(position).address));
            HttpTask.startAsyncDataPostRequest(URLConstant.ADD_COLLECTION, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("status") == 0) {
                            int serverId = jsonObject.getInt("id");
                            setDataList();
                            listViewAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CardAddrActivity.this, "增加地址失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Toast.makeText(CardAddrActivity.this, "增加地址失败，请重试", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });

        }

        public void deleteLocation(int position) {
            ArrayList<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id", dataList.get(position).serverId + ""));
            HttpTask.startAsyncDataPostRequest(URLConstant.DELETE_LOCATION, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    Log.v(TAG, "delete location success");
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                    Log.v(TAG, "delete location error");
                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
        }
    }

}
