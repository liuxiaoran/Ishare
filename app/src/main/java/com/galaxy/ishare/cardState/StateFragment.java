package com.galaxy.ishare.cardState;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.CardState;
import com.galaxy.ishare.model.Friend;
import com.galaxy.ishare.model.InviteFriend;
import com.galaxy.ishare.utils.CardStateUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StateFragment extends Fragment {

    private final String TAG = "StateFragmene";
    private ListView contactListView, toInviteContactListView;

    private ArrayList<Friend> contactList;
    private ArrayList<InviteFriend> toInviteContactList;
    private ContactListAdapter contactListAdapter;
    private ToInviteContactListAdapter toInviteContactListAdapter;

    View newStatus;
    ListView cardStatusListView;
    List<CardState> stateLsit = null;
    List<Map<String, Object>> stateMapList = null;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver;
    AdapterView.OnItemClickListener cardListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        newStatus = lf.inflate(R.layout.state_card_status, null);

        cardStatusListView = (ListView) newStatus.findViewById(R.id.card_statedata_list);

        cardListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id == -1) {
                    // 点击的是headerView或者footerView
                    return;
                }
                Intent intent = new Intent(getActivity(), CardStateDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("stateDetail", stateLsit.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        };

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData();
                cardStatusListView.setOnItemClickListener(cardListener);
            }
        };
        localBroadcastManager.registerReceiver(receiver, new IntentFilter(BroadcastActionConstant.UPDATE_USER_LOCATION));


        /*contactList = FriendDao.getInstance(getActivity()).query();
        toInviteContactList = InviteFriendDao.getInstance(getActivity()).query();


        contactListView = (ListView) view.findViewById(R.id.contact_friend_listview);
        contactListAdapter = new ContactListAdapter(getActivity());
        contactListView.setAdapter(contactListAdapter);

        toInviteContactListView = (ListView) view.findViewById(R.id.contact_toinvite_friend_listview);
        toInviteContactListAdapter = new ToInviteContactListAdapter(getActivity());
        toInviteContactListView.setAdapter(toInviteContactListAdapter);*/

//        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
//        localBroadcastManager.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                contactList = FriendDao.getInstance(getActivity()).query();
//                toInviteContactList = InviteFriendDao.getInstance(getActivity()).query();
//                contactListAdapter.notifyDataSetChanged();
//                toInviteContactListAdapter.notifyDataSetChanged();
//
//            }
//        },new IntentFilter(BroadcastConstant.UPDATE_FRIEND_LIST));
//        writeContactList();
        return newStatus;
    }


    public void getData() {
        try {
            String lat = String.valueOf(IShareContext.getInstance().getUserLocation().getLatitude());
            String lon = String.valueOf(IShareContext.getInstance().getUserLocation().getLongitude());
            if (lat != null && lat.length() > 0 && lon != null && lon.length() > 0) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
                params.add(new BasicNameValuePair("longitude", String.valueOf(lon)));
                params.add(new BasicNameValuePair("borrow_id", IShareContext.getInstance().getCurrentUser().getUserId()));
                HttpTask.startAsyncDataPostRequest(URLConstant.STATE_CARD, params, new HttpDataResponse() {
                    @Override
                    public void onRecvOK(HttpRequestBase request, String result) {
                        int status = 0;
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            status = jsonObject.getInt("status");
                            if (status == 0) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                stateMapList = CardStateUtil.change2ListMap(jsonArray);
                                stateLsit = CardStateUtil.change2CardState(jsonArray);
                            } else {
                                Toast.makeText(getActivity(), "由于网络原因，请求失败，请重试", Toast.LENGTH_LONG).show();
                                return;
                            }
                            cardStatusListView.setAdapter(new CardStateAdapture(getActivity(), stateMapList));
                        } catch (JSONException e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                        Log.v(TAG, retCode.toString());
                        Toast.makeText(getActivity(), "由于网络原因，请求失败，请重试", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRecvCancelled(HttpRequestBase request) {
                    }

                    @Override
                    public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
                    }
                });
            }
        } catch (Exception e) {
            Log.e("StateFragment getdata", e.toString());
        }
    }

    @Override
    public void onStop() {
        localBroadcastManager.unregisterReceiver(receiver);
        super.onStop();
    }

    /**
     * 从网络获取数据，并写入contactList,toInviteContactList
     */

    public void writeContactList() {


        List<NameValuePair> params = new ArrayList<>();
        HttpTask.startAsyncDataGetRequset(URLConstant.FRIEND_CONTACT, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("friends");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject contact = array.getJSONObject(i);
                        Friend friend = new Friend(contact.getString("name"), contact.getString("phone"));
//                        FriendDao.getInstance(getActivity()).add(friend);
                        contactList.add(friend);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                contactListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });

        HttpTask.startAsyncDataGetRequset(URLConstant.INVITE_CONTACT, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("friends");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject contact = array.getJSONObject(i);
                        InviteFriend inviteFriend = new InviteFriend(contact.getString("name"), contact.getString("phone"));

                        toInviteContactList.add(inviteFriend);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                toInviteContactListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {

            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });


    }

    class ContactListAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public ContactListAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return contactList.size();
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

            FriendContactHolder holder = null;
            if (convertView == null) {
                holder = new FriendContactHolder();

                convertView = layoutInflater.inflate(R.layout.contact_friend_item, null);
                holder.nameTv = (TextView) convertView.findViewById(R.id.contact_friend_item_name_tv);
                convertView.setTag(holder);
            } else {
                holder = (FriendContactHolder) convertView.getTag();
            }

            holder.nameTv.setText(contactList.get(position).getFriendName());


            return convertView;
        }
    }

    class ToInviteContactListAdapter extends BaseAdapter {


        private LayoutInflater layoutInflater;

        public ToInviteContactListAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return toInviteContactList.size();
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

            InviteFriendContactHolder holder = null;
            if (convertView == null) {
                holder = new InviteFriendContactHolder();

                convertView = layoutInflater.inflate(R.layout.contact_invite_friend_item, null);
                holder.nameTv = (TextView) convertView.findViewById(R.id.contact_invite_friend_name_tv);
                holder.inviteTv = (TextView) convertView.findViewById(R.id.contact_invite_friend_invite_tv);


            } else {
                holder = (InviteFriendContactHolder) convertView.getTag();
            }

            holder.nameTv.setText(toInviteContactList.get(position).getInviteFriendName());
            holder.inviteTv.setTag(toInviteContactList.get(position).getInviteFriendPhone());
            holder.inviteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 发出短信，邀请

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage((String) v.getTag(), null, "我们一起使用ishare吧", null, null);


                }
            });

            return convertView;
        }
    }

    class FriendContactHolder {
        public ImageView avatarIv;
        public TextView nameTv;

    }

    class InviteFriendContactHolder {
        public ImageView avatarIv;
        public TextView nameTv;
        public TextView inviteTv;
    }

}
