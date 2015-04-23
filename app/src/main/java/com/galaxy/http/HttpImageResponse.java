package com.galaxy.http;

import java.io.File;

import org.apache.http.client.methods.HttpRequestBase;

import android.graphics.Bitmap;

public interface HttpImageResponse {
	void onRecvOK(HttpRequestBase request, Bitmap bmp, File bmpFile);

	void onRecvError(HttpRequestBase request, HttpCode retCode);

	void onRecvCancelled(HttpRequestBase request);

	void onReceiving(HttpRequestBase request, long dataSize, long downloadSize);
}
