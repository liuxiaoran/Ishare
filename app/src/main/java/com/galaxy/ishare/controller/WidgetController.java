package com.galaxy.ishare.controller;

import android.content.Context;
import android.view.View;
import android.widget.Button;
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
            textView.setSelected(false);
            textView.setTextColor(context.getResources().getColor(R.color.blue));

        } else if (view instanceof Button) {
            button = (Button) view;
            button.setSelected(false);
            button.setTextColor(context.getResources().getColor(R.color.blue));
        }


    }

    public void setWidgetUnClickable(View view, Context context) {
        view.setClickable(false);
        TextView textView = null;
        Button button = null;
        if (view instanceof TextView) {
            textView = (TextView) view;
            textView.setSelected(true);
            textView.setTextColor(context.getResources().getColor(R.color.gray));

        } else if (view instanceof Button) {
            button = (Button) view;
            button.setSelected(true);
            button.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

}
