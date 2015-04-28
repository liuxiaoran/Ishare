package com.galaxy.ishare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.contact.ContactFragment;
import com.galaxy.ishare.friendcircle.DiscoverFragment;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpPostExt;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.sharedcard.ItemListFragment;
import com.galaxy.ishare.usercenter.MeFragment;
import com.galaxy.ishare.utils.PhoneContactManager;
import com.galaxy.ishare.utils.SPUtil;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private RadioGroup mTabGroup = null;
    private RadioButton mShareItemButton, mDiscoverButton, mContactButton, mMeButton;

    private Fragment mShareItemFragment, mDiscoverFragment, mContactFragment, mMeFragment;
//    private TextView mTitle;

    private int[] mRadioId = new int[]{R.id.GlobalListButton, R.id.RecommendButton, R.id.MeButton};

    private static final String TAG = "mainactivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        mTitle = (TextView)findViewById(R.id.title);

        initTabs();

        mShareItemFragment = new ItemListFragment();
        mDiscoverFragment = new DiscoverFragment();
        mContactFragment = new ContactFragment();
        mMeFragment = new MeFragment();

        FragmentTransaction mCurTransaction = getFragmentManager().beginTransaction();
        mCurTransaction.add(R.id.fragment_container, mShareItemFragment);
        mCurTransaction.add(R.id.fragment_container, mDiscoverFragment);
        mCurTransaction.add(R.id.fragment_container, mContactFragment);
        mCurTransaction.add(R.id.fragment_container, mMeFragment);
        mCurTransaction.hide(mDiscoverFragment);
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

        if (IShareContext.getInstance().checkFirstLogin()) {
            giveReadContactPermission();
        }

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
        mDiscoverButton = (RadioButton) findViewById(R.id.RecommendButton);
        mContactButton = (RadioButton) findViewById(R.id.ContactButton);
        mMeButton = (RadioButton) findViewById(R.id.MeButton);
        mTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction mCurTransaction = getFragmentManager().beginTransaction();
                mCurTransaction.hide(mShareItemFragment);
                mCurTransaction.hide(mDiscoverFragment);
                mCurTransaction.hide(mContactFragment);
                mCurTransaction.hide(mMeFragment);
                if (checkedId == mShareItemButton.getId()) {
//                	mTitle.setText(R.string.share_item_tab);
                    mCurTransaction.show(mShareItemFragment);
                } else if (checkedId == mDiscoverButton.getId()) {
//                	mTitle.setText(R.string.discover_tab);
                    mCurTransaction.show(mDiscoverFragment);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //    // dialog询问读取联系人，开启线程读取联系人
    private void giveReadContactPermission() {
        new MaterialDialog.Builder(this)
                .title("获取联系人")
                .content("如果您同意则会匹配您的手机中的好友")
                .positiveText("我同意")
                .negativeText("不同意")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        new Thread() {
                            @Override
                            public void run() {
                                ArrayList<User> phoneContacts = IShareContext.getInstance().getPhoneContacts();
                                File contactFile = PhoneContactManager.encodePhoneContactFile(phoneContacts);
                                Log.v(TAG, "" + contactFile.getAbsolutePath());
                            }
                        }.start();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        super.onNeutral(dialog);
                    }
                }).show();
//
    }

}