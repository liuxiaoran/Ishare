package com.galaxy.util.http;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ApnUtil {
    public static final String WAP = "wap";

    public static final String CM_NET = "cmnet";

    public static final String CM_WAP = "cmwap";

    public static final String CT_NET = "ctnet";

    public static final String CT_WAP = "ctwap";

    public static final String UN_3G_NET = "3gnet";

    public static final String UN_3G_WAP = "3gwap";

    public static final String UN_NET = "uninet";

    public static final String UN_WAP = "uniwap";

    public static final String CMWAP_PROXY = "10.0.0.172";

    public static final String CTWAP_PROXY = "10.0.0.200";

    public static final String UNWAP_PROXY = "xxx";

    public static final int DEFAULT_PORT = 80;

    public static HttpHost getHttpHost(Context context) {
        HttpHost proxy = null;
        String apnName = getActivityApnType(context);
        boolean isConnected = ApnUtil.isActivityApnConnected(context);
        if (apnName != null && isConnected) {
            if (apnName.equalsIgnoreCase(CM_WAP)) {
                proxy = new HttpHost(ApnUtil.CMWAP_PROXY, ApnUtil.DEFAULT_PORT);
            } else if (apnName.equalsIgnoreCase(CT_WAP)) {
                proxy = new HttpHost(ApnUtil.CTWAP_PROXY, ApnUtil.DEFAULT_PORT);
            } else if (apnName.equalsIgnoreCase(UN_WAP)) {
                proxy = new HttpHost(ApnUtil.CMWAP_PROXY, ApnUtil.DEFAULT_PORT);
            } else if (apnName.equalsIgnoreCase(UN_3G_WAP)) {
                proxy = new HttpHost(ApnUtil.CMWAP_PROXY, ApnUtil.DEFAULT_PORT);
            } else {
                String proxy_ip = android.net.Proxy.getDefaultHost();
                if (proxy_ip != null && proxy_ip.length() > 0) {
                    int proxy_port = android.net.Proxy.getDefaultPort();
                    proxy = new HttpHost(proxy_ip, proxy_port);
                }
            }
        }

        return proxy;
    }

    public static String getActivityApnType(Context context) {
        String apnType = null;
        ConnectivityManager conMgr = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo info = conMgr.getActiveNetworkInfo();

        if (null == info) {
            apnType = "";
        } else {
            apnType = info.getExtraInfo();
        }

        return apnType;
    }

    public static String getCarrierName(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm == null ? "" : tm.getSimOperatorName();
    }

    public static boolean isActivityApnConnected(Context context) {
        boolean isConnected = false;
        ConnectivityManager conMgr = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo info = conMgr.getActiveNetworkInfo();

        if (null != info) {
            isConnected = info.isConnected();
        }

        return isConnected;
    }

    public static boolean isWapApnConnected(Context context) {
        String apnName = getActivityApnType(context);
        boolean isConnected = ApnUtil.isActivityApnConnected(context);
        if (apnName != null && isConnected) {
            if (apnName.equalsIgnoreCase(CM_WAP)) {
                return true;
            } else if (apnName.equalsIgnoreCase(CT_WAP)) {
                return true;
            } else if (apnName.equalsIgnoreCase(UN_WAP)) {
                return true;
            } else if (apnName.equalsIgnoreCase(UN_3G_WAP)) {
                return true;
            }
        }
        return false;
    }
}
