package com.galaxy.ishare.http;

import android.util.Log;

import com.galaxy.ishare.Global;
import com.galaxy.ishare.http.HttpDataFetcher.HttpDataResult;
import com.galaxy.ishare.http.HttpFileDownloder.FileDownloadResult;
import com.galaxy.ishare.http.HttpImageDownloder.ImageDownloadResult;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HttpTask {

    private static final String TAG = "httptask";
    private static ExecutorService mDataPool = Executors.newFixedThreadPool(2);
    private static ExecutorService mFilePool = Executors.newFixedThreadPool(4);

    public static void startSyncDataRequset(final HttpGetExt request) {
    }

    public static void startSyncDataRequset(final HttpPostExt request) {
    }

    public static void startAsyncDataRequset(final HttpGetExt request, final HttpDataResponse response) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                HttpDataResult result = new HttpDataFetcher().execute(request);
                if (result.resultCode == HttpCode.OK) {
                    String retStr = null;
                    if (result.encoding == null || result.equals("")) {
                        retStr = new String(result.data);

                    } else {
                        try {
                            retStr = new String(result.data, result.encoding);
                        } catch (UnsupportedEncodingException e) {
                            revDataError(request.getRequestBase(), response, HttpCode.E_DATA_ERROR);
                            e.printStackTrace();
                            return;
                        }
                    }
                    revDataOk(request.getRequestBase(), response, retStr);
                } else {
                    revDataError(request.getRequestBase(), response, result.resultCode);
                }
            }
        };
        mDataPool.execute(task);
    }

    public static void startAsyncDataRequset(final HttpPostExt request, final List<BasicNameValuePair> params, final HttpDataResponse response) {

        Runnable task = new Runnable() {
            @Override
            public void run() {
                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    revDataError(request, response, HttpCode.E_DATA_ERROR);
                }
                request.setEntity(entity);
                HttpDataResult result = new HttpDataFetcher().execute(request);
                if (result.resultCode == HttpCode.OK) {
                    String retStr = null;
                    if (result.encoding == null || result.equals("")) {
                        retStr = new String(result.data);
                    } else {
                        try {
                            retStr = new String(result.data, result.encoding);
                        } catch (UnsupportedEncodingException e) {
                            revDataError(request, response, HttpCode.E_DATA_ERROR);
                            e.printStackTrace();
                            return;
                        }
                    }
                    revDataOk(request, response, retStr);
                } else {
                    revDataError(request, response, result.resultCode);
                }
            }
        };
        mDataPool.execute(task);

    }


    public static HttpGetExt startAsyncDataGetRequset(String request, final List<NameValuePair> params, final HttpDataResponse response) {

        if (Global.userId != null && Global.key != null) {
            request = request + "?" + "open_id=" + Global.userId + "&key=" + Global.key;

            if (params != null) {
                for (int i = 0; i <= params.size() - 1; i++) {
                    request = request + "&" + params.get(i).getName() + "=" + params.get(i).getValue();
                }
            }
        } else {
            if (params != null) {
                request = request + "?" + params.get(0).getName() + "=" + params.get(0).getValue();

                for (int i = 1; i <= params.size() - 1; i++) {
                    request = request + "&" + params.get(i).getName() + "=" + params.get(i).getValue();
                }
            }
        }
        Log.v(TAG, request + "----request");
        HttpGetExt httpGetExt = new HttpGetExt(request);
        startAsyncDataRequset(httpGetExt, response);
        return httpGetExt;
    }

    public static HttpPostExt startAsyncDataPostRequest(String url, List<BasicNameValuePair> params, HttpDataResponse response) {
        HttpPostExt httpPostExt = null;
        if (Global.key != null && Global.userId != null) {
            params.add(new BasicNameValuePair("open_id", Global.userId));
            params.add(new BasicNameValuePair("key", Global.key));
            Log.v(TAG, Global.key);
            httpPostExt = new HttpPostExt(url);
            startAsyncDataRequset(httpPostExt, params, response);

        } else {
            Log.v(TAG, Global.key + "else");
            if (params != null) {
                httpPostExt = new HttpPostExt(url);
                startAsyncDataRequset(httpPostExt, params, response);
            }
        }
        return httpPostExt;

    }


    private static void revDataOk(final HttpRequestBase request, final HttpDataResponse response, final String ret) {
        Global.RunOnUiThread(new Runnable() {
            @Override
            public void run() {
                response.onRecvOK(request, ret);
            }
        });
    }

    private static void revDataError(final HttpRequestBase request, final HttpDataResponse response, final HttpCode retCode) {
        Global.RunOnUiThread(new Runnable() {
            @Override
            public void run() {
                response.onRecvError(request, retCode);
            }
        });
    }


    //专用于下载模块
    // private DownloadStrategy mDownloadStrategy;
    public static void downloadFile(final HttpGetExt request, final String filePath, HttpFileResponse response) {
        final SoftReference<HttpFileResponse> softResp = new SoftReference<HttpFileResponse>(response);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                final FileDownloadResult result = new HttpFileDownloder().execute(request, softResp, filePath);
                Global.RunOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HttpFileResponse resp = softResp.get();
                        if (resp == null) {
                            return;
                        }

                        if (result.resultCode == HttpCode.OK) {
                            resp.onRecvOK(request, result.filePath);
                        } else if (result.resultCode == HttpCode.CANCELLED) {
                            resp.onRecvCancelled(request, filePath);
                        } else {
                            resp.onRecvError(request, filePath, result.resultCode);
                        }
                    }
                });
            }
        };
        mFilePool.execute(task);
    }

    public static ImageDownloadResult downloadImage(final HttpGetExt request, final HttpImageResponse response) {
//      if (localCache.containsKey(filePath)) {
//    	retBitmap = localCache.get(filePath);
//    	if (retBitmap != null && !retBitmap.isRecycled()) {
//    		if (response != null) {
//    			result.setResultOK();
//    			result.setRetBitmap(retBitmap);
//    			result.setImagePath(filePath);
//    			result.setSource(ImageResult.SRC_CACHE);
//    			return result;
//    		}
//    	}
//    }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                final ImageDownloadResult result = new HttpImageDownloder().execute(request, new SoftReference<HttpImageResponse>(response));
                if (result.resultCode == HttpCode.OK) {
                    Global.RunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response.onRecvOK(request, result.image, result.imageFile);
                        }
                    });
                } else if (result.resultCode == HttpCode.CANCELLED) {
                    Global.RunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response.onRecvCancelled(request);
                        }
                    });
                } else {
                    Global.RunOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            response.onRecvError(request, result.resultCode);
                        }
                    });
                }
            }
        };
        mFilePool.execute(task);


