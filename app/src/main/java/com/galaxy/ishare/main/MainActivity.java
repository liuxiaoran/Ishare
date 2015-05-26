package com.galaxy.ishare.main;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.galaxy.ishare.BindPhone.BindPhoneActivity;
import com.galaxy.ishare.IShareApplication;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.contact.ContactFragment;
import com.galaxy.ishare.database.FriendDao;
import com.galaxy.ishare.database.InviteFriendDao;
import com.galaxy.ishare.mapLBS.CardActivity;
import com.galaxy.ishare.model.User;
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


    public static final String PUBLISH_TO_BING_PHONE="PUBLISH_TO_BING_PHONE";

    private RadioGroup mTabGroup = null;
    private RadioButton mShareItemButton, mContactButton, mMeButton;

    private Fragment mShareItemFragment, mContactFragment, mMeFragment;
//    private TextView mTitle;

    private int[] mRadioId = new int[]{R.id.GlobalListButton, R.id.MeButton};

    private static final String TAG = "mainactivity";

    private String contactFilePath = "/data/data/files/phoneContact.txt";

    private FriendDao friendDao;
    private InviteFriendDao inviteFriendDao;

    private LocationClient mLocationClient;

    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = IShareContext.getInstance().createCustomActionBar(this, R.layout.main_action_bar,false);
        Button mapButton = (Button) actionBar.getCustomView().findViewById(R.id.mapStyle);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardActivity.class);
                startActivity(intent);
            }
        });

//        mTitle = (TextView)findViewById(R.id.title);

        initTabs();

        friendDao = FriendDao.getInstance(this);
        inviteFriendDao = InviteFriendDao.getInstance(this);

        mShareItemFragment = new ItemListFragment();
        mContactFragment = new ContactFragment();
        mMeFragment = new MeFragment();

        FragmentTransaction mCurTransaction = getFragmentManager().beginTransaction();
        mCurTransaction.add(R.id.fragment_container, mShareItemFragment);
        mCurTransaction.add(R.id.fragment_container, mContactFragment);
        mCurTransaction.add(R.id.fragment_container, mMeFragment);
        mCurTransaction.hide(mContactFragment);
        mCurTransaction.hide(mMeFragment);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_publish) {
            if(IShareContext.getInstance().getCurrentUser().getUserPhone()==null){

                Intent intent = new Intent (this, BindPhoneActivity.class);
                intent.putExtra(BindPhoneActivity.PARAMETER_WHO_COME,PUBLISH_TO_BING_PHONE);
                startActivity(intent);

            }else {
                Intent intent = new Intent(this, PublishItemActivity.class);
                startActivity(intent);
            }
        } else if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

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
        mShareItemButton = (RadioButton) findViewById(R.id.GlobalListButton);
//        mDiscoverButton = (RadioButton) findViewById(R.id.RecommendButton);
        mContactButton = (RadioButton) findViewById(R.id.ContactButton);
        mMeButton = (RadioButton) findViewById(R.id.MeButton);
        mTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction mCurTransaction = getFragmentManager().beginTransaction();
                mCurTransaction.hide(mShareItemFragment);
                mCurTransaction.hide(mContactFragment);
                mCurTransaction.hide(mMeFragment);
                if (checkedId == mShareItemButton.getId()) {
//                	mTitle.setText(R.string.share_item_tab);
                    mCurTransaction.show(mShareItemFragment);
                } else if (checkedId == mContactButton.getId()) {
//                    mTitle.setText(R.string.contact_tab);
                    mCurTransaction.show(mContactFragment);
                } else if (checkedId == mMeButton.getId()) {
//                    mTitle.setText(R.string.me_tab);
                    mCurTransaction.show(mMeFragment);
                }
                mCurTransaction.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
