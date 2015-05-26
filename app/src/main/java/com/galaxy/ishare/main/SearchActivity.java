package com.galaxy.ishare.main;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/5/14.
 */
public class SearchActivity extends ActionBarActivity {

    private FButton searchBtn;
    private EditText contentEt;
    private LinearLayout adLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search_activity);

        android.support.v7.app.ActionBar actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.main_search_actionbar, true);

        searchBtn = (FButton) actionBar.getCustomView().findViewById(R.id.search_btn);
        contentEt = (EditText)actionBar.getCustomView().findViewById(R.id.search_et);
        adLayout = (LinearLayout)findViewById(R.id.main_search_ad_layout);




        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                adLayout.setVisibility(View.INVISIBLE);



            }
        });

    }

}
