package com.galaxy.ishare.http;

import com.galaxy.ishare.model.User;
import org.apache.http.client.methods.HttpRequestBase;

public interface HttpDataResponse {
    User onRecvOK(HttpRequestBase request, String result);

    void onRecvError(HttpRequestBase request, HttpCode retCode);

    void onRecvCancelled(HttpRequestBase request);

    void onReceiving(HttpRequestBase request, int dataSize, int downloadSize);

}
