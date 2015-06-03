package com.galaxy.ishare.main;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.bindphone.BindPhoneActivity;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.database.FriendDao;
import com.galaxy.ishare.database.InviteFriendDao;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.order.OrderFragment;
import com.galaxy.ishare.publishware.PublishItemActivity;
import com.galaxy.ishare.sharedcard.ItemListFragment;
import com.galaxy.ishare.usercenter.MeFragment;
import com.galaxy.ishare.utils.AppAsyncHttpClient;
import com.galaxy.ishare.utils.PhoneContactManager;
import com.galaxy.ishare.utils.SPUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {


    public static final String PUBLISH_TO_BING_PHONE = "PUBLISH_TO_BING_PHONE";
    public static int orderType = 0;

    private RadioGroup mTabGroup = null;
    private RadioButton mShareItemButton, orderButton, mMeButton;

    private Fragment mShareItemFragment;
    private Fragment mMeFragment;
    private Fragment orderFragment;
//    private TextView mTitle;

    private int[] mRadioId = new int[]{R.id.shareBtn, R.id.MeButton};

    private static final String TAG = "mainactivity";

    private String contactFilePath = "/data/data/files/phoneContact.txt";

    private FriendDao friendDao;
    private InviteFriendDao inviteFriendDao;

    private LocationClient mLocationClient;

    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";

    private ActionBar actionBar;
    private TextView titleTv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recoverActionBar("分享");

        // 将titleTv放在中间
//        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//        titleTv.measure(w, h);
//        int width = titleTv.getMeasuredWidth();
//        int screenWidth = Global.screenWidth;
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleTv.getLayoutParams(); /*或者 LinearLayout.LayoutParams p = new  LinearLayout.LayoutParams(width,height); 这里的width和height是以像素为单位*/
//        lp.setMargins( (screenWidth) / 2, 0, 0, 0);
//        titleTv.setLayoutParams(lp);


//        mTitle = (TextView)findViewById(R.id.title);

        initTabs();

        friendDao = FriendDao.getInstance(this);
        inviteFriendDao = InviteFriendDao.getInstance(this);

        mShareItemFragment = new ItemListFragment();
        // 一个trasaction 只能commit一次
        FragmentTransaction mCurTransaction = getFragmentManager().beginTransaction();
        mCurTransaction.add(R.id.fragment_container, mShareItemFragment);
        mCurTransaction.commit();

        setTab();

//        if(!SPUtil.getChooseFav()) {
//        	mTitle.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					showFavDialog();
//				}
//			}, 1000);
//        }

        if (IShareContext.getInstance().firstLogin()) {
            new Thread() {
                @Override
                public void run() {
                    ArrayList<User> phoneContacts = IShareContext.getInstance().getPhoneContacts();
                    File contactFile = PhoneContactManager.encodePhoneContactFile(phoneContacts);
                    Log.v(TAG, "" + contactFile.getAbsolutePath());

                    uploadContactData();

                }
            }.start();
        }

        // 利用百度地图获取位置
        mLocationClient = ((IShareApplication) getApplication()).mLocationClient;
        initLocation();
        mLocationClient.start();
    }

    private void recoverActionBar(String title) {
        if (title.equals("分享")) {
            actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.main_share_action_bar, false);
            ImageView searchIv = (ImageView) actionBar.getCustomView().findViewById(R.id.main_search_iv);
            TextView publishTv = (TextView) actionBar.getCustomView().findViewById(R.id.main_publish_tv);
            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.main_search_iv) {

                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);

                    } else if (v.getId() == R.id.main_publish_tv) {
                        if (IShareContext.getInstance().getCurrentUser().getUserPhone() == null) {

                            Intent intent = new Intent(MainActivity.this, BindPhoneActivity.class);
                            intent.putExtra(BindPhoneActivity.PARAMETER_WHO_COME, PUBLISH_TO_BING_PHONE);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(MainActivity.this, PublishItemActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            };
            searchIv.setOnClickListener(mOnClickListener);
            publishTv.setOnClickListener(mOnClickListener);
        } else {
            actionBar = IShareContext.getInstance().createActionbar(this, false, title);
        }
        titleTv = (TextView) actionBar.getCustomView().findViewById(R.id.action_bar_title_tv);
        titleTv.setText(title);
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setOpenGps(true);// 打开gps
        int span = 1000;
        option.setScanSpan(span);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }



    private void uploadContactData() {

        RequestParams uploadParams = new RequestParams();

        try {
            uploadParams.put("upload_contact", new File(contactFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AppAsyncHttpClient.post(URLConstant.UPLOAD_CONTACT, uploadParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

//                try {
////                    JSONArray jsonArray = response.getJSONArray("friends");
////                    for (int i=0;i<jsonArray.length();i++) {
////                        JSONObject contact= jsonArray.getJSONObject(i);
////                        friendDao.add(new Friend(contact.getString("name"),contact.getString("phone")));
////
////                    }
////                    JSONArray inviteFriendArray =  response.getJSONArray("invite_friends");
////                    for (int  i=0;i<jsonArray.length();i++) {
////                        JSONObject contact  = inviteFriendArray.getJSONObject(i);
////                        inviteFriendDao.add(new InviteFriend(contact.getString("name"),contact.getString("phone")));
////
////                    }
////                    Intent  intent  = new Intent();
////                    intent.setAction(BroadcastConstant.UPDATE_FRIEND_LIST);
////                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONArray errorResponse) {

            }


        });


    }


    private void showFavDialog() {

        LayoutInflater inflater = LayoutInflater.from(IShareContext.mContext);
        View innerView = inflater.inflate(R.layout.choose_fav, null);
        Builder builder = new AlertDialog.Builder(this).setTitle("choose").setIcon(R.drawable.ic_launcher).setView(innerView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtil.setChooseFav(true);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setTab() {
        int tab = getIntent().getIntExtra("tab", -1);
        if (tab >= 0 && tab < mRadioId.length) {
            mTabGroup.check(mRadioId[tab]);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setTab();
    }

    private void initTabs() {
        mTabGroup = (RadioGroup) findViewById(R.id.tab_group);
        mShareItemButton = (RadioButton) findViewById(R.id.shareBtn);
        orderButton = (RadioButton) findViewById(R.id.orderBtn);
        mMeButton = (RadioButton) findViewById(R.id.MeButton);
        mTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                if (checkedId == mShareItemButton.getId()) {
                    recoverActionBar("分享");
                    if (mShareItemFragment == null) {
                        mShareItemFragment = new ItemListFragment();

                        transaction.add(R.id.fragment_container, mShareItemFragment);
                    }
                    if (mMeFragment != null) {
                        transaction.hide(mMeFragment);
                    }
                    if (orderFragment != null) {
                        transaction.hide(orderFragment);
                    }

                    transaction.show(mShareItemFragment);
                } else if (checkedId == orderButton.getId()) {
                    if (orderFragment == null) {
                        orderFragment = new OrderFragment();
                        transaction.add(R.id.fragment_container, orderFragment);
                    }
                    if (mShareItemFragment != null) {
                        transaction.hide(mShareItemFragment);
                    }
                    if (mMeFragment != null) {
                        transaction.hide(mMeFragment);
                    }
                    actionBar = IShareContext.getInstance().createCustomActionBar(MainActivity.this, R.layout.main_order_action_bar, false);
                    final TextView borrowTv = (TextView) actionBar.getCustomView().findViewById(R.id.order_actionbar_borrow_tv);
                    final TextView lendTv = (TextView) actionBar.getCustomView().findViewById(R.id.order_actionbar_lend_tv);


                    View.OnClickListener textViewListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.order_actionbar_borrow_tv) {
                                borrowTv.setTextColor(getResources().getColor(R.color.huise));
                                borrowTv.setBackgroundResource(R.drawable.order_actionbar_white_tv);

                                lendTv.setTextColor(getResources().getColor(R.color.white));
                                lendTv.setBackgroundResource(R.drawable.order_actionbar_gray_tv);

                                FragmentTransaction borrowTransaction = getFragmentManager().beginTransaction();
//                                  Bundle borrowBundle  = new Bundle();
//                                  borrowBundle.putInt(OrderFragment.PARAMETER_ODER_TYPE, OrderFragment.BORROW_ORDER);
//                                  orderFragment.setArguments(borrowBundle);
                                orderType = OrderFragment.BORROW_ORDER;
                                borrowTransaction.show(orderFragment);
                                borrowTransaction.commit();

                            } else if (v.getId() == R.id.order_actionbar_lend_tv) {
                                borrowTv.setTextColor(getResources().getColor(R.color.white));
                                borrowTv.setBackgroundResource(R.drawable.order_actionbar_gray_tv);

                                lendTv.setTextColor(getResources().getColor(R.color.huise));
                                lendTv.setBackgroundResource(R.drawable.order_actionbar_white_tv);

                                FragmentTransaction lendTransaction = getFragmentManager().beginTransaction();
//                                  Bundle lendBundle  = new Bundle();
//                                  lendBundle.putInt(OrderFragment.PARAMETER_ODER_TYPE, OrderFragment.LEND_ORDER);
//                                  orderFragment.setArguments(lendBundle);
                                orderType = OrderFragment.LEND_ORDER;
                                lendTransaction.show(orderFragment);
                                lendTransaction.commit();

                            }
                        }
                    };
                    borrowTv.setOnClickListener(textViewListener);
                    lendTv.setOnClickListener(textViewListener);
                    Bundle bundle = new Bundle();
                    bundle.putInt(OrderFragment.PARAMETER_ODER_TYPE, OrderFragment.BORROW_ORDER);
                    orderFragment.setArguments(bundle);
                    transaction.show(orderFragment);

                } else if (checkedId == mMeButton.getId()) {
                    recoverActionBar("我");
                    if (mMeFragment == null) {
                        mMeFragment = new MeFragment();
                        transaction.add(R.id.fragment_container, mMeFragment);
                    }
                    if (mShareItemFragment != null) {
                        transaction.hide(mShareItemFragment);
                    }
                    if (orderFragment != null) {
                        transaction.hide(orderFragment);
                    }
                    transaction.show(mMeFragment);
                }
                transaction.commit();

            }
        });
    }



    @Override
    protected void onStop() {
        if (mLocationClient != null)
            mLocationClient.stop();
        super.onStop();
    }
//    //    // dialog询问读取联系人，开启线程读取联系人
//    private void giveReadContactPermission() {
//        new MaterialDialog.Builder(this)
//                .title("获取联系人")
//                .content("如果您同意则会匹配您的手机中的好友")
//                .positiveText("我同意")
//                .negativeText("不同意")
//                .callback(new MaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onPositive(MaterialDialog dialog) {
//                        super.onPositive(dialog);
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                ArrayList<User> phoneContacts = IShareContext.getInstance().getPhoneContacts();
//                                File contactFile = PhoneContactManager.encodePhoneContactFile(phoneContacts);
//                                Log.v(TAG, "" + contactFile.getAbsolutePath());
//                            }
//                        }.start();
//                    }
//
//                    @Override
//                    public void onNegative(MaterialDialog dialog) {
//                        super.onNegative(dialog);
//                    }
//
//                    @Override
//                    public void onNeutral(MaterialDialog dialog) {
//                        super.onNeutral(dialog);
//                    }
//                }).show();
//

}
