package com.galaxy.ishare.user_request;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.publishware.PoiSearchActivity;
import com.galaxy.ishare.publishware.PublishItemActivity;
import com.galaxy.ishare.publishware.ShopLocateSearchActivity;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

/**
 * Created by liuxiaoran on 15/6/9.
 */
public class PublishRequestActivity extends IShareActivity {

    LinearLayout shopNameLayout;
    EditText descriptionEt;
    TextView shopNameTv, addrTv;
    FButton confirmBtn, meirongBtn, meifaBtn, meijiaBtn, qinziBtn, otherBtn;
    FButton[] cardTypeBtns;

    MClickListener mClickListener;
    HttpInteract httpInteract;

    private boolean isHasShopLatLng = false;
    double shopLatitude;
    double shopLongitude;
    int cardType = 0;
    private static final String TAG = "publishRequestActivity";
    private ImageView shopNameHintIv, shopAddrHintIv, descriptionHintIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_publish_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultHomeActionbar(this, "发布请求");

        mClickListener = new MClickListener();


        initViews();

        setButtonSelected(0);
        confirmBtn.setOnClickListener(mClickListener);
        httpInteract = new HttpInteract();
        for (int i = 0; i < cardTypeBtns.length; i++) {
            cardTypeBtns[i].setOnClickListener(mClickListener);
        }

        shopNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishRequestActivity.this, ShopLocateSearchActivity.class);
                intent.putExtra(ShopLocateSearchActivity.PARAMETER_WHO_COME, ShopLocateSearchActivity.PUBLISHREQUEST_TO_SEARCH);
                startActivityForResult(intent, 0);
            }
        });

        descriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (descriptionEt.getText().toString().length() > 0 && !"\n".equals(descriptionEt.getText().toString())) {
                    descriptionHintIv.setImageResource(R.drawable.icon_green_check);
                } else {
                    descriptionHintIv.setImageResource(R.drawable.icon_red_dot);
                }
            }
        });


    }


    private void initViews() {


        descriptionEt = (EditText) findViewById(R.id.request_publish_description_et);

        confirmBtn = (FButton) findViewById(R.id.request_publish_confirm_btn);
        meirongBtn = (FButton) findViewById(R.id.request_publish_meirong_btn);
        meifaBtn = (FButton) findViewById(R.id.request_publish_meifa_btn);
        meijiaBtn = (FButton) findViewById(R.id.request_publish_meijia_btn);
        qinziBtn = (FButton) findViewById(R.id.request_publish_qingzi_btn);
        otherBtn = (FButton) findViewById(R.id.request_publish_other_btn);

        cardTypeBtns = new FButton[]{meirongBtn, meifaBtn, meijiaBtn, qinziBtn, otherBtn};

        shopNameLayout = (LinearLayout) findViewById(R.id.request_shop_name_layout);
        shopNameTv = (TextView) findViewById(R.id.request_shop_name_tv);
        addrTv = (TextView) findViewById(R.id.request_shop_location_tv);

        shopNameHintIv = (ImageView) findViewById(R.id.request_shop_name_hint_iv);
        shopAddrHintIv = (ImageView) findViewById(R.id.request_shop_addr_hint_iv);
        descriptionHintIv = (ImageView) findViewById(R.id.request_description_hint_iv);


    }

    private void setButtonSelected(int index) {
        cardTypeBtns[index].setButtonColor(getResources().getColor(R.color.card_type_select_btn));
        cardTypeBtns[index].setTextColor(getResources().getColor(R.color.light_primary_text));

        for (int i = 0; i < cardTypeBtns.length; i++) {
            if (i != index) {
                setButtonUnSelect(i);
            }
        }
    }

    private void setButtonUnSelect(int index) {
        cardTypeBtns[index].setButtonColor(getResources().getColor(R.color.card_type_unselect_btn));
        cardTypeBtns[index].setTextColor(getResources().getColor(R.color.dark_primary_text));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.request_publish_meirong_btn) {


                cardType = 0;
                setButtonSelected(0);


            } else if (v.getId() == R.id.request_publish_meifa_btn) {

                setButtonSelected(1);


            } else if (v.getId() == R.id.request_publish_meijia_btn) {

                setButtonSelected(2);
                cardType = 2;


            } else if (v.getId() == R.id.request_publish_qingzi_btn) {

                setButtonSelected(3);
                cardType = 3;


            } else if (v.getId() == R.id.request_publish_other_btn) {

                setButtonSelected(4);
                cardType = 4;


            } else if (v.getId() == R.id.request_publish_confirm_btn) {

                if (checkInfo()) {
                    Log.v(TAG, "arrive");
                    httpInteract.publishRequest();
                }

            }
