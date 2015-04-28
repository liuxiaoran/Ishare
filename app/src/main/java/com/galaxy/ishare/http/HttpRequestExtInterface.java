package com.galaxy.ishare.http;


import org.apache.http.client.methods.HttpRequestBase;

public interface HttpRequestExtInterface {

    public HttpRequestBase getRequestBase();

    public boolean getContinueLast();

    public void setContinueLast(boolean value);

    public int[] getRange();

    public void setRange(int begin, int end);

    public boolean isCancelled();

    public void cancel();

    public void setGzip(boolean gzip);

    public boolean isGzip();

    public int getRetryTimes();

    public void setRetry(int times);
}
