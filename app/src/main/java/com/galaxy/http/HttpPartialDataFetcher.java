package com.galaxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;

import android.util.Log;

import com.galaxy.ishare.AppConst;
 
public class HttpPartialDataFetcher {
	public interface HttpDownloadMonitor {
		public void onRangeNotSupport();
		public void onDownloadStart(int totalSize);
		public void onDownloading(int totalSize, byte[] curData, int curSize);
		public void onDownloadingDone();
	}

	protected String           TAG                          = "HttpModule";
	
    /** 每次读取数据数目 */
    protected final static int BUF_SIZE                     = 16 << 10;
    
    private HttpDownloadMonitor mMonitor;

    public void setDownloadMonitor(HttpDownloadMonitor monitor) {
    	mMonitor = monitor;
    }
    
    protected HttpCode doRequest(HttpGetExt request) {
        InputStream entityStream = null;
        HttpClient httpClient = null;
        try {
            request.addHeader("User-Agent", AppConst.HTTP_AGENG);      

            if(request.getContinueLast() == false || request.getRange() == null) {
            	return HttpCode.E_RANGE_NOT_SATISFIABLE;
            }
            
            int[] range = request.getRange();
            request.setHeader("RANGE", "bytes=" + range[0] + "-" + (range[1] == HttpGetExt.RANGE_END ? "" : range[1] + ""));
            
            httpClient = HttpConnManager.getHttpClient();

            long startTime = System.currentTimeMillis();

            Log.v(TAG, "Uri-->" + request.getURI());

            HttpResponse httpResponse = httpClient.execute(request);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.v(TAG, "Uri-->" + request.getURI() + "..." + responseCode + "..." + (System.currentTimeMillis() - startTime));

            if (responseCode == HttpStatus.SC_OK) {
            	if(request.getContinueLast()) {
            		mMonitor.onRangeNotSupport();
            	}            	
            } else if(responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
            	// right ret code.
            } else if(responseCode == HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE) {
            	return HttpCode.E_RANGE_NOT_SATISFIABLE;
            } else {
            	return HttpCode.E_SERVICE_ACCESS;
            }

            Header[] headers = httpResponse.getAllHeaders();
            request.setGzip(false);
            for (int i = 0; i < headers.length; i++) {
                Header header = headers[i];
                if (header.getName().equals("Content-Encoding") && header.getValue().equals("gzip")) {
                    request.setGzip(true);
                }
            }

            byte[] data = new byte[BUF_SIZE];
            entityStream = httpResponse.getEntity().getContent();
            int dataLength = (int)httpResponse.getEntity().getContentLength();

            mMonitor.onDownloadStart(dataLength);
            while(true) {
            	if(request.isCancelled()) {
            		Log.v("xxx", "abort");
            		request.abort();
            		return HttpCode.CANCELLED;
            	}
            	
                int bytesRead = entityStream.read(data);
                if (bytesRead == -1) {
                    if(mMonitor != null) {
                    	mMonitor.onDownloadingDone();
                    }
                    break;
                }

                if(mMonitor != null) {
                	mMonitor.onDownloading(dataLength, data, bytesRead);
                }
                Thread.sleep(400);
            }
        } catch (SocketTimeoutException e) {
            Log.e(TAG, e.toString() + request.getURI(), e);
            return HttpCode.E_NET_TIMEOUT;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.toString() + request.getURI(), e);
            return HttpCode.E_NET_ACCESS;
        } catch (Exception e) {
            Log.e(TAG, e.toString() + request.getURI(), e);
            return HttpCode.E_NET_ACCESS;
        } finally {
            if (entityStream != null) {
                try {
                	if(entityStream != null) {
                		entityStream.close();                		
                	}
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                ClientConnectionManager ccm = httpClient.getConnectionManager();
                if (ccm != null) {
                    ccm.closeExpiredConnections();
                }
            }
        }

        return HttpCode.OK;
    }

    public HttpCode execute(HttpGetExt request) {
    	HttpCode retCode = HttpCode.CANCELLED;
        int retryTime = request.getRetryTimes();
        
        while (retryTime > 0 && !request.isCancelled()) {
            retCode = doRequest(request);
            if (retCode != null && (retCode == HttpCode.OK || retCode == HttpCode.E_RANGE_NOT_SATISFIABLE)) {
                break;
            }
            --retryTime;
        }
        return retCode;
    }

}
