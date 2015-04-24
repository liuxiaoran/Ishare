package com.galaxy.util.http;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;

public class HttpGetExt extends HttpGet {
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

    public boolean getContinueLast() {
        return mContiuneLast;
    }

    public void setContinueLast(boolean value) {
        mContiuneLast = value;
    }

    public int[] getRange() {
        return mRange;
    }

    public void setRange(int begin, int end) {
        mContiuneLast = true;

        if (mRange == null) {
            mRange = new int[2];
        }
        mRange[0] = begin;
        mRange[1] = end;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void cancel() {
        this.mCancelled = true;
    }

    public void setGzip(boolean gzip) {
        this.mGzip = gzip;
    }

    public boolean isGzip() {
        return mGzip;
    }

    public int getRetryTimes() {
        return mRetryTimes;
    }

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
