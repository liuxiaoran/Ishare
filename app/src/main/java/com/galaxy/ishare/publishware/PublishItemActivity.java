package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.OwnerAvailableItem;
import com.galaxy.ishare.utils.ImageParseUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.galaxy.ishare.utils.PhoneUtil;
import com.galaxy.ishare.utils.PoiSearchUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.hoang8f.widget.FButton;


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

    private EditText shopLocationEt, descriptionEt;
    private MyClickListener myClickListener;

    private TextView discountTv, changeAvailableTv;

    private RadioButton chargeRb, memberRb, meirongRb, meifaRb, meijiaRb, qinziRb, otherRb;

    private ImageView shopLocationIv, discountIv;

    private LinearLayout availableLayout;
    private FButton publishBtn;

    private ArrayList<HashMap<String, String>> ownerAvailableList;


    private SuggestionSearch mSuggestionSearch;
    private ArrayAdapter<String> sugAdapter;

    private double shopLongitude;
    private double shopLatitude;

    private int trade_type = -1;
    int ware_type = -1;

    public UploadData uploadDataClient;

    public int discountInteger;
    public int discountDecimal;


    private GridView photoGridView;
    private int maxUploadPicCount = 3;
    private ArrayList<Bitmap> gridViewBitmapList;
    private ArrayList<Uri> picUriList;
    private int cameraPicCount = 0;
    // 是否已经选择了maxUploadPicCount 个图片
    private boolean isToMaxPicNumber = false;
    GridViewAdapter gridViewAdapter;

    // 存空闲的时间地点，为了使进入CardOwnerAvailableShowActivity 重新载入数据展示。
    public static ArrayList<OwnerAvailableItem> dataList;



    Handler poiSearchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PoiSearchUtil.POI_WHAT) {


                int count = msg.arg1;

                shopLocationEt.setHint("找到了" + count + "个店,可定位选择");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        IShareContext.getInstance().createDefaultHomeActionbar(this, "发布新卡");
        findViewsById();

        dataList = new ArrayList<>();

        myClickListener = new MyClickListener();


        shopLocationIv.setOnClickListener(myClickListener);


        ownerAvailableList = new ArrayList<>();


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
                if (shopNameTv.getText().toString().length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                if (shopNameTv.getText().toString() != null && !shopNameTv.getText().toString().equals("")) {
                    mSuggestionSearch
                            .requestSuggestion((new SuggestionSearchOption())
                                    .keyword(shopNameTv.getText().toString()).city(IShareContext.getInstance().getUserLocationNotNull().getCity()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {

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


        // 监听店名输入框失去焦点，查询
        shopNameTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false) {
                    if (!shopNameTv.getText().toString().equals("")) {
                        PoiSearchUtil.searchPoiInCity(shopNameTv.getText().toString(), poiSearchHandler, 0);
                    }
                }
            }
        });


        shopLocationIv.setOnClickListener(myClickListener);
        changeAvailableTv.setOnClickListener(myClickListener);
        publishBtn.setOnClickListener(myClickListener);
        discountIv.setOnClickListener(myClickListener);


    }

    private void findViewsById() {

        shopNameTv = (AutoCompleteTextView) findViewById(R.id.publish_shop_name_tv);

        shopLocationEt = (EditText) findViewById(R.id.publish_shop_location_et);
        discountTv = (TextView) findViewById(R.id.publish_ware_discount_tv);


        chargeRb = (RadioButton) findViewById(R.id.publish_type_charge_rb);
        memberRb = (RadioButton) findViewById(R.id.publish_type_member_rb);
        meirongRb = (RadioButton) findViewById(R.id.publish_meirong_rb);
        meifaRb = (RadioButton) findViewById(R.id.publish_meifa_rb);
        meijiaRb = (RadioButton) findViewById(R.id.publish_meijia_rb);
        qinziRb = (RadioButton) findViewById(R.id.publish_qinzi_rb);
        otherRb = (RadioButton) findViewById(R.id.publish_other_rb);


        shopLocationIv = (ImageView) findViewById(R.id.publish_shop_location_iv);

        descriptionEt = (EditText) findViewById(R.id.publish_card_description_et);

        changeAvailableTv = (TextView) findViewById(R.id.publish_ware_choose_available_tv);

        availableLayout = (LinearLayout) findViewById(R.id.publish_ware_avaialble_layout);


        publishBtn = (FButton) findViewById(R.id.publish_ware_publish_btn);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    private boolean checkInfo() {
        boolean ret = true;
        if (shopNameTv.getText().toString().equals("") || trade_type == -1 || ware_type == -1 || discountTv.getText().toString().equals("") ||
                descriptionEt.getText().toString().equals("") || shopLocationEt.getText().toString().equals("")) {

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

    NumberPicker picker2 = null;
    NumberPicker picker1 = null;
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.publish_shop_location_iv) {

                if (shopNameTv.getText().toString().equals("")) {

                    Toast.makeText(PublishItemActivity.this, "请填写店名", Toast.LENGTH_SHORT).show();

                } else {


                    Intent intent = new Intent(PublishItemActivity.this, PoiSearchActivity.class);
                    intent.putExtra(PoiSearchActivity.PARAMETER_SHOP_NAEM, shopNameTv.getText().toString());
                    startActivityForResult(intent, PoiSearchActivity.PARAMETER_PULBISH_REQUEST_CODE);
                }

            } else if (v.getId() == R.id.publish_ware_publish_btn) {

                if (checkInfo()) {
                    uploadDataClient.publishCard();
                }


            } else if (v.getId() == R.id.publish_shop_location_iv) {

                Intent intent = new Intent(PublishItemActivity.this, PoiSearchActivity.class);
                intent.putExtra(PoiSearchActivity.PARAMETER_SHOP_NAEM, shopNameTv.getText().toString());
                startActivityForResult(intent, PoiSearchActivity.PARAMETER_PULBISH_REQUEST_CODE);


            } else if (v.getId() == R.id.publish_ware_choose_available_tv) {

            } else if (v.getId() == R.id.publish_discount_arrow_iv) {


                MaterialDialog discountDialog = new MaterialDialog.Builder(PublishItemActivity.this).title("填写卡的折扣")
                        .customView(R.layout.publish_card_discount_dialog, true)
                        .positiveText("确定")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                discountInteger = picker1.getValue();
                                discountDecimal = picker2.getValue();
                                discountTv.setText(Double.parseDouble(discountInteger + "." + discountDecimal) + "");
                            }
                        })
                        .build();
                View view = discountDialog.getCustomView();
                final View positiveAction = discountDialog.getActionButton(DialogAction.POSITIVE);
                picker1 = (NumberPicker) view.findViewById(R.id.publish_card_numberpicker1_discount);
                picker2 = (NumberPicker) view.findViewById(R.id.publish_card_numberpicker2_discount);
                picker1.setMaxValue(9);
                picker1.setMinValue(0);
                picker2.setMaxValue(9);
                picker2.setMinValue(0);
                picker1.setFocusable(true);
                picker1.setFocusableInTouchMode(true);
                picker2.setFocusable(true);
                picker2.setFocusableInTouchMode(true);


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

            uploadDataClient.publishCard();
            this.finish();

            // 释放内存
            dataList = null;
            cardItemArrayList = null;


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
//        PoiSearchUtil.destroyPoiSearch();
    }

    // 上传数据到服务器
    class UploadData {
        public void publishCard() {

            QiniuUtil qiniuUtil = QiniuUtil.getInstance();

            final String[] imageKey = new String[picUriList.size()];


            for (int i = 0; i < picUriList.size(); i++) {

                imageKey[i] = qiniuUtil.generateKey("card");
                String filePath = ImageParseUtil.getImageAbsolutePath(PublishItemActivity.this, picUriList.get(i));
                Log.v(TAG, "filepath " + i + " " + filePath);
                qiniuUtil.uploadFileDefault(filePath, imageKey[i], new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {

                        if (responseInfo.isOK()) {
                            Log.v(TAG, "ok");
                        }
                    }
                });
            }
            publishShareItem(imageKey);

        }

        public void publishShareItem(String[] imageKey) {


            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("owner", IShareContext.getInstance().getCurrentUser().getUserId()));

            params.add(new BasicNameValuePair("shop_name", shopNameTv.getText().toString()));
            params.add(new BasicNameValuePair("shop_longitude", shopLongitude + ""));
            params.add(new BasicNameValuePair("shop_latitude", shopLatitude + ""));
            params.add(new BasicNameValuePair("description", descriptionEt.getText().toString()));


            params.add(new BasicNameValuePair("ware_type", ware_type + ""));
            params.add(new BasicNameValuePair("discount", discountTv.getText().toString()));
            params.add(new BasicNameValuePair("trade_type", trade_type + ""));
            params.add(new BasicNameValuePair("shop_location", shopLocationEt.getText().toString()));

//            for (int i = 0; i < ownerAvailableLocationEtList.size(); i++) {
//                HashMap hashMap = new HashMap();
//                hashMap.put("location", ownerAvailableLocationEtList.get(i).getText().toString());
//                hashMap.put("time", ownerAvailableTimeEtList.get(i).getText().toString());
//                ownerAvailableList.add(hashMap);
//            }
            if (ownerAvailableList.size() >= 1) {
                params.add(new BasicNameValuePair("owner_available", JsonObjectUtil.parseListToJsonString(ownerAvailableList)));
            }


            if (imageKey != null && imageKey.length > 0) {
                String[] imgs = new String[imageKey.length];
                for (int i = 0; i < imageKey.length; i++) {
                    imgs[i] = QiniuUtil.getInstance().getFileUrl(imageKey[i]);
                    Log.v(TAG, "file url:" + i + imgs[i]);

                }

                String arrayStr = JsonObjectUtil.parseArrayToJsonString(imgs);
                params.add(new BasicNameValuePair("img", arrayStr));
                Log.v(TAG, "img: " + arrayStr);

            }
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


    @Override
    protected void onPause() {
        super.onPause();

    }
}
