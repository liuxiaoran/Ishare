package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.galaxy.ishare.main.MainActivity;
import com.galaxy.ishare.model.OwnerAvailableItem;
import com.galaxy.ishare.utils.ImageParseUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.galaxy.ishare.utils.PhoneUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/5.
 */
public class PublishItemActivity extends ActionBarActivity implements OnGetSuggestionResultListener {

    public static final int PARAMETER_OWNER_LOCATION_RESULT_CODE = 1;
    public static final int PARAMETER_SHOP_LOCATION_RESULT_CODE = 2;
    public static final int PARAMETER_AVAILABLE_RESULT_CODE = 3;

    public static final int PARAMETER_PREVIEW_DELETE_RESULT_CODE = 4;
    public static final String PARETER_DELETE_POSITION = "PARETER_DELETE_POSITION";
    public static final String PARAMETER_RET_OWNER_ADDR = "PARAMETER_RET_OWNER_ADDR";

    //选择图片使用的request
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;

    private static final String TAG = "PublishItemActivity";

    private AutoCompleteTextView shopNameTv;
    private MaterialEditText  cardDesctiptionEt,  ownerAvailableLocationEt, ownerAvailableTimeEt;
    private EditText shopLocationEt,discountEt;
    private MyClickListener myClickListener;
    private RelativeLayout industryLayout;
//    private LinearLayout ownerAvailableLayout;

    private TextView industryTv;
//            addMoreTv;

    private RadioButton chargeRb, memberRb;
    private RadioButton friendRb, indirectFriendRb, allRb;

    private ImageView shopLocationIv, ownerLocationIv;

    private ArrayList<HashMap<String, String>> ownerAvailableList;

//    private ArrayList<MaterialEditText> ownerAvailableLocationEtList;
//    private ArrayList<MaterialEditText> ownerAvailableTimeEtList;

    private SuggestionSearch mSuggestionSearch;
    private ArrayAdapter<String> sugAdapter;

    private double shopLongitude;
    private double shopLatitude;

    private int trade_type = -1;
    int ware_type = -1;

    public UploadData uploadDataClient;

//    public int currentEtIndex;

    private GridView photoGridView;
    private int maxUploadPicCount = 3;
    private ArrayList<Bitmap> gridViewBitmapList;
    private ArrayList<Uri> picUriList;
    private int cameraPicCount = 0;
    // 是否已经选择了maxUploadPicCount 个图片
    private boolean isToMaxPicNumber = false;
    GridViewAdapter gridViewAdapter;

    // 存空闲的时间地点，为了使进入CardOwnerAvailableShowActivity 重新载入数据展示。
    public static  ArrayList<OwnerAvailableItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        ActionBar actionBar = IShareContext.getInstance().createDefaultActionbar(this);
        TextView titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.actionbar_title_tv);
        titleTv.setText("发布新卡");
        findViewsById();

        dataList=new ArrayList<>();

        myClickListener = new MyClickListener();
        industryLayout.setOnClickListener(myClickListener);
//        addMoreTv.setOnClickListener(myClickListener);
        shopLocationIv.setOnClickListener(myClickListener);
//        ownerLocationIv.setOnClickListener(myClickListener);
//        ownerLocationIv.setTag(0);

        ownerAvailableList = new ArrayList<>();
