package com.galaxy.ishare.http;

import org.apache.http.client.methods.HttpRequestBase;

public interface HttpFileResponse {
    void onStart(final HttpRequestBase request, final String filePath, final int dataSize);

    void onRecvOK(final HttpRequestBase request, final String filePath);

    void onRecvError(final HttpRequestBase request, final String filePath, final HttpCode retCode);

    void onRecvCancelled(final HttpRequestBase request, final String filePath);

    void onReceiving(final HttpRequestBase request, final String filePath, final int dataSize, final int downloadSize);
}
