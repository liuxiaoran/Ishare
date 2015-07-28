package com.galaxy.ishare.publishware;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.UserLocationDao;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.UserLocation;
import com.galaxy.ishare.usercenter.me.CardAddrActivity;
import com.galaxy.ishare.utils.ImageParseUtil;
import com.galaxy.ishare.utils.JsonObjectUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;


/**
 * Created by liuxiaoran on 15/5/5.
 */
public class PublishItemActivity extends IShareActivity implements OnGetSuggestionResultListener {

    public static final int PARAMETER_OWNER_LOCATION_RESULT_CODE = 1;
    public static final int PARAMETER_SHOP_LOCATION_RESULT_CODE = 2;
    public static final int PARAMETER_AVAILABLE_RESULT_CODE = 3;

    public static final int ADDR_SEARCH_TO_PUBLISH = 4;
    public static final int PUBLISH_TO_ADDR = 5;
    public static final int ADDR_TO_PUBLISH_RESULT_CODE = 6;

    public static final int PARAMETER_PREVIEW_DELETE_RESULT_CODE = 4;
    public static final String PARETER_DELETE_POSITION = "PARETER_DELETE_POSITION";
    public static final String PARAMETER_RET_OWNER_ADDR = "PARAMETER_RET_OWNER_ADDR";

    //选择图片使用的request
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;

    private static final String TAG = "PublishItemActivity";

    private TextView shopNameTv;

    private EditText descriptionEt;
    private MyClickListener myClickListener;

    private TextView discountTv, changeAvailableTv, shopLocationTv, commissionTv;

    private RadioButton chargeRb, memberRb, meirongRb, meifaRb, meijiaRb, qinziRb, otherRb;

    private ImageView discountIv;

    private LinearLayout availableLayout;
    private FButton publishBtn;

    //    private ArrayList<HashMap<String, String>> ownerAvailableList;
    private int locationId = -1;

    private ArrayAdapter<String> sugAdapter;

    private double shopLongitude;
    private double shopLatitude;

    private int trade_type = -1;
    int ware_type = -1;

    public UploadData uploadDataClient;

    public int discountInteger;
    public int discountDecimal;
    public int commissionInteger;
    public int commissionDecimal;


    private GridView photoGridView;
    private int maxUploadPicCount = 3;
    //    private ArrayList<Bitmap> gridViewBitmapList;
    private ArrayList<Uri> picUriList;
    private int cameraPicCount = 0;
    // 是否已经选择了maxUploadPicCount 个图片
    private boolean isToMaxPicNumber = false;

    GridViewAdapter gridViewAdapter;

    private LinearLayout shopNameLayout;