//
//        if (request.getURI() == null) {
//            response.onImageRecvError(imageType, request.getTag(), ImageResult.ERROR_URL_NULL);
//            result.setResultFail();
//            return result;
//        }
//
//        final String filePath = getImageFilePath(imageType, request.getURI());
//        SLog.v("图片类型:" + imageType + "---filePath=" + filePath);
//        request.setFilePath(filePath);
//        Bitmap retBitmap = null;
//        BitmapCache localCache = getImageArrayCache(imageType);
//
//        // 1取本地缓存
//        if (localCache.containsKey(filePath)) {
//        	retBitmap = localCache.get(filePath);
//        	if (retBitmap != null && !retBitmap.isRecycled()) {
//        		if (response != null) {
//        			result.setResultOK();
//        			result.setRetBitmap(retBitmap);
//        			result.setImagePath(filePath);
//        			result.setSource(ImageResult.SRC_CACHE);
//        			return result;
//        		}
//        	}
//        }
//
//
//        // 2取本地保存数据
//        File fileTmp = new File(filePath);
//        if (fileTmp.exists()) {
//            HttpTask.startRunnableRequestInPool(new Runnable() {
//                @Override
//                public void run() {
//                    final Bitmap bitmap = ImageUtil.fromFileToBitmap(imageType, filePath);
//                    if (bitmap != null) {
//                        putImageInCache(imageType, filePath, bitmap);
//                    }
//                    Application.getInstance().runOnUIThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            response.onImageRecvOK(imageType, request.getTag(), bitmap, filePath);
//                        }
//                    });
//                }
//            });
//			result.setResultOK();
//			result.setSource(ImageResult.SRC_FLASH);
//            return result;
//        }
//
//		// 3访问网络数据
//		if (NetStatusReceiver.netStatus == NetStatusReceiver.NETSTATUS_INAVAILABLE) {
//			response.onImageRecvError(imageType, request.getTag(),
//					ImageResult.ERROR_NO_NET);
//			result.setResultFail();
//			result.setStatus(ImageResult.ERROR_NO_NET);
//			return result;
//		}
//
//		ImageDownloadPool.getInstance().addTask(imageType, request, response);
//		result.setResultOK();
//		result.setSource(ImageResult.SRC_NET);
//
//        return result;
        return null;
    }
}
