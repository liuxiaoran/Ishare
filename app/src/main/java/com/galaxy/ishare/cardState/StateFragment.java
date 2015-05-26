package com.galaxy.ishare.cardState;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxy.ishare.Global;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Friend;
import com.galaxy.ishare.model.InviteFriend;
import com.galaxy.ishare.model.User;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateFragment extends Fragment {

    private ListView contactListView, toInviteContactListView;

    private ArrayList<Friend> contactList;
    private ArrayList<InviteFriend> toInviteContactList;
    private ContactListAdapter contactListAdapter;
    private ToInviteContactListAdapter toInviteContactListAdapter;

    View newStatus;
    ListView cardStatusListView;
    List<Map<String, Object>> dataList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        newStatus = lf.inflate(R.layout.activity_card_status,null);

        dataList = getData();
        cardStatusListView = (ListView) newStatus.findViewById(R.id.card_statedata_list);
        cardStatusListView.setAdapter(new CardStateAdapture(getActivity(), dataList));

        AdapterView.OnItemClickListener cardListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("log", "aaaaaaaaaaaaaaaaa");
                /*if (id == -1) {
                    // 点击的是headerView或者footerView
                    return;
                }*/
                Intent intent = new Intent(getActivity(), CardStateDetail.class);
                startActivity(intent);
            }
        };
        cardStatusListView.setOnItemClickListener(cardListener);
       
        

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


    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 30; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.icon_markd);
            map.put("type", "发卡" + i);
            map.put("discount", "1." + i + "折");
            map.put("location", "知春记录东方尚见旗舰店,大运村" + i);
            map.put("shopDistance", "2" + i + "km");
            map.put("cardDistance", "3" + i + "km");
            map.put("cardStatus", "状态" + i);
            list.add(map);
        }
        return list;
    }

    /**
     * 从网络获取数据，并写入contactList,toInviteContactList
     */
    public void writeContactList() {


        List<NameValuePair> params = new ArrayList<>();
        HttpTask.startAsyncDataGetRequset(URLConstant.FRIEND_CONTACT, params, new HttpDataResponse() {
            @Override
            public User onRecvOK(HttpRequestBase request, String result) {

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

                return null;
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
            public User onRecvOK(HttpRequestBase request, String result) {

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

                return null;
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
