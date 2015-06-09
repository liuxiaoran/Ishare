package com.galaxy.ishare.user_request;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxy.ishare.R;

import org.w3c.dom.Text;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/6/9.
 */
public class PublishRequestActivity extends ActionBarActivity {
    EditText shopAddrEt, shopNameEt, descriptionEt;
    TextView discountTv;
    FButton confirmBtn, meirongBtn, meifaBtn, meijiaBtn, movieBtn, qinziBtn, otherBtn;
    FButton[] cardTypeBtns;
    ImageView locateIv;
    MClickListener mClickListener;
    int[] clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_publish_activity);

        mClickListener = new MClickListener();
        clickCount = new int[6];

        initViews();
        locateIv.setOnClickListener(mClickListener);

    }


    private void initViews() {

        shopNameEt = (EditText) findViewById(R.id.request_publish_shop_name_et);
        shopAddrEt = (EditText) findViewById(R.id.request_publish_shop_addr_et);
        descriptionEt = (EditText) findViewById(R.id.request_publish_description_et);

        confirmBtn = (FButton) findViewById(R.id.request_publish_confirm_btn);
        meirongBtn = (FButton) findViewById(R.id.request_publish_meirong_btn);
        meifaBtn = (FButton) findViewById(R.id.request_publish_meifa_btn);
        meijiaBtn = (FButton) findViewById(R.id.request_publish_meijia_btn);
        movieBtn = (FButton) findViewById(R.id.request_publish_movie_btn);
        qinziBtn = (FButton) findViewById(R.id.request_publish_qingzi_btn);
        otherBtn = (FButton) findViewById(R.id.request_publish_other_btn);

        cardTypeBtns = new FButton[]{confirmBtn, meirongBtn, meifaBtn, meijiaBtn, movieBtn, qinziBtn, otherBtn};

        discountTv = (TextView) findViewById(R.id.request_publish_discount_tv);
        locateIv = (ImageView) findViewById(R.id.request_publish_shop_locate_iv);


    }

    class MClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.request_publish_meirong_btn) {

                clickCount[0]++;
                if (clickCount[0] % 2 == 1) {
                    cardTypeBtns[0].setButtonColor(getResources().getColor(R.color.card_type_select_btn));

                } else {
                    cardTypeBtns[0].setButtonColor(getResources().getColor(R.color.card_type_unselect_btn));
                }

            } else if (v.getId() == R.id.request_publish_meifa_btn) {
                clickCount[1]++;
                if (clickCount[1] % 2 == 1) {
                    cardTypeBtns[1].setButtonColor(getResources().getColor(R.color.color_primary));
                } else {

                }

            } else if (v.getId() == R.id.request_publish_meijia_btn) {

            } else if (v.getId() == R.id.request_publish_movie_btn) {

            } else if (v.getId() == R.id.request_publish_qingzi_btn) {

            } else if (v.getId() == R.id.request_publish_other_btn) {

            }
        }
    }
}
