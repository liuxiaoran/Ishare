package com.galaxy.ishare.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;
import java.util.Map;

/**
 * Created by liuxiaoran on 15/4/27.
 */
public class AppAsyncHttpClient {


    /**
     * 请求数据的hTTPClient
     */
    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * no external parameters
     *
     * @param url
     * @param responseHandler
     */
    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
    }

    /**
     * @param url
     * @param map             需要传递参数的键值对 会自动将map中的键值对转换
     * @param responseHandler
     */
    public static void get(String url, Map map, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams(map);
        if (map == null)
            client.get(url, responseHandler);
        else
            client.get(url, params, responseHandler);
    }

    /**
     * 最原始的请求形式
     *
     * @param url
     * @param params          请求所需要的参数
     * @param responseHandler
     */
    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (params == null)
            client.get(url, responseHandler);
        else
            client.get(url, params, responseHandler);
    }

    /**
     * 这里的list的最佳形式应该是 ArrayList<Map<String,String>> 用于复杂的数据结构上传
     *
     * @param url
     * @param lists           会自动将list转换为字符串
     * @param key             list所对应的key 之后会转化为json形式
     * @param responseHandler
     */
    public static void get(String url, List lists, String key, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();

        params.put(key, lists);
        if (params == null)
            client.get(url, responseHandler);
        else
            client.get(url, params, responseHandler);
    }

    /**
     * 这个请求是应该最常用
     *
     * @param url
     * @param object          支持各种类型 list,hashMap, hashSet 等，
     * @param key
     * @param responseHandler
     */
    public static void get(String url, Object object, String key, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put(key, object);
        if (params == null)
            client.get(url, responseHandler);
        else
            client.get(url, params, responseHandler);
    }

    public static void post(String url, AsyncHttpResponseHandler responseHandler) {
        client.post(url, responseHandler);
    }

    /**
     * 最原始的请求形式
     *
     * @param url
     * @param params          参数
     * @param responseHandler
     */
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        if (params == null)
            client.post(url, params, responseHandler);
        else
            client.post(url, params, responseHandler);
    }

    /**
     * @param url
     * @param map             需要传递参数的键值对 会自动将map中的键值对转换
     * @param responseHandler
     */
    public static void post(String url, Map<String, String> map, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams(map);
        client.post(url, params, responseHandler);
    }

    /**
     * 需要上传数组的形式可以进行考虑
     * 这里的list的最佳形式应该是 ArrayList<Map<String,String>> 用于复杂的数据结构上传
     *
     * @param url
     * @param lists           会自动将list转换为字符串
     * @param key
     * @param responseHandler
     */
    public static void post(String url, List lists, String key, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put(key, lists);
        client.post(url, params, responseHandler);
    }

    /**
     * 这个请求是应该最常用
     *
     * @param url
     * @param object          支持各种类型 list,hashMap, hashSet 等，
     * @param key
     * @param responseHandler
     */
    public static void post(String url, Object object, String key, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put(key, object);
        client.post(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, FileAsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    /**
     * todo 需要请求delete的形式
     *
     * @param url
     * @param responseHandler
     */
    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(url, responseHandler);
    }

    /**
     * todo 需要进行put请求的形式
     *
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(url, params, responseHandler);
    }


}
