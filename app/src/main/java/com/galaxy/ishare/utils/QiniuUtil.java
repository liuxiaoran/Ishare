package com.galaxy.ishare.utils;


import android.util.Log;

import com.galaxy.ishare.IShareContext;
import com.galaxy.ishare.constant.URLConstant;
import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoran on 15/5/24.
 * 封装七牛服务
 */
public class QiniuUtil {

    public static final String hostName="http://7xixyl.com1.z0.glb.clouddn.com/";
    private static QiniuUtil instance;
    public static  QiniuUtil getInstance(){
        if (instance==null){
            instance  = new QiniuUtil();
        }
        return  instance;
    }

    // 通过generatekey得到的key 得到file url
    public String getFileUrl (String key ){
        return hostName +key;

    }

    // qiniu 文件裁剪原则:http://developer.qiniu.com/docs/v6/api/reference/fop/image/imageview2.html
    public String getFileThumbnailUrl(String fileUrl, int longEdge, int shortEdge) {
        return fileUrl + "?imageView2" + "/1/w/" + longEdge + "/h/" + shortEdge;
    }

    public String getThumbnailUrl(String key, int longEdge, int shortEdge) {
        return getFileUrl(key) + "?imageView2" + "/1/w/" + longEdge + "/h/" + shortEdge;
    }

    // 返回key
    public String generateKey(String name) {
        return IShareContext.getInstance().getCurrentUser().getUserId() + name + System.currentTimeMillis();
    }

    public void uploadBytesDefault (final byte[] bytes ,final String key, final UpCompletionHandler handler){

        List<NameValuePair>params  = new ArrayList<>();
        params.add(new BasicNameValuePair("qiniu_key",key));
        HttpTask.startAsyncDataGetRequset(URLConstant.QIUNIU_TOKEN, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result)  {

                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    int status =  jsonObject.getInt("status");
                    if (status==0) {
                        String token = jsonObject.getString("token");
                        uploadBytes(bytes, key, token, handler, null);
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

    /**
     * 最常用的上传文件，内部会于服务器取得token
     *
     * @param filePath 文件路径
     * @param key      通过generateKey 获得
     * @param handler
     */
    public void uploadFileDefault(final String filePath, final String key, final UpCompletionHandler handler) {

        List<NameValuePair>params  = new ArrayList<>();
        params.add(new BasicNameValuePair("qiniu_key",key));
        HttpTask.startAsyncDataGetRequset(URLConstant.QIUNIU_TOKEN, params, new HttpDataResponse() {
            @Override
            public void onRecvOK(HttpRequestBase request, String result)  {

                try {
                    JSONObject jsonObject  = new JSONObject(result);
                    int status =  jsonObject.getInt("status");
                    if (status==0) {
                        String token = jsonObject.getString("token");
                        uploadFile(filePath, key, token, handler, null);
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

    public void uploadFile(String filePath, String key, String token, UpCompletionHandler handler, UploadOptions options) {
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath, key, token,
                handler, options);
    }

    public void uploadBytes(byte[] bytes ,String key,String token,UpCompletionHandler handler,UploadOptions options){

        UploadManager uploadManager = new UploadManager();
        uploadManager.put(bytes, key, token,
                handler, options);
    }

    public void uploadFile(File file ,String key,String token,UpCompletionHandler handler,UploadOptions options){

        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, key, token,
                handler, options);
    }

}
