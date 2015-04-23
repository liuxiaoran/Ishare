package com.galaxy.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import com.galaxy.ishare.AppConst;
 
public class HttpDataFetcher {
	
	public class HttpDataResult {
		public HttpCode resultCode;
		public byte[] data;
		public String encoding = "UTF_8"; 
	}

	protected String           TAG                          = "HttpModule";
	
    /** 每次读取数据数目 */
    protected final static int BUF_SIZE                     = 4096;

    private boolean mUseGzip = true;
    
    protected HttpDataResult doRequest(HttpGetExt request) {
    	HttpDataResult result = new HttpDataResult();

        InputStream entityStream = null;
        HttpClient httpClient = null;
        try {
            if (request.isGzip() && mUseGzip) {
            	request.setHeader("Accept-Encoding", "gzip,deflate");
            }
            request.addHeader("User-Agent", AppConst.HTTP_AGENG);      

            httpClient = HttpConnManager.getHttpClient();

            long startTime = System.currentTimeMillis();

            Log.v(TAG, "Uri-->" + request.getURI());

            HttpResponse httpResponse = httpClient.execute(request);
            int responseCode = httpResponse.getStatusLine().getStatusCode();

            Log.v(TAG, "Uri-->" + request.getURI() + "..." + responseCode + "..." + (System.currentTimeMillis() - startTime));

            if (responseCode == HttpStatus.SC_OK) {
                result.resultCode = HttpCode.OK;
            } else {
                result.resultCode = HttpCode.E_SERVICE_ACCESS;
                return result;
            }

            Header[] headers = httpResponse.getAllHeaders();
            request.setGzip(false);
            for (int i = 0; i < headers.length; i++) {
                Header header = headers[i];
                if (header.getName().equals("Content-Encoding") && header.getValue().equals("gzip")) {
                    request.setGzip(true);
                }
                if (header.getName().equals("Content-Type") && header.getValue() != null) {
                    String[] types = header.getValue().split(";");
                    for (String str : types) {
                        if (str.contains("charset")) {
                            str.trim();
                            result.encoding = str.substring(str.indexOf('=') + 1);
                        }
                    }
                }
            }

            byte[] data = new byte[BUF_SIZE];
            ByteArrayBuffer byteBuf = new ByteArrayBuffer(BUF_SIZE);
            entityStream = httpResponse.getEntity().getContent();
//            int dataLength = (int)httpResponse.getEntity().getContentLength();

            while(true) {
            	if(request.isCancelled()) {
            		request.abort();
            		result.resultCode = HttpCode.CANCELLED;
            		byteBuf.clear();
            		return result;
            	}
            	
                int bytesRead = entityStream.read(data);
                if (bytesRead == -1) {
                    break;
                }

                byteBuf.append(data, 0, bytesRead);
            }

            if(responseCode == HttpStatus.SC_OK) {
                byte[] byteData = byteBuf.toByteArray();

            	if (request.isGzip()) {
            		byte[] unzipData = GzipDecoder.gzipDecoder(byteData);

                    if (unzipData==null || unzipData.length<=0) { //解压错误
                    	mUseGzip = false;
                    }

                    result.data = unzipData;
            	} else {
            		result.data = byteData;
            	}
            }
        } catch (SocketTimeoutException e) {
        	result.resultCode = HttpCode.E_NET_TIMEOUT;
            Log.e(TAG, e.toString() + request.getURI(), e);
        } catch (OutOfMemoryError e) {
        	result.resultCode = HttpCode.E_NET_ACCESS;
            Log.e(TAG, e.toString() + request.getURI(), e);
        } catch (Exception e) {
        	result.resultCode = HttpCode.E_NET_ACCESS;
            Log.e(TAG, e.toString() + request.getURI(), e);
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

        return result;
    }

    public HttpDataResult execute(HttpGetExt request) {
        int retryTime = request.getRetryTimes();
        HttpDataResult result = new HttpDataResult();
        
        while (retryTime > 0 && !request.isCancelled()) {
            result = doRequest(request);
            if (result != null && result.resultCode == HttpCode.OK) {
                break;
            }
            --retryTime;
        }
        return result;
    }

}