    private RelativeLayout discountLayout, commissionLayout;
    private ImageView shopNameHintIv, shopAddrHintIv, discountHintIv, descriptionHintIv, commissionHintIv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publishware_activity);

        IShareContext.getInstance().createDefaultHomeActionbar(this, "发布新卡");
        findViewsById();


        myClickListener = new MyClickListener();


        uploadDataClient = new UploadData();


        shopNameTv = (TextView) findViewById(R.id.publish_shop_name_tv);
        picUriList = new ArrayList<>();
        photoGridView = (GridView) findViewById(R.id.publishware_cardpic_gridview);

        gridViewAdapter = new GridViewAdapter(this);
        photoGridView.setAdapter(gridViewAdapter);


        changeAvailableTv.setOnClickListener(myClickListener);
        publishBtn.setOnClickListener(myClickListener);
        discountIv.setOnClickListener(myClickListener);
        discountLayout.setOnClickListener(myClickListener);

        shopNameLayout.setOnClickListener(myClickListener);

        writeAddrIntoLayout();

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
                }
            }
        });

        commissionLayout.setOnClickListener(myClickListener);
    }

    private void findViewsById() {

        shopNameTv = (TextView) findViewById(R.id.publish_shop_name_tv);

        shopNameLayout = (LinearLayout) findViewById(R.id.publish_shop_name_layout);
        shopLocationTv = (TextView) findViewById(R.id.publish_shop_location_tv);
        discountTv = (TextView) findViewById(R.id.publish_ware_discount_tv);
        discountIv = (ImageView) findViewById(R.id.publish_discount_arrow_iv);


        chargeRb = (RadioButton) findViewById(R.id.publish_type_charge_rb);
        memberRb = (RadioButton) findViewById(R.id.publish_type_member_rb);
        meirongRb = (RadioButton) findViewById(R.id.publish_meirong_rb);
        meifaRb = (RadioButton) findViewById(R.id.publish_meifa_rb);
        meijiaRb = (RadioButton) findViewById(R.id.publish_meijia_rb);
        qinziRb = (RadioButton) findViewById(R.id.publish_qinzi_rb);
        otherRb = (RadioButton) findViewById(R.id.publish_other_rb);


        descriptionEt = (EditText) findViewById(R.id.publish_card_description_et);

        changeAvailableTv = (TextView) findViewById(R.id.publish_ware_choose_addr_tv);

        availableLayout = (LinearLayout) findViewById(R.id.publish_ware_avaialble_layout);


        publishBtn = (FButton) findViewById(R.id.publish_ware_publish_btn);
        discountLayout = (RelativeLayout) findViewById(R.id.publishware_discount_layout);

        shopNameHintIv = (ImageView) findViewById(R.id.publishware_shopname_hint_iv);
        shopAddrHintIv = (ImageView) findViewById(R.id.publish_shop_addr_hint_iv);
        discountHintIv = (ImageView) findViewById(R.id.publish_discount_arrow_iv);
        descriptionHintIv = (ImageView) findViewById(R.id.publish_description_hint_iv);

        commissionHintIv = (ImageView) findViewById(R.id.publish_ware_commission_arrow_iv);
        commissionLayout = (RelativeLayout) findViewById(R.id.publishware_commission_layout);
        commissionTv = (TextView) findViewById(R.id.publish_ware_commission_tv);


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

        if (chargeRb.isChecked()) {

            ware_type = 0;
        } else if (memberRb.isChecked()) {
            ware_type = 1;
        }
        if (meirongRb.isChecked()) {
            trade_type = 1;
        } else if (meifaRb.isChecked()) {
            trade_type = 2;
        } else if (meijiaRb.isChecked()) {
            trade_type = 3;
        } else if (qinziRb.isChecked()) {
            trade_type = 4;
        } else if (otherRb.isChecked()) {
            trade_type = 5;
        }


        if (shopNameTv.getText().toString().equals("") || trade_type == -1 || ware_type == -1 || discountTv.getText().toString().equals("") ||
                shopLocationTv.getText().toString().equals("") ||
                commissionTv.getText().toString().equals("")) {

            ret = false;
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
        } else if (locationId == -1) {
            ret = false;
            Toast.makeText(this, "请填写您的地址", Toast.LENGTH_LONG).show();
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
    FButton confirmBtn = null;

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.publish_shop_name_layout) {
                Intent intent = new Intent(PublishItemActivity.this, ShopLocateSearchActivity.class);
                intent.putExtra(ShopLocateSearchActivity.PARAMETER_WHO_COME, ShopLocateSearchActivity.PUBLISHCARD_TO_SEARCH);
                startActivityForResult(intent, 0);
            } else if (v.getId() == R.id.publish_ware_publish_btn) {

                if (checkInfo()) {
                    uploadDataClient.publishCard();

                }


            } else if (v.getId() == R.id.publish_ware_choose_addr_tv) {

                Intent intent = new Intent(PublishItemActivity.this, CardAddrActivity.class);
                intent.putExtra(CardAddrActivity.PARAMETER_WHO_COME, PUBLISH_TO_ADDR);
                startActivityForResult(intent, CardOwnerAvailableAddrSearchActivity.PUBLISH_TO_MAP_REQUEST_CODE);

            } else if (v.getId() == R.id.publishware_discount_layout) {


                MaterialDialog discountDialog = new MaterialDialog.Builder(PublishItemActivity.this).title("填写卡的折扣")
                        .customView(R.layout.publish_card_discount_dialog, true)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                discountInteger = picker1.getValue();
                                discountDecimal = picker2.getValue();
                                discountTv.setText(Double.parseDouble(discountInteger + "." + discountDecimal) + "折");
                                discountHintIv.setImageResource(R.drawable.icon_green_check);
                            }
                        })
                        .positiveText("确定")
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
                picker1.setValue(7);
                picker2.setValue(0);


                discountDialog.show();


            } else if (v.getId() == R.id.publishware_commission_layout) {
                MaterialDialog commissionDialog = new MaterialDialog.Builder(PublishItemActivity.this).title("填写服务费")
                        .customView(R.layout.publish_card_commission_dialog, true)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                commissionInteger = picker1.getValue();
                                commissionDecimal = picker2.getValue();
                                commissionTv.setText(Double.parseDouble(commissionInteger + "." + commissionDecimal) + "%");
                                commissionHintIv.setImageResource(R.drawable.icon_green_check);
                            }
                        })
                        .positiveText("确定")
                        .build();
                View view = commissionDialog.getCustomView();
                final View positiveAction = commissionDialog.getActionButton(DialogAction.POSITIVE);
                picker1 = (NumberPicker) view.findViewById(R.id.publish_card_numberpicker1_commission);
                picker2 = (NumberPicker) view.findViewById(R.id.publish_card_numberpicker2_commission);
                picker1.setMaxValue(9);
                picker1.setMinValue(0);
                picker2.setMaxValue(9);
                picker2.setMinValue(0);
                picker1.setFocusable(true);
                picker1.setFocusableInTouchMode(true);
                picker2.setFocusable(true);
                picker2.setFocusableInTouchMode(true);
                picker1.setValue(2);
                picker2.setValue(0);


                commissionDialog.show();

            }
        }
    }

    private void writeAddrIntoLayout() {
        availableLayout.removeAllViews();
        // 得到有空的列表，构造ownerAvailableList
        ArrayList<UserLocation> cardItemArrayList = UserLocationDao.getInstance(this).query(IShareContext.getInstance().getCurrentUser().getUserId());
        if (cardItemArrayList != null) {
            for (UserLocation item : cardItemArrayList) {

                if (item.isChoosed) {
                    locationId = item.serverId;
                    Log.v(TAG, "locationId is " + locationId);
                    View availableItem = getLayoutInflater().inflate(R.layout.publishware_available_item, null);
                    TextView addrTv = (TextView) availableItem.findViewById(R.id.publishware_available_item_addr_tv);
                    addrTv.setText(item.address);
                    availableLayout.addView(availableItem);
                }


            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "resultcode: " + resultCode);
//        if (resultCode == PARAMETER_AVAILABLE_RESULT_CODE || resultCode == ADDR_SEARCH_TO_PUBLISH) {
//            Log.v(TAG, "arrive result");
//
//            writeAddrIntoLayout();
//
//
//        }

        if (resultCode == PARAMETER_SHOP_LOCATION_RESULT_CODE) {
            shopLatitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LATITUDE, 0);
            shopLongitude = data.getDoubleExtra(PoiSearchActivity.PARAMETER_SHOP_LONGITUDE, 0);

            shopLocationTv.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR));
            shopNameTv.setText(data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_NAME));
            Log.v(TAG, "shop addr" + data.getStringExtra(PoiSearchActivity.PARAMETER_SHOP_ADDR));

            shopNameHintIv.setImageResource(R.drawable.icon_green_check);
            shopAddrHintIv.setImageResource(R.drawable.icon_green_check);

        } else if (resultCode == ADDR_TO_PUBLISH_RESULT_CODE) {
            Log.v(TAG, "arrive addtopubish request code");
            writeAddrIntoLayout();
        }
