package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.galaxy.ishare.Global;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class PublishItemActivity extends ActionBarActivity implements OnGetSuggestionResultListener {

    public static final int PARAMETER_OWNER_LOCATION_RESULT_CODE = 1;
    public static final int PARAMETER_SHOP_LOCATION_RESULT_CODE = 2;
    public static final String PARAMETER_RET_OWNER_ADDR = "PARAMETER_RET_OWNER_ADDR";

    private static final String TAG = "PublishItemActivity";

    private AutoCompleteTextView shopNameTv;
    private MaterialEditText discountEt, cardDesctiptionEt, shopLocationEt, ownerAvailableLocationEt, ownerAvailableTimeEt;

    private MyClickListener myClickListener;
    private RelativeLayout industryLayout;
    private LinearLayout ownerAvailableLayout;

    private TextView industryTv, addMoreTv;

    private RadioButton chargeRb, memberRb;
    private CheckBox friendCb, indirectFriendCb, allCb;

    private ImageView shopLocationIv, ownerLocationIv;

    private ArrayList<HashMap<String, String>> ownerAvailableList;

    private ArrayList<MaterialEditText> ownerAvailableLocationEtList;
    private ArrayList<MaterialEditText> ownerAvailableTimeEtList;

    private SuggestionSearch mSuggestionSearch;
    private ArrayAdapter<String> sugAdapter;

    private double shopLongitude;
    private double shopLatitude;

    private int trade_type;

    public UploadData uploadDataClient;

    public int currentEtIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        findViewsById();

        myClickListener = new MyClickListener();
        industryLayout.setOnClickListener(myClickListener);
        addMoreTv.setOnClickListener(myClickListener);
        shopLocationIv.setOnClickListener(myClickListener);
        ownerLocationIv.setOnClickListener(myClickListener);
        ownerLocationIv.setTag(0);

        ownerAvailableList = new ArrayList<>();
        ownerAvailableLocationEtList = new ArrayList<>();
        ownerAvailableTimeEtList = new ArrayList<>();
        ownerAvailableLocationEtList.add(ownerAvailableLocationEt);
        ownerAvailableTimeEtList.add(ownerAvailableTimeEt);

        uploadDataClient = new UploadData();


//        if (IShareContext.getInstance().getUserLocation()!=null){
//            cityTv.setText(IShareContext.getInstance().getUserLocation().getCity());
//            provinceTv.setText(IShareContext.getInstance().getUserLocation().getProvince());
//            locationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());
//        }


        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        shopNameTv = (AutoCompleteTextView) findViewById(R.id.publish_shop_name_tv);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        shopNameTv.setAdapter(sugAdapter);


        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        shopNameTv.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(IShareContext.getInstance().getUserLocation().getCity()));
            }
        });

    }

    private void findViewsById() {

        shopNameTv = (AutoCompleteTextView) findViewById(R.id.publish_shop_name_tv);

        chargeRb = (RadioButton) findViewById(R.id.publish_type_charge_rb);
        memberRb = (RadioButton) findViewById(R.id.publish_type_member_rb);

        discountEt = (MaterialEditText) findViewById(R.id.publish_discount_et);
        industryLayout = (RelativeLayout) findViewById(R.id.publish_industry_layout);
        industryTv = (TextView) findViewById(R.id.publish_industry_tv);

        shopLocationEt = (MaterialEditText) findViewById(R.id.publish_shop_location_et);
        ownerAvailableLayout = (LinearLayout) findViewById(R.id.publish_layout);

        addMoreTv = (TextView) findViewById(R.id.publish_add_more_tv);


        friendCb = (CheckBox) findViewById(R.id.publish_ware_friend_cb);
        indirectFriendCb = (CheckBox) findViewById(R.id.publish_ware_indirect_friend_cb);
        allCb = (CheckBox) findViewById(R.id.publish_ware_all_cb);


        cardDesctiptionEt = (MaterialEditText) findViewById(R.id.publish_card_description_et);
        shopLocationIv = (ImageView) findViewById(R.id.publish_shop_location_iv);
        ownerLocationIv = (ImageView) findViewById(R.id.publish_owner_location_iv);
        ownerAvailableLocationEt = (MaterialEditText) findViewById(R.id.publish_owner_location_et);
        ownerAvailableTimeEt = (MaterialEditText) findViewById(R.id.publish_owner_time_et);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {

            uploadDataClient.publishShareItem();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null)
                sugAdapter.add(info.key);
        }
        sugAdapter.notifyDataSetChanged();
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.publish_industry_layout) {
                new MaterialDialog.Builder(PublishItemActivity.this)
                        .title("卡类型")
                        .items(R.array.ware_items)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                String[] array = PublishItemActivity.this.getResources().getStringArray(R.array.ware_items);
                                String selected = array[i];
                                industryTv.setText(selected);
                                trade_type = i;

                                return true;
                            }
                        })
                        .positiveText("确认")
                        .show();

            } else if (v.getId() == R.id.publish_add_more_tv) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.publish_owner_item_layout, null);
                ImageView locationIv = (ImageView) view.findViewById(R.id.publish_owner_location_iv);

                final MaterialEditText locationEt = (MaterialEditText) view.findViewById(R.id.publish_owner_row_location_et);
                MaterialEditText timeEt = (MaterialEditText) view.findViewById(R.id.publish_owner_row_time_et);

                ownerAvailableLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                ownerAvailableLocationEtList.add(locationEt);
                ownerAvailableTimeEtList.add(timeEt);

                // 标示这个ImageView 对应第几个editText
                locationIv.setTag(ownerAvailableLocationEtList.size() - 1);
                Log.v(TAG, "outer " + (ownerAvailableLocationEtList.size() - 1));

                locationIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentEtIndex = (int) v.getTag();
                        ownerAvailableLocationEtList.get(currentEtIndex).setText(IShareContext.getInstance().getUserLocation().getLocationStr());

                    }
                });

            } else if (v.getId() == R.id.publish_shop_location_iv) {

                Intent intent = new Intent(PublishItemActivity.this, PoiSearchActivity.class);
                intent.putExtra(PoiSearchActivity.PARAMETER_SHOP_NAEM, shopNameTv.getText().toString());
                startActivityForResult(intent, PoiSearchActivity.PARAMETER_PULBISH_REQUEST_CODE);

            } else if (v.getId() == R.id.publish_owner_location_iv) {
                ownerAvailableLocationEt.setText(IShareContext.getInstance().getUserLocation().getLocationStr());


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PARAMETER_OWNER_LOCATION_RESULT_CODE) {

        } else if (resultCode == PARAMETER_SHOP_LOCATION_RESULT_CODE) {
            shopLatitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LATITUDE, 0);
            shopLongitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LONGITUDE, 0);

            shopLocationEt.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
    }

    // 上传数据到服务器
    class UploadData {
        public void publishShareItem() {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("owner", Global.phone));
            params.add(new BasicNameValuePair("shop_name", shopNameTv.getText().toString()));
            params.add(new BasicNameValuePair("shop_longitude", shopLongitude + ""));
            params.add(new BasicNameValuePair("shop_latitude", shopLatitude + ""));

            int ware_type = 0;
            if (chargeRb.isChecked()) {
                ware_type = 0;
            }
            if (memberRb.isChecked()) {
                ware_type = 1;
            }
            params.add(new BasicNameValuePair("ware_type", ware_type + ""));
            params.add(new BasicNameValuePair("discount", discountEt.getText().toString()));
            params.add(new BasicNameValuePair("trade_type", trade_type + ""));
            params.add(new BasicNameValuePair("shop_location", shopLocationEt.getText().toString()));

            for (int i = 0; i < ownerAvailableLocationEtList.size(); i++) {
                HashMap hashMap = new HashMap();
                hashMap.put("location", ownerAvailableLocationEtList.get(i).getText().toString());
                hashMap.put("time", ownerAvailableTimeEtList.get(i).getText().toString());
                ownerAvailableList.add(hashMap);
            }
            params.add(new BasicNameValuePair("owner_available", JsonObjectUtil.parseListToJsonArray(ownerAvailableList).toString()));

            ArrayList<String> shareTypeList = new ArrayList<>();
            if (friendCb.isChecked()) {
                shareTypeList.add("0");
            }
            if (indirectFriendCb.isChecked()) {
                shareTypeList.add("1");
            }
            if (allCb.isChecked()) {
                shareTypeList.add("2");
            }

            params.add(new BasicNameValuePair("share_type", JsonObjectUtil.parseListToJsonArray(shareTypeList).toString()));

            Log.v(TAG, "arrive");

            HttpTask.startAsyncDataPostRequest(URLConstant.PUBLISH_SHARE_ITEM, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    Toast.makeText(PublishItemActivity.this, "发卡成功", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                    Log.v(TAG, "error:" + retCode);
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
