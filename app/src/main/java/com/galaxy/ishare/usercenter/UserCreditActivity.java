package com.galaxy.ishare.usercenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.R;
import com.galaxy.ishare.utils.DisplayUtil;
import com.galaxy.ishare.utils.ImageParseUtil;
import com.galaxy.ishare.utils.PhoneUtil;
import com.galaxy.ishare.utils.QiniuUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.soundcloud.android.crop.Crop;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by liuxiaoran on 15/7/4.
 */
public class UserCreditActivity extends ActionBarActivity {

    //选择图片使用的request
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    private ImageView personIv, idCardIv, workCardIv;
    private int picIndex;   // 要上传的是哪个图片， 0：个人照片； 1：身份证；2:工牌
    public String[] picUpLoadUrl;   // 图片的url
    public String personalFileName = "personName.jpg";
    public String idcardFileName = "idcard.jpg";
    public String jobCardFileName = "jobcard.jpg";
    public String[] fileFileNames = new String[]{personalFileName, idcardFileName, jobCardFileName};
    //    private Uri[]picUriList;  // 照片的uri
    public static final String TAG = "usercreditactivity";
    private ImageView[] picIvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_credit);

        picUpLoadUrl = new String[3];

        personIv = (ImageView) findViewById(R.id.activity_myself_credit_personalpic_iv);
        idCardIv = (ImageView) findViewById(R.id.activity_myself_credit_idcard_iv);
        workCardIv = (ImageView) findViewById(R.id.activity_myself_credit_jobcard_iv);
        picIvs = new ImageView[3];
        picIvs[0] = personIv;
        picIvs[1] = idCardIv;
        picIvs[2] = workCardIv;
        ClickListener clickListener = new ClickListener();
        for (int i = 0; i < 3; i++) {
            picIvs[i].setOnClickListener(clickListener);
        }


    }

    private File currentCaptureFile;

    private void showDialog() {

        new MaterialDialog.Builder(this)
                .title("选择图片来源")
                .items(R.array.pic_source_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            //选择本地图片
                            Crop.pickImage(UserCreditActivity.this);
                        } else if (which == 1) {

                            //拍照
                            Intent intentFromCapture = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // 判断存储卡是否可以用，可用进行存储
                            if (PhoneUtil.hasSdcard()) {

                                currentCaptureFile = new File(UserCreditActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                        fileFileNames[picIndex]);
                                intentFromCapture.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(currentCaptureFile));
                            }

                            startActivityForResult(intentFromCapture,
                                    CAMERA_REQUEST_CODE);
                        }
                    }
                })
                .show();

    }


    class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.activity_myself_credit_personalpic_iv) {
                picIndex = 0;

            } else if (v.getId() == R.id.activity_myself_credit_idcard_iv) {
                picIndex = 1;

            } else if (v.getId() == R.id.activity_myself_credit_jobcard_iv) {
                picIndex = 2;

            }
            showDialog();

        }
    }

    private Uri currentUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == IMAGE_REQUEST_CODE) {
                currentUri = result.getData();

            } else if (requestCode == CAMERA_REQUEST_CODE) {
                if (currentCaptureFile != null) {

                    try {
                        currentUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(),
                                currentCaptureFile.getAbsolutePath(), null, null));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }


            if (currentUri != null) {
                // 拍照返回的图片上传到服务器
                String filePath = ImageParseUtil.getImageAbsolutePath(UserCreditActivity.this, currentUri);
                QiniuUtil qiniuUtil = QiniuUtil.getInstance();
                String imageKey = qiniuUtil.generateKey("credit");
                qiniuUtil.uploadFileDefault(filePath, imageKey, new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {

                        if (responseInfo.isOK()) {
                            Log.v(TAG, "ok");
                        }
                    }
                });

                picUpLoadUrl[picIndex] = qiniuUtil.getFileThumbnailUrl(qiniuUtil.getFileUrl(imageKey), DisplayUtil.dip2px(this, 70), DisplayUtil.dip2px(this, 70));

                // 将图片显示出来
                ImageLoader.getInstance().displayImage(currentUri.toString(), picIvs[picIndex], null, null);

            }

        }

        super.onActivityResult(requestCode, resultCode, result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
