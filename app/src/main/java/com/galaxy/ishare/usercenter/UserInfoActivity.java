package com.galaxy.ishare.usercenter;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by YangJunLin on 2015/5/23.
 */
public class UserInfoActivity extends ActionBarActivity {

    private static final String TAG = "MyselfInfoActivity";

    private TextView nameTv, phoneTv, genderTv;
    private CircleImageView avatarIv;


    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/ishare/ishare_portrait.jpg";//temp file

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);

        ActionBar actionBar = IShareContext.getInstance().createActionbar(this, false, "个人信息");

        initViews();

    }

    private void initViews() {
        nameTv = (TextView) findViewById(R.id.usercenter_info_nickname_tv);
        phoneTv = (TextView) findViewById(R.id.usercenter_info_phone_tv);
        genderTv = (TextView) findViewById(R.id.usercenter_info_gender_tv);
        avatarIv = (CircleImageView) findViewById(R.id.usercenter_info_avatar_iv);


        nameTv.setText(IShareContext.getInstance().getCurrentUser().getUserName());
        phoneTv.setText(IShareContext.getInstance().getCurrentUser().getUserPhone());
        genderTv.setText(IShareContext.getInstance().getCurrentUser().getGender());
        ImageLoader.getInstance().displayImage(IShareContext.getInstance().getCurrentUser().getAvatar(), avatarIv, null, null);

    }

}