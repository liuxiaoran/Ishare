package com.galaxy.ishare.utils;

import com.galaxy.ishare.http.HttpCode;
import com.galaxy.ishare.http.HttpDataResponse;
import com.galaxy.ishare.http.HttpTask;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.File;

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

    public String getFileUrl (String key ){
        return hostName +key;

    }

//    public void uploadFile (final String fileName ,final String key, final UpCompletionHandler handler){
//        HttpTask.startAsyncDataGetRequset(, , new HttpDataResponse() {
//            @Override
//            public void onRecvOK(HttpRequestBase request, String result) {
//                String token ;
//                uploadFile(new File (fileName),key,token,handler,null);
//            }
//
//            @Override
//            public void onRecvError(HttpRequestBase request, HttpCode retCode) {
//
//            }
//
//            @Override
//            public void onRecvCancelled(HttpRequestBase request) {
//
//            }
//
//            @Override
//            public void onReceiving(HttpRequestBase request, int dataSize, int downloadSize) {
//
//            }
//        });
//    }

    public void uploadFile(File file ,String key,String token,UpCompletionHandler handler,UploadOptions options){

        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, key, token,
                handler, null);
    }

}
