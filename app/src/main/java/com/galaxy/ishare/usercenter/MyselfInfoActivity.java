package com.galaxy.ishare.usercenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.galaxy.ishare.R;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.galaxy.ishare.model.User;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by YangJunLin on 2015/5/23.
 */
public class MyselfInfoActivity extends Activity {

    private static final String TAG = "MyselfInfoActivity";

    TextView nickname = null;
    TextView myGender = null;
    ImageView myPhoto = null;
    RelativeLayout imageLayout = null;
    Bitmap bp = null;
    float scaleWidth;
    float scaleHeight;
    int h;
    boolean num = false;
    private static int imageClick = 0;

    final AlertDialog[] imageDialog = new AlertDialog[1];

    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/ishare/ishare_portrait.jpg";//temp file
    private static Uri photoPick = null;
    Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        nickname = (TextView) findViewById(R.id.myself_info_mynickname);
        myGender = (TextView) findViewById(R.id.myself_info_mygender);
        myPhoto = (ImageView) findViewById(R.id.myself_info_myphoto);

        imageLayout = (RelativeLayout) findViewById(R.id.myself_info_image);

       /* Display display = getWindowManager().getDefaultDisplay();
        bp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_marke);
        final int width = bp.getWidth();
        int height = bp.getHeight();
        int w = display.getWidth();
        int h = display.getHeight();
        scaleWidth = ((float) w) / width;
        scaleHeight = ((float) h) / height;
        myPhoto.setImageBitmap(bp);
*/
        getUser();

        ImageButton selfInfoBack = (ImageButton) findViewById(R.id.myself_info_back_image);
        View.OnClickListener selfInfoBackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        selfInfoBack.setOnClickListener(selfInfoBackListener);

        RelativeLayout nickLayout = (RelativeLayout) findViewById(R.id.myself_info_nickname);
        final TextView myNickName = (TextView) findViewById(R.id.myself_info_mynickname);
        View.OnClickListener nickNameListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyselfNaneActivity.class);
                intent.putExtra("name", myNickName.getText());
                startActivity(intent);
            }
        };
        nickLayout.setOnClickListener(nickNameListener);

        final RelativeLayout gender = (RelativeLayout) findViewById(R.id.myself_info_gender);

        final View.OnClickListener genderListener = new View.OnClickListener() {
            int gender;

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MyselfInfoActivity.this).setTitle("请选择").setSingleChoiceItems(new String[]{"男", "女"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gender = which;
                    }
                })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    UserUtils.updateUserInfo(null, null, null, String.valueOf(gender));
                                    if (gender == 0) {
                                        myGender.setText("男");
                                    } else if (gender == 1) {
                                        myGender.setText("女");
                                    }
                                }
                            }
                        }).show();
            }
        };
        gender.setOnClickListener(genderListener);

        final View.OnClickListener imageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageClick == 0) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                    setContentView(R.layout.activity_myself_image);
                    imageClick = 1;
                } else if (imageClick == 1) {
                    setContentView(R.layout.activity_myself_info);
                }
            }
        };
//        myPhoto.setOnClickListener(imageListener);


        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog[0] = new AlertDialog.Builder(MyselfInfoActivity.this).setTitle("请选择").setSingleChoiceItems(new String[]{"相机", "照片"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, 1);
                            imageDialog[0].dismiss();
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");//相片类型  
                            startActivityForResult(intent, 3);
                            imageDialog[0].dismiss();
                        }
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                Log.d(TAG, "TAKE_BIG_PICTURE: data = " + data);//it seems to be null
                //TODO sent to crop
                cropImageUri(imageUri, 700, 700, 2);
                break;
            case 2://from crop_big_picture
                Log.d(TAG, "CROP_BIG_PICTURE: data = " + data);//it seems to be null
                if (imageUri != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myPhoto.setImageBitmap(bitmap);
                }
                break;
            case 3:
                photoPick = data.getData();
                cropImageUri(photoPick, 700, 700, 4);
            case 4:
                if (imageUri != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoPick);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myPhoto.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    private void getUser() {
        HttpTask.startAsyncDataGetRequset(URLConstant.QUERY_USER, null, new HttpDataResponse() {
            @Override
            public User onRecvOK(HttpRequestBase request, String result) {
                User userInfo = new User();
                int status = 0;
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    status = jsonObject.getInt("status");
                    if (status == 0) {
                        JSONObject tmp = jsonObject.getJSONObject("data");
                        userInfo.setUserName(tmp.getString("nickname"));
                        userInfo.setAvatar(tmp.getString("avatar"));
                        userInfo.setUserPhone(tmp.getString("phone"));
                        userInfo.setGender(tmp.getInt("gender"));
                        userInfo.setUserId(tmp.getString("open_id"));
                    }
                    nickname.setText(userInfo.getUserName());
                    myGender.setText(userInfo.getGender() == 0 ? "男" : "女");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return userInfo;
            }

            @Override
            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
                Log.e(this.getClass().getName(), retCode.toString());
            }

            @Override
            public void onRecvCancelled(HttpRequestBase request) {

            }

            @Override
            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        getUser();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
