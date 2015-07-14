package com.galaxy.ishare.usercenter;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
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

import java.net.URL;
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
    private HttpInteract httpInteract;

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
        Log.v(TAG, currentUser.getPersonPicUrl() + "  " + currentUser.getIdCardPic1Url() + "  " + currentUser.getIdCardPic2Url() + " " + currentUser.getJobCardUrl());
        if (currentUser.getRealName() != null) {
            realNameEt.setText(IShareContext.getInstance().getCurrentUser().getRealName());
        }
        if (currentUser.getPersonPicUrl() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getPersonPicUrl(), personIv, null, null);
        }
        if (currentUser.getIdCardPic1Url() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getIdCardPic1Url(), idCard1Iv, null, null);
        }
        if (currentUser.getIdCardPic2Url() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getIdCardPic2Url(), idCard2Iv, null, null);
        }
        if (currentUser.getJobLocation() != null) {
            jobLocationEt.setText(currentUser.getJobLocation());
            Log.v(TAG, currentUser.getJobLocation() + "-----");
        }
        if (currentUser.getJobCardUrl() != null) {
            ImageLoader.getInstance().displayImage(currentUser.getJobCardUrl(), workCardIv, null, null);
        }
        //初始化changePicture
        init(4);

        changeImageView[0] = personIv;
        changeImageView[1] = idCard1Iv;
        changeImageView[2] = idCard2Iv;
        changeImageView[3] = workCardIv;

        callbacks[0] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String url) {
                // 将个人照片保存在本地
                User user = IShareContext.getInstance().getCurrentUser();
                user.setPersonPicUrl(url);
                IShareContext.getInstance().saveCurrentUser(user);
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
                // 将身份证保存在本地
                User user = IShareContext.getInstance().getCurrentUser();
                user.setIdCardPic1Url(url);
                IShareContext.getInstance().saveCurrentUser(user);
                List<BasicNameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("idCard1", url));
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
        callbacks[2] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String url) {
                User user = IShareContext.getInstance().getCurrentUser();
                user.setIdCardPic2Url(url);
                IShareContext.getInstance().saveCurrentUser(user);
                List<BasicNameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("idCard2", url));
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
        callbacks[3] = new ChangePictureCallback() {
            @Override
            public void afterChangePicture(String picUrl) {
                User user = IShareContext.getInstance().getCurrentUser();
                user.setJobCardUrl(picUrl);
                IShareContext.getInstance().saveCurrentUser(user);
                List<BasicNameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("work_card", picUrl));
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
        ClickListener listener = new ClickListener();

        for (int i = 0; i <= 3; i++) {
            changeImageView[i].setOnClickListener(listener);
        }
        httpInteract = new HttpInteract();

    }


    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.activity_myself_credit_personalpic_iv) {
                setCurrentImageViewIndex(0);

            } else if (v.getId() == R.id.activity_myself_credit_idcard1_iv) {
                setCurrentImageViewIndex(1);

            } else if (v.getId() == R.id.activity_myself_credit_jobcard_iv) {
                setCurrentImageViewIndex(3);

            } else if (v.getId() == R.id.activity_myself_credit_idcard2_tv) {
                setCurrentImageViewIndex(2);
            }

            showDialog();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            User user = IShareContext.getInstance().getCurrentUser();
            if (realNameEt.getText().toString() != "" && !realNameEt.getText().toString().equals(user.getRealName())) {

                user.setRealName(realNameEt.getText().toString());
                httpInteract.updateRealName(realNameEt.getText().toString());
            }
            if (jobLocationEt.getText().toString() != "" && !jobLocationEt.getText().toString().equals(user.getJobLocation())) {
                user.setJobLocation(jobLocationEt.getText().toString());
                httpInteract.updateJobLocation(jobLocationEt.getText().toString());
            }
            IShareContext.getInstance().saveCurrentUser(user);
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    class HttpInteract {
        public void updateRealName(String realName) {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("real_name", realName));
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

        public void updateJobLocation(String location) {
            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("work_unit", location));
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
    }
}