//        ownerAvailableLocationEtList = new ArrayList<>();
//        ownerAvailableTimeEtList = new ArrayList<>();
//        ownerAvailableLocationEtList.add(ownerAvailableLocationEt);
//        ownerAvailableTimeEtList.add(ownerAvailableTimeEt);

        uploadDataClient = new UploadData();





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


        gridViewBitmapList = new ArrayList();
        picUriList = new ArrayList<>();
        gridViewBitmapList.add(ImageParseUtil.getBitmapFromResource(this, R.drawable.card_pic_add));
        photoGridView = (GridView) findViewById(R.id.publishware_cardpic_gridview);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == gridViewBitmapList.size() - 1 && !isToMaxPicNumber) {
                    // 点击的是选择添加图片
                    new MaterialDialog.Builder(PublishItemActivity.this)
                            .title("选择图片来源")
                            .items(R.array.pic_source_items)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                    if (which == 0) {
                                        //选择本地图片
                                        Intent intentFromGallery = new Intent();
                                        intentFromGallery.setType("image/*"); // 设置文件类型
                                        intentFromGallery
                                                .setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(intentFromGallery,
                                                IMAGE_REQUEST_CODE);
                                    } else if (which == 1) {

                                        cameraPicCount++;
                                        //拍照
                                        Intent intentFromCapture = new Intent(
                                                MediaStore.ACTION_IMAGE_CAPTURE);
                                        // 判断存储卡是否可以用，可用进行存储
                                        if (PhoneUtil.hasSdcard()) {

                                            File cardPicFile = new File(PublishItemActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                                    "cardimg" + cameraPicCount + ".jpg");
                                            intentFromCapture.putExtra(
                                                    MediaStore.EXTRA_OUTPUT,
                                                    Uri.fromFile(cardPicFile));
                                            startActivityForResult(intentFromCapture,
                                                    CAMERA_REQUEST_CODE);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                }
                            })
                            .show();
                } else {
                    // 点击的是已经选择的图片，进行预览或删除
                    Intent intent = new Intent(PublishItemActivity.this, PreviewPictureActivity.class);

                    intent.putExtra(PreviewPictureActivity.PARAMETER_POSITION, position);
                    intent.putExtra(PreviewPictureActivity.PARAMENTER_PIC_URI_STRING, picUriList.get(position).toString());
                    PublishItemActivity.this.startActivityForResult(intent, PreviewPictureActivity.PUBLISH_TO_PREVIEW_REQUEST_CODE);
                }
            }
        });

        gridViewAdapter = new GridViewAdapter(this);
        photoGridView.setAdapter(gridViewAdapter);


    }

    private void findViewsById() {

        shopNameTv = (AutoCompleteTextView) findViewById(R.id.publish_shop_name_tv);

        chargeRb = (RadioButton) findViewById(R.id.publish_type_charge_rb);
        memberRb = (RadioButton) findViewById(R.id.publish_type_member_rb);

        discountEt = (EditText) findViewById(R.id.publish_discount_et);
        industryLayout = (RelativeLayout) findViewById(R.id.publish_industry_layout);
        industryTv = (TextView) findViewById(R.id.publish_industry_tv);

        shopLocationEt = (EditText) findViewById(R.id.publish_shop_location_et);
//        ownerAvailableLayout = (LinearLayout) findViewById(R.id.publish_layout);
//
//        addMoreTv = (TextView) findViewById(R.id.publish_add_more_tv);


        friendRb = (RadioButton) findViewById(R.id.publish_ware_friend_rb);
        indirectFriendRb = (RadioButton) findViewById(R.id.publish_ware_indirect_friend_rb);
        allRb = (RadioButton) findViewById(R.id.publish_ware_all_rb);


        cardDesctiptionEt = (MaterialEditText) findViewById(R.id.publish_card_description_et);
        shopLocationIv = (ImageView) findViewById(R.id.publish_shop_location_iv);
        ownerLocationIv = (ImageView) findViewById(R.id.publish_owner_location_iv);
//        ownerAvailableLocationEt = (MaterialEditText) findViewById(R.id.publish_owner_location_et);
//        ownerAvailableTimeEt = (MaterialEditText) findViewById(R.id.publish_owner_time_et);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next_step, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_next) {

            if (chargeRb.isChecked()) {
                ware_type = 0;
            }
            if (memberRb.isChecked()) {
                ware_type = 1;
            }
            if (checkInfo()) {
                Intent intent = new Intent(this, CardOwnerAvailableShowActivity.class);
                startActivityForResult(intent, CardOwnerAvailableShowActivity.PUBLISH_TO_SHOW_REQUST_CODE);
            }


        } else if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    private boolean checkInfo() {
        boolean ret = true;
        if (shopNameTv.getText().toString().equals("") || trade_type == -1 || ware_type == -1 || discountEt.getText().toString().equals("") ||
                cardDesctiptionEt.getText().toString().equals("") || shopLocationEt.getText().toString().equals("")) {

            ret = false;
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
        }
        return ret;
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
                        .items(R.array.trade_items)
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {

                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                                String[] array = PublishItemActivity.this.getResources().getStringArray(R.array.trade_items);
                                String selected = array[i];
                                industryTv.setText(selected);
                                trade_type = i;

                                return true;
                            }
                        })
                        .positiveText("确认")
                        .show();

            }
// else if (v.getId() == R.id.publish_add_more_tv) {
//                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                View view = layoutInflater.inflate(R.layout.publish_owner_item_layout, null);
//                ImageView locationIv = (ImageView) view.findViewById(R.id.publish_owner_location_iv);
//
//                final MaterialEditText locationEt = (MaterialEditText) view.findViewById(R.id.publish_owner_row_location_et);
//                MaterialEditText timeEt = (MaterialEditText) view.findViewById(R.id.publish_owner_row_time_et);
//
//                ownerAvailableLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//                ownerAvailableLocationEtList.add(locationEt);
//                ownerAvailableTimeEtList.add(timeEt);
//
//                // 标示这个ImageView 对应第几个editText
//                locationIv.setTag(ownerAvailableLocationEtList.size() - 1);
//                Log.v(TAG, "outer " + (ownerAvailableLocationEtList.size() - 1));
//
//                locationIv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        currentEtIndex = (int) v.getTag();
//                        ownerAvailableLocationEtList.get(currentEtIndex).setText(IShareContext.getInstance().getUserLocation().getLocationStr());
//
//                    }
//                });

