package com.galaxy.ishare.cardState;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxy.ishare.R;

import java.util.List;
import java.util.Map;

/**
 * Created by YangJunLin on 2015/5/26.
 */
public class CardStateAdapture extends BaseAdapter {


    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public CardStateAdapture(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    private final class Zujian {
        public ImageView image;
        public TextView type;
        public TextView discount;
        public TextView location;
        public TextView shopDistance;
        public TextView cardDistance;
        public TextView cardStatus;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian = null;
        if (convertView == null) {
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.card_station_list, null);
            zujian.image = (ImageView) convertView.findViewById(R.id.card_image_state);
            zujian.type = (TextView) convertView.findViewById(R.id.card_type);
            zujian.discount = (TextView) convertView.findViewById(R.id.discount);
            zujian.location = (TextView) convertView.findViewById(R.id.location);
            zujian.shopDistance = (TextView) convertView.findViewById(R.id.shop_distance);
            zujian.cardDistance = (TextView) convertView.findViewById(R.id.card_distance);
            zujian.cardStatus = (TextView) convertView.findViewById(R.id.card_station);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据
        zujian.image.setBackgroundResource((Integer) data.get(position).get("image"));
        zujian.type.setText((String) data.get(position).get("type"));
        zujian.discount.setText((String) data.get(position).get("discount"));
        zujian.location.setText((String) data.get(position).get("location"));
        zujian.shopDistance.setText((String) data.get(position).get("shopDistance"));
        zujian.cardDistance.setText((String) data.get(position).get("cardDistance"));
        zujian.cardStatus.setText((String) data.get(position).get("cardStatus"));

//        convertView.setClickable(true);
       /* View.OnClickListener myClickListener = new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent = new Intent(this, CardStateDetail.class);
                //code to be written to handle the click event 
            }
        };
        convertView.setOnClickListener(myClickListener);*/
        
        return convertView;
    }

}