//        else if (resultCode == PARAMETER_PREVIEW_DELETE_RESULT_CODE) {
//            int deletePosition = data.getIntExtra(PARETER_DELETE_POSITION, 0);
//            picUriList.remove(deletePosition);
//            gridViewAdapter.notifyDataSetChanged();
//        }
        // 处理选择图片的返回
        else if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {

                case IMAGE_REQUEST_CODE:
                    Uri uri = data.getData();
                    Bitmap bitmap = null;
//                    bitmap = ImageParseUtil.getBitmapFromUri(uri, this);
                    Log.v(TAG, "uri is: " + uri.toString());
//                    if (gridViewBitmapList.size() != maxUploadPicCount)
//                        gridViewBitmapList.add(gridViewBitmapList.size() - 1, bitmap);
//                    else {
//                        // 最后一张图片加载
//                        gridViewBitmapList.set(gridViewBitmapList.size() - 1, bitmap);
//                        isToMaxPicNumber = true;
//                    }
                    picUriList.add(uri);
                    if (picUriList.size() == maxUploadPicCount) {
                        isToMaxPicNumber = true;
                    }
                    gridViewAdapter.notifyDataSetChanged();

                    break;
//                case CAMERA_REQUEST_CODE:
//                    File cardPicFile = new File(PublishItemActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
//                            "cardimg" + cameraPicCount + ".jpg");
//
//                    Uri u = null;
//                    try {
//                        u = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
//                                cardPicFile.getAbsolutePath(), null, null));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    if (gridViewBitmapList.size() != maxUploadPicCount)
//                        gridViewBitmapList.add(gridViewBitmapList.size() - 1, ImageParseUtil.getBitmapFromUri(u, this));
//                    else {
//                        // 最后一张图片展示
//                        gridViewBitmapList.set(gridViewBitmapList.size() - 1, ImageParseUtil.getBitmapFromUri(u, this));
//                        isToMaxPicNumber = true;
//                    }
//                    picUriList.add(u);
//                    gridViewAdapter.notifyDataSetChanged();
//
//                    break;

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        for (int i = 0; i < gridViewBitmapList.size(); i++) {
//            if (!gridViewBitmapList.get(i).isRecycled()) {
//                gridViewBitmapList.get(i).recycle();
//            }
//        }
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
            params.add(new BasicNameValuePair("shop_location", shopLocationTv.getText().toString()));
            params.add(new BasicNameValuePair("service_charge", commissionInteger + "." + commissionDecimal));


            params.add(new BasicNameValuePair("location_id", locationId + ""));


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
                    PublishItemActivity.this.finish();
                }

                @Override
                public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                    Log.v(TAG, "error:" + retCode);
                    Toast.makeText(PublishItemActivity.this, "发卡失败，请重试", Toast.LENGTH_LONG).show();
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
            int ret = 0;
            if (picUriList.size() < maxUploadPicCount) {
                ret = picUriList.size() + 1;
            } else {
                ret = picUriList.size();
            }
            return ret;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView picIv = null;

            Log.v(TAG, "convertview is null  " + position);
            convertView = inflater.inflate(R.layout.publishware_cardpic_gridview_item, null);
            picIv = (ImageView) convertView.findViewById(R.id.publishware_cardpic_gridview_iv);

            final ImageView deleteIv = (ImageView) convertView.findViewById(R.id.publishware_cardpic_delete_iv);
            convertView.setTag(picIv);


            picIv.setTag(0);


            if (picUriList.size() == maxUploadPicCount) {
                ImageLoader.getInstance().displayImage(picUriList.get(position).toString(), picIv);
            } else {
                if (position <= picUriList.size() - 1) {

                    ImageLoader.getInstance().displayImage(picUriList.get(position).toString(), picIv);
                } else if (position == picUriList.size()) {
                    Log.v(TAG, "postion: add " + position + " " + picIv.toString());
                    picIv.setTag(position);
                    if (position == (int) picIv.getTag())
                        picIv.setImageResource(R.drawable.card_pic_add);
                }
            }
            deleteIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picUriList.remove(position);
                    gridViewAdapter.notifyDataSetChanged();
                    isToMaxPicNumber = false;

                }
            });
            final ImageView finalPicIv = picIv;
            picIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (position == picUriList.size() && !isToMaxPicNumber) {
                        //选择本地图片
                        Intent intentFromGallery = new Intent();
                        intentFromGallery.setType("image/*"); // 设置文件类型
                        intentFromGallery
                                .setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intentFromGallery,
                                IMAGE_REQUEST_CODE);
                    } else {
                        int clickCount = (int) finalPicIv.getTag();
                        clickCount++;
                        if (clickCount % 2 == 1) {
                            deleteIv.setVisibility(View.VISIBLE);
                        } else {
                            deleteIv.setVisibility(View.INVISIBLE);
                        }
                        finalPicIv.setTag(clickCount);

                    }

                }
            });


            return convertView;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
}
