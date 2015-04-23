package com.galaxy.http;

import org.apache.http.client.methods.HttpRequestBase;

public interface HttpDataResponse {
	void onRecvOK(HttpRequestBase request, String result);

	void onRecvError(HttpRequestBase request, HttpCode retCode);

	void onRecvCancelled(HttpRequestBase request);

	void onReceiving(HttpRequestBase request, int dataSize, int downloadSize);

}
