package com.galaxy.http;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.net.URI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.galaxy.http.HttpPartialDataFetcher.HttpDownloadMonitor;
import com.galaxy.ishare.AppConst;
import com.galaxy.ishare.utils.FileUtil;
import com.galaxy.ishare.utils.StringUtil;
 
public class HttpImageDownloder{
	public class ImageDownloadResult {
		HttpCode resultCode;
		Bitmap image;
		File imageFile;
	}
	
	private String mImageCachePath = AppConst.SD_ROOT_PATH + "/image/";
	
	private long mLastFileSize;
	private long mTotalDownloadSize;

    private HttpDownloadMonitor mMonitor = new HttpDownloadMonitor() {
		@Override
		public void onDownloading(int totalSize, byte[] curData, int curSize) {
			if(mRequest.getContinueLast()) {
				try {
					if(!mRequest.isCancelled()) {
						mRaf.write(curData, 0, curSize);
						mTotalDownloadSize += curSize;
						HttpImageResponse resp = mResponse.get();
						if(resp != null) {
							resp.onReceiving(mRequest, mLastFileSize + totalSize, mLastFileSize + mTotalDownloadSize);
						}
					}
				} catch (IOException e) {
					mRequest.cancel();
					mRequest.abort();
					e.printStackTrace();
					
					try {
						mRaf.close();
					} catch (Exception e2) {}
				}
			}
		}
		public void onDownloadingDone() {
			if(!mRequest.isCancelled()) {
				try {
					mRaf.close();
					File dest = new File(getImageFilePath(mRequest.getURI()));
					if(dest.exists()) {
						dest.delete();
					}
					boolean b = mTempDownloadFile.renameTo(dest);
					Log.v("xxx", "rename : " + b);
				} catch (Exception e2) {}				
			}
		}
		@Override
		public void onRangeNotSupport() {
			try {
				mLastFileSize = 0;
				mRaf.seek(0L);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onDownloadStart(int totalSize) {
		};
	};
	private File mTempDownloadFile;
	private HttpGetExt mRequest;
	private SoftReference<HttpImageResponse> mResponse;
	private RandomAccessFile mRaf;
	private HttpPartialDataFetcher mDataFetcher = new HttpPartialDataFetcher();
	
    public ImageDownloadResult execute(HttpGetExt request, SoftReference<HttpImageResponse> response) {
    	mResponse = response;
    	
    	ImageDownloadResult result = new ImageDownloadResult();
    	
    	mRequest = request;
    	mRequest.mGzip = false;
    	mRequest.setContinueLast(true);
    	while(true) {
		if(mRequest.getContinueLast()) {
			mTempDownloadFile = new File(getImageFilePath(mRequest.getURI()) + ".tmp");
			try {
				if(mTempDownloadFile.exists()) {
					mRequest.setRange((int)mTempDownloadFile.length(), HttpGetExt.RANGE_END);
				} else {
					FileUtil.makeDIRAndCreateFile(mTempDownloadFile.getPath());
					mRequest.setRange(0, HttpGetExt.RANGE_END);
				}
				mRaf = new RandomAccessFile(mTempDownloadFile, "rwd");
				mLastFileSize = mTempDownloadFile.length();
				mRaf.seek(mLastFileSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mDataFetcher.setDownloadMonitor(mMonitor);
		}

    	
    	HttpCode codeRet = mDataFetcher.execute(request);
    	
    	if(codeRet == HttpCode.OK) {
    		String path = getImageFilePath((mRequest.getURI()));
    		File imgFile = new File(path);
    		if(imgFile.exists()) {
				Bitmap bmp = BitmapFactory.decodeFile(path);
				if(bmp != null) {
					result.resultCode = HttpCode.OK;
					result.image = bmp;
					result.imageFile = imgFile;
					return result;
				}
    		}
    		result.resultCode = HttpCode.E_DATA_ERROR;
    	} else if(codeRet == HttpCode.E_RANGE_NOT_SATISFIABLE){    		
    		mTempDownloadFile.delete();
    		mRequest.setRange(0, HttpGetExt.RANGE_END);
    		continue;
    	}else {    		
    		result.resultCode = codeRet;
    	}
    	
    	return result;
    	}
    }

	public void setImageCachePath(String path) {
		mImageCachePath = path;
	}

	private String getImageFilePath(URI uri) {
		return mImageCachePath + StringUtil.toMd5(uri.toASCIIString());
	}

}