//            }
            else if (v.getId() == R.id.publish_shop_location_iv) {

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

        if (resultCode == PARAMETER_AVAILABLE_RESULT_CODE) {

            // 得到有空的列表，构造ownerAvailableList， 上传服务器
            ArrayList<OwnerAvailableItem> cardItemArrayList = data.getParcelableArrayListExtra(CardOwnerAvailableShowActivity.PARAMETER_RETURN_AVAILABLE_LIST);

            for (OwnerAvailableItem item : cardItemArrayList) {
                HashMap hashMap = new HashMap();
                hashMap.put("name", item.name);
                hashMap.put("phone", item.phone);
                hashMap.put("location", item.location);
                hashMap.put("time", item.time);
                hashMap.put("longitude", item.longitude + "");
                hashMap.put("latitude", item.latitude + "");
                ownerAvailableList.add(hashMap);

            }

            uploadDataClient.publishShareItem();
            this.finish();

            // 释放内存
            dataList=null;
            cardItemArrayList=null;


        } else if (resultCode == PARAMETER_SHOP_LOCATION_RESULT_CODE) {
            shopLatitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LATITUDE, 0);
            shopLongitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LONGITUDE, 0);

            shopLocationEt.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR));
        } else if (resultCode == PARAMETER_PREVIEW_DELETE_RESULT_CODE) {
            int deletePosition = data.getIntExtra(PARETER_DELETE_POSITION, 0);
            gridViewBitmapList.remove(deletePosition);
            gridViewAdapter.notifyDataSetChanged();
        }
        // 处理选择图片的返回
        else if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {

                case IMAGE_REQUEST_CODE:
                    Uri uri = data.getData();
                    Bitmap bitmap = null;
                    bitmap = ImageParseUtil.getBitmapFromUri(uri, this);

                    if (gridViewBitmapList.size() != maxUploadPicCount)
                        gridViewBitmapList.add(gridViewBitmapList.size() - 1, bitmap);

                    else {
                        // 最后一张图片加载
                        gridViewBitmapList.set(gridViewBitmapList.size() - 1, bitmap);
                        isToMaxPicNumber = true;
                    }
                    picUriList.add(uri);

                    gridViewAdapter.notifyDataSetChanged();

                    break;
                case CAMERA_REQUEST_CODE:
                    File cardPicFile = new File(PublishItemActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            "cardimg" + cameraPicCount + ".jpg");

                    Uri u = null;
                    try {
                        u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                cardPicFile.getAbsolutePath(), null, null));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (gridViewBitmapList.size() != maxUploadPicCount)
                        gridViewBitmapList.add(gridViewBitmapList.size() - 1, ImageParseUtil.getBitmapFromUri(u, this));
                    else {
                        // 最后一张图片展示
                        gridViewBitmapList.set(gridViewBitmapList.size() - 1, ImageParseUtil.getBitmapFromUri(u, this));
                        isToMaxPicNumber = true;
                    }
                    picUriList.add(u);
                    gridViewAdapter.notifyDataSetChanged();

                    break;

            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSuggestionSearch.destroy();
        for (int i = 0; i < gridViewBitmapList.size(); i++) {
            if (!gridViewBitmapList.get(i).isRecycled()) {
                gridViewBitmapList.get(i).recycle();
            }
        }
    }

    // 上传数据到服务器
    class UploadData {
        public void publishShareItem() {
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("owner", Global.phone));

            params.add(new BasicNameValuePair("shop_name", shopLocationEt.getText().toString()));
            params.add(new BasicNameValuePair("shop_longitude", shopLongitude + ""));
            params.add(new BasicNameValuePair("shop_latitude", shopLatitude + ""));
            params.add(new BasicNameValuePair("description", cardDesctiptionEt.getText().toString()));


            params.add(new BasicNameValuePair("ware_type", ware_type + ""));
            params.add(new BasicNameValuePair("discount", discountEt.getText().toString()));
            params.add(new BasicNameValuePair("trade_type", trade_type + ""));
            params.add(new BasicNameValuePair("shop_location", shopLocationEt.getText().toString()));

//            for (int i = 0; i < ownerAvailableLocationEtList.size(); i++) {
//                HashMap hashMap = new HashMap();
//                hashMap.put("location", ownerAvailableLocationEtList.get(i).getText().toString());
//                hashMap.put("time", ownerAvailableTimeEtList.get(i).getText().toString());
//                ownerAvailableList.add(hashMap);
//            }
            if (ownerAvailableList.size() >= 1) {
                params.add(new BasicNameValuePair("owner_available", JsonObjectUtil.parseListToJsonArray(ownerAvailableList).toString()));
            }

            int shareType = 0;
            if (friendRb.isChecked()) {
                shareType = 0;
            }
            if (indirectFriendRb.isChecked()) {
                shareType = 1;
            }
            if (allRb.isChecked()) {
                shareType = 2;
            }

            params.add(new BasicNameValuePair("share_type", shareType + ""));


            HttpTask.startAsyncDataPostRequest(URLConstant.PUBLISH_SHARE_ITEM, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {

                    Log.v(TAG, result);
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

    class GridViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public GridViewAdapter(Context Context) {
            inflater = (LayoutInflater) Context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return gridViewBitmapList.size();
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
                convertView = inflater.inflate(R.layout.publishware_cardpic_gridview_item, null);
            }
            ImageView picIv = (ImageView) convertView.findViewById(R.id.publishware_cardpic_gridview_iv);

            picIv.setImageBitmap(gridViewBitmapList.get(position));


            return convertView;
        }
    }


}
