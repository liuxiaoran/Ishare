package com.galaxy.ishare.sharedcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/6/1.
 */
public class CategoryWindowAdapter extends BaseAdapter {

    private String[] data;
    private Context context;
    private LayoutInflater layoutInflater;

    public CategoryWindowAdapter(String[] data, Context context) {
        this.data = data;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
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
            convertView = layoutInflater.inflate(R.layout.window_listview_categoty_item, null);
        }

        TextView categoryTv = (TextView) convertView.findViewById(R.id.window_listview_category_tv);
        categoryTv.setText(data[position]);

        return convertView;
    }
}
