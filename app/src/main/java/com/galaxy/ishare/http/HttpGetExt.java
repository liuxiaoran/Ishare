package com.galaxy.ishare.http;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class HttpGetExt extends HttpGet implements HttpRequestExtInterface {
    private static final String TAG = "HttpModule";

    public final static String METHOD_NAME = "GET";

    public final static int RANGE_END = -1;

    boolean mGzip = true;

    int mRetryTimes = 1;

    boolean mCancelled = false;

    boolean mContiuneLast = false;

    int[] mRange;

    public HttpGetExt() {
        super();
    }

    public HttpGetExt(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpGetExt(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public HttpRequestBase getRequestBase() {
        return this;
    }

    @Override
    public boolean getContinueLast() {
        return mContiuneLast;
    }

    @Override
    public void setContinueLast(boolean value) {
        mContiuneLast = value;
    }

    @Override
    public int[] getRange() {
        return mRange;
    }

    @Override
    public void setRange(int begin, int end) {
        mContiuneLast = true;

        if (mRange == null) {
            mRange = new int[2];
        }
        mRange[0] = begin;
        mRange[1] = end;
    }

    @Override
    public boolean isCancelled() {
        return mCancelled;
    }

    @Override
    public void cancel() {
        this.mCancelled = true;
    }

    @Override
    public void setGzip(boolean gzip) {
        this.mGzip = gzip;
    }

    @Override
    public boolean isGzip() {
        return mGzip;
    }

    @Override
    public int getRetryTimes() {
        return mRetryTimes;
    }

    @Override
    public void setRetry(int times) {
        this.mRetryTimes = times;
    }

    @Override
    public boolean equals(Object obj) {
        HttpGetExt getExt = (HttpGetExt) obj;

        if (getExt == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!objEquals(getURI(), getExt.getURI())) {
            return false;
        }
        if (!objEquals(params, getExt.params)) {
            return false;
        }
        if (!objEquals(headergroup, getExt.headergroup)) {
            return false;
        }

        return true;
    }

    private boolean objEquals(Object obj1, Object obj2) {
        if (obj1 != null) {
            return obj1.equals(obj2);
        }
        if (obj2 != null) {
            return obj2.equals(obj1);
        }

        return true;
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
