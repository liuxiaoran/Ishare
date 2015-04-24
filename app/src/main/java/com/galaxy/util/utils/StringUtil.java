package com.galaxy.util.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class StringUtil {
    public static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            int byteValue = 0xFF & b;
            if (byteValue < 0x10) {
                hexString.append("0" + Integer.toHexString(0xFF & b)).append(separator);
            } else {
                hexString.append(Integer.toHexString(0xFF & b)).append(separator);
            }
        }
        return hexString.toString();
    }

    public static String toMd5(String src) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(src.getBytes());
            return toHexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
            Log.e("Md5 encode failed!", e.getMessage());
            return null;
        }
    }

    public static String subCnString(String str, int length) {
        int n = 0;
        int i = 0;
        int j = 0;
        int byteNum = length * 2;
        boolean flag = true;
        if (str == null) {
            return "";
        }

        for (i = 0; i < str.length(); i++) {
            if ((int) (str.charAt(i)) < 128) {
                n += 1;
            } else {
                n += 2;
            }
            if (n > byteNum && flag) {
                j = i;
                flag = false;
            }
            if (n >= byteNum + 2) {
                break;
            }
        }

        if (n >= byteNum + 2 && i != str.length() - 1) {
            str = str.substring(0, j);
            str += "...";
        }
        return str;
    }

    public static String getSimpleSizeText(long bytes) {
        String retString = "";
        if (bytes <= 0) {
            retString = "0";
        } else if (bytes < (1 << 10)) {
            retString = "1K";
        } else if (bytes < (1 << 20)) {
            return (bytes >> 10) + "K";
        } else if (bytes < (1 << 30)) {
            retString += bytes / (float) (1 << 20);
            if (retString.contains(".")) {
                retString = retString.substring(0, retString.indexOf(".") + 2) + "M";
            }
        } else {
            retString += bytes / (float) (1 << 30);
            if (retString.contains(".")) {
                retString = retString.substring(0,
                        Math.min(retString.indexOf(".") + 3, retString.length()))
                        + "G";
            }
        }
        return retString;
    }

    public static boolean isNull(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotNull(String str) {
        return str != null && !"".equals(str);
    }
}
