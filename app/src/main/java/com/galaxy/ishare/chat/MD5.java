package com.galaxy.ishare.chat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Zhan on 2015/5/28.
 */
public class MD5 {
    public static String md5(String input) {
        String result = input;
        if(input != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
                md.update(input.getBytes());
                BigInteger hash = new BigInteger(1, md.digest());
                result = hash.toString(16);
                while (result.length() < 32) {
                    result = "0" + result;
                }
            } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
            }
        }
        return result;
    }
}
