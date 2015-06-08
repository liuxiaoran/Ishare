package com.galaxy.ishare.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxy.ishare.R;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class WidgetController {

    public static WidgetController instance;

    public static WidgetController getInstance() {
        if (instance == null) {
            instance = new WidgetController();
        }
        return instance;
    }

    /**
     * widget 可点并且变蓝
     *
     * @param view
     */
    public void setWidgetClickable(View view, Context context) {

        view.setClickable(true);
        TextView textView = null;
        Button button = null;
        if (view instanceof TextView) {
            textView = (TextView) view;
            textView.setEnabled(true);
            textView.setTextColor(context.getResources().getColor(R.color.blue));

        } else if (view instanceof Button) {
            button = (Button) view;
            button.setEnabled(true);
            button.setTextColor(context.getResources().getColor(R.color.blue));
        }


    }

    public void setWidgetUnClickable(View view, Context context) {
        view.setClickable(false);
        TextView textView = null;
        Button button = null;
        if (view instanceof TextView) {
            textView = (TextView) view;
            textView.setEnabled(false);
            textView.setTextColor(context.getResources().getColor(R.color.gray));

        } else if (view instanceof Button) {
            button = (Button) view;
            button.setEnabled(false);
            button.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    public void widgetGetFoucus(View view){
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    public void setViewUnPressed(View view) {
        view.setEnabled(false);
        view.setClickable(false);
//        view.setPressed(false);
    }

    public void setViewClickable(View view) {
        view.setEnabled(true);
        view.setClickable(true);
    }


    /**
     * 把星写入layout
     *
     * @param ratingCount  星的评分
     * @param context
     * @param ratingLayout
     */
    public void setRatingLayout(double ratingCount, Context context, LinearLayout ratingLayout) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ratingLayout.removeAllViews();
        int hasStar = 0;
        for (int i = 0; i < ratingCount - 1; i++) {
            View view = inflater.inflate(R.layout.card_item_star_iv, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.share_item_item_star_iv);
            imageView.setImageResource(R.drawable.star_full);
            ratingLayout.addView(view);
            hasStar++;
        }
        if (ratingCount % 1 >= 0.5) {
            View view = inflater.inflate(R.layout.card_item_star_iv, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.share_item_item_star_iv);
            imageView.setImageResource(R.drawable.star_half);
            ratingLayout.addView(view);
            hasStar++;
        }
        for (int i = 1; i <= 5 - hasStar; i++) {
            View view = inflater.inflate(R.layout.card_item_star_iv, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.share_item_item_star_iv);
            imageView.setImageResource(R.drawable.star_empty);
            ratingLayout.addView(view);
        }

    }

}