//            else if (v.getId() == R.id.request_publish_shop_locate_iv) {
//                if (shopNameEt.getText().toString().equals("")) {
//
//                    Toast.makeText(PublishRequestActivity.this, "请填写店名", Toast.LENGTH_SHORT).show();
//
//                } else {
//
//                    Intent intent = new Intent(PublishRequestActivity.this, PoiSearchActivity.class);
////                    intent.putExtra(PoiSearchActivity.PARAMETER_SHOP_NAEM, shopNameEt.getText().toString());
//                    startActivityForResult(intent, PoiSearchActivity.PARAMETER_PULBISH_REQUEST_CODE);
//                }
//
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PublishItemActivity.PARAMETER_SHOP_LOCATION_RESULT_CODE) {
            isHasShopLatLng = true;
            shopLatitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LATITUDE, 0);
            shopLongitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LONGITUDE, 0);

            addrTv.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR));
            shopNameTv.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_NAME));

            shopAddrHintIv.setImageResource(R.drawable.icon_green_check);
            shopNameHintIv.setImageResource(R.drawable.icon_green_check);
        }
    }

    public boolean checkInfo() {
        if (shopNameTv.getText().toString().equals("")) {
            Toast.makeText(this, "请填写店名", Toast.LENGTH_LONG).show();
            return false;
        }
        if (addrTv.getText().toString().equals("")) {
            Toast.makeText(this, "请填写店的地址", Toast.LENGTH_LONG).show();
            return false;
        }
        if (cardType == -1) {
            Toast.makeText(this, "请选择卡的种类", Toast.LENGTH_LONG).show();
            return false;
        }
        if (descriptionEt.getText().toString().equals("")) {
            Toast.makeText(this, "请填写描述", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    class HttpInteract {

        public void publishRequest() {

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("shop_name", shopNameTv.getText().toString()));
            params.add(new BasicNameValuePair("shop_location", addrTv.getText().toString()));
            if (isHasShopLatLng) {
                params.add(new BasicNameValuePair("shop_longitude", shopLongitude + ""));
                params.add(new BasicNameValuePair("shop_latitude", shopLatitude + ""));
            }
            params.add(new BasicNameValuePair("user_location", IShareContext.getInstance().getUserLocation().getLocationStr()));
            params.add(new BasicNameValuePair("user_longitude", IShareContext.getInstance().getUserLocation().getLongitude() + ""));
            params.add(new BasicNameValuePair("user_latitude", IShareContext.getInstance().getUserLocation().getLatitude() + ""));
            params.add(new BasicNameValuePair("trade_type", cardType + ""));
            params.add(new BasicNameValuePair("description", descriptionEt.getText().toString()));

            HttpTask.startAsyncDataPostRequest(URLConstant.PUBLISH_CARD_REQUEST, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    try {
                        Log.v(TAG, "result:  " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("status") == 0) {
                            Toast.makeText(PublishRequestActivity.this, "发布请求成功", Toast.LENGTH_SHORT).show();
                            PublishRequestActivity.this.finish();
                        } else {
                            Toast.makeText(PublishRequestActivity.this, "失败请重发", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                    Log.v(TAG, "retCode: " + retCode);
                    Toast.makeText(PublishRequestActivity.this, "服务器有问题", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onRecvCancelled(HttpRequestBase request) {

                }

                @Override
                public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                }
            });
        }

    }
}
