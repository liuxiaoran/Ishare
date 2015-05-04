package com.galaxy.ishare.contact;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.R;
import com.galaxy.ishare.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.Contact;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {

    private ListView contactListView, toInviteContactListView;

    private ArrayList<Contact> contactList, toInviteContactList;
    private ContactListAdapter contactListAdapter;
    private ToInviteContactListAdapter toInviteContactListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        View view = lf.inflate(R.layout.contact_fragment, container, false);
        contactList = new ArrayList<Contact>();
        toInviteContactList = new ArrayList<Contact>();

        contactListView = (ListView) view.findViewById(R.id.contact_friend_listview);
        contactListAdapter = new ContactListAdapter(getActivity());
        contactListView.setAdapter(contactListAdapter);

        toInviteContactListView = (ListView) view.findViewById(R.id.contact_toinvite_friend_listview);
        toInviteContactListAdapter = new ToInviteContactListAdapter(getActivity());
        toInviteContactListView.setAdapter(toInviteContactListAdapter);

        writeContactList();

        return view;
    }

    /**
     * 从网络获取数据，并写入contactList,toInviteContactList
     */
    public void writeContactList() {


        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("phone", Global.phone));
        HttpTask.startAsyncDataGetRequset(URLConstant.FRIEND_CONTACT, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    JSONArray array = object.getJSONArray("friends");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject contact = array.getJSONObject(i);
                        contactList.add(new Contact(contact.getString("name"), contact.getString("phone")));

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
                        toInviteContactList.add(new Contact(contact.getString("name"), contact.getString("phone")));
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

            holder.nameTv.setText(contactList.get(position).getContactName());


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

                convertView = layoutInflater.inflate(R.layout.contact_invite_friend_item,null);
                holder.nameTv= (TextView)convertView.findViewById(R.id.contact_invite_friend_name_tv);
                holder.inviteTv = (TextView)convertView.findViewById(R.id.contact_invite_friend_invite_tv);


            }else {
                holder =  (InviteFriendContactHolder)convertView.getTag();
            }

            holder.nameTv.setText(toInviteContactList.get(position).getContactName());
            holder.inviteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 发出短信，邀请
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
