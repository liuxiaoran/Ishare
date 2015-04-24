package com.galaxy.util.http;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;

import android.util.Log;

import com.galaxy.util.http.HttpPartialDataFetcher.HttpDownloadMonitor;
import com.galaxy.util.utils.FileUtil;

public class HttpFileDownloder {
    public class FileDownloadResult {
        HttpCode resultCode;
        String filePath;
    }

    private String mFilePath;
    private HttpDownloadMonitor mMonitor = new HttpDownloadMonitor() {
        @Override
        public void onDownloadStart(int totalSize) {
            HttpFileResponse resp = mResponse.get();
            if (resp != null) {
                resp.onStart(mRequest, mFilePath, mLastFileSize + totalSize);
            }
        }

        ;

        @Override
        public void onDownloading(int totalSize, byte[] curData, int curSize) {
            if (mRequest.getContinueLast()) {
                try {
                    if (!mRequest.isCancelled()) {
                        mRaf.write(curData, 0, curSize);
                        mTotalDownloadSize += curSize;

                        HttpFileResponse resp = mResponse.get();
                        if (resp != null) {
                            resp.onReceiving(mRequest, mFilePath, mLastFileSize + totalSize, mLastFileSize + mTotalDownloadSize);
                        }
                    }
                } catch (IOException e) {
                    mRequest.cancel();
                    mRequest.abort();
                    e.printStackTrace();

                    try {
                        mRaf.close();
                    } catch (Exception e2) {
                    }
                }
            }
        }

        public void onDownloadingDone() {
            if (!mRequest.isCancelled()) {
                try {
                    mRaf.close();
                    File dest = new File(mFilePath);
                    if (dest.exists()) {
                        dest.delete();
                    }
                    boolean b = mTempDownloadFile.renameTo(dest);
                    Log.v("xxx", "rename : " + b);
                } catch (Exception e2) {
                }
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
    };
    private File mTempDownloadFile;
    private HttpGetExt mRequest;
    private RandomAccessFile mRaf;
    private HttpPartialDataFetcher mDataFetcher = new HttpPartialDataFetcher();
    private SoftReference<HttpFileResponse> mResponse;
    private int mLastFileSize;
    private int mTotalDownloadSize;

    public FileDownloadResult execute(HttpGetExt request, SoftReference<HttpFileResponse> response, String filePath) {
        mFilePath = filePath;
        mResponse = response;

        FileDownloadResult result = new FileDownloadResult();
        result.filePath = mFilePath;

        mRequest = request;
        mRequest.mGzip = false;
        mRequest.setContinueLast(true);
        while (true) {
            if (mRequest.getContinueLast()) {
                mTempDownloadFile = new File(mFilePath + ".tmp");
                try {
                    if (mTempDownloadFile.exists()) {
                        mRequest.setRange((int) mTempDownloadFile.length(), HttpGetExt.RANGE_END);
                    } else {
                        FileUtil.makeDIRAndCreateFile(mTempDownloadFile.getPath());
                        mRequest.setRange(0, HttpGetExt.RANGE_END);
                    }
                    mRaf = new RandomAccessFile(mTempDownloadFile, "rwd");
                    mLastFileSize = (int) mTempDownloadFile.length();
                    mRaf.seek(mLastFileSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mDataFetcher.setDownloadMonitor(mMonitor);
            }


            HttpCode codeRet = mDataFetcher.execute(request);

            if (codeRet == HttpCode.OK) {
                File file = new File(mFilePath);
                if (file.exists()) {
                    result.resultCode = HttpCode.OK;
                    return result;
                }
                result.resultCode = HttpCode.E_DATA_ERROR;
            } else if (codeRet == HttpCode.E_RANGE_NOT_SATISFIABLE) {
                mTempDownloadFile.delete();
                mRequest.setRange(0, HttpGetExt.RANGE_END);
                continue;
            } else {
                result.resultCode = codeRet;
            }

            return result;
        }
    }
}
