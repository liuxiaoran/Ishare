package com.galaxy.ishare.user_request;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/6/3.
 */
public class RequestListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList dataList;

    public RequestListAdapter(Context context, ArrayList dataList) {
        mContext = context;
        this.dataList = dataList;
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


        return null;
    }

    class RequestHolder {
        public ImageView shopIv;

    }
}
