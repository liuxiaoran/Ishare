package com.galaxy.ishare.usercenter;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import com.galaxy.ishare.utils.ChangePictureActivity;
import com.galaxy.ishare.utils.ChangePictureCallback;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/7/4.
 */
public class UserCreditActivity extends ChangePictureActivity {

    private ImageView personIv, idCard1Iv, idCard2Iv, workCardIv;

    public static final String TAG = "usercreditactivity";


    private EditText realNameEt;
    private EditText jobLocationEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_credit);

        IShareContext.getInstance().createActionbar(this, true, "个人信用");


        personIv = (ImageView) findViewById(R.id.activity_myself_credit_personalpic_iv);
        idCard1Iv = (ImageView) findViewById(R.id.activity_myself_credit_idcard1_iv);
        idCard2Iv = (ImageView) findViewById(R.id.activity_myself_credit_idcard2_tv);
        workCardIv = (ImageView) findViewById(R.id.activity_myself_credit_jobcard_iv);
        realNameEt = (EditText) findViewById(R.id.activity_myself_credit_realname_et);
        jobLocationEt = (EditText) findViewById(R.id.activity_myself_credit_work_et);


        User currentUser = IShareContext.getInstance().getCurrentUser();
        if (currentUser.getRealTime() != null) {
            realNameEt.setText(IShareContext.getInstance().getCurrentUser().getRealTime());
        }
        if (currentUser.getPersonPicUrl() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getPersonPicUrl(), idCard1Iv, null, null);
        }
        if (currentUser.getIdCardPic1Url() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getIdCardPic1Url(), idCard1Iv, null, null);
        }
        if (currentUser.getIdCardPic2Url() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getIdCardPic2Url(), idCard2Iv, null, null);
        }
        if (currentUser.getJobLocation() != null) {
            jobLocationEt.setText(currentUser.getJobLocation());
        }
        if (currentUser.getJobCardUrl() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getJobCardUrl(), workCardIv, null, null);
        }
        //初始化changePicture
        init(3);

        changeImageView[0] = personIv;
        changeImageView[1] = idCard1Iv;
        changeImageView[2] = workCardIv;

        callbacks[0] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String url) {
                List<BasicNameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("per_photo", url));
                HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_CREDIT, params, new HttpDataResponse() {
                    @Override
                    public void onRecvOK(HttpRequestBase request, String result) {

                    }

                    @Override
                    public void onRecvError(HttpRequestBase request, HttpCode retCode) {

                    }

                    @Override
                    public void onRecvCancelled(HttpRequestBase request) {

                    }

                    @Override
                    public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

                    }
                });
            }
        };
        callbacks[1] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String url) {
                List<BasicNameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("idCard1", url));

            }
        };
        callbacks[2] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String url) {

            }
        };
        ClickListener listener = new ClickListener();

        for (int i = 0; i <= 2; i++) {
            changeImageView[i].setOnClickListener(listener);
        }

    }


    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.activity_myself_credit_personalpic_iv) {
                setCurrentImageViewIndex(0);

            } else if (v.getId() == R.id.activity_myself_credit_idcard1_iv) {
                setCurrentImageViewIndex(1);

            } else if (v.getId() == R.id.activity_myself_credit_jobcard_iv) {
                setCurrentImageViewIndex(2);

            }
            showDialog();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


}
