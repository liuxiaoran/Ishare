package com.galaxy.util.http;

import java.net.URI;

import org.apache.http.client.methods.HttpPost;

public class HttpPostExt extends HttpPost {
    private static final String TAG = "HttpModule";

    boolean mGzip = true;

    int mRetryTimes = 1;

    boolean mCancelled = false;

    public HttpPostExt() {
        super();
    }

    public HttpPostExt(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpPostExt(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.mCancelled = cancelled;
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
        HttpPostExt getExt = (HttpPostExt) obj;

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
}
