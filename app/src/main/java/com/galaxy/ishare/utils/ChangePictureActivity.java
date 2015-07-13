package com.galaxy.ishare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.galaxy.ishare.IShareActivity;
import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.BroadcastActionConstant;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.ConfirmCode;
import com.galaxy.ishare.model.User;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.soundcloud.android.crop.Crop;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/7/11.
 * 更换头像sample
 */
public class ChangePictureActivity extends IShareActivity {


    private static final String TAG = "MyselfInfoActivity";

    public static final int CAMERA_REQUEST_CODE = 1;

    private int cachePicIndex = 0;


    private File picSaveFile;

    private static final String IMAGE_FILE_NAME = "faceImage.jpg";


    private ImageView imageView;

    private HttpInteract httpInteract;

    /**
     * 子类要调用
     *
     * @param imageView
     */
    public void initChangePicture(ImageView imageView) {
        this.imageView = imageView;
        httpInteract = new HttpInteract();
    }

    public void showDialog() {

        new MaterialDialog.Builder(this)
                .title("选择图片来源")
                .items(R.array.pic_source_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            //选择本地图片
                            Crop.pickImage(ChangePictureActivity.this);
                        } else if (which == 1) {

                            //拍照
                            Intent intentFromCapture = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // 判断存储卡是否可以用，可用进行存储
                            if (PhoneUtil.hasSdcard()) {

                                File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                        IMAGE_FILE_NAME);
                                intentFromCapture.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(file));
                            }
                            startActivityForResult(intentFromCapture,
                                    CAMERA_REQUEST_CODE);
                        }
                    }
                })
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        //结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
                beginCrop(result.getData());
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, result);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                if (PhoneUtil.hasSdcard()) {
                    File tempFile = new File(
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            IMAGE_FILE_NAME);

                    beginCrop(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(this, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_LONG).show();
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, result);
    }

    private void beginCrop(Uri source) {
        // 可能是crop 库的问题， 后面的文件名必须不同，否则多次改变之后还是第一次的图片
        picSaveFile = new File(getCacheDir(), "cropped" + cachePicIndex);
        cachePicIndex++;
        Uri outputUri = Uri.fromFile(picSaveFile);
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.v(TAG, "uri :" + Crop.getOutput(result));
            getImageToViewAndUploadToQiniu(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageToViewAndUploadToQiniu(Uri uri) {

        imageView.setImageURI(uri);

        // 产生key 并且上传七牛
        String key = QiniuUtil.getInstance().generateKey("avatar");
        QiniuUtil.getInstance().uploadFileDefault(picSaveFile.getAbsolutePath(), key, new UpCompletionHandler() {
            @Override
            public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {

                if (responseInfo.isOK()) {
                    Log.v(TAG, "avatar upload qiniu is ok");
                }
            }
        });

        String avatarUrl = QiniuUtil.getInstance().getFileUrl(key);

        // 更新本地头像存储
        User user = IShareContext.getInstance().getCurrentUser();
        user.setAvatar(avatarUrl);
        IShareContext.getInstance().saveCurrentUser(user);


        // 发出广播通过头像改变
        Intent intent = new Intent();
        intent.setAction(BroadcastActionConstant.UPDATE_USER_AVATAR);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        // 通知服务器头像改变
        httpInteract.updateAvatar(avatarUrl);


    }

    class HttpInteract {
        public void updateAvatar(String avatarUrl) {

            List<BasicNameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("avatar", avatarUrl));
            HttpTask.startAsyncDataPostRequest(URLConstant.UPDATE_USER_INFO, params, new HttpDataResponse() {
                @Override
                public void onRecvOK(HttpRequestBase request, String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            Log.v(TAG, "update avatar   success");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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