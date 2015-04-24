package com.galaxy.ishare;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.galaxy.ishare.contact.ContactFragment;
import com.galaxy.ishare.friendcircle.DiscoverFragment;
import com.galaxy.ishare.sharedcard.ItemListFragment;
import com.galaxy.ishare.usercenter.MeFragment;
import com.galaxy.util.utils.SPUtil;

public class MainActivity extends ActionBarActivity {

    private RadioGroup mTabGroup = null;
    private RadioButton mShareItemButton, mDiscoverButton, mContactButton, mMeButton;

    private Fragment mShareItemFragment, mDiscoverFragment, mContactFragment, mMeFragment;
//    private TextView mTitle;

    private int[] mRadioId = new int[] {R.id.GlobalListButton, R.id.RecommendButton, R.id.MeButton};

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
    	if(tab >= 0 && tab <mRadioId.length) {
    		mTabGroup.check(mRadioId[tab]);
    	}
    }
    @Override
    protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
    	setTab();
    }

    private void initTabs(){
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
                if(checkedId == mShareItemButton.getId()){
//                	mTitle.setText(R.string.share_item_tab);
                    mCurTransaction.show(mShareItemFragment);
                }else if(checkedId == mDiscoverButton.getId()){
//                	mTitle.setText(R.string.discover_tab);
                    mCurTransaction.show(mDiscoverFragment);
                }else if(checkedId == mContactButton.getId()){
//                    mTitle.setText(R.string.contact_tab);
                    mCurTransaction.show(mContactFragment);
                }else if(checkedId == mMeButton.getId()){
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
}