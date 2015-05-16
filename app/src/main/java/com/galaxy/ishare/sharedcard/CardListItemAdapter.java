package com.galaxy.ishare.sharedcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.model.CardItem;

import java.util.ArrayList;

/**
 * Created by liuxiaoran on 15/5/16.
 */
public class CardListItemAdapter extends BaseAdapter {

    private ArrayList<CardItem> dataList;
    private LayoutInflater mLayoutInflater;

    public CardListItemAdapter(ArrayList data, Context context) {
        this.dataList = data;
        mLayoutInflater = LayoutInflater.from(context);

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

        if (convertView == null) {

        }


        return convertView;
    }

    class CardHolder {
        public ImageView cardIv;
        public TextView shopName;
        public TextView tradeTypeTv;
        public TextView cardTypeTv;
        public TextView discountTv;
        public TextView shopDistanceTv;
        public TextView ownerDistanceTv;
    }
}
