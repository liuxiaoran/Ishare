package com.galaxy.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

import com.galaxy.ishare.Global;

/**
 * http连接池
 * 
 * @author
 */
public class HttpConnManager {
	private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 30000;
	private static final int DEFAULT_HTTP_SO_TIMEOUT = 30000;
	private static final long DEFAULT_HTTP_CONNMGR_TIMEOUT = 30000L;
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 4096;
	private static HttpClient client;
	private static BasicCookieStore mCookie = new BasicCookieStore();

	private HttpConnManager() {

	}

	public static synchronized HttpClient getHttpClient() {
		if (client == null) {
			HttpParams httpParams = new BasicHttpParams();
			ConnManagerParams.setTimeout(httpParams, DEFAULT_HTTP_CONNMGR_TIMEOUT);
			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(20));
			ConnManagerParams.setMaxTotalConnections(httpParams, 100);

			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
			HttpConnectionParams.setStaleCheckingEnabled(httpParams, false);
			HttpClientParams.setRedirecting(httpParams, true);
//			HttpProtocolParams.setUserAgent(httpParams, Constants.TENCENTNEWS_USER_AGENT);
			HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_HTTP_SO_TIMEOUT);
			HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_HTTP_CONNECT_TIMEOUT);
			HttpConnectionParams.setTcpNoDelay(httpParams, true);
			HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			try {

				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);

				// SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
				SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				// sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);

				schemeRegistry.register(new Scheme("https", sf, 443));
			} catch (Exception e) {
				e.printStackTrace();
			}
			ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
			client = new DefaultHttpClient(manager, httpParams);

//			SchemeRegistry schemeRegistry = new SchemeRegistry();
//			SSLSocketFactory sslsocketfactory = MySSLSocketFactory.getSocketFactory();
//			sslsocketfactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
//			schemeRegistry.register(new Scheme("https", sslsocketfactory, 443));
//			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//			client = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams);

			Context context = Global.mContext;
			HttpHost httpHost = ApnUtil.getHttpHost(context);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);
			((AbstractHttpClient) client).setCookieStore(mCookie);
			((AbstractHttpClient) client).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					if (response == null) {
						throw new IllegalArgumentException("HTTP response may not be null");
					}
					HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
					while (it.hasNext()) {
						HeaderElement he = it.nextElement();
						String param = he.getName();
						String value = he.getValue();
						if (value != null && param.equalsIgnoreCase("timeout")) {
							try {
								long timeout = Long.parseLong(value);
								timeout = timeout > 11 ? (timeout - 10) : 10;
								return timeout * 1000;
							} catch (NumberFormatException ignore) {
							}
						}
					}
					return 180 * 1000;
				}
			});
		}

		Context context = Global.mContext;
		HttpHost httpHost = ApnUtil.getHttpHost(context);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpHost);

		mCookie.clear();

		return client;
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws Exception {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}
}
